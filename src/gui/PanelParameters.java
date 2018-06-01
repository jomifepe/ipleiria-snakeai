package gui;

import snake.ProblemType;
import snake.snakeAI.ga.geneticOperators.*;
import snake.snakeAI.ga.selectionMethods.RouletteWheel;
import snake.snakeAI.ga.selectionMethods.SelectionMethod;
import snake.snakeAI.ga.selectionMethods.Tournament;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

import snake.snakeAI.SnakeIndividual;
import snake.snakeAI.SnakeProblem;

public class PanelParameters extends PanelAtributesValue {

    public static final int TEXT_FIELD_LENGHT = 7;

    public static final String SEED = "1";
    public static final String POPULATION_SIZE = "100";
    public static final String GENERATIONS = "1000";
    public static final String TOURNAMENT_SIZE = "10";
    public static final String PROB_RECOMBINATION = "0.7";
    public static final String PROB_MUTATION = "0.2";

    static JComboBox comboBoxSelectionProblemType = new JComboBox(ProblemType.values());
    static JCheckBox checkBoxUsePenalty = new JCheckBox("Penalize slacking agents");

    static JTextField textFieldSeed = new JTextField(SEED, TEXT_FIELD_LENGHT);
    JTextField textFieldN = new JTextField(POPULATION_SIZE, TEXT_FIELD_LENGHT);
    JTextField textFieldGenerations = new JTextField(GENERATIONS, TEXT_FIELD_LENGHT);
    JButton buttonRandomizeSeed = new JButton("R");
    static JCheckBox checkBoxUseSeed = new JCheckBox("Use provided seed");

    String[] selectionMethods = {"Tournament", "Roulette"};
    JComboBox comboBoxSelectionMethods = new JComboBox(selectionMethods);
    JTextField textFieldTournamentSize = new JTextField(TOURNAMENT_SIZE, TEXT_FIELD_LENGHT);

    String[] recombinationMethods = {"One cut", "Two cuts", "Uniform"};
    JComboBox comboBoxRecombinationMethods = new JComboBox(recombinationMethods);
    JTextField textFieldProbRecombination = new JTextField(PROB_RECOMBINATION, TEXT_FIELD_LENGHT);

    String[] mutationMethods = {"Gaussian", "Add Or Subtract", "Random Gene Swap"};
    JComboBox comboBoxMutationMethods = new JComboBox(mutationMethods);
    JTextField textFieldProbMutation = new JTextField(PROB_MUTATION, TEXT_FIELD_LENGHT);

    private final ArrayList<CBSnakeTypeListener> cbSnakeTypeListeners = new ArrayList<>();

    public PanelParameters() {
        title = "Genetic algorithm parameters";

        labels.add(new JLabel("Problem type: "));
        valueComponents.add(comboBoxSelectionProblemType);
        comboBoxSelectionProblemType.addActionListener(new JComboBoxSelectionSnakeType_ActionAdapter(this));

        labels.add(new JLabel(""));
        checkBoxUsePenalty.setSelected(true);
        valueComponents.add(checkBoxUsePenalty);

        labels.add(new JLabel("Seed: "));
        buttonRandomizeSeed.setToolTipText("Randomize");
        buttonRandomizeSeed.addActionListener(e -> textFieldSeed.setText(String.valueOf(getRandomSeedValue())));
        textFieldSeed.setText(String.valueOf(getRandomSeedValue()));
        JPanel panelSeed = new JPanel();
        panelSeed.setLayout(new BorderLayout());
        panelSeed.add(textFieldSeed, BorderLayout.CENTER);
        panelSeed.add(buttonRandomizeSeed, BorderLayout.AFTER_LINE_ENDS);
        valueComponents.add(panelSeed);
        textFieldSeed.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel(""));
        valueComponents.add(checkBoxUseSeed);

        labels.add(new JLabel("Population size: "));
        valueComponents.add(textFieldN);
        textFieldN.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel("# of generations: "));
        valueComponents.add(textFieldGenerations);
        textFieldGenerations.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel("Selection method: "));
        valueComponents.add(comboBoxSelectionMethods);
        comboBoxSelectionMethods.addActionListener(new JComboBoxSelectionMethods_ActionAdapter(this));

        labels.add(new JLabel("Tournament size: "));
        valueComponents.add(textFieldTournamentSize);
        textFieldTournamentSize.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel("Recombination method: "));
        valueComponents.add(comboBoxRecombinationMethods);

        labels.add(new JLabel("Recombination prob.: "));
        valueComponents.add(textFieldProbRecombination);

        labels.add(new JLabel("Mutation method: "));
        valueComponents.add(comboBoxMutationMethods);
        comboBoxMutationMethods.addActionListener(new JComboBoxMutationMethods_ActionAdapter(this));

        labels.add(new JLabel("Mutation prob.: "));
        valueComponents.add(textFieldProbMutation);

        configure();
    }

    public void addCBSnakeTypeListener(CBSnakeTypeListener listener) {
        cbSnakeTypeListeners.add(listener);
    }

    public void warnCBSnakeTypeListeners() {
        for (CBSnakeTypeListener listener : cbSnakeTypeListeners)
            listener.snakeTypeChanged(comboBoxSelectionProblemType.getSelectedIndex());
    }

    public void JComboBoxSelectionSnakeType_actionPerformed(ActionEvent e) {
        warnCBSnakeTypeListeners();
    }

    public void actionPerformedSelectionMethods(ActionEvent e) {
        textFieldTournamentSize.setEnabled(comboBoxSelectionMethods.getSelectedIndex() == 0);
    }

    public static int getCBProblemTypeSelectedIndex() {
        return comboBoxSelectionProblemType.getSelectedIndex();
    }

    public static ProblemType getProblemType() {
        return (ProblemType) comboBoxSelectionProblemType.getSelectedItem();
    }

    private int getRandomSeedValue() {
        Random rnd = new Random();
        return rnd.nextInt(Integer.MAX_VALUE) * (rnd.nextBoolean() ? 1 : -1);
    }

    public static void setCBSelectionProblemType(ProblemType problemType) {
        PanelParameters.comboBoxSelectionProblemType.setSelectedItem(problemType);
    }

    public SelectionMethod<SnakeIndividual, SnakeProblem> getSelectionMethod() {
        switch (comboBoxSelectionMethods.getSelectedIndex()) {
            case 0:
                return new Tournament<>(
                        Integer.parseInt(textFieldN.getText()),
                        Integer.parseInt(textFieldTournamentSize.getText()));
            case 1:
                return new RouletteWheel<>(
                        Integer.parseInt(textFieldN.getText()));

        }
        return null;
    }

    public Recombination<SnakeIndividual> getRecombinationMethod() {
        double recombinationProb = Double.parseDouble(textFieldProbRecombination.getText());

        switch (comboBoxRecombinationMethods.getSelectedIndex()) {
            case 0:
                return new RecombinationOneCut<>(recombinationProb);
            case 1:
                return new RecombinationTwoCuts<>(recombinationProb);
            case 2:
                return new RecombinationUniform<>(recombinationProb);
        }
        return null;
    }

    public Mutation<SnakeIndividual> getMutationMethod() {
        double mutationProbability = Double.parseDouble(textFieldProbMutation.getText());

        switch (comboBoxMutationMethods.getSelectedIndex()) {
            case 0:
                return new MutationGaussian<>(mutationProbability);
            case 1:
                return new MutationAddOrSubtract<>(mutationProbability);
            case 2:
                return new MutationRandomGeneSwap<>(mutationProbability);
        }
        return null;
    }

    public static boolean isCustomSeedCheckBoxChecked() {
        return checkBoxUseSeed.isSelected();
    }

    public static boolean isPenalizationCheckBoxChecked() { return checkBoxUsePenalty.isSelected(); }

    public static int getSeedValue() {
        String seed = textFieldSeed.getText().trim();
        return Integer.parseInt(seed);
    }
}

class JComboBoxSelectionMethods_ActionAdapter implements ActionListener {

    final private PanelParameters adaptee;

    JComboBoxSelectionMethods_ActionAdapter(PanelParameters adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.actionPerformedSelectionMethods(e);
    }
}

class JComboBoxMutationMethods_ActionAdapter implements ActionListener {

    final private PanelParameters adaptee;

    JComboBoxMutationMethods_ActionAdapter(PanelParameters adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) { }
}

class JComboBoxSelectionSnakeType_ActionAdapter implements ActionListener {

    final private PanelParameters adaptee;

    JComboBoxSelectionSnakeType_ActionAdapter(PanelParameters adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.JComboBoxSelectionSnakeType_actionPerformed(e);
    }
}

class IntegerTextField_KeyAdapter implements KeyListener {

    final private MainFrame adaptee;

    IntegerTextField_KeyAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (!Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            e.consume();
        }
    }
}

