package de.escidoc.core.test.oai.setdefinition;

import static org.junit.Assert.fail;

import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.notfound.ResourceNotFoundException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;

public class SetDefinitionDeleteTest extends SetDefinitionTestBase {
    String objid = null;
    
    /**
     * @param transport
     *            The transport identifier.
     */
    public SetDefinitionDeleteTest(final int transport) {
        super(transport);
    }

    /**
     * Set up test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    public void setUp() throws Exception {

        Document createdSetDefinitionDocument =
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
    public void tearDown() throws Exception {

        super.tearDown();
        // delete(itemId);
    }
    
    public void testDeleteSuccessfully() throws Exception {
      delete(objid);
      
      try{
          retrieve(objid);
          fail("No exception on retrieve of the deleted set definition.");
      }
      catch (Exception e) {
          Class<?> ec = ResourceNotFoundException.class;
          EscidocRestSoapTestsBase.assertExceptionType(ec, e);
      }
       
    }
}
