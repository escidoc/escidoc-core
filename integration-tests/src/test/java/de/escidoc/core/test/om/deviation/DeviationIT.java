package de.escidoc.core.test.om.deviation;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.OmTestBase;
import de.escidoc.core.test.om.deviation.DeviationTestBase;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertTrue;

/**
 * @author Michael Hoppe
 */
public class DeviationIT extends DeviationTestBase {

    /**
     * Test retrieving the fedora describe xml.
     *
     * @throws Exception e
     */
    @Test
    public void testDescribe() throws Exception {
        String describe = getDescribe();
        assertTrue(describe.contains("repositoryName"));
    }

    /**
     * Test retrieving an item-xml.
     *
     * @throws Exception e
     */
    @Test
    public void testExport() throws Exception {

        String toBeCreatedXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_test_item0.xml");

        String createdXml = create(toBeCreatedXml);
        String id = getObjidValue(createdXml);
        String itemXml = export(id);
        assertXmlValidItem(itemXml);

    }

    /**
     * Test retrieving an content.
     *
     * @throws Exception e
     */
    @Test
    public void testDatastreamDissimination() throws Exception {

        String toBeCreatedXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_test_item0.xml");

        String createdXml = create(toBeCreatedXml);
        Document document = EscidocAbstractTest.getDocument(createdXml);
        String id = getObjidValue(createdXml);
        String componentId = getObjidValue(document, OmTestBase.XPATH_ITEM_COMPONENTS + "/" + NAME_COMPONENT);
        String content =
            (String) getDatastreamDissimination(id, Constants.ITEM_BASE_URI + "/" + id + "/" + Constants.SUB_COMPONENT
                + "/" + componentId + "/" + Constants.SUB_CONTENT);
        assertTrue(content.contains("Antriebsvorrichtung"));

    }

}
