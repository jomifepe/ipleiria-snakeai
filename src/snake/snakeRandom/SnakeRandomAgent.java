package snake.snakeRandom;

import snake.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeRandomAgent extends SnakeAgent {

    public SnakeRandomAgent(Cell cell, Color color) {
        super(cell, color.darker(), color);
    }

    @Override
    protected Action decide(Perception perception) {
        ArrayList<Action> directions = new ArrayList<>();

        Cell w = perception.getW();
        Cell n = perception.getN();
        Cell e = perception.getE();
        Cell s = perception.getS();

        if (w != null && w.hasFood())
            return Action.WEST;
        if (n != null && n.hasFood())
            return Action.NORTH;
        if (e != null && e.hasFood())
            return Action.EAST;
        if (s != null && s.hasFood())
            return Action.SOUTH;

        if (e != null && e.isFree())
            directions.add(Action.EAST);
        if (n != null && n.isFree())
            directions.add(Action.NORTH);
        if (s != null && s.isFree())
            directions.add(Action.SOUTH);
        if (w != null && w.isFree())
            directions.add(Action.WEST);

        return directions.size() > 0 ? directions.get(Environment.random.nextInt(directions.size())) : null;
    }
}
