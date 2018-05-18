package snake.snakeAI.ga.statistics;

import snake.snakeAI.ga.experiments.ExperimentEvent;
import snake.snakeAI.ga.GAEvent;
import snake.snakeAI.ga.GAListener;
import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.Individual;
import snake.snakeAI.ga.Problem;
import snake.snakeAI.ga.experiments.Parameter;
import snake.snakeAI.ga.utils.FileOperations;
import snake.snakeAI.ga.utils.Maths;

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
        String filePath = "statistics/";
        String fileName = "average_fitness";

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
        xlsHeaders.append("Average\t");
        xlsHeaders.append("Standard Deviation\r\n");

        double averageFitness = Maths.average(values);
        double sd = Maths.standardDeviation(values, averageFitness);

        if (!FileOperations.fileExists(xlsFullPath))
            FileOperations.appendToTextFile(xlsFullPath, xlsHeaders.toString());
        
        FileOperations.appendToTextFile(xlsFullPath,
                e.getSource() + "\t" + averageFitness + "\t" + sd + "\r\n"
        );
    }    
}
