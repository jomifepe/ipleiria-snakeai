package snake.snakeAI;

import snake.ProblemType;
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
    private double recombinationProbability;
    private double mutationProbability;
    private int tournamentSize = -1;
    private ProblemType problemType;
    private double delta;

    public SnakeExperimentsFactory(File configFile) throws IOException {
        super(configFile);
    }

    @Override
    public Experiment buildExperiment() throws IOException {
        numRuns = Integer.parseInt(getParameterValue("Runs"));
        populationSize = Integer.parseInt(getParameterValue("Population size"));
        maxGenerations = Integer.parseInt(getParameterValue("Max generations"));

        // PROBLEM TYPE
        switch (getParameterValue("Problem type")) {
            case "one_ai":
                problemType = ProblemType.ONE_AI;
                break;
            case "two_identical_ai":
                problemType = ProblemType.TWO_IDENTICAL_AI;
                break;
            case "two_different_ai":
                problemType = ProblemType.TWO_DIFFERENT_AI;
                break;
        }

        //SELECTION
        switch(getParameterValue("Selection")) {
            case "tournament":
                tournamentSize = Integer.parseInt(getParameterValue("Tournament size"));
                selection = new Tournament<>(populationSize, tournamentSize);
                break;
            case "roullette":
                selection = new RouletteWheel<>(populationSize);
        }

        //RECOMBINATION
        recombinationProbability = Double.parseDouble(getParameterValue("Recombination probability"));
        switch(getParameterValue("Recombination")){
            case "one_cut":
                recombination = new RecombinationOneCut<>(recombinationProbability);
                break;
            case "two_cuts":
                recombination = new RecombinationTwoCuts<>(recombinationProbability);
                break;
            case "uniform":
                recombination = new RecombinationUniform<>(recombinationProbability);
                break;
        }

        //MUTATION
        mutationProbability = Double.parseDouble(getParameterValue("Mutation probability"));
        delta = Double.parseDouble(getParameterValue("Delta"));
        switch (getParameterValue("Mutation")) {
            case "gaussian":
                mutation = new MutationGaussian<>(mutationProbability, delta);
                break;
            case "add_or_subtract":
                mutation = new MutationAddOrSubtract<>(mutationProbability, delta);
                break;
        }

        //PROBLEM 
        problem = SnakeProblem.buildProblemFromFile(new File(getParameterValue("Problem file")));

        String experimentValuesString = getExperimentValuesString();

        experiment = new Experiment<>(this, numRuns, problem, experimentValuesString);

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

    public SnakeProblem getProblem() {
        return problem;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    private String getExperimentValuesString() {
        StringBuilder sb = new StringBuilder();
        sb.append(problemType.name().toLowerCase()).append("\t");
        sb.append(populationSize).append("\t");
        sb.append(maxGenerations).append("\t");
        sb.append(selection).append("\t");

        if (selection instanceof Tournament) {
            sb.append(tournamentSize).append("\t");
        } else {
            sb.append("-\t");
        }

        sb.append(recombination).append("\t");
        sb.append(String.format( "%.1f", recombinationProbability)).append("\t");
        sb.append(mutation).append("\t");
        sb.append(mutationProbability);

        return sb.toString();
    }

    @Override
    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("Population size: " + populationSize + System.lineSeparator());
        sb.append("Generations: " + maxGenerations + System.lineSeparator());
        sb.append("Selection type: " + selection + System.lineSeparator());

        if (selection instanceof Tournament)
            sb.append("Selection size: " + tournamentSize + System.lineSeparator());

        sb.append("Recombination type: " + recombination + System.lineSeparator());
        sb.append("Recombination prob.: " + recombinationProbability + System.lineSeparator());
        sb.append("Mutation type: " + mutation + System.lineSeparator());
        sb.append("Mutation prob.: " + mutationProbability);

        return sb.toString();
    }
}
