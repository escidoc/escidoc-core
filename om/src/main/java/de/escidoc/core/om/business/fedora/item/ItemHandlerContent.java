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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.item.Component;
import de.escidoc.core.common.business.interfaces.EscidocServiceRedirectInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.FoXmlProviderConstants;
import de.escidoc.core.om.service.result.EscidocServiceRedirect;

import org.esidoc.core.utils.io.EscidocBinaryContent;
import org.esidoc.core.utils.io.Stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.esidoc.core.utils.io.MimeStream;

/**
 * Content relevant methods for Item.
 *
 * @author Steffen Wagner
 */
public class ItemHandlerContent extends ItemHandlerUpdate {

    private static final String TRANSFORM_SERVICE_DIGILIB = "digilib";

    private static final String TRANSFORM_DIGILIB_CLIENT = "digimage";

    /**
     * Pattern used to detect the redirect url place holder in an http page.
     */
    private static final Pattern PATTERN_REDIRECT_URL = Pattern.compile("\\$\\{REDIRECT_URL\\}");

    public static final String AUTHENTICATION = "eSciDocUserHandle";

    private static final int BUFFER_SIZE = 0xFFFF;

    private static final String BASE_PATH_CONTENT_SERVICE = "/om/contentService/";

    private static final String DIGIMAGE_REDIRECT_FILENAME = BASE_PATH_CONTENT_SERVICE + "redirect.html";

    private final Map<String, String> templates = new HashMap<String, String>();

    /**
     * @param id objid of Item
     * @param componentId
     * @return EscidocBinaryContent
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    public EscidocBinaryContent retrieveContent(final String id, final String componentId)
        throws ResourceNotFoundException, AuthorizationException, FedoraSystemException, TripleStoreSystemException,
        WebserverSystemException, IntegritySystemException, XmlParserSystemException {

        setItem(id);
        Component component = getItem().getComponent(componentId);
        if (component == null) {
            // try to find component by content category
            component = getItem().getComponentsByLocalName().get(componentId);
            if (component == null) {
                throw new ComponentNotFoundException("The component " + componentId + " does not exist in item "
                    + getItem().getId() + '.');
            }
        }

        try {
            checkWithdrawn("Content not retrievable.");
        }
        catch (final InvalidStatusException e1) {
            throw new AuthorizationException(e1);
        }
        final String visibility = component.getProperty(TripleStoreUtility.PROP_VISIBILITY);

        if ("private".equals(visibility) && UserContext.isRetrieveRestrictedToReleased()) {
            throw new AuthorizationException("The Content of the component " + componentId
                + " has visibility 'private'.");
        }
        final EscidocBinaryContent bin = new EscidocBinaryContent();

        // set file name
        final String fileName = component.getProperty(Constants.DC_NS_URI + Elements.ELEMENT_DC_TITLE);
        if (fileName != null && fileName.length() > 0) {
            bin.setFileName(fileName);
        }
        else {
            bin.setFileName("Content of component " + componentId);
        }

        final Datastream content = component.getContent();
        final String storage = content.getControlGroup();

        if ("R".equals(storage)) {
            bin.setRedirectUrl(content.getLocation());
        }
        else {
            try {
                // bin content can be got with the Stream (getContent()),
                // but try to stream
                final MimeStream mimeStream =
                    this.getFedoraServiceClient().getMimeTypedBinaryContent(component.getId(), "content",
                        getItem().getVersionDate());

                try {
                    bin.setContent(mimeStream.getStream().getInputStream());
                }
                catch (IOException e) {
                    throw new WebserverSystemException("Error on loading binary content.", e);
                }

                // set mime-type:
                if (mimeStream.getMimeType() != null) {
                    bin.setMimeType(mimeStream.getMimeType());
                }
                else if (component.getProperty(TripleStoreUtility.PROP_MIME_TYPE) != null) {
                    bin.setMimeType(component.getProperty(TripleStoreUtility.PROP_MIME_TYPE));
                }
                else {
                    bin.setMimeType(FoXmlProviderConstants.MIME_TYPE_APPLICATION_OCTET_STREAM);
                }
            }
            catch (final Exception e) {
                throw new WebserverSystemException(e);
            }
        }

        return bin;
    }

    /**
     * Retrieve Content filtered by a transformation service (Image Scaler, Graphic generator, ..).
     *
     * @param id          The id of the Item.
     * @param componentId The id of the Component.
     * @param transformer The name of the transformation (service).
     * @param param       The transformation parameter as HTTP GET String.
     * @return EscidocBinaryContent of the transformed content.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    public EscidocBinaryContent retrieveContent(
        final String id, final String componentId, final String transformer, final String param)
        throws ItemNotFoundException, ComponentNotFoundException, SystemException, AuthorizationException {

        setItem(id);
        final Component component = getComponent(componentId);

        try {
            checkWithdrawn("Content not retrievable.");
        }
        catch (final InvalidStatusException e1) {
            throw new AuthorizationException(e1);
        }
        final String visibility = component.getProperty(TripleStoreUtility.PROP_VISIBILITY);

        if ("private".equals(visibility) && UserContext.isRetrieveRestrictedToReleased()) {
            throw new AuthorizationException("The content of the component " + componentId
                + " has visibility 'private'.");
        }
        final Datastream content = component.getContent();

        final String storage = content.getControlGroup();

        final EscidocBinaryContent bin = new EscidocBinaryContent();
        if ("R".equals(storage)) {
            bin.setRedirectUrl(content.getLocation());
        }
        final Stream stream =
            this.getFedoraServiceClient().getBinaryContent(component.getId(), "content", getItem().getVersionDate());
        try {
            bin.setContent(stream.getInputStream());
        }
        catch (IOException e) {
            throw new WebserverSystemException("Error on loading binary content.", e);
        }
        bin.setFileName(component.getProperty(de.escidoc.core.common.business.Constants.DC_NS_URI
            + Elements.ELEMENT_DC_TITLE));
        return bin;
    }

    /**
     * Redirect to a content service. The framework can redirect to a content service (like digilib digimage) to provide
     * a convenient REST user interface.
     *
     * @param id            The id of the Item.
     * @param componentId   The id of the component.
     * @param transformer   The name of the transformation service.
     * @param clientService The client name of the transformation service.
     * @return A HTTP/HTML redirect to the client service.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    public EscidocServiceRedirectInterface redirectContentService(
        final String id, final String componentId, final String transformer, final String clientService)
        throws ItemNotFoundException, ComponentNotFoundException, SystemException, AuthorizationException {

        setItem(id);
        final Component component = getComponent(componentId);

        try {
            checkWithdrawn("Content not retrievable.");
        }
        catch (final InvalidStatusException e1) {
            throw new AuthorizationException(e1);
        }
        final String visibility = component.getProperty(TripleStoreUtility.PROP_VISIBILITY);

        if ("private".equals(visibility) && UserContext.isRetrieveRestrictedToReleased()) {
            throw new AuthorizationException("The Content of the component " + componentId
                + " has visibility 'private'.");
        }
        final Datastream content = component.getContent();

        final String storage = content.getControlGroup();
        final String contentUrl;
        if ("R".equals(storage)) {
            contentUrl = content.getLocation();
        }
        else {
            contentUrl =
                EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_BASEURL) + getItem().getHref()
                    + getComponent(componentId).getHrefPart() + "/content";

        }

        final String url;
        if (transformer.equals(TRANSFORM_SERVICE_DIGILIB) && clientService.equals(TRANSFORM_DIGILIB_CLIENT)) {

            url = getServiceUrl(clientService) + "?fn=" + contentUrl;
        }
        else {
            throw new InvalidParameterException("The content transformation service " + transformer
                + " is not supported.");
        }

        final EscidocServiceRedirectInterface response = new EscidocServiceRedirect();
        response.setContent(getServiceRedirect(url));

        return response;
    }

    /**
     * Get the URL of the digilib client (GUI) (digimage).
     *
     * @param service The name of the requested service.
     * @return digilib scaler URL.
     * @throws SystemException Thrown if the URL could not be obtained from configuration.
     */
    private static String getServiceUrl(final String service) throws SystemException {

        final EscidocConfiguration conf = EscidocConfiguration.getInstance();
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
     * Get a HTTP/HTML redirect page. The redirect is set to the redirectUrl parameter.
     *
     * @param redirectUrl The redirect URL.
     * @return The HTML page with the redirect command.
     * @throws WebserverSystemException Thrown if creating of the redirect page failed.
     */
    private String getServiceRedirect(final String redirectUrl) throws WebserverSystemException {

        final String pageContent = getFileContent(DIGIMAGE_REDIRECT_FILENAME);

        return PATTERN_REDIRECT_URL.matcher(pageContent).replaceAll(redirectUrl);
    }

    /**
     * Get content of template file.
     *
     * @param templateFileName The name of the template.
     * @return content of file as String
     * @throws WebserverSystemException Thrown if reading of file failed.
     */
    private String getFileContent(final String templateFileName) throws WebserverSystemException {

        String templ = templates.get(templateFileName);

        if (templ == null) {
            try {
                templ = initFileContent(templateFileName);
            }
            catch (final IOException e) {
                throw new WebserverSystemException(e);
            }
        }

        return templ;
    }

    /**
     * Get the content of the template file as {@code String}.<br> The content is stored in a {@code Map} to
     * prevent unnecessary file resource accesses
     *
     * @param templateFileName The file name of the template that shall be retrieved/loaded.
     * @return content of template file
     * @throws IOException Thrown in case of an I/O error.
     */
    private String initFileContent(final String templateFileName) throws IOException {

        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = ItemHandlerContent.class.getResourceAsStream(templateFileName);
            if (inputStream == null) {
                throw new IOException(StringUtility.format("Template not found", templateFileName));
            }
            final byte[] buffer = new byte[BUFFER_SIZE];
            int length = inputStream.read(buffer);
            while (length != -1) {
                result.write(buffer, 0, length);
                length = inputStream.read(buffer);
            }
            templates.put(templateFileName, result.toString());
        }
        finally {
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
    @Deprecated
    public String createContentStream(final String itemId, final String xmlData) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Deprecated
    public String createContentStreams(final String id, final String xmlData) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Deprecated
    public void deleteContentStream(final String id, final String name) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Deprecated
    public void deleteContentStreams(final String id) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Deprecated
    public String updateContentStream(final String id, final String name, final String xml) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Deprecated
    public String updateContentStreams(final String id, final String xmlData) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.business.interfaces.ItemHandlerInterface#
     * retrieveContentStream(java.lang.String, java.lang.String)
     */
    @Deprecated
    public String retrieveContentStream(final String itemId, final String name) throws ItemNotFoundException,
        ContentStreamNotFoundException, TripleStoreSystemException, IntegritySystemException, WebserverSystemException,
        FedoraSystemException {

        setItem(itemId);
        final String contentStream = renderContentStream(name, true);
        if (contentStream.length() == 0) {
            throw new ContentStreamNotFoundException("The item with id " + itemId
                + " does not contain a content stream" + " with name " + name);
        }
        return contentStream;
    }

    @Deprecated
    public EscidocBinaryContent retrieveContentStreamContent(final String itemId, final String name)
        throws ItemNotFoundException, AuthorizationException, FedoraSystemException, TripleStoreSystemException,
        WebserverSystemException, IntegritySystemException {

        setItem(itemId);
        try {
            checkWithdrawn("Content not retrievable.");
        }
        catch (final InvalidStatusException e1) {
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
    @Deprecated
    public String retrieveContentStreams(final String itemId) throws ItemNotFoundException, IntegritySystemException,
        FedoraSystemException, TripleStoreSystemException, WebserverSystemException {
        setItem(itemId);
        return renderContentStreams(true);
    }

    @Deprecated
    private EscidocBinaryContent getContentStream(final String name) throws WebserverSystemException {

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
            final Stream stream =
                this.getFedoraServiceClient().getBinaryContent(getItem().getId(), name, getItem().getVersionDate());
            try {
                bin.setContent(stream.getInputStream());
            }
            catch (IOException e) {
                throw new WebserverSystemException("Error on loading binary content.", e);
            }
        }

        return bin;
    }

}
