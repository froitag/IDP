package de.tum.in.fedsparql.inference.framework.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.DatabaseID;
import de.tum.in.fedsparql.inference.framework.Script;

/**
 * Represents a DependencyGraph for a a collection of Scripts.
 */
public class DependencyGraph extends DirectedGraph<Script> {

	/* constructors */
	/**
	 * Default Constructor.
	 */
	public DependencyGraph() {

	}
	/**
	 * Initializes a DependencyGraph with the given Scripts.
	 * 
	 * @param scripts The collection's Scripts.
	 */
	public DependencyGraph(Script[] scripts) {
		for (Script script: scripts) {
			this.addScript(script);
		}
	}
	/**
	 * Copy Constructor.
	 * Clones a DependencyGraph
	 * 
	 * @param dGraph DependencyGraph to clone
	 */
	public DependencyGraph(DependencyGraph dGraph) {
		this(dGraph.getScripts().toArray(new Script[0]));

		Map<Script,Set<Script>> rem = dGraph.getRemovedDependencies();
		for (Script script: rem.keySet()) {
			for (Script dependency: rem.get(script)) {
				this.removeDependency(script, dependency);
			}
		}

		Map<Script,Set<Script>> add = dGraph.getAddedDependencies();
		for (Script script: add.keySet()) {
			for (Script dependency: add.get(script)) {
				this.addDependency(script, dependency);
			}
		}
	}
	/**
	 * Converts a DirectedGraph<Script> into a DepdencyGraph
	 * 
	 * @param directedGraph
	 */
	public DependencyGraph(DirectedGraph<Script> directedGraph) {
		if (directedGraph == null) return;

		for (Script script: directedGraph.getVertices()) {
			this.addScript(script);
		}
	}


	/* public methods */
	/**
	 * Adds a Script to this Graph.
	 * (Maps to addVertex())
	 * 
	 * @param script
	 * @return this for fluent interface
	 */
	public DependencyGraph addScript(Script script) {
		this.addVertex(script);
		return this;
	}

	/**
	 * Removes a Script from this Graph.
	 * (Maps to removeVertex())
	 * 
	 * @param script
	 * @return this for fluent interface
	 */
	public DependencyGraph removeScript(Script script) {
		this.removeVertex(script);
		return this;
	}

	/**
	 * Adds a Dependency to this Graph.
	 * => `script` depends on `dependency`
	 * (Maps to addEdge())
	 * 
	 * @param script
	 * @param dependency
	 * @return this for fluent interface
	 */
	public DependencyGraph addDependency(Script script, Script dependency) {
		this.addEdge(script, dependency);
		return this;
	}

	/**
	 * Removes a Dependency from this Graph.
	 * => `script` doesn't depend on `dependency`
	 * (Maps to removeEdge())
	 * 
	 * @param script
	 * @param dependency
	 * @return this for fluent interface
	 * @see #removeEdge(Script, Script)
	 */
	public DependencyGraph removeDependency(Script script, Script dependency) {
		this.removeEdge(script, dependency);
		return this;
	}

	/**
	 * Removes a Set of Dependencies from this Graph.
	 * (Maps to removeDependency())
	 * 
	 * @param dependencies Map(Script => Set of Scripts the Key shall not longer depend on)
	 * @return this for fluent interface
	 * @see #removeDependency(Script, Script)
	 */
	public DependencyGraph removeDependencies(Map<Script,Set<Script>> dependencies) {
		for (Script script: dependencies.keySet()) {
			for (Script dependency: dependencies.get(script)) {
				this.removeDependency(script, dependency);
			}
		}
		return this;
	}

	/**
	 * Gets all Scripts of this Graph.
	 * (Maps to getVertices())
	 */
	public Set<Script> getScripts() {
		return this.getVertices();
	}

	/**
	 * Gets all Databases the Scripts of this Graph read from.
	 */
	public Set<DatabaseID> getInputDatabases() {
		Set<DatabaseID> ret=new HashSet<DatabaseID>();

		for (Script script: this.getScripts()) {
			ret.addAll(script.inputDatabases);
		}

		return ret;
	}
	/**
	 * Gets all Databases the Scripts of this Graph write to.
	 */
	public Set<DatabaseID> getOutputDatabases() {
		Set<DatabaseID> ret=new HashSet<DatabaseID>();

		for (Script script: this.getScripts()) {
			ret.addAll(script.outputDatabases);
		}

		return ret;
	}

	/**
	 * Gets a Map describing the Dependencies of this Graph.
	 * (does not consider inherited dependencies, e.g. if C depends on B and B depends on A, the dependency C->A is not included)
	 * 
	 * @return Map: Script => Set of Scripts the Key depends on
	 */
	public Map<Script,Set<Script>> getDirectDependencies() {
		Map<Script,Set<Script>> dependencies = new HashMap<Script,Set<Script>>();

		for (Script script: this.getVertices()) {
			dependencies.put(new Script(script),
					this.getDirectDependencies(script));
		}

		return dependencies;
	}
	/**
	 * Gets the direct Dependencies of a specific Script.
	 * (does not consider inherited dependencies, e.g. if C depends on B and B depends on A, the dependency C->A is not included)
	 * 
	 * @param script
	 * @return Set of Scripts the given Script depends on
	 */
	public Set<Script> getDirectDependencies(Script script) {
		Set<Script> dependencies = new HashSet<Script>();

		Set<Edge> edges = this.getEdges(script);
		for (Edge edge: edges) {
			dependencies.add(edge.destination);
		}

		return dependencies;
	}
	/**
	 * Gets the direct Dependents of a specific Script.
	 * (does not consider inherited dependencies, e.g. if C depends on B and B depends on A, the dependency C->A is not included)
	 * 
	 * @param script
	 * @return Set of Scripts which depend on the given Script
	 */
	public Set<Script> getDirectDependents(Script script) {
		Set<Script> dependents = new HashSet<Script>();

		Set<Edge> edges = this.getEdgesIncoming(script);
		for (Edge edge: edges) {
			dependents.add(edge.origin);
		}

		return dependents;
	}

	/**
	 * Gets a map Describing all Dependencies (including inherited ones) of the Scripts of this Graph.
	 *
	 * @return Map: Script => Set of Scripts the Key depends on
	 */
	public Map<Script,Set<Script>> getAllDependencies() {
		Map<Script,Set<Script>> ret = new HashMap<Script,Set<Script>>();

		for (Script script: this.getScripts()) {
			ret.put(new Script(script),
					this.getAllDependencies(script));
		}

		return ret;
	}
	/**
	 * Gets all Dependencies (including inherited ones) of the given Script.
	 *
	 * @param script
	 * @return Set of Scripts the given Script depends on
	 */
	public Set<Script> getAllDependencies(Script script) {
		Set<Script> dependencies = new HashSet<Script>();

		Queue<Script> queue = new LinkedList<Script>();
		Set<Script> processed = new HashSet<Script>();

		// init with direct dependencies
		dependencies.addAll(this.getDirectDependencies(script));
		queue.addAll(dependencies);

		while (!queue.isEmpty()) {
			Script qItem = queue.remove();

			Set<Script> qItemDeps = this.getDirectDependencies(qItem);
			dependencies.addAll(qItemDeps);
			qItemDeps.removeAll(processed);
			queue.addAll(qItemDeps);

			processed.add(qItem);
		}

		return dependencies;
	}
	/**
	 * Gets all manually added dependencies.
	 * 
	 * @return Map<Script => Set<Dependencies>>
	 */
	public Map<Script,Set<Script>> getAddedDependencies() {
		Map<Script,Set<Script>> ret = new HashMap<Script,Set<Script>>();

		for (Script script: _manuallyAddedDependencies.keySet()) {
			ret.put(script, new HashSet<Script>());
			for (Script dependency: _manuallyAddedDependencies.get(script)) {
				ret.get(script).add(dependency);
			}
		}

		return ret;
	}

	/**
	 * Gets all manually removed dependencies.
	 * 
	 * @return Map<Script => Set<Dependencies>>
	 */
	public Map<Script,Set<Script>> getRemovedDependencies() {
		Map<Script,Set<Script>> ret = new HashMap<Script,Set<Script>>();

		for (Script script: _manuallyRemovedDependencies.keySet()) {
			ret.put(script, new HashSet<Script>());
			for (Script dependency: _manuallyRemovedDependencies.get(script)) {
				ret.get(script).add(dependency);
			}
		}

		return ret;
	}

	/**
	 * Returns all Scripts of this Graph that do not depend on other scripts (may be used to start processing).
	 */
	public Set<Script> getIndependentScripts() {
		Set<Script> ret = new HashSet<Script>();

		for (Script script: this.getScripts()) {
			if (this.getAllDependencies(script).size() <= 0) {
				ret.add(new Script(script));
			}
		}

		return ret;
	}

	/**
	 * @return the count of vertices
	 */
	public int size() {
		return _vertices.size();
	}

	/**
	 * generates a PNG-UML-Graph using PlantUML+GraphVIZ
	 */
	@Override
	public DirectedGraphPng generatePNG() {
		Map<Object,Map<String,String>> vertexAnnotations = new HashMap<Object, Map<String,String>>();
		for (Script script: this.getScripts()) {
			vertexAnnotations.put(script, new HashMap<String,String>());

			vertexAnnotations.get(script).put("in", script.inputDatabases.toString());
			vertexAnnotations.get(script).put("out", script.outputDatabases.toString());
		}

		return new DirectedGraphPng(this, "Dependency-Graph", vertexAnnotations);
	}

	/**
	 * Pritty-Prints all Scripts of this Graph.
	 * 
	 * @return this for fluent interface
	 */
	public DependencyGraph printScripts() {
		System.out.println("Scripts:");
		for (Script script: this.getScripts()) {
			System.out.println("Script \""+script.id+"\": \t"+script.inputDatabases+"\t->\t"+script.outputDatabases);
		}

		return this;
	}
	/**
	 * prints all direct dependencies
	 * 
	 * @return this for fluent interface
	 */
	public DependencyGraph printDirectDependencies() {
		/**
			r1:[]
			r2:[r5]
			r3:[]
			r4:[r2, r3]
			r5:[]
		 */

		System.out.println("Direct Dependencies:");
		Map<Script,Set<Script>> dependencies = this.getDirectDependencies();
		for (Script script: dependencies.keySet()) {
			System.out.print(script.id+":");
			System.out.print(dependencies.get(script));
			System.out.println();
		}

		return this;
	}
	/**
	 * prints all inherited dependencies
	 *
	 * @return this for fluent interface
	 */
	public DependencyGraph printInheritedDependencies() {
		/**
				r1:[]
				r2:[r5]
				r3:[]
				r4:[r2, r3, r5]
				r5:[]
		 */

		System.out.println("Inherited Dependencies (incl. direct ones):");
		Map<Script,Set<Script>> dependencies = this.getAllDependencies();
		for (Script script: dependencies.keySet()) {
			System.out.print(script.id+":");
			System.out.print(dependencies.get(script));
			System.out.println();
		}

		return this;
	}
	/**
	 * prints all manually removed dependencies
	 * 
	 * @return this for fluent interface
	 */
	public DependencyGraph printRemovedDependencies() {
		/**
			r1:[]
			r2:[]
			r3:[]
			r4:[]
			r5:[r5]
		 */

		System.out.println("Manually Removed Dependencies:");
		Map<Script,Set<Script>> removedDependencies = this.getRemovedDependencies();
		for (Script script: removedDependencies.keySet()) {
			System.out.print(script.id+":");
			System.out.print(removedDependencies.get(script));
			System.out.println();
		}

		return this;
	}


	/* protected overrides */
	@Override
	protected void _onVertexAdded(Script script) {
		// add script to the Input-Database-Relation-Cache
		for (DatabaseID iDB: script.inputDatabases) {
			if (!_inputRelations.containsKey(iDB)) {
				_inputRelations.put(iDB, new HashSet<Script>());
			}
			_inputRelations.get(iDB).add(script);
		}
		// add script to the Output-Database-Relation-Cache
		for (DatabaseID oDB: script.outputDatabases) {
			if (!_outputRelations.containsKey(oDB)) {
				_outputRelations.put(oDB, new HashSet<Script>());
			}
			_outputRelations.get(oDB).add(script);
		}

		// add direct dependencies!
		for (DatabaseID iDB: script.inputDatabases) {
			if (_outputRelations.containsKey(iDB)) {
				for (Script dependency: _outputRelations.get(iDB)) {
					if (_isManuallyRemovedDependency(script, dependency)) continue; // ignore manually removed dependencies

					String dbString=iDB.toString();
					for (DatabaseID db: dependency.outputDatabases) {
						if (db.equals(iDB)) {
							dbString = db.toString();// get the String from the dependency's output DBs as it may be a "fresh" db -> annotated with a *
							break;
						}
					}

					Edge edge = this.getEdge(script, dependency);
					if (edge == null) {
						_addEdge(script, dependency, dbString);
					} else {
						edge.annotation = edge.annotation.toString().equals("") ? dbString : edge.annotation.toString() + "," + dbString;
					}
				}
			}
		}
		for (DatabaseID oDB: script.outputDatabases) {
			if (_inputRelations.containsKey(oDB)) {
				for (Script dependent: _inputRelations.get(oDB)) {
					if (_isManuallyRemovedDependency(dependent, script)) continue; // ignore manually removed dependencies

					String dbString = oDB.toString();

					Edge edge = this.getEdge(dependent, script);
					if (edge == null) {
						_addEdge(dependent, script, dbString);
					} else {
						edge.annotation = edge.annotation.toString().equals("") ? dbString : edge.annotation.toString() + "," + dbString;
					}
				}
			}
		}
	}
	@Override
	protected void _onVertexRemoved(Script script) {
		// remove direct dependencies!
		// -> already done by DirectedGraph.removeVertex

		// remove the script from the Output-Database-Relation-Cache
		for (DatabaseID oDB: script.outputDatabases) {
			if (_outputRelations.containsKey(oDB)) {
				_outputRelations.get(oDB).remove(script);
			}
		}
		// remove the script from the Input-Database-Relation-Cache
		for (DatabaseID iDB: script.inputDatabases) {
			if (_inputRelations.containsKey(iDB)) {
				_inputRelations.get(iDB).remove(script);
			}
		}

		// remove the script from the Manually-Added-Dependencies-Cache
		for (Script s: _manuallyAddedDependencies.keySet()) {
			_manuallyAddedDependencies.get(s).remove(script);
		}
		_manuallyAddedDependencies.remove(script);

		// remove the script from the Manually-Removed-Dependencies-Cache
		for (Script s: _manuallyRemovedDependencies.keySet()) {
			_manuallyRemovedDependencies.get(s).remove(script);
		}
		_manuallyRemovedDependencies.remove(script);
	}
	@Override
	protected void _onEdgeAdded(Edge edge) {
		// manually added dependency
		if (_manuallyRemovedDependencies.containsKey(edge.origin) && _manuallyRemovedDependencies.get(edge.origin).contains(edge.destination)) {
			_manuallyRemovedDependencies.get(edge.origin).remove(edge.destination);
		} else {
			if (!_manuallyAddedDependencies.containsKey(edge.origin)) {
				_manuallyAddedDependencies.put(edge.origin, new HashSet<Script>());
			}
			_manuallyAddedDependencies.get(edge.origin).add(edge.destination);
		}
	}
	@Override
	protected void _onEdgeRemoved(Edge edge) {
		// manually removed dependency
		if (_manuallyAddedDependencies.containsKey(edge.origin) && _manuallyAddedDependencies.get(edge.origin).contains(edge.destination)) {
			_manuallyAddedDependencies.get(edge.origin).remove(edge.destination);
		} else {
			if (!_manuallyRemovedDependencies.containsKey(edge.origin)) {
				_manuallyRemovedDependencies.put(edge.origin, new HashSet<Script>());
			}
			_manuallyRemovedDependencies.get(edge.origin).add(edge.destination);
		}
	}


	/* protected methods */
	/**
	 * checks if a specific dependency was manually added
	 * 
	 * @param script
	 * @param dependency
	 * @return true if it was added || false
	 */
	protected boolean _isManuallyAddedDependency(Script script, Script dependency) {
		return _manuallyAddedDependencies.containsKey(script) && _manuallyAddedDependencies.get(script).contains(dependency);
	}
	/**
	 * checks if a specific dependency was manually removed
	 * 
	 * @param script
	 * @param dependency
	 * @return true if it was removed || false
	 */
	protected boolean _isManuallyRemovedDependency(Script script, Script dependency) {
		return _manuallyRemovedDependencies.containsKey(script) && _manuallyRemovedDependencies.get(script).contains(dependency);
	}


	/* protected member */
	/**
	 *  database <-> script relations
	 *  Map(database => Set of Scripts which use the Key as output DB)
	 */
	protected Map<DatabaseID,Set<Script>> _outputRelations=new HashMap<DatabaseID,Set<Script>>();
	/**
	 *  database <-> script relations
	 *  Map(database => Set of Scripts which use the Key as input DB)
	 */
	protected Map<DatabaseID,Set<Script>> _inputRelations=new HashMap<DatabaseID,Set<Script>>();
	/**
	 *  manually added dependencies.
	 *  Script => Set of Dependencies
	 */
	protected Map<Script,Set<Script>> _manuallyAddedDependencies=new HashMap<Script,Set<Script>>();
	/**
	 *  manually removed dependencies.
	 *  Script => Set of Dependencies
	 */
	protected Map<Script,Set<Script>> _manuallyRemovedDependencies=new HashMap<Script,Set<Script>>();
}
