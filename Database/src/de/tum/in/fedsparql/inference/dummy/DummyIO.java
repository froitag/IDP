package de.tum.in.fedsparql.inference.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.tum.in.fedsparql.inference.io.Database;
import de.tum.in.fedsparql.inference.io.Database.Type;
import de.tum.in.fedsparql.inference.io.IO;
import de.tum.in.fedsparql.inference.io.Node;
import de.tum.in.fedsparql.rts.executor.FSException;
import de.tum.in.fedsparql.rts.executor.FSResultSet;

public class DummyIO extends IO{
	
	/** All databases*/
	private List<Database> databases = new ArrayList<Database>();
	
	/** Databases by name*/
	private Map<String, Database> dbByName = new HashMap<String, Database>();
	
	/** Nodes by name*/
	private Map<String, Node> nodeByName = new HashMap<String, Node>();

	/** The available nodes*/
	private List<Node> nodes = null;
	
	/** Maps databases to nodes*/
	private Map<Database, Node> dbToNode = new HashMap<Database, Node>();
	
	/** Maps nodes to databases*/
	private Map<Node, List<Database>> nodeToDbs = new HashMap<Node, List<Database>>();
	
	/**
	 * Creates a new IO instance
	 * @param nodes
	 */
	public DummyIO(List<Node> nodes){
		this.nodes = nodes;
		for (Node node : nodes){
			nodeToDbs.put(node, new ArrayList<Database>());
			nodeByName.put(node.getName(), node);
		}
	}
	
	@Override
	public Database createDatabase(Node node, String name, Type type) throws FSException {
		Database db = new DummyDatabase(name);
		databases.add(db);
		dbByName.put(name, db);
		dbToNode.put(db, node);
		nodeToDbs.get(node).add(db);
		return db;
	}

	@Override
	public FSResultSet execute(String query, Database... databases) throws FSException {

		// Prepare model(s)
		Model model = null;
		if (databases==null){
			model = ModelFactory.createDefaultModel();
			for (Database b : this.databases){
				model.add(((DummyDatabase)b).getModel());
			}
		}
		else if (databases.length==1){
			model = ((DummyDatabase)databases[0]).getModel();
		}
		else{
			model = ModelFactory.createDefaultModel();
			for (Database b : databases){
				model.add(((DummyDatabase)b).getModel());
			}
		}
		
		// Execute query
		QueryExecution e = QueryExecutionFactory.create(query, model);
		ResultSet rs = e.execSelect();
		return new DummyResultSet(rs);
	}

	@Override
	public int getSize(Database database) throws FSException {
		return ((DummyDatabase)database).getSize();
	}

	@Override
	public void writeTriple(Database database, String subject, String predicate,
			String object) throws FSException {
		((DummyDatabase)database).write(subject, predicate, object);
	}
	
	/**
	 * Adds a database to this IO instance
	 * @param database
	 */
	public void register(Node node, Database db){
		databases.add(db);
		dbByName.put(db.getName(), db);
		dbToNode.put(db, node);
		nodeToDbs.get(node).add(db);
	}

	@Override
	public List<Database> getDatabases() {
		return this.databases;
	}

	@Override
	public Database getDatabaseByName(String name) {
		return this.dbByName.get(name);
	}

	@Override
	public Node getNodeByName(String name) {
		return this.nodeByName.get(name);
	}

	@Override
	public List<Node> getNodes() {
		return this.nodes;
	}

	@Override
	public List<Database> getDatabasesForNode(Node node) {
		return this.nodeToDbs.get(node);
	}

	@Override
	public Node getNodeForDatabase(Database database) {
		return this.dbToNode.get(database);
	}

	@Override
	public Database createDatabase(Type type) throws FSException {
		// TODO: This method is not compliant with the specs...
		return new DummyDatabase();
	}

	@Override
	public Database announceDatabase(Node node, String name, Type type) {
		Database db = new DummyDatabase(name);
		dbByName.put(name, db);
		dbToNode.put(db, node);
		return db;
	}
}
