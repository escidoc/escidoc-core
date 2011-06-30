/**
 * 
 */
package de.escidoc.core.test.oum.organizationalunit;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMdRecordException;

/**
 * @author Marko Voß
 * 
 */
@RunWith(value = Parameterized.class)
public class OuNegativeTest extends OrganizationalUnitTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public OuNegativeTest(final int transport) {
        super(transport);
    }

    @Test(expected = MissingMdRecordException.class)
    public void testMissingMdRecordExceptionOnCreate() throws Exception {
        final String templateName = "organizational-unit-minimal-for-create-01.xml";
        String organizationalUnitXml = getExampleTemplate(templateName);

        final Document doc = getDocument(organizationalUnitXml);
        final Element mdRecord = (Element) selectSingleNode(doc, "/organizational-unit/md-records/md-record");
        mdRecord.setAttribute("name", "foo");

        organizationalUnitXml = toString(doc, false);

        create(organizationalUnitXml);
    }

    @Test(expected = MissingMdRecordException.class)
    @Ignore("See INFR-1016 for reference")
    public void testMissingMdRecordExceptionOnUpdate() throws Exception {
        final String templateName = "organizational-unit-minimal-for-create-01.xml";
        String organizationalUnitXml = getExampleTemplate(templateName);

        organizationalUnitXml = create(organizationalUnitXml);

        final Document doc = getDocument(organizationalUnitXml);
        final Element mdRecord = (Element) selectSingleNode(doc, "/organizational-unit/md-records/md-record");
        mdRecord.setAttribute("name", "foo");

        organizationalUnitXml = toString(doc, false);

        update(getObjidValue(organizationalUnitXml), organizationalUnitXml);
    }
}
