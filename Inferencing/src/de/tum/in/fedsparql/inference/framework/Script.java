package de.tum.in.fedsparql.inference.framework;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * representing a script that can be run on de.tum.in.fedsparql.inference.io.Databases
 * 
 */
public class Script {
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
	public Script(String[] inputDatabases, String[] outputDatabases, String script) {

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
		this.inputDatabases = new HashSet<String>(script.inputDatabases);
		this.outputDatabases = new HashSet<String>(script.outputDatabases);
		this.script = script.script;
	}
}
