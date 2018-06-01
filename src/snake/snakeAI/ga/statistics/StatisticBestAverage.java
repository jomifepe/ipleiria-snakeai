package snake.snakeAI.ga.statistics;

import gui.PanelParameters;
import snake.ProblemType;
import snake.snakeAI.SnakeExperimentsFactory;
import snake.snakeAI.ga.experiments.ExperimentEvent;
import snake.snakeAI.ga.GAEvent;
import snake.snakeAI.ga.GAListener;
import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.Individual;
import snake.snakeAI.ga.Problem;
import snake.snakeAI.ga.experiments.Parameter;
import snake.snakeAI.ga.utils.FileOperations;
import snake.snakeAI.ga.utils.Maths;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticBestAverage<E extends Individual, P extends Problem<E>> implements GAListener  {
    
    private final double[] values;
    private int run;
    
    public StatisticBestAverage(int numRuns) {
        values = new double[numRuns];
    }

    @Override
    public void generationEnded(GAEvent e) {    
    }

    @Override
    public void runEnded(GAEvent e) {
        GeneticAlgorithm<E, P> ga = e.getSource();
        values[run++] = ga.getBestInRun().getFitness();
    }

    @Override
    public void experimentEnded(ExperimentEvent e) {
//        ProblemType problemType = PanelParameters.getProblemType();
//        String strProblemType = problemType.name().toLowerCase();


        String filePath = "statistics/";
        String fileName = "average_fitness";

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
        xlsHeaders.append("Average\t");
        xlsHeaders.append("Standard Deviation\r\n");

        double averageFitness = Maths.average(values);
        double sd = Maths.standardDeviation(values, averageFitness);

        SimpleDateFormat time = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        Date date = new Date();

        if (!FileOperations.fileExists(xlsFullPath)) {
            FileOperations.appendToTextFile(xlsFullPath, xlsHeaders.toString());
        }
        
        FileOperations.appendToTextFile(xlsFullPath,
                time.format(date) + "\t" + e.getSource() + "\t" +
                        String.format("%.1f", averageFitness) + "\t" + String.format("%.4f", sd) + "\r\n"
        );
    }    
}
