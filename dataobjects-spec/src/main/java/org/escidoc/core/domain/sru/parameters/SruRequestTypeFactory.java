package org.escidoc.core.domain.sru.parameters;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.escidoc.core.domain.properties.java.EntryTypeTO;
import org.escidoc.core.domain.sru.*;

import org.escidoc.core.domain.sru.ObjectFactory;
import org.escidoc.core.utils.io.Stream;
import org.escidoc.core.utils.xml.StreamElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Move class to object-mapping-module, which does not yet exist.
 *
 * @author Marko Voss
 * 
 */
@Guarded
public class SruRequestTypeFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SruRequestTypeFactory.class);

    private static final ObjectFactory FACTORY = new ObjectFactory();

    /**
     *
     * @param sruParams
     * @param additionalParams
     * @return
     * @throws JAXBException
     */
    @NotNull
    public static JAXBElement<? extends RequestTypeTO> createRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<Map.Entry<String, String>> additionalParams) throws JAXBException {

        if (isExplainRequest(sruParams)) {
            return createExplainRequestTO(sruParams, additionalParams);
        }
        else if (isScanRequest(sruParams)) {
            return createScanRequestTO(sruParams, additionalParams);
        }
        else {
            /* Current default behavior
             * TODO:
             * This decision should be decided in business layer but returning <tt>null</tt> here is no option as long
             * as we support the old interfaces because we need to map the request to the Map. When we got rid of the
             * old interfaces, we could pass <tt>null</tt> to the business layer to let the business layer decide, what
             * should happen. However, in this case we will loose the other query parameters because of we are passing
             * <tt>null</tt> to the business layer.
             *
             * I would suggest the following solution:
             * - Generate the SRU objects on business layer to be reusable for all layers.
             * - Implement another SRU object, which also extends the RequestType, which can be used, if the
             *   operation-parameter is invalid. (e.g. UnknownRequestType extends RequestType)
             * - Pass the UnknownRequestType to the business layer to let the business layer decide, what to do.
             */
            return createSearchRetrieveRequestTO(sruParams, additionalParams);
        }
    }

    /**
     *
     * @param sruParams
     * @param additionalParam
     * @return
     * @throws JAXBException
     */
    @NotNull
    public static JAXBElement<? extends RequestTypeTO> createRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final Map.Entry<String, String>... additionalParam) throws JAXBException {
        List<Map.Entry<String, String>> pairs = new ArrayList<Map.Entry<String, String>>(additionalParam.length);
        Collections.addAll(pairs, additionalParam);
        return createRequestTO(sruParams, pairs);
    }

    /**
     *
     * @param sruParams
     * @return
     * @throws JAXBException
     */
    @NotNull
    public static JAXBElement<? extends RequestTypeTO> createRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams) throws JAXBException {
        return createRequestTO(sruParams, new ArrayList<Map.Entry<String, String>>(0));
    }

    /**
     * 
     * @param roleId
     * @param userId
     * @param omitHighlighting
     * @return
     */
    @NotNull
    public static List<Map.Entry<String, String>> getDefaultAdditionalParams(
        String roleId, String userId, String omitHighlighting) {
        final List<Map.Entry<String, String>> additionalParams = new LinkedList<Map.Entry<String, String>>();
        if (roleId != null) {
            additionalParams.add(new AbstractMap.SimpleEntry<String, String>(SruConstants.SRU_PARAM_ROLE_ID, roleId));
        }
        if (userId != null) {
            additionalParams.add(new AbstractMap.SimpleEntry<String, String>(SruConstants.SRU_PARAM_USER_ID, userId));
        }
        if (omitHighlighting != null) {
            additionalParams.add(new AbstractMap.SimpleEntry<String, String>(
                    SruConstants.SRU_PARAM_OMIT_HIGHLIGHTING, omitHighlighting));
        }
        return additionalParams;
    }

    /**
     *
     * @param sruParams
     * @return
     */
    public static boolean isSearchRequest(@NotNull
    final SruSearchRequestParametersBean sruParams) {
        return SruConstants.SRU_PARAM_OP_SEARCH.equals(sruParams.getOperation());
    }

    /**
     *
     * @param sruParams
     * @return
     */
    public static boolean isExplainRequest(@NotNull
    final SruSearchRequestParametersBean sruParams) {
        return SruConstants.SRU_PARAM_OP_EXPLAIN.equals(sruParams.getOperation());
    }

    /**
     *
     * @param sruParams
     * @return
     */
    public static boolean isScanRequest(@NotNull
    final SruSearchRequestParametersBean sruParams) {
        return SruConstants.SRU_PARAM_OP_SCAN.equals(sruParams.getOperation());
    }

    /**
     *
     * @param sruParams
     * @param additionalParams
     * @return
     * @throws JAXBException
     */
    @NotNull
    public static JAXBElement<SearchRetrieveRequestTypeTO> createSearchRetrieveRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<Map.Entry<String, String>> additionalParams) throws JAXBException {

        final SearchRetrieveRequestTypeTO searchType = FACTORY.createSearchRetrieveRequestTypeTO();
        searchType.setMaximumRecords(createNonNegativeInteger(sruParams.getMaximumRecords()));
        searchType.setQuery(sruParams.getQuery());
        searchType.setRecordPacking(sruParams.getRecordPacking());
        searchType.setRecordSchema(sruParams.getRecordSchema());
        searchType.setRecordXPath(sruParams.getRecordXPath());
        searchType.setResultSetTTL(createNonNegativeInteger(sruParams.getResultSetTTL()));
        searchType.setSortKeys(sruParams.getSortKeys());
        searchType.setStartRecord(createPositiveInteger(sruParams.getStartRecord()));
        try {
            if (sruParams.getStylesheet() != null) {
                searchType.setStylesheet(new URI(sruParams.getStylesheet()));
            }
        }
        catch (URISyntaxException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring invalid stylesheet-URI: " + sruParams.getStylesheet());
            }
        }
        searchType.setVersion(sruParams.getVersion());
        searchType.setExtraRequestData(createExtraDataType(additionalParams));

        return FACTORY.createSearchRetrieveRequest(searchType);
    }

    /**
     *
     * @param sruParams
     * @param additionalParams
     * @return
     */
    @NotNull
    public static JAXBElement<ExplainRequestTypeTO> createExplainRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<Map.Entry<String, String>> additionalParams) throws JAXBException {

        final ExplainRequestTypeTO explainType = FACTORY.createExplainRequestTypeTO();
        explainType.setRecordPacking(sruParams.getRecordPacking());
        try {
            if (sruParams.getStylesheet() != null) {
                explainType.setStylesheet(new URI(sruParams.getStylesheet()));
            }
        }
        catch (URISyntaxException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring invalid stylesheet-URI: " + sruParams.getStylesheet());
            }
        }
        explainType.setVersion(sruParams.getVersion());
        explainType.setExtraRequestData(createExtraDataType(additionalParams));

        return FACTORY.createExplainRequest(explainType);
    }

    /**
     * 
     * @return
     */
    @NotNull
    public static JAXBElement<ScanRequestTypeTO> createScanRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<Map.Entry<String, String>> additionalParams) throws JAXBException {

        final ScanRequestTypeTO scanType = FACTORY.createScanRequestTypeTO();
        scanType.setMaximumTerms(createPositiveInteger(sruParams.getMaximumTerms()));
        scanType.setResponsePosition(createNonNegativeInteger(sruParams.getResponsePosition()));
        scanType.setScanClause(sruParams.getScanClause());
        try {
            if (sruParams.getStylesheet() != null) {
                scanType.setStylesheet(new URI(sruParams.getStylesheet()));
            }
        }
        catch (URISyntaxException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring invalid stylesheet-URI: " + sruParams.getStylesheet());
            }
        }
        scanType.setVersion(sruParams.getVersion());
        scanType.setExtraRequestData(createExtraDataType(additionalParams));

        return FACTORY.createScanRequest(scanType);
    }

    /**
     * 
     * @param additionalParams
     * @return
     */
    private static ExtraDataTypeTO createExtraDataType(final List<Map.Entry<String, String>> additionalParams) throws JAXBException {
        if (additionalParams == null || additionalParams.size() == 0)
            return null;

        final JAXBContext context = JAXBContext.newInstance(EntryTypeTO.class.getPackage().getName());
        final ExtraDataTypeTO extra = FACTORY.createExtraDataTypeTO();

        for (Map.Entry<String, String> entry : additionalParams) {
            final EntryTypeTO entryTypeTO = new EntryTypeTO();
            entryTypeTO.setKey(entry.getKey());
            entryTypeTO.setContent(entry.getValue());
            final Stream stream = new Stream();
            context.createMarshaller().marshal(entryTypeTO, stream);
            try {
                stream.close();
            } catch (IOException e) {
                LOG.warn("Unable to close stream.", e);
            }
            extra.getAny().add(new StreamElement(stream));
        }

        return extra;
    }

    /**
     * 
     * @param value
     * @return
     */
    private static NonNegativeInteger createNonNegativeInteger(final String value) {
        try {
            if (value != null) {
                return new NonNegativeInteger(value);
            }
        }
        catch (final NumberFormatException e) {
        }
        return null;
    }

    /**
     * 
     * @param value
     * @return
     */
    private static PositiveInteger createPositiveInteger(final String value) {
        try {
            if (value != null) {
                return new PositiveInteger(value);
            }
        }
        catch (final NumberFormatException e) {
        }
        return null;
    }
}