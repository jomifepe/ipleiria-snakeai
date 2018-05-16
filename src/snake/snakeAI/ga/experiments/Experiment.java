package snake.snakeAI.ga.experiments;

import snake.snakeAI.ga.GeneticAlgorithm;
import snake.snakeAI.ga.Problem;
import java.util.ArrayList;
import java.util.List;

public class Experiment <E extends ExperimentsFactory, P extends Problem>{

    private final E factory;
    private final int numRuns;
    private GeneticAlgorithm ga;
    private final P problem;
    private final String textualRepresentation;

    public Experiment(E factory, int numRuns, P problem, String textualRepresentation) {
        this.factory = factory;
        this.numRuns = numRuns;
        this.problem = problem;
        this.textualRepresentation = textualRepresentation;
    }

    public void run() {
        for (int run = 0; run < numRuns; run++) {
            ga = factory.generateGAInstance(run + 1);
            ga.run(problem);
            System.out.println((run + 1) + " - " + ga.getBestInRun());
        }
        System.out.println("----------------------------");
        fireExperimentEnded();
    }

    //listeners
    final private List<ExperimentListener> listeners = new ArrayList<>(10);

    public synchronized void addExperimentListener(ExperimentListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void fireExperimentEnded() {
        for (ExperimentListener listener : listeners) {
            listener.experimentEnded(new ExperimentEvent(this));
        }
    }
    
    @Override
    public String toString(){
        return textualRepresentation;
    }
}
