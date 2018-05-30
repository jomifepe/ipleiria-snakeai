package snake;

import gui.PanelParameters;
import snake.snakeAI.SnakeIndividual;
import snake.snakeAI.nn.SnakeAIAgent;
import snake.snakeAI.nn.SnakeAIAgentV1;
import snake.snakeAI.nn.SnakeAIAgentV2;
import snake.snakeAdhoc.SnakeAdhocAgent;
import snake.snakeRandom.SnakeRandomAgent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Environment {
    public static Random random;
    protected final Cell[][] grid;
    protected final List<SnakeAgent> agents;
    protected Food food;
    protected final int maxIterations;
    protected int numIterations;

    public Environment(int size, int maxIterations) {
        random = new Random();
        this.food = null;
        this.maxIterations = maxIterations;
        this.numIterations = 0;

        this.grid = new Cell[size][size];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }

        this.agents = new ArrayList<>();
    }

    public void initialize(Integer seed) {
        if (seed != null) {
            random.setSeed(seed);
        }

        resetEnvironment();
        placeAgents();
        placeFood();
    }

    private void resetEnvironment() {
        this.food = null;
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

    protected abstract void placeAgents();

    void placeFood() {
        int foodLine, foodColumn;

        do {
            foodLine = random.nextInt(grid.length);
            foodColumn = random.nextInt(grid.length);
        } while (grid[foodLine][foodColumn].hasAgent() || grid[foodLine][foodColumn].hasTailCell());

        grid[foodLine][foodColumn].setFood(food = new Food(new Cell(foodLine, foodColumn)));
    }

    protected Cell getAgentFreeCell() {
        int line, column;

        do {
            line = random.nextInt(grid.length);
            column = random.nextInt(grid.length);
        } while (!isCellAgentFree(line, column));

        return new Cell(line, column);
    }

    protected boolean isCellAgentFree(int line, int column) {
        return agents.stream().noneMatch(snakeAgent -> snakeAgent.getHead().getLine() == line &&
                snakeAgent.getHead().getLine() == column);
    }

    public void simulate() {
        for (int i = 0; i < maxIterations; i++) {
            for (SnakeAgent agent : agents) {
                if (!agent.isAlive()) {
                    return;
                }

                agent.act(this);
                fireUpdatedEnvironment();
            }
            numIterations++;
        }
    }

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

    public int getNumIterations() {
        return numIterations;
    }

    public List<SnakeAgent> getAgents() {
        return agents;
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
}
