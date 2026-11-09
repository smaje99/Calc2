package com.calc2.infrastructure;

import com.calc2.application.*;
import com.calc2.domain.AngleUnit;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.prefs.Preferences;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PreferencesSettingsRepositoryTest {
    @Test void loadsValidFieldsAndFallsBackIndependentlyForMalformedValues() throws Exception {
        Path isolatedRoot = Files.createTempDirectory("calc2-prefs-test-");
        System.setProperty("java.util.prefs.userRoot", isolatedRoot.toString());
        Preferences node = Preferences.userRoot().node("calc2-test/" + UUID.randomUUID());
        try {
            node.put("angleUnit", "RADIANS"); node.put("maximumFractionDigits", "3"); node.put("theme", "broken");
            CalculatorSettings settings = new PreferencesSettingsRepository(node).load();
            assertEquals(AngleUnit.RADIANS, settings.getAngleUnit());
            assertEquals(10, settings.getMaximumFractionDigits());
            assertEquals(CalculatorTheme.DARK, settings.getTheme());
        } finally { node.removeNode(); }
    }
}
