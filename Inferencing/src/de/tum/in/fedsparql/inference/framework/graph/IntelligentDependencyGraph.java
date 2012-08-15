package de.tum.in.fedsparql.inference.framework.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.DatabaseID;
import de.tum.in.fedsparql.inference.framework.Script;

/**
 * DependencyGraph Derivation that comes up with some Intelligence.
 * Offers additional Methods for automatic Cycle Elimination.
 */
public class IntelligentDependencyGraph extends DependencyGraph {

	/* constructors */
	/**
	 * Default Constructor.
	 */
	public IntelligentDependencyGraph() {

	}
	/**
	 * Initializes a DependencyGraph with the given Scripts.
	 * 
	 * @param scripts The collection's Scripts.
	 */
	public IntelligentDependencyGraph(Script[] scripts) {
		super(scripts);
	}
	/**
	 * Copy Constructor.
	 * Clones a DependencyGraph
	 * 
	 * @param dGraph DependencyGraph to clone
	 */
	public IntelligentDependencyGraph(DependencyGraph dGraph) {
		super(dGraph);
	}
	/**
	 * Converts a DirectedGraph<Script> into a DepdencyGraph
	 * 
	 * @param directedGraph
	 */
	public IntelligentDependencyGraph(DirectedGraph<Script> directedGraph) {
		super(directedGraph);
	}


	/* public methods */
	/**
	 * Suggest some Dependencies that could be removed to simplify the Graph / remove Cycles.
	 * Result can directly be passed to {@link #removeDependencies(DependenciesRemovalSuggestion)}.
	 * 
	 * @return {@link DependenciesRemovalSuggestion}
	 */
	public DependenciesRemovalSuggestion suggestDependenciesToRemove() {
		DependenciesRemovalSuggestion suggestion = new DependenciesRemovalSuggestion();

		_suggestTrivials(suggestion);

		_suggestFreshDBRemovals(suggestion);

		return suggestion;
	}

	/**
	 * Removes Dependencies like suggested by {@link #suggestDependenciesToRemove()}.
	 * @param removalSuggestion
	 * @return
	 */
	public IntelligentDependencyGraph removeDependencies(DependenciesRemovalSuggestion removalSuggestion) {
		this.removeDependencies(removalSuggestion.dependencies);
		return this;
	}


	/* helper classes */
	/**
	 * Return-Value of {@link IntelligentDependencyGraph#suggestDependenciesToRemove()}.
	 * Contains the suggested Dependencies + some additional Information about them.
	 */
	public static class DependenciesRemovalSuggestion {
		/* public member */
		/**
		 * Suggested Dependencies to remove.
		 * Map(Script => Set of Scripts the Key shall not longer depend on)
		 */
		public Map<Script,Set<Script>> dependencies=new HashMap<Script,Set<Script>>();
		/**
		 * Some Information about the Suggestion
		 */
		public String info="";


		/* constructor */
		/**
		 * default constructor, only visible for the package
		 */
		DependenciesRemovalSuggestion() {

		}

		/* public methods */
		/**
		 * Adds a Suggestion to {@link #dependencies}.
		 * 
		 * @param script
		 * @param dependency
		 * @return this for fluent interface
		 */
		public DependenciesRemovalSuggestion addSuggestion(Script script, Script dependency) {
			if (!this.dependencies.containsKey(script)) {
				this.dependencies.put(script, new HashSet<Script>());
			}
			this.dependencies.get(script).add(dependency);
			return this;
		}
		/**
		 * Adds a message to {@link #info}.
		 * 
		 * @param msg
		 * @return this for fluent interface
		 */
		public DependenciesRemovalSuggestion addInfo(String msg) {
			this.info += msg + System.getProperty("line.separator");
			return this;
		}

		/**
		 * Pritty-Prints {@link #dependencies} + {@link #info}.
		 * 
		 * @return this for fluent interface
		 */
		public DependenciesRemovalSuggestion print() {
			System.out.println("Suggestions:");
			for (Script script: this.dependencies.keySet()) {
				System.out.println("\t" + script + ": " + this.dependencies.get(script));
			}

			System.out.println("Info:");
			String[] info = this.info.split(System.getProperty("line.separator"));
			for (String msg: info) {
				System.out.println("\t" + msg + System.getProperty("line.separator"));
			}

			return this;
		}
	}


	/* protected methods */
	/**
	 * Suggests some trivial removals.
	 * Like "Script1 depends on Script1"
	 * 
	 * @param suggestion
	 */
	protected void _suggestTrivials(DependenciesRemovalSuggestion suggestion) {
		// suggest to remove all dependencies between a script and itself
		for (Edge edge: this.getEdges()) {
			if (edge.origin!=null ? edge.origin.equals(edge.destination) : edge.destination==null) {
				suggestion.addSuggestion(edge.origin, edge.destination);
				suggestion.addInfo("Removed Direct-Self-Dependency: " + edge.origin + " -"+(edge.annotation!=null?edge.annotation:"")+"-> " + edge.destination);
			}
		}
	}

	/**
	 * Give Suggestions for the "Fresh-DB" System.
	 * If a Database is marked as "Fresh" in a specific Script, you know this Script needs to be processed before any other Script reading from the same DB.
	 * 
	 * @param suggestion
	 * @see DatabaseID#isFresh()
	 */
	protected void _suggestFreshDBRemovals(DependenciesRemovalSuggestion suggestion) {
		/**
		 * In a Cycle like: (S1: a->b), (S2: b>c), (S3: c>a) (S1 depends on S3, S2 on S1, S3 on S2)
		 * we have no idea where to start, we could do: (S1->S2->S3), (S2->S3->S1), (S3->S2->S1)
		 * 
		 * But if we have the information that S1 creates the DB b, we know for sure that S1 needs to be processed prior to S2,
		 * therefore only one possibility is left: (S1->S2->S3).
		 * We fixed the dependency between S2 and S1 and therefore said that this dependency can not be removed.
		 * 
		 * For "linear"/simple cycles with (n) dependencies, we need to fix (n-1) edges, so only 1 edge is left to be removed which will eliminate the cycle.
		 * ------
		 * The more complex a cycle is (non linear, like (S1: a->b), (S2: b>c), (S3: c>d), (S4: d>a), (S5: c>b), (S6: a>c)), the harder it is to determine what dependencies can be removed.
		 * 
		 * The solution is the newly introduced "CycleBreaker" Algorithm:		 *
		 * 1. Determine the Strongly-Connected-Components=SCC with >0 edges (non linear, complex cycles) of the Graph (use Tarjan's Algorithm)
		 * 2. Mark all dependencies that can't be removed within one SCC (e.g. if (S1: a>b*; S2: b>a) S1 creates a DB that S2 reads from, the dependency S2->S1 needs to stay)
		 * 3. Remove all dependencies that contradict the dependencies we fixed in 2.) (e.g. (S1: a>b*; S2: b>a) the dependency S1->S2 contradicts the requirement that S2->S1)
		 * 4. Hopefully step 3.) removed all cycles and we don't need to beg the user for more input ;)
		 */

		// 1.) Determine the Strongly-Connected-Components
		List<DirectedGraph<Script>> cycles = this.getCycles();

		// 2.) Mark non-removable dependencies, handle each cycle separately
		for (DirectedGraph<Script> dgCycle: cycles) {
			DependencyGraph cycle = new DependencyGraph(dgCycle);

			// we simulate marking by building up a new DependencyGraph (=> we can benefit from the inherited dependency calculation)
			DependencyGraph markGraph = new DependencyGraph(cycle);
			markGraph.removeDependencies(markGraph.getDirectDependencies());

			for (Script script: cycle.getVertices()) {
				for (DatabaseID oDB: script.outputDatabases) {
					if (oDB.isFresh()) {
						Set<Script> dependents = cycle.getDirectDependents(script);
						for (Script dependent: dependents) {
							if (dependent.inputDatabases.contains(oDB)) {
								// script writes to oDB*, dependent reads from oDB
								markGraph.addEdge(dependent, script, oDB.toString());
							}
						}
					}
				}
			}

			// 3.) remove contradicting dependencies
			Map<Script,Set<Script>> inheritedDependencies = markGraph.getAllDependencies();
			for (Script script: inheritedDependencies.keySet()) {
				for (Script dependency: inheritedDependencies.get(script)) {
					Edge edge = cycle.getEdge(dependency, script);
					if (edge != null) {
						suggestion.addSuggestion(dependency, script); // remove contradicting!
						suggestion.addInfo("Removed 'Fresh-DB'-Contradicting-Dependency: '" + dependency + "' -"+(edge.annotation!=null?edge.annotation:"")+"-> '" + script + "'"
								+ (markGraph.getEdge(script, dependency)!=null ? " (because of strong direct dependency '"+script+"' -"+markGraph.getEdge(script, dependency).annotation+"-> '" + dependency + "')" : ""));
					}
				}
			}
		}
	}
}
