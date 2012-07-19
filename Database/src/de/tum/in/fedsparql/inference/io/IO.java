package de.tum.in.fedsparql.inference.io;

import java.util.List;

import de.tum.in.fedsparql.inference.io.Database.Type;
import de.tum.in.fedsparql.rts.executor.FSException;
import de.tum.in.fedsparql.rts.executor.FSResultSet;

/**
 * Interface for the rules engine.
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
	 * Creates a new persistent database.
	 * @param node
	 * @param name
	 * @param type
	 * @return
	 * @throws FSException
	 */
	public abstract Database createDatabase(Node node, String name, Type type) throws FSException;
	
	/**
	 * Tells the IO subsystem that this database will be created during the execution of a script
	 * @param node
	 * @param name
	 * @param type
	 * @return
	 * @throws FSException
	 */
	public abstract Database announceDatabase(Node node, String name, Type type);
	
	/**
	 * Creates a temporary database on the same node on which the script is executed. The database will
	 * be deleted automatically when the script terminates.
	 * @param type
	 * @return
	 * @throws FSException
	 */
	public abstract Database createDatabase(Type type) throws FSException;
	
	
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
	
	/**
	 * Returns a node
	 * @param name
	 * @return
	 */
	public abstract Node getNodeByName(String name);
	
	/**
	 * Returns a list of all available nodes
	 * @return
	 */
	public abstract List<Node> getNodes();
	
	/**
	 * Returns list of all databases hosted by the given node
	 * @param node
	 * @return
	 */
	public abstract List<Database> getDatabasesForNode(Node node);
	
	/**
	 * Returns the node on which the given database is hosted
	 * @param database
	 * @return
	 */
	public abstract Node getNodeForDatabase(Database database);

}
