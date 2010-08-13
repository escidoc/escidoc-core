package de.escidoc.core.test.oai.setdefinition;

import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;

public class SetDefinitionCreateTest extends SetDefinitionTestBase {


    /**
     * @param transport
     *            The transport identifier.
     */
    public SetDefinitionCreateTest(final int transport) {
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

        super.setUp();
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
     * Test successful creating an SetDefinition resource.
     * 
     * @test.name Create SetDefinition
     * @test.input SetDefinition XML representation
     * @test.inputDescription: Valid XML representation of the SetDefinition.
     * @test.expected: XML representation of the created SetDefinition
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testCreateSuccessfully() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_setdefinition_for_create.xml");

        assertEquals(
            "Creation date and last modification date are different. ",
            assertCreationDateExists("", createdDocument),
            getLastModificationDateValue(createdDocument));
    }
    
    /**
     * Test declining creating an SetDefinition resource with a set specification,
     * which is already exist.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testCreateSetDefinitionWithNotUniqueSetSpecification() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_setdefinition_for_create.xml");

        assertEquals(
            "Creation date and last modification date are different. ",
            assertCreationDateExists("", createdDocument),
            getLastModificationDateValue(createdDocument));
        String createdSetDefinition = toString(createdDocument, false);
        try{
            create(createdSetDefinition);
            fail("No exception on create set definition with not unique set specification.");
        }
        catch (Exception e) {
            Class<?> ec = UniqueConstraintViolationException.class;
            EscidocRestSoapTestsBase.assertExceptionType(ec, e);
        }
    }
}
