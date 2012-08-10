package de.tum.in.fedsparql.inference.framework.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.graph.DirectedGraph;
import de.tum.in.fedsparql.inference.framework.xceptions.DependencyCycleException;

/*
 * Applies Topological Sorting to a DirectedGraph (http://en.wikipedia.org/wiki/Topological_sorting)
 * 
 * 1. process all items that don't depend on any other items
 * 2. remove the processed items
 * 3. recalculate dependencies and start again at (1)
 * 
 * if the graph doesn't contain cycles this method will determine with no items to process left
 */
public class TopologicalSorting {

	public TopologicalSorting(DirectedGraph<?> graph) throws DependencyCycleException {
		DirectedGraph<Object> clone = graph.shallowClone();
		if (!_doSorting(clone)) {
			throw new DependencyCycleException("Topological-Sorting can only be applied to graphs not containing any cycles!");
		}
	}


	/* public methods */
	public List<Set<Object>> getSteps() {
		return _steps;
	}


	/* protected methods */
	protected boolean _doSorting(DirectedGraph<Object> graph) {
		/**
		 * 1. process all items that don't depend on any other items
		 * 2. remove the processed items
		 * 3. recalculate dependencies and start again at (1)
		 * 
		 * if the graph doesn't contain cycles this method will determine with no items to process left
		 */
		if (graph.containsCycle()) return false;


		// loop until all vertices were processed
		while (graph.getVertices().size() > 0) {
			Set<Object> independentVertices=new HashSet<Object>();

			// determine all independent vertices
			for (Object vertex: graph.getVertices()) {
				if (graph.getEdges(vertex).size() <= 0) {
					independentVertices.add(vertex);
				}
			}

			// remove them from the graph (+ recalculation of the dependencies is implicitly done by DirectedGraph)
			for (Object vertex: independentVertices) {
				graph.removeVertex(vertex);
			}

			// save this "step" and continue with the new smaller graph
			_steps.add(independentVertices);
		}


		return true;
	}


	/* protected member */
	protected List<Set<Object>> _steps=new ArrayList<Set<Object>>();
}
