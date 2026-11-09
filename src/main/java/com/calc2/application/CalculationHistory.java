package com.calc2.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Bounded newest-first history for one calculator session. */
public final class CalculationHistory {
    private final int capacity;
    private final List<CalculationRecord> records = new ArrayList<>();
    /** Creates history that retains at most {@code capacity} records.
     * @param capacity maximum number of records; must be positive
     */
    public CalculationHistory(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be positive.");
        this.capacity = capacity;
    }
    /** Adds a record at the front and evicts the oldest record if needed.
     * @param record successful calculation to remember
     */
    public void add(CalculationRecord record) {
        if (record == null) throw new IllegalArgumentException("Record is required.");
        records.add(0, record);
        if (records.size() > capacity) records.remove(records.size() - 1);
    }
    /** Returns an unmodifiable snapshot ordered newest first.
     * @return an independent snapshot of the session history
     */
    public List<CalculationRecord> newestFirst() {
        return Collections.unmodifiableList(new ArrayList<>(records));
    }
}
