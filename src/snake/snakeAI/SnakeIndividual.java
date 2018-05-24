package snake.snakeAI;

import gui.PanelParameters;
import snake.Environment;
import snake.ProblemType;
import snake.SnakeAgent;
import snake.snakeAI.ga.RealVectorIndividual;
import snake.snakeAI.nn.SnakeAIAgent;
import util.ConsoleColor;
import util.ConsoleUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SnakeIndividual extends RealVectorIndividual<SnakeProblem, SnakeIndividual> {

    private double weight;
    private double bestMoves;
    private double bestTail;

    public SnakeIndividual(SnakeProblem problem, int size /*TODO?*/) {
        super(problem, size);
    }

    public SnakeIndividual(SnakeIndividual original) {
        super(original);
        this.weight = original.weight;
        this.bestMoves = original.bestMoves;
        this.bestTail = original.bestTail;
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
        Environment environment = this.problem.getEnvironment();
        int numSimulations = problem.getNumEvironmentSimulations();
        List<SnakeAgent> agents = new ArrayList<>();
        int stepsTakenSinceLastFood = 0;

        int movements = 0, food = 0;
        for (int i = 0; i < numSimulations; i++) {
            /* generating the SnakeAIAgent and the food */
            environment.initialize(i);

            /* getting the agents currently on the environment */
            agents = environment.getAgents();

            for (SnakeAgent agent : agents) {
                if (!(agent instanceof SnakeAIAgent))
                    throw new IllegalArgumentException("Operation not supported for " +
                            agent.getClass().getSimpleName());

                /* setting the agent's neural network weights */
                ((SnakeAIAgent) agent).setWeights(genome);
            }

            environment.simulate();
            movements += environment.getIterations();
            for (SnakeAgent agent : agents) {
                food += agent.getTailSize();
                stepsTakenSinceLastFood += agent.getStepsTakenSinceLastFood();
            }
        }

        bestMoves = (double) movements / numSimulations;
        bestTail = (double) food / numSimulations;

        boolean stalling = stepsTakenSinceLastFood > (100 * agents.size());
        ConsoleUtils.println(stalling ? ConsoleColor.BRIGHT_RED : ConsoleColor.BRIGHT_GREEN, String.valueOf(stepsTakenSinceLastFood));

        return fitness = (food << 10) - (movements >> (stalling ? 3 : 5));
    }

    public double[] getGenome(){
        return genome;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fitness + "\t");
        sb.append(bestTail + "\t");
        sb.append(bestMoves);

        return sb.toString();
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fitness: ");
        sb.append(fitness);
        sb.append(System.lineSeparator());
        sb.append("Food pieces eaten: ");
        sb.append(String.format(Locale.US, "%.1f", bestTail));
        sb.append(System.lineSeparator());
        sb.append("Movements: ");
        sb.append(String.format(Locale.US, "%.1f", bestMoves));

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

    public double getBestMoves() {
        return bestMoves;
    }

    public double getBestTail() {
        return bestTail;
    }
}
