package de.tum.in.fedsparql.inference.io;

import java.util.List;

import de.tum.in.fedsparql.rts.executor.FSException;
import de.tum.in.fedsparql.rts.executor.FSResultSet;

/**
 * Interface for the rules engine. V-1
 * 
 * @author prasser
 */
public abstract class IO {
	
	/**
	 * Executes the query on all databases.
	 * @param query
	 * @return
	 */
	public FSResultSet execute(String query) throws FSException{
		return execute(query, (Database[])null);
	}

	/** 
	 * Executes the given query on the given set of databases. 
	 * @param query
	 * @param databases
	 * @return
	 */
	public abstract FSResultSet execute(String query, Database... databases) throws FSException;
	
	/**
	 * Writes a triple to the given database.
	 * @param database
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	public abstract void writeTriple(Database database, String subject, String predicate, String object) throws FSException;
	
	/**
	 * Creates a new database.
	 * @param type
	 * @return
	 */
	public abstract Database createDatabase(Database.Type type) throws FSException;
	
	/**
	 * Returns the size of a database.
	 * @param database
	 * @return
	 */
	public abstract int getSize(Database database) throws FSException;
	
	/**
	 * Returns a list of all available databases
	 * @return
	 */
	public abstract List<Database> getDatabases();
	
	/**
	 * Returns a database
	 * @param name
	 * @return
	 */
	public abstract Database getDatabaseByName(String name);
}
