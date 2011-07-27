/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.persistence.impl;

import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.PidSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.PIDSystem;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.service.ConnectionUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Communication with PIDManager WebService via RESTlet API.
 *
 * @author Steffen Wagner
 */
@Configurable
public class PIDManagerRESTService implements PIDSystem {

    private String pidGeneratorServer;

    private String separator = "/";

    private String pidNamespace = "hdl";

    private String globalPrefix;

    private String localPrefix = "";

    @Autowired
    @Qualifier("escidoc.core.common.util.service.ConnectionUtility")
    private ConnectionUtility connectionUtility;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.common.persistence.PIDSystem#assignPID(java.lang.String,
     * java.lang.String)
     */
    @Override
    public String assignPID(final String systemID, final String param) throws PidSystemException,
        MissingMethodParameterException, WebserverSystemException {

        if (param == null) {
            throw new MissingMethodParameterException("Invalid param structure.");
        }
        final String username = EscidocConfiguration.getInstance().get("escidoc-core.PidSystemRESTService.user");
        final String password = EscidocConfiguration.getInstance().get("escidoc-core.PidSystemRESTService.password");
        final String pidResult;
        try {
            final String xmlParam = preparePidManagerDatastructure(systemID, param);
            final URL url = new URL(this.pidGeneratorServer + this.globalPrefix + '/');
            final HttpResponse httpPostRes = this.connectionUtility.postRequestURL(url, xmlParam, username, password);
            final int status = httpPostRes.getStatusLine().getStatusCode();
            if (status == HttpURLConnection.HTTP_OK) {
                pidResult = obtainPidResult(httpPostRes.getEntity().getContent());
            }
            else if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw new Exception("Authorization at PIDManager fails.");
            }
            else {
                throw new PidSystemException(EntityUtils.toString(httpPostRes.getEntity(),
                    XmlUtility.CHARACTER_ENCODING));
            }
        }
        catch (final Exception e) {
            throw new PidSystemException(e);
        }

        return pidResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.common.persistence.PIDSystem
     * #generatePID(java.lang.String)
     */
    @Override
    public String generatePID(final String systemID) throws PidSystemException {
        String result = this.pidNamespace + ':' + this.globalPrefix + this.separator;
        if (this.localPrefix != null && localPrefix.length() > 0) {
            result += this.localPrefix + this.separator;
        }
        result += systemID;

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.common.persistence.PIDSystem
     * #neverGeneratePID(java.lang.String)
     */
    @Override
    public void neverGeneratePID(final String pid) {
        throw new UnsupportedOperationException("Method neverGeneratePID() not supported by used PID System.");
    }

    /**
     * Delete a Persistent Identifier from the PID System. Attention: A Persistent Identifier exist even if the resource
     * is permanently in-accessible. Therefore is a PID deletion usually not necessary. Use this with caution.
     *
     * @param pid The Persistent Identifier which is to delete.
     * @throws PidSystemException Thrown if delete from the PID System fails.
     */
    public void deletePID(final String pid) throws PidSystemException {

        final String pidServer = this.pidGeneratorServer + this.globalPrefix + '/';

        // fetch suffix from pid and prepare URL
        final String suffix = pid.replace("hdl:" + this.globalPrefix + '/', "");
        if (suffix.length() < 1) {
            throw new PidSystemException("Wrong handle identifier.");
        }

        try {
            final URL url = new URL(pidServer + suffix);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("PID System connetion broken (" + url.toString() + ") " + conn.getResponseCode());
            }
        }
        catch (final Exception e) {
            throw new PidSystemException(e);
        }
    }

    /**
     * Set the system for PID management service.
     *
     * @param url URL to PID Manager Service (http://host[:port]/)
     */
    public void setPidGeneratorServer(final String url) {
        this.pidGeneratorServer = url;
    }

    /**
     * Set the globalPrefix for generated PIDs.
     *
     * @param globalPrefix The globalPrefix for generated PIDs
     * @throws MissingMethodParameterException
     *          If {@code globalPrefix} is null.
     */
    public void setGlobalPrefix(final String globalPrefix) throws MissingMethodParameterException {
        Utility.checkNotNull(globalPrefix, "global prefix for PID");
        this.globalPrefix = globalPrefix;
    }

    /**
     * Set the localPrefix for generated PIDs. This a part of the PID between the global prefix and the system id.
     * Default is "test" to indicate that the generated (Dummy-)PIDs are not registered.
     *
     * @param localPrefix The localPrefix for generated PIDs
     */
    public void setLocalPrefix(final String localPrefix) {
        this.localPrefix = localPrefix;
    }

    /**
     * Set the separator between the parts of generated PIDs. Default is "/".
     *
     * @param separator The separator for generated PIDs
     * @throws MissingMethodParameterException
     *          If {@code separator} is null.
     */
    public void setSeparator(final String separator) throws MissingMethodParameterException {
        Utility.checkNotNull(separator, "separator");
        this.separator = separator;
    }

    /**
     * Set the namespace of PID.
     *
     * @param pidNamespace The namespace for generated PIDs
     * @throws MissingMethodParameterException
     *          If {@code pidNamespace} is null.
     */
    public void setPidNamespace(final String pidNamespace) throws MissingMethodParameterException {
        Utility.checkNotNull(pidNamespace, "namespace for PID");
        this.pidNamespace = pidNamespace;
    }

    /**
     * Add systemID to data structure for PIDManager.
     *
     * @param systemID Objid of resource.
     * @param param    XML parameter from assignPID user interface.
     * @return XML data structure for PID Manager enriched with objid.
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     * @throws javax.xml.transform.TransformerException
     */
    private static String preparePidManagerDatastructure(final String systemID, final String param)
        throws ParserConfigurationException, SAXException, IOException, TransformerException {

        final String xmlParam;

        // add the systemID of object for semantic identifier
        final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document doc = db.parse(new ByteArrayInputStream(param.getBytes()));
        final NodeList systemIDs = doc.getElementsByTagName("systemID");

        if (systemIDs.getLength() == 1) {
            xmlParam = param;
        }
        else {
            final Node first = doc.getFirstChild();
            final Node sysid = doc.createElement("systemID");
            sysid.setTextContent(systemID);
            first.appendChild(sysid);

            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            final StreamResult result = new StreamResult(new StringWriter());
            final Source source = new DOMSource(doc);
            transformer.transform(source, result);

            xmlParam = result.getWriter().toString();
        }
        return xmlParam;
    }

    /**
     * Obtain PID from respose message of PIDManager.
     *
     * @param in InputStream from PIDManager.
     * @return PID
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    private static String obtainPidResult(final InputStream in) throws ParserConfigurationException, SAXException,
        IOException {
        String returnValue;
        try {
            final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = db.parse(in);
            // retrieve PID result
            final NodeList nl = doc.getElementsByTagName("pid");
            final Node n = nl.item(0);
            returnValue = n.getTextContent();
        }
        finally {
            IOUtils.closeStream(in);
        }
        return returnValue;
    }
}
