package de.tum.in.fedsparql.inference.io;

public class Node {

	private String name = "Undefined";
	private String host = "localhost";
	private int port = 2221;
	
	public Node(String name, String host, int port){
		this.name = name;
		this.host = host;
		this.port = port;
	}
	
	public String getName() {
		return name;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
