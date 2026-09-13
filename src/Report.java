import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

/**
 * Produces console summaries and CSV reports.
 */
public class Report {

    /**
     * Writes performance data and model predictions to a CSV file.
     */
    public static void writeCsv(
            PerformanceData data,
            List<Analysis.Fit> fits,
            String directory) throws IOException {

        Path outputDirectory = Paths.get(directory);

        Files.createDirectories(outputDirectory);

        String safeName = data.getAlgorithmName()
                .replaceAll("[^A-Za-z0-9._-]+", "_");

        Path outputFile =
                outputDirectory.resolve(safeName + ".csv");

        Analysis.Fit linear =
                findFit(fits, Analysis.Model.LINEAR);

        Analysis.Fit nLogN =
                findFit(fits, Analysis.Model.N_LOG_N);

        Analysis.Fit quadratic =
                findFit(fits, Analysis.Model.QUADRATIC);

        try (BufferedWriter writer =
                     Files.newBufferedWriter(outputFile)) {

            writer.write(
                    "size,"
                    + "trials,"
                    + "mean_ms,"
                    + "median_ms,"
                    + "stddev_ms,"
                    + "min_ms,"
                    + "max_ms,"
                    + "fit_n_ms,"
                    + "fit_n_log_n_ms,"
                    + "fit_n_squared_ms"
            );

            writer.newLine();

            for (PerformanceData.Row row : data.getRows()) {

                String line = String.format(
                        Locale.US,
                        "%d,%d,%.9f,%.9f,%.9f,%.9f,%.9f,"
                        + "%.9f,%.9f,%.9f",
                        row.getSize(),
                        row.getTrials(),
                        row.getMeanMs(),
                        row.getMedianMs(),
                        row.getStandardDeviationMs(),
                        row.getMinMs(),
                        row.getMaxMs(),
                        linear.predictMs(row.getSize()),
                        nLogN.predictMs(row.getSize()),
                        quadratic.predictMs(row.getSize())
                );

                writer.write(line);
                writer.newLine();
            }
        }

        System.out.println(
                "CSV written to: "
                + outputFile.toAbsolutePath());
    }

    /**
     * Prints the statistics and model comparison.
     */
    public static void printSummary(
            PerformanceData data,
            List<Analysis.Fit> fits) {

        System.out.println();
        System.out.println("========================================");
        System.out.println("Algorithm: "
                + data.getAlgorithmName());
        System.out.println("Input: "
                + data.getInputDescription());
        System.out.println("----------------------------------------");

        System.out.printf(
                "%10s %12s %12s %12s%n",
                "n",
                "mean(ms)",
                "median(ms)",
                "stddev(ms)"
        );

        for (PerformanceData.Row row : data.getRows()) {

            System.out.printf(
                    Locale.US,
                    "%10d %12.6f %12.6f %12.6f%n",
                    row.getSize(),
                    row.getMeanMs(),
                    row.getMedianMs(),
                    row.getStandardDeviationMs()
            );
        }

        System.out.println();
        System.out.println("Growth-model fits:");
        System.out.printf(
                "%-12s %14s %14s%n",
                "Model",
                "MAPE (%)",
                "R^2"
        );

        for (Analysis.Fit fit : fits) {

            System.out.printf(
                    Locale.US,
                    "%-12s %14.4f %14.6f%n",
                    fit.getModel().getDescription(),
                    fit.getMape(),
                    fit.getRSquared()
            );
        }

        Analysis.Fit best = Analysis.bestFit(data);

        System.out.println();
        System.out.println(
                "Best model by MAPE: "
                + best.getModel().getDescription()
        );
    }

    private static Analysis.Fit findFit(
            List<Analysis.Fit> fits,
            Analysis.Model model) {

        for (Analysis.Fit fit : fits) {
            if (fit.getModel() == model) {
                return fit;
            }
        }

        throw new IllegalArgumentException(
                "Missing fit for model: " + model);
    }
}