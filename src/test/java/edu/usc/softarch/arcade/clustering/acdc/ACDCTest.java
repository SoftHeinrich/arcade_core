package edu.usc.softarch.arcade.clustering.acdc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import edu.usc.softarch.arcade.BaseTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import edu.usc.softarch.arcade.util.FileUtil;
import edu.usc.softarch.arcade.RsfCompare;

public class ACDCTest extends BaseTest {
	private final String resourcesDir = resourcesBase + fs + "ACDC";
	private final String factsDir = resourcesBase + fs + "Facts";
	private final String outputDirPath = outputBase + fs + "ACDCTest";

	/**
	 * Clusters system given dependencies rsf file.
	 *
	 * @param version Name and version of subject system.
	 */
	@ParameterizedTest
	@CsvSource({
		"httpd-2.3.8",

		"httpd-2.4.26",

		"struts-2.3.30",

		"struts-2.5.2"

	})
	public void mainTest(String version) {
		String deps = factsDir + fs + version + "_deps.rsf"; //Input for arch recovery
		String clusters = outputDirPath + fs + version + "_mainTestResult.rsf"; //output file
		String oracle = resourcesDir + fs + version + "_acdc_clustered.rsf"; //dont know where does it come from

		// Run ACDC
		assertDoesNotThrow(() -> ACDC.run(deps, clusters));
		String result = assertDoesNotThrow(
			() -> FileUtil.readFile(clusters, StandardCharsets.UTF_8));

		// ------------------------- Generate Oracles ------------------------------

		if (generateOracles) {
			assertDoesNotThrow(() -> {
				Path resultPath = Paths.get(clusters);
				Path oraclePath = Paths.get(oracle);
				Files.copy(resultPath, oraclePath, StandardCopyOption.REPLACE_EXISTING);
			});
		}

		// ------------------------- Generate Oracles ------------------------------
		//TODO how is oracle generated?
		// Load oracle
		String oracleResult = assertDoesNotThrow(
			() -> FileUtil.readFile(oracle, StandardCharsets.UTF_8));

		// Use RsfCompare.compareTo to compare file contents
		RsfCompare resultRsf = new RsfCompare(result);
		RsfCompare oracleRsf = new RsfCompare(oracleResult);
		assertEquals(oracleRsf, resultRsf);
	}
}
