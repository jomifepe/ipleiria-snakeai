package snake.snakeAI;

import gui.PanelParameters;
import snake.Environment;
import snake.EnvironmentAI;
import snake.ProblemType;
import snake.SnakeAgent;
import snake.snakeAI.ga.RealVectorIndividual;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SnakeIndividual extends RealVectorIndividual<SnakeProblem, SnakeIndividual> {

    private double movementsMean;
    private int movementsMax;
    private double foodMean;
    private int foodMax;

    public SnakeIndividual(SnakeProblem problem, int size /*TODO?*/) {
        super(problem, size);
    }

    public SnakeIndividual(SnakeIndividual original) {
        super(original);
        this.movementsMean = original.movementsMean;
        this.movementsMax = original.movementsMax;
        this.foodMean = original.foodMean;
        this.foodMax = original.foodMax;
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
        int numSimulations = problem.getNumEvironmentSimulations();
        List<SnakeAgent> agents = new ArrayList<>();
        int stepsTakenSinceLastFood = 0;

        int penalty = 0; // used with two different snakes
        int[] totalIndividualSnakeFoods = {0, 0};
        int[] totalIndividualSnakeMovements = {0, 0};

        int movements = 0, food = 0;
        for (int i = 0; i < numSimulations; i++) {
            /* generating the SnakeAIAgent and the food */
            Environment.random.setSeed(i);
            environment.initialize();
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

            if (auxAgentsFoodSum > foodMax) {
                foodMax = auxAgentsFoodSum;
                movementsMax = auxAgentsMovementsSum;
            }
        }

        /* penalty to prevent one of the two different snakes from slacking */
        if (PanelParameters.getProblemType() == ProblemType.TWO_DIFFERENT_AI) {
            if(totalIndividualSnakeFoods[0]  != totalIndividualSnakeFoods[1])
                penalty = Math.abs(totalIndividualSnakeFoods[0] - totalIndividualSnakeFoods[1]) << 10;
            if(totalIndividualSnakeMovements[0] != totalIndividualSnakeMovements[1])
                penalty += Math.abs(totalIndividualSnakeMovements[0] - totalIndividualSnakeMovements[1]) << 5;

//            penalty = (Math.abs(totalIndividualSnakeFoods[0] - totalIndividualSnakeFoods[1]) << 10) +
//                    (Math.abs(totalIndividualSnakeMovements[0] - totalIndividualSnakeMovements[1]) << 5);
        }

        movementsMean = (double) movements / numSimulations;
        foodMean = (double) food / numSimulations;
        stepsTakenSinceLastFood = stepsTakenSinceLastFood / (numSimulations + agents.size());

        boolean stalling = stepsTakenSinceLastFood > 100;
        return fitness = (food << 10) - (movements >> (stalling ? 1 : 5)) - penalty;
    }

    public double[] getGenome(){
        return genome;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format( "%.1f", fitness) + "\t");
        sb.append(String.format( "%.1f", foodMean) + "\t");
        sb.append(foodMax + "\t");
        sb.append(String.format( "%.1f", movementsMean));

        return sb.toString();
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fitness: ");
        sb.append(fitness);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Food pieces eaten: ");
        sb.append(System.lineSeparator());
        sb.append("▪  Average from 10 simulations: ");
        sb.append(String.format( "%.1f", foodMean));
        sb.append(System.lineSeparator());
        sb.append("▪  Best run: ");
        sb.append(foodMax);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Movements: ");
        sb.append(System.lineSeparator());
        sb.append("▪  Average from 10 simulations: ");
        sb.append(String.format("%.1f", movementsMean));
        sb.append(System.lineSeparator());
        sb.append("▪  Best run: ");
        sb.append(movementsMax);

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

    public double getMovementsMean() {
        return movementsMean;
    }

    public double getFoodMean() {
        return foodMean;
    }

    public int getFoodMax() {
        return foodMax;
    }
}
