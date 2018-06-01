package snake.snakeAI.nn;

import snake.Action;
import snake.Cell;
import snake.Perception;
import snake.snakeAI.nn.utils.ActivationFunction;

import java.awt.*;

public class SnakeAIAgentV2 extends SnakeAIAgent {

    public SnakeAIAgentV2(Cell cell, int inputLayerSize, int hiddenLayerSize, int outputLayerSize,
                          ActivationFunction activationFunction, Color color) {
        super(cell, inputLayerSize, hiddenLayerSize, outputLayerSize, activationFunction, color);
    }

    @Override
    protected int[] buildInputsFromPerception(Perception perception) {
        Cell n = perception.getN();
        Cell s = perception.getS();
        Cell e = perception.getE();
        Cell w = perception.getW();
        Cell food = environment.getFood();

        return new int[] {
                food.isToTheNorthOf(head) ? Action.NORTH.getY() : Action.SOUTH.getY(),
                food.isToTheEastOf(head) ? Action.EAST.getX() : Action.WEST.getX(),
                n != null && n.isFree() ? (previous == null ? 1 : (previous.opposite() != Action.NORTH ? 1 : 0)) : 0,
                s != null && s.isFree() ? (previous == null ? 1 : (previous.opposite() != Action.SOUTH ? 1 : 0)) : 0,
                e != null && e.isFree() ? (previous == null ? 1 : (previous.opposite() != Action.EAST ? 1 : 0)) : 0,
                w != null && w.isFree() ? (previous == null ? 1 : (previous.opposite() != Action.WEST ? 1 : 0)) : 0
        };
    }
}
