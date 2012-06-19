package old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;

import de.tum.in.fedsparql.inference.dummy.JenaIO;
import de.tum.in.fedsparql.inference.io.Database;
import de.tum.in.fedsparql.rts.executor.FSException;
import de.tum.in.fedsparql.rts.executor.FSResultSet;

/**
 * let one combine databases and execute rules on it
 * 
 */
public class InferenceIO {


	/* constructors */
	/**
	 * constructor
	 */
	public InferenceIO(JenaIO io) {
		_io = io;
	}


	/* public methods */
	/**
	 * Adds a database to the pool
	 * 
	 * @param db
	 * @return this for fluent interface
	 */
	public InferenceIO addDatabase(Database db) {
		// register DB @IO
		_io.register(db);

		return this;
	}



	/**
	 * Translate GUIDs from a specific DB.
	 * Takes any triple containing `origGUID` and reinserts them with `origGUID` replaced by `newGUID`.
	 * Used when merging databases that contain similar information but use different GUIDs for it.
	 * Any newly created triple will only be available temporary and is not inserted into the actual DB.
	 * 
	 * @param dbName
	 * @param GUID
	 * @param toGUID
	 * @return
	 */
	public InferenceIO addTranslation(GUIDTranslation guidTranslation) {
		if (guidTranslation==null) return this;

		_guidTranslations.add(guidTranslation);

		return this;
	}
	/**
	 * Removes a previously added GUIDTranslation.
	 * 
	 * @param guidTranslation
	 * @return this for fluent interface.
	 */
	public InferenceIO removeTranslation(GUIDTranslation guidTranslation) {
		_guidTranslations.remove(guidTranslation);

		return this;
	}



	/**
	 * adds a NormalisationScript.
	 * 
	 * @param normalisationScript
	 * @return this for fluent interface
	 */
	public InferenceIO addNormalisationScript(NormalisationScript normalisationScript) {
		if (normalisationScript==null) return this;

		_normalisationScripts.add(normalisationScript);

		return this;
	}

	/**
	 * removes a NormalisationScript by its name.
	 * 
	 * @param name
	 * @return this for fluent interface
	 */
	public InferenceIO removeNormalisationScript(NormalisationScript normalisationScript) {
		_normalisationScripts.remove(normalisationScript);

		return this;
	}



	/**
	 * Normalizes DB entries (translate GUIDs, translate Values to unique formats).
	 * First translates Values using the given NormalisationScripts, then translates the GUIDs.
	 * 
	 * @return this for fluent interface
	 * @throws FSException
	 */
	public InferenceIO preprocess() throws FSException {

		Map<String,Database> tempDBs = new HashMap<String,Database>();


		/*** do Value Translations ***/
		// TODO: remove old values after having normalised them?
		Map<String,List<String>> normalisedPredicates=new HashMap<String,List<String>>();
		for (NormalisationScript normalisationScript: _normalisationScripts) {
			String dbName = normalisationScript.getDBName();
			String predicateGUID = normalisationScript.getPredicateGUID();

			// get DB
			Database db = _io.getDatabaseByName(dbName);
			if (db == null) {
				//throw new Exception("Normalisations for '"+dbName+"' couldn't be processed since the database doesn't exist");
				continue;
			}

			// add temporary DB for the DB that should get normalised
			if (!tempDBs.containsKey(dbName)) {
				tempDBs.put(dbName, _io.createDatabase(Database.Type.IN_MEMORY));
			}
			Database tempDB = tempDBs.get(dbName);

			// normalise
			FSResultSet rs = _io.execute("SELECT ?s ?o WHERE {?s " + predicateGUID + " ?o.}", db);
			while (rs.hasNext()){
				String[] tuple = rs.next();
				try {
					_io.writeTriple(tempDB, tuple[rs.column("s")], predicateGUID, normalisationScript.normalise(tuple[rs.column("o")]));
				} catch (ScriptException e) {
					//throw new Exception("Normalisation for DB '"+dbName+"', predicate '"+predicateGUID+"' couldn't be processed since the Script contains errors");
					e.printStackTrace();
				}
			}
			rs.close();


			// add predicate GUID to list of normalised predicates
			if (!normalisedPredicates.containsKey(dbName)) {
				normalisedPredicates.put(dbName, new ArrayList<String>());
			}
			normalisedPredicates.get(dbName).add(predicateGUID);
		}

		/*** do GUID translations ***/
		// merge all translations for one database into one single translation per database
		Map<String,GUIDTranslation> guidTranslationInstances = new HashMap<String,GUIDTranslation>(); // dbname => GUIDTranslation
		for (GUIDTranslation guidTranslation: _guidTranslations) {
			if (!guidTranslationInstances.containsKey(guidTranslation.getDBName())) {
				guidTranslationInstances.put(guidTranslation.getDBName(), new GUIDTranslation(guidTranslation.getDBName()));
			}

			guidTranslationInstances.get(guidTranslation.getDBName()).addTranslation(guidTranslation);
		}

		// add new triples to our temporary database for each database translation
		for (String dbName: guidTranslationInstances.keySet()) {
			GUIDTranslation translation = guidTranslationInstances.get(dbName);
			if (translation == null) continue;

			// get database
			Database origDB = _io.getDatabaseByName(dbName);
			if (origDB == null) {
				//throw new Exception("Translations for '"+dbName+"' couldn't be processed since the database doesn't exist");
				continue;
			}
			// get temp database for results
			if (!tempDBs.containsKey(dbName)) {
				tempDBs.put(dbName, _io.createDatabase(Database.Type.IN_MEMORY));
			}
			Database tempDB = tempDBs.get(dbName);

			// run query and insert new triples
			Map<String,Set<String>> guidTranslations = translation.getTranslations();
			for (String origGUID: guidTranslations.keySet())  {
				Set<String> newGUIDs = guidTranslations.get(origGUID);
				if (newGUIDs == null) continue;

				for (String newGUID: newGUIDs) {
					// add all SUBJECT GUID translations
					FSResultSet rs = _io.execute("SELECT ?p ?o WHERE {" + origGUID + " ?p ?o.}", origDB);
					while (rs.hasNext()){
						String[] tuple = rs.next();
						_io.writeTriple(tempDB, newGUID, tuple[rs.column("p")],  tuple[rs.column("o")]);
					}
					rs.close();

					// add all PREDICATE GUID translations
					boolean predicateWasNormalised=false;
					if (normalisedPredicates.containsKey(dbName)) {
						predicateWasNormalised = normalisedPredicates.get(dbName).contains(origGUID);
					}
					rs = _io.execute("SELECT ?s ?o WHERE {?s " + origGUID + " ?o.}", predicateWasNormalised ? tempDB : origDB);
					while (rs.hasNext()){
						String[] tuple = rs.next();
						_io.writeTriple(tempDB, tuple[rs.column("s")], newGUID, tuple[rs.column("o")]);
					}
					rs.close();
				}
			}
		}





		// TODO: normalise entries (preprocess)

		// add temporary DB to IO
		_tempDB = _io.createDatabase(Database.Type.IN_MEMORY);
		_io.register(_tempDB);

		for (Database db: tempDBs.values()) {
			FSResultSet rs = _io.execute("SELECT ?s ?p ?o WHERE {?s ?p ?o.}", db);

			while (rs.hasNext()){
				String[] tuple = rs.next();
				_io.writeTriple(_tempDB, tuple[rs.column("s")], tuple[rs.column("p")], tuple[rs.column("o")]);
			}
		}

		return this;
	}

	/**
	 * Executes the query on all databases.
	 * @param rule
	 * @return FSResultSet
	 * @throws FSException
	 */
	public FSResultSet execute(String query) throws FSException {
		return _io.execute(query);
	}





	/* protected helper */


	/* protected member */
	/** global IO instance */
	JenaIO _io; // TODO: make JenaIO.register() available in the base class IO => we could work with a general IO object instead of a specific JenaIO one.
	/** temporary database (used for preprocessing) */
	Database _tempDB=null;

	/** contains GUID translations */
	Set<GUIDTranslation> _guidTranslations=new HashSet<GUIDTranslation>();
	Set<NormalisationScript> _normalisationScripts=new HashSet<NormalisationScript>();
}
