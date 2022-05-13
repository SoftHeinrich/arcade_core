package edu.usc.softarch.arcade.clustering.criteria;

import edu.usc.softarch.arcade.clustering.Architecture;

public class PreSelectedStoppingCriterion
		extends StoppingCriterion {
	private int numClusters;

	public PreSelectedStoppingCriterion(int numClusters) {
		this.numClusters = numClusters;	}

	public boolean notReadyToStop(Architecture arch) {
		return arch.size() != 1 && arch.size() != numClusters; }

	public void setNumClusters(int numClusters) {
		this.numClusters = numClusters; }
}
