package com.calc2.application;

/** Saves the selected preferences through the application settings boundary. */
public final class SaveCalculatorSettings {
    private final SettingsRepository repository;
    /** Creates the use case with its storage boundary.
     * @param repository application-owned settings port
     */
    public SaveCalculatorSettings(SettingsRepository repository) { this.repository = repository; }
    /** Saves the supplied settings.
     * @param settings validated settings to save
     */
    public void execute(CalculatorSettings settings) { repository.save(settings); }
}
