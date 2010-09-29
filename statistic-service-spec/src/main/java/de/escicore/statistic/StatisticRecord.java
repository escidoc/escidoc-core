package de.escicore.statistic;

import java.util.List;

/**
 * Represents a record in statistic.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public interface StatisticRecord {

    /**
     * Returns the scope.
     *
     * @return the scope
     */
    LinkRequired getScope();

    /**
     * Returns a list of parameters.
     *
     * @return a list of parameters
     */
    List<Parameter> getParameter();
}
