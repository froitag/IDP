package de.tum.in.fedsparql.inference.framework;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.fedsparql.inference.io.Database;

/**
 * representing a script that can be run on de.tum.in.fedsparql.inference.io.Databases
 * 
 */
public class Script {
	public String id;
	public Set<Database> inputDatabases;
	public Set<Database> outputDatabases;
	public String script;

	/**
	 * constructor
	 * 
	 * @param inputDatabases
	 * @param outputDatabases
	 * @param script
	 */
	public Script(String id, Database[] inputDatabases, Database[] outputDatabases, String script) {
		this.id = id;
		this.inputDatabases = new HashSet<Database>(Arrays.asList(inputDatabases));
		this.outputDatabases = new HashSet<Database>(Arrays.asList(outputDatabases));
		this.script = script;
	}
	/**
	 * copy constructor
	 * 
	 * @param script
	 */
	public Script(Script script) {
		this.id = script.id;
		this.inputDatabases = new HashSet<Database>(script.inputDatabases);
		this.outputDatabases = new HashSet<Database>(script.outputDatabases);
		this.script = script.script;
	}

	/**
	 * two Scripts equal each other if they have the same ID
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Script)) return false;

		Script script = (Script) obj;
		return script!=null && (script.id!=null?script.id.equals(this.id):this.id==null);
	}
	/**
	 * hashcode of the script's ID
	 */
	@Override
	public int hashCode() {
		return this.id!=null ? this.id.hashCode() : 0;
	}

	/**
	 * the script's ID
	 */
	@Override
	public String toString() {
		return this.id;
	}
}
