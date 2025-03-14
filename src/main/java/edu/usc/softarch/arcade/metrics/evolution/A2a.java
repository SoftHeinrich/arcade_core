package edu.usc.softarch.arcade.metrics.evolution;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.usc.softarch.arcade.clustering.data.ReadOnlyArchitecture;
import edu.usc.softarch.arcade.metrics.RenameFixer;
import edu.usc.softarch.arcade.util.CentralTendency;
import edu.usc.softarch.arcade.util.FileUtil;
import edu.usc.softarch.arcade.util.McfpDriver;
import edu.usc.softarch.util.EnhancedSet;

/**
 * Calculates A2A.
 */
public class A2a {
	//region PUBLIC INTERFACE
	public static void main(String[] args) throws IOException {
		if (args.length > 1)
			System.out.println(run(args[0], args[1]));
		else
			runBatch(args[0], true);
	}

	public static double run(String sourceRsf, String targetRsf)
			throws IOException {
		return (new A2a(sourceRsf, targetRsf)).solve();
	}

	public static double run(File sourceRsf, File targetRsf) throws IOException {
		return (new A2a(sourceRsf, targetRsf)).solve();
	}

	public static double run(ReadOnlyArchitecture sourceRa,
			ReadOnlyArchitecture targetRa) {
		return (new A2a(sourceRa, targetRa)).solve();
	}

	public static CentralTendency runBatch(String path) throws IOException {
		return runBatch(path, true);
	}

	public static CentralTendency runBatch(String path, boolean verbose)
			throws IOException {
		List<File> clusterFiles = FileUtil.getFileListing(
			new File(FileUtil.tildeExpandPath(path)));
		clusterFiles = clusterFiles.stream()
			.filter(f -> f.getName().contains(".rsf")).collect(Collectors.toList());
		clusterFiles = FileUtil.sortFileListByVersion(clusterFiles);

		double[] sysEvoValues = new double[clusterFiles.size() - 1];

		for (int i = 1; i < clusterFiles.size(); i++) {
			sysEvoValues[i - 1] = run(clusterFiles.get(i - 1), clusterFiles.get(i));
			if (verbose)
				System.out.println("A2A from " + clusterFiles.get(i - 1).getName()
					+ " to " + clusterFiles.get(i).getName() + ": " + sysEvoValues[i - 1]);
		}

		return new CentralTendency(sysEvoValues);
	}
	//endregion

	//region ATTRIBUTES
	private double a2a;
	private final ReadOnlyArchitecture sourceClusters;
	private final ReadOnlyArchitecture targetClusters;
	//endregion

	//region CONSTRUCTORS
	public A2a(String sourceRsf, String targetRsf) throws IOException {
		this.a2a = -1;
		this.sourceClusters = ReadOnlyArchitecture.readFromRsf(sourceRsf);
		this.targetClusters = ReadOnlyArchitecture.readFromRsf(targetRsf);
		RenameFixer.fix(this.sourceClusters, this.targetClusters);
	}

	public A2a(File sourceRsf, File targetRsf) throws IOException {
		this.a2a = -1;
		this.sourceClusters = ReadOnlyArchitecture.readFromRsf(sourceRsf);
		this.targetClusters = ReadOnlyArchitecture.readFromRsf(targetRsf);
		RenameFixer.fix(this.sourceClusters, this.targetClusters);
	}

	public A2a(ReadOnlyArchitecture sourceRa, ReadOnlyArchitecture targetRa) {
		this.a2a = -1;
		this.sourceClusters = sourceRa;
		this.targetClusters = targetRa;
	}
	//endregion

	//region PROCESSING
	private int numerator() {
		// balance the number of clusters in the source and target architectures
		int numClusterDifference =
			Math.abs(this.sourceClusters.size() - this.targetClusters.size());

		// remove and add entities to the source and target architectures
		EnhancedSet<String> sourceEntities = sourceClusters.getEntities();
		EnhancedSet<String> targetEntities = targetClusters.getEntities();

		Set<String> addedEntities = targetEntities.difference(sourceEntities);
		Set<String> removedEntities = sourceEntities.difference(targetEntities);

		// MCFP is calculated only over the entities shared between both versions. TODO: what is MCFP?
		// Entities which were added or removed between versions are considered
		// separately by doubling their quantities (once for add/remove, once for
		// move).
		ReadOnlyArchitecture sourceTrimmed = //src arch - removed entities == entities remaining
			sourceClusters.difference(removedEntities);
		ReadOnlyArchitecture targetTrimmed =
			targetClusters.difference(addedEntities); // tgt arch - added entities == entities remaining from src

		McfpDriver mcfpDriver = new McfpDriver(sourceTrimmed, targetTrimmed); // doing the hungarian matching

		int numAddedEntities = addedEntities.size();
		int numRemovedEntities = removedEntities.size();
		// The cost is divided by two because MCFP counts each move twice, once for
		// the source and once for the target
		int numMovedEntities = mcfpDriver.getCost() / 2;

		return numClusterDifference + 2 * numAddedEntities
			+ 2 * numRemovedEntities + numMovedEntities; //TODO calculation might be wrong, as added and moved entities are counted twice
	}  //TODO need to check whether for the given example struts, the entities are the same

	private double denominator() {
		int numSourceClusters = this.sourceClusters.size();
		int numTargetClusters = this.targetClusters.size();
		int numSourceEntities = this.sourceClusters.countEntities();
		int numTargetEntities = this.targetClusters.countEntities();

		return numSourceClusters + 2.0 * numSourceEntities
			+ numTargetClusters + 2.0 * numTargetEntities;
	}

	public double solve() {
		if (this.a2a == -1) {
			int numerator_val = numerator();
			double denominator_val =  denominator();
			this.a2a = (1 - numerator_val / denominator_val) * 100;
		}
		return this.a2a;
	}
	//endregion
}
