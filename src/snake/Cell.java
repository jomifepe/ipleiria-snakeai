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

    public Color getColor() {
        if (hasAgent()) {
            return agentHead.getHeadColor();
        } else if (hasFood()) {
            return food.getColor();
        } else if (hasTailCell()) {
            return tailCell.getColor();
        } else {
            return Cell.COLOR;
        }
    }
}