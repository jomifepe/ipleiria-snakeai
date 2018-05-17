package snake;

public enum Action {
    NORTH   (0, -1),
    SOUTH   (0, 1),
    WEST    (-1, 0),
    EAST    (1, 0);

    private int x;
    private int y;

    Action(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static Action valueOf(int x, int y) {
        for (Action a : Action.values()) {
            if (x == a.getX() && y == a.getY())
                return a;
        }

        return null;
    }

    public Action opposite() {
        return valueOf(this.getX() * - 1, this.getY() * -1);
    }

    @Override
    public String toString() {
        switch (this) {
            case NORTH: return "NORTH ↑";
            case SOUTH: return "SOUTH ↓";
            case EAST:  return "EAST →";
            case WEST:  return "WEST ←";
        }

        return super.toString();
    }
}