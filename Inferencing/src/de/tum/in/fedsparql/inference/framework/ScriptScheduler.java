package de.tum.in.fedsparql.inference.framework;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class ScriptScheduler {

	/* constructor */
	public ScriptScheduler(ExecutionEnvironment environment) {
		_environment = environment;
	}

	/* public methods */
	public abstract Schedule generateSchedule(ScriptCollection scriptCollection);


	/* protected member */
	protected ExecutionEnvironment _environment;


	/* public static classes */
	public static class Schedule extends LinkedList<ScheduleStep> {
		@Override
		public String toString() {
			String ret="";

			int i=0;
			for (ScheduleStep step: this) {
				ret += "Step "+(++i)+": ";
				ret += step;
				ret += "\r\n";
			}

			return ret;
		}

		private static final long serialVersionUID = 1L;
	}
	public static class ScheduleStep extends HashMap<ExecutionEnvironment.ProcessingUnit,Script> {
		private static final long serialVersionUID = 1L;
	}
}
