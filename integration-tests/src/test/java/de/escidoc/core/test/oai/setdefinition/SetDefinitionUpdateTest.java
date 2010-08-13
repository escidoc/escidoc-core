package de.escidoc.core.test.oai.setdefinition;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;

/**
 * 
 * @author ROF
 *
 */
public class SetDefinitionUpdateTest extends SetDefinitionTestBase {
    private String objid;

    private Document createdSetDefinitionDocument;

    /**
     * @param transport
     *            The transport identifier.
     */
    public SetDefinitionUpdateTest(final int transport) {
        super(transport);
    }

    /**
     * Set up test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected void setUp() throws Exception {

        createdSetDefinitionDocument =
            createSuccessfully("escidoc_setdefinition_for_create.xml");
        objid = getObjidValue(createdSetDefinitionDocument);
    }

    /**
     * Clean up after test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
        // delete(itemId);
    }

    /**
     * 
     * @throws Exception
     */
    public void testUpdateSuccessfullyNameAndDescription() throws Exception {
        final String retrievedLastModificationDate =
            getLastModificationDateValue(createdSetDefinitionDocument);
        String nameXPath = "/set-definition/properties/name";
        String descriptionXPath = "/set-definition/properties/description";
        String newName = "newName";
        String newDescription = "newDescription";
        Node toBeUpdated =
            substitute(createdSetDefinitionDocument, nameXPath, newName);
        toBeUpdated = substitute(toBeUpdated, descriptionXPath, newDescription);
        String createdSetDefinition = toString(toBeUpdated, false);
        String updatedSetDefinition = update(objid, createdSetDefinition);
        Document updatedSetDefinitionDocument =
            getDocument(updatedSetDefinition);
        String name =
            selectSingleNode(updatedSetDefinitionDocument, nameXPath)
                .getTextContent();
        String description =
            selectSingleNode(updatedSetDefinitionDocument, descriptionXPath)
                .getTextContent();
        final String updatedLastModificationDate =
            getLastModificationDateValue(updatedSetDefinitionDocument);
        assertEquals("Set definition name after update is wrong", name, newName);
        assertEquals("Set definition description after update is wrong",
            description, newDescription);
        assertDateBeforeAfter(retrievedLastModificationDate,
            updatedLastModificationDate);
    }

    /**
     * 
     * @throws Exception
     */
    public void testIgnoreUpdateOfSpecificationAndQuery() throws Exception {

        String specificationXPath = "/set-definition/specification";
        String queryXPath = "/set-definition/query";
        String newSpecification = "newSpecification";
        String newQuery = "newQuery";
        String oldSpecification =
            selectSingleNode(createdSetDefinitionDocument, specificationXPath)
                .getTextContent();
        String oldQuery =
            selectSingleNode(createdSetDefinitionDocument, queryXPath)
                .getTextContent();
        Node toBeUpdated =
            substitute(createdSetDefinitionDocument, specificationXPath,
                newSpecification);
        toBeUpdated = substitute(toBeUpdated, queryXPath, newQuery);
        String createdSetDefinition = toString(toBeUpdated, false);
        String updatedSetDefinition = update(objid, createdSetDefinition);
        Document updatedSetDefinitionDocument =
            getDocument(updatedSetDefinition);
        String specification =
            selectSingleNode(updatedSetDefinitionDocument, specificationXPath)
                .getTextContent();
        String query =
            selectSingleNode(updatedSetDefinitionDocument, queryXPath)
                .getTextContent();
        assertEquals("Set definition specification is changed after update",
            oldSpecification, specification);
        assertEquals("Set definition query is changed after update", oldQuery,
            query);
        assertEquals(
            "Creation date and last modification date are different. ",
            getLastModificationDateValue(createdSetDefinitionDocument),
            getLastModificationDateValue(updatedSetDefinitionDocument));
    }

    /**
     * 
     * @throws Exception
     */
    public void testUpdateWithIdNull() throws Exception {
        try {
            update(null, "");
            fail("No exception on update without id..");
        }
        catch (Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocRestSoapTestsBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }
}
