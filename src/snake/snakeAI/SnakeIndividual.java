package snake.snakeAI;

import gui.PanelParameters;
import snake.EnvironmentAI;
import snake.ProblemType;
import snake.SnakeAgent;
import snake.snakeAI.ga.RealVectorIndividual;

import java.util.ArrayList;
import java.util.List;

public class SnakeIndividual extends RealVectorIndividual<SnakeProblem, SnakeIndividual> {

    private double avgMovements;
    private double avgFoods;
    private int bestMovements;
    private int bestFoods;
    private int numBestSimulation;
    private int numSimulations;

    public SnakeIndividual(SnakeProblem problem, int size) {
        super(problem, size);
    }

    public SnakeIndividual(SnakeIndividual original) {
        super(original);
        this.avgMovements = original.avgMovements;
        this.bestMovements = original.bestMovements;
        this.avgFoods = original.avgFoods;
        this.bestFoods = original.bestFoods;
        this.numBestSimulation = original.numBestSimulation;
        this.numSimulations = original.numSimulations;
    }

    @Override
    public double computeFitness() {
        if (!(this.problem.getEnvironment() instanceof EnvironmentAI)) {
            throw new IllegalStateException("Invalid environment type");
        }

        EnvironmentAI environment = (EnvironmentAI) this.problem.getEnvironment();
        numSimulations = problem.getNumEvironmentSimulations();
        List<SnakeAgent> agents = new ArrayList<>();
        int stepsTakenSinceLastFood = 0;

        int penalty = 0; // used with two different snakes
        int[] totalIndividualSnakeFoods = {0, 0};
        int[] totalIndividualSnakeMovements = {0, 0};

        bestFoods = 0;
        int movements = 0, food = 0;
        for (int i = 0; i < numSimulations; i++) {
            /* generating the SnakeAIAgent and the food */
            environment.initialize(i);
            /* getting the agents currently on the environment */
            agents = environment.getAgents();
            /* setting the agent's neural network weights */
            environment.setWeights(genome);
            /* testing the agent(s) performance with the new weights */
            environment.simulate();

            int auxAgentsFoodSum = 0;
            int auxAgentsMovementsSum = 0;
            for (int j = 0; j < agents.size(); j++) {
                SnakeAgent agent = agents.get(j);
                int currentAgentFood = agent.getTailSize();
                int currentAgentMovements = agent.getMovements();

                /* only used for penalty --------------------------------------- */
                    totalIndividualSnakeFoods[j] += currentAgentFood;
                    totalIndividualSnakeMovements[j] += currentAgentMovements;
                /* ------------------------------------------------------------- */

                food += currentAgentFood;
                movements += currentAgentMovements;
                stepsTakenSinceLastFood += agent.getStepsTakenSinceLastFood();

                auxAgentsFoodSum += currentAgentFood;
                auxAgentsMovementsSum += currentAgentMovements;
            }

            if (auxAgentsFoodSum > bestFoods) {
                bestFoods = auxAgentsFoodSum;
                bestMovements = auxAgentsMovementsSum;
                numBestSimulation = i + 1;
            }
        }

        avgMovements = (double) movements / numSimulations;
        avgFoods = (double) food / numSimulations;
        stepsTakenSinceLastFood = stepsTakenSinceLastFood / numSimulations;

        boolean stalling = stepsTakenSinceLastFood > (agents.size() * 100);

        /* penalty to prevent one of the two different snakes from slacking */
        if (PanelParameters.getProblemType() == ProblemType.TWO_DIFFERENT_AI) {
            penalty = (Math.abs(totalIndividualSnakeFoods[0] - totalIndividualSnakeFoods[1]) << 11) +
                    (Math.abs(totalIndividualSnakeMovements[0] - totalIndividualSnakeMovements[1]) << (stalling ? 4 : 5));
        }

        boolean penalizeSlackingAgents = PanelParameters.isPenalizationCheckBoxChecked();
        return fitness = (food << 11) - (((double) movements) / (stalling ? 16.0 : 32.0)) - (penalizeSlackingAgents ? penalty : 0);
    }

    public double[] getGenome(){
        return genome;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format( "%.1f", fitness)).append("\t");
        sb.append(String.format( "%.1f", avgFoods)).append("\t");
        sb.append(bestFoods).append("\t");
        sb.append(String.format( "%.1f", avgMovements)).append("\t");;
        sb.append(bestMovements);

        return sb.toString();
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("Solution found on simulation ").append(numBestSimulation).append("/").append(numSimulations);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());

        sb.append("▪  Fitness: ").append(String.format( "%.1f", fitness));
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());

        sb.append("Food pieces eaten: ");
        sb.append(System.lineSeparator());
        sb.append("▪  Average from 10 simulations: ").append(String.format( "%.1f", avgFoods));
        sb.append(System.lineSeparator());
        sb.append("▪  Best run: ").append(bestFoods);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());

        sb.append("Movements: ");
        sb.append(System.lineSeparator());
        sb.append("▪  Average from 10 simulations: ").append(String.format("%.1f", avgMovements));
        sb.append(System.lineSeparator());
        sb.append("▪  Best run: ").append(bestMovements);

        return sb.toString();
    }

    /**
     *
     * @param i
     * @return 1 if this object is BETTER than i, -1 if it is WORST than I and
     * 0, otherwise.
     */
    @Override
    public int compareTo(SnakeIndividual i) {
        return Double.compare(this.fitness, i.fitness);
    }

    @Override
    public SnakeIndividual clone() {
        return new SnakeIndividual(this);
    }

    public double getAvgMovements() {
        return avgMovements;
    }

    public double getAvgFoods() {
        return avgFoods;
    }

    public int getBestFoods() {
        return bestFoods;
    }

    public int getBestMovements() {
        return bestMovements;
    }

    public int getNumBestSimulation() {
        return numBestSimulation;
    }

    public int getNumSimulations() {
        return numSimulations;
    }
}
