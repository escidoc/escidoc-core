package de.escidoc.core.common.util.logger;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.LoggerFactory;

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
        Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        if(caused != null) {
            LoggerFactory.getLogger(logCategory).error(caused.getMessage());
        }
    }
}
