package de.tum.in.fedsparql.inference.framework;

import java.util.HashSet;
import java.util.Set;

/**
 * represents an environment in which scripts may be executed.
 */
public class ExecutionEnvironment {

	/** a set of processing units (=computers/cpus/cores) */
	public Set<ProcessingUnit> processingUnits=new HashSet<ProcessingUnit>();

	public ExecutionEnvironment(String[] processingUnits) {
		for (String processingUnit: processingUnits) {
			this.processingUnits.add(new ProcessingUnit(processingUnit));
		}
	}

	public static class ProcessingUnit {
		public String id;

		public ProcessingUnit(String id) {
			this.id = id;
		}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ProcessingUnit) || obj==null) return false;

			ProcessingUnit pu = (ProcessingUnit) obj;
			return pu.id!=null ? pu.id.equals(this.id) : this.id==null;
		}
		@Override
		public int hashCode() {
			return this.id!=null ? this.id.hashCode() : null;
		}
		@Override
		public String toString() {
			return this.id;
		}
	}
}
