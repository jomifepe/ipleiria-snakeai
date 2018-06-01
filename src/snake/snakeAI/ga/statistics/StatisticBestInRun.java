package snake.snakeAI.ga.statistics;

import gui.PanelParameters;
import snake.EnvironmentAI;
import snake.EnvironmentNonAI;
import snake.ProblemType;
import snake.snakeAI.SnakeExperimentsFactory;
import snake.snakeAI.SnakeIndividual;
import snake.snakeAI.ga.experiments.ExperimentEvent;
import snake.snakeAI.ga.GAEvent;
import snake.snakeAI.ga.GAListener;
import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.Individual;
import snake.snakeAI.ga.Problem;
import snake.snakeAI.ga.utils.FileOperations;
import snake.snakeAI.nn.utils.ActivationFunction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        String fileName = "best_per_experiment";

        /* XLS file */
        String xlsFullPath = filePath + fileName + ".xls";

        StringBuilder xlsHeaders = new StringBuilder();
        xlsHeaders.append("Time\t");
        xlsHeaders.append("Problem type\t");
        xlsHeaders.append("NN-I\t");
        xlsHeaders.append("NN-H\t");
        xlsHeaders.append("NN-O\t");
        xlsHeaders.append("NN-Activation\t");
        xlsHeaders.append("Population size\t");
        xlsHeaders.append("Generations\t");
        xlsHeaders.append("Selection method\t");
        xlsHeaders.append("Selection size\t");
        xlsHeaders.append("Recombination method\t");
        xlsHeaders.append("Recombination prob.\t");
        xlsHeaders.append("Mutation type\t");
        xlsHeaders.append("Mutation prob.\t");
        xlsHeaders.append("Fitness\t");
        xlsHeaders.append("Average Foods\t");
        xlsHeaders.append("Best Foods\t");
        xlsHeaders.append("Average Movements\t");
        xlsHeaders.append("Best Movements\r\n");

        SimpleDateFormat time = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        Date date = new Date();

        if (!FileOperations.fileExists(xlsFullPath)) {
            FileOperations.appendToTextFile(xlsFullPath, xlsHeaders.toString());
        }

        FileOperations.appendToTextFile(xlsFullPath,
                time.format(date) + "\t" + e.getSource() + "\t" + bestInExperiment.toString() + "\r\n"
        );

        /* TXT file */
        String txtFullPath = filePath + fileName + ".txt";

        StringBuilder txtIndividual = new StringBuilder();
        txtIndividual.append("Fitness: " + bestInExperiment.getFitness() + "\r\n");
        txtIndividual.append("Average Foods: " + ((SnakeIndividual) bestInExperiment).getAvgFoods() + "\r\n");
        txtIndividual.append("Best Foods: " + ((SnakeIndividual) bestInExperiment).getBestFoods() + "\r\n");
        txtIndividual.append("Average Movements: " + ((SnakeIndividual) bestInExperiment).getAvgMovements() + "\r\n");
        txtIndividual.append("Best Movements: " + ((SnakeIndividual) bestInExperiment).getBestMovements() + "\r\n");
        txtIndividual.append("\r\n" + "//--------------------------------" + "\r\n");

        FileOperations.appendToTextFile(txtFullPath,
                "Time: " + time.format(date) + "\r\n" +
                        e.getSource().getFactory().prettyPrint() + "\r\n" + txtIndividual.toString() + "\r\n"
        );
    }
}
