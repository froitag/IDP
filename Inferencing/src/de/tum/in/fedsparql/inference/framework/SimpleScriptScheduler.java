package de.tum.in.fedsparql.inference.framework;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.ExecutionEnvironment.ProcessingUnit;

public class SimpleScriptScheduler extends ScriptScheduler {


	public SimpleScriptScheduler(ExecutionEnvironment environment) {
		super(environment);
	}

	@Override
	public Schedule generateSchedule(ScriptCollection scriptCollection) {
		Schedule schedule = new Schedule();
		int unitCount = _environment.processingUnits.size();

		Set<Script> scripts=scriptCollection.getScripts();
		Set<Script> processed=new HashSet<Script>();
		Set<Set<Script>> independentScripts = scriptCollection.getIndependencies();
		Set<Script> nextScripts = scriptCollection.getIndependentScripts();

		while (processed.size() != scripts.size()) {

			if (nextScripts.size()==0) {
				System.out.println("FAIL NO MORE SCRIPTS AVAILABLE, aborting scheduling");
			}
			Script script = nextScripts.iterator().next();

			Set<Script> set=null;
			int setScriptCount=0;
			for (Set<Script> indiSet: independentScripts) {
				if (!indiSet.contains(script)) continue;

				if (setScriptCount < Math.min(indiSet.size(), unitCount)) {
					set = new HashSet<Script>();
					setScriptCount=0;
					for (Script eScript: indiSet) {
						if (unitCount < ++setScriptCount) break;

						set.add(eScript);
					}
				}
			}
			if (set==null || set.size()<=0) {
				set = new HashSet<Script>();
				set.add(script);
			}

			ScheduleStep step = new ScheduleStep();
			for (ProcessingUnit pu: _environment.processingUnits) {
				Script eScript = set.size()>0 ? (Script)set.iterator().next() : null;
				step.put(pu, eScript);

				if (eScript != null) {
					set.remove(eScript);
					processed.add(eScript);
					independentScripts = _removeScript(eScript, independentScripts);
					nextScripts.remove(eScript);
				}

				Set<Script> notProcessed = new HashSet<Script>(scripts);
				notProcessed.removeAll(processed);
				for (Script newScript: notProcessed) {
					if (!scriptCollection.dependsOn(newScript, notProcessed)) {
						nextScripts.add(newScript);
					}
				}
			}
			schedule.add(step);
		}

		return schedule;
	}

	protected Set<Set<Script>> _removeScript(Script script, Set<Set<Script>> independentScripts) {
		if (script==null) return null;

		Set<Set<Script>> ret = new HashSet<Set<Script>>();
		for (Set<Script> set: independentScripts) {
			Set<Script> newSet = new HashSet<Script>();
			for (Script s: set) {
				if (!script.equals(s)) {
					newSet.add(script);
				}
			}
			if (newSet.size() > 1) {
				ret.add(newSet);
			}
		}

		return ret;
	}
}
