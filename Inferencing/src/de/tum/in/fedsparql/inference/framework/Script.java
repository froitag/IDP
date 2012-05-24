package de.tum.in.fedsparql.inference.framework;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * representing a script that can be run on de.tum.in.fedsparql.inference.io.Databases
 * 
 */
public class Script {
	public String id;
	public Set<String> inputDatabases;
	public Set<String> outputDatabases;
	public String script;

	/**
	 * constructor
	 * 
	 * @param inputDatabases
	 * @param outputDatabases
	 * @param script
	 */
	public Script(String id, String[] inputDatabases, String[] outputDatabases, String script) {
		this.id = id;
		this.inputDatabases = new HashSet<String>(Arrays.asList(inputDatabases));
		this.outputDatabases = new HashSet<String>(Arrays.asList(outputDatabases));
		this.script = script;
	}
	/**
	 * copy constructor
	 * 
	 * @param script
	 */
	public Script(Script script) {
		this.id = script.id;
		this.inputDatabases = new HashSet<String>(script.inputDatabases);
		this.outputDatabases = new HashSet<String>(script.outputDatabases);
		this.script = script.script;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Script)) return false;

		Script script = (Script) obj;
		return script!=null && (script.id!=null?script.id.equals(this.id):this.id==null);
	}
	@Override
	public int hashCode() {
		return this.id!=null ? this.id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return this.id;
	}
}
