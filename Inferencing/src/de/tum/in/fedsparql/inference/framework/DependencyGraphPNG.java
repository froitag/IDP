package de.tum.in.fedsparql.inference.framework;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.SourceStringReader;


/**
 * generates a PNG-Graph for a given DependencyGraph
 */
public class DependencyGraphPNG extends APNG {

	/* constructors */
	/**
	 * creates a DependencyGraphPNG given a specific graph.
	 * @param plan
	 */
	public DependencyGraphPNG(DependencyGraph dGraph) {
		_png = _genPNG(dGraph);
	}

	/* protected methods */
	/**
	 * generates a PNG-Graph using PlantUML+GraphVIZ
	 * @return byte array containing the PNG || NULL if generation failed
	 */
	protected byte[] _genPNG(DependencyGraph dGraph) {

		// assemble PlantUML graph description string
		String source = "";
		source += "@startuml\n";
		source += "title Dependency-Graph\n";

		// create synonyms for each script
		Map<Script,String> plantIdentifiers = new HashMap<Script,String>();
		int i=0;
		for (Script script: dGraph.getScripts()) {
			plantIdentifiers.put(script, "s"+(i++));
		}

		// create graph
		// annotate scripts with in+out DBs
		for (Script script: plantIdentifiers.keySet()) {
			source += "state \""+script.id+"\" as "+plantIdentifiers.get(script)+"\n";
			source += plantIdentifiers.get(script)+" : in = " + script.inputDatabases + "\n";
			source += plantIdentifiers.get(script)+" : out = " + script.outputDatabases + "\n";
		}

		// draw arrows
		Map<Script,Set<Script>> deps = dGraph.getDirectDependencies();
		for (Script script: deps.keySet()) {
			for (Script dep: deps.get(script)) {
				Set<DatabaseID> depDBs = new HashSet<DatabaseID>(dep.outputDatabases);
				depDBs.retainAll(script.inputDatabases);
				source += plantIdentifiers.get(script) + " --> " + plantIdentifiers.get(dep) + " : " + depDBs + "\n";
			}
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

}
