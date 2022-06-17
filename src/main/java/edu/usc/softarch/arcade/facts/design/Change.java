package edu.usc.softarch.arcade.facts.design;

import java.util.ArrayList;
import java.util.Collection;

public class Change {
	//region ATTRIBUTES
	public final String priorClusterName;
	public final String newClusterName;
	private final Collection<String> addedElements;
	private final Collection<String> removedElements;
	//endregion

	//region CONSTRUCTORS
	public Change(String priorClusterName, String newClusterName,
			Collection<String> addedElements, Collection<String> removedElements) {
		this.priorClusterName = priorClusterName;
		this.newClusterName = newClusterName;
		this.addedElements = new ArrayList<>(addedElements);
		this.removedElements = new ArrayList<>(removedElements);
	}
	//endregion

	//region ACCESSORS
	public Collection<String> getAddedElements() {
		return new ArrayList<>(addedElements); }

	public Collection<String> getRemovedElements() {
		return new ArrayList<>(removedElements); }
	//endregion
}
