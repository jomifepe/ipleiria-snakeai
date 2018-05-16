package snake.snakeAI;

import snake.Environment;
import snake.snakeAI.ga.Problem;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SnakeProblem implements Problem<SnakeIndividual> {
    final private int environmentSize;
    final private int maxIterations;
    final private int numInputs;
    final private int numHiddenUnits;
    final public int numOutputs;
    final private int numEnvironmentRuns;

    final private Environment environment;

    public SnakeProblem(int environmentSize, int maxIterations,
                        int numInputs, int numHiddenUnits,
                        int numOutputs, int numEnvironmentRuns) {

        this.environmentSize = environmentSize;
        this.maxIterations = maxIterations;
        this.numInputs = numInputs;
        this.numHiddenUnits = numHiddenUnits;
        this.numOutputs = numOutputs;
        this.numEnvironmentRuns = numEnvironmentRuns;

        environment = new Environment(environmentSize, maxIterations);
        environment.setNNDimensions(numInputs, numHiddenUnits, numOutputs);
    }

    @Override
    public SnakeIndividual getNewIndividual() {
        int genomeSize = (numInputs + 1) * (numHiddenUnits) + (numHiddenUnits + 1 /* bias */) * numOutputs;
        return new SnakeIndividual(this, genomeSize /*TODO?*/);
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
            String[] tokens = line.split(":");
            parametersValues.add(tokens[1].trim());
        }

        int environmentSize = Integer.parseInt(parametersValues.get(0));
        int maxIterations = Integer.parseInt(parametersValues.get(1));
        int numInputs = Integer.parseInt(parametersValues.get(2));
        int numHiddenUnits = Integer.parseInt(parametersValues.get(3));
        int numOutputs = Integer.parseInt(parametersValues.get(4));
        int numEnvironmentRuns = Integer.parseInt(parametersValues.get(5));

        return new SnakeProblem(
                environmentSize,
                maxIterations,
                numInputs,
                numHiddenUnits,
                numOutputs,
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
