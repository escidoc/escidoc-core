package de.escidoc.core.statistic;

import java.util.List;

/**
 * Represents a record in statistic.
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
