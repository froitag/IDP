package de.tum.in.fedsparql.inference.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.tum.in.fedsparql.inference.io.Database;
import de.tum.in.fedsparql.inference.io.IO;
import de.tum.in.fedsparql.inference.io.Database.Type;
import de.tum.in.fedsparql.rts.executor.FSException;
import de.tum.in.fedsparql.rts.executor.FSResultSet;

public class JenaIO extends IO{
	
	/** All databases*/
	private List<Database> databases = new ArrayList<Database>();
	
	/** Databases by name*/
	private Map<String, Database> dbByName = new HashMap<String, Database>();

	@Override
	public Database createDatabase(Type type) throws FSException {
		return new JenaDatabase();
	}

	@Override
	public FSResultSet execute(String query, Database... databases) throws FSException {

		// Prepare model(s)
		Model model = null;
		if (databases==null){
			model = ModelFactory.createDefaultModel();
			for (Database b : this.databases){
				model.add(((JenaDatabase)b).getModel());
			}
		}
		else if (databases.length==1){
			model = ((JenaDatabase)databases[0]).getModel();
		}
		else{
			model = ModelFactory.createDefaultModel();
			for (Database b : databases){
				model.add(((JenaDatabase)b).getModel());
			}
		}
		
		// Execute query
		QueryExecution e = QueryExecutionFactory.create(query, model);
		ResultSet rs = e.execSelect();
		return new JenaResultSet(rs);
	}

	@Override
	public int getSize(Database database) throws FSException {
		return ((JenaDatabase)database).getSize();
	}

	@Override
	public void writeTriple(Database database, String subject, String predicate,
			String object) throws FSException {
		((JenaDatabase)database).write(subject, predicate, object);
	}
	
	/**
	 * Adds a database to this IO instance
	 * @param database
	 */
	public void register(Database database){
		this.databases.add(database);
		this.dbByName.put(database.getName(), database);
	}

	@Override
	public List<Database> getDatabases() {
		return this.databases;
	}

	@Override
	public Database getDatabaseByName(String name) {
		return this.dbByName.get(name);
	}
}
