package snake.snakeAI.ga.statistics;

import snake.snakeAI.ga.experiments.ExperimentEvent;
import snake.snakeAI.ga.GAEvent;
import snake.snakeAI.ga.GAListener;
import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.Individual;
import snake.snakeAI.ga.Problem;

public class StatisticBestInRun<I extends Individual, P extends Problem<I>> implements GAListener {

    private I bestInExperiment;

    public StatisticBestInRun() {
    }

    @Override
    public void generationEnded(GAEvent e) {
    }

    @Override
    public void runEnded(GAEvent e) {
        GeneticAlgorithm<I, P> ga = e.getSource();
        if (bestInExperiment == null || ga.getBestInRun().compareTo(bestInExperiment) > 0) {
            bestInExperiment = (I) ga.getBestInRun().clone();
        }
    }

    @Override
    public void experimentEnded(ExperimentEvent e) {
        snake.snakeAI.ga.utils.FileOperations.appendToTextFile("statistic_best_per_experiment_fitness.xls", e.getSource() + "\t" + bestInExperiment.getFitness() + "\r\n");
        snake.snakeAI.ga.utils.FileOperations.appendToTextFile("statistic_best_per_experiment.txt", "\r\n\r\n" + e.getSource() + "\r\n" + bestInExperiment);
    }
}
