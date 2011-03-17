import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Unit tests for methods of XmlUtility class.
 *  
 * @author SWA
 *
 */
public class XmlUtilityTest {

    @Test
    public void getObjidId() throws Exception {

        assertEquals("Objid not well separated.", "escidoc:123",
            XmlUtility.getObjidWithoutVersion("escidoc:123:45"));
    }
}