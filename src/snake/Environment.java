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
    public static final int CB_RANDOM = 0;
    public static final int CB_ADHOC = 1;
    public static final int CB_AI = 2;
    public static final int CB_2IDENTICALAI = 3;
    public static final int CB_2DISTINCTAI = 4;

    public static Random random;
    private final Cell[][] grid;
    private final List<SnakeAgent> agents;
    private Food food;
    private final int maxIterations;
    private int numIterations;

    private SnakeIndividual bestInRun = null;

    private int numNNInputs;
    private int numNNHiddenUnits;
    private int numNNOutputs;

    public Environment(int size, int maxIterations) {
        random = new Random();
        this.maxIterations = maxIterations;

        this.grid = new Cell[size][size];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }

        this.agents = new ArrayList<>();
        this.numIterations = 0;
        this.numNNOutputs = this.numNNHiddenUnits = this.numNNOutputs = 0;
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

    // TODO MODIFY TO PLACE ADHOC OR AI SNAKE AGENTS
    private void placeAgents() {
        Cell cell = new Cell(random.nextInt(grid.length), random.nextInt(grid.length));
        switch (PanelParameters.getCBSnakeType()) {
            case CB_RANDOM:
                agents.add(new SnakeRandomAgent(cell, Color.GREEN));
                break;
            case CB_ADHOC:
                agents.add(new SnakeAdhocAgent(cell, Color.GREEN));
                break;
            case CB_AI:
                if (numNNInputs == 0 || numNNHiddenUnits == 0 || numNNOutputs == 0)
                    throw new IllegalArgumentException("Invalid Neural Network dimensions");

                SnakeAIAgent agent = new SnakeAIAgent(cell, numNNInputs, numNNHiddenUnits, numNNOutputs, Color.GREEN);
                if (bestInRun != null)
                    agent.setWeights(bestInRun.getGenome());
                agents.add(agent);
                break;
            case CB_2IDENTICALAI:
                if (numNNInputs == 0 || numNNHiddenUnits == 0 || numNNOutputs == 0)
                    throw new IllegalArgumentException("Invalid Neural Network dimensions");

                SnakeAIAgent agentBase = new SnakeAIAgent(cell, numNNInputs, numNNHiddenUnits, numNNOutputs, Color.GREEN);
                if (bestInRun != null)
                    agentBase.setWeights(bestInRun.getGenome());
                agents.add(agentBase);
                cell = new Cell(random.nextInt(grid.length-1), random.nextInt(grid.length-1));
                SnakeAIAgent agentClone = new SnakeAIAgent(cell, numNNInputs, numNNHiddenUnits, numNNOutputs, Color.BLUE);
                if (bestInRun != null)
                    agentClone.setWeights(bestInRun.getGenome());
                agents.add(agentClone);
                break;
            case CB_2DISTINCTAI:
                break;
        }
    }

    void placeFood() {
        int foodLine;
        int foodColumn;

        do {
            foodLine = random.nextInt(grid.length);
            foodColumn = random.nextInt(grid.length);
        } while (grid[foodLine][foodColumn].hasAgent() || grid[foodLine][foodColumn].hasTailCell());

        grid[foodLine][foodColumn].setFood(food = new Food(new Cell(foodLine, foodColumn)));
    }

    public void simulate() {
        boolean isAlive = true;

        for (int i = 0; i < maxIterations; i++) {
            if (!isAlive)
                break;

            for (SnakeAgent agent : agents) {
                agent.act(this);
                fireUpdatedEnvironment();
                isAlive = agent.isAlive();
            }

            numIterations++;
        }
    }

   /* public void simulate() {
        boolean isAlive = true;

        for (int i = 0; i < maxIterations; i++) {
            for (SnakeAgent agent : agents) {
                agent.act(this);
                fireUpdatedEnvironment();
                if( !agent.isAlive() )
                    isAlive = false;
                break;
            }
            if(!isAlive)
                break;

            numIterations++;
        }
    }*/

    public int getSize() {
        return grid.length;
    }

    public boolean hasBestInRun() {
        return bestInRun != null;
    }

    public void setBestInRun(SnakeIndividual bestInRun) {
        this.bestInRun = bestInRun;
    }

//    public Cell getCellToThe(Action action, Cell relativeTo) {
//        int line = relativeTo.getLine() + action.getX();
//        int column = relativeTo.getColumn() + action.getY();
//        if (line < 0 || line >= grid.length || column < 0 || column >= grid[0].length)
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

    public void setNNDimensions(int numInputs, int numHiddenUnits, int numOutputs) {
        this.numNNInputs = numInputs;
        this.numNNHiddenUnits = numHiddenUnits;
        this.numNNOutputs = numOutputs;
    }
}
