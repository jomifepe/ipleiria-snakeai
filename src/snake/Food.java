package snake;

import java.awt.*;

public class Food {
    public static final Color COLOR = Color.RED;

    private Cell cell;

    public Food(Cell cell) {
        this.cell = cell;
        if (cell != null)
            this.cell.setFood(this);
    }

    public Color getColor() {
        return COLOR;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        if (this.cell != null)
            this.cell.setFood(null);
        this.cell = cell;
        if (cell != null)
            this.cell.setFood(this);
    }
}
