package snake.snakeAI;

import gui.PanelParameters;
import snake.Environment;
import snake.SnakeAgent;
import snake.snakeAI.ga.RealVectorIndividual;
import snake.snakeAI.nn.SnakeAIAgent;

import java.util.List;

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
        Environment environment = problem.getEnvironment();
        int maxIterations = problem.getMaxIterations();
        int numSimulations = problem.getNumEvironmentSimulations();

        int movements = 0, food = 0;
        for (int i = 0; i < numSimulations; i++) {
            // generating the SnakeAIAgent and the food
            environment.initialize(i);

            // getting the agents currently on the environment
            List<SnakeAgent> agents = environment.getAgents();

            for (SnakeAgent agent : agents) {
                if (!(agent instanceof SnakeAIAgent))
                    throw new IllegalArgumentException("Operation not supported for " +
                            agent.getClass().getSimpleName());

                // setting the agent's neural network weights
                ((SnakeAIAgent) agent).setWeights(genome);
            }
            environment.simulate();
            movements += environment.getIterations();
            for (SnakeAgent agent : agents) {
                food += agent.getTailSize();
//                System.out.println(agent.getTailSize());
            }
        }

        bestMoves = (double) movements / numSimulations;
        bestTail = (double) food / numSimulations;

        return fitness = ((maxIterations * numSimulations) / 16) + (food << 8) - (movements >> 4);
//        return fitness = ((maxIterations * numSimulations) / 16) + (food << 4) - (movements >> 4);
    }

    public double[] getGenome(){
        return genome;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fitness: ");
        sb.append(fitness);
        sb.append(System.lineSeparator());
        sb.append("Food pieces eaten: ");
        sb.append(bestTail);
        sb.append(System.lineSeparator());
        sb.append("Movements: ");
        sb.append(bestMoves);

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
}
