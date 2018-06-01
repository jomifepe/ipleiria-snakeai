package snake.snakeAI.nn;

import snake.*;
import snake.snakeAI.nn.utils.ActivationFunction;

import java.awt.Color;

public abstract class SnakeAIAgent extends SnakeAgent {
    final protected int inputLayerSize;
    final protected int hiddenLayerSize;
    final protected int outputLayerSize;
    final protected ActivationFunction activationFunction;

    /**
     * Network inputs array.
     */
    final protected int[] inputs;
    /**
     * Hiddden layer weights.
     */
    final protected double[][] w1;
    /**
     * Output layer weights.
     */
    final protected double[][] w2;
    /**
     * Hidden layer activation values.
     */
    final protected double[] hiddenLayerOutput;
    /**
     * Output layer activation values.
     */
    final protected double[] output;

    public SnakeAIAgent(Cell cell, int inputLayerSize, int hiddenLayerSize, int outputLayerSize,
                        ActivationFunction activationFunction, Color color) {
        super(cell, color.darker(), color);
        this.inputLayerSize = inputLayerSize;
        this.hiddenLayerSize = hiddenLayerSize;
        this.outputLayerSize = outputLayerSize;
        this.activationFunction = activationFunction;
        inputs = new int[inputLayerSize];
        inputs[inputs.length - 1] = -1; //bias entry
        w1 = new double[inputLayerSize][hiddenLayerSize]; // the bias entry for the hidden layer neurons is already counted in inputLayerSize variable
        w2 = new double[hiddenLayerSize + 1][outputLayerSize]; // + 1 due to the bias entry for the output neurons
        hiddenLayerOutput = new double[hiddenLayerSize + 1];
        hiddenLayerOutput[hiddenLayerSize] = -1; // the bias entry for the output neurons
        output = new double[outputLayerSize];
    }


    protected void setInputs(int[] inputs) {
        for (int i = 0; i < this.inputs.length; i++)
            this.inputs[i] = inputs[i];
    }

    /**
     * Initializes the network's weights
     *
     * @param weights vector of weights comming from the individual.
     */
    public void setWeights(double[] weights) {
        int w = 0;
        /* initializing the weights of the connections
        between the inputs and the hidden layer */
        for (int i = 0; i < w1.length; i++) {
            for (int j = 0; j < w1[0].length; j++) {
                w1[i][j] = weights[w++];
            }
        }
        /* initializing the weights of the connections
        between the hidden layer and the output layer */
        for (int i = 0; i < w2.length; i++) {
            for (int j = 0; j < w2[0].length; j++) {
                w2[i][j] = weights[w++];
            }
        }
    }

    /**
     * Computes the output of the network for the inputs saved in the class
     * vector "inputs".
     *
     */
    protected void forwardPropagation() {
        float sum = 0;

        // computing the activation values of the hidden layer neurons
        for (int i = 0; i < hiddenLayerSize; i++, sum = 0) {
            for (int j = 0; j < inputLayerSize; j++)
                sum += inputs [j] * w1[j][i];
            hiddenLayerOutput[i] = activationFunction.compute(sum);
        }

        // computing the activation values of the output layer neurons
        for (int i = 0; i < outputLayerSize; i++, sum = 0) {
            for (int j = 0; j < hiddenLayerSize + 1; j++)
                sum += hiddenLayerOutput[j] * w2[j][i];
            output[i] = activationFunction.compute(sum);
        }
    }

    protected final int NEURON_NORTH = 0;
    protected final int NEURON_SOUTH = 1;
    protected final int NEURON_EAST = 2;
    protected final int NEURON_WEST = 3;
    protected Action previous = null;

    @Override
    protected Action decide(Perception perception) {
        int[] vInputs = buildInputsFromPerception(perception);

        if (vInputs.length != inputLayerSize)
            throw new IllegalArgumentException("Not enough inputs to fill the input layer (layer size: " + inputLayerSize + ")");

        setInputs(vInputs);

        forwardPropagation();
//        printOutputs();

        int decision = 0;
        double highestValue = output[0];
        for (int i = 0; i < outputLayerSize; i++) {
            if (output[i] > highestValue)
                decision = i;
        }

        switch (decision) {
            case NEURON_NORTH: return (previous = Action.NORTH);
            case NEURON_SOUTH: return (previous = Action.SOUTH);
            case NEURON_EAST: return (previous = Action.EAST);
            case NEURON_WEST: return (previous = Action.WEST);
        }

        return null;
    }

    private void printOutputs() {
        System.out.print("[ ");
        for (double out : output)
            System.out.print(String.format("%.2f", out) + " ");
        System.out.println("]");
    }

    protected abstract int[] buildInputsFromPerception(Perception perception);
}
