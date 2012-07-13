package de.escidoc.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.xpath.XPathAPI;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.escidoc.core.test.common.resources.ResourceProvider;
import de.escidoc.core.test.common.util.xml.SchemaBaseResourceResolver;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public abstract class EscidocAssertions {

    private static final Logger LOGGER = LoggerFactory.getLogger(EscidocAssertions.class);

    private static Map<URL, Schema> schemaCache = new HashMap<URL, Schema>();

    // #################################################################################################################
    // Pattern
    // #################################################################################################################

    /**
     * Pattern to detect place holder in Velocity templates that are not replaced by values.
     */
    private static final Pattern PATTERN_VELOCITY_PLACEHOLDER = Pattern.compile("\\$\\{.*?\\}");

    private static final Pattern PATTERN_VELOCITY_PLACEHOLDER2 = Pattern.compile("\\$esc\\.");

    // #################################################################################################################
    // Schema Access
    // #################################################################################################################

    /**
     * Gets the <code>Schema</code> object for the provided url.
     *
     * @param urlString The <code>String</code> specifying the URL.
     * @return Returns the <code>Schema</code> object.
     * @throws Exception If anything fails.
     */
    public static Schema getSchema(final String urlString) throws Exception {
        return getSchema(new URL(urlString));
    }

    /**
     * Gets the <code>Schema</code> object for the provided <code>URL</code>.
     *
     * @param url The url to get the schema for.
     * @return Returns the <code>Schema</code> object.
     * @throws Exception If anything fails.
     */
    public static Schema getSchema(final URL url) throws Exception {

        Schema schema = schemaCache.get(url);
        if (schema == null) {
            URLConnection conn = url.openConnection();
            InputStream schemaStream = conn.getInputStream();
            schema = getSchema(schemaStream);
            schemaCache.put(url, schema);
        }
        return schema;
    }

    /**
     * Gets the <code>Schema</code> object for the provided <code>InputStream</code>.
     *
     * @param schemaStream The Stream containing the schema.
     * @return Returns the <code>Schema</code> object.
     * @throws Exception If anything fails.
     */
    private static Schema getSchema(final InputStream schemaStream) throws Exception {
        if (schemaStream == null) {
            throw new IllegalArgumentException("No schema input stream provided");
        }
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // set resource resolver to change schema-location-host
        sf.setResourceResolver(new SchemaBaseResourceResolver());
        return sf.newSchema(new SAXSource(new InputSource(schemaStream)));
    }

    // #################################################################################################################
    // eSciDoc XML assertions
    // #################################################################################################################

    /**
     * Validates Item XML against the XML Schema, checks if the xml:base exists and if all placeholders are replaced.
     *
     * @param xmlData The xml document as string.
     * @throws Exception If an error occurs.
     */
    public void assertXmlValidItem(final String xmlData) throws Exception {
        assertXlinkXmlBaseExists(xmlData);
        assertAllPlaceholderResolved(xmlData);
        assertItemXlinkTitles(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_IR_ITEM));
    }

    /**
     * Validates Component XML against the XML Schema, checks if the xml:base exists and if all placeholders are
     * replaced.
     *
     * @param xmlData The xml document as string.
     * @throws Exception If an error occurs.
     */
    public void assertXmlValidComponent(final String xmlData) throws Exception {
        assertXlinkXmlBaseExists(xmlData);
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_IR_ITEM));
    }

    /**
     * Validates ContentRelation XML against the XML Schema, checks if the xml:base exists and if all placeholders are
     * replaced.
     *
     * @param xmlData The xml document as string.
     * @throws Exception If an error occurs.
     */
    public void assertXmlValidContentRelation(final String xmlData) throws Exception {
        assertXlinkXmlBaseExists(xmlData);
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_IR_CONTENT_RELATION));
    }

    /**
     * Validates ContentRelations reistered predicates list XML against the XML Schema, checks if the xml:base exists
     * and if all placeholders are replaced.
     *
     * @param xmlData The xml document as string.
     * @throws Exception If an error occurs.
     */
    public void assertXMLValidRegisteredPredicates(final String xmlData) throws Exception {
        assertXlinkXmlBaseExists(xmlData);
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_IR_PREDICATE_LIST));
    }

    /**
     * Validates Content Model XML against the XML Schema, if the xml:base exists and if all placeholders are resolved.
     *
     * @param xmlData The xml document as string.
     * @throws Exception If an error occurs.
     */
    public void assertXmlValidContentModel(final String xmlData) throws Exception {
        assertXlinkXmlBaseExists(xmlData);
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_CMM_CONTENT_MODEL));
    }

    public void assertXmlValidSetDefinition(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_OAI_SET_DEFINITION));
    }

    /**
     * Asserts that the provided xml data is a valid for grants schema.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidGrants(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_AA_GRANTS));
    }

    /**
     * Assert the XML structure of the return value (task oriented methods).
     *
     * @param xmlData The return value of task oriented method
     * @throws Exception Thrown if the XML has not the expected structure or values.
     */
    public void assertXmlValidResult(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_COMMON_RESULTS));
    }

    /**
     * Asserts that the provided xml data is a valid context.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidContext(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_IR_CONTEXT));
    }

    /**
     * Asserts that the provided xml data is a valid scope.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidScope(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_SM_SCOPE));
    }

    /**
     * Asserts that the provided xml data is a valid SRW response.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidSrwResponse(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_COMMON_SRW));
    }

    /**
     * Asserts that the provided XML data is a valid struct-map.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidStructMap(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_IR_STRUCT_MAP));
    }

    /**
     * Asserts that the provided XML data is valid against successors XSD.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidSuccessors(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_OUM_OU_SUCCESSORS));
    }

    /**
     * Asserts that the provided xml data is a valid aggregation-definition.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidAggregationDefinition(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_SM_AGGREGATION_DEFINITION));
    }

    /**
     * Asserts that the provided xml data is a valid aggregation-definition.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidTmeResult(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_TME_JHOVE));
    }

    /**
     * Asserts that the provided xml data is a valid report-definition.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidReportDefinition(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_SM_REPORT_DEFINITION));
    }

    /**
     * Asserts that the provided xml data is a valid report.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidReport(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_SM_REPORT));
    }

    /**
     * Asserts that the provided xml data is a valid PDP request.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidPDPRequests(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_AA_PDP_REQUESTS));
    }

    /**
     * Asserts that the provided xml data is valid for authorization responses.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidPDPResults(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_AA_PDP_RESULTS));
    }

    /**
     * Asserts that the provided xml data is a valid role.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidRole(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_AA_ROLE));
    }

    /**
     * Asserts that the provided xml data is a valid index configuration.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidIndexConfiguration(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_ADM_INDEX_CONFIGURATION));
    }

    /**
     * Asserts that the provided xml data is a valid user account.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidUserAccount(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_AA_USER_ACCOUNT));
    }

    /**
     * Asserts that the provided xml data is a valid for preferences schema.<br/>
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidPreferences(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_AA_PREFERENCES));
    }

    /**
     * Asserts that the provided xml data is a valid for attributes schema.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidAttributes(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_AA_ATTRIBUTES));
    }

    /**
     * Asserts that the provided xml data is a valid user account.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidUserAttribute(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_AA_ATTRIBUTES));
    }

    /**
     * Asserts that the provided xml data is valid for a staging file.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidStagingFile(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        EscidocTestBase.assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_ST_FILE));
    }

    /**
     * Asserts that the provided xml data is a valid organizational unit.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidOrganizationalUnit(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_OUM_OU));
    }

    /**
     * Asserts that the provided xml data is a valid list of user accounts.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidUserAccountList(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_AA_USER_ACCOUNT_LIST));
    }

    /**
     * Asserts that the provided xml data is a valid user group.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidUserGroup(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_AA_USER_GROUP));
    }

    public void assertXmlValidContainer(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_IR_CONTAINER));
    }

    public void assertXmlValidVersionHistory(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_COMMON_VERSION_HISTORY));
    }

    public void assertXmlValidRelations(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_COMMON_RELATIONS));
    }

    public void assertXmlValidOUParents(final String xmlData) throws Exception {
        assertXmlValidOrganizationalUnit(xmlData);
    }

    /**
     * Asserts that the provided xml data is a valid organizational unit pathlist.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidOrganizationalUnitPathList(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_OUM_OU_PATH_LIST));
    }

    /**
     * Asserts that the provided xml data is a valid organizational unit.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public void assertXmlValidOrganizationalUnitsRefs(final String xmlData) throws Exception {
        assertAllPlaceholderResolved(xmlData);
        assertXmlValid(xmlData, new URL(Constants.XSD_ACCESS_OUM_OU_REF_LIST));
    }

    /**
     * Asserts that the provided xml data is valid for a explain plan.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If anything fails.
     */
    public static void assertXmlValidExplainPlan(final String xmlData) throws Exception {
        String replacedData = xmlData;
        if (replacedData.contains("explainResponse")) {
            replacedData = replacedData.replaceFirst("(?s).*?(<[^>]*?explain[\\s>].*?</[^>]*?explain.*?>).*", "$1");
        }
        assertXmlValid(replacedData, new URL(Constants.XSD_ACCESS_COMMON_ZEEREX));
    }

    // #################################################################################################################
    // eSciDoc XML helper assertions
    // #################################################################################################################

    /**
     * Assert that all Xlink titles match the (eSciDoc) Xlink title conventions.
     * <p/>
     * TODO this test is not complete, because of an outstanding definition. (see issue INFR-865)
     *
     * @param xmlData XML of the Item
     */
    public void assertItemXlinkTitles(final String xmlData) throws Exception {
        Document document = EscidocAbstractTest.getDocument(xmlData);
        // relations
        Node relations = XPathAPI.selectSingleNode(document, "/item/relations/@title", document);
        if (relations != null) {
            assertEquals("Xlink:title of relations differs from convention", "Relations of Item", relations
                .getTextContent());
        }
    }

    /**
     * Assert that all Xlink titles match the (eSciDoc) Xlink title conventions.
     * <p/>
     * TODO this test is not complete, because of an outstanding definition. (see issue INFR-865)
     *
     * @param xmlData XML of the Container
     */
    public void assertContainerXlinkTitles(final String xmlData) throws Exception {
        Document document = EscidocAbstractTest.getDocument(xmlData);
        // relations
        Node relations = XPathAPI.selectSingleNode(document, "/container/relations/@title", document);
        if (relations != null) {
            assertEquals("Xlink:title of relations differs from convention", "Relations of Container", relations
                .getTextContent());
        }
    }

    // #################################################################################################################
    // General XML assertions
    // #################################################################################################################

    /**
     * Asserts there is no namespace declaration for prefixes starting with 'xml'.
     *
     * @param xmlData The xml document as string.
     */
    public static void assertXmlPrefixNotDeclared(final String xmlData) {
        if (xmlData.contains("xmlns:xml")) {
            fail("Namespace declaration for prefixes starting with 'xml'"
                + " is not allowed. Even not the declaration of the XML "
                + "Namespace because MS Internet Explorer perceive it as " + "an error.");
        }
    }

    /**
     * Asserts that all template placeholders are replaced by values.
     *
     * @param xmlData The xml data to be asserted.
     */
    protected void assertAllPlaceholderResolved(final String xmlData) {
        assertTrue("Placeholder not resolved during rendering\n." + xmlData, !PATTERN_VELOCITY_PLACEHOLDER.matcher(
            xmlData).find());
        assertTrue("Placeholder not resolved during rendering\n." + xmlData, !PATTERN_VELOCITY_PLACEHOLDER2.matcher(
            xmlData).find());
    }

    /**
     * Asserts that no local href (without protocol and host) appears without xml:base.
     *
     * @param xmlData The xml data to be asserted.
     * @throws Exception If an error ocurres.
     */
    protected void assertXlinkXmlBaseExists(final String xmlData) throws Exception {
        Document document = EscidocAbstractTest.getDocument(xmlData);
        NodeList localHrefs = selectNodeList(document, "//*[starts-with(@href, '/')]");
        NodeList xmlBase = selectNodeList(document, "//@base", document);

        assertTrue("xml:base needed", localHrefs.getLength() == 0 || xmlBase.getLength() != 0);
    }

    /**
     * Assert the provided XML data is valid against the provided schema.
     *
     * @param xmlData   The xml data to be asserted.
     * @param schemaURL The URL of the schema.
     * @throws Exception If anything fails.
     */
    public static void assertXmlValid(final String xmlData, final URL schemaURL) throws Exception {
        assertXmlValid(xmlData, getSchema(schemaURL));
    }

    /**
     * Assert the provided XML data is valid against the provided schema.
     *
     * @param xmlData The xml data to be asserted.
     * @param schema  The schema.
     * @throws Exception If anything fails.
     */
    public static void assertXmlValid(final String xmlData, final Schema schema) throws Exception {

        assertNotNull("No Xml data. ", xmlData);
        try {
            Validator validator = schema.newValidator();
            InputStream in = new ByteArrayInputStream(xmlData.getBytes(Constants.DEFAULT_CHARSET));
            validator.validate(new SAXSource(new InputSource(in)));
        }
        catch (final Exception e) {
            final StringBuilder errorMsg = new StringBuilder("XML invalid. ");
            errorMsg.append(e.getMessage());
            if (LOGGER.isDebugEnabled()) {
                errorMsg.append(xmlData);
                errorMsg.append("============ End of invalid xml ============\n");
            }
            fail(errorMsg.toString());
        }
        assertXmlPrefixNotDeclared(xmlData);
    }

    /**
     * Assert XML content is equal.
     *
     * @param message      The message printed if assertion fails.
     * @param expected     The expected XML content.
     * @param toBeAsserted The XML content to be compared with the expected content.
     * @throws Exception If anything fails.
     */
    public static void assertXmlEquals(final String message, final String expected, final String toBeAsserted)
        throws Exception {

        Document expectedDoc = EscidocAbstractTest.getDocument(expected);
        Document assertedDoc = EscidocAbstractTest.getDocument(toBeAsserted);

        assertXmlEquals(message, expectedDoc, assertedDoc);
    }

    /**
     * Assert XML content is equal.
     *
     * @param message      The message printed if assertion fails.
     * @param expected     The expected XML content.
     * @param toBeAsserted The XML content to be compared with the expected content.
     * @throws Exception If anything fails.
     */
    public static void assertXmlEquals(final String message, final InputStream expected, final String toBeAsserted)
        throws Exception {

        Document expectedDoc = EscidocAbstractTest.getDocument(ResourceProvider.getContentsFromInputStream(expected));
        Document assertedDoc = EscidocAbstractTest.getDocument(toBeAsserted);
        assertXmlEquals(message, expectedDoc, assertedDoc);
    }

    /**
     * Assert XML content is equal.
     *
     * @param message      The message printed if assertion fails.
     * @param expected     The expected XML content.
     * @param toBeAsserted The XML content to be compared with the expected content.
     * @throws Exception If anything fails.
     */
    public static void assertXmlEquals(final String message, final File expected, final String toBeAsserted)
        throws Exception {

        assertXmlEquals(message, new FileInputStream(expected), toBeAsserted);
    }

    /**
     * Assert that the Element/Attribute selected by the xPath exists.
     *
     * @param message The message printed if assertion fails.
     * @param xml     The xml document as String.
     * @param xPath   The xPath.
     * @throws Exception If anything fails.
     */
    public static void assertXmlExists(final String message, final String xml, final String xPath) throws Exception {

        assertXmlExists(message, EscidocAbstractTest.getDocument(xml), xPath);
    }

    /**
     * Assert that the Element/Attribute selected by the xPath exists.
     *
     * @param message The message printed if assertion fails.
     * @param node    The Node.
     * @param xPath   The xPath.
     * @throws Exception If anything fails.
     */
    public static void assertXmlExists(final String message, final Node node, final String xPath) throws Exception {

        NodeList nodes = selectNodeList(node, xPath);
        assertTrue(message, nodes.getLength() > 0);
    }

    /**
     * Assert that the Element/Attribute selected by the xPath exists.
     *
     * @param message       The message printed if assertion fails.
     * @param node          The Node.
     * @param xPath         The xPath.
     * @param namespaceNode The namespace node.
     * @throws Exception If anything fails.
     */
    public static void assertXmlExists(
        final String message, final Node node, final String xPath, final Node namespaceNode) throws Exception {

        NodeList nodes = selectNodeList(node, xPath, namespaceNode);
        assertTrue(message, nodes.getLength() > 0);
    }

    /**
     * Assert that the value in the Document selected by the xPath equals the expected value.
     *
     * @param message       The message printed if assertion fails.
     * @param xml           The xml document as String.
     * @param xPath         The xPath.
     * @param expectedValue The expected value.
     * @throws Exception If anything fails.
     */
    public static void assertXmlEquals(
        final String message, final String xml, final String xPath, final String expectedValue) throws Exception {
        assertXmlEquals(message, EscidocAbstractTest.getDocument(xml), xPath, expectedValue);
    }

    /**
     * Assert that the value in the Document selected by the xPath equals the expected value.
     *
     * @param message       The message printed if assertion fails.
     * @param node          The Node.
     * @param xPath         The xPath.
     * @param expectedValue The expected value.
     * @throws Exception If anything fails.
     */
    public static void assertXmlEquals(
        final String message, final Node node, final String xPath, final String expectedValue) throws Exception {
        Node comp = selectSingleNode(node, xPath);
        assertNotNull(message + " Node selected by xpath not found [" + xPath + "]", comp);
        final String trimmed = comp.getTextContent().trim();
        assertEquals(message, expectedValue, trimmed);
    }

    /**
     * Assert that the value in the Document selected by the xPath equals the expected value.
     *
     * @param message  The message printed if assertion fails.
     * @param expected The expected node
     * @param result   The result to be asserted.
     * @param xPath    The xPath.
     * @throws Exception If anything fails.
     */
    public static void assertXmlEquals(final String message, final Node expected, final Node result, final String xPath)
        throws Exception {

        assertXmlEquals(message, expected, xPath, result, xPath);
    }

    /**
     * Assert that the value(s) in the to be asserted node selected by the xPath equals the expected value.
     *
     * @param message           The message printed if assertion fails.
     * @param expected          The node from which the expected value is selected by the xpath.
     * @param expectedXpath     The xpath expression navigating in the node to the expected value.
     * @param toBeAsserted      The node for that the value selected by the xpath shall be asserted.
     * @param toBeAssertedXpath The xPath navigating to the value that shall be asserted.
     * @throws Exception If anything fails.
     */
    public static void assertXmlEquals(
        final String message, final Node expected, final String expectedXpath, final Node toBeAsserted,
        final String toBeAssertedXpath) throws Exception {

        final String msg = prepareAssertionFailedMessage(message);

        if (expected == toBeAsserted) {
            return;
        }
        final NodeList expectedNodes = selectNodeList(expected, expectedXpath);
        final NodeList toBeAssertedNodes = selectNodeList(toBeAsserted, toBeAssertedXpath);
        assertEquals(msg + "Number of selected nodes differ. ", expectedNodes.getLength(), toBeAssertedNodes
            .getLength());
        final int length = toBeAssertedNodes.getLength();
        for (int i = 0; i < length; i++) {
            assertXmlEquals(msg + "Asserting " + (i + 1) + ". node. ", expectedNodes.item(i), toBeAssertedNodes.item(i));
        }
    }

    /**
     * Assert that the value in the Document selected by the xPath NOT equals the unexpected value.
     *
     * @param message      The message printed if assertion fails.
     * @param unexpected   The unexpected node
     * @param toBeAsserted The result to be asserted.
     * @param xPath        The xPath.
     * @throws Exception If anything fails.
     */
    public static void assertXmlNotEquals(
        final String message, final Node unexpected, final Node toBeAsserted, final String xPath) throws Exception {

        Node toBeAssertedComp = selectSingleNode(toBeAsserted, xPath);
        Node unexpectedComp = selectSingleNode(unexpected, xPath);
        assertNotEquals(message, unexpectedComp.getTextContent(), toBeAssertedComp.getTextContent());
    }

    /**
     * Asserts that the timestamp has been updated.<br> This assertion fails if the timestamp in the previous document
     * is not less than the timestamp in the updated document. The timestamp is identified by the root element's
     * "last-modification-timestamp" attribute.
     *
     * @param message      The message printed if assertion fails.
     * @param previous     The document containing the expected timestamp
     * @param toBeAsserted The document for that the timestamp shall be asserted.
     * @throws Exception If an error ocurres.
     */
    public static void assertXmlLastModificationDateUpdate(
        final String message, final Document previous, final Document toBeAsserted) throws Exception {

        final String previousLastModificationDateValue = getLastModificationDateValue(previous);
        final String toBeAssertedLastModificationDateValue = getLastModificationDateValue(toBeAsserted);
        assertDateBeforeAfter(previousLastModificationDateValue, toBeAssertedLastModificationDateValue);
    }

    /**
     * Assert XML content is equal.<br/>
     * <p/>
     * This methods compares the attributes (if any exist) and either recursively compares the child elements (if any
     * exists) or the text content.<br/> Therefore, mixed content is NOT supported by this method.
     *
     * @param messageIn    The message printed if assertion fails.
     * @param expected     The expected XML content.
     * @param toBeAsserted The XML content to be compared with the expected content.
     * @throws Exception If anything fails.
     */
    public static void assertXmlEquals(final String messageIn, final Node expected, final Node toBeAsserted)
        throws Exception {
        // Assert both nodes are null or both nodes are not null
        if (expected == null) {
            assertNull(messageIn + "Unexpected node. ", toBeAsserted);
            return;
        }
        assertNotNull(messageIn + " Expected node. ", toBeAsserted);
        if (expected.equals(toBeAsserted)) {
            return;
        }
        String nodeName = getLocalName(expected);
        String message = messageIn;
        if (!message.contains("-- Asserting ")) {
            message = message + "-- Asserting " + nodeName + ". ";
        }
        else {
            message = message + "/" + nodeName;
        }
        // assert both nodes are nodes of the same node type
        // if thedocument container xslt directive than is the nodeName
        // "#document" is here compared
        assertEquals(message + " Type of nodes are different", expected.getNodeType(), toBeAsserted.getNodeType());
        if (expected.getNodeType() == Node.TEXT_NODE) {
            assertEquals(message + " Text nodes are different. ", expected.getTextContent().trim(), toBeAsserted
                .getTextContent().trim());
        }
        // assert attributes
        NamedNodeMap expectedAttributes = expected.getAttributes();
        NamedNodeMap toBeAssertedAttributes = toBeAsserted.getAttributes();
        if (expectedAttributes == null) {
            assertNull(message + " Unexpected attributes. [" + nodeName + "]", toBeAssertedAttributes);
        }
        else {
            assertNotNull(message + " Expected attributes. ", toBeAssertedAttributes);
            final int expectedNumberAttributes = expectedAttributes.getLength();
            for (int i = 0; i < expectedNumberAttributes; i++) {
                Node expectedAttribute = expectedAttributes.item(i);
                String expectedAttributeNamespace = expectedAttribute.getNamespaceURI();
                Node toBeAssertedAttribute;
                final String expectedAttributeNodeName = expectedAttribute.getNodeName();
                if (expectedAttributeNamespace != null) {
                    final String localName = expectedAttribute.getLocalName();
                    toBeAssertedAttribute =
                        toBeAssertedAttributes.getNamedItemNS(expectedAttributeNamespace, localName);
                    if (!expectedAttributeNodeName.startsWith("xmlns:") && !expectedAttributeNodeName.equals("xmlns")) {
                        assertNotNull(message + " Expected attribute " + expectedAttributeNodeName,
                            toBeAssertedAttribute);
                    }
                }
                else {
                    // not namespace aware parsed. Attributes may have different
                    // prefixes which are now part of their node name.
                    // To compare expected and to be asserted attribute, it is
                    // first it is tried to find the appropriate to be asserted
                    // attribute by the node name. If this fails, xpath
                    // selection is used after extracting the expected
                    // attribute name
                    toBeAssertedAttribute = toBeAssertedAttributes.getNamedItem(expectedAttributeNodeName);
                    if (toBeAssertedAttribute == null) {
                        final String attributeName = getLocalName(expectedAttribute);
                        final String attributeXpath = "@" + attributeName;
                        toBeAssertedAttribute = selectSingleNode(toBeAsserted, attributeXpath);
                    }
                    if (!expectedAttributeNodeName.startsWith("xmlns:") && !expectedAttributeNodeName.equals("xmlns")) {
                        assertNotNull(message + " Expected attribute " + expectedAttributeNodeName,
                            toBeAssertedAttribute);
                    }
                }
                if (!expectedAttributeNodeName.startsWith("xmlns:") && !expectedAttributeNodeName.equals("xmlns")) {
                    assertEquals(message + " Attribute value mismatch [" + expectedAttribute.getNodeName() + "] ",
                        expectedAttribute.getTextContent(), toBeAssertedAttribute.getTextContent());
                }
            }
        }
        // As mixed content (text + child elements) is not supported,
        // either the child elements or the text content have to be asserted.
        // Therefore, it is first tried to assert the children.
        // After that it is checked if children have been found. If this is not
        // the case, the text content is compared.
        NodeList expectedChildren = expected.getChildNodes();
        NodeList toBeAssertedChildren = toBeAsserted.getChildNodes();
        int expectedNumberElementNodes = 0;
        int toBeAssertedNumberElementNodes = 0;
        List<Node> previouslyAssertedChildren = new ArrayList<Node>();
        for (int i = 0; i < expectedChildren.getLength(); i++) {
            Node expectedChild = expectedChildren.item(i);
            if (expectedChild.getNodeType() == Node.ELEMENT_NODE) {
                expectedNumberElementNodes++;
                String expectedChildName = getLocalName(expectedChild);
                String expectedUri = expectedChild.getNamespaceURI();
                boolean expectedElementAsserted = false;
                for (int j = 0; j < toBeAssertedChildren.getLength(); j++) {
                    final Node toBeAssertedChild = toBeAssertedChildren.item(j);
                    // prevent previously asserted children from being
                    // asserted again
                    if (previouslyAssertedChildren.contains(toBeAssertedChild)) {
                        continue;
                    }
                    if (toBeAssertedChild.getNodeType() == Node.ELEMENT_NODE
                        && expectedChildName.equals(getLocalName(toBeAssertedChild))
                        && (expectedUri == null || expectedUri.equals(toBeAssertedChild.getNamespaceURI()))) {
                        expectedElementAsserted = true;
                        toBeAssertedNumberElementNodes++;
                        assertXmlEquals(message, expectedChild, toBeAssertedChild);
                        // add asserted child to list of asserted children to
                        // prevent it from being asserted again.
                        previouslyAssertedChildren.add(toBeAssertedChild);
                        break;
                    }
                }
                if (!expectedElementAsserted) {
                    fail(message + " Did not found expected corresponding element [" + nodeName + ", "
                        + expectedChildName + ", " + i + "]");
                }
            }
        }
        // check if any element node in toBeAssertedChildren exists
        // that has not been asserted. In this case, this element node
        // is unexpected!
        for (int i = 0; i < toBeAssertedChildren.getLength(); i++) {
            Node toBeAssertedChild = toBeAssertedChildren.item(i);
            // prevent previously asserted children from being
            // asserted again
            if (previouslyAssertedChildren.contains(toBeAssertedChild)) {
                continue;
            }
            if (toBeAssertedChild.getNodeType() == Node.ELEMENT_NODE) {
                fail(message + "Found unexpected element node [" + nodeName + ", " + getLocalName(toBeAssertedChild)
                    + ", " + i + "]");
            }
        }
        // if no children have been found, text content must be compared
        if (expectedNumberElementNodes == 0 && toBeAssertedNumberElementNodes == 0) {
            String expectedContent = expected.getTextContent();
            String toBeAssertedContent = toBeAsserted.getTextContent();
            assertEquals(message, expectedContent, toBeAssertedContent);
        }
    }

    public static void assertOrderNotAfter(final int lower, final int higher) throws Exception {
        if (lower > higher) {
            fail("Incorrect order: " + lower + " < " + higher + ".");
        }
    }

    public static void assertOrderNotAfter(final String lower, final String higher) throws Exception {
        if (lower.compareTo(higher) > 0) {
            LOGGER.debug("Incorrect order: " + lower + " < " + higher + ".");
        }
    }

    // #################################################################################################################
    // HTTP assertions
    // #################################################################################################################

    /**
     * Assert that the http request was successful.
     *
     * @param message The message printed if assertion fails.
     * @param httpRes The http method.
     */
    public static void assertHttpStatusOK(final String message, final HttpResponse httpRes) {
        assertHttpStatus(message, HttpServletResponse.SC_OK, httpRes);

    }

    /**
     * Assert that the http request was successful.
     *
     * @param message The message printed if assertion fails.
     * @param httpRes The http method.
     */
    public static void assertHttpStatusOfMethod(final String message, final HttpResponse httpRes) {
        // Delete Operation delivers Status code 206, HttpResponse doesn't
        // contain the original hhtp method
        if (httpRes.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
            assertHttpStatus(message, HttpServletResponse.SC_OK, httpRes);

        }
        else if ((httpRes.getStatusLine().getStatusCode() == HttpServletResponse.SC_NO_CONTENT)) {
            assertHttpStatus(message, HttpServletResponse.SC_NO_CONTENT, httpRes);
        }

    }

    /**
     * Assert that the http response has the expected status.
     *
     * @param message        The message printed if assertion fails.
     * @param expectedStatus The expected status.
     * @param httpRes        The http method.
     */
    public static void assertHttpStatus(final String message, final int expectedStatus, final HttpResponse httpRes) {
        assertEquals(message + " Wrong response status!", expectedStatus, httpRes.getStatusLine().getStatusCode());
    }

    // #################################################################################################################
    // Other assertions
    // #################################################################################################################

    /**
     * Assert that Map is empty.
     *
     * @param message The message printed if assertion fails.
     * @param map     The map to check.
     */
    public static void assertEmptyMap(final String message, final Map map) {
        if (map == null) {
            fail(message + " Map is null");
        }
        if (!map.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            for (Object key : map.keySet()) {
                buf.append(key);
            }
            fail(message + buf.toString());
        }
    }

    /**
     * Assert that the http request was successful.
     *
     * @param message The message printed if assertion fails.
     * @param httpRes The http method.
     */
    public static void assertContentTypeTextXmlUTF8OfMethod(final String message, final HttpResponse httpRes) {
        assertContentType(message, MediaType.TEXT_XML.toString(), "utf-8", httpRes);
    }

    public static void assertContentType(
        final String message, final String expectedContentType, final String expectedCharset, final HttpResponse httpRes) {
        Header[] headers = httpRes.getAllHeaders();
        String contentTypeHeaderValue = null;
        for (int i = 0; i < headers.length && contentTypeHeaderValue == null; ++i) {
            if (headers[i].getName().toLowerCase(Locale.ENGLISH).equals("content-type")) {
                contentTypeHeaderValue = headers[i].getValue();
            }
        }
        assertNotNull("No content-type header found, but expected 'content-type=" + expectedContentType + ";"
            + expectedCharset + "'", contentTypeHeaderValue);
        assertTrue("Wrong content-type found, expected '" + expectedContentType + "' but was '"
            + contentTypeHeaderValue + "'", contentTypeHeaderValue.contains(expectedContentType));
        assertTrue("Wrong charset found, expected '" + expectedCharset + "' but was '" + contentTypeHeaderValue + "'",
            contentTypeHeaderValue.contains(expectedContentType));
    }

    /**
     * Assert that the http response has the expected status.
     *
     * @param message     The message printed if assertion fails.
     * @param matchString expected String to match.
     * @param toTest      toTest.
     */
    public static void assertMatches(final String message, final String matchString, final String toTest) {
        if (!toTest.matches("(?s).*" + matchString + ".*")) {
            fail(message);
        }
    }

    /**
     * Asserts to objects are not equal.
     *
     * @param message      The message printed if assertion fails.
     * @param unexpected   The unexpected node
     * @param toBeAsserted The result to be asserted.
     */
    public static void assertNotEquals(final String message, final Object unexpected, final Object toBeAsserted) {

        if ((unexpected == null && toBeAsserted == null) || (unexpected != null && unexpected.equals(toBeAsserted))) {
            if (message == null || message.isEmpty()) {
                fail("Values are equal. Expected unequal");
            }
            else {
                fail(message);
            }
        }
    }

    /**
     * Assert that the before timestamp is lower than the after timestamp.
     *
     * @param before Timestamp before the event
     * @param after  Timestamp after event
     * @throws Exception Thrown if the before timestamp is not lower than after.
     */
    public static void assertDateBeforeAfter(final String before, final String after) throws Exception {
        Calendar oldModCal = getCalendarFromXmlDateString(before);
        Calendar newModCal = getCalendarFromXmlDateString(after);

        if (!oldModCal.before(newModCal)) {
            fail("Old last modification date is not before new last" + " modification date.");
        }
    }

    // #################################################################################################################
    // Helper - XPath
    // #################################################################################################################

    /**
     * Return the list of children of the node selected by the xPath.
     *
     * @param node  The node.
     * @param xPath The xPath.
     * @return The list of children of the node selected by the xPath.
     * @throws javax.xml.transform.TransformerException
     *          If anything fails.
     */
    public static NodeList selectNodeList(final Node node, final String xPath) throws TransformerException {
        return XPathAPI.selectNodeList(node, xPath);
    }

    /**
     * Return the list of children of the node selected by the xPath.
     *
     * @param node          The node.
     * @param xPath         The xPath.
     * @param namespaceNode The namespace node.
     * @return The list of children of the node selected by the xPath.
     * @throws TransformerException If anything fails.
     */
    public static NodeList selectNodeList(final Node node, final String xPath, final Node namespaceNode)
        throws TransformerException {
        return XPathAPI.selectNodeList(node, xPath, namespaceNode);
    }

    /**
     * Return the child of the node selected by the xPath.
     *
     * @param node  The node.
     * @param xPath The xPath.
     * @return The child of the node selected by the xPath.
     * @throws TransformerException If anything fails.
     */
    public static Node selectSingleNode(final Node node, final String xPath) throws TransformerException {
        return XPathAPI.selectSingleNode(node, xPath);
    }

    /**
     * Return the child of the node selected by the xPath.<br> This method includes an assert that the specified node
     * exists.
     *
     * @param node  The node.
     * @param xPath The xPath.
     * @return The child of the node selected by the xPath.
     * @throws Exception If anything fails.
     */
    public static Node selectSingleNodeAsserted(final Node node, final String xPath) throws Exception {
        Node result = selectSingleNode(node, xPath);
        assertNotNull("Node does not exist [" + xPath + "]", result);
        return result;
    }

    /**
     * Return the child of the node selected by the xPath.
     *
     * @param node          The node.
     * @param xPath         The xPath.
     * @param namespaceNode The namespace node.
     * @return The child of the node selected by the xPath.
     * @throws TransformerException If anything fails.
     */
    public static Node selectSingleNode(final Node node, final String xPath, final Node namespaceNode)
        throws TransformerException {
        return XPathAPI.selectSingleNode(node, xPath, namespaceNode);
    }

    // #################################################################################################################
    // Helper - Other
    // #################################################################################################################

    /**
     * Gets the local name (the node name without the namespace prefix) of the provided node.
     *
     * @param node The node to extract the name from.
     * @return Returns <code>node.getLocalName</code> if this is set, or the value of <code>node.getNodeName</code>
     *         without the namespace prefix.
     */
    private static String getLocalName(final Node node) {

        String name = node.getLocalName();
        if (name == null) {
            name = node.getNodeName().replaceAll(".*?:", "");
        }
        return name;
    }

    protected static String prepareAssertionFailedMessage(final String message) {
        final String msg;
        if (message == null) {
            msg = "";
        }
        else if (!message.endsWith(" ")) {
            msg = message + " ";
        }
        else {
            msg = message;
        }
        return msg;
    }

    /**
     * Creates a <code>java.util.Calendar</code> object from an xml dateTime string.
     *
     * @param dateTime The xml dateTime string.
     * @return The Calendar object.
     * @throws java.text.ParseException If the dateTime string can not be correctly parsed.
     */
    public static Calendar getCalendarFromXmlDateString(String dateTime) throws ParseException {
        Calendar cal;

        if (dateTime.length() >= 20 && dateTime.length() <= 23) {
            // no timezone
            // ensure 3 digits for millis
            int add = 23 - dateTime.length();
            while (add > 0) {
                dateTime += "0";
                add--;
            }
            dateTime += "+0000";
        }
        else if (dateTime.length() == 19) {
            // no timezone
            // not 3 digits for millis
            // no dot
            dateTime += ".000+0000";
        }
        // else if (dateTime.length() == 18) {
        // // no timezone
        // // not 3 digits for millis
        // // no dot
        // dateTime += "0.000+0000";
        // }
        // else if (dateTime.length() == 17) {
        // // no timezone
        // // not 3 digits for millis
        // // no dot
        // dateTime += "00.000+0000";
        // }
        // else if (dateTime.length() == 16) {
        // // no timezone
        // // not 3 digits for millis
        // // no dot
        // dateTime += ":00.000+0000";
        // }

        TimeZone gmt = TimeZone.getTimeZone("GMT");

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat tf = new SimpleDateFormat("HH:mm:ss.SSS");

        Date oldModDateDate = df.parse(dateTime);
        Date oldModDateTime = tf.parse(dateTime, new ParsePosition(11));

        // DateFormat f = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
        // dateTime = dateTime.replace('T', '-');
        // dateTime = dateTime.trim();

        cal = Calendar.getInstance();
        // cal.setTime(f.parse(dateTime));
        cal.setTimeZone(gmt);

        cal.setTime(oldModDateDate);
        long oldModDateDateMillis = cal.getTimeInMillis();

        cal.setTime(oldModDateTime);
        long oldModDateTimeMillis = cal.getTimeInMillis();

        long oldModDateMillis = oldModDateDateMillis + oldModDateTimeMillis;
        cal.setTimeInMillis(oldModDateMillis);

        return cal;
    }

    /**
     * Gets the last-modification-date attribute of the root element from the document.
     *
     * @param document The document to retrieve the value from.
     * @return Returns the attribute value.
     * @throws Exception If anything fails.
     */
    public static String getLastModificationDateValue(final Document document) throws Exception {
        return getRootElementAttributeValue(document, "last-modification-date");
    }

    /**
     * Gets the last-modification-date attribute of the root element from the document.
     *
     * @param document The document to retrieve the value from.
     * @return Returns the attribute value.
     * @throws Exception If anything fails.
     */
    public static DateTime getLastModificationDateValue2(final Document document) throws Exception {
        String dateString = getRootElementAttributeValue(document, "last-modification-date");
        if (dateString == null) {
            return null;
        }
        final Calendar calendar = DatatypeConverter.parseDate(dateString);
        return new DateTime(calendar.getTimeInMillis(), DateTimeZone.forTimeZone(calendar.getTimeZone()));
    }

    /**
     * Gets the creation-date element of the first element named "properties" from the document.
     *
     * @param document The document to retrieve the value from.
     * @return Returns the creation date value or <code>null</code>.
     * @throws Exception If anything fails.
     */
    public static String getCreationDateValue(final Document document) throws Exception {
        return getCreationDateValue(document, null);
    }

    /**
     * Gets the creation-date element of the specified properties element from the document.
     *
     * @param document The document to retrieve the value from.
     * @param xPath    The xpath to the parent element that contains the creation date element. If this is
     *                 <code>null</code>, the first element named "properties" will be selected.
     * @return Returns the creation date value or <code>null</code>.
     * @throws Exception If anything fails.
     */
    public static String getCreationDateValue(final Document document, final String xPath) throws Exception {
        String creationDateXpath;
        if (xPath == null) {
            creationDateXpath = "//properties[1]/creation-date";
        }
        else {
            creationDateXpath = xPath + "/creation-date";
        }
        final Node creationDateElement = selectSingleNode(document, creationDateXpath);
        if (creationDateElement == null) {
            return null;
        }
        else {
            return creationDateElement.getTextContent();
        }
    }

    /**
     * Gets the value of the specified attribute of the root element from the document.
     *
     * @param document      The document to retrieve the value from.
     * @param attributeName The name of the attribute whose value shall be retrieved.
     * @param namespaceURI  The namespace URI of the attribute.
     * @return Returns the attribute value.
     * @throws Exception If anything fails.
     */
    public static String getRootElementAttributeValueNS(
        final Document document, final String attributeName, final String namespaceURI) throws Exception {

        Node root = getRootElement(document);
        if (root.getNamespaceURI() != null) {
            // has been parsed namespace aware
            Node attr = root.getAttributes().getNamedItemNS(namespaceURI, attributeName);
            assertNotNull("Attribute not found [" + namespaceURI + ":" + attributeName + "]. ", attr);
            return attr.getTextContent();
        }
        else {
            // has not been parsed namespace aware.
            String xPath;
            if (attributeName.startsWith("@")) {
                xPath = "/*/" + attributeName;
            }
            else {
                xPath = "/*/@" + attributeName;
            }
            assertXmlExists("Attribute not found [" + xPath + "]. ", document, xPath);
            final Node attr = selectSingleNode(root, xPath);
            assertNotNull("Attribute not found [" + attributeName + "]. ", attr);
            return attr.getTextContent();
        }
    }

    /**
     * Gets the value of the specified attribute of the root element from the document.
     *
     * @param document      The document to retrieve the value from.
     * @param attributeName The name of the attribute whose value shall be retrieved.
     * @return Returns the attribute value.
     * @throws Exception If anything fails.
     */
    public static String getRootElementAttributeValue(final Document document, final String attributeName)
        throws Exception {

        Node root = getRootElement(document);

        // has not been parsed namespace aware.
        String xPath;
        if (attributeName.startsWith("@")) {
            xPath = "/*/" + attributeName;
        }
        else {
            xPath = "/*/@" + attributeName;
        }
        assertXmlExists("Attribute not found [" + attributeName + "]. ", document, xPath);
        final Node attr = selectSingleNode(root, xPath);
        assertNotNull("Attribute not found [" + attributeName + "]. ", attr);
        return attr.getTextContent();
    }

    /**
     * Gets the root element of the provided document.
     *
     * @param doc The document to get the root element from.
     * @return Returns the first child of the document htat is an element node.
     * @throws Exception If anything fails.
     */
    public static Element getRootElement(final Document doc) throws Exception {

        Node node = doc.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
            node = node.getNextSibling();
        }
        return null;
    }
}