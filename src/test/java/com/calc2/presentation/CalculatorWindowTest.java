package com.calc2.presentation;

import com.calc2.application.CalculatorTheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculatorWindowTest {
    @Test
    void usesAReadableSmallerFontForLongCalculationErrors() {
        assertEquals(15, CalculatorWindow.resultFontSize(true));
        assertEquals(34, CalculatorWindow.resultFontSize(false));
    }

    @Test
    void usesReadableExpressionTextColorForDarkTheme() {
        assertEquals(new java.awt.Color(240, 244, 250), CalculatorWindow.expressionForeground(CalculatorTheme.DARK));
    }

    @Test
    void keypadUsesUnicodeConstantSymbolsAndKeepsExpressionAreaHeightStable() {
        assertTrue(java.util.Arrays.asList(CalculatorWindow.keypadLabels()).contains("π"));
        assertTrue(java.util.Arrays.asList(CalculatorWindow.keypadLabels()).contains("ℯ"));
        assertEquals(48, CalculatorWindow.resultDisplayHeight());
    }

    @Test
    void keypadUsesMathematicalFunctionSymbolsAndIncludesAnswerRecall() {
        java.util.List<String> labels = java.util.Arrays.asList(CalculatorWindow.keypadLabels());
        assertTrue(labels.contains("√"));
        assertTrue(labels.contains("sin"));
        assertTrue(labels.contains("log₁₀"));
        assertTrue(labels.contains("Ans"));
    }
}
