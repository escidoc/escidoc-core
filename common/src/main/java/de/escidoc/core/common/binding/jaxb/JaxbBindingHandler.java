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
package de.escidoc.core.common.binding.jaxb;

import de.escidoc.core.common.binding.BindingHandlerInterface;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MIH
 * @spring.bean id = "common.binding.JaxbBindingHandler"
 * @common
 */
public class JaxbBindingHandler implements BindingHandlerInterface {

    private final Map<String, JAXBContext> storedContexts =
        new HashMap<String, JAXBContext>();

    /**
     * Unmarshals given xml-data to java-binding-object. Needs contextPath
     * (package-name of binding-object).
     * 
     * @param xml
     *            xml.
     * @param contextPath
     *            contextPath.
     * @return Returns Java binding Object.
     * 
     * @throws XmlParserSystemException
     *             ex
     * 
     * @common
     */
    public Object unmarshal(final String xml, final String contextPath)
        throws XmlParserSystemException {
        try {
            // Store Context in HashMap for faster later use
            JAXBContext jc;
            if (storedContexts.get(contextPath) != null) {
                jc = storedContexts.get(contextPath);
            }
            else {
                jc = JAXBContext.newInstance(contextPath);
                storedContexts.put(contextPath, jc);
            }
            Unmarshaller u = jc.createUnmarshaller();
            Object bindingObject =
                u.unmarshal(new ByteArrayInputStream(xml
                    .getBytes(XmlUtility.CHARACTER_ENCODING)));
            return bindingObject;
        }
        catch (Exception e) {
            throw new XmlParserSystemException(e);
        }
    }

    /**
     * Marshals given Object to xml-String.
     * 
     * @param bindingObject
     *            bindingObject.
     * @return String xml.
     * 
     * @throws XmlParserSystemException
     *             ex
     * 
     * @common
     */
    public String marshal(final Object bindingObject)
        throws XmlParserSystemException {
        try {
            // Initialize JAXB-Context with package-Name of binding-Object
            // Store Context in HashMap for faster later use
            String packageName =
                bindingObject.getClass().getPackage().getName();
            JAXBContext jc;
            if (storedContexts.get(packageName) != null) {
                jc = storedContexts.get(packageName);
            }
            else {
                jc = JAXBContext.newInstance(packageName);
                storedContexts.put(packageName, jc);
            }

            Marshaller m = jc.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Marshall
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            m.marshal(bindingObject, out);

            // MIH: workaround: reduce empty elements
            String xml = new String(
                out.toByteArray(), XmlUtility.CHARACTER_ENCODING);
            xml = xml.replaceAll("(?s)(<[^\\/]*?)>\\s*?<\\/.*?>", "$1/>");
            return xml;
        }
        catch (Exception e) {
            throw new XmlParserSystemException(e);
        }
    }
}
