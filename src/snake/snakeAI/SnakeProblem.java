package snake.snakeAI;

import snake.Environment;
import snake.EnvironmentAI;
import snake.EnvironmentNonAI;
import snake.snakeAI.ga.Problem;
import snake.snakeAI.nn.utils.ActivationFunction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SnakeProblem implements Problem<SnakeIndividual> {
    final private int environmentSize;
    final private int maxIterations;
    final private int numEnvironmentRuns;
    final private Environment environment;
    private List<Integer> numInputs = null;
    private List<Integer> numHiddenUnits = null;
    private List<Integer> numOutputs = null;
    private List<ActivationFunction> activationFunction = null;

    public SnakeProblem(int environmentSize, int maxIterations, int numEnvironmentRuns) {
        this.environmentSize = environmentSize;
        this.maxIterations = maxIterations;
        this.numEnvironmentRuns = numEnvironmentRuns;

        this.environment = new EnvironmentNonAI(environmentSize, maxIterations);
    }

    public SnakeProblem(int environmentSize, int maxIterations, int numEnvironmentRuns,
                        List<Integer> numInputs, List<Integer> numHiddenUnits, List<Integer> numOutputs,
                        List<ActivationFunction> activationFunction) {

        this.environmentSize = environmentSize;
        this.maxIterations = maxIterations;
        this.numEnvironmentRuns = numEnvironmentRuns;
        this.numInputs = numInputs;
        this.numHiddenUnits = numHiddenUnits;
        this.numOutputs = numOutputs;
        this.activationFunction = activationFunction;

        environment = new EnvironmentAI(environmentSize, maxIterations);
        ((EnvironmentAI) environment).setNNParameters(numInputs, numHiddenUnits, numOutputs, activationFunction);
    }

    @Override
    public SnakeIndividual getNewIndividual() {
        int totalGenomeSize = ((EnvironmentAI) environment).getGenomeSizes().stream().mapToInt(Integer::intValue).sum();
        return new SnakeIndividual(this, totalGenomeSize);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getNumEvironmentSimulations() {
        return numEnvironmentRuns;
    }

    public int getMaxIterations() { return maxIterations; }

    // MODIFY IF YOU DEFINE OTHER PARAMETERS
    public static SnakeProblem buildProblemFromFile(File file) throws IOException {
        int NUM_ONE_AI_OBLIGATORY_PARAMS = 4;
        int NUM_TWO_AI_OBLIGATORY_PARAMS = 7;

        java.util.Scanner f;
        try {
            f = new java.util.Scanner(file);
        } catch (FileNotFoundException e) {
            return null;
        }

        List<String> lines = new LinkedList<>();

        while (f.hasNextLine()) {
            String s = f.nextLine();
            if (!s.equals("") && !s.startsWith("//")) {
                lines.add(s);
            }
        }

        List<String> parametersValues = new LinkedList<>();
        for (String line : lines) {
            String[] tokens = line.split(":|,");
            for (int i = 1; i < tokens.length; i++) {
                parametersValues.add(tokens[i].trim());
            }
        }

        int environmentSize, maxIterations, numEnvironmentRuns;


        try {
            environmentSize = Integer.parseInt(parametersValues.get(0));
            maxIterations = Integer.parseInt(parametersValues.get(1));
            numEnvironmentRuns = Integer.parseInt(parametersValues.get(2));
        } catch (NumberFormatException e) {
            return null;
        }

        if (parametersValues.size() > NUM_ONE_AI_OBLIGATORY_PARAMS) {
            List<Integer> numInputs = new ArrayList<>();
            List<Integer> numHiddenUnits = new ArrayList<>();
            List<Integer> numOutputs = new ArrayList<>();
            List<ActivationFunction> activationFunctions = new ArrayList<>();

            try {
                numInputs.add(Integer.parseInt(parametersValues.get(3)));

                if (parametersValues.size() > NUM_TWO_AI_OBLIGATORY_PARAMS) {
                    numInputs.add(Integer.parseInt(parametersValues.get(4)));
                    numHiddenUnits.add(Integer.parseInt(parametersValues.get(5)));
                    numHiddenUnits.add(Integer.parseInt(parametersValues.get(6)));
                    numOutputs.add(Integer.parseInt(parametersValues.get(7)));
                    numOutputs.add(Integer.parseInt(parametersValues.get(8)));
                    activationFunctions.add(ActivationFunction.valueOf(parametersValues.get(9).toUpperCase()));
                    activationFunctions.add(ActivationFunction.valueOf(parametersValues.get(10).toUpperCase()));
                } else {
                    numHiddenUnits.add(Integer.parseInt(parametersValues.get(4)));
                    numOutputs.add(Integer.parseInt(parametersValues.get(5)));
                    activationFunctions.add(ActivationFunction.valueOf(parametersValues.get(6).toUpperCase()));
                }
            } catch (IllegalArgumentException e) {
                return null;
            }

            return new SnakeProblem(
                    environmentSize,
                    maxIterations,
                    numEnvironmentRuns,
                    numInputs,
                    numHiddenUnits,
                    numOutputs,
                    activationFunctions);
        }

        return new SnakeProblem(
                environmentSize,
                maxIterations,
                numEnvironmentRuns);
    }

    // MODIFY IF YOU DEFINE OTHER PARAMETERS
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Environment size: ");
        sb.append(environmentSize);
        sb.append("\n");
        sb.append("Maximum number of iterations: ");
        sb.append(maxIterations);
        sb.append("\n");
        sb.append("Number of environment simulations: ");
        sb.append(numEnvironmentRuns);

        if (environment instanceof EnvironmentAI) {
            sb.append("\n");
            sb.append("Number of inputs: ");
            sb.append(numInputs);
            sb.append("\n");
            sb.append("Number of hidden units: ");
            sb.append(numHiddenUnits);
            sb.append("\n");
            sb.append("Number of outputs: ");
            sb.append(numOutputs);
        }

        return sb.toString();
    }

}
