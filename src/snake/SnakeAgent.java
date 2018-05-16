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

    public SnakeAgent(Cell cell, Color headColor, Color tailColor) {
        this.head = cell;
        if (cell != null) {
            this.head.setAgentHead(this);
        }
        this.colorHead = headColor;
        this.colorTail = tailColor;
        tail = new LinkedList<>();
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
                "Action decided:", action != null ? action.name() : "null");
    }

    protected Perception buildPerception() {
        return new Perception(environment.getNorthCell(head), environment.getSouthCell(head),
                environment.getEastCell(head), environment.getWestCell(head));
    }

    protected void execute(Action action)
    {
        Cell nextCell = null;
        Cell currentCell = new Cell(head.getLine(), head.getColumn());

        if (action == Action.NORTH &&
                head.getLine() != 0 &&
                !environment.getNorthCell(head).hasTailCell()) {

            nextCell = environment.getNorthCell(head);
        } else if (action == Action.SOUTH &&
                head.getLine() != environment.getNumLines() - 1 &&
                !environment.getSouthCell(head).hasTailCell()) {

            nextCell = environment.getSouthCell(head);
        } else if (action == Action.WEST && head.getColumn() != 0  &&
                !environment.getWestCell(head).hasTailCell()) {

            nextCell = environment.getWestCell(head);
        } else if (action == Action.EAST &&
                head.getColumn() != environment.getNumColumns() - 1 &&
                !environment.getEastCell(head).hasTailCell()) {

            nextCell = environment.getEastCell(head);
        }

        if (nextCell != null && !nextCell.hasAgent()) {
            boolean currentCellHasFood = nextCell.hasFood();
            updateTail(currentCell, currentCellHasFood, environment);
            setCell(nextCell);
        }

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
        if (this.head != null) {
            this.head.setAgentHead(null);
        }
        this.head = newCell;
        if (newCell != null) {
            newCell.setAgentHead(this);
        }

        if (newCell.hasFood()) {
            this.head.setFood(null);
            environment.placeFood();
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
}
