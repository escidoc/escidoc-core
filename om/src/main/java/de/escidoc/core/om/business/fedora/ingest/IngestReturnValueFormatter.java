package de.escidoc.core.om.business.fedora.ingest;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.om.business.interfaces.ValueFormatter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Provide a formatter for the return value of the ingest.
 *
 * @author Kai Strnad
 */
@Service("business.ingestReturnValueFormatter")
public class IngestReturnValueFormatter implements ValueFormatter {

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected IngestReturnValueFormatter() {
    }

    /**
     * Format the return value from the ingest.
     */
    // TODO: make velocity template out of string builder concatenation
    @Override
    public String format(final Map<String, String> values) {
        final StringBuilder xml = new StringBuilder();
        xml
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n").append("<result xmlns=\"").append(
                Constants.RESULT_NAMESPACE_URI).append("\" ").append('>').append("<objid resourceType=\"").append(
                values.get(Constants.INGEST_RESOURCE_TYPE)).append("\">").append(values.get(Constants.INGEST_OBJ_ID))
            .append("</objid></result>");
        return xml.toString();
    }

}
