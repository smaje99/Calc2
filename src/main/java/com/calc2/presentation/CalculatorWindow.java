package com.calc2.presentation;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;

/**
 * Displays the calculator as a compact, readable Swing window.
 *
 * <p>This class is the view in the presentation layer. It creates labels and buttons,
 * arranges them with Swing layouts, and forwards each button press to a
 * {@link CalculatorController}. The view does not parse or calculate expressions. After
 * the controller updates its state, this window refreshes the expression and result labels.</p>
 *
 * <p>The display uses a subdued expression line and a larger result line. The keypad uses
 * four equal columns so every control has a consistent size, including the power, decimal,
 * parentheses, and clear keys.</p>
 */
public class CalculatorWindow extends JFrame {
    private static final Color BACKGROUND = new Color(22, 28, 39);
    private static final Color DISPLAY_BACKGROUND = new Color(31, 39, 53);
    private static final Color NUMBER_KEY = new Color(48, 58, 75);
    private static final Color OPERATOR_KEY = new Color(54, 91, 132);
    private static final Color ACCENT_KEY = new Color(0, 164, 154);
    private static final Color TEXT = new Color(240, 244, 250);
    private static final Color MUTED_TEXT = new Color(161, 174, 194);

    /** Receives calculator input and supplies the expression, result, and error state. */
    private final CalculatorController controller;

    /** Shows the expression currently being entered, above the larger result value. */
    private final JLabel expressionLabel = new JLabel("Enter an expression", SwingConstants.RIGHT);

    /** Shows the current result or the readable error returned by the controller. */
    private final JLabel resultLabel = new JLabel("0", SwingConstants.RIGHT);

    /**
     * Builds the window and connects its keys to the supplied controller.
     *
     * @param controller controller that owns calculator input and result state
     */
    public CalculatorWindow(CalculatorController controller) {
        super("Calc2");
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(createContent());
        setMinimumSize(new java.awt.Dimension(330, 470));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        content.add(createDisplay(), BorderLayout.NORTH);
        content.add(createKeypad(), BorderLayout.CENTER);
        return content;
    }

    private JPanel createDisplay() {
        JPanel panel = new JPanel(new BorderLayout(8, 10));
        panel.setBackground(DISPLAY_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));
        expressionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        expressionLabel.setForeground(MUTED_TEXT);
        resultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 34));
        resultLabel.setForeground(TEXT);
        panel.add(expressionLabel, BorderLayout.NORTH);
        panel.add(resultLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createKeypad() {
        JPanel panel = new JPanel(new GridLayout(5, 4, 10, 10));
        panel.setOpaque(false);
        String[] keys = {"AC", "(", ")", "÷", "7", "8", "9", "×", "4", "5", "6", "−",
                "1", "2", "3", "+", "^", "0", ".", "="};
        for (String key : keys) {
            panel.add(createButton(key));
        }
        return panel;
    }

    private JButton createButton(String key) {
        JButton button = new JButton(key);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        button.setForeground(TEXT);
        button.setBackground(keyColor(key));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        button.setMargin(new Insets(4, 4, 4, 4));
        button.addActionListener(event -> {
            controller.press(key);
            refreshDisplay();
        });
        return button;
    }

    private Color keyColor(String key) {
        if ("=".equals(key)) {
            return ACCENT_KEY;
        }
        if ("AC".equals(key) || "(".equals(key) || ")".equals(key) || "+".equals(key)
                || "−".equals(key) || "×".equals(key) || "÷".equals(key) || "^".equals(key)) {
            return OPERATOR_KEY;
        }
        return NUMBER_KEY;
    }

    private void refreshDisplay() {
        String expression = controller.getExpression();
        expressionLabel.setText(expression.isEmpty() ? "Enter an expression" : expression);
        resultLabel.setText(controller.getDisplay());
        resultLabel.setForeground(controller.hasError() ? new Color(255, 132, 132) : TEXT);
        resultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, resultFontSize(controller.hasError())));
    }

    static int resultFontSize(boolean showingError) {
        return showingError ? 15 : 34;
    }
}
