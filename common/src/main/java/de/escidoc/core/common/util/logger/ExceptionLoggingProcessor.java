package de.escidoc.core.common.util.logger;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class ExceptionLoggingProcessor implements Processor {

    private String logCategory = ExceptionLoggingProcessor.class.getName();

    public void setLogCategory(final String logCategory) {
        this.logCategory = logCategory;
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"}) Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        if(caused != null) {
            Logger.getLogger(logCategory).error(caused.getMessage());
        }
    }
}
