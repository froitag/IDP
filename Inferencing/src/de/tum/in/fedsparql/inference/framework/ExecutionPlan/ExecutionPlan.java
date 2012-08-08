package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.DependencyGraph;
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;
import de.tum.in.fedsparql.inference.framework.exceptions.CircularDependencyException;

/**
 * Creates an ExecutionPlan from a DependencyGraph
 */
public class ExecutionPlan {

	/**
	 * Creates an ExecutionPlan.
	 * 
	 * @param _dGraph DependencyGraph to create a ExecutionPlan for
	 * @throws CircularDependencyException if the given DependencyGraph contains circular-dependencies
	 */
	public ExecutionPlan(DependencyGraph dGraph) throws CircularDependencyException {
		_dGraph = new DependencyGraph(dGraph);
		_genPlan();
	}


	/* public methods */
	/**
	 * get steps
	 */
	public Set<ExecutionStep> getSteps() {
		return _steps;
	}
	/**
	 * get start step
	 */
	public Start getStartStep() {
		return _startStep;
	}
	/**
	 * run plan
	 * @throws Exception
	 */
	public void execute(Scheduler dispatcher) throws Exception {
		_startStep.execute(dispatcher);
		dispatcher.dispose();
	}

	/**
	 * gets a copy of the underlying DependencyGraph of this ExecutionPlan.
	 * (changes to this DependencyGraph doesn't affect this ExecutionPlan)
	 */
	public DependencyGraph getDependencyGraph() {
		return new DependencyGraph(_dGraph);
	}
	/**
	 * generates a PNG-UML-Graph using PlantUML+GraphVIZ
	 */
	public ExecutionPlanPNG generatePNG() {
		return new ExecutionPlanPNG(this);
	}


	/* protected methods */
	protected void _genPlan() throws CircularDependencyException {
		if (_dGraph.containsCycle()) {
			throw new CircularDependencyException("ScriptCollection contains circular dependencies! Please remove all cycles..");
		}

		/*
		 * create plan using Topological Sorting (http://en.wikipedia.org/wiki/Topological_sorting)
		 * 
		 * 1. process all items that don't depend on any other items
		 * 2. remove the processed items
		 * 3. recalculate dependencies and start again at (1)
		 * 
		 * if the graph doesn't contain circles this method will determine with no items left
		 */
		int stepId=0;

		Set<Script> processedScripts = new HashSet<Script>(); // already processed scripts
		Set<ScriptExecution> independentExecutionSteps = new HashSet<ScriptExecution>(); // set of ScriptExecutions that need to be processed (contains scripts that don't have any dependencies left)

		// create start step
		_steps = new HashSet<ExecutionStep>();
		_startStep = new Start(++stepId);
		_endStep = new Finish(++stepId);
		_steps.add(_startStep);
		_steps.add(_endStep);

		// initialize search, start with scripts that don't depend on any other scripts
		Set<Script> processableScripts = _dGraph.getIndependentScripts();
		if (processableScripts.size() > 0) {
			if (processableScripts.size() > 1) { // FORK
				Fork f = new Fork(++stepId); // create fork
				_steps.add(f);

				for (Script script: processableScripts) {
					// create _scriptExecution step
					ScriptExecution se = new ScriptExecution(++stepId, script, _dGraph);
					_steps.add(se);

					f.branches.add(se);
					independentExecutionSteps.add(se);
				}

				_startStep.next = f;
			} else { // single execution
				ScriptExecution se = new ScriptExecution(++stepId, processableScripts.iterator().next(), _dGraph);
				independentExecutionSteps.add(se);

				_startStep.next = se;
				_steps.add(se);
			}
		}

		// create plan
		Map<Script,SynchronizationPoint> synchPoints=new HashMap<Script,SynchronizationPoint>();
		Set<Script> availableScripts = _dGraph.getScripts();
		while (processedScripts.size() < availableScripts.size() && independentExecutionSteps.size() > 0) { // loop until all Scripts were processed

			Set<ScriptExecution> newIndependentExecutionSteps=new HashSet<ScriptExecution>(); // next set of independent scripts (will replace nextExecutionSteps after this step)
			for (ScriptExecution exec: independentExecutionSteps) { // loop through the current ScriptExecution steps

				processedScripts.add(exec.script);
				Set<Script> nextScripts = _dGraph.getDirectDependentScripts(exec.script); // scripts the current step leads to

				if (nextScripts.size() == 0) {

					/*** LINK TO FINISH ***/
					exec.next = _endStep;
					_endStep.waitFor.add(exec);

				} else if (nextScripts.size() == 1) {

					/*** ONE SINGLE LINK ***/

					// the link type depends on the dependencies of the script
					Script script = nextScripts.iterator().next();
					Set<Script> scriptDeps = _dGraph.getDirectDependencies(script);

					if (scriptDeps.size() > 1) {
						// SYNCH POINT

						if (!synchPoints.containsKey(script)) { // create synch point
							// create script execution that follows synch point
							ScriptExecution se = new ScriptExecution(++stepId, script, _dGraph);
							newIndependentExecutionSteps.add(se);
							_steps.add(se);


							// create synch point
							SynchronizationPoint synchPoint = new SynchronizationPoint(++stepId, se);
							_steps.add(synchPoint);

							synchPoints.put(script, synchPoint);
						}

						synchPoints.get(script).waitFor.add(exec);
						exec.next = synchPoints.get(script);
					} else {
						// SEQUENTIAL SCRIPT EXECUTION
						ScriptExecution se = new ScriptExecution(++stepId, script, _dGraph);

						exec.next = se;
						newIndependentExecutionSteps.add(se);
						_steps.add(se);
					}


				} else {

					/*** FORK ***/

					Fork f = new Fork(++stepId);
					_steps.add(f);

					for (Script script: nextScripts) {
						// the link type depends on the dependencies of the script
						Set<Script> scriptDeps = _dGraph.getDirectDependencies(script);

						if (scriptDeps.size() > 1) {
							// SYNCH POINT

							if (!synchPoints.containsKey(script)) { // create synch point
								// create script execution that follows synch point
								ScriptExecution se = new ScriptExecution(++stepId, script, _dGraph);

								newIndependentExecutionSteps.add(se);
								_steps.add(se);

								// create synch point
								SynchronizationPoint synchPoint = new SynchronizationPoint(++stepId, se);
								_steps.add(synchPoint);

								synchPoints.put(script, synchPoint);
							}
							synchPoints.get(script).waitFor.add(exec);
							f.branches.add(synchPoints.get(script));
						} else {
							// SCRIPT EXECUTION

							ScriptExecution se = new ScriptExecution(++stepId, script, _dGraph);

							f.branches.add(se);
							newIndependentExecutionSteps.add(se);
							_steps.add(se);
						}
					}

					exec.next = f;
				}
			}
			independentExecutionSteps = newIndependentExecutionSteps;
		}
	}


	/* protected member */
	protected DependencyGraph _dGraph;
	protected Set<ExecutionStep> _steps;
	protected Start _startStep;
	protected Finish _endStep;
}
