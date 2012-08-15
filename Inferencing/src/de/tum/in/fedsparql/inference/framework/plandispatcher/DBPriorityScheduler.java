package de.tum.in.fedsparql.inference.framework.plandispatcher;

import java.util.List;

import de.tum.in.fedsparql.inference.framework.DatabaseID;
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.graph.DependencyGraph;
import de.tum.in.fedsparql.inference.io.Database;
import de.tum.in.fedsparql.inference.io.Dispatcher;
import de.tum.in.fedsparql.inference.io.IO;
import de.tum.in.fedsparql.inference.io.Monitoring;
import de.tum.in.fedsparql.inference.io.Monitoring.Load;
import de.tum.in.fedsparql.inference.io.Node;

public class DBPriorityScheduler extends PriorityScheduler {

	public DBPriorityScheduler(DependencyGraph collection, IO io,
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
		for (DatabaseID databaseID : script.inputDatabases) {
			if (dbListContains(databaseID, databases))
				hostedInputDatabases++;
		}
		for (DatabaseID database : script.outputDatabases) {
			if (dbListContains(database, databases))
				hostedOutputDatabases++;
		}

		return (1 - load.cpu) * 50 + (1 - load.io) * 50 + hostedInputDatabases * 100 + hostedOutputDatabases * 100;
	}
	protected boolean dbListContains(DatabaseID dbID, List<Database> list) {
		for (Database db: list) {
			if (db.getName()!=null ? db.getName().equals(dbID) : dbID==null) {
				return true;
			}
		}

		return false;
	}

}
