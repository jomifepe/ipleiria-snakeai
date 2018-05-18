package snake.snakeAI.ga.geneticOperators;

import snake.Environment;
import snake.snakeAI.ga.RealVectorIndividual;

public class MutationGaussian<I extends RealVectorIndividual> extends Mutation<I> {

    private double delta;

    public MutationGaussian(double probability) {
        super(probability);
        this.delta = .3;
    }

    public MutationGaussian(double probability, double delta) {
        super(probability);
        this.delta = delta;
    }

    @Override
    public void run(I ind) {
        for (int i = 0; i < ind.getNumGenes(); i++) {
            if (Environment.random.nextDouble() < probability) {
                double gene = ind.getGene(i);
                ind.setGene(i, gene + Environment.random.nextGaussian() * delta);
            }
        }
    }
    
    @Override
    public String toString(){
        return "Gaussian";
    }
}