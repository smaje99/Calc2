package com.calc2.application;

/** Boundary used by the application to load and save user calculator settings. */
public interface SettingsRepository {
    /** Loads stored preferences, substituting defaults for missing or invalid values.
     * @return validated settings
     */
    CalculatorSettings load();
    /** Saves a complete immutable set of preferences.
     * @param settings settings to persist
     */
    void save(CalculatorSettings settings);
}
