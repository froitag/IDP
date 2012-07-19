package de.tum.in.fedsparql.inference;

import java.util.List;

import de.tum.in.fedsparql.inference.io.Database;

public class Script {

	private List<Database> input = null;
	private List<Database> output = null;
	private String script = null;
	private String name = null;
	
	public Script(List<Database> input, List<Database> output, String name, String script){
		this.input = input;
		this.output = output;
		this.script = script;
		this.name = name;
	}
	
	public List<Database> getInput() {
		return input;
	}

	public List<Database> getOutput() {
		return output;
	}
	
	public String getScript(){
		return this.script;
	}
	
	public String getName(){
		return this.name;
	}
}
