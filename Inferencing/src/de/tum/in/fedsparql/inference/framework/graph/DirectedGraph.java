package de.tum.in.fedsparql.inference.framework.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.algorithms.Tarjan;

/**
 * Represents a Generic Directed-Graph.
 * !! This Model operates with hash-based Collections => be sure to implement hashCode() + equals() !!
 *
 * @param <Vertex>
 */
public class DirectedGraph<Vertex> {

	/* constructors */
	public DirectedGraph() {

	}
	/**
	 * (shallow) Copy-Constructor
	 * (neither clones vertices nor edges..)
	 * 
	 * @param dGraph
	 */
	public DirectedGraph(DirectedGraph<Vertex> dGraph) {
		if (dGraph != null) {
			this._vertices = new HashSet<Vertex>(dGraph._vertices);
			this._edges = new HashMap<Vertex,Set<Edge>>(dGraph._edges);
		}
	}


	/* public methods */	/**
	 * Adds a Vertex to this Graph.
	 * 
	 * @param vertex
	 * @return this for fluent interface
	 */
	public DirectedGraph<Vertex> addVertex(Vertex vertex) {
		if (!_vertices.contains(vertex)) {
			_addVertex(vertex);

			_onVertexAdded(vertex);
		}

		return this;
	}
	/**
	 * Removes a Vertex from this Graph.
	 * 
	 * @param vertex
	 * @return this for fluent interface
	 */
	public DirectedGraph<Vertex> removeVertex(Vertex vertex) {
		if (_vertices.contains(vertex)) {

			_remVertex(vertex);

			_onVertexRemoved(vertex);
		}

		return this;
	}

	/**
	 * Adds an Edge to this Graph.
	 * @param origin
	 * @param destination
	 * @return this for fluent interface
	 */
	public DirectedGraph<Vertex> addEdge(Vertex origin, Vertex destination) {
		return this.addEdge(origin, destination, null);
	}
	/**
	 * Adds an Edge to this Graph incl. Annotation.
	 * 
	 * @param origin
	 * @param destination
	 * @param annotation
	 * @return this for fluent interface
	 */
	public DirectedGraph<Vertex> addEdge(Vertex origin, Vertex destination, Object annotation) {
		if (_vertices.contains(origin) && _vertices.contains(destination)) {
			Edge edge = new Edge(origin, destination, annotation);
			if (!_edges.containsKey(origin) || !_edges.get(origin).contains(edge)) {

				_addEdge(origin, destination, annotation);

				_onEdgeAdded(edge);
			}
		}
		return this;
	}
	/**
	 * Removes an Edge from the Graph.
	 * 
	 * @param origin
	 * @param destination
	 * @return this for fluent interface
	 */
	public DirectedGraph<Vertex> removeEdge(Vertex origin, Vertex destination) {
		Edge edge= new Edge(origin, destination);
		if (_edges.containsKey(origin) && _edges.get(origin).contains(edge)) {
			_remEdge(origin, destination);
			_onEdgeRemoved(edge);
		}
		return this;
	}

	/**
	 * Gets all Vertices of this Graph.
	 */
	public Set<Vertex> getVertices() {
		return new HashSet<Vertex>(_vertices);
	}
	/**
	 * Gets all Edges of this Graph.
	 */
	public Set<Edge> getEdges() {
		Set<Edge> edges = new HashSet<Edge>();
		for (Set<Edge> es: _edges.values()) {
			edges.addAll(es);
		}
		return edges;
	}
	/**
	 * Gets all Edges that origin from a specific Vertex.
	 * 
	 * @param origin
	 */
	public Set<Edge> getEdges(Object origin) {
		if (!_edges.containsKey(origin)) return new HashSet<Edge>();

		return new HashSet<Edge>(_edges.get(origin));
	}
	public Set<Edge> getEdgesIncoming(Object destination) {
		Set<Edge> incomingEdges = new HashSet<Edge>();

		if (_vertices.contains(destination)) {
			Set<Edge> edges = this.getEdges();
			for (Edge edge: edges) {
				if (edge.destination!=null ? edge.destination.equals(destination) : destination==null) {
					incomingEdges.add(edge);
				}
			}
		}

		return incomingEdges;
	}
	/**
	 * Looks for a specific Edge and returns it if available.
	 * 
	 * @param origin
	 * @param destination
	 * @return Edge if found || NULL
	 */
	public Edge getEdge(Object origin, Object destination) {
		if (_edges.containsKey(origin)) {
			for (Edge e: _edges.get(origin)) {
				if (e.destination!=null ? e.destination.equals(destination) : destination==null) {
					return e;
				}
			}
		}
		return null;
	}


	/**
	 * Uses Tarjan to determine the Strongly-Connected-Components of this Graph.
	 * 
	 * @see Tarjan
	 */
	@SuppressWarnings("unchecked")
	public List<DirectedGraph<Vertex>> getStronglyConnectedComponents() {
		List<DirectedGraph<?>> sccs = (new Tarjan(this)).getSCCs();

		List<DirectedGraph<Vertex>> castedSccs = new ArrayList<DirectedGraph<Vertex>>();
		for (DirectedGraph<?> scc: sccs) {
			castedSccs.add((DirectedGraph<Vertex>) scc);
		}
		return castedSccs;
	}

	/**
	 * Gets all Cycles as (Sub-)DirectedGraphs (SCCs with >0 edges).
	 * Information about manually added / removed  Dependencies will get lost.
	 * 
	 * (Uses the Tarjan Algorithm)
	 * @see #getStronglyConnectedComponents()
	 */
	public List<DirectedGraph<Vertex>> getCycles() {
		List<DirectedGraph<Vertex>> cycles = new ArrayList<DirectedGraph<Vertex>>();

		List<DirectedGraph<Vertex>> sccs = this.getStronglyConnectedComponents();
		for (DirectedGraph<Vertex> scc: sccs) {
			if (scc.getEdges().size() > 0) {
				cycles.add(scc);
			}
		}

		return cycles;
	}

	/**
	 * Checks if this Graph contains Cycles.
	 * (Uses this.getCycles())
	 * 
	 * @return true if it contains at least 1 cycle
	 * @see {@link #getCycles()}
	 */
	public boolean containsCycle() {
		return this.getCycles().size() > 0;
	}

	/**
	 * generates a PNG-UML-Graph using PlantUML+GraphVIZ
	 */
	public DirectedGraphPng generatePNG() {
		return new DirectedGraphPng(this);
	}

	public DirectedGraph<Object> shallowClone() {
		DirectedGraph<Object> clone = new DirectedGraph<Object>();

		clone._vertices = new HashSet<Object>(this._vertices);
		for (DirectedGraph<?>.Edge edge: this.getEdges()) {
			clone.addEdge(edge.origin, edge.destination, edge.annotation);
		}

		return clone;
	}


	/* helper classes */
	/**
	 * Class representing an Edge of this Graph.
	 * Only origin+destination are checked for equality.
	 */
	public class Edge {
		public Vertex origin;
		public Vertex destination;
		public Object annotation;

		public Edge(Vertex origin, Vertex destination) {
			this(origin, destination, null);
		}
		public Edge(Vertex origin, Vertex destination, Object annotation) {
			this.origin = origin;
			this.destination = destination;
			this.annotation = annotation;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof DirectedGraph.Edge) {
				@SuppressWarnings("rawtypes")
				DirectedGraph.Edge edge = (DirectedGraph.Edge) obj;

				boolean equals = this.origin!=null ? this.origin.equals(edge.origin) : edge.origin==null;
				equals = equals && (this.destination!=null ? this.destination.equals(edge.destination) : edge.destination==null);

				return equals;
			}

			return false;
		}
		@Override
		public int hashCode() {
			return new String(
					(this.origin!=null ? this.origin.hashCode()+"" : "NULL")
					+ "|"
					+ (this.destination!=null ? this.destination.hashCode()+"" : "NULL")
					).hashCode();
		}
		@Override
		public String toString() {
			return this.origin + " -"+(this.annotation!=null?this.annotation:"")+"-> " + this.destination;
		}
	}



	/* protected methods */
	/**
	 * Actual Vertex Adding.
	 * Is used by addVertex() + may be used to bypass calling _onVertexAdded()
	 * 
	 * @param vertex
	 */
	protected void _addVertex(Vertex vertex) {
		_vertices.add(vertex);
	}
	/**
	 * Actual Vertex Removing.
	 * Is used by removeVertex() + may be used to bypass calling _onVertexRemoved()
	 * 
	 * @param vertex
	 */
	protected void _remVertex(Vertex vertex) {
		Set<Edge> edges = this.getEdges();
		for (Edge edge: edges) {
			if ((edge.origin!=null ? edge.origin.equals(vertex) : vertex==null)
					|| (edge.destination!=null ? edge.destination.equals(vertex) : vertex==null)) {
				_remEdge(edge.origin, edge.destination);
			}
		}

		_vertices.remove(vertex);
	}
	/**
	 * Actual Edge Adding.
	 * Is used by addEdge() + may be used to bypass calling _onEdgeAdded()
	 * 
	 * @param vertex
	 */
	protected void _addEdge(Vertex origin, Vertex destination, Object annotation) {
		Edge edge = new Edge(origin, destination, annotation);
		if (!_edges.containsKey(origin)) {
			_edges.put(origin, new HashSet<Edge>());
		}
		_edges.get(origin).add(edge);
	}
	/**
	 * Actual Edge Removing.
	 * Is used by removeEdge() + may be used to bypass calling _onEdgeRemoved()
	 * 
	 * @param vertex
	 */
	protected void _remEdge(Vertex origin, Vertex destination) {
		Edge edge= new Edge(origin, destination);
		if (_edges.containsKey(origin)) {
			_edges.get(origin).remove(edge);
		}
	}


	/* protected methods - to be overridden*/
	/**
	 * Gets called after a Vertex was added. To be overridden.
	 * 
	 * @param vertex The added Vertex
	 */
	protected void _onVertexAdded(Vertex vertex) {}
	/**
	 * Gets called after a Vertex was removed. To be overridden.
	 * 
	 * @param vertex The removed Vertex
	 */
	protected void _onVertexRemoved(Vertex vertex) {}
	/**
	 * Gets called after an Edge was added. To be overridden.
	 * 
	 * @param edge The added Edge
	 */
	protected void _onEdgeAdded(Edge edge) {}
	/**
	 * Gets called after an Edge was removed. To be overridden.
	 * Does NOT get called when an Edge was deleted because its Vertex was removed.
	 * 
	 * @param edge The removed Edge
	 */
	protected void _onEdgeRemoved(Edge edge) {}


	/* protected member */
	/** vertices of this graph */
	protected Set<Vertex> _vertices = new HashSet<Vertex>();
	/** edges of this graph (edges in the value-set origin from the key) */
	protected Map<Vertex,Set<Edge>> _edges = new HashMap<Vertex,Set<Edge>>();
}
