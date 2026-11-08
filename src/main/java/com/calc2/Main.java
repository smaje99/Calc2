package com.calc2;

import com.calc2.presentation.CalculatorController;
import com.calc2.presentation.CalculatorWindow;

import javax.swing.SwingUtilities;

/**
 * Starts the Calc2 desktop calculator.
 *
 * <p>This class is the application's composition point: it creates the controller and
 * window, then asks Swing to build and show them on the event dispatch thread. Swing uses
 * that single thread for screen updates and button events, which prevents the interface
 * from being changed concurrently by unrelated threads.</p>
 */
public final class Main {
    private Main() {
        // Utility entry point: no instances are needed.
    }

    /**
     * Schedules the calculator window to open after the program starts.
     *
     * @param args command-line arguments; this desktop calculator does not currently use them
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorController controller = new CalculatorController();
            CalculatorWindow window = new CalculatorWindow(controller);
            window.setVisible(true);
        });
    }
}
