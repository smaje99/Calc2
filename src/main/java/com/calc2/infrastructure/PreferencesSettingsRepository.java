package com.calc2.infrastructure;

import com.calc2.application.*;
import com.calc2.domain.AngleUnit;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/** Stores settings in a supplied Java Preferences node; storage failures are non-fatal. */
public final class PreferencesSettingsRepository implements SettingsRepository {
    private final Preferences preferences;
    /** Uses the caller's node so tests and applications can select independent storage.
     * @param preferences node used for persistence
     */
    public PreferencesSettingsRepository(Preferences preferences) { this.preferences = preferences; }
    @Override public CalculatorSettings load() {
        CalculatorSettings defaults = CalculatorSettings.defaults();
        AngleUnit angle = defaults.getAngleUnit(); int digits = defaults.getMaximumFractionDigits(); CalculatorTheme theme = defaults.getTheme();
        try { String value = preferences.get("angleUnit", "DEGREES"); try { angle = AngleUnit.valueOf(value); } catch (RuntimeException ignored) { } } catch (RuntimeException ignored) { }
        try { int value = preferences.getInt("maximumFractionDigits", 10); if (value == 2 || value == 4 || value == 6 || value == 10) digits = value; } catch (RuntimeException ignored) { }
        try { String value = preferences.get("theme", "DARK"); try { theme = CalculatorTheme.valueOf(value); } catch (RuntimeException ignored) { } } catch (RuntimeException ignored) { }
        return new CalculatorSettings(angle, digits, theme);
    }
    @Override public void save(CalculatorSettings settings) {
        try {
            preferences.put("angleUnit", settings.getAngleUnit().name());
            preferences.putInt("maximumFractionDigits", settings.getMaximumFractionDigits());
            preferences.put("theme", settings.getTheme().name());
            preferences.flush();
        } catch (SecurityException | IllegalStateException | BackingStoreException ignored) { }
    }
}
