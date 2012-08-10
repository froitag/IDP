package de.tum.in.fedsparql.inference.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.algorithms.TopologicalSorting;
import de.tum.in.fedsparql.inference.framework.plan.ExecutionPlanPng;
import de.tum.in.fedsparql.inference.framework.plan.ExecutionStep;
import de.tum.in.fedsparql.inference.framework.plan.Finish;
import de.tum.in.fedsparql.inference.framework.plan.Fork;
import de.tum.in.fedsparql.inference.framework.plan.ScriptExecution;
import de.tum.in.fedsparql.inference.framework.plan.Start;
import de.tum.in.fedsparql.inference.framework.plan.SynchronizationPoint;
import de.tum.in.fedsparql.inference.framework.plandispatcher.Scheduler;
import de.tum.in.fedsparql.inference.framework.xceptions.DependencyCycleException;
import de.tum.in.fedsparql.inference.framework.xceptions.ExecutionPlanException;

/**
 * Creates an ExecutionPlan from a DependencyGraph
 */
public class ExecutionPlan extends de.tum.in.fedsparql.inference.ExecutionPlan {

	/**
	 * Creates an ExecutionPlan.
	 * 
	 * @param _dGraph DependencyGraph to create a ExecutionPlan for
	 * @throws DependencyCycleException if the given DependencyGraph contains circular-dependencies
	 */
	public ExecutionPlan(DependencyGraph dGraph) throws ExecutionPlanException {
		_dGraph = new DependencyGraph(dGraph);
		_steps = new ArrayList<ExecutionStep>();
		_genPlan();
	}


	/* public methods */
	/**
	 * gets a copy of the underlying DependencyGraph of this ExecutionPlan.
	 * (changes to this DependencyGraph doesn't affect this ExecutionPlan)
	 */
	public DependencyGraph getDependencyGraph() {
		return new DependencyGraph(_dGraph);
	}
	/**
	 * get steps
	 */
	public ArrayList<ExecutionStep> getSteps() {
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
	 * generates a PNG-UML-Graph using PlantUML+GraphVIZ
	 */
	public ExecutionPlanPng generatePNG() {
		return new ExecutionPlanPng(this);
	}


	/* protected methods */
	protected void _genPlan() throws ExecutionPlanException {

		// check if input is OK
		List<Set<Object>> topologicalSortedSets=null;
		try {
			topologicalSortedSets = (new TopologicalSorting(_dGraph)).getSteps();
		} catch (DependencyCycleException e) {
			throw new ExecutionPlanException("DependencyGraph must be free of circular dependencies!", e);
		}
		if (topologicalSortedSets==null || topologicalSortedSets.size()<=0) {
			throw new ExecutionPlanException("DependencyGraph must at least contain 1 Script!");
		}


		// create start step
		_startStep = _createStart();

		// create initial step after start
		Set<Script> startScripts = new HashSet<Script>();
		for (Object script: topologicalSortedSets.remove(0)) {
			startScripts.add((Script)script);
		}

		if (startScripts.size() > 1) {
			// FORK

			Set<ExecutionStep> branches = new HashSet<ExecutionStep>();
			for (Script script: startScripts) {
				branches.add(_createScriptExecution(script));
			}

			_startStep.next = _createFork(branches);
		} else {
			// SINGLE EXECUTION

			_startStep.next = _createScriptExecution(startScripts.iterator().next());
		}


		// go through the topological sorting steps + build up execution-plan
		for (int i=0; i<topologicalSortedSets.size(); i++) {
			for (Object oScript: topologicalSortedSets.get(i)) {
				Script script = (Script) oScript;

				ExecutionStep step = _createScriptExecution(script);

				Set<Script> dependencies = _dGraph.getDirectDependencies(script);
				if (dependencies.size() > 1) {
					step = _createSynchPoint(step);
				}
				for (Script dependency: _dGraph.getDirectDependencies(script)) {
					_connect(step, _scriptExecutions.get(dependency));
				}
			}
		}


		// attach finish state
		Set<ScriptExecution> finalScripts=new HashSet<ScriptExecution>();
		for (ExecutionStep step: _steps) {
			if ((step instanceof ScriptExecution) && ((ScriptExecution) step).next==null) {
				finalScripts.add((ScriptExecution) step);
			}
		}
		_createFinish(finalScripts);
	}

	protected void _connect(ExecutionStep step, ScriptExecution depSe) throws ExecutionPlanException {
		if (depSe == null) return;

		// add depSe to the SynchronizationPoint if the current step is one
		if (step instanceof SynchronizationPoint) {
			((SynchronizationPoint) step).waitFor.add(depSe);
		}

		// link the step to the dependency
		if (depSe.next == null) {

			// next --> SINGLE EXECUTION
			depSe.next = step;

		} else if (depSe.next instanceof ScriptExecution) {

			// next --> FORK
			Set<ExecutionStep> branches = new HashSet<ExecutionStep>();
			branches.add(depSe.next);
			branches.add(step);

			depSe.next = _createFork(branches);

		} else if (depSe.next instanceof Fork) {

			// next +-> FORK
			Fork f = (Fork) depSe.next;
			f.branches.add(step);

		} else if (depSe.next instanceof SynchronizationPoint) {

			// next --> FORK
			Set<ExecutionStep> branches = new HashSet<ExecutionStep>();
			branches.add(depSe.next);
			branches.add(step);

			depSe.next = _createFork(branches);

		} else {
			throw new ExecutionPlanException("Reached a '"+depSe.next.getClass().getSimpleName()+"' while trying to connect two steps!");
		}
	}
	protected Start _createStart() {
		Start start = new Start(_steps.size());
		_steps.add(start);

		return start;
	}
	protected Finish _createFinish(Set<ScriptExecution> finalScripts) {
		Finish finish = new Finish(_steps.size());
		for (ScriptExecution se: finalScripts) {
			se.next = finish;
			finish.waitFor.add(se);
		}
		_steps.add(finish);

		return finish;
	}
	protected ScriptExecution _createScriptExecution(Script script) {
		ScriptExecution se = new ScriptExecution(_steps.size(), script, _dGraph);
		_scriptExecutions.put(script, se);
		_steps.add(se);

		return se;
	}
	protected Fork _createFork(Set<ExecutionStep> branches) {
		Fork f = new Fork(_steps.size());
		for (ExecutionStep branch: branches) {
			f.branches.add(branch);
		}
		_steps.add(f);

		return f;
	}
	protected SynchronizationPoint _createSynchPoint(ExecutionStep next) {
		SynchronizationPoint synchPoint = new SynchronizationPoint(_steps.size(), next);
		_steps.add(synchPoint);

		return synchPoint;
	}


	/* protected member */
	protected DependencyGraph _dGraph;
	protected ArrayList<ExecutionStep> _steps;
	protected Start _startStep;

	Map<Script,ScriptExecution> _scriptExecutions = new HashMap<Script,ScriptExecution>();
}
