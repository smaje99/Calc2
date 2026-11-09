package com.calc2.application;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class CalculationHistoryTest {
    @Test void keepsNewestFirstAndBoundsCapacityWithoutDeduplicating() {
        CalculationHistory history = new CalculationHistory(2);
        CalculationRecord first = new CalculationRecord("1+1", 2, Instant.EPOCH);
        CalculationRecord second = new CalculationRecord("1+1", 2, Instant.EPOCH.plusSeconds(1));
        CalculationRecord third = new CalculationRecord("2+1", 3, Instant.EPOCH.plusSeconds(2));
        history.add(first); history.add(second); history.add(third);
        assertEquals(2, history.newestFirst().size());
        assertSame(third, history.newestFirst().get(0));
        assertSame(second, history.newestFirst().get(1));
        assertThrows(UnsupportedOperationException.class, () -> history.newestFirst().clear());
    }

    @Test void retainsAtMostFiftyRecords() {
        CalculationHistory history = new CalculationHistory(50);
        for (int i = 0; i < 51; i++) history.add(new CalculationRecord(String.valueOf(i), i, Instant.ofEpochSecond(i)));
        assertEquals(50, history.newestFirst().size());
        assertEquals("50", history.newestFirst().get(0).getExpression());
        assertEquals("1", history.newestFirst().get(49).getExpression());
    }
}
