import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores aggregated timing measurements for one algorithm.
 */
public class PerformanceData {

    private final String algorithmName;
    private final String inputDescription;
    private final List<Row> rows;

    public PerformanceData(
            String algorithmName,
            String inputDescription) {

        this.algorithmName = algorithmName;
        this.inputDescription = inputDescription;
        this.rows = new ArrayList<>();
    }

    public void add(Row row) {
        rows.add(row);
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public String getInputDescription() {
        return inputDescription;
    }

    public List<Row> getRows() {
        return Collections.unmodifiableList(rows);
    }

    /**
     * One aggregated measurement for one input size.
     */
    public static class Row {

        private final int size;
        private final int trials;

        private final double meanNs;
        private final double medianNs;
        private final double standardDeviationNs;

        private final long minNs;
        private final long maxNs;

        public Row(
                int size,
                int trials,
                double meanNs,
                double medianNs,
                double standardDeviationNs,
                long minNs,
                long maxNs) {

            this.size = size;
            this.trials = trials;
            this.meanNs = meanNs;
            this.medianNs = medianNs;
            this.standardDeviationNs = standardDeviationNs;
            this.minNs = minNs;
            this.maxNs = maxNs;
        }

        public int getSize() {
            return size;
        }

        public int getTrials() {
            return trials;
        }

        public double getMeanNs() {
            return meanNs;
        }

        public double getMedianNs() {
            return medianNs;
        }

        public double getStandardDeviationNs() {
            return standardDeviationNs;
        }

        public long getMinNs() {
            return minNs;
        }

        public long getMaxNs() {
            return maxNs;
        }

        public double getMeanMs() {
            return meanNs / 1_000_000.0;
        }

        public double getMedianMs() {
            return medianNs / 1_000_000.0;
        }

        public double getStandardDeviationMs() {
            return standardDeviationNs / 1_000_000.0;
        }

        public double getMinMs() {
            return minNs / 1_000_000.0;
        }

        public double getMaxMs() {
            return maxNs / 1_000_000.0;
        }
    }
}