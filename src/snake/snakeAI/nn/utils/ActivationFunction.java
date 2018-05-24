package snake.snakeAI.nn.utils;

public class ActivationFunction {
    /**
     * Sigmoid activation function
     * @param weightedSum
     * @return
     */
    public static double sigmoid(float weightedSum) {
        return 1 / (1 + Math.exp(-weightedSum));
    }

    /**
     * Hyperbolic Tangent activation function
     * @param weightedSum
     * @return
     */
    public static double tahn(float weightedSum) {
        return Math.tanh(weightedSum);
    }
}
