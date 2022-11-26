package edu.usc.softarch.arcade.clustering;

import edu.usc.softarch.arcade.clustering.data.Architecture;
import edu.usc.softarch.arcade.clustering.data.FeatureVectors;
import edu.usc.softarch.arcade.clustering.simmeasures.SimMeasure;
import edu.usc.softarch.arcade.topics.exceptions.UnmatchingDocTopicItemsException;

import java.io.IOException;

public class ArchitectureMock extends Architecture {
	//region ATTRIBUTES
	public Architecture initialArchitecture;
	public Architecture architectureWithDocTopics;
	//endregion

	//region CONSTRUCTORS
	ArchitectureMock(String projectName, String projectVersion,
			String projectPath, SimMeasure.SimMeasureType simMeasure,
			String depsPath, String language, String artifactsDir,
			String packagePrefix)
			throws UnmatchingDocTopicItemsException, IOException {
		super(projectName, projectVersion, projectPath, simMeasure, depsPath,
			language, artifactsDir, packagePrefix);
	}

	@Override
	protected void initializeClusters(FeatureVectors vectors, String language,
			String packagePrefix) {
		super.initializeClusters(vectors, language, packagePrefix);
		this.initialArchitecture = new Architecture(this);
	}

	@Override
	protected void initializeClusterDocTopics()
			throws UnmatchingDocTopicItemsException {
		super.initializeClusterDocTopics();
		this.architectureWithDocTopics = new Architecture(this);
	}
	//endregion
}
