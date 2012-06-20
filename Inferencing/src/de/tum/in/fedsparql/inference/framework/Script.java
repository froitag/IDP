package de.tum.in.fedsparql.inference.framework;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.fedsparql.inference.io.Database;

/**
 * representing a script which can be executed on de.tum.in.fedsparql.inference.io.Databases
 * 
 */
public class Script {
	/*
	 * public member
	 */
	/**
	 * The Script's unique ID
	 */
	public String id;
	/**
	 * Set of Databases the Script reads from
	 */
	public Set<Database> inputDatabases;
	/**
	 * Set of Databases the Script writes to
	 */
	public Set<Database> outputDatabases;
	/**
	 * The actual JavaScript
	 */
	public String jScript;



	/*
	 * constructors
	 */
	public Script(String id) {
		this(id, new Database[]{}, new Database[]{}, "");
	}
	/**
	 * Constructor.
	 * Initializes a Script.
	 * 
	 * @param id The Script's unique ID
	 * @param inputDatabases Set of Databases the Script reads from
	 * @param outputDatabases Set of Databases the Script writes to
	 * @param jScript The actual JavaScript
	 */
	public Script(String id, Database[] inputDatabases, Database[] outputDatabases, String jScript) {
		this.id = id;
		this.inputDatabases = new HashSet<Database>(Arrays.asList(inputDatabases));
		this.outputDatabases = new HashSet<Database>(Arrays.asList(outputDatabases));
		this.jScript = jScript;
	}
	/**
	 * Copy Constructor.
	 * Clones a given script.
	 * 
	 * @param script
	 */
	public Script(Script script) {
		this.id = script.id;
		this.inputDatabases = new HashSet<Database>(script.inputDatabases);
		this.outputDatabases = new HashSet<Database>(script.outputDatabases);
		this.jScript = script.jScript;
	}


	/*
	 * overridden methods
	 */
	/**
	 * Two Scripts equal each other if they have the same ID.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Script)) return false;

		Script script = (Script) obj;
		return script!=null && (script.id!=null?script.id.equals(this.id):this.id==null);
	}
	/**
	 * HashCode of the script's ID.
	 */
	@Override
	public int hashCode() {
		return this.id!=null ? this.id.hashCode() : 0;
	}

	/**
	 * The script's ID.
	 */
	@Override
	public String toString() {
		return this.id;
	}
}
