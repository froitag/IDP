package de.tum.in.fedsparql.inference.framework.ExecutionPlanDispatcher;

import java.util.List;

import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;
import de.tum.in.fedsparql.inference.io.Database;
import de.tum.in.fedsparql.inference.io.Dispatcher;
import de.tum.in.fedsparql.inference.io.IO;
import de.tum.in.fedsparql.inference.io.Monitoring;
import de.tum.in.fedsparql.inference.io.Monitoring.Load;
import de.tum.in.fedsparql.inference.io.Node;

public class DBPriorityScheduler extends PriorityScheduler {

	public DBPriorityScheduler(ScriptCollection collection, IO io,
			Monitoring monitoring, Dispatcher dispatcher) {
		super(collection, io, monitoring, dispatcher);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected double getNodePriority(Script script, Node node) {
		Load load = monitoring.monitor(node);
		List<Database> databases = io.getDatabasesForNode(node);
		
		int hostedInputDatabases = 0;
		int hostedOutputDatabases = 0;
		for (Database database : script.inputDatabases) {
			if (databases.contains(database))
				hostedInputDatabases++;
		}
		for (Database database : script.outputDatabases) {
			if (databases.contains(database))
				hostedOutputDatabases++;
		}
		
		return (1 - load.cpu) * 50 + (1 - load.io) * 50 + hostedInputDatabases * 100 + hostedOutputDatabases * 100;
	}

}
