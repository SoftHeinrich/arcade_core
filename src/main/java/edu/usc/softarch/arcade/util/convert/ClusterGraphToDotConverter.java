package edu.usc.softarch.arcade.util.convert;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.usc.softarch.arcade.clustering.ClusterUtil;
import edu.usc.softarch.arcade.facts.driver.RsfReader;

public class ClusterGraphToDotConverter {
	public static void main(String[] args) {
		String depsFilename = args[0];
		String clustersFilename = args[1];
		String dotFilename = args[2];

		RsfReader.loadRsfDataFromFile(depsFilename);
		List<List<String>> depFacts = RsfReader.unfilteredFaCtS;
		RsfReader.loadRsfDataFromFile(clustersFilename);
		List<List<String>> clusterFacts = RsfReader.unfilteredFaCtS;
		
		Map<String,Set<String>> clusterMap = ClusterUtil.buildClusterMap(clusterFacts);
		
		Set<List<String>> edges = ClusterUtil.buildClusterEdges(clusterMap, depFacts);
		
		try (FileWriter out = new FileWriter(dotFilename)) {
			out.write("digraph G {\n");
			
			for (String clusterName : clusterMap.keySet()) {
				out.write("\t\"" + clusterName + "\"" + ";\n");
			}
			
			for (List<String> edge : edges) {
				String source = edge.get(0);
				String target = edge.get(1);
				out.write("\t\"" + source + "\" -> \"" + target + "\";\n"); 
			}
			
			out.write("}\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}