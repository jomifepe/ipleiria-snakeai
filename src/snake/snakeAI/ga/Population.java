package snake.snakeAI.ga;

import java.util.ArrayList;
import java.util.List;

public class Population<I extends Individual, P extends Problem<I>> {

    private final List<I> individuals;
    private I best;

    public Population(int size) {
        individuals = new ArrayList<>(size);
    }

    public Population(int size, P problem) {
        individuals = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            I newIndividual = problem.getNewIndividual();
            individuals.add(newIndividual);
        }
    }

    public I evaluate() {
        best = getIndividual(0);
        for (I individual : individuals) {
            individual.computeFitness();
            if (individual.getFitness() > best.getFitness())
                best = individual;
//            if (individual.compareTo(best) < 0) {
//                best = individual;
//            }
        }
        return best;
    }

    public double getAverageFitness() {
        double fitnessSum = 0;
        for (I individual : individuals) {
            fitnessSum += individual.getFitness();
        }
        return fitnessSum / individuals.size();
    }

    public void addIndividual(I individual) {
        individuals.add(individual);
    }

    public I getIndividual(int index) {
        return individuals.get(index);
    }

    public I getBest() {
        return best;
    }

    public int getSize() {
        return individuals.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (I individual : individuals) {
            sb.append(individual);
        }
        return sb.toString();
    }
}
