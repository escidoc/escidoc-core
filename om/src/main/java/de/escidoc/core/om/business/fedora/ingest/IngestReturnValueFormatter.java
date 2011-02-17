package de.escidoc.core.om.business.fedora.ingest;

import de.escidoc.core.om.business.interfaces.ValueFormatter;

import java.util.Map;

import static de.escidoc.core.common.business.Constants.INGEST_OBJ_ID;
import static de.escidoc.core.common.business.Constants.INGEST_RESOURCE_TYPE;
import static de.escidoc.core.common.business.Constants.RESULT_NAMESPACE_URI;

/**
 * Provide a formatter for the return value of the ingest.
 *
 * @author KST
 *
 * @spring.bean id="business.ingestReturnValueFormatter"
 *
 */
public class IngestReturnValueFormatter implements ValueFormatter {

    /**
     * Format the return value from the ingest.
     *
     * @see Interface documentation
     */
    // TODO: make velocity template out of string builder concatenation
    public String format(Map<String, String> values) {
        StringBuilder xml = new StringBuilder();
        xml
            .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n").append(
                "<result xmlns=\"").append(RESULT_NAMESPACE_URI).append("\" ")
            .append('>').append("<objid resourceType=\"").append(
                values.get(INGEST_RESOURCE_TYPE)).append("\">").append(
                values.get(INGEST_OBJ_ID)).append("</objid></result>");
        return xml.toString();
    }

}
