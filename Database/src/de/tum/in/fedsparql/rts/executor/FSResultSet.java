package de.tum.in.fedsparql.rts.executor;

import java.util.Iterator;

/**
 * Represent a result set for a federated sparql query
 * @author prasser
 *
 */
public interface FSResultSet extends Iterator<String[]>{

	/** Returns the header (ordered list of columns)*/
	public String[] getHeader();
	
	/** Returns the index for the given variable*/
	public int column(String name);

	/** Closes the result set*/
	public void close() throws FSException;
}
