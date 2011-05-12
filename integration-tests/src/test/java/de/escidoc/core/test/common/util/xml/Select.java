package de.escidoc.core.test.common.util.xml;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of methods to select values from eSciDoc XML resources.
 * <p/>
 * FIXME methods are duplicated from Escidoc[.*]TestBase. If great refactoring of test environment starts than remove
 * this methods from the Escidoc[.*]TestBase classes to shrink them.
 *
 * @author Steffen Wagner
 */
public class Select {

    /**
     * Gets the objid attribute of the root element from the document.
     *
     * @param document  The document to retrieve the value from.
     * @return Returns the objid of the document.
     * @throws Exception If anything fails.
     */
    public static String getObjidValue(final Document document) throws Exception {

        return getObjidFromHref(getRootElementAttributeValueNS(document,
            de.escidoc.core.test.Constants.ATTRIBUTE_NAME_HREF, de.escidoc.core.test.Constants.XLINK_NS_URI));
    }

    /**
     * Remove version informaion from given objid.
     *
     * @param document  The document to retrieve the value from.
     * @return Returns the objid of the document without version suffix.
     * @throws Exception If anything fails.
     */
    public static String getObjidValueWithoutVersion(final Document document) throws Exception {

        String result = getObjidValue(document);
        return getObjidValueWithoutVersion(result);
    }

    /**
     * Get objid wihtout version suffix.
     *
     * @param objid Objid (with or without version suffix)
     * @return objid without version suffix
     * @throws Exception Thrown if pattern matching failed.
     */
    public static String getObjidValueWithoutVersion(final String objid) throws Exception {

        final Pattern patternIdWithoutVersion = Pattern.compile("([a-zA-Z]+:[0-9]+):[0-9]+");

        String result = objid;

        Matcher m = patternIdWithoutVersion.matcher(result);
        if (m.find()) {
            result = m.group(1);
        }
        return result;
    }

    /**
     * Gets the id from the provided uri (href).
     *
     * @param href The uri to extract the id from.
     * @return Returns the extracted id.
     */
    public static String getObjidFromHref(final String href) {

        return href.substring(href.lastIndexOf('/') + 1);
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
        final Node attr = XPathAPI.selectSingleNode(root, xPath);

        return attr.getTextContent();
    }

    /**
     * Gets the root element of the provided document.
     *
     * @param doc The document to get the root element from.
     * @return Returns the first child of the document that is an element node.
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
            final Node attr = XPathAPI.selectSingleNode(root, xPath);
            return attr.getTextContent();
        }
    }

}
