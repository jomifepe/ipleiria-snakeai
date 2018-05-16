package snake.snakeAI.ga.geneticOperators;

import snake.snakeAI.ga.Individual;

public abstract class Recombination <I extends Individual> extends GeneticOperator {

    public Recombination(double probability) {
        super(probability);
    }

    public abstract void run(I ind1, I ind2);
}