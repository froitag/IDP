
package de.tum.in.fedsparql.inference.framework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import de.tum.in.fedsparql.inference.io.Database;

/**
 * Manages a collection of de.tum.in.fedsparql.inference.framework.Script.
 * Is immutable after creation (except the manual removal/adding of dependencies).
 * (actual dependency calculation is done in _calculateDependencies() which is called after DependencyGraph creation + each time the graph was manually changed)
 */
public class DependencyGraph {

	/* constructors */
	/**
	 * Constructor.
	 * Initializes a ScriptCollection with the given Scripts.
	 * 
	 * @param scripts The collection's Scripts.
	 */
	public DependencyGraph(Script[] scripts) {
		for (Script script: scripts) {
			_addScript(script);
		}

		_calculateDependencies();
	}
	/**
	 * Constructor.
	 * Initializes a ScriptCollection with given Scripts and a set of dependencies which shall be ignored.
	 * 
	 * @param scripts The collection's Scripts.
	 * @param manuallyRemovedDependencies Map<Script => Set<Script>>, states that Script is independent from Set<Script>
	 */
	public DependencyGraph(Script[] scripts, Map<Script,Set<Script>> manuallyRemovedDependencies) {
		for (Script script: scripts) {
			_addScript(script);
		}
		_removedDependencies = new HashMap<Script,Set<Script>>(manuallyRemovedDependencies);

		_calculateDependencies();
	}
	/**
	 * Copy Constructor.
	 * Clones a given ScriptCollection.
	 * 
	 * @param scriptCollection
	 */
	public DependencyGraph(DependencyGraph scriptCollection) {
		this(scriptCollection.getScripts().toArray(new Script[0]), scriptCollection.getManuallyRemovedDependencies());
	}


	/* public methods */
	/**
	 * gets the scripts of this collection.
	 * 
	 * @return a set of copies of the scripts
	 */
	public Set<Script> getScripts() {
		Set<Script> ret=new HashSet<Script>();
		for (Script script: _scripts) {
			ret.add(new Script(script));
		}

		return ret;
	}
	/**
	 * returns all databases the scripts read from.
	 */
	public Set<Database> getInputDatabases() {
		Set<Database> ret=new HashSet<Database>();

		for (Script script: _scripts) {
			ret.addAll(script.inputDatabases);
		}

		return ret;
	}
	/**
	 * returns all databases the scripts write to.
	 */
	public Set<Database> getOutputDatabases() {
		Set<Database> ret=new HashSet<Database>();

		for (Script script: _scripts) {
			ret.addAll(script.outputDatabases);
		}

		return ret;
	}

	/**
	 * Gets all manually added dependencies.
	 * 
	 * @return Map<Script => Set<Dependencies>>
	 */
	public Map<Script,Set<Script>> getManuallyAddedDependencies() {
		Map<Script,Set<Script>> ret = new HashMap<Script,Set<Script>>();

		for (Script script: _addedDependencies.keySet()) {
			ret.put(script, new HashSet<Script>());
			for (Script dependency: _addedDependencies.get(script)) {
				ret.get(script).add(dependency);
			}
		}

		return ret;
	}

	/**
	 * Gets all manually removed dependencies.
	 * 
	 * @return Map<Script => Set<Dependencies>>
	 */
	public Map<Script,Set<Script>> getManuallyRemovedDependencies() {
		Map<Script,Set<Script>> ret = new HashMap<Script,Set<Script>>();

		for (Script script: _removedDependencies.keySet()) {
			ret.put(script, new HashSet<Script>());
			for (Script dependency: _removedDependencies.get(script)) {
				ret.get(script).add(dependency);
			}
		}

		return ret;
	}

	/**
	 * states whether this ScriptCollection contains circular dependencies
	 * @return
	 */
	public boolean containsCycle() {
		return _containsCycles;
	}
	/**
	 * gets a map describing the dependencies of the scripts.
	 * (does not consider inherited dependencies, e.g. if C depends on B and B depends on A, the dependency C->A is not included)
	 * only returns copies of the actual scripts.
	 * 
	 * @return Map: Script => Set of scripts this script depends on
	 */
	public Map<Script,Set<Script>> getDirectDependencies() {
		Map<Script,Set<Script>> ret = new HashMap<Script,Set<Script>>();

		for (Script script: _scripts) {
			ret.put(new Script(script),
					this.getDirectDependencies(script));
		}

		return ret;
	}
	/**
	 * returns the dependencies of a script.
	 * (does not consider inherited dependencies, e.g. if C depends on B and B depends on A, the dependency C->A is not included)
	 * only returns copies of the actual scripts.
	 * 
	 * @param script
	 * @return Set of scripts the given script depends on
	 */
	public Set<Script> getDirectDependencies(Script script) {
		if (!_scripts.contains(script)) return null;


		Set<Script> dependencies = new HashSet<Script>();
		if (_scriptDependencies.containsKey(script)) {
			for (Script dependency: _scriptDependencies.get(script)) {
				dependencies.add(new Script(dependency));
			}
		}

		return dependencies;
	}
	public Set<Script> getDirectDependentScripts(Script script) {
		if (!_scripts.contains(script)) return null;


		Set<Script> dependencies = new HashSet<Script>();
		if (_scriptDependenciesVV.containsKey(script)) {
			for (Script dependency: _scriptDependenciesVV.get(script)) {
				dependencies.add(new Script(dependency));
			}
		}

		return dependencies;
	}
	/**
	 * gets a map describing all dependencies of the scripts including inherited ones.
	 * only returns copies of the actual scripts.
	 * 
	 * @return Map: Script => Set of scripts this script depends on
	 */
	public Map<Script,Set<Script>> getAllDependencies() {
		Map<Script,Set<Script>> ret = new HashMap<Script,Set<Script>>();

		for (Script script: _scripts) {
			ret.put(new Script(script),
					this.getAllDependencies(script));
		}

		return ret;
	}
	/**
	 * returns all dependencies of the scripts including inherited ones.
	 * only returns copies of the actual scripts.
	 * 
	 * @param script
	 * @return Set of scripts the given script depends on
	 */
	public Set<Script> getAllDependencies(Script script) {
		if (!_scriptInheritedDependencies.containsKey(script)) return null;


		Set<Script> dependencies = new HashSet<Script>();

		for (Script dependency: _scriptInheritedDependencies.get(script)) {
			dependencies.add(new Script(dependency));
		}

		return dependencies;
	}

	/**
	 * returns all scripts of this collection that do not depend on other scripts (may be used to start processing).
	 * returns a set of copies that can be manipulated without changing the ScriptCollection itself.
	 */
	public Set<Script> getIndependentScripts() {
		Set<Script> ret = new HashSet<Script>();

		for (Script script: _scripts) {
			if (_scriptInheritedDependencies.get(script).size() <= 0) {
				ret.add(new Script(script));
			}
		}

		return ret;
	}


	/**
	 * manually adds a dependency
	 * => "`script` depends on `dependency`"
	 * 
	 * @param script
	 * @param dependency
	 * @return this for fluent interface
	 */
	public DependencyGraph addDependency(Script script, Script dependency) {
		// form set to pass addDependencies(Map)
		Map<Script,Set<Script>> deps = new HashMap<Script,Set<Script>>();
		Set<Script> set = new HashSet<Script>();
		set.add(dependency);
		deps.put(script, set);

		// forward to addDependencies(Map)
		this.addDependencies(deps);

		return this;
	}
	/**
	 * adds the dependencies of the scripts to their regarding set of scripts
	 * 
	 * @param dependencies
	 * @return this for fluent interface
	 */
	public DependencyGraph addDependencies(Map<Script,Set<Script>> dependencies) {
		// add dependencies to the _addedDependencies map
		for (Script script: dependencies.keySet()) {
			if (!_addedDependencies.containsKey(script)) {
				_addedDependencies.put(script, new HashSet<Script>());
			}

			_addedDependencies.get(script).addAll(dependencies.get(script));

			// remove the deps from the manually removedDependencies map
			for (Script dep: dependencies.get(script)) {
				if (_removedDependencies.containsKey(script)) {
					_removedDependencies.get(script).remove(dep);
				}
			}
		}

		// recalculate inherited dependencies etc.
		_calculateDependencies();

		return this;
	}

	/**
	 * removes the dependency of `script` to `dependency`.
	 * => "`script` doesn't depend on `dependency`"
	 * 
	 * @param script
	 * @param dependency
	 * @return this for fluent interface
	 */
	public DependencyGraph removeDependency(Script script, Script dependency) {
		Map<Script,Set<Script>> deps = new HashMap<Script,Set<Script>>();
		Set<Script> set = new HashSet<Script>();
		set.add(dependency);
		deps.put(script, set);

		this.removeDependencies(deps);

		return this;
	}
	/**
	 * removes the dependencies of the scripts to their regarding set of scripts
	 * 
	 * @param dependencies
	 * @return this for fluent interface
	 */
	public DependencyGraph removeDependencies(Map<Script,Set<Script>> dependencies) {
		// add dependencies to the _removedDependencies map
		for (Script script: dependencies.keySet()) {
			if (!_removedDependencies.containsKey(script)) {
				_removedDependencies.put(script, new HashSet<Script>());
			}

			_removedDependencies.get(script).addAll(dependencies.get(script));

			// remove the deps from the manually addedDependencies map
			for (Script dep: dependencies.get(script)) {
				if (_addedDependencies.containsKey(script)) {
					_addedDependencies.get(script).remove(dep);
				}
			}
		}

		// recalculate inherited dependencies
		_calculateDependencies();

		return this;
	}
	/**
	 * removes all dependencies to `dependency`
	 * 
	 * @param dependency
	 * @return this for fluent interface
	 */
	public DependencyGraph removeDependency(Script dependency) {
		// forward to addDependency(Script,Script) for each script of this collection
		// TODO: better: form Map and pass to addDependency(Map) => only 1x _calculateDependencies call
		for (Script script: _scripts) {
			this.removeDependency(script, dependency);
		}

		return this;
	}




	/**
	 * prints all scripts of the collection
	 * 
	 * @return this for fluent interface
	 */
	public DependencyGraph printScripts() {
		for (Script script: _scripts) {
			System.out.println("Script \""+script.id+"\": \t"+script.inputDatabases+"\t->\t"+script.outputDatabases);
		}

		return this;
	}
	/**
	 * prints all direct dependencies
	 * 
	 * @return this for fluent interface
	 */
	public DependencyGraph printDirectDependencies() {
		/**
			r1:[]
			r2:[r5]
			r3:[]
			r4:[r2, r3]
			r5:[]
		 */

		for (Script script: _scriptDependencies.keySet()) {
			System.out.print(script.id+":");
			System.out.print(_scriptDependencies.get(script));
			System.out.println();
		}

		return this;
	}
	/**
	 * prints all inherited dependencies
	 * 
	 * @return this for fluent interface
	 */
	public DependencyGraph printInheritedDependencies() {
		/**
			r1:[]
			r2:[r5]
			r3:[]
			r4:[r2, r3, r5]
			r5:[]
		 */

		for (Script script: _scriptInheritedDependencies.keySet()) {
			System.out.print(script.id+":");
			System.out.print(_scriptInheritedDependencies.get(script));
			System.out.println();
		}

		return this;
	}



	/* protected helper */
	/**
	 * add a script to the collection
	 * 
	 * @param script
	 * @return this for fluent interface
	 */
	protected DependencyGraph _addScript(Script script) {
		if (script==null) return this;

		script = new Script(script); // copy the script to prevent the outside world from having a direct reference to it

		// add it to the hashset
		_scripts.add(new Script(script));

		// add its output databases to the _outputRelations cache
		for (Database oDB: script.outputDatabases) {
			if (!_outputRelations.containsKey(oDB)) {
				_outputRelations.put(oDB, new HashSet<Script>());
			}

			_outputRelations.get(oDB).add(script);
		}

		return this;
	}
	/**
	 * calculates the dependencies between the scripts
	 * 
	 * @return this for fluent interface
	 */
	protected DependencyGraph _calculateDependencies() {

		_scriptDependencies = new HashMap<Script,Set<Script>>(); // direct dependencies (key=script, value=set of scripts the first script depends on)
		_scriptDependenciesVV = new HashMap<Script,Set<Script>>(); // direct dependencies vice versa (key=script, value=set of scripts that depend on the first script)
		_scriptInheritedDependencies = new HashMap<Script,Set<Script>>(); // inherited dependencies (key=script, value=set of scripts the first script depends on)
		_scriptInheritedDependenciesVV = new HashMap<Script,Set<Script>>(); // inherited dependencies vice versa (key=script, value=set of scripts that depend on the first script)
		_containsCycles = false;


		/** calculate direct dependencies */
		for (Script script: _scripts) {
			// add the scripts dependencies to the _scriptDependencies cache
			_scriptDependencies.put(script, new HashSet<Script>());

			for (Database iDB: script.inputDatabases) {
				if (!_outputRelations.containsKey(iDB)) continue;

				for (Script dependency: _outputRelations.get(iDB)) {
					if (_isManuallyRemovedDependency(script, dependency)) continue; // ignore manually removed dependencies

					_scriptDependencies.get(script).add(dependency);
					if (!_scriptDependenciesVV.containsKey(dependency)) {
						_scriptDependenciesVV.put(dependency, new HashSet<Script>());
					}
					_scriptDependenciesVV.get(dependency).add(script);
				}
			}
		}
		// add manually added dependencies
		for (Script script: _addedDependencies.keySet()) {
			if (!_scripts.contains(script)) continue;

			for (Script dep: _addedDependencies.get(script)) {
				if (!_scripts.contains(dep)) continue;

				_scriptDependencies.get(script).add(dep);

				if (!_scriptDependenciesVV.containsKey(dep)) {
					_scriptDependenciesVV.put(dep, new HashSet<Script>());
				}
				_scriptDependenciesVV.get(dep).add(script);
			}
		}


		/** calculate inherited dependencies */
		Set<Script> processed=new HashSet<Script>();
		for (Script script: _scripts) {
			if (processed.contains(script)) continue;

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
				processed.add(qScript);
				queue.remove();

				// add the current script as dependency to all scripts in the current path
				for (Script pathEntry: qPath) {
					if (_isManuallyRemovedDependency(pathEntry, qScript)) continue; // ignore the manually removed dependencies

					_scriptInheritedDependencies.get(pathEntry).add(qScript);
					if (!_scriptInheritedDependenciesVV.containsKey(qScript)) {
						_scriptInheritedDependenciesVV.put(qScript, new HashSet<Script>());
					}
					_scriptInheritedDependenciesVV.get(qScript).add(pathEntry);
				}

				// add the new dependencies to the queue
				for (Script dependency: _scriptDependencies.get(qScript)) {
					if (_isManuallyRemovedDependency(qScript,dependency)) continue;
					if (qPath.contains(dependency)) {
						//throw new CircularDependencyException("Circular Dependency found! Please remove all cycles..");
						_containsCycles = true;
						continue;
					}
					List<Script> dependencyPath = new LinkedList<Script>(qPath);
					dependencyPath.add(qScript);
					queue.add(new InheritedDependenciesQueueEntry(dependency, dependencyPath));
				}
			}
		}

		return this;
	}
	/**
	 * queue entry for calculating inherited dependencies
	 */
	protected class InheritedDependenciesQueueEntry {
		public Script script;
		public List<Script> path;

		public InheritedDependenciesQueueEntry(Script entry, List<Script> path) {
			this.script = entry;
			this.path = path;
		}
	}
	/**
	 * checks if a given dependency was manually removed using this.addDependency or not.
	 * 
	 * @param script
	 * @param dependency
	 */
	protected boolean _isManuallyRemovedDependency(Script script, Script dependency) {
		return (_removedDependencies.containsKey(script) && _removedDependencies.get(script).contains(dependency));
		// ignore the manually removed dependencies
	}
	/**
	 * checks if two scripts depend on each other.
	 * -> returns true if either s1 depends on s2 or s2 depends on s1
	 */
	protected boolean _dependOn(Script s1, Script s2) {
		if (!_scriptInheritedDependencies.containsKey(s1) || !_scriptInheritedDependencies.containsKey(s2)) return false;


		boolean ret=false;

		ret = ret || _scriptInheritedDependencies.get(s1).contains(s2);
		ret = ret || _scriptInheritedDependencies.get(s2).contains(s1);

		return ret;
	}


	/* protected member */
	/** this collection's scripts */
	protected Set<Script> _scripts=new HashSet<Script>();
	/**
	 *  manually removed dependencies.
	 *  Script => Set<Dependencies>
	 */
	protected Map<Script,Set<Script>> _removedDependencies=new HashMap<Script,Set<Script>>();
	/**
	 *  manually added dependencies.
	 *  Script => Set<Dependencies>
	 */
	protected Map<Script,Set<Script>> _addedDependencies=new HashMap<Script,Set<Script>>();



	/* cached stuff, generated from _calculateDependencies */
	/** database<->script relations Map(outputDatabase => Set<Scripts> which write into it) */
	protected Map<Database,Set<Script>> _outputRelations=new HashMap<Database,Set<Script>>();
	/** script dependencies, Map(Script => Set<Scripts> which the key script directly depends on) */
	protected Map<Script,Set<Script>> _scriptDependencies;
	/** script dependencies vice versa, Map(Script => Set<Scripts> which depend on the key script) */
	protected Map<Script,Set<Script>> _scriptDependenciesVV;
	/** inherited dependencies Map(Script => Set<Scripts> which the first script depends on) */
	protected Map<Script,Set<Script>> _scriptInheritedDependencies;
	/** inherited dependencies vice versa, Map(Script => Set<Scripts> which depend on the first script) */
	protected Map<Script,Set<Script>> _scriptInheritedDependenciesVV;
	protected boolean _containsCycles=false;
}
