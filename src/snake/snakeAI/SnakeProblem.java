package snake.snakeAI;

import snake.Environment;
import snake.snakeAI.ga.Problem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SnakeProblem implements Problem<SnakeIndividual> {
    final private int environmentSize;
    final private int maxIterations;
    final private int numEnvironmentRuns;
    final private List<Integer> numInputs;
    final private List<Integer> numHiddenUnits;
    final private List<Integer> numOutputs;

    final private Environment environment;

    public SnakeProblem(int environmentSize, int maxIterations, int numEnvironmentRuns,
                        List<Integer> numInputs, List<Integer> numHiddenUnits, List<Integer> numOutputs) {

        this.environmentSize = environmentSize;
        this.maxIterations = maxIterations;
        this.numEnvironmentRuns = numEnvironmentRuns;
        this.numInputs = numInputs;
        this.numHiddenUnits = numHiddenUnits;
        this.numOutputs = numOutputs;

        environment = new Environment(environmentSize, maxIterations);
        environment.setNNDimensions(numInputs, numHiddenUnits, numOutputs);
    }

    @Override
    public SnakeIndividual getNewIndividual() {
        int sumInputs = numInputs.stream().mapToInt(Integer::intValue).sum();
        int sumHiddenUnits = numHiddenUnits.stream().mapToInt(Integer::intValue).sum();
        int sumOutputs = numOutputs.stream().mapToInt(Integer::intValue).sum();

        int genomeSize = (sumInputs + 1) * (sumHiddenUnits) + (sumHiddenUnits + 1) * sumOutputs;
        return new SnakeIndividual(this, genomeSize);
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
        java.util.Scanner f = new java.util.Scanner(file);

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

        int environmentSize = Integer.parseInt(parametersValues.get(0));
        int maxIterations = Integer.parseInt(parametersValues.get(1));
        int numEnvironmentRuns = Integer.parseInt(parametersValues.get(2));

        List<Integer> numInputs = new ArrayList<>();
        List<Integer> numHiddenUnits = new ArrayList<>();
        List<Integer> numOutputs = new ArrayList<>();

        numInputs.add(Integer.parseInt(parametersValues.get(3)));

        if (parametersValues.size() > 6) {
            numInputs.add(Integer.parseInt(parametersValues.get(4)));
            numHiddenUnits.add(Integer.parseInt(parametersValues.get(5)));
            numHiddenUnits.add(Integer.parseInt(parametersValues.get(6)));
            numOutputs.add(Integer.parseInt(parametersValues.get(7)));
            numOutputs.add(Integer.parseInt(parametersValues.get(8)));
        } else {
            numHiddenUnits.add(Integer.parseInt(parametersValues.get(4)));
            numOutputs.add(Integer.parseInt(parametersValues.get(5)));
        }

        return new SnakeProblem(
                environmentSize,
                maxIterations,
                numEnvironmentRuns,
                numInputs,
                numHiddenUnits,
                numOutputs);
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
        sb.append("Number of inputs: ");
        sb.append(numInputs);
        sb.append("\n");
        sb.append("Number of hidden units: ");
        sb.append(numHiddenUnits);
        sb.append("\n");
        sb.append("Number of outputs: ");
        sb.append(numOutputs);
        sb.append("\n");
        sb.append("Number of environment simulations: ");
        sb.append(numEnvironmentRuns);
        return sb.toString();
    }

}
