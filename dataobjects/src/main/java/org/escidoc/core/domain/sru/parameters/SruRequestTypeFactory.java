package org.escidoc.core.domain.sru.parameters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import de.escidoc.core.common.business.Constants;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.escidoc.core.domain.sru.*;

import de.escidoc.core.common.util.service.KeyValuePair;
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

    public static final String SRW_REQUEST_SEARCH_OP = "searchRetrieve";

    public static final String SRW_REQUEST_SCAN_OP = "scan";

    public static final String SRW_REQUEST_EXPLAIN_OP = "explain";

    private static final ObjectFactory factory = new ObjectFactory();

    /**
     * 
     * @param sruParams
     * @param additionalParams
     * @return
     */
    @NotNull
    public static final JAXBElement<? extends RequestType> createRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<KeyValuePair> additionalParams) {

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
     * @param keyValuePairs
     * @return
     */
    @NotNull
    public static final JAXBElement<? extends RequestType> createRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final KeyValuePair... keyValuePairs) {
        List<KeyValuePair> pairs = new ArrayList<KeyValuePair>(keyValuePairs.length);
        for (KeyValuePair pair : keyValuePairs) {
            pairs.add(pair);
        }
        return createRequestTO(sruParams, pairs);
    }

    /**
     *
     * @param sruParams
     * @return
     */
    @NotNull
    public static final JAXBElement<? extends RequestType> createRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams) {
        return createRequestTO(sruParams, new ArrayList<KeyValuePair>(0));
    }

    /**
     * 
     * @param roleId
     * @param userId
     * @param omitHighlighting
     * @return
     */
    @NotNull
    public static final List<KeyValuePair> getDefaultAdditionalParams(
        String roleId, String userId, String omitHighlighting) {
        final List<KeyValuePair> additionalParams = new LinkedList<KeyValuePair>();
        if (roleId != null) {
            additionalParams.add(new KeyValuePair(Constants.SRU_PARAMETER_ROLE, roleId));
        }
        if (userId != null) {
            additionalParams.add(new KeyValuePair(Constants.SRU_PARAMETER_USER, userId));
        }
        if (omitHighlighting != null) {
            additionalParams.add(new KeyValuePair(Constants.SRU_PARAMETER_OMIT_HIGHLIGHTING, omitHighlighting));
        }
        return additionalParams;
    }

    /**
     * 
     * @return
     */
    public static boolean isSearchRequest(@NotNull
    final SruSearchRequestParametersBean sruParams) {
        return SRW_REQUEST_SEARCH_OP.equals(sruParams.getOperation());
    }

    /**
     * 
     * @return
     */
    public static boolean isExplainRequest(@NotNull
    final SruSearchRequestParametersBean sruParams) {
        return SRW_REQUEST_EXPLAIN_OP.equals(sruParams.getOperation());
    }

    /**
     * 
     * @return
     */
    public static boolean isScanRequest(@NotNull
    final SruSearchRequestParametersBean sruParams) {
        return SRW_REQUEST_SCAN_OP.equals(sruParams.getOperation());
    }

    /**
     *
     *
     * @param sruParams
     * @param additionalParams
     * @return
     */
    @NotNull
    public static final JAXBElement<SearchRetrieveRequestType> createSearchRetrieveRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<KeyValuePair> additionalParams) {

        final SearchRetrieveRequestType searchType = factory.createSearchRetrieveRequestType();
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

        return factory.createSearchRetrieveRequest(searchType);
    }

    /**
     *
     * @param sruParams
     * @param additionalParams
     * @return
     */
    @NotNull
    public static final JAXBElement<ExplainRequestType> createExplainRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<KeyValuePair> additionalParams) {

        final ExplainRequestType explainType = factory.createExplainRequestType();
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

        return factory.createExplainRequest(explainType);
    }

    /**
     * 
     * @return
     */
    @NotNull
    public static final JAXBElement<ScanRequestType> createScanRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<KeyValuePair> additionalParams) {

        final ScanRequestType scanType = factory.createScanRequestType();
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

        return factory.createScanRequest(scanType);
    }

    /**
     * 
     * @param additionalParams
     * @return
     */
    private static ExtraDataType createExtraDataType(final List<KeyValuePair> additionalParams) {
        if (additionalParams == null || additionalParams.size() == 0)
            return null;

        final ExtraDataType extra = new ExtraDataType();
        extra.getAny().addAll(additionalParams);
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