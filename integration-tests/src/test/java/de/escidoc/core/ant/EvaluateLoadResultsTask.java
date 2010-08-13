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
package de.escidoc.core.ant;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.resources.ResourceProvider;
import de.escidoc.core.test.load.ItemThread;

/**
 * Read and evaluate load statistics files and convret them to a specified
 * output. By now only csv is suported as output format.
 * 
 * @author schneider
 * 
 */
public class EvaluateLoadResultsTask extends Task {

    public final static String OUTPUT_CSV = "csv";

    public final static String OUTPUT_CSV_EMPTY_ROW = "\n";

    public final static String OUTPUT_CSV_HEADER_1_ROW =
        ";;;LifeCycle;;;retrieve;;;create;;;submit;;;release;;\n";

    public final static String OUTPUT_CSV_HEADER_2_ROW =
        "Label;Invocations;Time;avg;min;max;avg;min;max;avg;min;max;avg;min;max;avg;min;max\n";

    public final static String OUTPUT_CSV_CONTENT_ROW =
        "label;invocations;runtime;l-avg;l-min;l-max;r-avg;r-min;r-max;c-avg;c-min;c-max;s-avg;s-min;s-max;rel-avg;rel-min;rel-max\n";

    private String inputPath;

    private String inputFileExtension = "xml";

    private String outputPath;

    private String outputFilename;

    private String outputFormat = OUTPUT_CSV;

    private String output;

    public void execute() {
        System.out.println("Ant Task execute:");
        System.out.println(toString());
        output = "";
        List<File> logs = getLogs();
        Iterator<File> logIter = logs.iterator();
        while (logIter.hasNext()) {
            File log = logIter.next();
            System.out.println(log.getPath());
            addToOutput(log);
        }
        System.out.println("\n\nresult:\n");
        System.out.println(output);
        try {
            ResourceProvider.saveToFile(getOutputPath(),
                getOutputFilename() + "." + getOutputFormat(), output);
        }
        catch (IOException e) {
            System.out.println("Error saving output to file '" + getOutputFilename()
                + "." + getOutputFormat() + "'");
            e.printStackTrace();
        }
    }

    private static final String REST_LIFECYCLE_LABEL =
        "rest " + ItemThread.ITEM_LIFE_CYCLE_TEXT;

    private static final String SOAP_LIFECYCLE_LABEL =
        "soap " + ItemThread.ITEM_LIFE_CYCLE_TEXT;

    private static final String REST_RETRIEVE_LABEL =
        "rest " + ItemThread.ITEM_RETRIEVE_TEXT;

    private static final String SOAP_RETRIEVE_LABEL =
        "soap " + ItemThread.ITEM_RETRIEVE_TEXT;

    private static final String REST_CREATE_LABEL =
        "rest " + ItemThread.ITEM_CREATE_TEXT;

    private static final String SOAP_CREATE_LABEL =
        "soap " + ItemThread.ITEM_CREATE_TEXT;

    private static final String REST_SUBMIT_LABEL =
        "rest " + ItemThread.ITEM_SUBMIT_TEXT;

    private static final String SOAP_SUBMIT_LABEL =
        "soap " + ItemThread.ITEM_SUBMIT_TEXT;

    private static final String REST_RELEASE_LABEL =
        "rest " + ItemThread.ITEM_RELEASE_TEXT;

    private static final String SOAP_RELEASE_LABEL =
        "soap " + ItemThread.ITEM_RELEASE_TEXT;

    private void addToOutput(final File input) {

        String content = OUTPUT_CSV_CONTENT_ROW;
        try {
            Document log =
                EscidocRestSoapTestsBase.getDocument(ResourceProvider
                    .getFileInputStreamFromFile(input.getParent(), input
                        .getName()));
            Node root =
                EscidocRestSoapTestsBase.selectSingleNode(log, "/statistics");

            content = content.replace("runtime", getAttrbute("runtime", root));

            String xpath_prefix = "/statistics/record[contains(@label,'";//
            String xpath_postfix = "')]";

            String xpath = xpath_prefix + SOAP_LIFECYCLE_LABEL + xpath_postfix;
            Node node = EscidocRestSoapTestsBase.selectSingleNode(log, xpath);
            if (node != null) {
                // "label;invocations;runtime;l-avg;l-min;l-max;r-avg;r-min;r-max;c-avg;c-min;c-max;s-avg;s-min;s-max;rel-avg;rel-min;rel-max"
                String label = getAttrbute("label", node);
                label =
                    label.substring(0, label
                        .indexOf(ItemThread.ITEM_LIFE_CYCLE_TEXT));
                content = content.replace("label", label.trim());
                content =
                    content.replace("invocations", getAttrbute("invocations",
                        node));

                content =
                    content.replaceFirst("l-avg", getAttrbute("avgTime", node));
                content =
                    content.replaceFirst("l-min", getAttrbute("minTime", node));
                content =
                    content.replaceFirst("l-max", getAttrbute("maxTime", node));

                xpath = xpath_prefix + SOAP_RETRIEVE_LABEL + xpath_postfix;
                node = EscidocRestSoapTestsBase.selectSingleNode(log, xpath);
                content =
                    content.replaceFirst("r-avg", getAttrbute("avgTime", node));
                content =
                    content.replaceFirst("r-min", getAttrbute("minTime", node));
                content =
                    content.replaceFirst("r-max", getAttrbute("maxTime", node));

                xpath = xpath_prefix + SOAP_CREATE_LABEL + xpath_postfix;
                node = EscidocRestSoapTestsBase.selectSingleNode(log, xpath);
                content =
                    content.replaceFirst("c-avg", getAttrbute("avgTime", node));
                content =
                    content.replaceFirst("c-min", getAttrbute("minTime", node));
                content =
                    content.replaceFirst("c-max", getAttrbute("maxTime", node));

                xpath = xpath_prefix + SOAP_SUBMIT_LABEL + xpath_postfix;
                node = EscidocRestSoapTestsBase.selectSingleNode(log, xpath);
                content =
                    content.replaceFirst("s-avg", getAttrbute("avgTime", node));
                content =
                    content.replaceFirst("s-min", getAttrbute("minTime", node));
                content =
                    content.replaceFirst("s-max", getAttrbute("maxTime", node));

                xpath = xpath_prefix + SOAP_RELEASE_LABEL + xpath_postfix;
                node = EscidocRestSoapTestsBase.selectSingleNode(log, xpath);
                content =
                    content.replaceFirst("rel-avg",
                        getAttrbute("avgTime", node));
                content =
                    content.replaceFirst("rel-min",
                        getAttrbute("minTime", node));
                content =
                    content.replaceFirst("rel-max",
                        getAttrbute("maxTime", node));
            }
            else {
                xpath = xpath_prefix + REST_LIFECYCLE_LABEL + xpath_postfix;
                node = EscidocRestSoapTestsBase.selectSingleNode(log, xpath);

                String label = getAttrbute("label", node);
                label =
                    label.substring(0, label
                        .indexOf(ItemThread.ITEM_LIFE_CYCLE_TEXT));
                content = content.replace("label", label.trim());
                content =
                    content.replace("invocations", getAttrbute("invocations",
                        node));

                content =
                    content.replaceFirst("l-avg", getAttrbute("avgTime", node));
                content =
                    content.replaceFirst("l-min", getAttrbute("minTime", node));
                content =
                    content.replaceFirst("l-max", getAttrbute("maxTime", node));

                xpath = xpath_prefix + REST_RETRIEVE_LABEL + xpath_postfix;
                node = EscidocRestSoapTestsBase.selectSingleNode(log, xpath);
                content =
                    content.replaceFirst("r-avg", getAttrbute("avgTime", node));
                content =
                    content.replaceFirst("r-min", getAttrbute("minTime", node));
                content =
                    content.replaceFirst("r-max", getAttrbute("maxTime", node));

                xpath = xpath_prefix + REST_CREATE_LABEL + xpath_postfix;
                node = EscidocRestSoapTestsBase.selectSingleNode(log, xpath);
                content =
                    content.replaceFirst("c-avg", getAttrbute("avgTime", node));
                content =
                    content.replaceFirst("c-min", getAttrbute("minTime", node));
                content =
                    content.replaceFirst("c-max", getAttrbute("maxTime", node));

                xpath = xpath_prefix + REST_SUBMIT_LABEL + xpath_postfix;
                node = EscidocRestSoapTestsBase.selectSingleNode(log, xpath);
                content =
                    content.replaceFirst("s-avg", getAttrbute("avgTime", node));
                content =
                    content.replaceFirst("s-min", getAttrbute("minTime", node));
                content =
                    content.replaceFirst("s-max", getAttrbute("maxTime", node));

                xpath = xpath_prefix + REST_RELEASE_LABEL + xpath_postfix;
                node = EscidocRestSoapTestsBase.selectSingleNode(log, xpath);
                content =
                    content.replaceFirst("rel-avg",
                        getAttrbute("avgTime", node));
                content =
                    content.replaceFirst("rel-min",
                        getAttrbute("minTime", node));
                content =
                    content.replaceFirst("rel-max",
                        getAttrbute("maxTime", node));

            }
            System.out.println(content);
            output += OUTPUT_CSV_EMPTY_ROW;
            output += OUTPUT_CSV_HEADER_1_ROW;
            output += OUTPUT_CSV_HEADER_2_ROW;
            output += content;
            output += OUTPUT_CSV_EMPTY_ROW;
        }
        catch (Exception e) {
            System.out.println("Error reading from file '" + input.getPath()
                + "'");
            e.printStackTrace();
        }

    }

    private String getAttrbute(final String attribute, final Node node) {
        String result = null;
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node attNode = attributes.getNamedItem(attribute);
            if (attNode != null) {
                result = attNode.getNodeValue();
            }
        }
        return result;
    }

    private List<File> getLogs() {
        List<File> result = new Vector<File>();
        File input = new File(getInputPath());
        File[] children = input.listFiles();
        for (int i = 0; i < children.length; ++i) {
            System.out.println("Child: " + children[i].getName());
            if (children[i].getName().endsWith(getInputFileExtension())) {
                result.add(children[i]);
            }
        }
        return result;
    }

    public String toString() {
        String result = "";
        result += "Supported output formats: [" + OUTPUT_CSV + "]\n";
        result += "Path to input:      " + inputPath + "\n";
        result += "Input File Ext:     " + inputFileExtension + "\n";
        result += "Path to output:     " + outputPath + "\n";
        result += "Output filename:    " + outputFilename + "\n";
        result += "Output format:      " + outputFormat + "\n";
        result += "Reading input from: " + readFrom() + "\n";
        result += "Save results to:    " + saveTo() + "\n";

        return result;
    }

    private String saveTo() {
        return ResourceProvider.concatenatePath(getOutputPath(),
            getOutputFilename() + "." + getOutputFormat());
    }

    private String readFrom() {
        return ResourceProvider.concatenatePath(getInputPath(), "*."
            + getInputFileExtension());
    }

    /**
     * @return the inputPath
     */
    public String getInputPath() {
        return inputPath;
    }

    /**
     * @param inputPath
     *            the inputPath to set
     */
    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    /**
     * @return the outputFilename
     */
    public String getOutputFilename() {
        return outputFilename;
    }

    /**
     * @param outputFilename
     *            the outputFilename to set
     */
    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    /**
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat
     *            the outputFormat to set
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @return the outputPath
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * @param outputPath
     *            the outputPath to set
     */
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * @return the inputFileExtension
     */
    public String getInputFileExtension() {
        return inputFileExtension;
    }

    /**
     * @param inputFileExtension
     *            the inputFileExtension to set
     */
    public void setInputFileExtension(String inputFileExtension) {
        this.inputFileExtension = inputFileExtension;
    }

}
