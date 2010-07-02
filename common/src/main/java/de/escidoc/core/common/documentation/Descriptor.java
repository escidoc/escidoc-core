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
package de.escidoc.core.common.documentation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.servlet.invocation.XMLBase;
import de.escidoc.core.common.util.logger.AppLogger;

/**
 * Ant task to transform a given escidoc REST descriptor to a docbook
 * documentation.
 * 
 * @author MSC
 * 
 */
public class Descriptor extends Task {

    /** The logger. */
    private static AppLogger logger = new AppLogger(Descriptor.class.getName());

    private String descriptor = null;

    private String outputPath = null;

    private boolean includeErrors = false;

    private boolean checkVisibility = false;

    private XMLBase helper = new XMLBase();

    /**
     * .
     * 
     */
    public Descriptor() {
    }

    /**
     * See Interface for functional description.
     * 
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() {

        try {
            Set<String> documentedResourceNames = new HashSet<String>();
            getLogger().error("[Deskriptor " + descriptor + "] Loading ...");
            System.out.println("[Deskriptor " + descriptor + "] Loading ...");
            Document xml = helper.getDocument(descriptor);

            String resourceXPath =
                helper.appendToXpath(XMLBase.XPATH_DELIMITER
                    + XMLBase.ROOT_ELEMENT, XMLBase.RESOURCE_ELEMENT);
            NodeList resources = helper.parse(resourceXPath, xml);
            String resourceName = null;
            for (int i = 0; i < resources.getLength(); ++i) {

                try {
                    resourceName =
                        helper.getAttributeValue(resources.item(i),
                            XMLBase.RESOURCE_NAME_ATTR);
                    if (!documentedResourceNames.contains(resourceName)) {
                        documentedResourceNames.add(resourceName);
                        Vector<Node> data = new Vector<Node>();
                        data.add(resources.item(i));
                        for (int j = i + 1; j < resources.getLength(); ++j) {
                            if (resourceName.equalsIgnoreCase(helper
                                .getAttributeValue(resources.item(j),
                                    XMLBase.RESOURCE_NAME_ATTR))) {
                                data.add(resources.item(j));
                            }
                        }
                        Resource docbook = new Resource(data, resourceName);
                        docbook.setIncludeErrors(this.includeErrors);
                        docbook.setCheckVisibility(this.checkVisibility);
                        docbook.toDocbook();

                        helper.saveToFile(getOutputPath() + "Rest"
                            + resourceName + "Api.xml", docbook
                            .getRestDocumentation());
                        helper.saveToFile(getOutputPath() + "Soap"
                            + resourceName + "Api.xml", docbook
                            .getSoapDocumentation());
                    }
                }
                catch (IOException e) {
                    getLogger().debug(
                        "Could not save file '" + getOutputPath()
                            + resourceName + ".xml'");
                    e.printStackTrace();
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @see org.apache.tools.ant.Task#init()
     */
    public void init() {

    }

    /**
     * @return Returns the descriptor.
     */
    public String getDescriptor() {
        return descriptor;
    }

    /**
     * @param descriptor
     *            The descriptor to set.
     */
    public void setDescriptor(final String descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * @return Returns the outputPath.
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * @param outputPath
     *            The outputPath to set.
     */
    public void setOutputPath(final String outputPath) {
        if (!outputPath.endsWith("/")) {
            this.outputPath = outputPath + "/";
        }
        else {
            this.outputPath = outputPath;
        }
    }

    /**
     * @return Returns the logger.
     * @common
     */
    public static AppLogger getLogger() {
        return logger;
    }

    /**
     * @return Returns the includeErrors.
     */
    public boolean isIncludeErrors() {
        return includeErrors;
    }

    /**
     * @param includeErrors
     *            The includeErrors to set.
     */
    public void setIncludeErrors(final boolean includeErrors) {
        this.includeErrors = includeErrors;
    }

    /**
     * @param checkVisibility
     *            The checkVisibility to set.
     */
    public void setCheckVisibility(final boolean checkVisibility) {
        this.checkVisibility = checkVisibility;
    }
}
