package snake.snakeAI.nn;

import snake.*;

import java.awt.Color;
import java.util.Arrays;

public class SnakeAIAgent extends SnakeAgent {
    final private int inputLayerSize;
    final private int hiddenLayerSize;
    final private int outputLayerSize;

    /**
     * Network inputs array.
     */
    final private int[] inputs;
    /**
     * Hiddden layer weights.
     */
    final private double[][] w1;
    /**
     * Output layer weights.
     */
    final private double[][] w2;
    /**
     * Hidden layer activation values.
     */
    final private double[] hiddenLayerOutput;
    /**
     * Output layer activation values.
     */
    final private double[] output;

    public SnakeAIAgent(Cell cell, int inputLayerSize, int hiddenLayerSize, int outputLayerSize, Color color) {
        super(cell, color.darker(), color);
        this.inputLayerSize = inputLayerSize;
        this.hiddenLayerSize = hiddenLayerSize;
        this.outputLayerSize = outputLayerSize;
        inputs = new int[inputLayerSize];
        inputs[inputs.length - 1] = -1; //bias entry
        w1 = new double[inputLayerSize][hiddenLayerSize]; // the bias entry for the hidden layer neurons is already counted in inputLayerSize variable
        w2 = new double[hiddenLayerSize + 1][outputLayerSize]; // + 1 due to the bias entry for the output neurons
        hiddenLayerOutput = new double[hiddenLayerSize + 1];
        hiddenLayerOutput[hiddenLayerSize] = -1; // the bias entry for the output neurons
        output = new double[outputLayerSize];
    }


    public void setInputs(int[] inputs) {
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
    private void forwardPropagation() {
        float sum = 0;

        // computing the activation values of the hidden layer neurons
        for (int i = 0; i < hiddenLayerSize; i++, sum = 0) {
            for (int j = 0; j < inputLayerSize; j++)
                sum += inputs [j] * w1[j][i];
            hiddenLayerOutput[i] = sigmoide(sum);
        }

        // computing the activation values of the output layer neurons
        for (int i = 0; i < outputLayerSize; i++, sum = 0) {
            for (int j = 0; j < hiddenLayerSize + 1; j++)
                sum += hiddenLayerOutput[j] * w2[j][i];
            output[i] = sigmoide(sum);
        }
    }

    private double sigmoide(float somaPesada) {
        return 1 / (1 + Math.exp(-somaPesada));
    }

    private final int NEURON_NORTH = 0;
    private final int NEURON_SOUTH = 1;
    private final int NEURON_EAST = 2;
    private final int NEURON_WEST = 3;
    private Action prevDecision = null;

    @Override
    protected Action decide(Perception perception) {
        Cell n = perception.getN();
        Cell s = perception.getS();
        Cell e = perception.getE();
        Cell w = perception.getW();
        Cell food = environment.getFood();

//        setInputs(new int[] {
//                food.isToTheNorthOf(head) ? 1 : 0,
//                food.isToTheSouthOf(head) ? 1 : 0,
//                food.isToTheEastOf(head) ? 1 : 0,
//                food.isToTheWestOf(head) ? 1 : 0,
//                n != null && n.isFree() ? (n.hasFood() ? 1 : 0) : -1,
//                s != null && s.isFree() ? (s.hasFood() ? 1 : 0) : -1,
//                e != null && e.isFree() ? (e.hasFood() ? 1 : 0) : -1,
//                w != null && w.isFree() ? (w.hasFood() ? 1 : 0) : -1
//        });

        setInputs(new int[] {
                food.isToTheNorthOf(head) ? 1 : 0,
                food.isToTheSouthOf(head) ? 1 : 0,
                food.isToTheEastOf(head) ? 1 : 0,
                food.isToTheWestOf(head) ? 1 : 0,
                n != null && n.isFree() && (prevDecision != null && prevDecision.opposite() != Action.NORTH) ? (n.hasFood() ? 1 : 0) : -1,
                s != null && s.isFree() && (prevDecision != null && prevDecision.opposite() != Action.SOUTH) ? (s.hasFood() ? 1 : 0) : -1,
                e != null && e.isFree() && (prevDecision != null && prevDecision.opposite() != Action.EAST) ? (e.hasFood() ? 1 : 0) : -1,
                w != null && w.isFree() && (prevDecision != null && prevDecision.opposite() != Action.WEST) ? (w.hasFood() ? 1 : 0) : -1
        });

        forwardPropagation();
//        printOutputs();

        int decision = 0;
        double highestValue = output[0];
        for (int i = 0; i < outputLayerSize; i++) {
            if (output[i] > highestValue)
                decision = i;
        }

        switch (decision) {
            case NEURON_NORTH: return (prevDecision = Action.NORTH);
            case NEURON_SOUTH: return (prevDecision = Action.SOUTH);
            case NEURON_EAST: return (prevDecision = Action.EAST);
            case NEURON_WEST: return (prevDecision = Action.WEST);
        }

        return null;
    }

    private void printOutputs() {
        System.out.print("[ ");
        for (double out : output)
            System.out.print(String.format("%.2f", out) + " ");
        System.out.println("]");
    }
}
