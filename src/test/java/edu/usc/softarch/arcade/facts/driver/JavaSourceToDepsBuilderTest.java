package edu.usc.softarch.arcade.facts.driver;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import edu.usc.softarch.arcade.util.FileUtil;
import edu.usc.softarch.arcade.util.RsfCompare;

public class JavaSourceToDepsBuilderTest {
	@BeforeEach
	public void setUp(){
		// Create ./target/test_results/JavaSourceToDepsBuilderTest/ if it does not already exist
		char fs = File.separatorChar;
		String outputPath = "." + fs + "target" + fs + "test_results" + fs + "JavaSourceToDepsBuilderTest";
		(new File(outputPath)).mkdirs();
	}

	@ParameterizedTest
	@CsvSource({
		// Test parameters: 
		// [binaries loc], 
		// [directory in which to place result rsf], 
		// [oracle rsf file location]

		// struts2 (2.3.30)
		".///src///test///resources///JavaSourceToDepsBuilderTest_resources///binaries///struts-2.3.30///lib_struts,"
		+ ".///target///test_results///JavaSourceToDepsBuilderTest///struts-2.3.30_buildTestResult.rsf,"
		+ ".///src///test///resources///JavaSourceToDepsBuilderTest_resources///struts-2.3.30_deps.rsf",
		// // struts2 (2.5.2)
		// ".///src///test///resources///JavaSourceToDepsBuilderTest_resources///binaries///struts-2.5.2///lib_struts,"
		// + ".///target///test_results///JavaSourceToDepsBuilderTest///struts-2.5.2_buildTestResult.rsf,"
		// + ".///src///test///resources///JavaSourceToDepsBuilderTest_resources///struts-2.5.2_deps.rsf",
	})
	public void buildTest(String classesDirPath, String depsRsfFilename, String oraclePath){
		/** Builds the dependencies RSF file for Java system **/
		// Format the paths properly
		String classes = classesDirPath.replace("///", File.separator);
		String deps = depsRsfFilename.replace("///", File.separator);
		String oracle = oraclePath.replace("///", File.separator);

		// Run JavaSourceToDepsBuilder.build()
		assertDoesNotThrow(() -> (new JavaSourceToDepsBuilder()).build(classes, deps));
		String result = assertDoesNotThrow(() ->
			{ return FileUtil.readFile(deps, StandardCharsets.UTF_8); });

		// Load oracle
		String oracleResult = assertDoesNotThrow(() ->
			{ return FileUtil.readFile(oracle, StandardCharsets.UTF_8); });

		// Use RsfCompare.compareTo to compare file contents
				// returns 0 if files have the same contents
		RsfCompare resultRsf = new RsfCompare(result);
		RsfCompare oracleRsf = new RsfCompare(oracleResult);
		assertTrue(oracleRsf.equals(resultRsf));
	}
}
