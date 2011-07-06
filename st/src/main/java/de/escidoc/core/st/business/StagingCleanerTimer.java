package de.escidoc.core.st.business;

/**
 * @author Michael Hoppe
 *         <p/>
 *         Singleton for timing Scheduled Job. We need this because Spring Scheduler has Bug and always executes
 *         scheduled job twice.
 */
public final class StagingCleanerTimer {

    private static final StagingCleanerTimer INSTANCE = new StagingCleanerTimer();

    private long lastExecutionTime;

    /**
     * private Constructor for Singleton.
     */
    private StagingCleanerTimer() {
    }

    /**
     * Only initialize Object once. Check for old objects in cache.
     *
     * @return IndexOptimizerServiceTimer IndexOptimizerServiceTimer
     */
    public static StagingCleanerTimer getInstance() {
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
