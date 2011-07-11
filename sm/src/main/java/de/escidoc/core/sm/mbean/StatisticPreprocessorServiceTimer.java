package de.escidoc.core.sm.mbean;

/**
 * @author Michael Hoppe
 *         <p/>
 *         Singleton for timing Scheduled Job. We need this because Spring Scheduler has Bug and always executes
 *         scheduled job twice.
 */
public final class StatisticPreprocessorServiceTimer {

    private static final StatisticPreprocessorServiceTimer INSTANCE = new StatisticPreprocessorServiceTimer();

    private long lastExecutionTime;

    /**
     * private Constructor for Singleton.
     */
    private StatisticPreprocessorServiceTimer() {
    }

    /**
     * Only initialize Object once. Check for old objects in cache.
     *
     * @return StatisticPreprocessorServiceTimer StatisticPreprocessorServiceTimer
     */
    public static StatisticPreprocessorServiceTimer getInstance() {
        return INSTANCE;
    }

    /**
     * Get lastExecutionTime.
     * @return
     */
    public long getLastExecutionTime() {
        final long newLastExecutionTime = System.currentTimeMillis();
        synchronized (INSTANCE) {
            final long savedLastExecutionTime = this.lastExecutionTime;
            this.lastExecutionTime = newLastExecutionTime;
            return savedLastExecutionTime;
        }
    }

}
