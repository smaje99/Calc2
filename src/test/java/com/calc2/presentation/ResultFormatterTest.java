package com.calc2.presentation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultFormatterTest {
    @Test void roundsForDisplayWithDecimalPointAndKeepsOrdinaryValuesOutOfScientificNotation() {
        assertEquals("1.24", ResultFormatter.format(1.235, 2));
        assertEquals("0.0", ResultFormatter.format(-0.0, 4));
        assertEquals("1000000000000000000000000.0", ResultFormatter.format(1.0e24, 2));
        assertEquals("0.0", ResultFormatter.format(1.0e-20, 10));
    }
}
