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

    public SnakeIndividual(SnakeProblem problem, int size /*TODO?*/) {
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

    /*
    para cada simulação (utilizar var de iteração como seed do random)
        ir ao genoma buscar os pesos das sinapses e colocá-los na RN (setWeights)
        mandar a snakeAI decidir
            colocar os inputs com os valores percecionados
            mandar executar o forwardpropagation
            observar os valores dos outputs
            decidir ação
        manda a cobra iterar o máximo de x vezes
        recolhe métricas (e.g, comidas, iterações, ...)

    atribuir e devolver a fitness (valorizar mais as comidas que as iterações)
    */

    @Override
    public double computeFitness() {
        if (!(this.problem.getEnvironment() instanceof EnvironmentAI)) {
            throw new IllegalStateException("Invalid environment type");
        }

        EnvironmentAI environment = (EnvironmentAI) this.problem.getEnvironment();
        numSimulations = problem.getNumEvironmentSimulations();
        List<SnakeAgent> agents;
        int stepsTakenSinceLastFood = 0;

        int penalty = 0; // used with two different snakes
        int[] totalIndividualSnakeFoods = {0, 0};
        int[] totalIndividualSnakeMovements = {0, 0};

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

            int auxAgentsFoodSum = 0, auxAgentsMovementsSum = 0;
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

        boolean stalling = stepsTakenSinceLastFood > 100;

        /* penalty to prevent one of the two different snakes from slacking */
        if (PanelParameters.getProblemType() == ProblemType.TWO_DIFFERENT_AI) {
            penalty = (Math.abs(totalIndividualSnakeFoods[0] - totalIndividualSnakeFoods[1]) << 11) +
                    (Math.abs(totalIndividualSnakeMovements[0] - totalIndividualSnakeMovements[1]) << (stalling ? 2 : 5));
        }

//        ConsoleUtils.println(stalling ? ConsoleColor.BRIGHT_RED : ConsoleColor.BRIGHT_GREEN, String.valueOf(stepsTakenSinceLastFood));
        return fitness = (food << 11) - (movements >> (stalling ? 2 : 5)) - penalty;
    }

    public double[] getGenome(){
        return genome;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format( "%.1f", fitness) + "\t");
        sb.append(String.format( "%.1f", avgFoods) + "\t");
        sb.append(bestFoods + "\t");
        sb.append(String.format( "%.1f", avgMovements));

        return sb.toString();
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("Solution found on simulation ").append(numBestSimulation).append("/").append(numSimulations);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());

        sb.append("▪  Fitness: ").append(fitness);
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
}
