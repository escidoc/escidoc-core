package de.escidoc.core.test.oai.setdefinition;

import de.escidoc.core.common.exceptions.remote.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SetDefinitionCreateIT extends SetDefinitionTestBase {

    /**
     * Test successful creating an SetDefinition resource.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateSuccessfully() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_setdefinition_for_create.xml");

        assertEquals("Creation date and last modification date are different. ", assertCreationDateExists("",
            createdDocument), getLastModificationDateValue(createdDocument));
    }

    /**
     * Test declining creating an SetDefinition resource with a set specification, which is already exist.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateSetDefinitionWithNotUniqueSetSpecification() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_setdefinition_for_create.xml");

        assertEquals("Creation date and last modification date are different. ", assertCreationDateExists("",
            createdDocument), getLastModificationDateValue(createdDocument));
        String createdSetDefinition = toString(createdDocument, false);
        try {
            create(createdSetDefinition);
            fail("No exception on create set definition with not unique set specification.");
        }
        catch (final Exception e) {
            Class<?> ec = UniqueConstraintViolationException.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }
}
