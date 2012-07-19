package de.tum.in.fedsparql.inference.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

import de.tum.in.fedsparql.rts.executor.FSException;
import de.tum.in.fedsparql.rts.executor.FSResultSet;

/**
 * A jena specific implementation of a result set
 * @author prasser
 */
public class DummyResultSet implements FSResultSet{

	/** The header*/
	private String[] header = null;
	
	/** The map*/
	private Map<String, Integer> column = null;
	
	/** The current result set*/
	private ResultSet rs = null;
	
	/** The next element*/
	private String[] next = null;
	
	/**
	 * Creates a new result set
	 * @param rs
	 */
	public DummyResultSet(ResultSet rs) {
		
		// Break if empty
		if (!rs.hasNext()) return;
		
		// Build header and map
		QuerySolution sol = rs.next();
		List<String> vars = new ArrayList<String>();
		Iterator<String> varnames = sol.varNames();
		while (varnames.hasNext()){
			vars.add(varnames.next());
		}
		header = vars.toArray(new String[vars.size()]);
		column = new HashMap<String, Integer>();
		for (int i=0; i<header.length; i++){
			column.put(header[i], i);
		}
		
		// Initialize next
		next = toArray(sol);
		this.rs = rs;
	}

	/**
	 * Converts a query solution into a string array
	 * @param sol
	 * @return
	 */
	private String[] toArray(QuerySolution sol) {
		String[] array = new String[header.length];
		int index = 0;
		Iterator<String> varnames = sol.varNames();
		while (varnames.hasNext()){
			array[index++] = toString(sol.get(varnames.next()));
		}
		return array;
	}

	/**
	 * Converts a RDFNode to a string
	 * @param node
	 * @return
	 */
	private String toString(RDFNode node) {
		if (node.isResource()){
			return "<"+node.toString()+">";
		}
		else if (node.isLiteral()){
			Literal l = node.asLiteral();
			if (l.getDatatypeURI()!=null){
				return "\""+l.getValue()+"\"^^<"+l.getDatatypeURI()+">";
			}
			else {
				return "\""+l.getValue()+"\"";
			}
		}
		else {
			throw new RuntimeException("Currently unsupported!");
		}
	}

	@Override
	public void close() throws FSException {
		// Empty by design
	}

	@Override
	public String[] getHeader() {
		return header;
	}

	@Override
	public int column(String name) {
		if (column!=null) return column.get(name);
		else return -1;
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public String[] next() {
		String[] result = next;
		if (rs.hasNext()) next = toArray(rs.next());
		else next = null;
		return result;
	}

	@Override
	public void remove() {
		// Empty by design
	}

}
