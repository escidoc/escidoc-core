package de.escidoc.core.test.common.util.xml;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;

import static junit.framework.Assert.assertNotNull;

/**
 * Assert eSciDoc XML representation.
 *
 * @author Steffen Wagner
 */
public class Assert {

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

        Node result = XPathAPI.selectSingleNode(node, xPath);
        assertNotNull("Node does not exist [" + xPath + "]", result);
        return result;
    }

}
