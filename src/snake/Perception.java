package snake;

public class Perception {
    private Cell n, s, e, w;

    public Perception(Cell N, Cell S, Cell E, Cell W) {
        this.n = N;
        this.s = S;
        this.e = E;
        this.w = W;
    }

    public Cell getE() {
        return e;
    }

    public Cell getN() {
        return n;
    }

    public Cell getW() {
        return w;
    }

    public Cell getS() {
        return s;
    }
}
