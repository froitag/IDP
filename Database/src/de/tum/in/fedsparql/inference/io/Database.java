package de.tum.in.fedsparql.inference.io;

/**
 * Represents a persistent or in-memory database
 * @author prasser
 */
public interface Database {

	/** The type of a database*/
	public static enum Type{
		PERSISTENT,
		IN_MEMORY
	}

	/** Returns the name of the database*/
	public String getName();

	/** Drops (deletes) the database*/
	public void drop();
}
