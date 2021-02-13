package edu.usc.softarch.arcade.antipattern.detection;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import mojo.MoJoCalculator;

import org.apache.log4j.PropertyConfigurator;

import edu.usc.softarch.arcade.config.Config;
import edu.usc.softarch.arcade.topics.DocTopics;
import edu.usc.softarch.arcade.topics.TopicModelExtractionMethod;
import edu.usc.softarch.arcade.util.FileUtil;

public class BatchSmellDetectionRunner {

	public static void main(String[] args) {
		String gtRsfsDir = args[1];
		String docTopicsFile = args[2];
		String selectedLang = args[3];
		String depsRsfFilename = args[4];
		String techniquesDir = args[5];
		String groundTruthFilename = args[6];
		// obtain rsf files in output directory
		File gtRsfsDirFile = new File(gtRsfsDir);
		File[] newGtFiles = gtRsfsDirFile.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".rsf");
			}
		});

		PropertyConfigurator.configure("cfg" + File.separator + "extractor_logging.cfg");

		Config.setMalletDocTopicsFilename(docTopicsFile);
		if (selectedLang.equals("c")) {
			Config.setSelectedLanguage(Config.Language.c);
		}
		else if (selectedLang.equals("java")) {
			Config.setSelectedLanguage(Config.Language.java);
		}
		Config.setDepsRsfFilename(depsRsfFilename);
		try {
			String mojoFmMappingFilename = "mojofm_mapping.csv";
			PrintWriter writer = new PrintWriter(techniquesDir
					+ File.separatorChar + mojoFmMappingFilename, "UTF-8");
			for (File gtRsfFile : newGtFiles) {
				Config.setSmellClustersFile(gtRsfFile.getAbsolutePath()); 
				String prefix = FileUtil.extractFilenamePrefix(gtRsfFile
						.getName());
				String detectedSmellsFilename = techniquesDir + prefix
						+ "_smells.ser";

				ArchSmellDetector asd = new ArchSmellDetector(depsRsfFilename, 
					gtRsfFile.getAbsolutePath(), detectedSmellsFilename, selectedLang,
					TopicModelExtractionMethod.VAR_MALLET_FILE,
					new DocTopics(docTopicsFile));
				asd.runAllDetectionAlgs();

				MoJoCalculator mojoCalc = new MoJoCalculator(gtRsfFile.getAbsolutePath(),
						groundTruthFilename, null);
				double mojoFmValue = mojoCalc.mojofm();
				System.out.println(mojoFmValue);

				writer.println(detectedSmellsFilename + "," + mojoFmValue);

			}
			writer.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}