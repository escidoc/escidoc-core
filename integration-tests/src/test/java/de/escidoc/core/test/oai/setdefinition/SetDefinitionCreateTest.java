package de.escidoc.core.test.oai.setdefinition;

import de.escidoc.core.common.exceptions.remote.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(value = Parameterized.class)
public class SetDefinitionCreateTest extends SetDefinitionTestBase {


    /**
     * @param transport
     *            The transport identifier.
     */
    public SetDefinitionCreateTest(final int transport) {
        super(transport);
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
    @Test
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
    @Test
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
        catch (final Exception e) {
            Class<?> ec = UniqueConstraintViolationException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }
}
