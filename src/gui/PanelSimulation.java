package gui;

import snake.Environment;
import snake.EnvironmentListener;
import util.ConsoleColor;
import util.ConsoleUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class PanelSimulation extends JPanel implements EnvironmentListener, CBSnakeTypeListener {

    public static final int PANEL_SIZE = 250;
    public static final int CELL_SIZE = 20;
    public static final int GRID_TO_PANEL_GAP = 20;
    public static final int UPDATE_INTERVAL = 150;
    MainFrame mainFrame;
    private Environment environment;
    private Image image;
    JPanel environmentPanel = new JPanel();
    JLabel simulationInfoLabel = new JLabel("Simulation no.:");
    JLabel simulationInfoNumber = new JLabel("");
    final JButton buttonSimulate = new JButton("Simulate");

    private SwingWorker<Void, Void> worker;

    public PanelSimulation(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.worker = null;

        environmentPanel.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setLayout(new BorderLayout());

        add(environmentPanel, java.awt.BorderLayout.NORTH);
        add(simulationInfoLabel, BorderLayout.LINE_START);
        add(simulationInfoNumber, BorderLayout.LINE_END);
        setSimulationInfoVisible(false);
        add(buttonSimulate, java.awt.BorderLayout.SOUTH);
        buttonSimulate.addActionListener(new SimulationPanel_jButtonSimulate_actionAdapter(this));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        mainFrame.getPanelParameters().addCBSnakeTypeListener(this);
        mainFrame.getPanelParameters().warnCBSnakeTypeListeners();
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setJButtonSimulateEnabled(boolean enabled) {
        buttonSimulate.setEnabled(enabled);
    }

    public void setSimulationInfoVisible(boolean flag) {
        simulationInfoLabel.setVisible(flag);
        simulationInfoNumber.setVisible(flag);
    }

    public void setSimulationInfoNumberText(String text) {
        this.simulationInfoNumber.setText(text);
    }

    public void jButtonSimulate_actionPerformed(ActionEvent e) {

        environment = mainFrame.getProblem().getEnvironment();
        environment.addEnvironmentListener(this);

        buildImage(environment);

        final PanelSimulation simulationPanel = this;

        if (worker != null && !worker.isDone()) {
            environment.removeEnvironmentListener(simulationPanel);
            worker.cancel(true);
        }

        worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                ConsoleUtils.println(ConsoleColor.BRIGHT_GREEN, "<simulate worker started>");
                try {
                    setSimulationInfoVisible(true);

                    int environmentSimulations = mainFrame.getProblem().getNumEvironmentSimulations();
                    Environment.random.setSeed(PanelParameters.getTFSeedValue());

                    for (int i = 0; i < environmentSimulations; i++) {
                        setSimulationInfoNumberText(String.valueOf(i + 1));
                        environment.initialize (
                            PanelParameters.getCBSnakeType() < Environment.CB_AI ?
                                    Environment.random.nextInt() : i
                        );
                        environmentUpdated();
                        environment.simulate();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                return null;
            }

            @Override
            public void done() {
                environment.removeEnvironmentListener(simulationPanel);
                ConsoleUtils.println(ConsoleColor.BRIGHT_RED, "<simulate worker done>");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                }
            }
        };
        worker.execute();
    }

    public void buildImage(Environment environment) {
        image = new BufferedImage(
                environment.getSize() * CELL_SIZE + 1,
                environment.getSize() * CELL_SIZE + 1,
                BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void environmentUpdated() {
        int n = environment.getSize();
        Graphics g = image.getGraphics();

        //Fill the cells color
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                g.setColor(environment.getCellColor(y, x));
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        //Draw the grid lines
        g.setColor(Color.BLACK);
        for (int i = 0; i <= n; i++) {
            g.drawLine(0, i * CELL_SIZE, n * CELL_SIZE, i * CELL_SIZE);
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, n * CELL_SIZE);
        }

        g = environmentPanel.getGraphics();
        g.drawImage(image, GRID_TO_PANEL_GAP, GRID_TO_PANEL_GAP, null);

        try {
            Thread.sleep(UPDATE_INTERVAL);
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * The simulate button should only be enabled when:
     * - A valid dataset was loaded
     *  With a loaded dataset:
     *      - When a non-AI snake type is selected
     *      - When an AI snake type is selected AND the algorithm was executed until an individual was selected
     * @param index
     */
    @Override
    public void snakeTypeChanged(int index) {
        if (!mainFrame.isDatasetLoaded() || (index >= Environment.CB_AI && !hasBestIndividualToSimulate())) {
            buttonSimulate.setEnabled(false);
            return;
        }

        buttonSimulate.setEnabled(true);
    }

    public boolean hasBestIndividualToSimulate() {
        // no loaded dataset
        if (mainFrame.getProblem().getEnvironment() == null)
            return false;
        // no best individual / the dataset didn't run
        if (!mainFrame.getProblem().getEnvironment().hasBestInRun())
            return false;

        return true;
    }
}

//--------------------
class SimulationPanel_jButtonSimulate_actionAdapter implements ActionListener {

    final private PanelSimulation adaptee;

    SimulationPanel_jButtonSimulate_actionAdapter(PanelSimulation adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonSimulate_actionPerformed(e);
    }
}


