package de.escidoc.sb.gsearch.mbean;


/**
 * @author mih
 * 
 *         Singleton for timing Scheduled Job.
 *         We need this because Spring Scheduler has Bug and 
 *         always executes scheduled job twice.
 * 
 */
public final class IndexOptimizerServiceTimer {

    private static IndexOptimizerServiceTimer instance = null;
    
    private boolean reserved = false;

    private long lastExecutionTime = 0;

    /**
     * private Constructor for Singleton.
     * 
     */
    private IndexOptimizerServiceTimer() {
    }

    /**
     * Only initialize Object once. Check for old objects in cache.
     * 
     * @return IndexOptimizerServiceTimer IndexOptimizerServiceTimer
     * 
     */
    public static synchronized IndexOptimizerServiceTimer getInstance() {
        if (instance == null) {
            instance = new IndexOptimizerServiceTimer();
        }
        return instance;
    }
    
    /**
     * Set lastExecutionTime to actual time.
     * 
     */
    public synchronized void actualizeLastExecutionTime() {
        lastExecutionTime = System.currentTimeMillis();
    }
    
    /**
     * Get lastExecutionTime.
     * 
     */
    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    /**
     * lock execution.
     * 
     */
    public synchronized boolean locked() {
        if (reserved == true) {
            return true;
        }
        reserved = true;
        return false;
    }
    
    /**
     * unlock execution.
     * 
     */
    public synchronized void unlock() {
        reserved = false;
    }
    
}

