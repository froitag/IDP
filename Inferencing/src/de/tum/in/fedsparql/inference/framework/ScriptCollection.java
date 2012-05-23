package de.tum.in.fedsparql.inference.framework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import de.tum.in.fedsparql.inference.framework.exceptions.CircularDependencyException;

public class ScriptCollection {

	/* constructors */
	/**
	 * constructor
	 * @param scripts
	 * @throws CircularDependencyException
	 */
	public ScriptCollection(Script[] scripts) throws CircularDependencyException {
		for (Script script: scripts) {
			_addScript(script);
		}

		_calculateDependencies();
	}


	/* public methods */
	/**
	 * gets a set of copies of the collection's scripts
	 */
	public Set<Script> getScripts() {
		Set<Script> ret=new HashSet<Script>();
		for (Script script: _scripts) {
			ret.add(new Script(script));
		}

		return ret;
	}
	//	public Set<String> getInputDataabses() {
	//
	//	}
	//	public Set<String> getOutputDatabases() {
	//
	//	}

	public void printDependencies() {
		/**
		  	r5:
			r3:
			r1:
			r4:	r3, r2
			r2:	r1
		 */

		for (Script script: _scriptDependencies.keySet()) {
			System.out.print(script.id+":");

			for (Script dependency: _scriptDependencies.get(script)) {
				System.out.print("\t" + dependency.id);
			}

			System.out.println();
		}
	}
	public void printInheritedDependencies() {
		/**
		  	r5:
			r3:
			r1:
			r4:	r3, r2
			r2:	r1
		 */

		for (Script script: _scriptInheritedDependencies.keySet()) {
			System.out.print(script.id+":");

			for (Script dependency: _scriptInheritedDependencies.get(script)) {
				System.out.print("\t" + dependency.id);
			}
			System.out.println();
		}
	}



	/* protected helper */
	/**
	 * add a script to the collection
	 * 
	 * @param script
	 * @return this for fluent interface
	 */
	protected ScriptCollection _addScript(Script script) {
		if (script==null) return this;

		script = new Script(script); // copy the script to prevent the outside world from having a direct reference to it

		// add it to the hashset
		_scripts.add(new Script(script));

		// add its output databases to the _outputRelations cache
		for (String oDB: script.outputDatabases) {
			if (!_outputRelations.containsKey(oDB)) {
				_outputRelations.put(oDB, new HashSet<Script>());
			}

			_outputRelations.get(oDB).add(script);
		}

		return this;
	}
	/**
	 * calculate the script dependencies
	 * @return this for fluent interface
	 * @throws CircularDependencyException
	 */
	protected ScriptCollection _calculateDependencies() throws CircularDependencyException {

		_scriptDependencies = new HashMap<Script,Set<Script>>();
		_scriptInheritedDependencies = new HashMap<Script,Set<Script>>();


		/** calculate direct dependencies */
		for (Script script: _scripts) {
			// add the scripts dependencies to the _scriptDependencies cache
			_scriptDependencies.put(script, new HashSet<Script>());

			for (String iDB: script.inputDatabases) {
				if (!_outputRelations.containsKey(iDB)) continue;

				for (Script dependency: _outputRelations.get(iDB)) {
					_scriptDependencies.get(script).add(dependency);
				}
			}
		}


		/** calculate inherited dependencies */
		for (Script script: _scripts) {
			_scriptInheritedDependencies.put(script, new HashSet<Script>());

			// walk through every available dependency path and check for circles + add dependencies to the cache
			Queue<InheritedDependenciesQueueEntry> queue = new LinkedList<InheritedDependenciesQueueEntry>();
			queue.add(
					new InheritedDependenciesQueueEntry(
							script,
							new LinkedList<Script>()
							)
					);
			while (queue.size() > 0) {
				List<Script> qPath = queue.peek().path;
				Script qScript = queue.peek().script;
				queue.remove();

				// add the current script as dependency to all scripts in the current path
				for (Script pathEntry: qPath) {
					_scriptInheritedDependencies.get(pathEntry).add(qScript);
				}

				// add the new dependencies to the queue
				for (Script dependency: _scriptDependencies.get(qScript)) {
					if (qPath.contains(dependency)) {
						throw new CircularDependencyException("Circular Dependency found! Please remove all circles before calculating..");
					}
					List<Script> dependencyPath = new LinkedList<Script>(qPath);
					dependencyPath.add(qScript);
					queue.add(new InheritedDependenciesQueueEntry(dependency, dependencyPath));
				}
			}
		}

		return this;
	}
	protected class InheritedDependenciesQueueEntry {
		public Script script;
		public List<Script> path;

		public InheritedDependenciesQueueEntry(Script entry, List<Script> path) {
			this.script = entry;
			this.path = path;
		}
	}

	/* protected member */
	/** this collection's scripts */
	protected Set<Script> _scripts=new HashSet<Script>();
	/** Map(outputDatabase => Set<Scripts> which write into it) */
	protected Map<String,Set<Script>> _outputRelations=new HashMap<String,Set<Script>>();
	/** Map(Script => Set<Scripts> which the first script directly depends on) */
	protected Map<Script,Set<Script>> _scriptDependencies;
	/** Map(Script => Set<Scripts> which the first script depends on) */
	protected Map<Script,Set<Script>> _scriptInheritedDependencies;
}
