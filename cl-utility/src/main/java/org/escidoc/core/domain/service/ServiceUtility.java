/**
 * 
 */
package org.escidoc.core.domain.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;
import org.escidoc.core.domain.properties.java.EntryTO;
import org.escidoc.core.domain.sru.*;
import org.escidoc.core.domain.sru.parameters.SruConstants;
import org.escidoc.core.domain.sru.parameters.SruRequestTypeFactory;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.util.xml.internal.JAXBContextProvider;
import org.escidoc.core.utils.io.EscidocBinaryContent;
import org.escidoc.core.utils.io.Stream;

import de.escidoc.core.common.exceptions.system.SystemException;
import org.escidoc.core.utils.xml.StreamElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Marko Voss
 * @deprecated Compatibility utility to map the new CXF services to the old services.
 * 
 */
@Deprecated
@Guarded
public class ServiceUtility {

    @Autowired
    @Qualifier("escidocJAXBContextProvider")
    private JAXBContextProvider jaxbContextProvider;

    protected ServiceUtility() {}
    /**
     * Convenience method to support SRU service methods.
     *
     * @param parameters the SRU parameters
     * @param roleId additional parameter <tt>roleId</tt>
     * @param userId additional parameter <tt>userId</tt>
     * @param omitHighlighting additional parameter <tt>omitHighlighting</tt>
     * @return the map required by the old services
     * @throws SystemException if anything goes wrong
     */
    public Map<String, String[]> handleSruRequest(@NotNull final SruSearchRequestParametersBean parameters,
                                                  final String roleId, final String userId,
                                                  final String omitHighlighting) throws SystemException {
        final List<Map.Entry<String, String>> additionalParams = SruRequestTypeFactory.getDefaultAdditionalParams(
                roleId, userId, omitHighlighting);
        final JAXBElement<? extends RequestTypeTO> requestTO;
        final Map<String, String[]> map;
        try {
            requestTO = SruRequestTypeFactory.createRequestTO(parameters, additionalParams);
            map = this.toMap(requestTO);
        } catch (JAXBException e) {
            throw new SystemException(e.getMessage(), e);
        } catch (IOException e) {
            throw new SystemException(e.getMessage(), e);
        }

        return map;
    }

    /**
     * Creates a new {@link Marshaller} using a new instance of the JAXBContext with the package of the specified object
     * as the <tt>contextPath</tt> for the JAXBContext in order to avoid redundant namespaces to appear in the resulting
     * XML.
     *
     * @param object the object to marshal
     * @return the marshaller
     * @throws JAXBException if an exception occurs while trying to create the marshaller
     */
    private Marshaller getMarshaller(@NotNull Object object) throws JAXBException {
        return JAXBContext.newInstance(object.getClass().getPackage().getName()).createMarshaller();
    }

    /**
     * Creates a new {@link Marshaller} using a new instance of the JAXBContext with the package of the specified class
     * as the <tt>contextPath</tt> for the JAXBContext in order to avoid redundant namespaces to appear in the resulting
     * XML.
     *
     * @param clazz the class, which represents the object to marshal
     * @return the marshaller
     * @throws JAXBException if an exception occurs while trying to create the marshaller
     */
    private Marshaller getMarshaller(@NotNull Class clazz) throws JAXBException {
        return JAXBContext.newInstance(clazz.getPackage().getName()).createMarshaller();
    }

    /**
     *
     * @param source the source to unmarshal from
     * @return the unmarshalled object
     * @throws JAXBException if an exception occurs while trying to unmarshal the source
     */
    private Object unmarshal(@NotNull Source source) throws JAXBException {
        final Unmarshaller unmarshaller = jaxbContextProvider.getJAXBContext().createUnmarshaller();
        return unmarshaller.unmarshal(source);
    }

    /**
     *
     * @param source the source to unmarshal from
     * @param clazz the class of the type of the return value
     * @param <T> the type of the return value
     * @return the unmarshalled object of the type of the specified class, which represents its type
     * @throws JAXBException if anything goes wrong
     */
    private <T> T unmarshal(@NotNull Source source, @NotNull Class<T> clazz) throws JAXBException {
        final Unmarshaller unmarshaller = JAXBContext.newInstance(clazz.getPackage().getName()).createUnmarshaller();
        return unmarshaller.unmarshal(source, clazz).getValue();
    }

    /**
     *
     * @param objectTO the object to marshal
     * @return the {@link Stream} containing the marshalled object
     * @throws Exception if anything goes wrong
     */
    private Stream marshal(@NotNull Object objectTO) throws Exception {
        final Stream stream = new Stream();
        final Marshaller marshaller = getMarshaller(objectTO);
        marshaller.marshal(objectTO, stream);
        stream.lock();
        return stream;
    }

    // Note: This code is slow and only for migration!
    // TODO: Replace this code and use domain objects!
    public final String toXML(@NotNull final Object objectTO) throws SystemException {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            Stream stream = marshal(objectTO);
            stream.writeCacheTo(stringBuilder);
        }
        catch (final Exception e) {
            throw new SystemException("Error on marshalling object: " + objectTO.getClass().getName(), e);
        }
        return stringBuilder.toString();
    }

    // Note: This code is slow and only for migration!
    // TODO: Replace this code and use domain objects!
    public final <T> T fromXML(@NotNull final Class<T> classTO, @NotNull @NotEmpty final String xmlString) throws SystemException {

        try {
            return unmarshal(new StreamSource(new StringReader(xmlString)), classTO);
        }
        catch (final Exception e) {
            throw new SystemException("Error on unmarshalling XML.", e);
        }
    }

    // Note: This code is slow and only for migration!
    // TODO: Replace this code and use domain objects!
    public final Object fromXML(@NotNull @NotEmpty final String xmlString) throws SystemException {
        try {
            return unmarshal(new StreamSource(new StringReader(xmlString)));
        }
        catch (Exception e) {
            throw new SystemException("Error on unmarshalling XML.", e);
        }
    }

    /**
     *
     * @param request the {@link JAXBElement} of the request
     * @return the map for the old services containing all field of the specified request
     */
    public final Map<String, String[]> toMap(@NotNull final JAXBElement<? extends RequestTypeTO> request)
            throws JAXBException, IOException {

        final Map<String, String[]> result = new HashMap<String, String[]>();

        if (request.getDeclaredType().equals(SearchRetrieveRequestTypeTO.class))
            insertIntoMap((SearchRetrieveRequestTypeTO) request.getValue(), result);
        else if (request.getDeclaredType().equals(ExplainRequestTypeTO.class))
            insertIntoMap((ExplainRequestTypeTO) request.getValue(), result);
        else if (request.getDeclaredType().equals(ScanRequestTypeTO.class))
            insertIntoMap((ScanRequestTypeTO) request.getValue(), result);

        return result;
    }

    public final Response toResponse(@NotNull
        final EscidocBinaryContent content) throws SystemException {
            final String externalContentRedirectUrl = content.getRedirectUrl();
            try {
                if (externalContentRedirectUrl != null) {
                    // redirect
                    return Response.seeOther(new URI(externalContentRedirectUrl)).entity(
                        "<html><body><a href=\"" + externalContentRedirectUrl + "\">The requested binary content"
                            + " is externally available under this location: " + externalContentRedirectUrl
                            + "</a></body></html>").type("text/html").status(Status.SEE_OTHER).build();
                }
                else {
                    // response with content
                    ResponseBuilder responseBuilder =
                        Response
                            .ok(content.getContent()).header("Cache-Control", "no-cache").header("Pragma", "no-cache")
                            .type(content.getMimeType());
                    if (content.getFileName() != null) {
                        responseBuilder.header("Content-Disposition", "inline;filename=\"" + content.getFileName() + '\"');
                    }
                    return responseBuilder.build();
                }
            }
            catch (URISyntaxException e) {
                throw new SystemException(e);
            }
            catch (IOException e) {
                throw new SystemException(e);
            }
        }

    /**
     *
     * @param request
     * @param result
     */
    private void insertIntoMap(final SearchRetrieveRequestTypeTO request, final Map<String, String[]> result)
            throws JAXBException, IOException {
        // type
        result.put(SruConstants.SRU_PARAM_OP, new String[] { SruConstants.SRU_PARAM_OP_SEARCH});
        // general
        insertGeneralRequestData(request, result);
        // specific
        if (request.getMaximumRecords() != null) {
            result.put(SruConstants.SRU_PARAM_MAXIMUM_RECORDS,
                    new String[] { request.getMaximumRecords().toString() });
        }
        if (request.getQuery() != null) {
            result.put(SruConstants.SRU_PARAM_QUERY, new String[] { request.getQuery() });
        }
        if (request.getRecordPacking() != null) {
            result.put(SruConstants.SRU_PARAM_RECORD_PACKING, new String[] { request.getRecordPacking() });
        }
        if (request.getRecordSchema() != null) {
            result.put(SruConstants.SRU_PARAM_RECORD_SCHEMA, new String[] { request.getRecordSchema() });
        }
        if (request.getRecordXPath() != null) {
            result.put(SruConstants.SRU_PARAM_RECORD_XPATH, new String[] { request.getRecordXPath() });
        }
        if (request.getResultSetTTL() != null) {
            result.put(SruConstants.SRU_PARAM_RESULT_SET_TTL, new String[] { request.getResultSetTTL().toString() });
        }
        if (request.getSortKeys() != null) {
            result.put(SruConstants.SRU_PARAM_SORT_KEYS, new String[] { request.getSortKeys() });
        }
        if (request.getStartRecord() != null) {
            result.put(SruConstants.SRU_PARAM_START_RECORD, new String[] { request.getStartRecord().toString() });
        }
        if (request.getStylesheet() != null) {
            result.put(SruConstants.SRU_PARAM_STYLESHEET, new String[] { request.getStylesheet().toASCIIString() });
        }
        // extra
        insertExtraRequestData(request.getExtraRequestData(), result);
    }

    /**
     *
     * @param request
     * @param result
     */
    private void insertIntoMap(final ExplainRequestTypeTO request, final Map<String, String[]> result)
            throws JAXBException, IOException {
        // type
        result.put(SruConstants.SRU_PARAM_OP, new String[] { SruConstants.SRU_PARAM_OP_EXPLAIN });
        // general
        insertGeneralRequestData(request, result);
        // specific
        if (request.getRecordPacking() != null) {
            result.put(SruConstants.SRU_PARAM_RECORD_PACKING, new String[] { request.getRecordPacking() });
        }
        if (request.getStylesheet() != null) {
            result.put(SruConstants.SRU_PARAM_STYLESHEET, new String[] { request.getStylesheet().toASCIIString() });
        }
        // extra
        insertExtraRequestData(request.getExtraRequestData(), result);
    }

    /**
     *
     * @param request
     * @param result
     */
    private void insertIntoMap(final ScanRequestTypeTO request, final Map<String, String[]> result) throws JAXBException, IOException {
        // type
        result.put(SruConstants.SRU_PARAM_OP, new String[] { SruConstants.SRU_PARAM_OP_SCAN });
        // general
        insertGeneralRequestData(request, result);
        // specific
        if (request.getScanClause() != null) {
            result.put(SruConstants.SRU_PARAM_SCAN_CLAUSE, new String[] { request.getScanClause() });
        }
        if (request.getResponsePosition() != null) {
            result.put(SruConstants.SRU_PARAM_RESPONSE_POSITION,
                    new String[] { request.getResponsePosition().toString() });
        }
        if (request.getMaximumTerms() != null) {
            result.put(SruConstants.SRU_PARAM_MAXIMUM_TERMS, new String[] { request.getMaximumTerms().toString() });
        }
        if (request.getStylesheet() != null) {
            result.put(SruConstants.SRU_PARAM_STYLESHEET, new String[] { request.getStylesheet().toASCIIString() });
        }
        // extra
        insertExtraRequestData(request.getExtraRequestData(), result);
    }

    /**
     *
     * @param requestTypeTO
     * @param result
     */
    private void insertGeneralRequestData(final RequestTypeTO requestTypeTO, final Map<String, String[]> result) {
        if (requestTypeTO.getVersion() != null) {
            result.put(SruConstants.SRU_PARAM_VERSION, new String[] { requestTypeTO.getVersion() });
        }
    }

    /**
     *
     * @param extraDataTypeTO
     * @param result
     */
    private void insertExtraRequestData(final ExtraDataTypeTO extraDataTypeTO, final Map<String, String[]> result)
            throws JAXBException, IOException {

        if (extraDataTypeTO != null && extraDataTypeTO.getAny() != null && !extraDataTypeTO.getAny().isEmpty()) {
            // we expect org.escidoc.core.domain.properties.java.EntryTO to be the content
            JAXBContext context = JAXBContext.newInstance(EntryTO.class.getPackage().getName());
            for (StreamElement element : extraDataTypeTO.getAny()) {
                EntryTO entryTO = context.createUnmarshaller().unmarshal(
                        new StreamSource(element.getStream().getInputStream()), EntryTO.class).getValue();
                result.put(entryTO.getKey(), new String[] { entryTO.getContent() });
            }
        }
    }
}