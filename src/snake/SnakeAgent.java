package snake;

import util.ConsoleColor;
import util.ConsoleUtils;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

public abstract class SnakeAgent {

    protected Cell head;
    protected Color colorHead;
    protected Color colorTail;
    protected List<TailCell> tail;
    protected Environment environment;
    protected boolean alive;
    private int stepsTakenSinceLastFood;

    public SnakeAgent(Cell cell, Color headColor, Color tailColor) {
        this.alive = true;
        this.head = cell;
        if (cell != null) {
            this.head.setAgentHead(this);
        }
        this.colorHead = headColor;
        this.colorTail = tailColor;
        this.tail = new LinkedList<>();
        this.stepsTakenSinceLastFood = 0;
    }

    Action previousAction = null;
    public void act(Environment environment) {
        this.environment = environment;

        Perception perception = buildPerception();
        Action action = decide(perception);

//        if (previousAction != action) {
//            previousAction = action;
//            printDecision(action);
//        }

        execute(action);
    }

    private void printDecision(Action action) {
        ConsoleUtils.println(action != null ? ConsoleColor.BRIGHT_GREEN: ConsoleColor.BRIGHT_RED,
                "Action decided:", action != null ? action.toString() : "null");
    }

    protected Perception buildPerception() {
        return new Perception(environment.getNorthCell(head), environment.getSouthCell(head),
                environment.getEastCell(head), environment.getWestCell(head));
    }

    protected void execute(Action action) {
        if (action == null) {
            alive = false;
            return;
        }

        int maxGridLin = environment.getNumLines() - 1;
        int maxGridCol = environment.getNumColumns() - 1;

        Cell nextCell = null;
        Cell currentCell = new Cell(head.getLine(), head.getColumn());

        switch (action) {
            case NORTH:
                Cell northCell = environment.getNorthCell(head);
                if (head.canMoveTo(northCell, maxGridLin, maxGridCol))
                    nextCell = northCell;
                break;
            case SOUTH:
                Cell southCell = environment.getSouthCell(head);
                if (head.canMoveTo(southCell, maxGridLin, maxGridCol))
                    nextCell = southCell;
                break;
            case EAST:
                Cell eastCell = environment.getEastCell(head);
                if (head.canMoveTo(eastCell, maxGridLin, maxGridCol))
                    nextCell = eastCell;
                break;
            case WEST:
                Cell westCell = environment.getWestCell(head);
                if (head.canMoveTo(westCell, maxGridLin, maxGridCol))
                    nextCell = westCell;
                break;
        }

        if (nextCell != null) {
            boolean currentCellHasFood = nextCell.hasFood();

            updateTail(currentCell, currentCellHasFood, environment);
            setCell(nextCell);
        }

        stepsTakenSinceLastFood++;
        alive = (nextCell != null);
    }

    private void updateTail(Cell currentCell, boolean currentCellHasFood, Environment environment) {
        if (tail.size() == 0 && !currentCellHasFood)
            return;

        if (tail.size() == 0) // adds the first tail cell
            tail.add(new TailCell(currentCell.getLine(), currentCell.getColumn(), colorTail));
        else // adds a cell to the "front" of the tail
            tail.add(0, new TailCell(currentCell.getLine(), currentCell.getColumn(), colorTail));

        renderTailCellOnGrid(currentCell.getLine(), currentCell.getColumn(), environment);

        if (!currentCellHasFood) { // removes the last tail cell if a piece of food isn't being eaten
            TailCell tailCellToRemove = tail.get(tail.size() - 1);
            removeTailCellFromGrid(tailCellToRemove.getLine(), tailCellToRemove.getColumn(), environment);
            tail.remove(tailCellToRemove);
        }
    }

    private void renderTailCellOnGrid(int line, int column, Environment environment) {
        environment.getCell(line, column).setTailCell(new TailCell(line, column, colorTail));
    }

    private void removeTailCellFromGrid(int line, int column, Environment environment) {
        environment.getCell(line, column).setTailCell(null);
    }

    protected abstract Action decide(Perception perception);

    public void setCell(Cell newCell) {
        if (head != null) {
            head.setAgentHead(null);
        }
        head = newCell;
        if (newCell != null) {
            newCell.setAgentHead(this);
        }

        if (newCell.hasFood()) {
            head.setFood(null);
            environment.placeFood();
            stepsTakenSinceLastFood = 0;
        }
    }

    public Color getHeadColor() {
        return colorHead;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getTailSize() {
        return tail.size();
    }

    public Cell getHead() {
        return head;
    }

    public int getStepsTakenSinceLastFood() {
        return this.stepsTakenSinceLastFood;
    }
}
