package gui;

import snake.EnvironmentAI;
import snake.EnvironmentNonAI;
import snake.ProblemType;
import snake.snakeAI.SnakeIndividual;
import snake.snakeAI.SnakeProblem;
import snake.snakeAI.SnakeExperimentsFactory;
import snake.snakeAI.ga.experiments.Experiment;
import snake.snakeAI.ga.experiments.ExperimentEvent;
import snake.snakeAI.ga.GAEvent;
import snake.snakeAI.ga.GAListener;
import snake.snakeAI.ga.GeneticAlgorithm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import util.ConsoleColor;
import util.ConsoleUtils;

public class MainFrame extends JFrame implements GAListener {

    private static final long serialVersionUID = 1L;
    private SnakeProblem problem;
    private GeneticAlgorithm<SnakeIndividual, SnakeProblem> ga;
    private SnakeIndividual bestInRun;
    private SnakeExperimentsFactory experimentsFactory;
    private PanelTextArea problemPanel;
    private PanelTextArea bestIndividualPanel;
    private PanelParameters panelParameters = new PanelParameters();
    private JButton buttonDataSet = new JButton("Data set");
    private JButton buttonRun = new JButton("Run");
    private JButton buttonStop = new JButton("Stop");
    private JButton buttonExperiments = new JButton("Experiments");
    private JButton buttonRunExperiments = new JButton("Run Experiments");
    private JTextField textFieldExperimentsStatus = new JTextField("", 10);
    private XYSeries seriesBestIndividual;
    private XYSeries seriesAverage;
    private SwingWorker<Void, Void> worker;

    private PanelSimulation simulationPanel;

    public MainFrame() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void jbInit() throws Exception {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("SNAKE AI");

        //North Left Panel
        JPanel panelNorthLeft = new JPanel(new BorderLayout());
        panelNorthLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        panelNorthLeft.add(panelParameters, java.awt.BorderLayout.WEST);
        JPanel panelButtons = new JPanel();
        panelButtons.add(buttonDataSet);
        buttonDataSet.addActionListener(new ButtonDataSet_actionAdapter(this));
        panelButtons.add(buttonRun);
        buttonRun.setEnabled(false);
        buttonRun.addActionListener(new ButtonRun_actionAdapter(this));
        panelButtons.add(buttonStop);
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new ButtonStop_actionAdapter(this));
        panelNorthLeft.add(panelButtons, java.awt.BorderLayout.SOUTH);

        //North Right Panel - Chart creation
        seriesBestIndividual = new XYSeries("Best");
        seriesAverage = new XYSeries("Average");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesBestIndividual);
        dataset.addSeries(seriesAverage);
        JFreeChart chart = ChartFactory.createXYLineChart("Evolution", // Title
                "generation", // x-axis Label
                "fitness", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        //North Panel
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(panelNorthLeft, java.awt.BorderLayout.WEST);
        northPanel.add(chartPanel, java.awt.BorderLayout.CENTER);

        //Center panel       
        problemPanel = new PanelTextArea("Problem data: ", 20, 40);
        bestIndividualPanel = new PanelTextArea("Best solution: ", 20, 40);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(problemPanel, java.awt.BorderLayout.WEST);
        centerPanel.add(bestIndividualPanel, java.awt.BorderLayout.CENTER);

        //South Panel
        JPanel southPanel = new JPanel();
        southPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        southPanel.add(buttonExperiments);
        buttonExperiments.addActionListener(new ButtonExperiments_actionAdapter(this));
        southPanel.add(buttonRunExperiments);
        buttonRunExperiments.setEnabled(false);
        buttonRunExperiments.addActionListener(new ButtonRunExperiments_actionAdapter(this));
        southPanel.add(new JLabel("Status: "));
        southPanel.add(textFieldExperimentsStatus);
        textFieldExperimentsStatus.setEditable(false);

        //Big left panel
        JPanel bigLeftPanel = new JPanel(new BorderLayout());
        bigLeftPanel.add(northPanel, java.awt.BorderLayout.NORTH);
        bigLeftPanel.add(centerPanel, java.awt.BorderLayout.CENTER);
        bigLeftPanel.add(southPanel, java.awt.BorderLayout.SOUTH);
        this.getContentPane().add(bigLeftPanel);

        simulationPanel = new PanelSimulation(this);

        //Global structure
        JPanel globalPanel = new JPanel(new BorderLayout());
        globalPanel.add(bigLeftPanel, java.awt.BorderLayout.WEST);
        globalPanel.add(simulationPanel, java.awt.BorderLayout.EAST);
        this.getContentPane().add(globalPanel);

        pack();
    }

    public SnakeProblem getProblem() {
        return problem;
    }

    public SnakeIndividual getBestInRun() {
        return bestInRun;
    }

    public PanelParameters getPanelParameters() {
        return panelParameters;
    }

    public PanelSimulation getSimulationPanel() {
        return simulationPanel;
    }

    public void buttonDataSet_actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(new java.io.File("."));
        int returnVal = fc.showOpenDialog(this);

        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File dataSet = fc.getSelectedFile();
                problem = SnakeProblem.buildProblemFromFile(dataSet);

                if (problem == null) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid dataset, check you parameters", "Error!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (problem.getEnvironment() instanceof EnvironmentAI &&
                        PanelParameters.getProblemType().ordinal() < ProblemType.ONE_AI.ordinal()) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid dataset for non-AI agent, change the problem type", "Error!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (problem.getEnvironment() instanceof EnvironmentNonAI &&
                        PanelParameters.getProblemType().ordinal() > ProblemType.ADHOC.ordinal()) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid dataset for AI agent, change the problem type", "Error!", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                problemPanel.textArea.setText(problem.toString());
                problemPanel.textArea.setCaretPosition(0);
                buttonRun.setEnabled(true);

                panelParameters.warnCBSnakeTypeListeners();
            }
        } catch (IOException e1) {
            e1.printStackTrace(System.err);
        } catch (java.util.NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void jButtonRun_actionPerformed(ActionEvent e) {
        try {
            if (problem == null) {
                JOptionPane.showMessageDialog(this, "You must first choose a problem", "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (PanelParameters.getCBProblemTypeSelectedIndex() < ProblemType.ONE_AI.ordinal())
                throw new IllegalArgumentException();

            bestIndividualPanel.textArea.setText("");
            seriesBestIndividual.clear();
            seriesAverage.clear();

            Random random = new Random();
            if (PanelParameters.isCustomSeedCheckBoxChecked()) {
                random.setSeed(PanelParameters.getSeedValue());
            }

            ga = new GeneticAlgorithm<>(
                    Integer.parseInt(panelParameters.textFieldN.getText()),
                    Integer.parseInt(panelParameters.textFieldGenerations.getText()),
                    panelParameters.getSelectionMethod(),
                    panelParameters.getRecombinationMethod(),
                    panelParameters.getMutationMethod(),
                    random);

            System.out.println(ga);

            ga.addGAListener(this);

            manageButtons(false, false, true, false, false, false);

            worker = new SwingWorker<Void, Void>() {
                @Override
                public Void doInBackground() {
                    ConsoleUtils.println(ConsoleColor.BRIGHT_GREEN, "<run worker started>");

                    try {
                        bestInRun = ga.run(problem);
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                    return null;
                }

                @Override
                public void done() {
                    manageButtons(true, true, false, true,
                            experimentsFactory != null, true);
                    panelParameters.warnCBSnakeTypeListeners();
                    ConsoleUtils.println(ConsoleColor.BRIGHT_RED, "<run worker done>");
                }
            };

            worker.execute();

        } catch (NumberFormatException e1) {
            JOptionPane.showMessageDialog(this, "Wrong parameters!", "Error!", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e2) {
            JOptionPane.showMessageDialog(this, "Operation not supported for non-AI agents", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void generationEnded(GAEvent e) {
        GeneticAlgorithm<SnakeIndividual, SnakeProblem> source = e.getSource();

        bestIndividualPanel.textArea.setText(source.getBestInRun().prettyPrint());

        seriesBestIndividual.add(source.getGeneration(), source.getBestInRun().getFitness());
        seriesAverage.add(source.getGeneration(), source.getAverageFitness());

        ((EnvironmentAI) problem.getEnvironment()).setBestInRun(source.getBestInRun().clone());

        if (worker.isCancelled()) {
            e.setStopped(true);
        }
    }

    @Override
    public void runEnded(GAEvent e) {
    }

    public void jButtonStop_actionPerformed(ActionEvent e) {
        worker.cancel(true);
    }

    public void buttonExperiments_actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(new java.io.File("."));
        int returnVal = fc.showOpenDialog(this);

        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                experimentsFactory = new SnakeExperimentsFactory(fc.getSelectedFile());
//                problemPanel.textArea.setText(experiment.prettyPrint());
//                problemPanel.textArea.setCaretPosition(0);

                manageButtons(true, problem != null, false, true, true, false);
            }
        } catch (IOException e1) {
            e1.printStackTrace(System.err);
        } catch (java.util.NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isDatasetLoaded() {
        return problem != null;
    }

    public void buttonRunExperiments_actionPerformed(ActionEvent e) {
        manageButtons(false, false, false, false, false, false);
        textFieldExperimentsStatus.setText("Running");

        worker = new SwingWorker<Void, Void>() {
            boolean error = false;

            @Override
            public Void doInBackground() {

                try {
                    while (experimentsFactory.hasMoreExperiments()) {
                        try {
                            Experiment experiment = experimentsFactory.nextExperiment();
                            if (experiment == null) {
                                error = true;
                                return null;
                            }

                            ProblemType problemType = ((SnakeExperimentsFactory) experiment.getFactory()).getProblemType();
                            PanelParameters.setCBSelectionProblemType(problemType);

                            experiment.run();

                        } catch (IOException e1) {
                            e1.printStackTrace(System.err);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
                return null;
            }

            @Override
            public void done() {
                manageButtons(true, problem != null, false, true, false, false);
                textFieldExperimentsStatus.setText(error ? "Failed, check file" : "Finished");
            }
        };
        worker.execute();
    }

    @Override
    public void experimentEnded(ExperimentEvent e) {
    }

    private void manageButtons(
            boolean dataSet,
            boolean run,
            boolean stopRun,
            boolean experiments,
            boolean runExperiments,
            boolean runEnvironment) {

        buttonDataSet.setEnabled(dataSet);
        buttonRun.setEnabled(run);
        buttonStop.setEnabled(stopRun);
        buttonExperiments.setEnabled(experiments);
        buttonRunExperiments.setEnabled(runExperiments);
        simulationPanel.setJButtonSimulateEnabled(runEnvironment);
    }
}

class PanelTextArea extends JPanel {

    JTextArea textArea;

    public PanelTextArea(String title, int rows, int columns) {
        textArea = new JTextArea(rows, columns);
        setLayout(new BorderLayout());
        add(new JLabel(title), java.awt.BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        add(scrollPane);
    }
}

class ButtonDataSet_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonDataSet_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.buttonDataSet_actionPerformed(e);
    }
}

class ButtonRun_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonRun_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRun_actionPerformed(e);
    }
}

class ButtonStop_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonStop_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonStop_actionPerformed(e);
    }
}

class ButtonExperiments_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonExperiments_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.buttonExperiments_actionPerformed(e);
    }
}

class ButtonRunExperiments_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonRunExperiments_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.buttonRunExperiments_actionPerformed(e);
    }
}
