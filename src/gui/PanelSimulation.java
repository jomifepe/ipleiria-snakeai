package gui;

import snake.*;
import util.ConsoleColor;
import util.ConsoleUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;

public class PanelSimulation extends JPanel implements EnvironmentListener, CBSnakeTypeListener {

    public static final int PANEL_SIZE = 250;
    public static final int CELL_SIZE = 20;
    public static final int GRID_TO_PANEL_GAP = 20;
    public static final int UPDATE_INTERVAL = 50;
    private MainFrame mainFrame;
    private Environment environment;
    private Image image;
    private JPanel environmentPanel = new JPanel();

    private final JPanel panelCenter = new JPanel();
    private final JPanel panelSimulationInfo = new JPanel();
    private JLabel simulationInfoLabel = new JLabel("Simulation number:");
    private JLabel simulationInfoCount = new JLabel("1");
    private JLabel simulationInfoSnake1FoodLabel = new JLabel("Foods eaten by snake 1:");
    private JLabel simulationInfoSnake1FoodCount = new JLabel("0");
    private JLabel simulationInfoSnake2FoodLabel = new JLabel("Foods eaten by snake 2:");
    private JLabel simulationInfoSnake2FoodCount = new JLabel("0");
    private JLabel simulationInfoTotalFoodsLabel = new JLabel("Total of foods eaten:");
    private JLabel simulationInfoTotalFoodsCount = new JLabel("0");
    private JLabel simulationInfoSnakesTotalMovementsLabel = new JLabel("Total of movements:");
    private JLabel simulationInfoSnakesTotalMovementsCount = new JLabel("0");

    private final JPanel panelSimulationButtons = new JPanel();
    private final JButton buttonSimulate = new JButton("Simulate");
    private final JButton buttonStopSimulate = new JButton("Stop");

    private SwingWorker<Void, Void> worker;
    private PanelSimulation simulationPanel;

    public PanelSimulation(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.worker = null;

        environmentPanel.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setLayout(new BorderLayout());

        Font labelFont = simulationInfoLabel.getFont();
        Font boldLabelFont = new Font(labelFont.getName(), Font.BOLD, labelFont.getSize());
        panelSimulationInfo.setLayout(new GridLayout(5, 2));
        simulationInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        simulationInfoCount.setHorizontalAlignment(SwingConstants.RIGHT);
        simulationInfoSnake1FoodLabel.setHorizontalAlignment(SwingConstants.LEFT);
        simulationInfoSnake1FoodCount.setHorizontalAlignment(SwingConstants.RIGHT);
        simulationInfoSnake2FoodLabel.setHorizontalAlignment(SwingConstants.LEFT);
        simulationInfoSnake2FoodCount.setHorizontalAlignment(SwingConstants.RIGHT);
        simulationInfoTotalFoodsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        simulationInfoTotalFoodsLabel.setFont(boldLabelFont);
        simulationInfoTotalFoodsCount.setHorizontalAlignment(SwingConstants.RIGHT);
        simulationInfoTotalFoodsCount.setFont(boldLabelFont);
        simulationInfoSnakesTotalMovementsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        simulationInfoSnakesTotalMovementsLabel.setFont(boldLabelFont);
        simulationInfoSnakesTotalMovementsCount.setHorizontalAlignment(SwingConstants.RIGHT);
        simulationInfoSnakesTotalMovementsCount.setFont(boldLabelFont);
        panelSimulationInfo.add(simulationInfoLabel);
        panelSimulationInfo.add(simulationInfoCount);
        panelSimulationInfo.add(simulationInfoSnake1FoodLabel);
        panelSimulationInfo.add(simulationInfoSnake1FoodCount);
        panelSimulationInfo.add(simulationInfoSnake2FoodLabel);
        panelSimulationInfo.add(simulationInfoSnake2FoodCount);
        panelSimulationInfo.add(simulationInfoTotalFoodsLabel);
        panelSimulationInfo.add(simulationInfoTotalFoodsCount);
        panelSimulationInfo.add(simulationInfoSnakesTotalMovementsLabel);
        panelSimulationInfo.add(simulationInfoSnakesTotalMovementsCount);

        panelCenter.setLayout(new BorderLayout());
        panelCenter.add(panelSimulationInfo, BorderLayout.NORTH);

        setSimulationInfoVisible(false);

        buttonSimulate.addActionListener(new SimulationPanel_jButtonSimulate_actionAdapter(this));
        buttonStopSimulate.addActionListener(new SimulationPanel_jButtonStopSimulate_actionAdapter(this));

        panelSimulationButtons.setLayout(new BorderLayout());
        panelSimulationButtons.add(buttonSimulate, BorderLayout.CENTER);
        panelSimulationButtons.add(buttonStopSimulate, BorderLayout.AFTER_LINE_ENDS);

        add(environmentPanel, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        add(panelSimulationButtons, BorderLayout.SOUTH);

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
        simulationInfoCount.setVisible(flag);
        simulationInfoSnake1FoodLabel.setVisible(flag);
        simulationInfoSnake1FoodCount.setVisible(flag);
        if (!flag || PanelParameters.getProblemType().ordinal() > ProblemType.ONE_AI.ordinal()) {
            simulationInfoSnake2FoodLabel.setVisible(flag);
            simulationInfoSnake2FoodCount.setVisible(flag);
            simulationInfoTotalFoodsLabel.setVisible(flag);
            simulationInfoTotalFoodsCount.setVisible(flag);
        }
        simulationInfoSnakesTotalMovementsLabel.setVisible(flag);
        simulationInfoSnakesTotalMovementsCount.setVisible(flag);
    }

    public void setSimulationInfoCount(int value) {
        this.simulationInfoCount.setText(String.valueOf(value));
    }

    int snake1Foods = 0;
    public void setSimulationInfoSnake1FoodCount(int value) {
        this.simulationInfoSnake1FoodCount.setText(String.valueOf(snake1Foods = value));
        updateSimulationInfoTotalFoodsCount();
    }

    int snake2Foods = 0;
    public void setSimulationInfoSnake2FoodCount(int value) {
        this.simulationInfoSnake2FoodCount.setText(String.valueOf(snake2Foods = value));
        updateSimulationInfoTotalFoodsCount();
    }

    public void updateSimulationInfoTotalFoodsCount() {
        this.simulationInfoTotalFoodsCount.setText(String.valueOf(snake1Foods + snake2Foods));
    }

    public void setSimulationInfoSnakesTotalMovementsCount(int value) {
        this.simulationInfoSnakesTotalMovementsCount.setText(String.valueOf(value));
    }

    public void jButtonSimulate_actionPerformed(ActionEvent e) {

        environment = mainFrame.getProblem().getEnvironment();
        environment.addEnvironmentListener(this);

        buildImage(environment);

        simulationPanel = this;

        if (worker != null && !worker.isDone()) {
            environment.removeEnvironmentListener(simulationPanel);
            worker.cancel(true);
        }

        setSimulationInfoVisible(false);
        setSimulationInfoVisible(true);

        worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                ConsoleUtils.println(ConsoleColor.BRIGHT_GREEN, "<simulate worker started>");
                try {
                    int environmentSimulations = mainFrame.getProblem().getNumEvironmentSimulations();

                    boolean usingCustomSeed = PanelParameters.isCustomSeedCheckBoxChecked();
                    if (usingCustomSeed) {
                        Environment.random.setSeed(PanelParameters.getSeedValue());
                    }

                    for (int i = 0; i < environmentSimulations; i++) {
                        setSimulationInfoCount(i + 1);
                        environment.initialize(usingCustomSeed && environment instanceof EnvironmentNonAI ? null : i);
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

    public void jButtonButtonStopSimulate_actionPerformed(ActionEvent e) {
        //worker.cancel(true);
        if (worker != null && !worker.isDone()) {
            environment.removeEnvironmentListener(simulationPanel);
            worker.cancel(true);
        }


    }

    public void buildImage(Environment environment) {
        image = new BufferedImage(
                environment.getNumLines() * CELL_SIZE + 1,
                environment.getNumLines() * CELL_SIZE + 1,
                BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void environmentUpdated() {
        int n = environment.getNumLines();
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

        int[] agentsFoods = {0, 0};
        int agentsTotalMovements = 0;
        List<SnakeAgent> agents = environment.getAgents();
        for (int i = 0; i < agents.size(); i++) {
            SnakeAgent agent = agents.get(i);
            agentsFoods[i] += agent.getTailSize();
            agentsTotalMovements += agent.getMovements();
        }

        setSimulationInfoSnake1FoodCount(agentsFoods[0]);
        setSimulationInfoSnake2FoodCount(agentsFoods[1]);
        setSimulationInfoSnakesTotalMovementsCount(agentsTotalMovements);

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
        if (!mainFrame.isDatasetLoaded() || (index > ProblemType.ADHOC.ordinal() && !hasBestIndividualToSimulate())) {
            buttonSimulate.setEnabled(false);
            return;
        }

        buttonSimulate.setEnabled(true);
    }

    public boolean hasBestIndividualToSimulate() {
        Environment environment = mainFrame.getProblem().getEnvironment();
        if (environment == null)
            return false;
        if (!(environment instanceof EnvironmentAI))
            return false;
        if (!((EnvironmentAI) environment).hasBestInRun())
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

class SimulationPanel_jButtonStopSimulate_actionAdapter implements ActionListener {

    final private PanelSimulation adaptee;

    SimulationPanel_jButtonStopSimulate_actionAdapter(PanelSimulation adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonButtonStopSimulate_actionPerformed(e);
    }
}


