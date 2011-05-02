package de.escidoc.core.statistic;

import org.apache.camel.InOnly;

/**
 * Service for managing statistic data.
 */
public interface StatisticService {

    /**
     * Creates a new record in statistic repository.
     *
     * @param statisticRecord a statistic data record
     */
    @InOnly
    void createStatisticRecord(StatisticRecord statisticRecord);

}
