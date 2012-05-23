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
	/**
	 * copy constructor
	 * 
	 * @param scriptCollection
	 * @throws CircularDependencyException
	 */
	public ScriptCollection(ScriptCollection scriptCollection) throws CircularDependencyException {
		this((Script[])scriptCollection.getScripts().toArray());
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
	public Set<String> getInputDatabases() {
		Set<String> ret=new HashSet<String>();

		for (Script script: _scripts) {
			ret.addAll(script.inputDatabases);
		}

		return ret;
	}
	/**
	 * returns all databases the scripts write to.
	 */
	public Set<String> getOutputDatabases() {
		Set<String> ret=new HashSet<String>();

		for (Script script: _scripts) {
			ret.addAll(script.outputDatabases);
		}

		return ret;
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
		if (!_scriptDependencies.containsKey(script)) return null;


		Set<Script> dependencies = new HashSet<Script>();

		for (Script dependency: _scriptDependencies.get(script)) {
			dependencies.add(new Script(dependency));
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
	//	/**
	//	 * returns a bunch of Set<Script>s. each set only contains scripts that are independent from each other, therefore all the scripts of one set can be processed simultaneously.
	//	 * all possible combinations are returned.
	//	 */
	//	public Set<Set<Script>> getIndependencies() {
	//		Set<Set<Script>> ret = new HashSet<Set<Script>>();
	//
	//		for (Set<Script> set: _independentlyProcessableScripts) {
	//			Set<Script> newSet = new HashSet<Script>();
	//
	//			for (Script script: set) {
	//				newSet.add(new Script(script));
	//			}
	//
	//			ret.add(newSet);
	//		}
	//
	//		return ret;
	//	}
	//	/**
	//	 * returns a bunch of Set<Script>s. each set only contains scripts that are independent from each other, therefore all the scripts of one set can be processed simultaneously.
	//	 * all possible combinations that involve the given script are returned.
	//	 */
	//	public Set<Set<Script>> getIndependencies(Script script) {
	//		if (!_scripts.contains(script)) return null;
	//
	//		Set<Set<Script>> ret = new HashSet<Set<Script>>();
	//
	//		for (Set<Script> set: _independentlyProcessableScripts) {
	//			if (!set.contains(script)) continue;
	//
	//			Set<Script> newSet = new HashSet<Script>();
	//			for (Script s: set) {
	//				newSet.add(new Script(s));
	//			}
	//			ret.add(newSet);
	//		}
	//
	//		return ret;
	//	}
	/**
	 * returns all scripts of this collection that do not depend on other scripts (may be used to start processing).
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
	 * returns true if script depends on scripts.
	 * all scripts must be known to the collection (passed at construction).
	 * 
	 * @param script
	 * @param scripts
	 */
	public boolean dependsOn(Script script, Set<Script> scripts) {
		if (script==null || !_scriptInheritedDependencies.containsKey(script)) return false;

		for (Script dependency: scripts) {
			if (!_scriptInheritedDependencies.containsKey(dependency)) continue;

			if (_scriptInheritedDependencies.get(script).contains(dependency)) {
				return true;
			}
		}

		return false;
	}


	public void printDirectDependencies() {
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
	}
	public void printInheritedDependencies() {
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
	}
	//	public void printIndependentlyProcessableScripts() {
	//		/**
	//		  	r5:
	//			r3:
	//			r1:
	//			r4:	r3, r2
	//			r2:	r1
	//		 */
	//
	//		for (Set<Script> set: _independentlyProcessableScripts) {
	//			System.out.println(set);
	//		}
	//	}



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
		_scriptInheritedDependenciesVV = new HashMap<Script,Set<Script>>();
		//_independentlyProcessableScripts = new HashSet<Set<Script>>();


		/** calculate direct dependencies */
		for (Script script: _scripts) {
			// add the scripts dependencies to the _scriptDependencies cache
			_scriptDependencies.put(script, new HashSet<Script>());
			_scriptInheritedDependencies.put(script, new HashSet<Script>());
			_scriptInheritedDependenciesVV.put(script, new HashSet<Script>());

			for (String iDB: script.inputDatabases) {
				if (!_outputRelations.containsKey(iDB)) continue;

				for (Script dependency: _outputRelations.get(iDB)) {
					_scriptDependencies.get(script).add(dependency);
				}
			}
		}


		/** calculate inherited dependencies */
		Set<Script> processed=new HashSet<Script>();
		for (Script script: _scripts) {
			if (processed.contains(script)) continue;

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
					_scriptInheritedDependencies.get(pathEntry).add(qScript);
					_scriptInheritedDependenciesVV.get(qScript).add(pathEntry);
				}

				// add the new dependencies to the queue
				for (Script dependency: _scriptDependencies.get(qScript)) {
					if (qPath.contains(dependency)) {
						throw new CircularDependencyException("Circular Dependency found! Please remove all cycles before calculating..");
					}
					List<Script> dependencyPath = new LinkedList<Script>(qPath);
					dependencyPath.add(qScript);
					queue.add(new InheritedDependenciesQueueEntry(dependency, dependencyPath));
				}
			}
		}


		//		/** calculate sets of scripts that may independently be processed */
		//		for (Script script: _scripts) {
		//
		//			Set<Script> independentScripts = new HashSet<Script>(_scripts);
		//			independentScripts.removeAll(_scriptInheritedDependencies.get(script)); // remove all scripts the current script depends on
		//			independentScripts.removeAll(_scriptInheritedDependenciesVV.get(script)); // remove all scripts which depend on the current script
		//			independentScripts.remove(script); // remove itself from its own independence list
		//
		//			Set<Set<Script>> currentSets = new HashSet<Set<Script>>();
		//			currentSets.add(new HashSet<Script>(Arrays.asList(new Script[]{script})));
		//
		//			for (Script independentScript: independentScripts) {
		//
		//				Set<Set<Script>> newSets=new HashSet<Set<Script>>();
		//				for (Set<Script> set: currentSets) {
		//					Set<Script> dependencies=new HashSet<Script>();
		//					for (Script setScript: set) {
		//						if (_dependOn(setScript, independentScript)) {
		//							dependencies.add(setScript);
		//						}
		//					}
		//
		//					if (dependencies.size() <= 0) {// script does not depend on any script of the current set -> insert it
		//						set.add(independentScript);
		//					} else { // script depends on scripts of the current set -> split the set up
		//						Set<Script> newSet = new HashSet<Script>(set);
		//						newSet.removeAll(dependencies);
		//						newSets.add(newSet);
		//					}
		//				}
		//				currentSets.addAll(newSets);
		//			}
		//
		//			for (Set<Script> set: currentSets) {
		//				if (set.size() > 1) {
		//					_independentlyProcessableScripts.add(set);
		//				}
		//			}
		//		}

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

	/** database<->script relations Map(outputDatabase => Set<Scripts> which write into it) */
	protected Map<String,Set<Script>> _outputRelations=new HashMap<String,Set<Script>>();
	/** script dependencies Map(Script => Set<Scripts> which the first script directly depends on) */
	protected Map<Script,Set<Script>> _scriptDependencies;
	/** inherited dependencies Map(Script => Set<Scripts> which the first script depends on) */
	protected Map<Script,Set<Script>> _scriptInheritedDependencies;
	/** inherited dependencies vice versa, Map(Script => Set<Scripts> which depend on the first script) */
	protected Map<Script,Set<Script>> _scriptInheritedDependenciesVV;
	/** sets of scripts that may independently be processed */
	//protected Set<Set<Script>> _independentlyProcessableScripts;
}
