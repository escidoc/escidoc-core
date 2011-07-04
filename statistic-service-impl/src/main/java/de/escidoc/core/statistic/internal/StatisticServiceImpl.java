package de.escidoc.core.statistic.internal;

import de.escidoc.core.sm.business.interfaces.StatisticDataHandlerInterface;
import de.escidoc.core.statistic.StatisticServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link StatisticService}.
 */
@Service("de.escidoc.core.statistic.internal.StatisticServiceImpl")
public class StatisticServiceImpl {

    @Autowired
    @Qualifier("business.StatisticDataHandler")
    private StatisticDataHandlerInterface statisticDataHandler;

    /**
     * Saves the static data string in the repository.
     *
     * @param statisticData the statistic data XML string
     * @throws StatisticServiceException if any error accours.
     */
    public void createStatisticRecord(final String statisticData) throws StatisticServiceException {
        try {
            // TODO: Refactor StatisticDataHandler and move to this module.
            this.statisticDataHandler.insertStatisticData(statisticData);
        }
        catch (final Exception e) {
            throw new StatisticServiceException("Error on saving statistic data " + statisticData + ":\n"
                + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "StatisticServiceImpl{" + "statisticDataHandler=" + this.statisticDataHandler + '}';
    }
}
