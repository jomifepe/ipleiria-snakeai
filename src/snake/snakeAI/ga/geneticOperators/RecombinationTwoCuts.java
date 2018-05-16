package snake.snakeAI.ga.geneticOperators;

import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.Individual;

public class RecombinationTwoCuts <I extends Individual> extends Recombination<I> {

    public RecombinationTwoCuts(double probability) {
        super(probability);
    }

    @Override
    public void run(I ind1, I ind2) {
        int cut1 = GeneticAlgorithm.random.nextInt(ind1.getNumGenes());
        int cut2 = GeneticAlgorithm.random.nextInt(ind1.getNumGenes());
        if (cut1 > cut2) {
            int aux = cut1;
            cut1 = cut2;
            cut2 = aux;
        }

        for (int i = cut1; i < cut2; i++) {
            ind1.swapGenes(ind2, i);        }
    }
    
    @Override
    public String toString(){
        return "Two cuts recombination (" + probability + ")";
    }    
}