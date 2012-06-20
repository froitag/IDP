package de.tum.in.fedsparql.inference.framework.GUI;

import de.tum.in.fedsparql.inference.io.Database;

public class TestDatabase implements Database {

	String name;
	
	public TestDatabase(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void drop() {
		// TODO Auto-generated method stub

	}

}
