package org.escidoc.core.domain.sru.parameters;

import java.util.List;

import javax.xml.bind.JAXBElement;

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

/**
 * 
 * @author Marko Voß
 * 
 */
public class SruRequestTypeFactory {

    public static final String SRW_REQUEST_SEARCH_OP = "searchRetrieve";

    public static final String SRW_REQUEST_SCAN_OP = "scan";

    public static final String SRW_REQUEST_EXPLAIN_OP = "explain";

    /**
     * 
     * @param sruParams
     * @param additionalParams
     * @return
     */
    public static final JAXBElement<? extends RequestType> createRequestTO(
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
    public static boolean isSearchRequest(final SruSearchRequestParametersBean sruParams) {
        return SRW_REQUEST_SEARCH_OP.equals(sruParams.getOperation());
    }

    /**
     * 
     * @return
     */
    public static boolean isExplainRequest(final SruSearchRequestParametersBean sruParams) {
        return SRW_REQUEST_EXPLAIN_OP.equals(sruParams.getOperation());
    }

    /**
     * 
     * @return
     */
    public static boolean isScanRequest(final SruSearchRequestParametersBean sruParams) {
        return SRW_REQUEST_SCAN_OP.equals(sruParams.getOperation());
    }

    /**
     * 
     * @return
     */
    public static final SearchRetrieveRequestTO createSearchRetrieveRequestTO(
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
        search.setStylesheet(sruParams.getStylesheet());
        search.setVersion(sruParams.getVersion());
        search.setExtraRequestData(createExtraDataType(additionalParams));

        return new SearchRetrieveRequestTO(search);
    }

    /**
     * 
     * @return
     */
    public static final ExplainRequestTO createExplainRequestTO(
        final SruSearchRequestParametersBean sruParams, final List<KeyValuePair> additionalParams) {

        if (!isExplainRequest(sruParams))
            return null;

        final ExplainRequestType explain = new ExplainRequestType();
        explain.setRecordPacking(sruParams.getRecordPacking());
        explain.setStylesheet(sruParams.getStylesheet());
        explain.setVersion(sruParams.getVersion());
        explain.setExtraRequestData(createExtraDataType(additionalParams));

        return new ExplainRequestTO(explain);
    }

    /**
     * 
     * @return
     */
    public static final ScanRequestTO createScanRequestTO(
        final SruSearchRequestParametersBean sruParams, final List<KeyValuePair> additionalParams) {

        if (!isScanRequest(sruParams))
            return null;

        final ScanRequestType scan = new ScanRequestType();
        scan.setMaximumTerms(createPositiveInteger(sruParams.getMaximumTerms()));
        scan.setResponsePosition(createNonNegativeInteger(sruParams.getResponsePosition()));
        scan.setScanClause(sruParams.getScanClause());
        scan.setStylesheet(sruParams.getStylesheet());
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