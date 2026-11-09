package com.calc2.application;

import com.calc2.domain.AngleUnit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorSettingsTest {
    @Test void usesDocumentedDefaultsAndRejectsUnsupportedPrecision() {
        CalculatorSettings defaults = CalculatorSettings.defaults();
        assertEquals(AngleUnit.DEGREES, defaults.getAngleUnit());
        assertEquals(10, defaults.getMaximumFractionDigits());
        assertEquals(CalculatorTheme.DARK, defaults.getTheme());
        assertThrows(IllegalArgumentException.class, () -> new CalculatorSettings(AngleUnit.DEGREES, 3, CalculatorTheme.DARK));
    }
}
