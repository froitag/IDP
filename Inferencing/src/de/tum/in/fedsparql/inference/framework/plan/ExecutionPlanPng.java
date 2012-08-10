package de.tum.in.fedsparql.inference.framework.plan;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import de.tum.in.fedsparql.inference.framework.APng;
import de.tum.in.fedsparql.inference.framework.Script;

/**
 * generates a PNG-Graph for a given ExecutionPlan
 */
public class ExecutionPlanPng extends APng {

	/* constructors */
	/**
	 * creates a ExecutionPlanPNG given a specific plan.
	 * @param plan
	 */
	public ExecutionPlanPng(ExecutionPlan plan) {
		_png = _genPNG(plan);
	}


	/* public methods */
	/**
	 * gets the PNG in byte representation
	 */
	@Override
	public byte[] getBytes() {
		return _png;
	}

	/**
	 * saves the PNG to a specific file
	 * @param file
	 * @throws IOException
	 */
	@Override
	public void save(File file) throws IOException {
		if (_png != null) {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(_png);
			fos.close();
		}
	}


	/* protected methods */
	/**
	 * generates a PNG-Graph using PlantUML+GraphVIZ
	 * @return byte array containing the PNG || NULL if generation failed
	 */
	protected byte[] _genPNG(ExecutionPlan plan) {
		// assemble PlantUML graph description string
		String source = "";
		source += "@startuml\n";
		source += "title Execution-Plan\n";

		// create synonyms for each script
		int i=0;
		for (Script script: plan.getDependencyGraph().getScripts()) {
			_plantIDs.put(script, "s"+(i++));
		}

		// create graph
		// annotate scripts with in+out DBs
		for (Script script: _plantIDs.keySet()) {
			source += "state \""+script.id+"\" as "+_plantIDs.get(script)+"\n";
			source += _plantIDs.get(script)+" : in = " + script.inputDatabases + "\n";
			source += _plantIDs.get(script)+" : out = " + script.outputDatabases + "\n";
		}

		// walk through the ExecutionPlan using a Queue starting with plan.getStartStep()
		Queue<PlanProcessingQueueEntry> queue = new LinkedList<PlanProcessingQueueEntry>();
		queue.add(new PlanProcessingQueueEntry(plan.getStartStep(),Arrays.asList(new ExecutionStep[0])));

		Set<String> alreadyAdded = new HashSet<String>();
		while (!queue.isEmpty()) {
			PlanProcessingQueueEntry curStep = queue.remove();

			//
			List<PlanProcessingQueueEntry> toAdd=new ArrayList<PlanProcessingQueueEntry>();
			if (curStep.step instanceof Start) {

				toAdd.add(new PlanProcessingQueueEntry(((Start)curStep.step).next, Arrays.asList(new ExecutionStep[]{curStep.step})));

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
							new PlanProcessingQueueEntry(nextStep, curStep.previousSteps)
							);
				}

			} else if (curStep.step instanceof SynchronizationPoint) {

				toAdd.add(new PlanProcessingQueueEntry(((SynchronizationPoint)curStep.step).next, curStep.previousSteps));

			} else if (curStep.step instanceof ScriptExecution) {

				for (ExecutionStep previousStep: curStep.previousSteps) {
					String edge = _stepToName(previousStep) + " --> " + _stepToName(curStep.step) + "\n";
					if (!alreadyAdded.contains(edge)) {
						source += edge;
						alreadyAdded.add(edge);
					}
				}

				toAdd.add(new PlanProcessingQueueEntry(((ScriptExecution)curStep.step).next, Arrays.asList(new ExecutionStep[]{curStep.step})));

			}

			queue.addAll(toAdd);
		}

		source += "@enduml\n";
		//		System.out.println(source);


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
	/**
	 * maps a step to a PlantUML node
	 * @param step
	 * @return PlantUML node name
	 */
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
			return _plantIDs.get(((ScriptExecution)step).script);
		}

		return null;
	}
	/**
	 * queue entry for "iterating" through the execution plan
	 */
	protected class PlanProcessingQueueEntry {
		public ExecutionStep step;
		public List<ExecutionStep> previousSteps;
		public PlanProcessingQueueEntry(ExecutionStep step, List<ExecutionStep> previousSteps) {
			this.step = step;
			this.previousSteps = new ArrayList<ExecutionStep>(previousSteps);
		}

		@Override
		public boolean equals(Object obj) {
			boolean ret=false;

			if (obj instanceof PlanProcessingQueueEntry) {
				PlanProcessingQueueEntry qe = (PlanProcessingQueueEntry) obj;

				ret = qe.step!=null && qe.step.equals(this.step);
				ret = ret && qe.previousSteps!=null && qe.previousSteps.equals(this.previousSteps);
			}

			return ret;
		}
	}


	/* protected member */
	protected Map<Script,String> _plantIDs=new HashMap<Script,String>();
}
