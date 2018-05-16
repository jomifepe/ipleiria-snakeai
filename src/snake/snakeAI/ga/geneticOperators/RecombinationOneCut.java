package snake.snakeAI.ga.geneticOperators;

import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.Individual;

public class RecombinationOneCut <I extends Individual> extends Recombination<I> {

    public RecombinationOneCut(double probability) {
        super(probability);
    }

    @Override
    public void run(I ind1, I ind2) {
        int cut = GeneticAlgorithm.random.nextInt(ind1.getNumGenes());

        for (int i = 0; i < cut; i++) {
            ind1.swapGenes(ind2, i);
        }
    }
    
    @Override
    public String toString(){
        return "One cut recombination (" + probability + ")";
    }    
}