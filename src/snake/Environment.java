package snake;

import gui.PanelParameters;
import snake.snakeAI.SnakeIndividual;
import snake.snakeAI.nn.SnakeAIAgent;
import snake.snakeAdhoc.SnakeAdhocAgent;
import snake.snakeRandom.SnakeRandomAgent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Environment {
    public static Random random;
    private final Cell[][] grid;
    private final List<SnakeAgent> agents;
    private Food food;
    private final int maxIterations;
    private int numIterations;

    private ProblemType modeOfOperation;

    private SnakeIndividual bestInRun = null;

    private List<Integer> numNNInputs;
    private List<Integer> numNNHiddenUnits;
    private List<Integer> numNNOutputs;

    public Environment(int size, int maxIterations) {
        random = new Random();
        this.maxIterations = maxIterations;
        this.numIterations = 0;

        this.grid = new Cell[size][size];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }

        this.agents = new ArrayList<>();
        this.numNNInputs = new ArrayList<>();
        this.numNNHiddenUnits = new ArrayList<>();
        this.numNNOutputs = new ArrayList<>();
    }

    public void initialize(int seed) {
//        random.setSeed(seed);
        resetEnvironment();
        placeAgents();
        placeFood();
    }

    private void resetEnvironment() {
        this.numIterations = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j].setAgentHead(null);
                grid[i][j].setFood(null);
                grid[i][j].setTailCell(null);
            }
        }

        agents.clear();
    }

    void placeAgents() {
        Cell agentCell = getAgentFreeCell();

        switch (PanelParameters.getProblemType()) {
            case RANDOM:
                agents.add(new SnakeRandomAgent(agentCell, Color.GREEN));
                break;
            case ADHOC:
                agents.add(new SnakeAdhocAgent(agentCell, Color.GREEN));
                break;
            case ONE_AI:
                if (!nnDimensionsSet())
                    throw new IllegalArgumentException("Invalid Neural Network dimensions");

                SnakeAIAgent agent = new SnakeAIAgent(agentCell, numNNInputs.get(0), numNNHiddenUnits.get(0), numNNOutputs.get(0), Color.GREEN);
                if (bestInRun != null)
                    agent.setWeights(bestInRun.getGenome());
                agents.add(agent);
                break;
            case TWO_IDENTICAL_AI:
                if (!nnDimensionsSet())
                    throw new IllegalArgumentException("Invalid Neural Network dimensions");

                agent = new SnakeAIAgent(agentCell, numNNInputs.get(0), numNNHiddenUnits.get(0), numNNOutputs.get(0), Color.GREEN);
                if (bestInRun != null)
                    agent.setWeights(bestInRun.getGenome());
                agents.add(agent);

                agentCell = getAgentFreeCell();
                agent = new SnakeAIAgent(agentCell, numNNInputs.get(0), numNNHiddenUnits.get(0), numNNOutputs.get(0), Color.ORANGE);
                if (bestInRun != null)
                    agent.setWeights(bestInRun.getGenome());
                agents.add(agent);
                break;
            case TWO_DIFFERENT_AI:
                if (!nnDimensionsSet())
                    throw new IllegalArgumentException("Invalid Neural Network dimensions");

                agent = new SnakeAIAgent(agentCell, numNNInputs.get(0), numNNHiddenUnits.get(0), numNNOutputs.get(0), Color.GREEN);
                if (bestInRun != null)
                    agent.setWeights(bestInRun.getGenome());
                agents.add(agent);

                agentCell = getAgentFreeCell();
                agent = new SnakeAIAgent(agentCell,
                        numNNInputs.get(numNNInputs.size() > 1 ? 1 : 0),
                        numNNHiddenUnits.get(numNNHiddenUnits.size() > 1 ? 1 : 0),
                        numNNOutputs.get(numNNOutputs.size() > 1 ? 1 : 0), Color.ORANGE);
                if (bestInRun != null)
                    agent.setWeights(bestInRun.getGenome());
                agents.add(agent);
                break;
        }
    }

    void placeFood() {
        int foodLine, foodColumn;

        do {
            foodLine = random.nextInt(grid.length);
            foodColumn = random.nextInt(grid.length);
        } while (grid[foodLine][foodColumn].hasAgent() || grid[foodLine][foodColumn].hasTailCell());

        grid[foodLine][foodColumn].setFood(food = new Food(new Cell(foodLine, foodColumn)));
    }

    private Cell getAgentFreeCell() {
        int line, column;

        do {
            line = random.nextInt(grid.length);
            column = random.nextInt(grid.length);
        } while (!isCellAgentFree(line, column));

        return new Cell(line, column);
    }

    private boolean isCellAgentFree(int line, int column) {
        return agents.stream().noneMatch(snakeAgent -> snakeAgent.getHead().getLine() == line &&
                snakeAgent.getHead().getLine() == column);
    }

    public void simulate() {
        boolean aliveSnakes = true;
        for (int i = 0; i < maxIterations; i++) {
            if (!aliveSnakes)
                break;

            for (SnakeAgent agent : agents) {
                if (!agent.isAlive())
                    continue;

                agent.act(this);
                fireUpdatedEnvironment();
            }

            /* verfifies if there's any snake left */
            aliveSnakes = agents.stream().anyMatch(SnakeAgent::isAlive);
            numIterations++;
        }
    }

    public boolean hasBestInRun() {
        return bestInRun != null;
    }

    public void setBestInRun(SnakeIndividual bestInRun) {
        this.bestInRun = bestInRun;
    }

    public void setModeOfOperation(ProblemType modeOfOperation) {
        this.modeOfOperation = modeOfOperation;
    }

    private boolean nnDimensionsSet() {
        return numNNInputs.size() > 0 && numNNHiddenUnits.size() > 0 && numNNOutputs.size() > 0;
    }

    public void setNNDimensions(List<Integer> numInputs, List<Integer> numHiddenUnits, List<Integer> numOutputs) {
        this.numNNInputs = numInputs;
        this.numNNHiddenUnits = numHiddenUnits;
        this.numNNOutputs = numOutputs;
    }

    //    public Cell getCellToThe(Action action, Cell relativeTo) {
//        int line = relativeTo.getLine() + action.getX();
//        int column = relativeTo.getColumn() + action.getY();
//
//        if (line < 0 || line >= getNumLines() || column < 0 || column >= getNumColumns())
//            return null;
//
//        return grid[line][column];
//    }

    public Cell getNorthCell(Cell cell) {
        if (cell.getLine() > 0) {
            return grid[cell.getLine() - 1][cell.getColumn()];
        }
        return null;
    }

    public Cell getSouthCell(Cell cell) {
        if (cell.getLine() < grid.length - 1) {
            return grid[cell.getLine() + 1][cell.getColumn()];
        }
        return null;
    }

    public Cell getEastCell(Cell cell) {
        if (cell.getColumn() < grid[0].length - 1) {
            return grid[cell.getLine()][cell.getColumn() + 1];
        }
        return null;
    }

    public Cell getWestCell(Cell cell) {
        if (cell.getColumn() > 0) {
            return grid[cell.getLine()][cell.getColumn() - 1];
        }
        return null;
    }

    public int getNumLines() {
        return grid.length;
    }

    public int getNumColumns() {
        return grid[0].length;
    }

    public final Cell getCell(int line, int column) {
        return grid[line][column];
    }

    public Color getCellColor(int line, int column) {
        return grid[line][column].getColor();
    }

    public Cell getFood() {
        return food.getCell();
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

    //listeners
    private final ArrayList<EnvironmentListener> listeners = new ArrayList<>();

    public synchronized void addEnvironmentListener(EnvironmentListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public synchronized void removeEnvironmentListener(EnvironmentListener l) {
        listeners.remove(l);
    }

    public void fireUpdatedEnvironment() {
        for (EnvironmentListener listener : listeners) {
            listener.environmentUpdated();
        }
    }

    public int getIterations() {
        return numIterations;
    }

    public List<SnakeAgent> getAgents() {
        return agents;
    }
}
