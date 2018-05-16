package snake.snakeAI.ga.geneticOperators;

import snake.snakeAI.ga.Individual;

public abstract class Mutation <I extends Individual> extends GeneticOperator{
    
    public Mutation(double probability){
        super(probability);
    }
    
    public abstract void run(I individual);
}
