package de.escidoc.core.test;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceBase {

    public static final String NAME_OBJID = "objid";

    public static final String PART_XLINK_HREF = "/@href";

    public static final Pattern PATTERN_OBJID_ATTRIBUTE = Pattern.compile("objid=\"([^\"]*)\"");

    public static final Pattern PATTERN_OBJID_HREF_ATTRIBUTE = Pattern.compile("href=\"/ir/[^/]+/([^\"]*)\"");

    public static final String VERSION_SUFFIX_SEPARATOR = ":";

    /**
     * Gets the objid attribute of the root element from the document.
     *
     * @param document The document to retrieve the value from.
     * @return Returns the attribute value.
     * @throws Exception If anything fails.
     */
    public String getObjidValue(final Document document) throws Exception {

        return getRootElementAttributeValue(document, NAME_OBJID);
    }

    /**
     * Gets the objid attribute of the element selected in the provided node.<br> It tries to get the objid attribute of
     * the selected node. If this fails, it tries to get the xlink:href attribute. If both fails, an assertion exception
     * is "thrown".
     *
     * @param node  The node to select an element from.
     * @param xPath The xpath to select the element in the provided node.
     * @return Returns the attribute value.
     * @throws Exception If anything fails.
     */
    public String getObjidValue(final Node node, final String xPath) throws Exception {

        Node selected = selectSingleNode(node, xPath);
        if (selected == null) {
            throw new Exception("No Element selected to retrieve the object id from");
        }
        NamedNodeMap attributes = selected.getAttributes();
        if (attributes == null) {
            throw new Exception("Selected node has no attributes (not an element?) ");
        }
        Node objidAttr = attributes.getNamedItem(NAME_OBJID);
        if (objidAttr != null) {
            return objidAttr.getTextContent();
        }
        else {
            objidAttr = selectSingleNode(selected, "." + PART_XLINK_HREF);
            if (objidAttr == null) {
                throw new Exception("Selected node neither has an objid attribute nor an xlink href attribute");
            }
            return getObjidFromHref(objidAttr.getTextContent());
        }
    }

    /**
     * Gets the objid attribute of the root element from the Xml.
     *
     * @param xml The xml representation of an object to retrieve the value from.
     * @return Returns the objid.
     * @throws Exception If anything fails.
     */
    public String getObjidValue(final String xml) throws Exception {

        Matcher m = PATTERN_OBJID_ATTRIBUTE.matcher(xml);
        if (m.find()) {
            return m.group(1);
        }
        else {
            m = PATTERN_OBJID_HREF_ATTRIBUTE.matcher(xml);
            if (m.find()) {
                return m.group(1);
            }
        }

        throw new Exception("Missing objid/href in provided xml data");
    }

    /**
     * Gets the id from the provided uri (href).
     *
     * @param href The uri to extract the id from.
     * @return Returns the extracted id.
     */
    public static String getObjidFromHref(final String href) {

        String grantId = href.substring(href.lastIndexOf('/') + 1);
        return grantId;
    }

    /**
     * Get objId with version part of latest version of document.
     *
     * @param xml The Item XML.
     * @return The object id of the latest version.
     * @throws Exception If anything fails.
     */
    public final String getLatestVersionObjidValue(final String xml) throws Exception {

        Node latestVersionNode =
            selectSingleNode(EscidocAbstractTest.getDocument(xml), "//properties/latest-version/number");
        String id = getObjidValue(xml) + VERSION_SUFFIX_SEPARATOR + latestVersionNode.getTextContent();
        return (id);
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
        assertXmlExists(document, xPath);
        final Node attr = selectSingleNode(root, xPath);
        if (attr == null) {
            throw new Exception("Attribute not found [" + attributeName + "]. ");
        }
        String value = attr.getTextContent();
        return value;
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
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
            node = node.getNextSibling();
        }
        return null;
    }

    /**
     * Assert that the Element/Attribute selected by the xPath exists.
     *
     * @param node  The Node.
     * @param xPath The xPath.
     * @throws Exception If anything fails.
     */
    public static void assertXmlExists(final Node node, final String xPath) throws Exception {

        NodeList nodes = selectNodeList(node, xPath);

        if (nodes.getLength() < 1) {
            throw new Exception("Node " + xPath + " is missing.");
        }
    }

    /**
     * Return the list of children of the node selected by the xPath.
     *
     * @param node  The node.
     * @param xPath The xPath.
     * @return The list of children of the node selected by the xPath.
     * @throws TransformerException If anything fails.
     */
    public static NodeList selectNodeList(final Node node, final String xPath) throws TransformerException {
        NodeList result = XPathAPI.selectNodeList(node, xPath);
        return result;
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

        Node result = XPathAPI.selectSingleNode(node, xPath);
        return result;
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
     * @param xml The document to retrieve the value from.
     * @return Returns the attribute value.
     * @throws Exception If anything fails.
     */
    public static String getLastModificationDateValue(final String xml) throws Exception {

        Document document = EscidocAbstractTest.getDocument(xml);
        return getLastModificationDateValue(document);
    }

    /**
     * Gets the last modification date from the Resource.
     *
     * @param resource The Resource.
     * @return last-modification-date
     * @throws Exception Thrown if anything fails.
     */
    public String getTheLastModificationDate(final Document resource) throws Exception {

        // get last-modification-date
        NamedNodeMap atts = resource.getDocumentElement().getAttributes();
        Node lastModificationDateNode = atts.getNamedItem("last-modification-date");
        return (lastModificationDateNode.getNodeValue());

    }

    /**
     * Get the last modification XML param.
     *
     * @param comment              The comment.
     * @param lastModificationDate The timestamp of the resource.
     * @return Returns the created task param xml.
     * @throws Exception Thrown if anything fails.
     */
    public String getTheLastModificationParam(final String comment, final String lastModificationDate) throws Exception {

        String param = "<param last-modification-date=\"" + lastModificationDate + "\" ";
        param += ">";
        if (comment != null) {
            param += "<comment>" + comment + "</comment>";
        }
        param += "</param>";

        return param;
    }

}
