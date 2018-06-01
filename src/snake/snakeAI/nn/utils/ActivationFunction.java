package snake.snakeAI.nn.utils;

public enum ActivationFunction {
    SIGMOID {
        /**
         * Sigmoid activation function
         * @param weightedSum
         * @return computed value
         */
        @Override
        public double compute(double weightedSum) {
            return 1 / (1 + Math.exp(-weightedSum));
        }
    },
    TAHN {
        /**
         * Hyperbolic Tangent activation function
         * @param weightedSum
         * @return computed value
         */
        @Override
        public double compute(double weightedSum) {
            return Math.tanh(weightedSum);
        }
    };

    public abstract double compute(double weightedSum);

    @Override
    public String toString() {
        String s = super.toString().toLowerCase();
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
