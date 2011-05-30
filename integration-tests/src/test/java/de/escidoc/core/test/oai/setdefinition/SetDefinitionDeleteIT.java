package de.escidoc.core.test.oai.setdefinition;

import de.escidoc.core.common.exceptions.remote.application.notfound.ResourceNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.fail;

public class SetDefinitionDeleteIT extends SetDefinitionTestBase {

    String objid = null;

    /**
     * Set up test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {

        Document createdSetDefinitionDocument = createSuccessfully("escidoc_setdefinition_for_create.xml");
        objid = getObjidValue(createdSetDefinitionDocument);
    }

    @Test
    public void testDeleteSuccessfully() throws Exception {
        delete(objid);

        try {
            retrieve(objid);
            fail("No exception on retrieve of the deleted set definition.");
        }
        catch (final Exception e) {
            Class<?> ec = ResourceNotFoundException.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }

    }
}
