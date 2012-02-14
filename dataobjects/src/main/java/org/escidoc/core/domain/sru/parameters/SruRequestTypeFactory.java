package org.escidoc.core.domain.sru.parameters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBElement;

import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.Pre;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.escidoc.core.domain.sru.ExplainRequestTO;
import org.escidoc.core.domain.sru.ExplainRequestType;
import org.escidoc.core.domain.sru.ExtraDataType;
import org.escidoc.core.domain.sru.RequestType;
import org.escidoc.core.domain.sru.ScanRequestTO;
import org.escidoc.core.domain.sru.ScanRequestType;
import org.escidoc.core.domain.sru.SearchRetrieveRequestTO;
import org.escidoc.core.domain.sru.SearchRetrieveRequestType;

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

    /**
     * 
     * @param sruParams
     * @param additionalParams
     * @return
     */
    public static final JAXBElement<? extends RequestType> createRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<KeyValuePair> additionalParams) {

        if (isSearchRequest(sruParams)) {
            return createSearchRetrieveRequestTO(sruParams, additionalParams);
        }
        else if (isExplainRequest(sruParams)) {
            return createExplainRequestTO(sruParams, additionalParams);
        }
        else if (isScanRequest(sruParams)) {
            return createScanRequestTO(sruParams, additionalParams);
        }
        return null;
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
     * @param sruParams
     * @param additionalParams
     * @return
     */
    public static final SearchRetrieveRequestTO createSearchRetrieveRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<KeyValuePair> additionalParams) {

        if (!isSearchRequest(sruParams))
            return null;

        final SearchRetrieveRequestType search = new SearchRetrieveRequestType();
        search.setMaximumRecords(createNonNegativeInteger(sruParams.getMaximumRecords()));
        search.setQuery(sruParams.getQuery());
        search.setRecordPacking(sruParams.getRecordPacking());
        search.setRecordSchema(sruParams.getRecordSchema());
        search.setRecordXPath(sruParams.getRecordXPath());
        search.setResultSetTTL(createNonNegativeInteger(sruParams.getResultSetTTL()));
        search.setSortKeys(sruParams.getSortKeys());
        search.setStartRecord(createPositiveInteger(sruParams.getStartRecord()));
        try {
            search.setStylesheet(new URI(sruParams.getStylesheet()));
        }
        catch (URISyntaxException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring invalid stylesheet-URI: " + sruParams.getStylesheet());
            }
        }
        search.setVersion(sruParams.getVersion());
        search.setExtraRequestData(createExtraDataType(additionalParams));

        return new SearchRetrieveRequestTO(search);
    }

    /**
     *
     * @param sruParams
     * @param additionalParams
     * @return
     */
    public static final ExplainRequestTO createExplainRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<KeyValuePair> additionalParams) {

        if (!isExplainRequest(sruParams))
            return null;

        final ExplainRequestType explain = new ExplainRequestType();
        explain.setRecordPacking(sruParams.getRecordPacking());
        try {
            explain.setStylesheet(new URI(sruParams.getStylesheet()));
        }
        catch (URISyntaxException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring invalid stylesheet-URI: " + sruParams.getStylesheet());
            }
        }
        explain.setVersion(sruParams.getVersion());
        explain.setExtraRequestData(createExtraDataType(additionalParams));

        return new ExplainRequestTO(explain);
    }

    /**
     * 
     * @return
     */
    public static final ScanRequestTO createScanRequestTO(@NotNull
    final SruSearchRequestParametersBean sruParams, final List<KeyValuePair> additionalParams) {

        if (!isScanRequest(sruParams))
            return null;

        final ScanRequestType scan = new ScanRequestType();
        scan.setMaximumTerms(createPositiveInteger(sruParams.getMaximumTerms()));
        scan.setResponsePosition(createNonNegativeInteger(sruParams.getResponsePosition()));
        scan.setScanClause(sruParams.getScanClause());
        try {
            scan.setStylesheet(new URI(sruParams.getStylesheet()));
        }
        catch (URISyntaxException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring invalid stylesheet-URI: " + sruParams.getStylesheet());
            }
        }
        scan.setVersion(sruParams.getVersion());
        scan.setExtraRequestData(createExtraDataType(additionalParams));

        return new ScanRequestTO(scan);
    }

    /**
     * 
     * @param additionalParams
     * @return
     */
    private static ExtraDataType createExtraDataType(final List<KeyValuePair> additionalParams) {
        if (additionalParams == null)
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