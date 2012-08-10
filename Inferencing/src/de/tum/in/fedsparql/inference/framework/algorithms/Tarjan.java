package de.tum.in.fedsparql.inference.framework.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.graph.DirectedGraph;

/**
 * Uses the Tarjan-Algorithm to determine the Strongly-Connected-Components of a DirectedGraph.
 *
 * @param <Vertex>
 * @see http://de.wikipedia.org/wiki/Algorithmus_von_Tarjan_zur_Bestimmung_starker_Zusammenhangskomponenten
 */
public class Tarjan {

	/* constructors */
	public Tarjan(DirectedGraph<?> graph){
		if(graph == null) return;

		Set<?> nodeList = graph.getVertices();
		// initialize vertex indices
		for (Object v: nodeList) {
			_vertexIndices.put(v, -1);
		}
		// initialize vertex lowlinks
		for (Object v: nodeList) {
			_vertexLowlinks.put(v, -1);
		}

		// start tarjan
		for (Object v : nodeList)
		{
			if(_vertexIndices.get(v) == -1)
			{
				tarjan(v, graph);
			}
		}
	}


	/* public methods */
	public List<DirectedGraph<?>> getSCCs() {
		return _SCC;
	}


	/* protected methods */
	protected void tarjan(Object vertex, DirectedGraph<?> graph){
		_vertexIndices.put(vertex, _index);
		_vertexLowlinks.put(vertex, _index);
		_index++;
		_stack.add(0, vertex);

		for (DirectedGraph<?>.Edge e: graph.getEdges(vertex)) {
			Object n = e.destination;
			if(_vertexIndices.get(n) == -1){
				tarjan(n, graph);
				_vertexLowlinks.put(vertex, Math.min(_vertexLowlinks.get(vertex), _vertexLowlinks.get(n)));
			}else if(_stack.contains(n)){
				_vertexLowlinks.put(vertex, Math.min(_vertexLowlinks.get(vertex), _vertexIndices.get(n)));
			}
		}
		if(_vertexLowlinks.get(vertex) == _vertexIndices.get(vertex)){
			Object n;
			List<Object> component = new ArrayList<Object>();
			do {
				n = _stack.remove(0);
				component.add(n);
			} while(n != vertex);

			// create DirectedGraph from component
			DirectedGraph<Object> componentGraph = new DirectedGraph<Object>();
			for (Object v: component) {
				componentGraph.addVertex(v);
			}
			for (Object v: component) {
				for (DirectedGraph<?>.Edge e: graph.getEdges(v)) {
					componentGraph.addEdge(e.origin, e.destination);
				}
			}

			_SCC.add(componentGraph);
		}
	}


	/* protected member */
	protected int _index=0;
	protected ArrayList<Object> _stack=new ArrayList<Object>();
	protected List<DirectedGraph<?>> _SCC=new ArrayList<DirectedGraph<?>>();

	protected Map<Object,Integer> _vertexIndices=new HashMap<Object,Integer>();
	protected Map<Object,Integer> _vertexLowlinks=new HashMap<Object,Integer>();
}