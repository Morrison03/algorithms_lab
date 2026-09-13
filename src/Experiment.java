/**
 * Runs an Algorithm across a range of requested input sizes.
 * @param <T> input type
 */
public class Experiment<T> {

    private final Algorithm<T> algorithm;
    private final InputGenerator<T> generator;
    private final Measurement<T> measurement;

    public Experiment(
            Algorithm<T> algorithm,
            InputGenerator<T> generator,
            Measurement<T> measurement) {

        this.algorithm = algorithm;
        this.generator = generator;
        this.measurement = measurement;
    }

    /**
     * Runs the experiment for all requested sizes.
     */
    public PerformanceData run(int[] sizes) {

        PerformanceData data = new PerformanceData(
                algorithm.getName(),
                generator.getDescription()
        );

        for (int size : sizes) {
            PerformanceData.Row row =
                    measurement.measure(algorithm, generator, size);

            data.add(row);
        }

        return data;
    }
}