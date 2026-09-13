import java.util.Arrays;

/**
 * Measures an Algorithm using JVM warm-up and repeated timed trials.
 * @param <T> input type consumed by the algorithm
 */
public class Measurement<T> {

    private final int warmupIterations;
    private final int timedTrials;

    public Measurement(int warmupIterations, int timedTrials) {
        if (warmupIterations < 0) {
            throw new IllegalArgumentException(
                    "warmupIterations cannot be negative");
        }

        if (timedTrials <= 0) {
            throw new IllegalArgumentException(
                    "timedTrials must be positive");
        }

        this.warmupIterations = warmupIterations;
        this.timedTrials = timedTrials;
    }

    /**
     * Measures the algorithm at one requested input size.
     */
    public PerformanceData.Row measure(
            Algorithm<T> algorithm,
            InputGenerator<T> generator,
            int size) {


// Input generation intentionally occurs outside any timer.
        for (int i = 0; i < warmupIterations; i++) {
            T input = generator.generate(size);
            algorithm.execute(input);
        }

        long[] times = new long[timedTrials];

        // Timed trials.
        for (int trial = 0; trial < timedTrials; trial++) {

            // Fresh input for every trial.
            T input = generator.generate(size);

            // ONLY execute() is inside the timed region.
            long start = System.nanoTime();
            algorithm.execute(input);
            long end = System.nanoTime();

            times[trial] = end - start;
        }

        return calculateStatistics(size, times);
    }

    private PerformanceData.Row calculateStatistics(
            int size,
            long[] times) {

        double sum = 0.0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        for (long time : times) {
            sum += time;

            if (time < min) {
                min = time;
            }

            if (time > max) {
                max = time;
            }
        }

        double mean = sum / times.length;

        long[] sorted = times.clone();
        Arrays.sort(sorted);

        double median;

        if (sorted.length % 2 == 1) {
            median = sorted[sorted.length / 2];
        } else {
            int upper = sorted.length / 2;
            int lower = upper - 1;

            median = (sorted[lower] + sorted[upper]) / 2.0;
        }

        double variance = 0.0;

        if (times.length > 1) {
            for (long time : times) {
                double difference = time - mean;
                variance += difference * difference;
            }

// Sample standard deviation.
            variance /= (times.length - 1);
        }

        double standardDeviation = Math.sqrt(variance);

        return new PerformanceData.Row(
                size,
                times.length,
                mean,
                median,
                standardDeviation,
                min,
                max
        );
    }

    public int getWarmupIterations() {
        return warmupIterations;
    }

    public int getTimedTrials() {
        return timedTrials;
    }
}