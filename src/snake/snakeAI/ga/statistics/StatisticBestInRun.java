package snake.snakeAI.ga.statistics;

import gui.PanelParameters;
import snake.ProblemType;
import snake.snakeAI.SnakeIndividual;
import snake.snakeAI.ga.experiments.ExperimentEvent;
import snake.snakeAI.ga.GAEvent;
import snake.snakeAI.ga.GAListener;
import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.Individual;
import snake.snakeAI.ga.Problem;
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
        ProblemType problemType = PanelParameters.getProblemType();
        String strProblemType = problemType.name().toLowerCase();

        String filePath = "statistics/";
        String fileName = strProblemType + "_best_per_experiment";

        /* XLS file */
        String xlsFullPath = filePath + fileName + ".xls";

        StringBuilder xlsHeaders = new StringBuilder();
        xlsHeaders.append("Problem type\t");
        xlsHeaders.append("Population size\t");
        xlsHeaders.append("Generations\t");
        xlsHeaders.append("Selection type\t");
        xlsHeaders.append("Tournament size\t");
        xlsHeaders.append("Recombination type\t");
        xlsHeaders.append("Recombination prob.\t");
        xlsHeaders.append("Mutation type\t");
        xlsHeaders.append("Mutation prob.\t");
        xlsHeaders.append("Fitness\t");
        xlsHeaders.append("Food Mean\t");
        xlsHeaders.append("Food Max\t");
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
        txtIndividual.append("Food Mean: " + ((SnakeIndividual) bestInExperiment).getAvgFoods() + "\r\n");
        txtIndividual.append("Food Max: " + ((SnakeIndividual) bestInExperiment).getBestFoods() + "\r\n");
        txtIndividual.append("Movements: " + ((SnakeIndividual) bestInExperiment).getAvgMovements() + "\r\n");
        txtIndividual.append("\r\n" + "//--------------------------------" + "\r\n");

        FileOperations.appendToTextFile(txtFullPath,
                "Problem type: " + strProblemType + "\r\n" +
                        e.getSource().getFactory().prettyPrint() + "\r\n" + txtIndividual.toString() + "\r\n"
        );
    }
}
