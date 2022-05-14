package edu.usc.softarch.arcade.metrics;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.usc.softarch.arcade.BaseTest;
import mojo.MoJoCalculator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import edu.usc.softarch.arcade.util.FileUtil;

public class MoJoEvolutionAnalyzerTest extends BaseTest {
	//region ATTRIBUTES
	private final String resourcesDir = resourcesBase + fs + "MoJoFM";
	private final String oraclesDir = resourcesDir + fs + "oracles";

	private static final Map<String, MoJoCalculator> mojoCalcs = new HashMap<>();
	//endregion

	//region TESTS
	/**
	 * Tests MoJoFM's calculation against a given system and recovery technique.
	 *
	 * @param systemName Name of the system being tested with.
	 * @param recoveryTechnique Name of the recovery technique being tested with.
	 */
	@ParameterizedTest
	@CsvSource({
		// Struts2 (acdc)
		"Struts2,"
			+ "acdc",

		// httpd (acdc)
		"httpd,"
			+ "acdc",

		// Struts2 (arc)
		"Struts2,"
			+ "arc",

		// httpd (arc)
		"httpd,"
			+ "arc"
	})
	public void mojoFmTest(String systemName, String recoveryTechnique) {
		String clustersDir = resourcesDir + fs + systemName
			+ fs + recoveryTechnique;
		String oraclePath = oraclesDir + fs + systemName + "_"
			+ recoveryTechnique + ".txt";

		Map<String, Double> mojoMap =
			assertDoesNotThrow(() -> calcMojoFM(clustersDir),
				"cluster files directory does not exist");
		Map<String, Double> oracleMojoMap =
			assertDoesNotThrow(() -> readOracle(oraclePath),
				"failed to read in oracle metrics file");

		// Compare mojoFmValues to oracle
		for (String filePair : mojoMap.keySet()) {
			assertEquals(oracleMojoMap.get(filePair), mojoMap.get(filePair),
				"mojoFmValue from comparison between " + filePair
					+ " does not match oracle");
		}
	}
	//endregion

	//region AUXILIARY
	/**
	 * Calculates the test results for a given system.
	 *
	 * @param clustersDir The path to the system's _clusters.rsf directory.
	 * @return A map of version pairs to MoJoFM values.
	 */
  private Map<String, Double> calcMojoFM(String clustersDir)
			throws FileNotFoundException {
    // Create map to MoJoFmValues and associated cluster files
		Map<String, Double> mojoFmMap = new HashMap<>();

    // Get the list of _clusters.rsf files for input.
    List<File> clusterFiles = getFileNames(clustersDir);

    File prevFile = null;

		// For each adjacent pair of files, calculate their MoJoFM values and
		// mark that pair of files as having been analyzed.
		for (File currFile : clusterFiles) {
			if (prevFile != null && currFile != null) {
				MoJoCalculator mojoCalc = getMojoCalc(currFile.getAbsolutePath(),
					prevFile.getAbsolutePath(), null);
				double mojoFmValue = mojoCalc.mojofm();
				mojoFmMap.put(currFile.getName() + " " + prevFile.getName(), mojoFmValue);
			}
			prevFile = currFile;
    }

    return mojoFmMap;
  }

	/**
	 * Reads in the test oracles for a given system.
	 *
	 * @param oraclePath The path to the system's oracle directory.
	 * @return A map of version pairs to MoJoFM oracle values.
	 */
  private Map<String, Double> readOracle(String oraclePath)
			throws IOException {
    // Create map to MoJoFMValues and associated cluster files
		Map<String, Double> oracleMojoMap = new HashMap<>();

    // Read in oracle file
    List<List<String>> records = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(oraclePath))) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				String[] values = line.split(",");
				records.add(Arrays.asList(values));
			}
		}

    // records.get(0) contains the mojoFmValues
    for (int i = 1; i < records.get(0).size(); i += 3)
      oracleMojoMap.put(records.get(0).get(i) + " "
				+ records.get(0).get(i + 1),
				Double.parseDouble(records.get(0).get(i + 2)));

    return oracleMojoMap;
  }

	private List<File> getFileNames(String dirPath) throws FileNotFoundException {
		List<File> clusterFiles = FileUtil.getFileListing(
			new File(FileUtil.tildeExpandPath(dirPath)));

		// FileUtil.sortFileListByVersion sorts the list by version
		return FileUtil.sortFileListByVersion(clusterFiles);
	}

	private MoJoCalculator getMojoCalc(String source, String target,
			String relations) {
		String mojoCalcName = source + target + relations;
		MoJoCalculator mojoCalc = mojoCalcs.get(mojoCalcName);
		if (mojoCalc != null) return mojoCalc;

		mojoCalc = new MoJoCalculator(source, target, relations);
		mojoCalcs.put(mojoCalcName, mojoCalc);
		return mojoCalc;
	}
	//endregion
}
