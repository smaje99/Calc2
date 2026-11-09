package com.calc2.presentation;

import com.calc2.application.*;
import com.calc2.domain.AngleUnit;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/** Swing view. The editable text field owns caret and selection behavior; the controller evaluates complete text. */
public class CalculatorWindow extends JFrame {
    private static final Color DARK = new Color(22, 28, 39);
    private static final Color LIGHT = new Color(241, 244, 248);
    /** Coordinates calculations requested by user input. */
    private final CalculatorController controller;
    /** Editable source of truth for text, caret, selection, and paste. */
    private final JTextField expressionInput = new JTextField();
    /** Displays the last evaluated value or a readable calculation error. */
    private final JLabel resultLabel = new JLabel("0", SwingConstants.RIGHT);
    /** Optional boundary used to persist preference changes. */
    private final SettingsRepository settingsRepository;
    /** Current immutable preferences applied to the window. */
    private CalculatorSettings settings;
    /** Root panel whose palette changes with the selected theme. */
    private JPanel content;

    /** Creates a window with the documented defaults and no persistence adapter.
     * @param controller calculation coordinator
     */
    public CalculatorWindow(CalculatorController controller) { this(controller, CalculatorSettings.defaults(), null); }
    /** Creates the calculator view with loaded settings and a persistence boundary.
     * @param controller calculation coordinator
     * @param settings initial settings to apply
     * @param repository boundary used to save future changes; may be null
     */
    public CalculatorWindow(CalculatorController controller, CalculatorSettings settings, SettingsRepository repository) {
        super("Calc2"); this.controller = controller; this.settings = settings == null ? CalculatorSettings.defaults() : settings;
        this.settingsRepository = repository;
        controller.setAngleUnit(this.settings.getAngleUnit());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setContentPane(createContent());
        setMinimumSize(new Dimension(380, 540)); pack(); setLocationRelativeTo(null); applySettings();
    }
    private JPanel createContent() {
        content = new JPanel(new BorderLayout(0, 12)); content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JPanel top = new JPanel(new BorderLayout(6, 8));
        expressionInput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
        top.add(expressionInput, BorderLayout.NORTH);
        resultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32)); top.add(resultLabel, BorderLayout.CENTER);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JComboBox<AngleUnit> angle = new JComboBox<>(AngleUnit.values()); angle.setSelectedItem(settings.getAngleUnit());
        JComboBox<Integer> precision = new JComboBox<>(new Integer[]{2, 4, 6, 10}); precision.setSelectedItem(settings.getMaximumFractionDigits());
        JComboBox<CalculatorTheme> theme = new JComboBox<>(CalculatorTheme.values()); theme.setSelectedItem(settings.getTheme());
        controls.add(new JLabel("Angle")); controls.add(angle); controls.add(new JLabel("Digits")); controls.add(precision); controls.add(theme);
        JButton history = new JButton("History"); history.addActionListener(e -> showHistory()); controls.add(history);
        top.add(controls, BorderLayout.SOUTH); content.add(top, BorderLayout.NORTH);
        content.add(createKeypad(), BorderLayout.CENTER);
        expressionInput.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void changed() { controller.recall(expressionInput.getText()); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { changed(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { changed(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { changed(); }
        });
        expressionInput.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent event) {
                if (!controller.isResultDisplayed() || event.isControlDown() || event.isMetaDown() || event.isAltDown()) return;
                int code = event.getKeyCode();
                if (code == java.awt.event.KeyEvent.VK_ENTER || code == java.awt.event.KeyEvent.VK_ESCAPE
                        || code == java.awt.event.KeyEvent.VK_LEFT || code == java.awt.event.KeyEvent.VK_RIGHT
                        || code == java.awt.event.KeyEvent.VK_HOME || code == java.awt.event.KeyEvent.VK_END
                        || code == java.awt.event.KeyEvent.VK_BACK_SPACE || code == java.awt.event.KeyEvent.VK_DELETE) return;
                char typed = event.getKeyChar();
                if ("+-*/^".indexOf(typed) >= 0) {
                    expressionInput.setText(controller.getDisplay());
                    expressionInput.setCaretPosition(expressionInput.getText().length());
                } else if (Character.isDigit(typed) || typed == '.' || typed == '(' || typed == ')'
                        || Character.isLetter(typed)) {
                    expressionInput.setText("");
                }
            }
        });
        expressionInput.addCaretListener(event -> {
            if (controller.isResultDisplayed()) controller.recall(expressionInput.getText());
        });
        installKeyboardActions();
        angle.addActionListener(e -> changeSettings((AngleUnit) angle.getSelectedItem(), settings.getMaximumFractionDigits(), settings.getTheme()));
        precision.addActionListener(e -> changeSettings(settings.getAngleUnit(), (Integer) precision.getSelectedItem(), settings.getTheme()));
        theme.addActionListener(e -> changeSettings(settings.getAngleUnit(), settings.getMaximumFractionDigits(), (CalculatorTheme) theme.getSelectedItem()));
        return content;
    }
    private JPanel createKeypad() {
        JPanel panel = new JPanel(new GridLayout(7, 4, 8, 8));
        String[] keys = {"AC", "(", ")", "÷", "7", "8", "9", "×", "4", "5", "6", "−", "1", "2", "3", "+", "^", "0", ".", "=", "sqrt", "sin", "cos", "tan", "log", "ln", "pi", "e"};
        for (String key : keys) { JButton button = new JButton(key); button.addActionListener(e -> keyPressed(key)); panel.add(button); }
        return panel;
    }
    private void installKeyboardActions() {
        InputMap input = expressionInput.getInputMap(JComponent.WHEN_FOCUSED); ActionMap actions = expressionInput.getActionMap();
        input.put(KeyStroke.getKeyStroke("ENTER"), "evaluate"); actions.put("evaluate", new AbstractAction() { public void actionPerformed(ActionEvent e) { evaluate(); } });
        input.put(KeyStroke.getKeyStroke("ESCAPE"), "clear"); actions.put("clear", new AbstractAction() { public void actionPerformed(ActionEvent e) { clear(); } });
    }
    private void keyPressed(String key) {
        if ("AC".equals(key)) { clear(); return; }
        if ("=".equals(key)) { evaluate(); return; }
        boolean function = "sqrt".equals(key) || "sin".equals(key) || "cos".equals(key) || "tan".equals(key) || "log".equals(key) || "ln".equals(key);
        if (controller.isResultDisplayed()) {
            if ("+−×÷^".contains(key)) expressionInput.setText(controller.getDisplay()); else expressionInput.setText("");
            expressionInput.setCaretPosition(expressionInput.getText().length());
        } else if (controller.hasError()) { expressionInput.setText(""); }
        String insertion = function ? key + "()" : key;
        if ("×".equals(insertion)) insertion = "*"; if ("÷".equals(insertion)) insertion = "/"; if ("−".equals(insertion)) insertion = "-";
        expressionInput.replaceSelection(insertion);
        if (function) expressionInput.setCaretPosition(expressionInput.getCaretPosition() - 1);
        controller.recall(expressionInput.getText()); resultLabel.setText(expressionInput.getText().isEmpty() ? "0" : expressionInput.getText());
    }
    private void evaluate() {
        controller.evaluate(expressionInput.getText());
        resultLabel.setText(controller.hasError() ? controller.getDisplay() : ResultFormatter.format(Double.parseDouble(controller.getDisplay()), settings.getMaximumFractionDigits()));
        resultLabel.setForeground(controller.hasError() ? new Color(205, 45, 45) : (settings.getTheme() == CalculatorTheme.DARK ? Color.WHITE : Color.BLACK));
        resultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, resultFontSize(controller.hasError())));
    }
    private void clear() { controller.clear(); expressionInput.setText(""); resultLabel.setText("0"); resultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, resultFontSize(false))); }
    private void showHistory() {
        List<CalculationRecord> entries = controller.getHistory(); DefaultListModel<CalculationRecord> model = new DefaultListModel<>();
        for (CalculationRecord record : entries) model.addElement(record);
        JList<CalculationRecord> list = new JList<>(model); list.setCellRenderer((l, value, index, selected, focus) -> new JLabel(value.getExpression() + " = " + ResultFormatter.format(value.getResult(), settings.getMaximumFractionDigits())));
        list.addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) { String text = list.getSelectedValue().getExpression(); controller.recall(text); expressionInput.setText(text); } });
        JOptionPane.showMessageDialog(this, new JScrollPane(list), "Calculation history", JOptionPane.PLAIN_MESSAGE);
    }
    private void changeSettings(AngleUnit unit, int digits, CalculatorTheme theme) {
        settings = new CalculatorSettings(unit, digits, theme); controller.setAngleUnit(unit); applySettings();
        if (controller.isResultDisplayed()) resultLabel.setText(ResultFormatter.format(Double.parseDouble(controller.getDisplay()), digits));
        if (settingsRepository != null) settingsRepository.save(settings);
    }
    private void applySettings() {
        Color background = settings.getTheme() == CalculatorTheme.DARK ? DARK : LIGHT;
        Color foreground = expressionForeground(settings.getTheme());
        applyPalette(content, background, foreground);
        expressionInput.setBackground(settings.getTheme() == CalculatorTheme.DARK ? new Color(31, 39, 53) : Color.WHITE);
        expressionInput.setForeground(foreground);
        resultLabel.setForeground(foreground);
        repaint();
    }
    private void applyPalette(Container parent, Color background, Color foreground) {
        parent.setBackground(background);
        for (Component child : parent.getComponents()) {
            if (child instanceof JPanel) applyPalette((Container) child, background, foreground);
            else if (child instanceof JLabel) ((JLabel) child).setForeground(foreground);
            else if (child instanceof JButton) {
                ((JButton) child).setBackground(settings.getTheme() == CalculatorTheme.DARK ? new Color(48, 58, 75) : new Color(220, 226, 234));
                ((JButton) child).setForeground(foreground);
            }
        }
    }
    /** Chooses a compact font for longer messages so calculation errors remain readable. */
    static int resultFontSize(boolean showingError) { return showingError ? 15 : 34; }

    /** Returns a foreground color with readable contrast against the selected theme. */
    static Color expressionForeground(CalculatorTheme theme) {
        return theme == CalculatorTheme.DARK ? new Color(240, 244, 250) : new Color(30, 38, 50);
    }
}
