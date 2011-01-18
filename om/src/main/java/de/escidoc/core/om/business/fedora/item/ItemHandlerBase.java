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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.HandlerBase;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.resources.Item;
import de.escidoc.core.common.business.fedora.resources.item.Component;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.FileSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.factory.ItemXmlProvider;
import de.escidoc.core.common.util.xml.factory.RelationsXmlProvider;
import de.escidoc.core.common.util.xml.renderer.VelocityXmlItemFoXmlRenderer;
import de.escidoc.core.common.util.xml.renderer.interfaces.ItemFoXmlRendererInterface;

/**
 * Contains base functionality of FedoraItemHandler. Is extended at least by
 * FedoraItemHandler.
 * 
 * @author FRS
 * 
 */
public class ItemHandlerBase extends HandlerBase {

    private static final String ERROR_MSG_NO_HTTP_PROTOCOL =
        "The url has a wrong protocol."
            + " The protocol must be a http protocol.";

    private static final Pattern PATTERN_ERROR_GETTING = Pattern.compile(
        "fedora.server.errors.GeneralException: Error getting",
        Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_MALFORMED_URL = Pattern
        .compile("fedora.server.errors.ObjectIntegrityException: "
            + "FOXML IO stream was bad : Malformed URL");

    private static AppLogger log = new AppLogger(
        ItemHandlerBase.class.getName());

    private Item item = null;

    private Item originItem = null;

    private String originId = null;

    private ItemFoXmlRendererInterface foxmlRenderer = null;

    /**
     * Upload the content (a base64 encoded byte stream) to the staging area.
     * 
     * @param content
     *            Base64 encoded byte stream.
     * @param fileName
     *            The file name.
     * @param mimeType
     *            The mime type of the content.
     * @return The url to the staging area where the resulting file is
     *         accessible.
     * @throws WebserverSystemException
     *             In case of an internal error during decoding or storing the
     *             content.
     */
    protected String uploadBase64EncodedContent(
        final String content, final String fileName, final String mimeType)
        throws WebserverSystemException {
        String uploadUrl = null;
        byte[] streamContent = null;
        try {
            streamContent = Base64.decodeBase64(content.getBytes());
            uploadUrl =
                Utility.getInstance().upload(streamContent, fileName, mimeType);
        }
        catch (final FileSystemException e) {
            log.error("Error while uploading of content to the staging area. "
                + e.getMessage());
            throw new WebserverSystemException(
                "Error while uploading of content to the staging area. ", e);
        }

        return uploadUrl;
    }

    /**
     * Returns an instance of ItemXmlProvider.
     * 
     * @return ItemXmlProvider.
     */
    protected ItemXmlProvider getItemXmlProvider() {
        return ItemXmlProvider.getInstance();
    }

    /**
     * Returns an instance of RelationsXmlProvider.
     * 
     * @return RelationsXmlProvider.
     */
    protected RelationsXmlProvider getRelationsXmlProvider() {

        return RelationsXmlProvider.getInstance();
    }

    /**
     * Returns the item that is managed by this ItemHandler. Every service
     * method has to call setItem().
     * 
     * @return the item
     */
    public Item getItem() {
        return item;
    }

    /**
     * Returns the origin item of the surrogate item that is managed by this
     * ItemHandler. Every service method has to call setOriginItem().
     * 
     * @return the item
     */
    public Item getOriginItem() {
        return originItem;
    }

    /**
     * Bounds a Item object to this handler. Subsequent calls to this method
     * have no effect.
     * 
     * @param id
     *            The ID of the item which should be bound to this Handler.
     * @throws ItemNotFoundException
     *             If there is no item with <code>id</code> in the repository.
     * @throws SystemException
     *             Thrown in case of an internal system error.
     */
    protected void setItem(final String id) throws ItemNotFoundException,
        SystemException {

        try {
            this.item = new Item(id);
        }
        catch (final StreamNotFoundException e) {
            throw new ItemNotFoundException(e);
        }
        catch (final ResourceNotFoundException e) {
            throw new ItemNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * Bounds an OriginItem of an SurrogateItem object to this handler.
     * Subsequent calls to this method have no effect.
     * 
     * @param id
     *            The ID of the item which should be bound to this Handler.
     * @throws ItemNotFoundException
     *             If there is no item with <code>id</code> in the repository.
     * @throws SystemException
     *             Thrown in case of an internal system error.
     */
    protected void setOriginItem(final String id) throws ItemNotFoundException,
        SystemException {
        try {
            this.originItem = new Item(id);
        }
        catch (final StreamNotFoundException e) {
            throw new ItemNotFoundException(e);
        }
        catch (final ResourceNotFoundException e) {
            throw new ItemNotFoundException(e.getMessage(), e);
        }

    }

    /**
     * Unbounds an OriginItem of an SurrogateItem object from this handler.
     * Subsequent calls to this method have no effect.
     * 
     */
    protected void resetOriginItem() {
        this.originItem = null;
        this.originId = null;
    }

    /**
     * Bounds an OriginItem of an SurrogateItem object to this handler.
     * Subsequent calls to this method have no effect.
     * 
     * @param originItem
     *            The item which should be bound to this Handler.
     */
    protected void setOriginItem(final Item originItem) {
        this.originItem = originItem;

    }

    /**
     * Returns the specified component if it belongs to this item. Every service
     * method has to call setItem() first.
     * 
     * @param id
     *            ID of the component.
     * @return The requested component object or null.
     * @throws ComponentNotFoundException
     *             Thrown if Component with provided objid could not be found.
     * @throws SystemException
     *             Thrown in case of internal error.
     */
    public Component getComponent(final String id)
        throws ComponentNotFoundException, SystemException {

        Component c = null;
        try {
            if (getOriginItem() != null) {
                c = getOriginItem().getComponent(id);
            }
            else {
                c = getItem().getComponent(id);
            }
        }
        catch (ComponentNotFoundException e) {
            if (getOriginItem() != null) {
                c = getOriginItem().getComponentByLocalName(id);
            }
            else {
                c = getItem().getComponentByLocalName(id);
            }
        }
        return c;
    }

    /**
     * Get versions datastream of the item.
     * 
     * @return The versions datastream.
     * @throws StreamNotFoundException
     *             If a datastream can not be retrieved.
     * @throws FedoraSystemException
     *             If Fedora reports an error.
     * @throws EncodingSystemException
     *             In case of an encoding failure.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    protected String getVersions() throws StreamNotFoundException,
        FedoraSystemException, EncodingSystemException,
        WebserverSystemException {

        return getItem().getWov().toStringUTF8();
    }

    /**
     * Check if the item is locked.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws LockingException
     *             If the item is locked and the current user is not the one who
     *             locked it.
     */
    protected void checkLocked() throws LockingException,
        WebserverSystemException {
        if (getItem().isLocked()
            && !getItem().getLockOwner().equals(
                getUtility().getCurrentUser()[0])) {
            throw new LockingException("Item + " + getItem().getId()
                + " is locked by " + getItem().getLockOwner() + ".");
        }
    }

    /**
     * Check if the requested item version is the latest version.
     * 
     * @throws ReadonlyVersionException
     *             If the requested item version is not the latest one.
     * 
     */
    protected void checkLatestVersion() throws ReadonlyVersionException {
        final String thisVersion = getItem().getVersionNumber();
        if (thisVersion != null
            && !thisVersion.equals(getItem().getLatestVersionNumber())) {
            throw new ReadonlyVersionException(
                "Only latest version can be modified.");
        }
    }

    /**
     * Check release status of object.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws InvalidStatusException
     *             Thrown if object is not in status released.
     * @throws TripleStoreSystemException
     *             If the triple store reports an error.
     */
    protected void checkReleased() throws InvalidStatusException,
        TripleStoreSystemException, WebserverSystemException {

        final String status =
            getItem().getProperty(PropertyMapKeys.PUBLIC_STATUS);
        // TripleStoreUtility.getInstance().getPropertiesElements(
        // getItem().getId(), TripleStoreUtility.PROP_PUBLIC_STATUS);
        // In first release, if object is once released no changes are allowed
        if (status.equals(Constants.STATUS_RELEASED)) {
            // check if the version is in status released
            // FIXME check if the LATEST version is in status released. That
            // seems to be the same because all methods that call checkReleased
            // also call checkLatestVersion. But the semantic should be true
            // without another method call. (? FRS)
            // if (TripleStoreUtility.getInstance().getPropertiesElements(
            // getItem().getId(),
            // TripleStoreUtility.PROP_LATEST_VERSION_STATUS).equals(
            // Constants.STATUS_RELEASED)) {
            if (getItem().getProperty(
                PropertyMapKeys.LATEST_VERSION_VERSION_STATUS).equals(
                Constants.STATUS_RELEASED)) {

                final String msg =
                    "The object is in state '" + Constants.STATUS_RELEASED
                        + "' and can not be" + " changed.";
                log.debug(msg);
                throw new InvalidStatusException(msg);
            }
        }
    }

    /**
     * Check status of a Context. An invalidStatusException is thrown if the
     * Context has not the requested status.
     * 
     * @param contextId
     *            The Id of the Context.
     * @param status
     *            The expected status of the Context.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws InvalidStatusException
     *             Thrown if the Context is not in the requested status.
     * @throws TripleStoreSystemException
     *             If the triple store reports an error.
     */
    protected void checkContextStatus(
        final String contextId, final String status)
        throws InvalidStatusException, TripleStoreSystemException,
        WebserverSystemException {

        if (contextId == null || status == null) {
            throw new WebserverSystemException(
                "Context id and status must not be 'null' for check "
                    + "context status.");
        }
        final String curStatus =
            getTripleStoreUtility().getPropertiesElements(contextId,
                TripleStoreUtility.PROP_PUBLIC_STATUS);
        if (curStatus == null || curStatus.length() == 0) {
            final String msg =
                "Can not get status of context " + contextId + ".";
            log.error(msg);
            throw new WebserverSystemException(msg);
        }
        // In first release, if object is once released no changes are allowed
        if (!curStatus.equals(status)) {
            final String msg =
                "The Context '" + contextId + "' is in state '" + curStatus
                    + "' and not in status " + status + ".";
            log.debug(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * Check if item is tagged with status released.
     * 
     * @throws InvalidStatusException
     *             If item status is not released.
     * @throws WebserverSystemException
     *             In case of an internal error.
     * @throws TripleStoreSystemException
     *             If the triple store reports an error.
     */
    protected void checkNotReleased() throws InvalidStatusException,
        TripleStoreSystemException, WebserverSystemException {
        checkNotStatus(Constants.STATUS_RELEASED);
    }

    /**
     * Check if item is not submitted.
     * 
     * @throws InvalidStatusException
     *             If item status is not released.
     * @throws TripleStoreSystemException
     *             If the triple store reports an error.
     * @throws WebserverSystemException
     *             In case of an internal error.
     */
    protected void checkNotSubmitted() throws InvalidStatusException,
        TripleStoreSystemException, WebserverSystemException {
        checkNotStatus(Constants.STATUS_SUBMITTED);
    }

    /**
     * Check if item is not in the specified status.
     * 
     * @param status
     *            A status.
     * 
     * @throws InvalidStatusException
     *             If item status is not in requested status.
     * @throws TripleStoreSystemException
     *             If the triple store reports an error.
     * @throws WebserverSystemException
     *             In case of an internal error.
     */
    protected void checkNotStatus(final String status)
        throws InvalidStatusException, TripleStoreSystemException,
        WebserverSystemException {

        // In first release, if object is once released no changes are allowed
        if (!status.equals(getItem().getStatus())) {
            final String msg = "The object is in not state '" + status + "'.";
            log.debug(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * Check if item is in the specified status.
     * 
     * @param status
     *            A status.
     * 
     * @throws InvalidStatusException
     *             If item status is in requested status.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    // TODO make this consistent to Container (proposal: checkStatusNot())
    protected void checkStatus(final String status)
        throws InvalidStatusException, WebserverSystemException {

        // In first release, if object is once released no changes are allowed
        if (status.equals(getItem().getStatus())) {
            final String msg = "The object is in state '" + status + "'.";
            log.debug(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * Check if item version is tagged with status released.
     * 
     * @param checkStatus
     *            The status which is to validate.
     * 
     * @throws InvalidStatusException
     *             If item version status is not equal to the requested
     *             <code>checkStatus</code>.
     * @throws IntegritySystemException
     *             Thrown if version status could not be obtained.
     */
    protected void checkVersionStatus(final String checkStatus)
        throws InvalidStatusException, IntegritySystemException {

        final String status = getItem().getVersionStatus();

        // In first release, if object is once released no changes are allowed
        if (!status.equals(checkStatus)) {
            final String msg =
                "The object is in state '" + checkStatus + "' and can not be"
                    + " changed.";
            log.debug(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * Check if item version is tagged with status released.
     * 
     * @param checkStatus
     *            The status which is to validate.
     * 
     * @throws InvalidStatusException
     *             If item status is equal to the requested.
     * @throws IntegritySystemException
     *             Thrown if version status could not be obtained.
     */
    protected void checkVersionStatusNot(final String checkStatus)
        throws InvalidStatusException, IntegritySystemException {

        final String status = getItem().getVersionStatus();

        if (status.equals(checkStatus)) {
            final String msg =
                "The object is in state '" + checkStatus + "' and can not be"
                    + " changed.";
            log.debug(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * Check if the item is in state withdrawn.
     * 
     * @param additionalMessage
     *            An error message prefix.
     * @throws InvalidStatusException
     *             If the item is not in state withdrawn.
     * @throws WebserverSystemException
     *             In case of an internal error.
     * @throws TripleStoreSystemException
     *             If the triple store reports an error.
     */
    protected void checkWithdrawn(final String additionalMessage)
        throws InvalidStatusException, TripleStoreSystemException,
        WebserverSystemException {

        final String status =
            getItem().getProperty(PropertyMapKeys.PUBLIC_STATUS);
        if (status.equals(Constants.STATUS_WITHDRAWN)) {
            final String msg =
                "The object is in state '" + Constants.STATUS_WITHDRAWN + "'. "
                    + additionalMessage;
            log.debug(msg);
            throw new InvalidStatusException(msg);

        }
    }

    /**
     * Handle a Fedora Exception thrown while uploading content.
     * 
     * @param url
     *            The URL.
     * @param e
     *            The Fedora Exception.
     * @throws FileNotFoundException
     *             Thrown if access to remote resource failed.
     * @throws FedoraSystemException
     *             Thrown if Fedora reports an error.
     */
    protected void handleFedoraUploadError(
        final String url, final FedoraSystemException e)
        throws FileNotFoundException, FedoraSystemException {

        final Matcher matcherErrorGetting =
            PATTERN_ERROR_GETTING.matcher(e.getMessage());
        final Matcher matcherMalformedUrl =
            PATTERN_MALFORMED_URL.matcher(e.getMessage());

        if (matcherErrorGetting.find() || matcherMalformedUrl.find()) {
            throw new FileNotFoundException(
                "Error getting content from " + url, e);
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            throw new FileNotFoundException(ERROR_MSG_NO_HTTP_PROTOCOL);
        }
        // TODO: Reuse HttpClient
        final HttpClient client = new DefaultHttpClient();
        try {
            final HttpGet method = new HttpGet(url);
            final HttpResponse response = client.execute(method);
            final int resultCode = response.getStatusLine().getStatusCode();
            if (resultCode != HttpServletResponse.SC_OK) {
                final String errorMsg =
                    StringUtility.concatenateWithBracketsToString(
                        "Bad request. ", response.getStatusLine(), url);
                log.error(errorMsg);
                throw new FileNotFoundException(errorMsg);
            }

        }
        catch (final Exception e1) {
            throw new FileNotFoundException(
                "Error getting content from " + url, e1);
        }
        finally {
            client.getConnectionManager().shutdown();
        }
        throw e;
    }

    /**
     * Get FoXML render.
     * 
     * @return The foxml renderer.
     */
    public ItemFoXmlRendererInterface getFoxmlRenderer() {

        if (foxmlRenderer == null) {
            foxmlRenderer = new VelocityXmlItemFoXmlRenderer();
        }
        return foxmlRenderer;
    }

    /**
     * Get objid of origin (like user has set).
     * 
     * @return objid of origin
     */
    public String getOriginId() {
        return originId;
    }

    /**
     * Set objid of origin (like user has set).
     * 
     * @param originId
     *            objid of origin.
     */
    public void setOriginId(final String originId) {
        this.originId = originId;
    }
}
