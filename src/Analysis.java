import java.util.ArrayList;
import java.util.List;

/**
 * Fits theoretical growth models to measured performance data.
 */
public class Analysis {

    /**
     * Candidate theoretical growth functions.
     */
    public enum Model {

        LINEAR("n") {
            @Override
            public double value(int n) {
                return n;
            }
        },

        N_LOG_N("n log n") {
            @Override
            public double value(int n) {
                if (n <= 1) {
                    return n;
                }

                return n * Math.log(n);
            }
        },

        QUADRATIC("n^2") {
            @Override
            public double value(int n) {
                return (double) n * n;
            }
        };

        private final String description;

        Model(String description) {
            this.description = description;
        }

        public abstract double value(int n);

        public String getDescription() {
            return description;
        }
    }

    /**
     * Fits one candidate model to the measured data.
     *
     * c = average(measuredTime / f(n))
     */
    public static Fit fit(
            PerformanceData data,
            Model model) {

        List<PerformanceData.Row> rows = data.getRows();

        if (rows.isEmpty()) {
            throw new IllegalArgumentException(
                    "Performance data contains no measurements");
        }

        double scalingSum = 0.0;

        for (PerformanceData.Row row : rows) {
            double theoretical = model.value(row.getSize());

            scalingSum += row.getMedianNs() / theoretical;
        }

        double c = scalingSum / rows.size();

        /*
         * Calculate MAPE and R^2.
         */

        double observedMean = 0.0;

        for (PerformanceData.Row row : rows) {
            observedMean += row.getMedianNs();
        }

        observedMean /= rows.size();

        double absolutePercentageErrorSum = 0.0;
        int percentageErrorCount = 0;

        double sse = 0.0;
        double sst = 0.0;

        for (PerformanceData.Row row : rows) {

            double observed = row.getMedianNs();
            double predicted =
                    c * model.value(row.getSize());

            double residual = observed - predicted;

            sse += residual * residual;

            double centered = observed - observedMean;
            sst += centered * centered;

            if (observed != 0.0) {
                absolutePercentageErrorSum +=
                        Math.abs(residual / observed);

                percentageErrorCount++;
            }
        }

        double mape =
                percentageErrorCount == 0
                        ? Double.NaN
                        : 100.0
                        * absolutePercentageErrorSum
                        / percentageErrorCount;

        double rSquared =
                sst == 0.0
                        ? Double.NaN
                        : 1.0 - (sse / sst);

        return new Fit(
                model,
                c,
                mape,
                rSquared
        );
    }

    /**
     * Fits all candidate growth models.
     */
    public static List<Fit> fitAll(PerformanceData data) {

        List<Fit> fits = new ArrayList<>();

        for (Model model : Model.values()) {
            fits.add(fit(data, model));
        }

        return fits;
    }

    /**
     * Returns the fitted result having the smallest MAPE.
     */
    public static Fit bestFit(PerformanceData data) {

        List<Fit> fits = fitAll(data);

        Fit best = fits.get(0);

        for (Fit fit : fits) {
            if (fit.getMape() < best.getMape()) {
                best = fit;
            }
        }

        return best;
    }

    /**
     * Result of fitting one theoretical model.
     */
    public static class Fit {

        private final Model model;
        private final double scalingConstant;
        private final double mape;
        private final double rSquared;

        public Fit(
                Model model,
                double scalingConstant,
                double mape,
                double rSquared) {

            this.model = model;
            this.scalingConstant = scalingConstant;
            this.mape = mape;
            this.rSquared = rSquared;
        }

        public Model getModel() {
            return model;
        }

        public double getScalingConstant() {
            return scalingConstant;
        }

        public double getMape() {
            return mape;
        }

        public double getRSquared() {
            return rSquared;
        }

        public double predictNs(int size) {
            return scalingConstant * model.value(size);
        }

        public double predictMs(int size) {
            return predictNs(size) / 1_000_000.0;
        }
    }
}