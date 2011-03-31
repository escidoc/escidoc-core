package de.escidoc.core.test.common.util;

import de.escidoc.core.test.common.compare.TripleStoreValue;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Steffen Wagner
 */
public class Version {

    /**
     * Check if resource is in latest version representation. version and latest version values are compared and then
     * (if they are equal) compared with the TripleStore.
     *
     * @param resource  Resource
     * @param transport Type of representation (REST/SOAP)
     * @return true if document is latest version, false otherwise
     * @throws Exception <ul> <li>If the document itself shows that this is not the latest version.</li> <li>If the
     *                   latest-version value not compares to the TripleStore</li> </ul>
     */
    public boolean isLatestVersion(final Document resource, final int transport) throws Exception {

        int versionNumber = getVersionNumber(resource, transport);
        int latestVersionNumber = getLatestVersionNumber(resource, transport);

        if (versionNumber > latestVersionNumber) {
            throw new Exception("Version number is larger than latest version number.");
        }
        else if (versionNumber == latestVersionNumber) {
            return true;
        }

        return false;
    }

    /**
     * Get the version number of a representation.
     *
     * @param resource  Versionated Resources (Item/Container)
     * @param transport What kind of resource (REST/SOAP)
     * @return version number
     * @throws Exception If extraction failed or number has invalid value.
     */
    public int getVersionNumber(final Document resource, final int transport) throws Exception {

        String versionNumber = null;
        Node versionNumberNode =
            XPathAPI
                .selectSingleNode(resource, "/item/properties/version/number |/container/properties/version/number");

        if (versionNumberNode == null) {
            throw new Exception("Missing version number in this representation.");
        }
        versionNumber = versionNumberNode.getTextContent();

        // check if it is a number
        int number = Integer.valueOf(versionNumber);
        if (number < 1) {
            throw new Exception("Invalid value of version number (<1)");
        }

        return number;
    }

    /**
     * Get latest version number of a representation.
     *
     * @param resource  Versionated Resources (Item/Container)
     * @param transport What kind of resource (REST/SOAP)
     * @return latest version number
     * @throws Exception If extraction failed or number has invalid value.
     */
    public int getLatestVersionNumber(final Document resource, final int transport) throws Exception {

        String latestVersionNumber = null;
        Node versionNumberNode =
            XPathAPI.selectSingleNode(resource,
                "/item/properties/latest-version/number |/container/properties/latest-version/number");

        if (versionNumberNode == null) {
            throw new Exception("Missing latest version number in this representation.");
        }
        latestVersionNumber = versionNumberNode.getTextContent();

        // check if it is a number
        int number = Integer.valueOf(latestVersionNumber);
        if (number < 1) {
            throw new Exception("Invalid value of latest version number (<1)");
        }

        TripleStoreValue tsv = new TripleStoreValue(transport);
        tsv.compareDocumentValueWithTripleStore(resource,
            "/item/properties/latest-version/number |/container/properties/latest-version/number",
            "/RDF/Description/number", "<http://escidoc.de/core/01/properties/version/number>");

        return number;
    }
}
