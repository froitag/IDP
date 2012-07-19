package de.tum.in.fedsparql.inference.dummy;

public class JenaDatabase extends DummyDatabase {
	public JenaDatabase(String name) {
		super(name);
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JenaDatabase) {
			JenaDatabase db = (JenaDatabase) obj;
			return db.getName() != null ? db.getName().equals(this.getName()) : this.getName() == null;
		}
		
		return false;
	}
}
