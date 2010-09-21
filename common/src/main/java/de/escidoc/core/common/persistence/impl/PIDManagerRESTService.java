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
package de.escidoc.core.common.persistence.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.PidSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.PIDSystem;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.ConnectionUtility;

/**
 * Communication with PIDManager WebService via RESTlet API.
 * 
 * @see PIDManager Web Service
 * 
 * @author SWA
 * 
 */
public class PIDManagerRESTService implements PIDSystem {

    private static AppLogger log =
        new AppLogger(PIDManagerRESTService.class.getName());

    private String pidGeneratorServer = null;

    private String separator = "/";

    private String pidNamespace = "hdl";

    private String globalPrefix = null;

    private String localPrefix = "";

    private ConnectionUtility connectionUtility = null;

    /**
     * PIDManagerRESTService.
     */
    public PIDManagerRESTService() {

        this.connectionUtility = new ConnectionUtility();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.common.persistence.PIDSystem#assignPID(java.lang.String,
     * java.lang.String)
     */
    public String assignPID(final String systemID, final String param)
        throws PidSystemException, MissingMethodParameterException,
        WebserverSystemException {

        String xmlParam = null;
        String pidResult = null;
        URL url = null;
        String username;
        String password;

        if (param == null) {
            throw new MissingMethodParameterException(
                "Invalid param structure.");
        }

        try {
            username =
                EscidocConfiguration.getInstance().get(
                    "escidoc-core.PidSystemRESTService.user");
            password =
                EscidocConfiguration.getInstance().get(
                    "escidoc-core.PidSystemRESTService.password");
        }
        catch (IOException e) {
            throw new WebserverSystemException(e);
        }

        HttpResponse httpPostRes = null;
        try {
            xmlParam = preparePidManagerDatastructure(systemID, param);

            url = new URL(this.pidGeneratorServer + this.globalPrefix + "/");
            httpPostRes =this.connectionUtility.postRequestURL(url, xmlParam, username,
                    password);

            int status = httpPostRes.getStatusLine().getStatusCode();
            
            if (status == HttpURLConnection.HTTP_OK) {

                pidResult = obtainPidResult(httpPostRes.getEntity().getContent());
            }
            else if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
                log.warn("Authorization failure at PIDManager. ");
                throw new Exception("Authorization at PIDManager fails.");
            }
            else {
                String msg = EntityUtils.toString(httpPostRes.getEntity());
                log.warn(msg);
                throw new PidSystemException(msg);
            }

        }
        catch (Exception e) {
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
    public String generatePID(final String systemID) throws PidSystemException {
        String result = pidNamespace + ":" + globalPrefix + separator;
        if (localPrefix != null && localPrefix.length() > 0) {
            result += localPrefix + separator;
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
    public void neverGeneratePID(final String pid) {
        throw new UnsupportedOperationException(
            "Method neverGeneratePID() not supported by used PID System.");
    }

    /**
     * Delete a Persistent Identifier from the PID System. Attention: A
     * Persistent Identifier exist even if the resource is permanently
     * in-accessible. Therefore is a PID deletion usually not necessary. Use
     * this with caution.
     * 
     * @param pid
     *            The Persistent Identifier which is to delete.
     * @throws PidSystemException
     *             Thrown if delete from the PID System fails.
     */
    public void deletePID(final String pid) throws PidSystemException {
        URL url = null;
        HttpURLConnection conn = null;

        String pidServer = this.pidGeneratorServer + this.globalPrefix + "/";

        // fetch suffix from pid and prepare URL
        String suffix = pid.replace("hdl:" + this.globalPrefix + "/", "");
        if (suffix.length() < 1) {
            throw new PidSystemException("Wrong handle identifier.");
        }

        try {
            url = new URL(pidServer + suffix);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("PID System connetion broken ("
                    + url.toString() + ") " + conn.getResponseCode());
            }
        }
        catch (Exception e) {
            throw new PidSystemException(e);
        }
    }

    /**
     * Set the system for PID management service.
     * 
     * @param url
     *            URL to PID Manager Service (http://host[:port]/)
     */
    public void setPidGeneratorServer(final String url) {
        this.pidGeneratorServer = url;
    }

    /**
     * Set the globalPrefix for generated PIDs.
     * 
     * @param globalPrefix
     *            The globalPrefix for generated PIDs
     * @throws MissingMethodParameterException
     *             If <code>globalPrefix</code> is null.
     */
    public void setGlobalPrefix(final String globalPrefix)
        throws MissingMethodParameterException {
        Utility.getInstance().checkNotNull(globalPrefix,
            "global prefix for PID");
        this.globalPrefix = globalPrefix;
    }

    /**
     * Set the localPrefix for generated PIDs. This a part of the PID between
     * the global prefix and the system id. Default is "test" to indicate that
     * the generated (Dummy-)PIDs are not registered.
     * 
     * @param localPrefix
     *            The localPrefix for generated PIDs
     */
    public void setLocalPrefix(final String localPrefix) {
        this.localPrefix = localPrefix;
    }

    /**
     * Set the separator between the parts of generated PIDs. Default is "/".
     * 
     * @param separator
     *            The separator for generated PIDs
     * @throws MissingMethodParameterException
     *             If <code>separator</code> is null.
     */
    public void setSeparator(final String separator)
        throws MissingMethodParameterException {
        Utility.getInstance().checkNotNull(separator, "separator");
        this.separator = separator;
    }

    /**
     * Set the namespace of PID.
     * 
     * @param pidNamespace
     *            The namespace for generated PIDs
     * @throws MissingMethodParameterException
     *             If <code>pidNamespace</code> is null.
     */
    public void setPidNamespace(final String pidNamespace)
        throws MissingMethodParameterException {
        Utility.getInstance().checkNotNull(pidNamespace, "namespace for PID");
        this.pidNamespace = pidNamespace;
    }

    /**
     * Add systemID to data structure for PIDManager.
     * 
     * @param systemID
     *            Objid of resource.
     * @param param
     *            XML parameter from assignPID user interface.
     * @return XML data structure for PID Manager enriched with objid.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    private String preparePidManagerDatastructure(
        final String systemID, final String param)
        throws ParserConfigurationException, SAXException, IOException,
        TransformerFactoryConfigurationError, TransformerException {

        String xmlParam = null;

        // add the systemID of object for semantic identifier
        javax.xml.parsers.DocumentBuilder db =
            javax.xml.parsers.DocumentBuilderFactory
                .newInstance().newDocumentBuilder();
        org.w3c.dom.Document doc =
            db.parse(new ByteArrayInputStream(param.getBytes()));
        NodeList systemIDs = doc.getElementsByTagName("systemID");

        if (systemIDs.getLength() != 1) {
            Node first = doc.getFirstChild();
            Node sysid = doc.createElement("systemID");
            sysid.setTextContent(systemID);
            first.appendChild(sysid);
            Transformer transformer = null;

            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);

            xmlParam = result.getWriter().toString();
        }
        else {
            xmlParam = param;
        }
        return xmlParam;
    }

    /**
     * Obtain PID from respose message of PIDManager.
     * 
     * @param in
     *            InputStream from PIDManager.
     * @return PID
     * 
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private String obtainPidResult(final InputStream in)
        throws ParserConfigurationException, SAXException, IOException {

        javax.xml.parsers.DocumentBuilder db =
            javax.xml.parsers.DocumentBuilderFactory
                .newInstance().newDocumentBuilder();
        org.w3c.dom.Document doc = db.parse(in);
        in.close();

        // retrieve PID result
        org.w3c.dom.NodeList nl = doc.getElementsByTagName("pid");
        org.w3c.dom.Node n = nl.item(0);

        return n.getTextContent();
    }
}
