package de.tum.in.fedsparql.inference.framework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScriptCollection {

	/* constructors */
	/**
	 * constructor
	 * @param scripts
	 */
	public ScriptCollection(Script[] scripts) {
		for (Script script: scripts) {
			this.addScript(script);
		}
	}

	/* public methods */
	/**
	 * add a script to the collection
	 * 
	 * @param script
	 * @return this for fluent interface
	 */
	public ScriptCollection addScript(Script script) {
		if (script==null) return this;

		script = new Script(script); // copy the script to prevent the outside world from having a direct reference to it

		// add it to the hashset
		_scripts.add(new Script(script));

		// add its output databases to the _outputRelations cache
		for (String db: script.outputDatabases) {
			if (!_outputRelations.containsKey(db)) {
				_outputRelations.put(db, new HashSet<Script>());
			}

			_outputRelations.get(db).add(script);
		}

		return this;
	}
	/**
	 * removes a script from the collection.
	 * 
	 * @param script
	 * @return this for fluent interface
	public ScriptCollection removeScript(Script script) {
		if (script==null) return this;

		return this;
	}
	/**
	 * gets a set of copies of the collection's scripts
	 */
	public Set<Script> getScripts() {
		Set<Script> ret=new HashSet<Script>();
		for (Script script: _scripts) {
			ret.add(script);
		}

		return ret;
	}

	/* protected member */
	/** this collection's scripts */
	protected Set<Script> _scripts=new HashSet<Script>();
	/** outputDatabase => Set<Scripts>  relations */
	protected Map<String,Set<Script>> _outputRelations=new HashMap<String,Set<Script>>();
}
