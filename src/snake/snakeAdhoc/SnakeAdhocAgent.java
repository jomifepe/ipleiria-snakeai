package snake.snakeAdhoc;

import snake.*;

import java.awt.*;


public class SnakeAdhocAgent extends SnakeAgent {

    public SnakeAdhocAgent(Cell cell, Color color) {
        super(cell, color.darker(), color);
    }

    @Override
    protected Action decide(Perception perception) {
        Action action = null;

        Cell food = environment.getFood();
        Cell w = perception.getW();
        Cell n = perception.getN();
        Cell e = perception.getE();
        Cell s = perception.getS();

        if (food.getLine() == head.getLine())  {
            if (n != null && n.isFree())
                action = Action.NORTH;
            if (s != null && s.isFree())
                action = Action.SOUTH;
        }

        if (food.getColumn() == head.getColumn()) {
            if (e != null && e.isFree())
                action = Action.EAST;
            if (w != null && w.isFree())
                action = Action.WEST;
        }

        if (food.isToTheWestOf(head)) {
            if (w != null && w.isFree()) {
                action = Action.WEST;
            } else {
                if (e != null && e.isFree())
                    action = Action.EAST;
                if (n != null && n.isFree())
                    action = Action.NORTH;
                if (s != null && s.isFree())
                    action = Action.SOUTH;
            }
        }
        if (food.isToTheEastOf(head)) {
            if (e != null && e.isFree()) {
                action = Action.EAST;
            } else {
                if (w != null && w.isFree())
                    action = Action.WEST;
                if (n != null && n.isFree())
                    action = Action.NORTH;
                if (s != null && s.isFree())
                    action = Action.SOUTH;
            }
        }
        if (food.isToTheNorthOf(head)) {
            if (n != null && n.isFree()) {
                action = Action.NORTH;
            } else {
                if (s != null && s.isFree())
                    action = Action.SOUTH;
                if (w != null && w.isFree())
                    action = Action.WEST;
                if (e != null && e.isFree())
                    action = Action.EAST;
            }
        }
        if (food.isToTheSouthOf(head)) {
            if (s != null && s.isFree()) {
                action = Action.SOUTH;
            } else {
                if (n != null && n.isFree())
                    action = Action.NORTH;
                if (w != null && w.isFree())
                    action = Action.WEST;
                if (e != null && e.isFree())
                    action = Action.EAST;
            }
        }

        return action;
    }

}
