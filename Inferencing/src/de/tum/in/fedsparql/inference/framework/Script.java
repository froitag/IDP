package de.tum.in.fedsparql.inference.framework;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * representing a script which can be executed on de.tum.in.fedsparql.inference.io.Databases
 * 
 */
public class Script extends de.tum.in.fedsparql.inference.Script {
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
	public Set<DatabaseID> inputDatabases;
	/**
	 * Set of Databases the Script writes to
	 */
	public Set<DatabaseID> outputDatabases;
	/**
	 * The actual JavaScript
	 */
	public String jScript;



	/*
	 * constructors
	 */
	public Script(String id) {
		this(id, new DatabaseID[]{}, new DatabaseID[]{}, "");
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
	public Script(String id, DatabaseID[] inputDatabases, DatabaseID[] outputDatabases, String jScript) {
		super(null, null, id, jScript);
		this.id = id;
		this.inputDatabases = new HashSet<DatabaseID>(Arrays.asList(inputDatabases));
		this.outputDatabases = new HashSet<DatabaseID>(Arrays.asList(outputDatabases));
		this.jScript = jScript;
	}
	/**
	 * Copy Constructor.
	 * Clones a given script.
	 * 
	 * @param script
	 */
	public Script(Script script) {
		super(null, null, script.id, script.jScript);
		this.id = script.id;
		this.inputDatabases = new HashSet<DatabaseID>(script.inputDatabases);
		this.outputDatabases = new HashSet<DatabaseID>(script.outputDatabases);
		this.jScript = script.jScript;
	}


	/*
	 * overridden methods
	 */
	/**
	 * The script's ID.
	 */
	@Override
	public String toString() {
		return this.id;
	}

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
}
