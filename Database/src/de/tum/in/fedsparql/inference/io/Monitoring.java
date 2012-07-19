package de.tum.in.fedsparql.inference.io;

public abstract class Monitoring {

	public static class Load{
		public final double io;
		public final double cpu;
		public Load(double io, double cpu){
			this.io = io;
			this.cpu = cpu;
		}
	}
	
	public abstract Load monitor(Node node);
}