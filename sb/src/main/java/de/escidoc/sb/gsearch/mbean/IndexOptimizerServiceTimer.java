package de.escidoc.sb.gsearch.mbean;

/**
 * @author Michael Hoppe
 *         <p/>
 *         Singleton for timing Scheduled Job. We need this because Spring Scheduler has Bug and always executes
 *         scheduled job twice.
 */
public final class IndexOptimizerServiceTimer {

    private static final IndexOptimizerServiceTimer INSTANCE = new IndexOptimizerServiceTimer();

    private long lastExecutionTime;

    /**
     * private Constructor for Singleton.
     */
    private IndexOptimizerServiceTimer() {
    }

    /**
     * Only initialize Object once. Check for old objects in cache.
     *
     * @return IndexOptimizerServiceTimer IndexOptimizerServiceTimer
     */
    public static IndexOptimizerServiceTimer getInstance() {
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
