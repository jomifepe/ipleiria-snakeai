package snake;

import gui.PanelParameters;
import snake.snakeAI.SnakeIndividual;
import snake.snakeAI.nn.SnakeAIAgent;
import snake.snakeAI.nn.SnakeAIAgentV1;
import snake.snakeAI.nn.SnakeAIAgentV2;
import snake.snakeAI.nn.utils.ActivationFunction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnvironmentAI extends Environment {

    private SnakeIndividual bestInRun = null;

    private List<Integer> numNNInputs;
    private List<Integer> numNNHidden;
    private List<Integer> numNNOutputs;
    private List<ActivationFunction> activationFunctions;

    private List<Integer> genomeSizes;

    public EnvironmentAI(int size, int maxIterations) {
        super(size, maxIterations);

        this.numNNInputs = new ArrayList<>();
        this.numNNHidden = new ArrayList<>();
        this.numNNOutputs = new ArrayList<>();
        this.genomeSizes = new ArrayList<>();
    }

    @Override
    protected void placeAgents() {
        Cell agentCell = getAgentFreeCell();

        if (!nnDimensionsSet())
            throw new IllegalArgumentException("Invalid Neural Network dimensions");

        switch (PanelParameters.getProblemType()) {
            case ONE_AI:
                agents.add(new SnakeAIAgentV1(agentCell, numNNInputs.get(0), numNNHidden.get(0), numNNOutputs.get(0),
                        activationFunctions.get(0), Color.GREEN));

                if (hasBestInRun()) {
                    setWeights(bestInRun.getGenome());
                }
                break;
            case TWO_IDENTICAL_AI:
                agents.add(new SnakeAIAgentV1(agentCell, numNNInputs.get(0), numNNHidden.get(0), numNNOutputs.get(0),
                        activationFunctions.get(0), Color.GREEN));

                agentCell = getAgentFreeCell();
                agents.add(new SnakeAIAgentV1(agentCell, numNNInputs.get(0), numNNHidden.get(0), numNNOutputs.get(0),
                        activationFunctions.get(0), Color.ORANGE));

                if (hasBestInRun()) {
                    setWeights(bestInRun.getGenome());
                }
                break;
            case TWO_DIFFERENT_AI:
                agents.add(new SnakeAIAgentV1(agentCell, numNNInputs.get(0), numNNHidden.get(0), numNNOutputs.get(0),
                        activationFunctions.get(0), Color.GREEN));

                agentCell = getAgentFreeCell();

//                SnakeAIAgentV2 agentV2 = new SnakeAIAgentV2(agentCell, numNNInputs.get(index), numNNHidden.get(index), numNNOutputs.get(index),
//                        activationFunctions.get(index), Color.ORANGE);
//                agents.add(agentV2);

                agents.add(new SnakeAIAgentV1(agentCell, numNNInputs.get(1), numNNHidden.get(1), numNNOutputs.get(1),
                        activationFunctions.get(1), Color.ORANGE));

                /**/

                if (hasBestInRun()) {
                    setWeights(bestInRun.getGenome());
                }
                break;
        }
    }

    public boolean hasBestInRun() {
        return bestInRun != null;
    }

    private boolean nnDimensionsSet() {
        return numNNInputs.size() > 0 && numNNHidden.size() > 0 && numNNOutputs.size() > 0;
    }

    public void setBestInRun(SnakeIndividual bestInRun) {
        this.bestInRun = bestInRun;
    }

    public void setNNParameters(List<Integer> numInputs, List<Integer> numHiddenUnits, List<Integer> numOutputs,
                                List<ActivationFunction> activationFunctions) {
        this.numNNInputs = numInputs;
        this.numNNHidden = numHiddenUnits;
        this.numNNOutputs = numOutputs;
        this.activationFunctions = activationFunctions;

        for (int i = 0; i < numInputs.size(); i++) {
            genomeSizes.add((numInputs.get(i) + 1) * numHiddenUnits.get(i) +
                    (numHiddenUnits.get(i) + 1) * numOutputs.get(i)
            );
        }
    }

    public void setWeights(double[] genome) {
        for (int i = 0, start = 0; i < agents.size(); i++) {
            if (PanelParameters.getProblemType() == ProblemType.TWO_DIFFERENT_AI) {
                ((SnakeAIAgent) agents.get(i)).setWeights(Arrays.copyOfRange(genome, start, start + genomeSizes.get(i) ));
                start = genomeSizes.get(i) - 1;
                continue;
            }

            ((SnakeAIAgent) agents.get(i)).setWeights(genome);
        }
    }

    public List<Integer> getGenomeSizes() {
        return genomeSizes;
    }

    public List<Integer> getNumNNInputs() {
        return numNNInputs;
    }

    public List<Integer> getNumNNHidden() {
        return numNNHidden;
    }

    public List<Integer> getNumNNOutputs() {
        return numNNOutputs;
    }

    public List<ActivationFunction> getActivationFunctions() {
        return activationFunctions;
    }
}
