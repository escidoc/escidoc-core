package de.escidoc.core.test.om.item.contentTools;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.resources.BinaryContent;
import de.escidoc.core.test.common.resources.PropertiesProvider;
import de.escidoc.core.test.om.item.ItemTestBase;

public class ContentTestBase extends ItemTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentTestBase.class);

    private static final int BUFFER_SIZE = 0x4FFF;

    private static final String TRANSFORM_SERVICE_DIGILIB = "digilib";

    private static final String TEMP_FILE_NAME = "testBinaryData.img";

    private static final String TEMP_REF_FILE_NAME = "testBinaryData-ref.img";

    /**
     * Waits until every thread of the vector with threads is finished.
     *
     * @param threads Vector with threads
     * @param sleep   milli secounds between thread alive checks.
     * @throws InterruptedException Thrown if a thread was interrupted.
     */
    protected void waitForThreads(final Vector<Thread> threads, final int sleep) throws InterruptedException {
        while (threadAlive(threads)) {
            Thread.sleep(sleep);
        }

    }

    /**
     * Check if at least one thread is still running. (Is faster than getNoOfAliveThreads())
     *
     * @param threads Vector with threads.
     * @return true if at least one thread is still running.
     */
    protected boolean threadAlive(final Vector<Thread> threads) {
        boolean alive = false;

        Iterator<Thread> it = threads.iterator();
        while (it.hasNext()) {
            if (it.next().isAlive()) {
                return true;
            }
        }
        return alive;
    }

    /**
     * Counts the number of running threads.
     *
     * @param threads Vector with threads.
     * @return number of runnig threads.
     */
    protected int getNoOfAliveThreads(final Vector<Thread> threads) {

        int runningThreads = 0;
        Iterator<Thread> it = threads.iterator();
        while (it.hasNext()) {
            if (it.next().isAlive()) {
                runningThreads++;
            }
        }
        return runningThreads;
    }

    /**
     * Counts the number of running threads.
     *
     * @param threads               Vector with threads.
     * @param noOfMaxRunningThreads max number of simultanious threads
     * @throws Exception Thrown if set thread to sleep failed.
     */
    protected void waitForRunningThreads(final Vector<Thread> threads, final int noOfMaxRunningThreads)
        throws Exception {

        while (getNoOfAliveThreads(threads) >= noOfMaxRunningThreads) {
            Thread.sleep(2000);
        }
    }

    /**
     * Read the InputStream into an ByteArrayOutputStream.
     *
     * @param inputStream The InutStream.
     * @return The ByteArrayOutputStream of the InputStream.
     * @throws IOException Thrown if transfer of InputStream to ByteArrayOutputStream failed.
     */
    protected ByteArrayOutputStream readBinaryContent(final InputStream inputStream) throws IOException {

        byte[] buffer = new byte[BUFFER_SIZE];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while ((b = inputStream.read(buffer)) > 0) {
            out.write(buffer, 0, b);
        }
        return out;
    }

    /**
     * Get the minor type of ContentType.
     *
     * @param contentType The content type (image/jpeg)
     * @return minor content type (jpeg)
     */
    protected String getFileExtentionFromContentType(final String contentType) {

        int pos = contentType.indexOf("/");
        String ext = contentType.substring(pos + 1);
        return ext;
    }

    /**
     * Get username and password Base64 encoded.
     *
     * @param userInfo username and password ('username:password')
     * @return Base64 encoding of username and password.
     */
    protected static String userNamePasswordBase64(final String userInfo) {

        String encs = new String(Base64.encodeBase64(userInfo.getBytes()));
        return "Basic " + encs;
    }

    /**
     * Content URL to Fedora!
     *
     * @param componentId The Fedora objid.
     * @param versionDate Set to null to ignore it.
     * @param transformer The transformation service.
     * @param param       The transformation parameter.
     * @return The Url to the transformation service (digilib) including resource parameter
     * @throws Exception If getting properties values failed.
     */
    protected URL getDigilibUrl(
        final String componentId, final String versionDate, final String transformer, final String param)
        throws Exception {

        String contentUrl = getFedoraUrl(componentId, versionDate);

        String digilibServer = PropertiesProvider.getInstance().getProperty(PropertiesProvider.DIGILIB_SCALER_URL);

        URL url = null;
        if (transformer.equals(TRANSFORM_SERVICE_DIGILIB)) {
            url = new URL(digilibServer + "?fn=" + contentUrl + "&" + param);
        }
        else {
            throw new InvalidParameterException("The content transformation service " + transformer
                + " is not supported.");
        }

        return url;
    }

    /**
     * Prepare the URL to the content of an Fedora object.
     *
     * @param componentId The Fedora objid.
     * @param versionDate The version date of the object
     * @return The Fedora content Url for the object.
     */
    protected String getFedoraUrl(final String componentId, final String versionDate) {

        String fedoraUser = PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_USER);
        String fedoraPw = PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_PASSWORD);
        String auth = fedoraUser + ":" + fedoraPw + "@";

        String fedoraUrl = PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_URL);
        int pos = fedoraUrl.indexOf("://");
        String protocol = fedoraUrl.substring(0, pos + 3);
        String hostPart = fedoraUrl.substring(pos + 3);

        String contentUrl = protocol + auth + hostPart + "/get/" + componentId + "/content";

        if (versionDate != null) {
            contentUrl += "/" + versionDate;
        }

        return contentUrl;
    }

    /**
     * Retrieve content from the escidoc-core interface and store value in file. The expected content type is checked.
     *
     * @param itemId      The id of the Item.
     * @param componentId The id of the Component.
     * @param contentType The expected contentType
     * @return The file handler of the binary content.
     * @throws Exception Thrown if retrieveing failed or the delivered content type compares not to the parameter.
     */
    protected File retrieveContentFromFramework(final String itemId, final String componentId, final String contentType)
        throws Exception {

        BinaryContent ins = retrieveBinaryContent(itemId, componentId);

        assertEquals(contentType, ins.getMimeType());

        // write out file
        File temp = File.createTempFile(TEMP_FILE_NAME, "tmp");
        ByteArrayOutputStream barray = readBinaryContent(ins.getContent());
        FileOutputStream fos = new FileOutputStream(temp);
        fos.write(barray.toByteArray());
        fos.flush();
        fos.close();
        ins.getContent().close();

        return temp;
    }

    /**
     * Retrieve content of object from repositoy and store it into tempory file.
     *
     * @param objectId    The id of the Fedora object.
     * @param contentType The expected contentType
     * @return The file handler of the binary content.
     * @throws Exception Thrown if retrieveing failed or the delivered content type compares not to the parameter.
     */
    protected File retrieveContentFromRepository(final String objectId, final String contentType) throws Exception {

        String fedoraUrl =
            PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_URL) + "/get/" + objectId
                + "/content";

        String auth =
            PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_USER) + ":"
                + PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_PASSWORD);

        URL url = new URL(fedoraUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setRequestProperty("Authorization", userNamePasswordBase64(auth));
        conn.setUseCaches(false);
        conn.connect();

        assertEquals(contentType, conn.getContentType());

        ByteArrayOutputStream barray = readBinaryContent(conn.getInputStream());
        File temp = File.createTempFile(TEMP_REF_FILE_NAME, "tmp");

        FileOutputStream fos = new FileOutputStream(temp);
        fos.write(barray.toByteArray());
        fos.flush();
        fos.close();
        conn.disconnect();

        return temp;
    }

    /**
     * Remove file and only log if it's not possible.
     *
     * @param temp The to remove file.
     */
    protected void removeSilent(final File temp) {
        if (temp.exists() && !temp.delete()) {
            LOGGER.warn("Could not delete temporary file. " + temp.getPath());
        }
    }

    /**
     * Upload all files of local directory to staging service.
     *
     * @param path       The local path to the files.
     * @param repetition number of repetitions
     * @return The URLs of the content at the staging service.
     * @throws Exception If something failed.
     */
    protected HashMap<URL, File> uploadDirAsContent(final String path, final int repetition) throws Exception {

        HashMap<URL, File> urls = new HashMap<URL, File>();
        // path workaround (until relative path is fixed within the test
        // project)
        File p = new File(path);
        if (!p.exists()) {
            if (path.startsWith("/")) {
                p = new File("." + path);
            }
        }
        Vector<File> files = listDir(p);

        for (int i = 0; i < repetition; i++) {
            Iterator<File> it = files.iterator();

            while (it.hasNext()) {
                File next = it.next();
                URL url = uploadFileToStagingServlet(next);
                urls.put(url, next);
            }
        }

        LOGGER.debug(urls.size() + " files uploaded to staging");
        return urls;
    }

    /**
     * Get all files from the directory.
     *
     * @param dir The base directory.
     * @return Vector with all files of the directory (including sub-directories).
     */
    public Vector<File> listDir(final File dir) {

        Vector<File> filelist = new Vector<File>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    filelist.addAll(listDir(files[i]));
                }
                else {
                    filelist.add(files[i]);
                }
            }
        }
        return filelist;
    }

    /**
     * Prepare a component for create.
     *
     * @param file The file which is already uploaded to staging service or available via HTTP.
     * @param url  The URL of the uploaded or HTTP accessible content.
     * @return The String respresentation of the Component to create.
     */
    protected String prepareComponentAsItem(final File file, final URL url) {

        String mimeType = new MimetypesFileTypeMap().getContentType(file);
        String component =
            "<escidocComponents:component>\n" + "<escidocComponents:properties>\n" + "<prop:description>FileSize="
                + file.length() + "byte(s)</prop:description>\n" + "<prop:valid-status>valid</prop:valid-status>\n"
                + "<prop:visibility>public</prop:visibility>\n"
                + "<prop:content-category>pre-print</prop:content-category>\n" + "<prop:file-name>" + file.getName()
                + "</prop:file-name>\n" + "<prop:mime-type>" + mimeType + "</prop:mime-type>\n"
                + "</escidocComponents:properties>\n"

                + "<escidocComponents:content\n" + "xlink:href=\"" + url + "\"\n" + "storage=\"internal-managed\" />\n"

                + "</escidocComponents:component>";

        return component;
    }

    /**
     * Get all Components of an Item.
     *
     * @param itemId Objid of Item.
     * @return Vector with objids of Components.
     * @throws Exception Thrown if retrieve or extracting of objids failed.
     */
    protected Vector<String> getAllComponents(final String itemId) throws Exception {

        Vector<String> components = new Vector<String>();

        String itemXml = retrieve(itemId);
        Document itemDoc = EscidocAbstractTest.getDocument(itemXml);

        NodeList componentsIdList = selectNodeList(itemDoc, "/item/components/component/@href");

        for (int i = componentsIdList.getLength() - 1; i >= 0; i--) {
            String value = componentsIdList.item(i).getNodeValue();
            int pos = value.lastIndexOf("/");
            value = value.substring(pos + 1);
            components.add(value);
        }

        return components;
    }

    protected HashMap<String, String> getAllComponents2(final String itemId) throws Exception {

        HashMap<String, String> components = new HashMap<String, String>();

        String itemXml = retrieve(itemId);
        Document itemDoc = EscidocAbstractTest.getDocument(itemXml);

        NodeList componentsIdList = selectNodeList(itemDoc, "/item/components/component/@href");

        for (int i = componentsIdList.getLength() - 1; i >= 0; i--) {
            String value = componentsIdList.item(i).getNodeValue();
            String mimeType = null;
            int pos = value.lastIndexOf("/");
            value = value.substring(pos + 1);
            mimeType = "image/jpeg";
            components.put(value, mimeType);
        }

        return components;
    }
}
