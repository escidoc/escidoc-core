/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.sb;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pdfbox.util.PDFMergerUtility;

import de.escidoc.core.test.security.client.PWCallback;
import de.fiz.edb.xml2pdf.XML2PDFMain;

/**
 * get all thieme xml-documents, put them in fedora and publish them.
 * 
 * @author MIH
 * 
 */
public class ThiemeIndexingTest extends SearchTestBase {

    private final ItemHelper item;

    //number of documents to generate
    private final int numDocuments = 84;
    
    //document to start with 
    private final int startDocument = 0;
    
    // Path to webserver, where fulltexts are saved
    private final String fulltextWebserver =
        "http://localhost:8082/ir/";

    // Path where fulltexts should be saved
    private final String webserverRoot =
        "C:\\workarea\\fedora3.1\\tomcat\\webapps\\ROOT\\ir";

    // Directory with patent-xmls
    private final String patentsFilePath =
        "C:\\Eigene Dateien\\patdoks\\a_dok_2003german.xml";

    // Directory with patent-xmls
    private final String patentsDirectory = "C:\\Eigene Dateien\\patdoks\\2005";

    // Directory where to save the transformed dokuments
    // private String transformedDirectory =
    // "C:\\eprojects\\eSciDocCoreTest\\src\\java\\de\\escidoc\\core\\test\\om\\template\\item";
    private final String transformedDirectory =
        webserverRoot + "\\items";

    private final String xmlDirectory =
        webserverRoot + "\\xml";

    private final String pdfDirectory =
        webserverRoot + "\\pdf";

    // Directory with stylesheets
    private final String stylesheetDirectory =
        "C:\\eprojects\\eSciDocCoreTest\\src\\java\\de\\escidoc\\core\\test\\sb\\";

    private final String restPatentsStylesheet =
        stylesheetDirectory + "patents_admin_rest.xsl";

    private final String soapPatentsStylesheet =
        stylesheetDirectory + "patents_admin_soap.xsl";
    
    private String chineseStringFulltext;
    
    private String chineseStringMetadata;
    
    private String specialSignAe;
    
    private String specialSignOe;
    
    private String specialSignUe;
    
    private String specialSignSmallAe;
    
    private String specialSignSmallOe;
    
    private String specialSignSmallUe;
    
    private String specialSignSmallA;
    
    private final Pattern xmlInsertPattern = Pattern.compile("(.*?<SDODE.*?>.*?<P.*?>)(.*?)(<.*)");
    private Matcher xmlInsertMatcher = xmlInsertPattern.matcher("");
    
    private final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    /**
     * @param transport
     *            The transport identifier.
     */
    public ThiemeIndexingTest(final int transport) {
        super(transport);
        item = new ItemHelper(transport);
        chineseStringFulltext = "\u7b80\u4f53\u4e2d\u6587\u7f51\u9875";
        chineseStringMetadata = "\u5161\u4e5f\u5305\u56e0\u6c98\u6c13\u4fb7\u67f5\u82d7\u5b6b\u5b6b\u8ca1";
        specialSignAe = "\u00c4";
        specialSignOe = "\u00d6";
        specialSignUe = "\u00dc";
        specialSignSmallAe = "\u00e4";
        specialSignSmallOe = "\u00f6";
        specialSignSmallUe = "\u00fc";
        specialSignSmallA = "a";
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void initialize() throws Exception {
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
    }

    /**
     * Test the search.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testIndexing() throws Exception {
        // Get all xml-documents from thieme and save them in filesystem
        // saveThiemeDocuments();
        emptyDirectory(transformedDirectory);
        // emptyDirectory(webserverRoot + "/pdf");
        emptyDirectory(xmlDirectory);
        savePatentDocuments(numDocuments, startDocument);
        //zipItems();
        // HashMap zipDirs = new HashMap();
        // zipDirs.put(transformedDirectory, "items");
        // zipDirs.put(webserverRoot + "/pdf", "pdf");
        // zipDirs.put(webserverRoot + "/xml", "xml");
        // zipDirectories(webserverRoot + "/fulltexts.zip", zipDirs);
        // displayFirstLine();
        // generatePatentPdfs(webserverRoot + "\\pdf");
        // /////////////////////////////////////////////////////////////////////

        // Iterate Directories, get all xml files and create item in fedora/////
        // iterate(new File(patentsDirectory + "\\transformed"));
        // /////////////////////////////////////////////////////////////////////

        // String xml = item.retrieve("escidoc:506");
        // DocumentBuilderFactory factory = null;
        // DocumentBuilder builder = null;
        // org.w3c.dom.Document doc = null;
        // factory = DocumentBuilderFactory.newInstance();
        // try {
        // builder = factory.newDocumentBuilder();
        // doc = builder.parse(new InputSource(new StringReader(xml)));
        // } catch (Exception e) {
        // }
        // xml = xml.replaceAll(">", ">\n");
    }

    private void emptyDirectory(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                files[i].delete();
            }
            else {
                emptyDirectory(files[i].getAbsolutePath());
            }
        }
    }

    /**
     * save all patent xmls and pdfs into file-system.
     * 
     */
    private void generatePatentPdfs(String path) {
        XML2PDFMain.main(new String[] { path });

        PDFMergerUtility pdfMerger = null;
        File pdfDir = new File(pdfDirectory);
        File[] pdffiles = pdfDir.listFiles();
        for (int i = 0; i < pdffiles.length; i++) {
            if (pdffiles[i].getName().endsWith("pdf") 
                    && !pdffiles[i].getName().startsWith("Chinese")) {
                pdfMerger = new PDFMergerUtility();
                pdfMerger.setDestinationFileName(
                        pdfDirectory + "\\" + pdffiles[i].getName());
                pdfMerger.addSource(pdffiles[i]);
                pdfMerger.addSource(new File(pdfDirectory + "\\ChineseString.pdf"));
                try {
                    pdfMerger.mergeDocuments();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        
    }

    /**
     * save all patent xmls and pdfs into file-system.
     * 
     */
    private void splitPatentsFile(int numDoks, int startDocument) {
        File patentFile = new File(patentsFilePath);

    }

    /**
     * save all patent xmls and pdfs into file-system.
     * 
     */
    private void zipItems() throws Exception {
        ZipOutputStream zipOut = new ZipOutputStream(
                new FileOutputStream(
                        webserverRoot));
        zip(new File(transformedDirectory), zipOut, false, null);
        zipOut.flush();
        zipOut.finish();
        zipOut.close();
        
        //delete files
        File itemDir = new File(transformedDirectory);
        File[] files = itemDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    /**
     * save all patent xmls and pdfs into file-system.
     * 
     */
    private void zip(File file, 
            ZipOutputStream zipOutputStream, 
            boolean withPath, 
            Pattern pattern) throws Exception {
        byte[] buffer = new byte[8192];
        int len = 0;
        if (!file.isDirectory()) {
            Matcher matcher = null;
            if (pattern != null) {
                matcher = pattern.matcher(file.getName());
            }
            if ((matcher != null && matcher.matches()) || matcher == null) {
                String path = null;
                if (withPath) {
                    path = file.getAbsolutePath();
                } else {
                    path = file.getName();
                }
                ZipEntry entry = new ZipEntry(path);
                zipOutputStream.putNextEntry(entry);

                FileInputStream fis = new FileInputStream(file);
                while ((len = fis.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len);
                }
                fis.close();
                zipOutputStream.closeEntry();
            }
        } else {
            File[] children = file.listFiles();
            for (int i = 0; i < children.length; i++) {
                File child = children[i];
                zip(child, zipOutputStream, withPath, pattern);
            }
        }
    }

    /**
     * save all patent xmls and pdfs into file-system.
     * 
     */
    private void savePatentDocuments(int numDoks, int startDocument) {
        splitPatentsFile(numDoks, startDocument);
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer restTransformer = null;
        Transformer soapTransformer = null;
        try {
            restTransformer =
                tFactory.newTransformer(new StreamSource(new File(
                    restPatentsStylesheet)));
            soapTransformer =
                tFactory.newTransformer(new StreamSource(new File(
                    soapPatentsStylesheet)));
        }
        catch (TransformerConfigurationException e1) {
            log.error(e1);
        }
        File dir = new File(pdfDirectory);
        File[] pdffiles = dir.listFiles();
        for (int i = 0; i < pdffiles.length; i++) {
            if (pdffiles[i].getName().endsWith("pdf") 
                    && !pdffiles[i].getName().startsWith("Chinese")) {
                pdffiles[i].delete();
            }
        }
        File[] files = new File(patentsDirectory).listFiles();
        int j = 0;
        OutputStreamWriter ostr = null;
        BufferedReader br = null;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".xml")) {
                try {
                    String absolutePath = files[i].getAbsolutePath();
                    ostr =
                        new OutputStreamWriter(new FileOutputStream(
                            pdfDirectory + "\\fileForPdfGeneration.xml",
                            false), DEFAULT_CHARSET);
                    br =
                        new BufferedReader(new InputStreamReader(
                            new FileInputStream(absolutePath), DEFAULT_CHARSET));
                    String currentLine = new String("");
                    int day = 28;
                    int month = 1;
                    int year = 1980;
                    int user = 1;
                    int statusCounter = 0;
                    String[] stati = new String[]{"pending", "submitted", "released"};
                    while (j <= numDoks
                        && (currentLine = br.readLine()) != null) {
                        currentLine = xmlInsertMatcher.reset(currentLine)
                                    .replaceFirst("$1 xmlfulltext " + chineseStringFulltext + " $2$3");
                        String pdfLine = xmlInsertMatcher.reset(currentLine)
                        .replaceFirst("$1 pdffulltext $2$3").replaceFirst("xmlfulltext", "");
                        int documentNum = j + startDocument;
                        ostr.write(xmlHeader + pdfLine + "\n");
                        ostr.flush();
                        String dirname = transformedDirectory + "\\";
                        String restFilename =
                            "escidoc_search_item" + documentNum + "_rest.xml";
                        String soapFilename =
                            "escidoc_search_item" + documentNum + "_soap.xml";
                        files[i].getName().replaceAll("\\.xml",
                            documentNum + "\\.xml");
                        String dayStr = "";
                        String monthStr = "";
                        if (day < 10) {
                            dayStr = "0" + day;
                        }
                        else {
                            dayStr = "" + day;
                        }
                        if (month < 10) {
                            monthStr = "0" + month;
                        }
                        else {
                            monthStr = "" + month;
                        }
                        String date =
                            year + "-" + monthStr + "-" + dayStr;

                        restTransformer.setParameter("fulltextPath", fulltextWebserver);
                        soapTransformer.setParameter("fulltextPath", fulltextWebserver);
                        restTransformer.setParameter("specialLetter", "");
                        soapTransformer.setParameter("specialLetter", "");
                        if (j == 0) {
                            restTransformer.setParameter("titleAppendix", "titleappendi+x titleappendi-x  + -");
                            soapTransformer.setParameter("titleAppendix", "titleappendi+x titleappendi-x + -");
                            restTransformer.setParameter("alternativeAppendix", "alternativeappendix+ alternativeappendix-");
                            soapTransformer.setParameter("alternativeAppendix", "alternativeappendix+ alternativeappendix-");
                            restTransformer.setParameter("specialLetter", specialSignAe);
                            soapTransformer.setParameter("specialLetter", specialSignAe);
                        }
                        else if (j == 1) {
                            restTransformer.setParameter("titleAppendix", "titleappendi&x titleappendi|x & |");
                            soapTransformer.setParameter("titleAppendix", "titleappendi&x titleappendi|x & |");
                            restTransformer.setParameter("alternativeAppendix", "alternativeappendix& alternativeappendix|");
                            soapTransformer.setParameter("alternativeAppendix", "alternativeappendix& alternativeappendix|");
                            restTransformer.setParameter("specialLetter", specialSignOe);
                            soapTransformer.setParameter("specialLetter", specialSignOe);
                        }
                        else if (j == 2) {
                            restTransformer.setParameter("titleAppendix", "titleappendi!x titleappendi(x ! (");
                            soapTransformer.setParameter("titleAppendix", "titleappendi!x titleappendi(x ! (");
                            restTransformer.setParameter("alternativeAppendix", "alternativeappendix! alternativeappendix(");
                            soapTransformer.setParameter("alternativeAppendix", "alternativeappendix! alternativeappendix(");
                            restTransformer.setParameter("specialLetter", specialSignUe);
                            soapTransformer.setParameter("specialLetter", specialSignUe);
                        }
                        else if (j == 3) {
                            restTransformer.setParameter("titleAppendix", "titleappendi)x titleappendi{x ) {");
                            soapTransformer.setParameter("titleAppendix", "titleappendi)x titleappendi{x ) {");
                            restTransformer.setParameter("alternativeAppendix", "alternativeappendix) alternativeappendix{");
                            soapTransformer.setParameter("alternativeAppendix", "alternativeappendix) alternativeappendix{");
                            restTransformer.setParameter("specialLetter", specialSignSmallAe);
                            soapTransformer.setParameter("specialLetter", specialSignSmallAe);
                        }
                        else if (j == 4) {
                            restTransformer.setParameter("titleAppendix", "titleappendi}x titleappendi[x } [");
                            soapTransformer.setParameter("titleAppendix", "titleappendi}x titleappendi[x } [");
                            restTransformer.setParameter("alternativeAppendix", "alternativeappendix} alternativeappendix[");
                            soapTransformer.setParameter("alternativeAppendix", "alternativeappendix} alternativeappendix[");
                            restTransformer.setParameter("specialLetter", specialSignSmallOe);
                            soapTransformer.setParameter("specialLetter", specialSignSmallOe);
                        }
                        else if (j == 5) {
                            restTransformer.setParameter("titleAppendix", "titleappendi]x titleappendi~x ] ~");
                            soapTransformer.setParameter("titleAppendix", "titleappendi]x titleappendi~x ] ~");
                            restTransformer.setParameter("alternativeAppendix", "alternativeappendix] alternativeappendix~");
                            soapTransformer.setParameter("alternativeAppendix", "alternativeappendix] alternativeappendix~");
                            restTransformer.setParameter("specialLetter", specialSignSmallUe);
                            soapTransformer.setParameter("specialLetter", specialSignSmallUe);
                        }
                        else if (j == 6) {
                            restTransformer.setParameter("titleAppendix", "titleappendi:x titleappendi*x : *");
                            soapTransformer.setParameter("titleAppendix", "titleappendi:x titleappendi*x : *");
                            restTransformer.setParameter("alternativeAppendix", "alternativeappendix: alternativeappendix*");
                            soapTransformer.setParameter("alternativeAppendix", "alternativeappendix: alternativeappendix*");
                            restTransformer.setParameter("specialLetter", specialSignSmallA);
                            soapTransformer.setParameter("specialLetter", specialSignSmallA);
                        }
                        else if (j == 7) {
                            restTransformer.setParameter("titleAppendix", "titleappendi?x titleappendi^x ? ^");
                            soapTransformer.setParameter("titleAppendix", "titleappendi?x titleappendi^x ? ^");
                            restTransformer.setParameter("alternativeAppendix", "alternativeappendix? alternativeappendix^");
                            soapTransformer.setParameter("alternativeAppendix", "alternativeappendix? alternativeappendix^");
                        }
                        else if (j == 8) {
                            restTransformer.setParameter("titleAppendix", "titleappendi\\x titleappendi\"x \\ \"");
                            soapTransformer.setParameter("titleAppendix", "titleappendi\\x titleappendi\"x \\ \"");
                            restTransformer.setParameter("alternativeAppendix", "alternativeappendix\\ alternativeappendix\"");
                            soapTransformer.setParameter("alternativeAppendix", "alternativeappendix\\ alternativeappendix\"");
                        }
                        else if (j == 9) {
                            restTransformer.setParameter("titleAppendix", "titleappendi\'x titleappendi/x \' /");
                            soapTransformer.setParameter("titleAppendix", "titleappendi\'x titleappendi/x \' /");
                            restTransformer.setParameter("alternativeAppendix", "alternativeappendix\' alternativeappendix/");
                            soapTransformer.setParameter("alternativeAppendix", "alternativeappendix\' alternativeappendix/");
                        }
                        restTransformer.setParameter("createdate", date);
                        soapTransformer.setParameter("createdate", date);
                        if (j % 6 > 0) {
                            restTransformer.setParameter("modifydate", date);
                            soapTransformer.setParameter("modifydate", date);
                        } else {
                            restTransformer.setParameter("modifydate", null);
                            soapTransformer.setParameter("modifydate", null);
                        }
                        if (j % 6 > 1) {
                            restTransformer.setParameter("submitdate", date);
                            soapTransformer.setParameter("submitdate", date);
                        } else {
                            restTransformer.setParameter("submitdate", null);
                            soapTransformer.setParameter("submitdate", null);
                        }
                        if (j % 6 > 2) {
                            restTransformer.setParameter("acceptdate", date);
                            soapTransformer.setParameter("acceptdate", date);
                        } else {
                            restTransformer.setParameter("acceptdate", null);
                            soapTransformer.setParameter("acceptdate", null);
                        }
                        if (j % 6 > 3) {
                            restTransformer.setParameter("publishdate", date);
                            soapTransformer.setParameter("publishdate", date);
                        } else {
                            restTransformer.setParameter("publishdate", null);
                            soapTransformer.setParameter("publishdate", null);
                        }
                        if (j % 6 > 4) {
                            restTransformer.setParameter("issueddate", date);
                            soapTransformer.setParameter("issueddate", date);
                        } else {
                            restTransformer.setParameter("issueddate", null);
                            soapTransformer.setParameter("issueddate", null);
                        }
                        restTransformer.setParameter("chineseString", chineseStringMetadata);
                        soapTransformer.setParameter("chineseString", chineseStringMetadata);
                        restTransformer.setParameter("user", PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE);
                        soapTransformer.setParameter("user", PWCallback.ID_PREFIX + PWCallback.DEPOSITOR_HANDLE);
                        restTransformer.setParameter("status", stati[statusCounter]);
                        soapTransformer.setParameter("status", stati[statusCounter]);
                        restTransformer.setParameter("docnum", j);
                        soapTransformer.setParameter("docnum", j);
                        transform(currentLine, dirname, restFilename,
                            restTransformer);
                        transform(currentLine, dirname, soapFilename,
                            soapTransformer);
                        saveFulltext(currentLine);
                        if (j % 3 == 0) {
                            day--;
                            if (day == 0) {
                                day = 28;
                                month++;
                            }
                            if (month == 13) {
                                month = 1;
                                year++;
                            }
                        }
                        user++;
                        statusCounter++;
                        if (user > 10) {
                            user = 1;
                        }
                        if ((statusCounter) % 3 == 0) {
                            statusCounter = 0;
                        }
                        j++;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("finished " + files[i].getName()
                            + " at document number " + j);
                    }
                    generatePatentPdfs(pdfDirectory);
                    File file =
                        new File(pdfDirectory
                            + "\\fileForPdfGeneration.xml");
                    file.delete();
                }
                catch (Exception e) {
                    log.error(e);
                }
                finally {
                    try {
                        ostr.close();
                    }
                    catch (IOException e) {
                    }
                    try {
                        br.close();
                    }
                    catch (IOException e) {
                    }
                }
            }
        }
    }

    /**
     * extracts fulltext out of text and saves it at webserver.
     * 
     * @param text
     *            String xml with fulltext
     * @param filename
     *            name of file to save
     */
    private void saveFulltext(final String text) {
        String encoding = null;
        if (text.matches(".*?\\?>")) {
            encoding = text.replaceAll("(.*?\\?>).*", "$1");
        }
        else {
            encoding = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        }
        String fulltext = text.replaceAll(".*?(<SDODE.*?</SDODE>).*", "$1");
        String accessionnumber =
            text.replaceAll(".*?PATDOC.*?DNUM=\"(.*?)\".*", "$1");
        try {
            BufferedOutputStream ostr =
                new BufferedOutputStream(new FileOutputStream(xmlDirectory
                    + File.separatorChar
                    + "EPFull_" + accessionnumber + ".xml", false));
            ostr.write(encoding.getBytes(DEFAULT_CHARSET));
            ostr.write(fulltext.getBytes(DEFAULT_CHARSET));
            ostr.flush();
            ostr.close();

        }
        catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * Transforms thieme-xml to escidoc-xml.
     * 
     * @param xml
     *            String thieme-xml
     * @param dirname
     *            String directory to save tramsformed xml
     * @param filename
     *            String file to save tramsformed xml
     * @param stylesheet
     *            String path to stylesheet
     */
    private void transform(
        final String xml, final String dirname, final String filename,
        final Transformer transformer) {
        try {
            transformer.transform(new StreamSource(new StringReader(xml)),
                new StreamResult(new FileOutputStream(dirname + filename)));

        }
        catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * open connection to specified database.
     * 
     * @param connectString
     *            String connectString
     * @param conn
     *            Connection conn
     * @param driverClass
     *            String driverClass
     * @return Connection connection
     * @throws Exception
     *             e
     * 
     */
    public Connection openConnection(
        final String connectString, Connection conn, final String driverClass)
        throws Exception {
        if ((conn == null) || (conn.isClosed())) {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(connectString);
        }
        return conn;
    }

}
