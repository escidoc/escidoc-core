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
package de.escidoc.core.test.load;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.resources.ResourceProvider;
import de.escidoc.core.test.load.statistics.Collector;
import de.escidoc.core.test.om.item.ItemTestBase;

/**
 * Test the mock implementation of the context resource.
 * 
 * @author MSC
 * 
 */
public class LoadTestBase extends ItemTestBase {

    // public static String STATS_SAVE_PATH = "build/log";

    // public static String LOG_SAVE_PATH = "build/log";
    private String statsSavePath = null;

    private String logSavePath = null;

    private String label = "";

    public static final String CRLF = "\n";

    /**
     * @param transport
     *            The transport identifier.
     */
    public LoadTestBase(final int transport) {
        super(transport);
    }

    protected void writeStatisticsFile() throws IOException {

        ResourceProvider.saveToFile(getTransport(), getStatsSavePath(), "/"
            + getStatisticsFileName(), Collector.getMessage());
    }

    protected void log(final String message) {
        log.debug(message);
        logToFile(message + CRLF);
    }

    protected void initLogFile(final boolean clear) {
        File path = new File(getLogSavePath());
        path.mkdirs();
        if (clear) {
            File log = new File(getLogSavePath() + "/" + getLogFileName());
            log.delete();
        }
    }

    protected void logException(final Exception e) {
        String message = CRLF + e.getClass() + " " + e.getMessage() + CRLF;
        for (int i = 0; i < (e.getStackTrace()).length; ++i) {
            message += (e.getStackTrace())[i].toString() + CRLF;
        }
        message += CRLF;
        log(message);
    }

    protected void logToFile(final String message) {
        try {
            BufferedWriter out =
                new BufferedWriter(new FileWriter(getLogSavePath() + "/"
                    + getLogFileName(), true));
            out.write(message);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            log.error("Writing to log file failed!");
            e.printStackTrace();
        }
    }

    public String getFileLabel() {
        String result = "";
        if (getTransport() == Constants.TRANSPORT_REST) {
            result += "rest";
        }
        else if (!getLabel().contains("soap")) {
            result += "soap";
        }
        result = getLabel() + result;
        return result;
    }

    protected String getStatisticsFileName() {
        return getFileLabel() + "-stats.xml";
    }

    protected String getLogFileName() {
        return getFileLabel() + "-log.txt";
    }

    /**
     * @return the label
     */
    public String getLabel() {
        if (!label.equals("") || !label.endsWith("-")) {
            return label + "-";
        }
        return label;
    }

    /**
     * @param label
     *            the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the logSavePath
     */
    public String getLogSavePath() {
        if (logSavePath == null) {
            initPaths();
        }
        return logSavePath;
    }

    /**
     * @return the statsSavePath
     */
    public String getStatsSavePath() {

        if (statsSavePath == null) {
            initPaths();
        }
        return statsSavePath;
    }

    private void initPaths() {
        Properties properties = new Properties();
        logSavePath = "build/log";
        statsSavePath = "build/log";
        // String currentUser = System.getProperties().getProperty("user.name");
        try {
            InputStream fis = null;
            String propertiesFilename = "load-test.properties";
            try {
                fis =
                    ResourceProvider.getFileInputStreamFromFile("etc",
                        propertiesFilename);
            }
            catch (IOException e) {
                if (fis == null) {
                    fis =
                        ResourceProvider.getFileInputStreamFromFile(
                            "../../etc", propertiesFilename);
                }
            }
            if (fis != null) {
                properties.load(fis);
                if (properties.get("log.save.path") != null) {
                    logSavePath = (String) properties.get("log.save.path");
                }

                if (properties.get("stats.save.path") != null) {
                    statsSavePath = (String) properties.get("stats.save.path");
                }
                fis.close();
            }
        }
        catch (IOException e) {
            log.debug("InitPath " + e);
        }
    }

}
