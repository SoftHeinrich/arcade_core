package edu.usc.softarch.arcade.clustering.techniques;

import edu.usc.softarch.arcade.clustering.Architecture;
import edu.usc.softarch.arcade.clustering.Cluster;
import edu.usc.softarch.arcade.clustering.SimData;
import edu.usc.softarch.arcade.clustering.SimilarityMatrix;
import edu.usc.softarch.arcade.clustering.criteria.SerializationCriterion;
import edu.usc.softarch.arcade.clustering.criteria.StoppingCriterion;
import edu.usc.softarch.arcade.topics.DistributionSizeMismatchException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class WcaRunner extends ClusteringAlgoRunner {
	//region INTERFACE
	public static Architecture run(Architecture arch,
			SerializationCriterion serialCrit, StoppingCriterion stopCrit,
			String language, String stoppingCriterionName,
			SimilarityMatrix.SimMeasure simMeasure)
			throws IOException, DistributionSizeMismatchException {
		// Create the runner object
		ClusteringAlgoRunner runner = new WcaRunner(language,
			serialCrit, arch);
		// Compute the clustering algorithm and return the resulting architecture
		return runner.computeArchitecture(stopCrit, stoppingCriterionName,
			simMeasure);
	}
	//endregion

	//region CONSTRUCTORS
	public WcaRunner(String language,
			SerializationCriterion serializationCriterion, Architecture arch) {
		super(language, serializationCriterion, arch);
	}
	//endregion

	@Override
	public Architecture computeArchitecture(
			StoppingCriterion stopCriterion, String stoppingCriterion,
			SimilarityMatrix.SimMeasure simMeasure)
			throws DistributionSizeMismatchException, FileNotFoundException {
		SimilarityMatrix simMatrix = new SimilarityMatrix(simMeasure, this.architecture);

		while (stopCriterion.notReadyToStop(super.architecture)) {
			if (stoppingCriterion.equalsIgnoreCase("clustergain")) {
				double clusterGain = 0;
				clusterGain = super.architecture.computeStructuralClusterGain();
				checkAndUpdateClusterGain(clusterGain);
			}

			SimData data = identifyMostSimClusters(simMatrix);

			Cluster cluster = data.c1;
			Cluster otherCluster = data.c2;
			Cluster newCluster = new Cluster(cluster, otherCluster);

			updateFastClustersAndSimMatrixToReflectMergedCluster(data, newCluster, simMatrix);

			if (super.serializationCriterion != null
					&& super.serializationCriterion.shouldSerialize()) {
				super.architecture.writeToRsf();
			}
		}

		return super.architecture;
	}
}
