package de.escidoc.core.test.oai.setdefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.security.client.PWCallback;

public class SetDefinitionRetrieveTest extends SetDefinitionTestBase {
private String objid;
private Document createdSetDefinitionDocument;
private String specification = null;
private String lmd = null;
    /**
     * @param transport
     *            The transport identifier.
     */
    public SetDefinitionRetrieveTest(final int transport) {
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

        createdSetDefinitionDocument =
            createSuccessfully("escidoc_setdefinition_for_create.xml");
        objid = getObjidValue(createdSetDefinitionDocument);
        specification = selectSingleNode(createdSetDefinitionDocument, 
            "/set-definition/specification").getTextContent();
        lmd = selectSingleNode(createdSetDefinitionDocument, 
        "/set-definition/@last-modification-date").getNodeValue();
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
    public void testRetrieveSuccessfully() throws Exception {
        PWCallback.setHandle(PWCallback.ANONYMOUS_HANDLE);
        String retrieved = retrieve(objid);
        Document retrievedDocument = getDocument(retrieved, false);
        String retrievedSpecification = selectSingleNode(retrievedDocument,
            "/set-definition/specification").getTextContent();
        String retrievedLmd = selectSingleNode(retrievedDocument,
        "/set-definition/@last-modification-date").getNodeValue();
        assertEquals("Retrieved set definition specification is wrong", this.specification, retrievedSpecification);
        assertEquals("Retrieved set definition last modofication date specification is wrong", this.lmd, retrievedLmd);
        PWCallback.setHandle(PWCallback.ADMINISTRATOR_HANDLE);
    }
    
    public void testRetrieveWithIdNull() throws Exception {
        try {
            retrieve(null);
            fail("No exception on retrieve without id..");
        }
        catch (Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocRestSoapTestsBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
        
    }
    
   
   
}
