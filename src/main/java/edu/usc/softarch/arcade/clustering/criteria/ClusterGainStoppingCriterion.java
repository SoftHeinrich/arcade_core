package edu.usc.softarch.arcade.clustering.criteria;

import edu.usc.softarch.arcade.clustering.Architecture;
import edu.usc.softarch.arcade.clustering.techniques.ClusteringAlgoRunner;

public class ClusterGainStoppingCriterion
		extends StoppingCriterion {
	public boolean notReadyToStop(Architecture arch) {
		return arch.size() != 1
			&& arch.size() != ClusteringAlgoRunner.numClustersAtMaxClusterGain;
	}
}
