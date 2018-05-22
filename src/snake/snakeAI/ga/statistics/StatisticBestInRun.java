package snake.snakeAI.ga.statistics;

import snake.snakeAI.SnakeIndividual;
import snake.snakeAI.ga.experiments.ExperimentEvent;
import snake.snakeAI.ga.GAEvent;
import snake.snakeAI.ga.GAListener;
import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.Individual;
import snake.snakeAI.ga.Problem;
import snake.snakeAI.ga.experiments.Parameter;
import snake.snakeAI.ga.utils.FileOperations;

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
        String filePath = "statistics/";
        String fileName = "best_per_experiment";

        /* XLS file */
        String xlsFullPath = filePath + fileName + ".xls";

        StringBuilder xlsHeaders = new StringBuilder();
        xlsHeaders.append("Population size\t");
        xlsHeaders.append("Generations\t");
        xlsHeaders.append("Selection type\t");

        Parameter selectionType = e.getSource().getFactory().getParameters("Selection");
        if (selectionType.values[selectionType.activeValueIndex].matches("tournament")) {
            xlsHeaders.append("Tournament size\t");
        }

        xlsHeaders.append("Recombination type\t");
        xlsHeaders.append("Recombination prob.\t");
        xlsHeaders.append("Mutation type\t");
        xlsHeaders.append("Mutation prob.\t");
        xlsHeaders.append("Fitness\t");
        xlsHeaders.append("Food Pieces\t");
        xlsHeaders.append("Movements\r\n");

        if (!FileOperations.fileExists(xlsFullPath)) {
            FileOperations.appendToTextFile(xlsFullPath, xlsHeaders.toString());
        }

        FileOperations.appendToTextFile(xlsFullPath,
                e.getSource() + "\t" + bestInExperiment.toString() + "\r\n"
        );

        /* TXT file */
        String txtFullPath = filePath + fileName + ".txt";

        StringBuilder txtIndividual = new StringBuilder();
        txtIndividual.append("Fitness: " + bestInExperiment.getFitness() + "\r\n");
        txtIndividual.append("Food Pieces: " + ((SnakeIndividual) bestInExperiment).getBestTail() + "\r\n");
        txtIndividual.append("Movements: " + ((SnakeIndividual) bestInExperiment).getBestMoves());

        FileOperations.appendToTextFile(txtFullPath,
                e.getSource().getFactory().prettyPrint() + "\n" + txtIndividual.toString() + "\r\n"
        );
    }
}
