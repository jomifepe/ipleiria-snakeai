package snake.snakeAI.ga.selectionMethods;

import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.Individual;
import snake.snakeAI.ga.Population;
import snake.snakeAI.ga.Problem;

public class Tournament <I extends Individual, P extends Problem<I>> extends SelectionMethod<I, P> {

    private int size;

    public Tournament(int popSize) {
        this(popSize, 2);
    }

    public Tournament(int popSize, int size) {
        super(popSize);
        this.size = size;
    }

    @Override
    public Population<I, P> run(Population<I, P> original) {
        Population<I, P> result = new Population<>(original.getSize());        

        for (int i = 0; i < popSize; i++) {
            result.addIndividual(tournament(original));
        }
        return result;
    }

    private I tournament(Population<I, P> population) {
        I best = population.getIndividual(GeneticAlgorithm.random.nextInt(popSize));

        for (int i = 1; i < size; i++) {
            I aux = population.getIndividual(GeneticAlgorithm.random.nextInt(popSize));
            if (aux.getFitness() > best.getFitness())
                best = aux;
        }
        return (I) best.clone();
    }
    
    @Override
    public String toString(){
        return "Tournament";
    }    
}