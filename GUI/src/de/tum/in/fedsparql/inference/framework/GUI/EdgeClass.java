package de.tum.in.fedsparql.inference.framework.GUI;

import de.tum.in.fedsparql.inference.framework.Script;

public class EdgeClass {

	boolean deleted = false;
	Script vertex1;
	Script vertex2;
	
	public EdgeClass(Script vertex1, Script vertex2) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
	}
	public EdgeClass(Script vertex1, Script vertex2, boolean deleted) {
		this(vertex1,vertex2);
		this.deleted = deleted;
	}
	
	public boolean getDeleted() {
		return deleted;
	}
	
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof EdgeClass) {
			EdgeClass e = (EdgeClass) obj;
			
			ret = e.vertex1!=null && e.vertex1.equals(this.vertex1);
			ret = ret && e.vertex2!=null && e.vertex2.equals(this.vertex2);
		}
		return ret;
	}
	
	@Override
	public int hashCode() {
		return (this.vertex1.toString() + "|" + this.vertex2.toString()).hashCode();
	}
	
}
