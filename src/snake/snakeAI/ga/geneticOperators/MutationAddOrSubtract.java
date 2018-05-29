package snake.snakeAI.ga.geneticOperators;

import snake.Environment;
import snake.snakeAI.ga.RealVectorIndividual;

public class MutationAddOrSubtract<I extends RealVectorIndividual> extends Mutation<I> {

    private double delta;

    public MutationAddOrSubtract(double probability) {
        super(probability);
        this.delta = .3;
    }

    public MutationAddOrSubtract(double probability, double delta) {
        super(probability);
        this.delta = delta;
    }

    @Override
    public void run(I ind) {
        for (int i = 0; i < ind.getNumGenes(); i++) {
            if (Environment.random.nextDouble() < probability) {
                double gene = ind.getGene(i);
                /* randomly chooses to sum or subtract */
                double val = (Environment.random.nextBoolean() ? 1 : -1) * (delta * gene);
                ind.setGene(i, gene + val);
            }
        }
    }
    
    @Override
    public String toString(){
        return "AddOrSubtract";
    }
}