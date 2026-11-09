package com.calc2.presentation;

import com.calc2.application.CalculatorTheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
