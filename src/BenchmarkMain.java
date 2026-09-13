import java.io.IOException;
import java.util.List;

public class BenchmarkMain {

    /*
     * Five warm-up iterations at every size provide repeated untimed executions so that JVM/JIT startup effects are less likely to dominate the recorded measurements.
     *
     * Twelve timed trials provide enough repeated observations to calculate a median and standard deviation without making the quadratic experiments too long.
     */
    private static final int WARMUP_ITERATIONS = 5;
    private static final int TIMED_TRIALS = 12;

    private static final int[] QUADRATIC_SIZES = {
            500,
            1000,
            2000,
            4000,
            6000,
            8000
    };

    private static final int[] N_LOG_N_SIZES = {
            10_000,
            20_000,
            40_000,
            80_000,
            160_000,
            320_000,
            640_000
    };

    public static void main(String[] args)
            throws IOException {

        System.out.println(
                "Algorithm Performance Laboratory");

        System.out.println(
                "Warm-up iterations per size: "
                + WARMUP_ITERATIONS);

        System.out.println(
                "Timed trials per size: "
                + TIMED_TRIALS);

        /*
         * Use separate generators with the same seed so every algorithm sees reproducible random data.
         */
        runExperiment(
                new SelectionSort(),
                new RandomIntArrayGenerator(42L),
                QUADRATIC_SIZES
        );

        runExperiment(
                new InsertionSort(),
                new RandomIntArrayGenerator(42L),
                QUADRATIC_SIZES
        );

        runExperiment(
                new MergeSort(),
                new RandomIntArrayGenerator(42L),
                N_LOG_N_SIZES
        );

        runExperiment(
                new ArraysSortWrapper(),
                new RandomIntArrayGenerator(42L),
                N_LOG_N_SIZES
        );
    }

    private static <T> void runExperiment(
            Algorithm<T> algorithm,
            InputGenerator<T> generator,
            int[] sizes) throws IOException {

        Measurement<T> measurement =
                new Measurement<>(
                        WARMUP_ITERATIONS,
                        TIMED_TRIALS
                );

        Experiment<T> experiment =
                new Experiment<>(
                        algorithm,
                        generator,
                        measurement
                );

        System.out.println();
        System.out.println(
                "Running " + algorithm.getName() + "...");

        PerformanceData data =
                experiment.run(sizes);

        List<Analysis.Fit> fits =
                Analysis.fitAll(data);

        Report.printSummary(data, fits);

        Report.writeCsv(
                data,
                fits,
                "results"
        );
    }
}