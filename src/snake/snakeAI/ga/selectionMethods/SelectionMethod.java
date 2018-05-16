package snake.snakeAI.ga.selectionMethods;

import snake.snakeAI.ga.Individual;
import snake.snakeAI.ga.Population;
import snake.snakeAI.ga.Problem;

public abstract class SelectionMethod <I extends Individual, P extends Problem<I>>{

    protected int popSize;
    
    public SelectionMethod(int popSize){
        this.popSize = popSize;
    }

    public abstract Population<I, P> run(Population<I, P> original);
}