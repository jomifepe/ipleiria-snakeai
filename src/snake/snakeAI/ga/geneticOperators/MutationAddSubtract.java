package snake.snakeAI.ga.geneticOperators;

import snake.Environment;
import snake.snakeAI.ga.RealVectorIndividual;

//PLEASE, MODIFY THE CLASS NAME
public class MutationAddSubtract<I extends RealVectorIndividual> extends Mutation<I> {

   
    public MutationAddSubtract(double probability) {
        super(probability);
    }

    @Override
    public void run(I ind) {
        for (int i = 0; i < ind.getNumGenes(); i++) {
            if (Environment.random.nextDouble() < probability) {
                double gene = ind.getGene(i);

//                double x = Environment.random.nextGaussian();
//                double y = (x * 0.5) + 0.5;
//                double newVal = Math.rint(y * 100000.0) * 0.00001;
//                ind.setGene(i, newVal);

                /* randomly chooses to sum or subtract */
                double percentage = (Environment.random.nextBoolean() ? 1 : 0) + .3;

                ind.setGene(i, gene - (percentage * gene));
            }
        }
    }
    
    @Override
    public String toString(){
        return "Subtraction mutation (" + probability + ")";
    }
}