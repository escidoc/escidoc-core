package org.escidoc.core.domain.sru.parameters;

import org.escidoc.core.domain.sru.ObjectFactory;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class SruConstants {

    private SruConstants() {
    }

    // OPERATION (not part of the SRW schema)
    public static final String SRU_PARAM_OP = "operation";

    public static final String SRU_PARAM_OP_SEARCH = "searchRetrieve";

    public static final String SRU_PARAM_OP_SCAN = "scan";

    public static final String SRU_PARAM_OP_EXPLAIN = "explain";

    // ADDITIONAL ESCIDOC PARAMS
    public static final String SRU_PARAM_ROLE_ID = "x-info5-roleId";

    public static final String SRU_PARAM_USER_ID = "x-info5-userId";

    public static final String SRU_PARAM_OMIT_HIGHLIGHTING = "x-info5-omitHighlighting";

    // PARAMETERS (we assume here, that the SRW/U team named the parameters like the element names, which is true for now)
    private static final ObjectFactory FACTORY = new ObjectFactory();

    public static final String SRU_PARAM_MAXIMUM_RECORDS = FACTORY.createMaximumRecords(null).getName().getLocalPart();

    public static final String SRU_PARAM_QUERY = FACTORY.createQuery(null).getName().getLocalPart();

    public static final String SRU_PARAM_RECORD_PACKING = FACTORY.createRecordPacking(null).getName().getLocalPart();

    public static final String SRU_PARAM_RECORD_SCHEMA = FACTORY.createRecordSchema(null).getName().getLocalPart();

    public static final String SRU_PARAM_RECORD_XPATH = FACTORY.createRecordXPath(null).getName().getLocalPart();

    public static final String SRU_PARAM_RESULT_SET_TTL = FACTORY.createResultSetTTL(null).getName().getLocalPart();

    public static final String SRU_PARAM_SORT_KEYS = FACTORY.createSortKeys(null).getName().getLocalPart();

    public static final String SRU_PARAM_START_RECORD = FACTORY.createStartRecord(null).getName().getLocalPart();

    public static final String SRU_PARAM_STYLESHEET = FACTORY.createStylesheet(null).getName().getLocalPart();

    public static final String SRU_PARAM_VERSION = FACTORY.createVersion(null).getName().getLocalPart();

    public static final String SRU_PARAM_RESPONSE_POSITION = FACTORY.createResponsePosition(null).getName().getLocalPart();

    public static final String SRU_PARAM_MAXIMUM_TERMS = FACTORY.createMaximumTerms(null).getName().getLocalPart();

    public static final String SRU_PARAM_SCAN_CLAUSE = FACTORY.createScanClause(null).getName().getLocalPart();
}
