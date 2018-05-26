package snake;

import gui.PanelParameters;
import snake.snakeAI.SnakeIndividual;
import snake.snakeAdhoc.SnakeAdhocAgent;
import snake.snakeRandom.SnakeRandomAgent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentNonAI extends Environment {

    public EnvironmentNonAI(int size, int maxIterations) {
        super(size, maxIterations);
    }

    @Override
    protected void placeAgents() {
        Cell agentCell = getAgentFreeCell();

        switch (PanelParameters.getProblemType()) {
            case RANDOM:
                agents.add(new SnakeRandomAgent(agentCell, Color.GREEN));
                break;
            case ADHOC:
                agents.add(new SnakeAdhocAgent(agentCell, Color.GREEN));
                break;
        }
    }
}
