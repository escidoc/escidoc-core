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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
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
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.FileSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.factory.ItemXmlProvider;
import de.escidoc.core.common.util.xml.factory.RelationsXmlProvider;
import de.escidoc.core.common.util.xml.renderer.VelocityXmlItemFoXmlRenderer;
import de.escidoc.core.common.util.xml.renderer.interfaces.ItemFoXmlRendererInterface;

/**
 * Contains base functionality of FedoraItemHandler. Is extended at least by FedoraItemHandler.
 *
 * @author Frank Schwichtenberg
 */
public class ItemHandlerBase extends HandlerBase {

    private static final String ERROR_MSG_NO_HTTP_PROTOCOL =
        "The url has a wrong protocol." + " The protocol must be a http protocol.";

    private static final Pattern PATTERN_ERROR_GETTING =
        Pattern.compile("fedora.server.errors.GeneralException: Error getting", Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_MALFORMED_URL =
        Pattern.compile("fedora.server.errors.ObjectIntegrityException: " + "FOXML IO stream was bad : Malformed URL");

    private Item item;

    private Item originItem;

    private String originId;

    private ItemFoXmlRendererInterface foxmlRenderer;

    /**
     * The policy decision point used to check access privileges.
     */
    @Autowired
    @Qualifier("service.PolicyDecisionPoint")
    private PolicyDecisionPointInterface pdp;

    /**
     * Upload the content (a base64 encoded byte stream) to the staging area.
     *
     * @param content  Base64 encoded byte stream.
     * @param fileName The file name.
     * @param mimeType The mime type of the content.
     * @return The url to the staging area where the resulting file is accessible.
     * @throws WebserverSystemException In case of an internal error during decoding or storing the content.
     */
    final String uploadBase64EncodedContent(final String content, final String fileName, final String mimeType)
        throws WebserverSystemException {
        final String uploadUrl;
        try {
            final byte[] streamContent = Base64.decodeBase64(content.getBytes());
            uploadUrl = this.getUtility().upload(streamContent, fileName, mimeType);
        }
        catch (final FileSystemException e) {
            throw new WebserverSystemException("Error while uploading of content to the staging area. ", e);
        }

        return uploadUrl;
    }

    /**
     * Returns an instance of ItemXmlProvider.
     *
     * @return ItemXmlProvider.
     */
    static ItemXmlProvider getItemXmlProvider() {
        return ItemXmlProvider.getInstance();
    }

    /**
     * Returns an instance of RelationsXmlProvider.
     *
     * @return RelationsXmlProvider.
     */
    protected static RelationsXmlProvider getRelationsXmlProvider() {

        return RelationsXmlProvider.getInstance();
    }

    /**
     * Returns the item that is managed by this ItemHandler. Every service method has to call setItem().
     *
     * @return the item
     */
    final Item getItem() {
        return this.item;
    }

    /**
     * Returns the origin item of the surrogate item that is managed by this ItemHandler. Every service method has to
     * call setOriginItem().
     *
     * @return the item
     */
    final Item getOriginItem() {
        return this.originItem;
    }

    /**
     * Bounds a Item object to this handler. Subsequent calls to this method have no effect.
     *
     * @param id The ID of the item which should be bound to this Handler.
     * @throws ItemNotFoundException If there is no item with {@code id} in the repository.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    final void setItem(final String id) throws ItemNotFoundException, TripleStoreSystemException,
        IntegritySystemException, FedoraSystemException, WebserverSystemException {

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
     * Load origin Item. User permissions are checked.
     *
     * @param errorMessage The error message if failure occurs because of permission restriction.
     * @return true if origin Item was loaded, false otherwise
     * @throws ItemNotFoundException  Thrown if Item with provided objid not exits.
     * @throws AuthorizationException Thrown if user has no permission to use origin Item.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    final boolean loadOrigin(final String errorMessage) throws ItemNotFoundException, AuthorizationException,
        TripleStoreSystemException, WebserverSystemException, IntegritySystemException, FedoraSystemException {

        final String originObjectId = getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        boolean origin = false;

        if (originObjectId != null) {
            origin = true;
            prepareAndSetOriginItem();
            if (!checkUserRights(getOriginItem().getFullId())) {
                throw new AuthorizationException(errorMessage);
            }
        }
        else {
            resetOriginItem();
        }

        return origin;
    }

    /**
     * Obtain right version of origin Item.
     *
     * @throws ItemNotFoundException Thrown if no Item with this objid exits.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    final void prepareAndSetOriginItem() throws ItemNotFoundException, TripleStoreSystemException,
        WebserverSystemException, IntegritySystemException, FedoraSystemException {

        final String originObjectId = getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        final String originId;

        final String originVersionId = getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN_VERSION);

        if (originVersionId == null) {
            final String latestReleaseNumber =
                getTripleStoreUtility().getPropertiesElements(originObjectId,
                    Constants.RELEASE_NS_URI + Elements.ELEMENT_NUMBER);
            setOriginId(originObjectId);
            originId = originObjectId + ':' + latestReleaseNumber;
        }
        else {
            originId = originObjectId + ':' + originVersionId;
            setOriginId(originId);
        }
        setOriginItem(originId);
    }

    /**
     * Check if the user has priviliges to access the origin Item.
     *
     * @param origin Objid of the origin Item
     * @return true if user has permission on origin Item, false if access with provided userid is forbidden.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    final boolean checkUserRights(final String origin) throws WebserverSystemException {

        final List<String> id = new ArrayList<String>();
        id.add(origin);

        final List<String> ids;
        try {
            ids = this.pdp.evaluateRetrieve("item", id);
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }

        return !(ids == null || ids.isEmpty());

    }

    /**
     * Bounds an OriginItem of an SurrogateItem object to this handler. Subsequent calls to this method have no effect.
     *
     * @param id The ID of the item which should be bound to this Handler.
     * @throws ItemNotFoundException If there is no item with {@code id} in the repository.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    final void setOriginItem(final String id) throws ItemNotFoundException, TripleStoreSystemException,
        IntegritySystemException, FedoraSystemException, WebserverSystemException {
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
     * Unbounds an OriginItem of an SurrogateItem object from this handler. Subsequent calls to this method have no
     * effect.
     */
    final void resetOriginItem() {
        this.originItem = null;
        this.originId = null;
    }

    /**
     * Bounds an OriginItem of an SurrogateItem object to this handler. Subsequent calls to this method have no effect.
     *
     * @param originItem The item which should be bound to this Handler.
     */
    final void setOriginItem(final Item originItem) {
        this.originItem = originItem;

    }

    /**
     * Returns the specified component if it belongs to this item. Every service method has to call setItem() first.
     *
     * @param id ID of the component.
     * @return The requested component object or null.
     * @throws ComponentNotFoundException Thrown if Component with provided objid could not be found.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    final Component getComponent(final String id) throws ComponentNotFoundException, FedoraSystemException,
        WebserverSystemException, TripleStoreSystemException, IntegritySystemException, XmlParserSystemException {

        Component c;
        try {
            c = getOriginItem() != null ? getOriginItem().getComponent(id) : getItem().getComponent(id);
        }
        catch (final ComponentNotFoundException e) {
            c =
                getOriginItem() != null ? getOriginItem().getComponentByLocalName(id) : getItem()
                    .getComponentByLocalName(id);
        }
        return c;
    }

    /**
     * Get versions datastream of the item.
     *
     * @return The versions datastream.
     * @throws StreamNotFoundException  If a datastream can not be retrieved.
     * @throws FedoraSystemException    If Fedora reports an error.
     * @throws EncodingSystemException  In case of an encoding failure.
     * @throws WebserverSystemException Thrown in case of internal error.
     */
    final String getVersions() throws StreamNotFoundException, FedoraSystemException, EncodingSystemException {

        return getItem().getWov().toStringUTF8();
    }

    /**
     * Check if the item is locked.
     *
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws LockingException         If the item is locked and the current user is not the one who locked it.
     */
    final void checkLocked() throws LockingException, WebserverSystemException {
        if (getItem().isLocked() && !getItem().getLockOwner().equals(Utility.getCurrentUser()[0])) {
            throw new LockingException("Item + " + getItem().getId() + " is locked by " + getItem().getLockOwner()
                + '.');
        }
    }

    /**
     * Check if the requested item version is the latest version.
     *
     * @throws ReadonlyVersionException If the requested item version is not the latest one.
     */
    final void checkLatestVersion() throws ReadonlyVersionException {
        final String thisVersion = getItem().getVersionNumber();
        if (thisVersion != null && !thisVersion.equals(getItem().getLatestVersionNumber())) {
            throw new ReadonlyVersionException("Only latest version can be modified.");
        }
    }

    /**
     * Check release status of object.
     *
     * @throws WebserverSystemException   Thrown in case of an internal error.
     * @throws InvalidStatusException     Thrown if object is not in status released.
     * @throws TripleStoreSystemException If the triple store reports an error.
     */
    final void checkReleased() throws InvalidStatusException, TripleStoreSystemException, WebserverSystemException {

        // In first release, if object is once released no changes are allowed
        if (Constants.STATUS_RELEASED.equals(getItem().getStatus())) {
            // check if the version is in status released
            // FIXME check if the LATEST version is in status released. That
            // seems to be the same because all methods that call checkReleased
            // also call checkLatestVersion. But the semantic should be true
            // without another method call. (? FRS)
            if (Constants.STATUS_RELEASED.equals(getItem().getProperty(PropertyMapKeys.LATEST_VERSION_VERSION_STATUS))) {
                throw new InvalidStatusException("The object is in state '" + Constants.STATUS_RELEASED
                    + "' and can not be" + " changed.");
            }
        }
    }

    /**
     * Check status of a Context. An invalidStatusException is thrown if the Context has not the requested status.
     *
     * @param contextId The Id of the Context.
     * @param status    The expected status of the Context.
     * @throws WebserverSystemException   Thrown in case of an internal error.
     * @throws InvalidStatusException     Thrown if the Context is not in the requested status.
     * @throws TripleStoreSystemException If the triple store reports an error.
     */
    final void checkContextStatus(final String contextId, final String status) throws InvalidStatusException,
        TripleStoreSystemException, WebserverSystemException {

        if (contextId == null || status == null) {
            throw new WebserverSystemException("Context id and status must not be 'null' for check "
                + "context status.");
        }
        final String curStatus =
            getTripleStoreUtility().getPropertiesElements(contextId, TripleStoreUtility.PROP_PUBLIC_STATUS);
        if (curStatus == null || curStatus.length() == 0) {
            throw new WebserverSystemException("Can not get status of context " + contextId + '.');
        }
        // In first release, if object is once released no changes are allowed
        if (!curStatus.equals(status)) {
            throw new InvalidStatusException("The Context '" + contextId + "' is in state '" + curStatus
                + "' and not in status " + status + '.');
        }
    }

    /**
     * Check if item is tagged with status released.
     *
     * @throws InvalidStatusException     If item status is not released.
     * @throws WebserverSystemException   In case of an internal error.
     * @throws TripleStoreSystemException If the triple store reports an error.
     */
    protected void checkNotReleased() throws InvalidStatusException, WebserverSystemException {
        checkNotStatus(Constants.STATUS_RELEASED);
    }

    /**
     * Check if item is not submitted.
     *
     * @throws InvalidStatusException     If item status is not released.
     * @throws TripleStoreSystemException If the triple store reports an error.
     * @throws WebserverSystemException   In case of an internal error.
     */
    protected void checkNotSubmitted() throws InvalidStatusException, WebserverSystemException {
        checkNotStatus(Constants.STATUS_SUBMITTED);
    }

    /**
     * Check if item is not in the specified status.
     *
     * @param status A status.
     * @throws InvalidStatusException     If item status is not in requested status.
     * @throws TripleStoreSystemException If the triple store reports an error.
     * @throws WebserverSystemException   In case of an internal error.
     */
    final void checkNotStatus(final String status) throws InvalidStatusException, WebserverSystemException {

        // In first release, if object is once released no changes are allowed
        if (!status.equals(getItem().getStatus())) {
            throw new InvalidStatusException("The object is in not state '" + status + "'.");
        }
    }

    /**
     * Check if item is in the specified status.
     *
     * @param status A status.
     * @throws InvalidStatusException   If item status is in requested status.
     * @throws WebserverSystemException Thrown in case of internal error.
     */
    // TODO make this consistent to Container (proposal: checkStatusNot())
    final void checkStatus(final String status) throws InvalidStatusException, WebserverSystemException {

        // In first release, if object is once released no changes are allowed
        if (status.equals(getItem().getStatus())) {
            throw new InvalidStatusException("The object is in state '" + status + "'.");
        }
    }

    /**
     * Check if item version is tagged with status released.
     *
     * @param checkStatus The status which is to validate.
     * @throws InvalidStatusException   If item version status is not equal to the requested {@code checkStatus}.
     * @throws IntegritySystemException Thrown if version status could not be obtained.
     */
    final void checkVersionStatus(final String checkStatus) throws InvalidStatusException, IntegritySystemException {

        final String status = getItem().getVersionStatus();

        // In first release, if object is once released no changes are allowed
        if (!status.equals(checkStatus)) {
            throw new InvalidStatusException("The object is in state '" + checkStatus + "' and can not be"
                + " changed.");
        }
    }

    /**
     * Check if item version is tagged with status released.
     *
     * @param checkStatus The status which is to validate.
     * @throws InvalidStatusException   If item status is equal to the requested.
     * @throws IntegritySystemException Thrown if version status could not be obtained.
     */
    final void checkVersionStatusNot(final String checkStatus) throws InvalidStatusException, IntegritySystemException {
        final String status = getItem().getVersionStatus();
        if (status.equals(checkStatus)) {
            throw new InvalidStatusException("The object is in state '" + checkStatus + "' and can not be"
                + " changed.");
        }
    }

    /**
     * Check if the item is in state withdrawn.
     *
     * @param additionalMessage An error message prefix.
     * @throws InvalidStatusException     If the item is not in state withdrawn.
     * @throws WebserverSystemException   In case of an internal error.
     * @throws TripleStoreSystemException If the triple store reports an error.
     */
    final void checkWithdrawn(final String additionalMessage) throws InvalidStatusException,
        TripleStoreSystemException, WebserverSystemException {

        final String status = getItem().getProperty(PropertyMapKeys.PUBLIC_STATUS);
        if (status.equals(Constants.STATUS_WITHDRAWN)) {
            throw new InvalidStatusException("The object is in state '" + Constants.STATUS_WITHDRAWN + "'. "
                + additionalMessage);

        }
    }

    /**
     * Handle a Fedora Exception thrown while uploading content.
     *
     * @param url The URL.
     * @param e   The Fedora Exception.
     * @throws FileNotFoundException Thrown if access to remote resource failed.
     * @throws FedoraSystemException Thrown if Fedora reports an error.
     */
    static void handleFedoraUploadError(final String url, final Exception e) throws FileNotFoundException,
        FedoraSystemException {

        final Matcher matcherErrorGetting = PATTERN_ERROR_GETTING.matcher(e.getMessage());
        final Matcher matcherMalformedUrl = PATTERN_MALFORMED_URL.matcher(e.getMessage());

        if (matcherErrorGetting.find() || matcherMalformedUrl.find()) {
            throw new FileNotFoundException("Error getting content from " + url, e);
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            throw new FileNotFoundException(ERROR_MSG_NO_HTTP_PROTOCOL);
        }
        // TODO: Reuse HttpClient
        final HttpClient client = new DefaultHttpClient();
        try {
            final HttpUriRequest method = new HttpGet(url);
            final HttpResponse response = client.execute(method);
            final int resultCode = response.getStatusLine().getStatusCode();
            if (resultCode != HttpServletResponse.SC_OK) {
                throw new FileNotFoundException(StringUtility.format("Bad request. ", response.getStatusLine(), url));
            }

        }
        catch (final Exception e1) {
            throw new FileNotFoundException("Error getting content from " + url, e1);
        }
        finally {
            client.getConnectionManager().shutdown();
        }
        throw new FedoraSystemException(e);
    }

    /**
     * Get FoXML render.
     *
     * @return The foxml renderer.
     */
    final ItemFoXmlRendererInterface getFoxmlRenderer() {

        if (this.foxmlRenderer == null) {
            this.foxmlRenderer = new VelocityXmlItemFoXmlRenderer();
        }
        return this.foxmlRenderer;
    }

    /**
     * Get objid of origin (like user has set).
     *
     * @return objid of origin
     */
    final String getOriginId() {
        return this.originId;
    }

    /**
     * Set objid of origin (like user has set).
     *
     * @param originId objid of origin.
     */
    final void setOriginId(final String originId) {
        this.originId = originId;
    }
}
