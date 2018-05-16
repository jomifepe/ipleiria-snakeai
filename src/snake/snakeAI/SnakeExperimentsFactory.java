package snake.snakeAI;

import snake.snakeAI.ga.experiments.*;
import snake.snakeAI.ga.GAListener;
import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.geneticOperators.*;
import snake.snakeAI.ga.selectionMethods.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import snake.snakeAI.ga.statistics.StatisticBestAverage;
import snake.snakeAI.ga.statistics.StatisticBestInRun;

public class SnakeExperimentsFactory extends ExperimentsFactory {

    private int populationSize;
    private int maxGenerations;
    private SelectionMethod<SnakeIndividual, SnakeProblem> selection;
    private Recombination<SnakeIndividual> recombination;
    private Mutation<SnakeIndividual> mutation;
    private SnakeProblem problem;
    private Experiment<SnakeExperimentsFactory, SnakeProblem> experiment;

    public SnakeExperimentsFactory(File configFile) throws IOException {
        super(configFile);
    }

    @Override
    public Experiment buildExperiment() throws IOException {
        numRuns = Integer.parseInt(getParameterValue("Runs"));
        populationSize = Integer.parseInt(getParameterValue("Population size"));
        maxGenerations = Integer.parseInt(getParameterValue("Max generations"));

        //SELECTION
        switch(getParameterValue("Selection")) {
            case "tournament":
                int tournamentSize = Integer.parseInt(getParameterValue("Tournament size"));
                selection = new Tournament<>(populationSize, tournamentSize);
                break;
            case "roullette":
                selection = new RouletteWheel<>(populationSize);
        }

        //RECOMBINATION
        double recombinationProbability = Double.parseDouble(getParameterValue("Recombination probability"));
        switch(getParameterValue("Recombination")){
            case "one_cut":
                recombination = new RecombinationOneCut<>(recombinationProbability);
                break;
            case "two_cuts":
                recombination = new RecombinationTwoCuts<>(recombinationProbability);
                break;
            case "uniform":
                recombination = new RecombinationUniform<>(recombinationProbability);
        }

        // TODO YOU MAY ADD NEW PARAMETERS (eg., NEW GENETIC OPERATORS, ...).



        //MUTATION
        double mutationProbability = Double.parseDouble(getParameterValue("Mutation probability"));
        if (getParameterValue("Mutation").equals("uniform_distribution")) {
            //TODO OTHER PARAMETERS TO YOUR MUTATION OPERATOR, IF THEY EXIST, ARE FETCHED HERE
            mutation = new MutationAddSubtract<>(mutationProbability /*TODO COMPLETE?*/);
        }

        //PROBLEM 
        problem = SnakeProblem.buildProblemFromFile(new File(getParameterValue("Problem file")));

        String textualRepresentation = buildTextualExperiment();

        experiment = new Experiment<>(this, numRuns, problem, textualRepresentation);

        statistics = new ArrayList<>();
        for (String statisticName : statisticsNames) {
            ExperimentListener statistic = buildStatistic(statisticName);
            statistics.add(statistic);
            experiment.addExperimentListener(statistic);
        }

        return experiment;
    }

    @Override
    public GeneticAlgorithm generateGAInstance(int seed) {
        GeneticAlgorithm<SnakeIndividual, SnakeProblem> ga =
                new GeneticAlgorithm<>(
                    populationSize,
                    maxGenerations,
                    selection,
                    recombination,
                    mutation,
                    new Random(seed));

        for (ExperimentListener statistic : statistics) {
            ga.addGAListener((GAListener) statistic);
        }

        return ga;
    }

    private ExperimentListener buildStatistic(String statisticName) {
        switch(statisticName){
            case "BestIndividual":
                return new StatisticBestInRun();
            case "BestAverage":
                return new StatisticBestAverage(numRuns);
        }        
        return null;
    }

    private String buildTextualExperiment() {
        StringBuilder sb = new StringBuilder();
        sb.append("Population size:" + populationSize + "\t");
        sb.append("Max generations:" + maxGenerations + "\t");
        sb.append("Selection:" + selection + "\t");
        sb.append("Recombination:" + recombination + "\t");
        sb.append("Mutation:" + mutation + "\t");
        return sb.toString();
    }
}
