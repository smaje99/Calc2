package com.calc2.application;

import com.calc2.domain.CalculationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculateExpressionTest {
    private final CalculateExpression calculateExpression = new CalculateExpression();

    @Test
    void executesAValidExpressionAndReturnsItsNumericResult() {
        assertEquals(14.0, calculateExpression.execute("2 + 3 * 4"), 0.000001);
    }

    @Test
    void propagatesAReadableCalculationErrorForInvalidInput() {
        CalculationException error = assertThrows(CalculationException.class,
                () -> calculateExpression.execute("2 +"));

        assertEquals("The expression is incomplete or invalid.", error.getMessage());
    }
}
