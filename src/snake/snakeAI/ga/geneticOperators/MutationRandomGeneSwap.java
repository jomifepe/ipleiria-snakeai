package snake.snakeAI.ga.geneticOperators;

import snake.Environment;
import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.RealVectorIndividual;

public class MutationRandomGeneSwap<I extends RealVectorIndividual> extends Mutation<I> {

    public MutationRandomGeneSwap(double probability) {
        super(probability);
    }

    @Override
    public void run(I ind) {
        for (int i = 0; i < ind.getNumGenes(); i++) {
            if (Environment.random.nextDouble() < probability) {
                double currentGeneValue = ind.getGene(i);
                int targetIndex = Environment.random.nextInt(ind.getNumGenes());
                double targetValue = ind.getGene(targetIndex);

                ind.setGene(targetIndex, currentGeneValue);
                ind.setGene(i, targetValue);
            }
        }
    }
    
    @Override
    public String toString(){
        return "AddOrSubtract";
    }
}