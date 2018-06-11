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
        Cell n = perception.getN();
        Cell s = perception.getS();
        Cell e = perception.getE();
        Cell w = perception.getW();

        if (n != null && n.hasFood())
            return Action.NORTH;
        if (s != null && s.hasFood())
            return Action.SOUTH;
        if (e != null && e.hasFood())
            return Action.EAST;
        if (w != null && w.hasFood())
            return Action.WEST;

        ArrayList<Action> directions = new ArrayList<Action>() {{
            if (n != null && n.isFree())
                add(Action.NORTH);
            if (s != null && s.isFree())
                add(Action.SOUTH);
            if (e != null && e.isFree())
                add(Action.EAST);
            if (w != null && w.isFree())
                add(Action.WEST);
        }};

        return directions.size() > 0 ? directions.get(Environment.random.nextInt(directions.size())) : null;
    }
}
