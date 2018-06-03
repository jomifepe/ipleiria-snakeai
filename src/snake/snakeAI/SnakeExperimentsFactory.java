package snake.snakeAI;

import snake.EnvironmentAI;
import snake.EnvironmentNonAI;
import snake.ProblemType;
import snake.snakeAI.ga.experiments.*;
import snake.snakeAI.ga.GAListener;
import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.geneticOperators.*;
import snake.snakeAI.ga.selectionMethods.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

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
                break;
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
            case "random_gene_swap":
                mutation = new MutationRandomGeneSwap<>(mutationProbability);
                break;
        }

        //PROBLEM 
        problem = SnakeProblem.buildProblemFromFile(new File(getParameterValue("Problem file")));
        if (problem == null) {
            return null;
        }

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
        EnvironmentAI environment = (EnvironmentAI) problem.getEnvironment();
        /* separate the values with commas */
        String nnInputs = environment.getNumNNInputs()
                .stream().map(Object::toString).collect(Collectors.joining(", "));
        String nnHidden = environment.getNumNNHidden()
                .stream().map(Object::toString).collect(Collectors.joining(", "));
        String nnOutputs = environment.getNumNNOutputs()
                .stream().map(Object::toString).collect(Collectors.joining(", "));
        String nnActivation = environment.getActivationFunctions()
                .stream().map(Object::toString).collect(Collectors.joining(", "));

        StringBuilder sb = new StringBuilder();
        sb.append(problemType.name().toLowerCase()).append("\t");
        sb.append(nnInputs).append("\t");
        sb.append(nnHidden).append("\t");
        sb.append(nnOutputs).append("\t");
        sb.append(nnActivation).append("\t");
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
        EnvironmentAI environment = (EnvironmentAI) problem.getEnvironment();
        /* separate the values with commas */
        String nnInputs = environment.getNumNNInputs()
                .stream().map(Object::toString).collect(Collectors.joining(", "));
        String nnHidden = environment.getNumNNHidden()
                .stream().map(Object::toString).collect(Collectors.joining(", "));
        String nnOutputs = environment.getNumNNOutputs()
                .stream().map(Object::toString).collect(Collectors.joining(", "));
        String nnActivation = environment.getActivationFunctions()
                .stream().map(Object::toString).collect(Collectors.joining(", "));

        StringBuilder sb = new StringBuilder();
        sb.append("Problem type: ").append(problemType.name().toLowerCase()).append("\r\n");
        sb.append("NN inputs units: ").append(nnInputs).append("\r\n");
        sb.append("NN hidden units: ").append(nnHidden).append("\r\n");
        sb.append("NN output units: ").append(nnOutputs).append("\r\n");
        sb.append("NN activation function(s): ").append(nnActivation).append("\r\n");
        sb.append("Population size: ").append(populationSize).append("\r\n");
        sb.append("Generations: ").append(maxGenerations).append("\r\n");
        sb.append("Selection method: ").append(selection).append("\r\n");

        if (selection instanceof Tournament)
            sb.append("Selection size: ").append(tournamentSize).append("\r\n");

        sb.append("Recombination type: ").append(recombination).append("\r\n");
        sb.append("Recombination prob.: ").append(recombinationProbability).append("\r\n");
        sb.append("Mutation type: ").append(mutation).append("\r\n");
        sb.append("Mutation prob.: ").append(mutationProbability);

        return sb.toString();
    }
}
