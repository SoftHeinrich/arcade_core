package edu.usc.softarch.arcade.clustering.acdc;

import edu.usc.softarch.arcade.clustering.acdc.data.Node;
import edu.usc.softarch.arcade.clustering.acdc.patterns.BodyHeader;
import edu.usc.softarch.arcade.clustering.acdc.patterns.ClusterLast;
import edu.usc.softarch.arcade.clustering.acdc.patterns.DownInducer;
import edu.usc.softarch.arcade.clustering.acdc.patterns.OrphanAdoption;
import edu.usc.softarch.arcade.clustering.acdc.patterns.Pattern;
import edu.usc.softarch.arcade.clustering.acdc.patterns.SubGraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This application facilitates the task of recovering the structure
 * of large and complex software systems in an automatic way by clustering.
 * 
 * The data structure used to represent the components of the software system
 * is a tree, which is clustered incrementally by using several patterns. 
 * 
 * Assumptions made:
 * ACDC will cluster the children of the root in the input.
 * Once an object has been clustered, it is never removed from its cluster
 * (but it might get further clustered within its cluster)
 */
public class ACDC {
	//region PUBLIC INTERFACE
	public static void main(String[] args) throws IOException {
		String inputName = args[0];
		String outputName = args[1];

		run(inputName, outputName);
	}

	public static void run(String inputName, String outputName)
			throws IOException {
		int maxClusterSize = 20; //used by SubGraph pattern

		//Seems doing initialization
		// Create a tree with a dummy root
		Node dummy = new Node("ROOT", "Dummy");
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(dummy);
		dummy.setTreeNode(root);
		Pattern inducer = new DownInducer(root);

		// Populate the tree from the input file, initiall 2 layer
 		TAInput.readInput(inputName, root); // running slow here

		Collection<Pattern> vpatterns = new ArrayList<>(); //TODO what are v patterns? maybe a wrapper for 3 patterns?
		vpatterns.add(new BodyHeader(root)); //paper STEP 2
		vpatterns.add(new SubGraph(root,maxClusterSize)); //TODO STEP 3-5?, why dont decouple them?
		vpatterns.add(new OrphanAdoption(root)); //TODO what is OrphanAdoption?

		// Induce all edges
		Collection<Node> allNodes = Pattern.allNodes(root);
		Pattern.induceEdges(allNodes);

		// Execute the patterns
 		for (Pattern p : vpatterns) //TODO what are 3 patterns?
			p.execute();

		// Take care of any objects that were not clustered
		Pattern c = new ClusterLast(root); //TODO why still any object not clustered? they should be adopted?
		c.execute();

		// Create output file
		inducer.execute();
		RSFOutput.writeOutput(outputName, root);
	}
	//endregion
}
