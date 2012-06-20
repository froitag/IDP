package de.tum.in.fedsparql.inference.framework;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.ExecutionEnvironment.ProcessingUnit;

public class ExtendedScriptScheduler extends ScriptScheduler {


	public ExtendedScriptScheduler(ExecutionEnvironment environment) {
		super(environment);
	}

	@Override
	public Schedule generateSchedule(ScriptCollection scriptCollection) {
		Schedule schedule = new Schedule();

		Set<Script> scripts=scriptCollection.getScripts();
		Set<Script> processed=new HashSet<Script>();
		Set<Script> nextScripts = scriptCollection.getIndependentScripts();

		while (processed.size() != scripts.size()) {

			ScheduleStep step = new ScheduleStep();

			for (ProcessingUnit pu: _environment.processingUnits) {
				Script script=null;
				if (!nextScripts.isEmpty()) {
					script = nextScripts.iterator().next();

					nextScripts.remove(script);
					processed.add(script);
				}

				step.put(pu, script);
			}

			schedule.add(step);

			// calculate next scripts
			Set<Script> notProcessed = new HashSet<Script>(scripts);
			notProcessed.removeAll(processed);
			for (Script nextScript: notProcessed) {
				if (!scriptCollection.dependsOn(nextScript, notProcessed)) {
					nextScripts.add(nextScript);
				}
			}
		}

		return schedule;
	}
}
