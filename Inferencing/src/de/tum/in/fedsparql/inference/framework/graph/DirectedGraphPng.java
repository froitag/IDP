package de.tum.in.fedsparql.inference.framework.graph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.plantuml.SourceStringReader;
import de.tum.in.fedsparql.inference.framework.stuff.APng;


/**
 * generates a PNG-Graph for a given DependencyGraph
 */
public class DirectedGraphPng extends APng {

	/* constructors */
	/**
	 * @param dGraph
	 */
	public DirectedGraphPng(DirectedGraph<?> dGraph) {
		this(dGraph, null, null);
	}
	/**
	 * @param dGraph
	 * @param title appears as header on the picture
	 */
	public DirectedGraphPng(DirectedGraph<?> dGraph, String title) {
		this(dGraph, title, null);
	}
	/**
	 * @param dGraph
	 * @param title appears as header on the picture
	 * @param vertexAnnotations (vertex => map("varName" => "description"))
	 */
	public DirectedGraphPng(DirectedGraph<?> dGraph, String title, Map<Object,Map<String,String>> vertexAnnotations) {
		_png = _genPNG(title, dGraph, vertexAnnotations);
	}

	/* protected methods */
	/**
	 * generates a PNG-Graph using PlantUML+GraphVIZ
	 * @return byte array containing the PNG || NULL if generation failed
	 */
	protected byte[] _genPNG(String title, DirectedGraph<?> dGraph, Map<Object,Map<String,String>> vertexAnnotations) {

		// assemble PlantUML graph description string
		String source = "";
		source += "@startuml\n";
		if (title != null) {
			source += "title "+title+"\n";
		}

		// create synonyms for each vertex + annotate them
		Map<Object,String> plantIdentifiers = new HashMap<Object,String>();
		int i=0;
		for (Object vertex: dGraph.getVertices()) {
			plantIdentifiers.put(vertex, "s"+(i++));
			source += "state \""+vertex+"\" as "+plantIdentifiers.get(vertex)+"\n";

			if (vertexAnnotations != null && vertexAnnotations.containsKey(vertex)) {
				for (String key: vertexAnnotations.get(vertex).keySet()) {
					source += plantIdentifiers.get(vertex)+" : " + key + " = " + vertexAnnotations.get(vertex).get(key) + "\n";
				}
			}
		}

		// draw arrows
		for (DirectedGraph<?>.Edge edge: dGraph.getEdges()) {
			source += plantIdentifiers.get(edge.origin) + " --> " + plantIdentifiers.get(edge.destination) + (edge.annotation!=null ? " : "+edge.annotation : "") + "\n";
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
