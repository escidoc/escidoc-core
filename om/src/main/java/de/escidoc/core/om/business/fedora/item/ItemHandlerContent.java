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
package de.escidoc.core.om.business.fedora.item;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.item.Component;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.om.service.interfaces.EscidocServiceRedirectInterface;
import de.escidoc.core.om.service.result.EscidocServiceRedirect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Content relevant methods for Item.
 * 
 * @author SWA
 * 
 */
public class ItemHandlerContent extends ItemHandlerUpdate {

    private static final AppLogger LOGGER =
        new AppLogger(ItemHandlerContent.class.getName());

    private static final String TRANSFORM_SERVICE_DIGILIB = "digilib";

    private static final String TRANSFORM_DIGILIB_CLIENT = "digimage";

    /**
     * Pattern used to detect the redirect url place holder in an http page.
     */
    private static final Pattern PATTERN_REDIRECT_URL =
        Pattern.compile("\\$\\{REDIRECT_URL\\}");

    public static final String AUTHENTICATION = "eSciDocUserHandle";

    private static final int BUFFER_SIZE = 0xFFFF;

    private static final String BASE_PATH_CONTENT_SERVICE =
        "/om/contentService/";

    private static final String DIGIMAGE_REDIRECT_FILENAME =
        BASE_PATH_CONTENT_SERVICE + "redirect.html";

    private final Map<String, String> templates = new HashMap<String, String>();

    /**
     * @param id
     *            objid of Item
     * @param componentId
     * @return EscidocBinaryContent
     * @throws ItemNotFoundException
     * @throws ComponentNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws InvalidStatusException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieveContent(java.lang.String,java.lang.String)
     */
    public EscidocBinaryContent retrieveContent(
        final String id, final String componentId)
        throws ItemNotFoundException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException,
        InvalidStatusException, ResourceNotFoundException,
        AuthorizationException {

        setItem(id);
        Component component = getItem().getComponent(componentId);
        if (component == null) {
            // try to find component by content category
            component = getItem().getComponentsByLocalName().get(componentId);
            if (component == null) {
                throw new ComponentNotFoundException("The component "
                    + componentId + " does not exist in item "
                    + getItem().getId() + '.');
            }
        }

        try {
            checkWithdrawn("Content not retrievable.");
        }
        catch (InvalidStatusException e1) {
            throw new AuthorizationException(e1);
        }
        final String visibility =
            component.getResourceProperties().get(
                TripleStoreUtility.PROP_VISIBILITY);

        if ("private".equals(visibility)
            && UserContext.isRetrieveRestrictedToReleased()) {
            throw new AuthorizationException("The Content of the component "
                + componentId + " has visibility 'private'.");
        }
        final Datastream content = component.getContent();

        final String storage = content.getControlGroup();
        final EscidocBinaryContent bin = new EscidocBinaryContent();

        final Map<String, String> properties = component.getResourceProperties();
        String fileName;

        if (component.getMdRecords().containsKey(
            XmlTemplateProvider.DEFAULT_METADATA_FOR_DC_MAPPING)
            && !component
                .getMdRecord(
                    XmlTemplateProvider.DEFAULT_METADATA_FOR_DC_MAPPING)
                .isDeleted()) {
            fileName =
                properties
                    .get(de.escidoc.core.common.business.Constants.DC_NS_URI
                        + Elements.ELEMENT_DC_TITLE);
            if ((fileName == null) || fileName.length() == 0) {
                fileName = "Content of component " + componentId;
            }
        }
        else {
            fileName = "Content of component " + componentId;
        }
        bin.setFileName(fileName);

        final String mimeType = properties.get(TripleStoreUtility.PROP_MIME_TYPE);
        bin.setMimeType(mimeType);

        try {
            if ("R".equals(storage)) {
                bin.setRedirectUrl(content.getLocation());
            }
            else {
                // bin content can be got with the Datastream (getContent()),
                // but try to stream
                String fedoraLocalUrl =
                    "/get/" + component.getId() + "/content";
                if (getItem().getVersionDate() != null) {
                    fedoraLocalUrl += '/' + getItem().getVersionDate();
                }
                bin.setContent(getFedoraUtility().requestFedoraURL(
                    fedoraLocalUrl));
            }
        }
        catch (Exception e) {
            throw new WebserverSystemException(e);
        }

        return bin;
    }

    /**
     * Retrieve Content filtered by a transformation service (Image Scaler,
     * Graphic generator, ..).
     * 
     * @param id
     *            The id of the Item.
     * @param componentId
     *            The id of the Component.
     * @param transformer
     *            The name of the transformation (service).
     * @param param
     *            The transformation parameter as HTTP GET String.
     * @return EscidocBinaryContent of the transformed content.
     * 
     * @throws ItemNotFoundException
     * @throws ComponentNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws InvalidStatusException
     * @throws AuthorizationException
     */
    public EscidocBinaryContent retrieveContent(
        final String id, final String componentId, final String transformer,
        final String param) throws ItemNotFoundException,
        ComponentNotFoundException, MissingMethodParameterException,
        SystemException, InvalidStatusException, AuthorizationException {

        setItem(id);
        final Component component = getComponent(componentId);

        try {
            checkWithdrawn("Content not retrievable.");
        }
        catch (InvalidStatusException e1) {
            throw new AuthorizationException(e1);
        }
        final String visibility =
            component.getResourceProperties().get(
                TripleStoreUtility.PROP_VISIBILITY);

        if ("private".equals(visibility)
            && UserContext.isRetrieveRestrictedToReleased()) {
            throw new AuthorizationException("The Content of the component "
                + componentId + " has visibility 'private'.");
        }
        final Datastream content = component.getContent();

        final String storage = content.getControlGroup();

        final EscidocBinaryContent bin = new EscidocBinaryContent();
        if ("R".equals(storage)) {
            bin.setRedirectUrl(content.getLocation());
        }

        final URL url;
        try {
            url =
                getContentUrl(component.getId(), getItem().getVersionDate(),
                    transformer, param);
        }
        catch (MalformedURLException e1) {
            throw new WebserverSystemException("Internal content URL corrupt.",
                e1);
        }

        final String fileName =
            component.getResourceProperties().get(
                TripleStoreUtility.PROP_FILENAME);

        getBinaryContent(bin, url, fileName);

        return bin;
    }

    /**
     * Redirect to a content service. The framework can redirect to a content
     * service (like digilib digimage) to provide a convenient REST user
     * interface.
     * 
     * @param id
     *            The id of the Item.
     * @param componentId
     *            The id of the component.
     * @param transformer
     *            The name of the transformation service.
     * @param clientService
     *            The client name of the transformation service.
     * @return A HTTP/HTML redirect to the client service.
     * 
     * @throws ItemNotFoundException
     * 
     * @throws ComponentNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws InvalidStatusException
     * @throws AuthorizationException
     */
    public EscidocServiceRedirectInterface redirectContentService(
        final String id, final String componentId, final String transformer,
        final String clientService) throws ItemNotFoundException,
        ComponentNotFoundException, MissingMethodParameterException,
        SystemException, InvalidStatusException, AuthorizationException {

        setItem(id);
        final Component component = getComponent(componentId);

        try {
            checkWithdrawn("Content not retrievable.");
        }
        catch (InvalidStatusException e1) {
            throw new AuthorizationException(e1);
        }
        final String visibility =
            component.getResourceProperties().get(
                TripleStoreUtility.PROP_VISIBILITY);

        if ("private".equals(visibility)
            && UserContext.isRetrieveRestrictedToReleased()) {
            throw new AuthorizationException("The Content of the component "
                + componentId + " has visibility 'private'.");
        }
        final Datastream content = component.getContent();

        final String storage = content.getControlGroup();
        final String contentUrl;
        if ("R".equals(storage)) {
            contentUrl = content.getLocation();
        }
        else {
            try {
                contentUrl =
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.ESCIDOC_CORE_BASEURL)
                        + getItem().getHref()
                        + getComponent(componentId).getHrefPart() + "/content";
                // + "/" + getItem().getVersionDate();
            }
            catch (IOException e) {
                throw new WebserverSystemException(e);
            }
        }

        final String url;
        if ((transformer.equals(TRANSFORM_SERVICE_DIGILIB))
            && (clientService.equals(TRANSFORM_DIGILIB_CLIENT))) {

            url = getServiceUrl(clientService) + "?fn=" + contentUrl;
        }
        else {
            throw new InvalidParameterException(
                "The content transformation service " + transformer
                    + " is not supported.");
        }

        final EscidocServiceRedirectInterface response = new EscidocServiceRedirect();
        response.setContent(getServiceRedirect(url));

        return response;
    }

    /**
     * Get the URL of the digilib scaler.
     * 
     * @return digilib scaler URL.
     * @throws SystemException
     *             Thrown if the URL could not be obtained from configuration.
     */
    private String getDigilibScalerUrl() throws SystemException {

        final String diglibUrl;
        try {
            diglibUrl =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.DIGILIB_SCALER);
        }
        catch (IOException e) {
            throw new SystemException(e);
        }

        return diglibUrl;
    }

    /**
     * Get the URL of the digilib client (GUI) (digimage).
     * 
     * @param service
     *            The name of the requested service.
     * @return digilib scaler URL.
     * @throws SystemException
     *             Thrown if the URL could not be obtained from configuration.
     */
    private String getServiceUrl(final String service) throws SystemException {

        final EscidocConfiguration conf;
        try {
            conf = EscidocConfiguration.getInstance();
        }
        catch (IOException e) {
            throw new SystemException(e);
        }

        // -----------------------------------
        final String serviceUrl;
        if (service.equals(TRANSFORM_DIGILIB_CLIENT)) {
            serviceUrl = conf.get(EscidocConfiguration.DIGILIB_CLIENT);
        }
        else {
            throw new SystemException("Service not supported.");
        }

        return serviceUrl;
    }

    /**
     * Get a HTTP/HTML redirect page. The redirect is set to the redirectUrl
     * parameter.
     * 
     * @param redirectUrl
     *            The redirect URL.
     * @return The HTML page with the redirect command.
     * 
     * @throws WebserverSystemException
     *             Thrown if creating of the redirect page failed.
     */
    private String getServiceRedirect(final String redirectUrl)
        throws WebserverSystemException {

        final String pageContent = getFileContent(DIGIMAGE_REDIRECT_FILENAME);

        return PATTERN_REDIRECT_URL
            .matcher(pageContent).replaceAll(redirectUrl);
    }

    /**
     * Get content of template file.
     * 
     * @param templateFileName
     *            The name of the template.
     * @return content of file as String
     * @throws WebserverSystemException
     *             Thrown if reading of file failed.
     */
    private String getFileContent(final String templateFileName)
        throws WebserverSystemException {

        String templ = templates.get(templateFileName);

        if (templ == null) {
            try {
                templ = initFileContent(templateFileName);
            }
            catch (IOException e) {
                throw new WebserverSystemException(e);
            }
        }

        return templ;
    }

    /**
     * Get the content of the template file as <code>String</code>.<br>
     * The content is stored in a <code>Map</code> to prevent unnecessary file
     * resource accesses
     * 
     * @param templateFileName
     *            The file name of the template that shall be retrieved/loaded.
     * @return content of template file
     * 
     * @throws IOException
     *             Thrown in case of an I/O error.
     * @om
     */
    private String initFileContent(final String templateFileName)
        throws IOException {

        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getResourceAsStream(templateFileName);
            if (inputStream == null) {
                throw new IOException(StringUtility.format(
                        "Template not found", templateFileName));
            }
            final byte[] buffer = new byte[BUFFER_SIZE];
            int length = inputStream.read(buffer);
            while (length != -1) {
                result.write(buffer, 0, length);
                length = inputStream.read(buffer);
            }
            templates.put(templateFileName, result.toString());
        } finally {
            IOUtils.closeStream(inputStream);
        }
        return result.toString(XmlUtility.CHARACTER_ENCODING);
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.business.interfaces.ItemHandlerInterface#
     * createContentStream(java.lang.String, java.lang.String)
     */
    public String createContentStream(final String itemId, final String xmlData) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public String createContentStreams(final String id, final String xmlData) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void deleteContentStream(final String id, final String name)
        throws ItemNotFoundException, SystemException,
        ContentStreamNotFoundException {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public void deleteContentStreams(final String id)
        throws ItemNotFoundException, SystemException {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public String updateContentStream(
        final String id, final String name, final String xml)
        throws ItemNotFoundException, SystemException,
        ContentStreamNotFoundException {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public String updateContentStreams(final String id, final String xmlData)
        throws ItemNotFoundException, SystemException {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.business.interfaces.ItemHandlerInterface#
     * retrieveContentStream(java.lang.String, java.lang.String)
     */
    public String retrieveContentStream(final String itemId, final String name)
        throws ItemNotFoundException, SystemException,
        ContentStreamNotFoundException, AuthorizationException {

        setItem(itemId);
        final String contentStream = renderContentStream(name, true);
        if (contentStream.length() == 0) {
            throw new ContentStreamNotFoundException("The item with id " + itemId
                    + " does not contain a content stream" + " with name "
                    + name);
        }
        return contentStream;
    }

    public EscidocBinaryContent retrieveContentStreamContent(
        final String itemId, final String name) throws ItemNotFoundException,
        SystemException, ContentStreamNotFoundException, AuthorizationException {

        setItem(itemId);
        try {
            checkWithdrawn("Content not retrievable.");
        }
        catch (InvalidStatusException e1) {
            throw new AuthorizationException(e1);
        }
        return getContentStream(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.business.interfaces.ItemHandlerInterface#
     * retrieveContentStreams(java.lang.String)
     */
    public String retrieveContentStreams(final String itemId)
        throws ItemNotFoundException, SystemException, AuthorizationException {
        setItem(itemId);
        return renderContentStreams(true);
    }

    private EscidocBinaryContent getContentStream(final String name)
        throws ContentStreamNotFoundException, FedoraSystemException,
        WebserverSystemException {

        final Datastream cs = getItem().getContentStream(name);

        final EscidocBinaryContent bin = new EscidocBinaryContent();
        final String fileName = cs.getLabel();
        bin.setFileName(fileName);

        final String mimeType = cs.getMimeType();
        bin.setMimeType(mimeType);

        if ("R".equals(cs.getControlGroup())) {
            bin.setRedirectUrl(cs.getLocation());
        }
        else {
            String fedoraLocalUrl = "/get/" + getItem().getId() + '/' + name;
            if (getItem().getVersionDate() != null) {
                fedoraLocalUrl += '/' + getItem().getVersionDate();
            }
            bin.setContent(getFedoraUtility()
                .requestFedoraURL(fedoraLocalUrl));
        }

        return bin;
    }

    /**
     * Build the Url for the binary content.
     * 
     * @param componentId
     *            The Id of the component.
     * @param versionDate
     *            The version date.
     * @param transformer
     *            The name of the transformation service.
     * @param param
     *            The parameter for the transformation service as GET parameter.
     * @return The Url of the binary content responding to the version.
     * @throws MalformedURLException
     *             Thrown id created Url string is now valid URL.
     * @throws SystemException
     *             Thrown in case of internal configuration failure.
     */
    private URL getContentUrl(
        final String componentId, final String versionDate,
        final String transformer, final String param)
        throws MalformedURLException, SystemException {
        final String fedoraUser =
            System.getProperty(EscidocConfiguration.FEDORA_USER);
        final String fedoraPw =
            System.getProperty(EscidocConfiguration.FEDORA_PASSWORD);
        final String auth = fedoraUser + ':' + fedoraPw + '@';

        final String fedoraUrl = System.getProperty(EscidocConfiguration.FEDORA_URL);
        final int pos = fedoraUrl.indexOf("://");
        final String protocol = fedoraUrl.substring(0, pos + 3);
        final String hostPart = fedoraUrl.substring(pos + 3);

        final String contentUrl =
            protocol + auth + hostPart + "/get/" + componentId + "/content"
                + '/' + versionDate;

        final URL url;
        if (transformer.equals(TRANSFORM_SERVICE_DIGILIB)) {
            url =
                new URL(getDigilibScalerUrl() + "?fn=" + contentUrl + '&'
                    + param);
        }
        else {
            throw new InvalidParameterException(
                "The content transformation service " + transformer
                    + " is not supported.");
        }

        return url;
    }

    /**
     * 
     * @param bin
     * @param url
     * @param fileName
     * @throws WebserverSystemException
     */
    private void getBinaryContent(
        final EscidocBinaryContent bin, final URL url, final String fileName)
        throws WebserverSystemException {
      
        bin.setContent(getFedoraUtility().requestFedoraURL(url.toString()
            ));
        
    }

}
