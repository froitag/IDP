package de.tum.in.fedsparql.inference.framework.ExecutionPlan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.sourceforge.plantuml.SourceStringReader;
import de.tum.in.fedsparql.inference.framework.DependencyGraph;
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher.Scheduler;
import de.tum.in.fedsparql.inference.framework.exceptions.CircularDependencyException;

public class ExecutionPlan {

	/**
	 * Creates an ExecutionPlan.
	 * 
	 * @param dGraph DependencyGraph to create a ExecutionPlan for
	 * @throws CircularDependencyException if the given DependencyGraph contains circular-dependencies
	 */
	public ExecutionPlan(DependencyGraph dGraph) throws CircularDependencyException {
		if (dGraph.containsCycle()) {
			throw new CircularDependencyException("ScriptCollection contains circular dependencies! Please remove all cycles..");
		}
		_dGraph = new DependencyGraph(dGraph);

		int stepId=0;

		Set<Script> processableScripts;
		Set<Script> done_scripts = new HashSet<Script>();
		Set<ScriptExecution> currentExecs = new HashSet<ScriptExecution>();

		// create start step
		_startStep = new Start(++stepId);
		_endStep = new Finish(++stepId);
		_steps.add(_startStep);
		_steps.add(_endStep);

		// add initial
		processableScripts = _dGraph.getIndependentScripts();
		if (processableScripts.size() > 0) {
			if (processableScripts.size() > 1) { // FORK
				Fork f = new Fork(++stepId); // create fork
				_steps.add(f);

				for (Script script: processableScripts) {
					// create _scriptExecution step
					ScriptExecution se = new ScriptExecution(++stepId);
					se.script = script;
					se.scriptCollection = dGraph;
					_steps.add(se);

					f.branches.add(se);
					currentExecs.add(se);
				}

				_startStep.next = f;
			} else { // single execution
				ScriptExecution se = new ScriptExecution(++stepId);
				se.script = processableScripts.iterator().next();
				se.scriptCollection = dGraph;
				currentExecs.add(se);

				_startStep.next = se;
				_steps.add(se);
			}
		}

		// create plan
		/**
		 * SynchronizationPoint before every Script that has more than 1 dependency -> inferencedDpenedenciesVV
		 * 	- keep temporary array of SynchronizationPoints
		 *  - only add scripts to synchronpoints that directly depend on it
		 *  - add+link ScriptExecution to synchropoint as soon as its handled
		 *  - move SynchronizationPoint from array into the plan as soon as
		 * 
		 * 
		 * - link to finish if no scripts depend on the script! -> inferencedDpenedenciesVV
		 * - direct link to next scriptexecution if it has only 1 dependency -> inferencedDpenedenciesVV
		 * - fork if script has more than 1 dependency (ELSE)
		 */

		Map<Script,SynchronizationPoint> synchPoints=new HashMap<Script,SynchronizationPoint>();
		Set<Script> availableScripts = _dGraph.getScripts();
		while (done_scripts.size() < availableScripts.size() && currentExecs.size()>0) { // loop until all _scripts were processed


			Set<ScriptExecution> nextExecs=new HashSet<ScriptExecution>();
			for (ScriptExecution exec: currentExecs) { // loop through the current _scriptExecution steps

				done_scripts.add(exec.script);
				Set<Script> execNextScripts = _dGraph.getDirectDependentScripts(exec.script);

				if (execNextScripts.size() == 0) {

					/*** LINK TO FINISH ***/
					exec.next = _endStep;
					_endStep.waitFor.add(exec);

				} else if (execNextScripts.size() == 1) {

					/*** ONE SINGLE LINK ***/

					// the link type depends on the dependencies of the script
					Script script = execNextScripts.iterator().next();
					Set<Script> scriptDeps = _dGraph.getDirectDependencies(script);

					if (scriptDeps.size() > 1) {
						// SYNCH POINT

						if (!synchPoints.containsKey(script)) { // create synch point
							// create script execution that follows synch point
							ScriptExecution se = new ScriptExecution(++stepId);
							se.script = script;
							se.scriptCollection = dGraph;

							nextExecs.add(se);
							_steps.add(se);


							// create synch point
							SynchronizationPoint synchPoint = new SynchronizationPoint(++stepId);
							synchPoints.put(script, synchPoint);
							synchPoint.next = se;

							_steps.add(synchPoint);
						}
						synchPoints.get(script).waitFor.add(exec);
						exec.next = synchPoints.get(script);
					} else {
						// SEQUENTIAL SCRIPT EXECUTION
						ScriptExecution se = new ScriptExecution(++stepId);
						se.scriptCollection = dGraph;
						se.script = script;

						exec.next = se;
						nextExecs.add(se);
						_steps.add(se);
					}


				} else {

					/*** FORK ***/

					Fork f = new Fork(++stepId);
					_steps.add(f);

					for (Script script: execNextScripts) {
						// the link type depends on the dependencies of the script
						Set<Script> scriptDeps = _dGraph.getDirectDependencies(script);

						if (scriptDeps.size() > 1) {
							// SYNCH POINT

							if (!synchPoints.containsKey(script)) { // create synch point
								// create script execution that follows synch point
								ScriptExecution se = new ScriptExecution(++stepId);
								se.script = script;
								se.scriptCollection = dGraph;

								nextExecs.add(se);
								_steps.add(se);

								// create synch point
								SynchronizationPoint synchPoint = new SynchronizationPoint(++stepId);
								synchPoints.put(script, synchPoint);
								synchPoint.next = se;

								_steps.add(synchPoint);
							}
							synchPoints.get(script).waitFor.add(exec);
							f.branches.add(synchPoints.get(script));
						} else {
							// SCRIPT EXECUTION

							ScriptExecution se = new ScriptExecution(++stepId);
							se.script = script;
							se.scriptCollection = dGraph;

							f.branches.add(se);
							nextExecs.add(se);
							_steps.add(se);
						}
					}

					exec.next = f;
				}
			}
			currentExecs = nextExecs;
		}
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
	}

	/**
	 * generates a PNG-Graph using PlantUML+GraphVIZ
	 * @return byte array containing the PNG || NULL if generation failed
	 */
	public byte[] generatePNG() {

		// assemble PlantUML graph description string
		String source = "";
		source += "@startuml\n";

		// walk through the ExecutionPlan using a Queue starting with this.getStartStep()
		Queue<QueueEntry> queue = new LinkedList<QueueEntry>();
		queue.add(new QueueEntry(this.getStartStep(),Arrays.asList(new ExecutionStep[0])));

		Set<String> alreadyAdded = new HashSet<String>();
		while (!queue.isEmpty()) {
			QueueEntry curStep = queue.remove();

			//
			List<QueueEntry> toAdd=new ArrayList<QueueEntry>();
			if (curStep.step instanceof Start) {

				toAdd.add(new QueueEntry(((Start)curStep.step).next, Arrays.asList(new ExecutionStep[]{curStep.step})));

			} else if (curStep.step instanceof Finish) {

				for (ExecutionStep previousStep: curStep.previousSteps) {
					String edge = _stepToName(previousStep) + " -right-> " + _stepToName(curStep.step) + "\n";
					if (!alreadyAdded.contains(edge)) {
						source += edge;
						alreadyAdded.add(edge);
					}
				}

			} else if (curStep.step instanceof Fork) {

				Fork fork = (Fork) curStep.step;
				for (ExecutionStep nextStep: fork.branches) {
					toAdd.add(
							new QueueEntry(nextStep, curStep.previousSteps)
							);
				}

			} else if (curStep.step instanceof SynchronizationPoint) {

				toAdd.add(new QueueEntry(((SynchronizationPoint)curStep.step).next, curStep.previousSteps));

			} else if (curStep.step instanceof ScriptExecution) {

				for (ExecutionStep previousStep: curStep.previousSteps) {
					String edge = _stepToName(previousStep) + " --> " + _stepToName(curStep.step) + "\n";
					if (!alreadyAdded.contains(edge)) {
						source += edge;
						alreadyAdded.add(edge);
					}
				}

				toAdd.add(new QueueEntry(((ScriptExecution)curStep.step).next, Arrays.asList(new ExecutionStep[]{curStep.step})));

			}

			queue.addAll(toAdd);
			//			for (QueueEntry add: toAdd) {
			//				queue.add(add);
			//			}
		}

		source += "@enduml\n";
		System.out.println(source);


		// create PNG from `source`-String
		try {
			SourceStringReader reader = new SourceStringReader(source);
			ByteArrayOutputStream png = new ByteArrayOutputStream();
			String desc = reader.generateImage(png);

			if (desc != null) {
				return png.toByteArray();
			}
		} catch (IOException e) {
		}

		return null;
	}
	class QueueEntry {
		public ExecutionStep step;
		public List<ExecutionStep> previousSteps;
		public QueueEntry(ExecutionStep step, List<ExecutionStep> previousSteps) {
			this.step = step;
			this.previousSteps = new ArrayList<ExecutionStep>(previousSteps);
		}

		@Override
		public boolean equals(Object obj) {
			boolean ret=false;

			if (obj instanceof QueueEntry) {
				QueueEntry qe = (QueueEntry) obj;

				ret = qe.step!=null && qe.step.equals(this.step);
				ret = ret && qe.previousSteps!=null && qe.previousSteps.equals(this.previousSteps);
			}

			return ret;
		}
	}
	protected String _stepToName(ExecutionStep step) {
		if (step instanceof Start) {
			return "[*]";
		} else if (step instanceof Fork) {
			return null;
		} else if (step instanceof Finish) {
			return "[*]";
		} else if (step instanceof SynchronizationPoint) {
			return null;
		} else if (step instanceof ScriptExecution) {
			return ((ScriptExecution)step).script.id;
		}

		return null;
	}


	/* protected member */
	protected DependencyGraph _dGraph;
	protected Set<ExecutionStep> _steps=new HashSet<ExecutionStep>();
	protected Start _startStep;
	protected Finish _endStep;
}
