package de.escidoc.core.sm.mbean;


/**
 * @author mih
 * 
 *         Singleton for timing Scheduled Job.
 *         We need this because Spring Scheduler has Bug and 
 *         always executes scheduled job twice.
 * 
 */
public final class StatisticPreprocessorServiceTimer {

    private static final StatisticPreprocessorServiceTimer instance = new StatisticPreprocessorServiceTimer();

    private long lastExecutionTime;

    /**
     * private Constructor for Singleton.
     * 
     */
    private StatisticPreprocessorServiceTimer() {
    }

    /**
     * Only initialize Object once. Check for old objects in cache.
     * 
     * @return StatisticPreprocessorServiceTimer StatisticPreprocessorServiceTimer
     * 
     */
    public static StatisticPreprocessorServiceTimer getInstance() {
        return instance;
    }
    
    /**
     * Get lastExecutionTime.
     * 
     */
    public long getLastExecutionTime() {
        long newLastExecutionTime = System.currentTimeMillis();
        synchronized(instance) {
            final long savedLastExecutionTime = lastExecutionTime;
            lastExecutionTime = newLastExecutionTime;
            return savedLastExecutionTime;
        }
    }

}

