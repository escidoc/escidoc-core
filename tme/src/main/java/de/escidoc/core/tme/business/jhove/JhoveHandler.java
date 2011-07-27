package de.escidoc.core.tme.business.jhove;

import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.tme.business.TmeUtility;
import de.escidoc.core.tme.business.interfaces.JhoveHandlerInterface;
import edu.harvard.hul.ois.jhove.App;
import edu.harvard.hul.ois.jhove.JhoveBase;
import edu.harvard.hul.ois.jhove.JhoveException;
import edu.harvard.hul.ois.jhove.OutputHandler;
import org.esidoc.core.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Michael Schneider
 */
@Service("business.JhoveHandler")
public class JhoveHandler implements JhoveHandlerInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(JhoveHandlerInterface.class);

    /**
     * Application name.
     */
    private static final String NAME = "Jhove";

    /**
     * Application build date, YYYY, MM, DD.
     */
    private static final int[] DATE = { 2008, 2, 21 };

    /**
     * Application release number.
     */
    private static final String RELEASE = "1.1";

    /**
     * Application invocation syntax.
     */
    private static final String USAGE =
        "java " + NAME + " [-c config] " + "[-m module] [-h handler] [-e encoding] [-H handler] [-o output] "
            + "[-x saxclass] [-t tempdir] [-b bufsize] [-l loglevel] [[-krs] " + "dir-file-or-uri [...]]";

    /**
     * Copyright information.
     */
    private static final String RIGHTS =
        "Copyright 2004-2008 by the President and Fellows of Harvard College. "
            + "Released under the GNU Lesser General Public License.";

    /**
     * Relative path to the JHove configuration file within the class path.
     */
    private static final String CONFIG_FILE = "tme/jhove.conf";

    /**
     * SAX parser implementation.
     */
    private static final String SAX_PARSER = "org.apache.xerces.parsers.SAXParser";

    /**
     * Temporary file which contains the JHove configuration.
     */
    private final File jhoveConfigFile;

    /**
     * Create a new JHoveHandler object.
     * <p/>
     * JHove can only read its configuration from a file, not from an input stream. To preserve the possibility to load
     * the configuration from the class path the input stream is written to a temporary file which can then be used as
     * configuration file for JHove.
     *
     * @throws IOException Thrown if the configuration file could not be loaded or copied.
     */
    protected JhoveHandler() throws IOException {
        OutputStream outputStream = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (inputStream == null) {
                throw new FileNotFoundException(CONFIG_FILE + " not found!");
            }

            this.jhoveConfigFile = File.createTempFile(NAME, null);
            jhoveConfigFile.deleteOnExit();
            inputStream = new BufferedInputStream(inputStream);
            outputStream = new FileOutputStream(this.jhoveConfigFile);
            IOUtils.copyAndCloseInput(inputStream, outputStream);
        }
        finally {
            IOUtils.closeStream(outputStream);
        }
    }

    /**
     * Parse the given request XML and identify the format of the given file and extract the meta data.
     *
     * @param requests The list of files to examine.
     * @return An XML with JHove results for the files.
     * @throws SystemException Thrown in case of an internal error.
     * @throws TmeException    Thrown if JHove produced an error during meta data extraction.
     */
    @Override
    public String extract(final String requests) throws SystemException, TmeException {

        final String[] files = TmeUtility.parseRequests(requests);
        return callJhove(files);
    }

    /**
     * Identify the format of the given file and extract the meta data.
     *
     * @param files The list of files to examine.
     * @return An XML with JHove results for the files.
     * @throws SystemException Thrown in case of an internal error.
     * @throws TmeException    Thrown if JHove produced an error during meta data extraction.
     */
    private String callJhove(final String[] files) throws SystemException, TmeException {
        final StringBuilder result = new StringBuilder();
        BufferedReader outputFileReader = null;
        File outputFile = null;
        try {
            final JhoveBase je = new JhoveBase();

            je.init(jhoveConfigFile.getPath(), SAX_PARSER);

            final String handlerName = "xml";
            final OutputHandler handler = je.getHandler(handlerName);

            if (handler == null) {
                throw new JhoveException("Jhove configuration error! Handler '" + handlerName + "' not found!");
            }

            je.setTempDirectory(System.getProperty("java.io.tmpdir"));
            je.setBufferSize(-1);
            je.setChecksumFlag(false);
            je.setShowRawFlag(false);
            je.setSignatureFlag(false);
            outputFile = je.tempFile();
            je.dispatch(new App(NAME, RELEASE, DATE, USAGE, RIGHTS), null, null, handler, outputFile.getPath(), files);
            outputFileReader = new BufferedReader(new FileReader(outputFile));

            String line;
            while ((line = outputFileReader.readLine()) != null) {
                result.append(line);
                result.append('\n');
            }
        }
        catch (final IOException e) {
            throw new SystemException("Error in Jhove output handling!", e);
        }
        catch (final Exception e) {
            throw new TmeException(e.getMessage(), e);
        }
        finally {
            if (outputFile != null) {
                outputFile.delete();
            }
            if (outputFileReader != null) {
                try {
                    outputFileReader.close();
                }
                catch (final IOException e) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("Error on closing file stream.");
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Error on closing file stream.", e);
                    }
                }
            }
        }
        return result.toString();
    }
}
