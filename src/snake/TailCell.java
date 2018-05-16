package snake;

import java.awt.*;

public class TailCell extends Cell {
    private Color COLOR;

    public TailCell(int line, int column, Color color) {
        super(line, column);
        COLOR = color;
    }

    @Override
    public Color getColor() {
        return COLOR;
    }
}
