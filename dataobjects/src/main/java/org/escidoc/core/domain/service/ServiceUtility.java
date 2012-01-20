/**
 * 
 */
package org.escidoc.core.domain.service;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.KeyValuePair;

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

    // Note: This code is slow and only for migration!
    // TODO: Replace this code and use domain objects!
    public static final Object fromXML(final String contextPath, final String xmlString) throws SystemException {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(contextPath);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final Source src = new StreamSource(new StringReader(xmlString));
            Object bindingObject = unmarshaller.unmarshal(src);
            return bindingObject;
        }
        catch (Exception e) {
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
        result.put(Constants.SRU_PARAMETER_OPERATION, new String[] { SruRequestTypeFactory.SRW_REQUEST_SEARCH_OP });

        if (request.getMaximumRecords() != null) {
            result
                .put(Constants.SRU_PARAMETER_MAXIMUM_RECORDS, new String[] { request.getMaximumRecords().toString() });
        }
        if (request.getQuery() != null) {
            result.put(Constants.SRU_PARAMETER_QUERY, new String[] { request.getQuery() });
        }
        if (request.getRecordPacking() != null) {
            result.put(Constants.SRU_PARAMETER_RECORD_PACKING, new String[] { request.getRecordPacking() });
        }
        if (request.getRecordSchema() != null) {
            result.put(Constants.SRU_PARAMETER_RECORD_SCHEMA, new String[] { request.getRecordSchema() });
        }
        if (request.getRecordXPath() != null) {
            result.put(Constants.SRU_PARAMETER_RECORD_XPATH, new String[] { request.getRecordXPath() });
        }
        if (request.getResultSetTTL() != null) {
            result.put(Constants.SRU_PARAMETER_RESULT_SET_TTL, new String[] { request.getResultSetTTL().toString() });
        }
        if (request.getSortKeys() != null) {
            result.put(Constants.SRU_PARAMETER_SORT_KEYS, new String[] { request.getSortKeys() });
        }
        if (request.getStartRecord() != null) {
            result.put(Constants.SRU_PARAMETER_START_RECORD, new String[] { request.getStartRecord().toString() });
        }
        if (request.getStylesheet() != null) {
            result.put(Constants.SRU_PARAMETER_STYLESHEET, new String[] { request.getStylesheet() });
        }
        if (request.getVersion() != null) {
            result.put(Constants.SRU_PARAMETER_VERSION, new String[] { request.getVersion() });
        }
        if (request.getExtraRequestData() != null && request.getExtraRequestData().getAny() != null
            && !request.getExtraRequestData().getAny().isEmpty()) {
            Iterator<Object> iterator = request.getExtraRequestData().getAny().iterator();
            while (iterator.hasNext()) {
                KeyValuePair keyValuePair = (KeyValuePair) iterator.next();
                result.put(keyValuePair.getKey(), new String[] { keyValuePair.getValue() });
            }
        }
    }

    private static final void insertIntoMap(final ExplainRequestType request, final Map<String, String[]> result) {
        result.put(Constants.SRU_PARAMETER_OPERATION, new String[] { SruRequestTypeFactory.SRW_REQUEST_EXPLAIN_OP });

        if (request.getRecordPacking() != null) {
            result.put(Constants.SRU_PARAMETER_RECORD_PACKING, new String[] { request.getRecordPacking() });
        }
        if (request.getStylesheet() != null) {
            result.put(Constants.SRU_PARAMETER_STYLESHEET, new String[] { request.getStylesheet() });
        }
        if (request.getVersion() != null) {
            result.put(Constants.SRU_PARAMETER_VERSION, new String[] { request.getVersion() });
        }
        if (request.getExtraRequestData() != null && request.getExtraRequestData().getAny() != null
            && !request.getExtraRequestData().getAny().isEmpty()) {
            Iterator<Object> iterator = request.getExtraRequestData().getAny().iterator();
            while (iterator.hasNext()) {
                KeyValuePair keyValuePair = (KeyValuePair) iterator.next();
                result.put(keyValuePair.getKey(), new String[] { keyValuePair.getValue() });
            }
        }
    }

    private static final void insertIntoMap(final ScanRequestType request, final Map<String, String[]> result) {
        result.put(Constants.SRU_PARAMETER_OPERATION, new String[] { SruRequestTypeFactory.SRW_REQUEST_SCAN_OP });

        if (request.getStylesheet() != null) {
            result.put(Constants.SRU_PARAMETER_STYLESHEET, new String[] { request.getStylesheet() });
        }
        if (request.getVersion() != null) {
            result.put(Constants.SRU_PARAMETER_VERSION, new String[] { request.getVersion() });
        }
        if (request.getScanClause() != null) {
            result.put(Constants.SRU_PARAMETER_SCAN_CLAUSE, new String[] { request.getScanClause() });
        }
        if (request.getResponsePosition() != null) {
            result.put(Constants.SRU_PARAMETER_RESPONSE_POSITION, new String[] { request
                .getResponsePosition().toString() });
        }
        if (request.getMaximumTerms() != null) {
            result.put(Constants.SRU_PARAMETER_MAXIMUM_TERMS, new String[] { request.getMaximumTerms().toString() });
        }
        if (request.getExtraRequestData() != null && request.getExtraRequestData().getAny() != null
            && !request.getExtraRequestData().getAny().isEmpty()) {
            Iterator<Object> iterator = request.getExtraRequestData().getAny().iterator();
            while (iterator.hasNext()) {
                KeyValuePair keyValuePair = (KeyValuePair) iterator.next();
                result.put(keyValuePair.getKey(), new String[] { keyValuePair.getValue() });
            }
        }
    }

}