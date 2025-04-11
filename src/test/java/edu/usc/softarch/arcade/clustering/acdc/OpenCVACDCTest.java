package edu.usc.softarch.arcade.clustering.acdc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.usc.softarch.arcade.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OpenCVACDCTest extends BaseTest {
    private final String resourcesDir = resourcesBase + fs + "ACDC";
    private final String opencvDir = resourcesBase + fs + "opencv";
    private final String outputDirPath = outputBase + fs + "OpenCVACDCTest";

    @BeforeEach
    public void setup() {
        // Create output directory if it doesn't exist
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    /**
     * Automatically processes all dependency files in the opencv directory
     * and runs ACDC on each file to recover architecture.
     */
    @Test
    public void analyzeAllOpenCVVersions() throws IOException {
        // Find all dependency files in the opencv directory
        List<Path> dependencyFiles;
        try (Stream<Path> paths = Files.walk(Path.of(opencvDir))) {
            dependencyFiles = paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith("_deps.rsf"))
                .collect(Collectors.toList());
        }

        System.out.println("Found " + dependencyFiles.size() + " OpenCV dependency files");

        // Process each dependency file
        for (Path dependencyFile : dependencyFiles) {
            String filename = dependencyFile.getFileName().toString();
            String baseName = filename.substring(0, filename.lastIndexOf("_deps.rsf"));

            System.out.println("Processing " + baseName);

            String inputFile = dependencyFile.toString();
            String outputFile = outputDirPath + fs + baseName + "_acdc_clustered.rsf";

            // Run ACDC on the dependency file
            assertDoesNotThrow(() -> {
                System.out.println("Running ACDC on " + inputFile);
                System.out.println("Output will be written to " + outputFile);
                ACDC.run(inputFile, outputFile);
                System.out.println("ACDC completed for " + baseName);
            });
        }

        System.out.println("Architecture recovery completed for all OpenCV versions");
    }
}