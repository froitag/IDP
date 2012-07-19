package de.tum.in.fedsparql.inference.dummy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;

import de.tum.in.fedsparql.inference.dummy.DummyParser.ParsedLiteral;
import de.tum.in.fedsparql.inference.io.Database;

/**
 * A dummy implementation of the database interface based on a jena in-memory database. V-1
 * @author prasser
 */
public class DummyDatabase implements Database {

	private static int ID = 0;
	
	/** The name of this database*/
	private String name = null;
	
	/** The database*/
	private Model model = null;

	/** 
	 * Creates a new jena (empty) database with default name
	 * @param name
	 */
	public DummyDatabase(){
		this.name = "GENERATED"+ID++;
		this.model = ModelFactory.createDefaultModel();
	}
	
	/** 
	 * Creates a new jena (empty) database
	 * @param name
	 */
	public DummyDatabase(String name){
		this.name = name;
		this.model = ModelFactory.createDefaultModel();
	}

	/** 
	 * Creates a new jena database containing the triples
	 * from the given file. RDF format must be "N-Triples"
	 * @param name
	 * @param file
	 * @throws FileNotFoundException 
	 */
	public DummyDatabase(String name, String file) throws FileNotFoundException{
		this.name = name;
		this.model = ModelFactory.createDefaultModel();
		this.model.read(new FileInputStream(new File(file)), null, FileUtils.langNTriple);
	}
	
	/**
	 * Returns the model
	 * @return
	 */
	public Model getModel(){
		return this.model;
	}
	
	@Override
	public void drop() {
		this.model.removeAll();
		this.model.close();
		this.model = null;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the size of the model
	 * @return
	 */
	public int getSize() {
		return (int)this.model.size();
	}

	/**
	 * Writes a triple to the database
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	public void write(String subject, String predicate, String object) {
		
		Resource s = model.createResource(subject.substring(1, subject.length()-1));
		Property p = model.createProperty(predicate.substring(1, predicate.length()-1));
		
		if (object.startsWith("<")){
			Resource o = model.createResource(object.substring(1, object.length()-1));
			model.add(model.createStatement(s, p, o));
		}
		else {
			ParsedLiteral pl = DummyParser.parseLiteral(object);
			Literal l = null;
			if (pl.datatype){
				l = model.createTypedLiteral(pl.value, pl.type);
			}
			else {
				l = model.createLiteral(pl.value, pl.type);
			}
			model.add(model.createStatement(s, p, l));
		}
	}
}
