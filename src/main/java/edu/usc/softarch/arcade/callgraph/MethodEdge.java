package edu.usc.softarch.arcade.callgraph;

import java.io.Serializable;

/**
 * @author joshua
 */
public class MethodEdge implements Serializable {
	// #region FIELDS ------------------------------------------------------------
	private static final long serialVersionUID = 5230066023797650367L;
	public MyMethod src;
	public MyMethod tgt;
	// #endregion FIELDS ---------------------------------------------------------

	// #region CONSTRUCTORS ------------------------------------------------------
	public MethodEdge(MyMethod src, MyMethod tgt) {
		this.src = new MyMethod(src);
		this.tgt = new MyMethod(tgt);
	}
	// #endregion CONSTRUCTORS ---------------------------------------------------
	
	// #region MISC --------------------------------------------------------------
	public boolean equals(Object o) {
		if (o == null) return false;

		MethodEdge e = (MethodEdge) o;
		return this.src.equals(e.src) && this.tgt.equals(e.tgt);
	}
	
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + (src == null ? 0 : src.hashCode());
		hash = 37 * hash + (tgt == null ? 0 : tgt.hashCode());
		return hash;
	}
	
	public String toString() {
		return "(" + src.toString() + "," + tgt.toString() + ")";
	}
	// #endregion MISC -----------------------------------------------------------
	
	// #region IO ----------------------------------------------------------------
	public String toDotString() {
		return "\t\"" + src.toString() + "\" -> \"" + tgt.toString() + "\";";
	}
	// #endregion IO -------------------------------------------------------------
}