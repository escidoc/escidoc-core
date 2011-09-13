/**
 * 
 */
package org.escidoc.core.domain.service;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.escidoc.core.domain.sru.ExplainRequestTO;
import org.escidoc.core.domain.sru.ExplainRequestType;
import org.escidoc.core.domain.sru.RequestType;
import org.escidoc.core.domain.sru.ScanRequestTO;
import org.escidoc.core.domain.sru.ScanRequestType;
import org.escidoc.core.domain.sru.SearchRetrieveRequestTO;
import org.escidoc.core.domain.sru.SearchRetrieveRequestType;
import org.escidoc.core.domain.sru.parameters.SruRequestTypeFactory;
import org.escidoc.core.utils.io.Stream;

import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Marko Voß
 * 
 */
public class ServiceUtility {

    private ServiceUtility() {

    }

    // Note: This code is slow and only for migration!
    // TODO: Replace this code and use domain objects!
    public static final String toXML(final Object objectTO) throws SystemException {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(objectTO.getClass());
            final Marshaller marshaller = jaxbContext.createMarshaller();
            final Stream stream = new Stream();
            marshaller.marshal(objectTO, stream);
            stream.lock();
            stream.writeCacheTo(stringBuilder);
        }
        catch (final Exception e) {
            throw new SystemException("Error on marshalling object: " + objectTO.getClass().getName(), e);
        }
        return stringBuilder.toString();
    }

    // Note: This code is slow and only for migration!
    // TODO: Replace this code and use domain objects!
    public static final <T> T fromXML(final Class<T> classTO, final String xmlString) throws SystemException {

        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(classTO);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final Source src = new StreamSource(new StringReader(xmlString));
            return unmarshaller.unmarshal(src, classTO).getValue();
        }
        catch (final Exception e) {
            throw new SystemException("Error on unmarshalling XML.", e);
        }
    }

    public static final Map<String, String[]> toMap(final JAXBElement<? extends RequestType> request) {
        final Map<String, String[]> result = new HashMap<String, String[]>();

        if (request instanceof SearchRetrieveRequestTO)
            insertIntoMap((SearchRetrieveRequestType) request.getValue(), result);
        else if (request instanceof ExplainRequestTO)
            insertIntoMap((ExplainRequestType) request.getValue(), result);
        else if (request instanceof ScanRequestTO)
            insertIntoMap((ScanRequestType) request.getValue(), result);

        return result;
    }

    private static final void insertIntoMap(final SearchRetrieveRequestType request, final Map<String, String[]> result) {
        result.put("operation", new String[] { SruRequestTypeFactory.SRW_REQUEST_SEARCH_OP });

        if (request.getMaximumRecords() != null) {
            result.put("maximumRecords", new String[] { request.getMaximumRecords().toString() });
        }
        if (request.getQuery() != null) {
            result.put("query", new String[] { request.getQuery() });
        }
        if (request.getRecordPacking() != null) {
            result.put("recordPacking", new String[] { request.getRecordPacking() });
        }
        if (request.getRecordSchema() != null) {
            result.put("recordSchema", new String[] { request.getRecordSchema() });
        }
        if (request.getRecordXPath() != null) {
            result.put("recordXPath", new String[] { request.getRecordXPath() });
        }
        if (request.getResultSetTTL() != null) {
            result.put("resultSetTTL", new String[] { request.getResultSetTTL().toString() });
        }
        if (request.getSortKeys() != null) {
            result.put("sortKeys", new String[] { request.getSortKeys() });
        }
        if (request.getStartRecord() != null) {
            result.put("startRecord", new String[] { request.getStartRecord().toString() });
        }
        if (request.getStylesheet() != null) {
            result.put("stylesheet", new String[] { request.getStylesheet() });
        }
        if (request.getVersion() != null) {
            result.put("version", new String[] { request.getVersion() });
        }
    }

    private static final void insertIntoMap(final ExplainRequestType request, final Map<String, String[]> result) {
        // TODO
    }

    private static final void insertIntoMap(final ScanRequestType request, final Map<String, String[]> result) {
        // TODO
    }

    public static final Map<String, String> toMap(final ExplainRequestType request) {
        return null;
    }

    public static final Map<String, String> toMap(final ScanRequestType request) {
        return null;
    }
}