import org.junit.Test;

import de.escidoc.core.common.business.fedora.Constants;
import static org.junit.Assert.*;

/**
 * Dump test.
 * 
 * These test package has to include funtional test only. No ear is ready, no
 * database is created, no JBoss is running! That's why integration tests are to
 * settle at escidoc.ear integrration tests!
 * 
 * @author SWA
 * 
 */
public class DumpTest {

    @Test
    public void testHelloWorld() {

        Constants c = new Constants();

        assertEquals("Just a test to see if everything works", "deleted",
            c.MIME_TYPE_DELETED);
    }
}