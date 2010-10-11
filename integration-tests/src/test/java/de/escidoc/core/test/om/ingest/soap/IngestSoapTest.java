package de.escidoc.core.test.om.ingest.soap;

import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.ingest.IngestAbstractTest;
import org.apache.axis.AxisFault;
import org.junit.Test;

import static org.junit.Assert.fail;

public class IngestSoapTest extends IngestAbstractTest {

    public IngestSoapTest() {
	super(Constants.TRANSPORT_SOAP);
    }

    /**
     * Test unexpected parser exception instead of InvalidXmlException during
     * create (see issue INFR-911).
     *
     * @throws Exception
     *             Thrown if behavior is not as expected.
     */
    @Test
    public void testInvalidXml() throws Exception {

        /*
         * The infrastructure has thrown an unexpected parser exception during
         * creation if a non XML datastructur is send (e.g. String).
         */
        try {
            ingest("laber-rababer");
            fail("Missing Invalid XML exception");
        }
        catch (AxisFault e) {
            // that's ok
        }
    }

}
