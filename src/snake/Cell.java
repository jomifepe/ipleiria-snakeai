package snake;

import java.awt.Color;

public class Cell {
    public static final Color COLOR = Color.WHITE;

    private final int line, column;
    private SnakeAgent agentHead;
    private Food food;
    private TailCell tailCell;

    public Cell(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public SnakeAgent getAgentHead() {
        return agentHead;
    }

    public boolean hasAgent() {
        return agentHead != null;
    }

    public boolean hasFood() { return food != null; }

    public boolean hasTailCell() { return tailCell != null; }

    public Food getFood() { return food; }

    public void setAgentHead(SnakeAgent agentHead) {
        this.agentHead = agentHead;
    }

    public void setFood(Food food) { this.food = food; }

    public void setTailCell(TailCell tailCell) { this.tailCell = tailCell; }

    public boolean isToTheNorthOf(Cell relativeTo) {
        return this.line < relativeTo.line;
    }

    public boolean isToTheSouthOf(Cell relativeTo) {
        return this.line > relativeTo.line;
    }

    public boolean isToTheEastOf(Cell relativeTo) {
        return this.column > relativeTo.column;
    }

    public boolean isToTheWestOf(Cell relativeTo) {
        return this.column < relativeTo.column;
    }

    public boolean isFree() {
        return this.tailCell == null && this.agentHead == null;
    }

    public boolean canMoveTo(Cell destination, int maxLin, int maxCol) {
        if (destination == null || destination.hasAgent() || destination.hasTailCell())
            return false;
        if (destination.isToTheNorthOf(this) && this.line == 0)
            return false;
        if (destination.isToTheSouthOf(this) && this.line == maxLin)
            return false;
        if (destination.isToTheEastOf(this) && this.column == maxCol)
            return false;
        if (destination.isToTheWestOf(this) && this.column == 0)
            return false;

        return true;
    }

    public Color getColor() {
        if (hasAgent())
            return agentHead.getHeadColor();
        if (hasFood())
            return food.getColor();
        if (hasTailCell())
            return tailCell.getColor();

        return Cell.COLOR;
    }
}