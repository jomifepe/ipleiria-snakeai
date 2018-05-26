package snake;

import gui.PanelParameters;
import snake.snakeAI.SnakeIndividual;
import snake.snakeAI.nn.SnakeAIAgent;
import snake.snakeAI.nn.SnakeAIAgentV1;
import snake.snakeAI.nn.SnakeAIAgentV2;
import snake.snakeAdhoc.SnakeAdhocAgent;
import snake.snakeRandom.SnakeRandomAgent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnvironmentAI extends Environment {

    private SnakeIndividual bestInRun = null;

    private List<Integer> numNNInputs;
    private List<Integer> numNNHiddenUnits;
    private List<Integer> numNNOutputs;

    private List<Integer> genomeSizes;

    public EnvironmentAI(int size, int maxIterations) {
        super(size, maxIterations);

        this.numNNInputs = new ArrayList<>();
        this.numNNHiddenUnits = new ArrayList<>();
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
                SnakeAIAgent agent = new SnakeAIAgentV1(agentCell, numNNInputs.get(0), numNNHiddenUnits.get(0), numNNOutputs.get(0), Color.GREEN);
                if (hasBestInRun()) {
                    agent.setWeights(bestInRun.getGenome());
                }
                agents.add(agent);
                break;
            case TWO_IDENTICAL_AI:
                agent = new SnakeAIAgentV1(agentCell, numNNInputs.get(0), numNNHiddenUnits.get(0), numNNOutputs.get(0), Color.GREEN);
                if (hasBestInRun()) {
                    agent.setWeights(bestInRun.getGenome());
                }
                agents.add(agent);

                agentCell = getAgentFreeCell();
                agent = new SnakeAIAgentV1(agentCell, numNNInputs.get(0), numNNHiddenUnits.get(0), numNNOutputs.get(0), Color.ORANGE);
                if (hasBestInRun()) {
                    agent.setWeights(bestInRun.getGenome());
                }
                agents.add(agent);
                break;
            case TWO_DIFFERENT_AI:
                agent = new SnakeAIAgentV1(agentCell, numNNInputs.get(0), numNNHiddenUnits.get(0), numNNOutputs.get(0), Color.GREEN);
                if (hasBestInRun()) {
                    agent.setWeights(bestInRun.getGenome());
                }
                agents.add(agent);

                agentCell = getAgentFreeCell();
                SnakeAIAgentV2 agentV2;
                if (numNNInputs.size() == 1) {
                    agentV2 = new SnakeAIAgentV2(agentCell, numNNInputs.get(0), numNNHiddenUnits.get(0), numNNOutputs.get(0), Color.ORANGE);
                } else {
                    agentV2 = new SnakeAIAgentV2(agentCell, numNNInputs.get(1), numNNHiddenUnits.get(1), numNNOutputs.get(1), Color.ORANGE);
                }
                if (bestInRun != null)
                    agentV2.setWeights(bestInRun.getGenome());
                agents.add(agentV2);
                break;
        }
    }

    public boolean hasBestInRun() {
        return bestInRun != null;
    }

    private boolean nnDimensionsSet() {
        return numNNInputs.size() > 0 && numNNHiddenUnits.size() > 0 && numNNOutputs.size() > 0;
    }

    public void setBestInRun(SnakeIndividual bestInRun) {
        this.bestInRun = bestInRun;
    }

    public void setNNDimensions(List<Integer> numInputs, List<Integer> numHiddenUnits, List<Integer> numOutputs) {
        this.numNNInputs = numInputs;
        this.numNNHiddenUnits = numHiddenUnits;
        this.numNNOutputs = numOutputs;


        for (int i = 0; i < numInputs.size(); i++) {
            genomeSizes.add(((numInputs.get(i) + 1) * numHiddenUnits.get(i)) +
                    ((numHiddenUnits.get(i) + 1) * numOutputs.get(i))
            );
        }
    }

    public void setWeights(double[] genomeWeights) {
        for (SnakeAgent agent : agents) {
            ((SnakeAIAgent) agent).setWeights(genomeWeights);
        }
    }

    public List<Integer> getGenomeSizes() {
        return genomeSizes;
    }

    public List<Integer> getNumNNInputs() {
        return numNNInputs;
    }

    public List<Integer> getNumNNHiddenUnits() {
        return numNNHiddenUnits;
    }

    public List<Integer> getNumNNOutputs() {
        return numNNOutputs;
    }
}
