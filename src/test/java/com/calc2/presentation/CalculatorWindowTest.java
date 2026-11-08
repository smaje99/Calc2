package com.calc2.presentation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorWindowTest {
    @Test
    void usesAReadableSmallerFontForLongCalculationErrors() {
        assertEquals(15, CalculatorWindow.resultFontSize(true));
        assertEquals(34, CalculatorWindow.resultFontSize(false));
    }
}
