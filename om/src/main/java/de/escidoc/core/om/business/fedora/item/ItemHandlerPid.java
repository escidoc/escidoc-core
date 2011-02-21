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

import java.io.IOException;
import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.resources.item.Component;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.PidSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.PIDSystem;
import de.escidoc.core.common.persistence.PIDSystemFactory;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Persistent Identifier relevant methods for Item.
 * 
 * @author SWA
 * 
 */
public class ItemHandlerPid extends ItemHandlerContent {

    private static final AppLogger LOGGER = new AppLogger(
        ItemHandlerPid.class.getName());

    private PIDSystemFactory pidGenFactory = null;

    private PIDSystem pidGen = null;

    /**
     * Assign persistent identifier to Content of Item.
     * 
     * @param id
     *            The object Id of item.
     * @param componentId
     *            The objectId of the Component.
     * @param taskParam
     *            The parameter for the Persistent Identifier Service as XML
     *            snippet.
     * @return The assigned PID as XML snippet.
     * @throws ItemNotFoundException
     *             Thrown if the object with id is does not exist or is no Item.
     * @throws LockingException
     *             Thrown if the Resource is locked.
     * @throws MissingMethodParameterException
     *             Thrown if a parameter is missing within
     *             <code>taskParam</code>.
     * @throws OptimisticLockingException
     *             Thrown if Item was altered in the mean time.
     * @throws InvalidStatusException
     *             Thrown if Item has the wrong status.
     * @throws XmlCorruptedException
     *             Thrown if taskParam is invalid XML.
     * @throws ComponentNotFoundException
     *             Thrown if the object with componentId does not exist or is no
     *             Component.
     * @throws ReadonlyVersionException
     *             Thrown if a provided item version id is not a latest version.
     * @throws SystemException
     *             Thrown in case of internal error.
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface
     *      #assignContentPid(java.lang.String,java.lang.String,
     *      java.lang.String)
     */
    public String assignContentPid(
        final String id, final String componentId, final String taskParam)
        throws ItemNotFoundException, LockingException,
        MissingMethodParameterException, OptimisticLockingException,
        InvalidStatusException, ComponentNotFoundException, SystemException,
        XmlCorruptedException, ReadonlyVersionException {

        setItem(id);

        // we can only update the latest version
        if (!getItem().isLatestVersion()) {
            String message =
                "Version " + getItem().getVersionNumber()
                    + " is not a latest version of the item. "
                    + "Assignment of version PID is restricted"
                    + " to the latest version.";
            LOGGER.debug(message);
            throw new ReadonlyVersionException(message);
        }

        final TaskParamHandler taskParameter =
            XmlUtility.parseTaskParam(taskParam);
        checkLocked();
        checkContentPidAssignable(componentId);

        getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(),
            "Item " + getItem().getId());

        final Component component = getItem().getComponent(componentId);

        String pid = taskParameter.getPid();
        if (pid == null) {
            // get PID from external PID System
            pid = getPid(component.getId(), taskParam);
        }
        else if (!getItem().validPidStructure(pid)) {
            throw new XmlCorruptedException("Empty pid element of taskParam.");
        }
        component.setObjectPid(pid);

        getItem().persist();

        return (prepareResponse(pid));
    }

    /**
     * Assign persistent identifier to Item object.
     * 
     * @param id
     *            The Id of the Item witch is to assign with an ObjectPid.
     * @param taskParam
     *            XML snippet with parameter for the persistent identifier
     *            system.
     * @return The assigned persistent identifier for the Item.
     * 
     * @throws ComponentNotFoundException
     *             Thrown if the Component was not found.
     * @throws ItemNotFoundException
     *             Thrown if the object with id is does not exist or is no Item.
     * @throws LockingException
     *             Thrown if the Item is locked
     * @throws MissingMethodParameterException
     *             Thrown if a parameter is missing within
     *             <code>taskParam</code>.
     * @throws OptimisticLockingException
     *             Thrown if Item was altered in the mean time.
     * @throws InvalidStatusException
     *             Thrown if Item has the wrong status.
     * @throws XmlCorruptedException
     *             Thrown if taskParam has invalid XML.
     * @throws SystemException
     *             Thrown in case of internal error.
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface
     *      #assignObjectPid(java.lang.String,java.lang.String)
     */
    public String assignObjectPid(final String id, final String taskParam)
        throws InvalidStatusException, ItemNotFoundException,
        ComponentNotFoundException, LockingException,
        MissingMethodParameterException, OptimisticLockingException,
        XmlCorruptedException, SystemException {

        setItem(id);
        final TaskParamHandler taskParameter =
            XmlUtility.parseTaskParam(taskParam);
        checkLocked();
        checkObjectPidAssignable();

        getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(),
            "Item " + getItem().getId());

        String pid = taskParameter.getPid();
        if (pid == null) {
            // get PID from external PID System
            pid = getPid(id, taskParam);
        }
        else if (!getItem().validPidStructure(pid)) {
            throw new XmlCorruptedException("Empty pid element of taskParam.");
        }

        getItem().setObjectPid(pid);
        getItem().persist();

        if (getItem().isLatestVersion()) {
            fireItemModified(getItem().getId());
        }

        return (prepareResponse(pid));
    }

    /**
     * Assign persistent identifier to a defined version of Item.
     * 
     * @param id
     *            The Id of the Item witch is to assign with a VersionPid. This
     *            id must contain the version number.
     * @param taskParam
     *            XML snippet with parameter for the persistent identifier
     *            system.
     * @return The assigned persistent identifier for the version of the Item.
     * 
     * @throws ComponentNotFoundException
     *             Thrown if the Component was not found.
     * @throws ItemNotFoundException
     *             Thrown if the Item was not found.
     * @throws LockingException
     *             Thrown if the Item is locked.
     * @throws MissingMethodParameterException
     *             Thrown if method parameter are missing.
     * @throws OptimisticLockingException
     *             Thrown in case of optimistic locking failure.
     * @throws InvalidStatusException
     *             Thrown if Item has the wrong status.
     * @throws XmlCorruptedException
     *             Thrown in case of invalid XML
     * @throws SystemException
     *             Thrown in case of internal error.
     * @throws ReadonlyVersionException
     *             Thrown if a provided item version id is not a latest version.
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface
     *      #assignVersionPid(java.lang.String,java.lang.String)
     */
    public String assignVersionPid(final String id, final String taskParam)
        throws ItemNotFoundException, LockingException,
        MissingMethodParameterException, OptimisticLockingException,
        InvalidStatusException, XmlCorruptedException, SystemException,
        ComponentNotFoundException, ReadonlyVersionException {

        setItem(id);

        // we can only update the latest version
        if (!getItem().isLatestVersion()) {
            String message =
                "Version " + getItem().getVersionNumber()
                    + " is not a latest version of the item. "
                    + "Assignment of version PID is restricted"
                    + " to the latest version.";
            LOGGER.debug(message);
            throw new ReadonlyVersionException(message);
        }

        final TaskParamHandler taskParameter =
            XmlUtility.parseTaskParam(taskParam);
        checkLocked();
        checkItemVersionPidAssignable();

        getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(),
            "Item " + getItem().getId());

        String pid = taskParameter.getPid();
        if (pid == null) {
            // get PID from external PID System
            pid = getPid(id, taskParam);
        }
        else if (!getItem().validPidStructure(pid)) {
            throw new XmlCorruptedException("Empty pid element of taskParam.");
        }

        getItem().setVersionPid(pid);
        getItem().persist();

        if (getItem().isLatestVersion()) {
            fireItemModified(getItem().getId());
        }

        return (prepareResponse(pid));
    }

    /**
     * Get Persistent Identifier from configured PID (Manager) service.
     * 
     * @param id
     *            Item ID
     * @param param
     *            XML snippet with PID Manager parameter.
     * @return Persistent Identifier
     * 
     * @throws PidSystemException
     *             Thrown if the communication with PID (Management) System
     *             fails.
     * @throws MissingMethodParameterException
     *             Thrown if necessary parameters are not part of the param XML
     *             structure.
     * @throws WebserverSystemException
     *             Thrown by assignPid().
     */
    final String getPid(final String id, final String param)
        throws PidSystemException, MissingMethodParameterException,
        WebserverSystemException {

        if (this.pidGenFactory == null) {
            pidGenFactory = PIDSystemFactory.getInstance();
        }
        if (this.pidGen == null) {
            pidGen = pidGenFactory.getPIDGenerator();
        }

        return (pidGen.assignPID(id, param));
    }

    /**
     * Check the status of persistent identifier in relation to the configured
     * behavior. This behavior is to configure by the escidoc-core.properties
     * until an existing implementation of the content model.
     * 
     * @throws InvalidStatusException
     *             Thrown if the Item has invalid status to release a Item.
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore request fails.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     * @throws IntegritySystemException
     *             If the integrity of the repository is violated.
     */
    final void checkPid() throws InvalidStatusException,
        TripleStoreSystemException, WebserverSystemException,
        IntegritySystemException {
        // this is part of a content model (which is currently missing)

        if (!releasableContentPid()) {
            throw new InvalidStatusException("ContentPid is missing! "
                + "Every content of a released item must have a contentPid.");
        }

        if (!releasableObjectPid()) {
            throw new InvalidStatusException("ObjectPid is missing! "
                + "A released item must have an objectPid.");
        }

        if (!releasableVersionPid()) {
            throw new InvalidStatusException("VersionPid is missing! "
                + "A released item must have a versionPid.");
        }
    }

    /**
     * Check if the Item has fulfilled all pre-conditions in relation to PID for
     * the release process.
     * 
     * @return true if all pre-conditions are fulfilled otherwise false.
     * @throws TripleStoreSystemException
     * @throws WebserverSystemException
     */
    private boolean releasableContentPid() throws WebserverSystemException,
        TripleStoreSystemException {
        boolean result = false;

        if (Boolean.valueOf(System
            .getProperty("cmm.Item.contentPid.releaseWithoutPid"))) {
            result = true;
        }
        else {
            // FIXME an exception is content model TOC, since we have a real
            // content model object here is a workaround
            try {
                final String curCm =
                    getItem().getProperty(
                        PropertyMapKeys.CURRENT_VERSION_CONTENT_MODEL_ID);
                final String tocCm =
                    EscidocConfiguration.getInstance().get(
                        "escidoc-core.toc.content-model");

                if (curCm.endsWith(tocCm)) {
                    result = true;
                }
                else {
                    result = true;

                    Collection<String> componentIds =
                        getItem().getComponentIds();

                    if (componentIds != null) {
                        for (String componentId : componentIds) {
                            if (!getItem()
                                .getComponent(componentId).hasObjectPid()) {
                                result = false;
                                break;
                            }
                        }
                    }
                }
            }
            catch (final ComponentNotFoundException e) {
                throw new WebserverSystemException(e);
            }
            catch (final IOException e) {
                throw new WebserverSystemException(e);
            }
            catch (final SystemException e) {
                throw new WebserverSystemException(e);
            }
        }
        return result;
    }

    /**
     * Check if the Item has fulfilled all pre-conditions in relation to PID for
     * the release process.
     * 
     * @return true if all pre-conditions are fulfilled otherwise false.
     * 
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore request fails.
     * @throws WebserverSystemException
     *             Thrown if check of existing versionPID throws Exception.
     */
    final boolean releasableObjectPid() throws TripleStoreSystemException,
        WebserverSystemException {
        if (Boolean.valueOf(System
            .getProperty("cmm.Item.objectPid.releaseWithoutPid"))) {
            return (true);
        }
        else { // objectPid is needed
               // FIXME an exception is content model TOC, since we have a real
               // content model object here is a workaround
            final String curCm =
                getItem().getProperty(
                    PropertyMapKeys.CURRENT_VERSION_CONTENT_MODEL_ID);
            String tocCm;
            try {
                tocCm =
                    EscidocConfiguration.getInstance().get(
                        "escidoc-core.toc.content-model");
            }
            catch (final IOException e) {
                throw new WebserverSystemException(e);
            }
            if (curCm.endsWith(tocCm)) {
                return true;
            }
            if (getItem().hasObjectPid()) {
                return (true);
            }
        }
        return (false);
    }

    /**
     * Check if the Item has fulfilled all pre-conditions in relation to PID for
     * the release process.
     * 
     * @return true if all pre-conditions are fulfilled otherwise false.
     * @throws WebserverSystemException
     *             Thrown if check of existing versionPID throws Exception.
     * @throws IntegritySystemException
     *             If the integrity of the repository is violated.
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore request failed.
     */
    final boolean releasableVersionPid() throws WebserverSystemException,
        IntegritySystemException, TripleStoreSystemException {
        if (Boolean.valueOf(System
            .getProperty("cmm.Item.versionPid.releaseWithoutPid"))) {
            return (true);
        }
        else {
            // FIXME an exception is content model TOC, since we have a real
            // content model object here is a workaround
            final String curCm =
                getItem().getProperty(
                    PropertyMapKeys.CURRENT_VERSION_CONTENT_MODEL_ID);
            String tocCm;
            try {
                tocCm =
                    EscidocConfiguration.getInstance().get(
                        "escidoc-core.toc.content-model");
            }
            catch (final IOException e) {
                throw new WebserverSystemException(e);
            }
            if (curCm.endsWith(tocCm)) {
                return true;
            }
        }

        // versionPid is needed
        return (getItem().hasVersionPid());
    }

    /**
     * Check if item fulfills all requirements for PID assignment. - status
     * released - not already assigned pid
     * 
     * @param componentId
     *            The objectId of the Component.
     * 
     * @throws InvalidStatusException
     *             If item status is not released
     * @throws SystemException
     *             Thrown if instance of configuration throws exception.
     * @throws ComponentNotFoundException
     *             Thrown if the component with the given componentId could not
     *             be found
     */
    private void checkContentPidAssignable(final String componentId)
        throws InvalidStatusException, SystemException,
        ComponentNotFoundException {

        checkStatus(Constants.STATUS_WITHDRAWN);
        checkVersionStatusNot(Constants.STATUS_WITHDRAWN);

        Boolean setPidAfterRelease;
        Boolean setPidBeforeRelease;
        try {
            setPidAfterRelease =
                Boolean.valueOf(EscidocConfiguration.getInstance().get(
                    "cmm.Item.contentPid.setPidAfterRelease"));
            setPidBeforeRelease =
                Boolean.valueOf(EscidocConfiguration.getInstance().get(
                    "cmm.Item.contentPid.setPidBeforeRelease"));
        }
        catch (final Exception e) {
            LOGGER.warn(e);
            throw new SystemException(e);
        }

        if (!setPidBeforeRelease) {
            checkVersionStatus(Constants.STATUS_RELEASED);
        }
        if (!setPidAfterRelease) {
            checkVersionStatusNot(Constants.STATUS_RELEASED);
        }

        final String pid = getItem().getComponent(componentId).getObjectPid();
        if ((pid != null) && (pid.length() > 0)) {
            final String msg =
                "This object version (" + getItem().getVersionId()
                    + ") is already assigned with PID '" + pid
                    + "' and can not be reassigned.";
            LOGGER.debug(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * Check if item fulfills all requirements for PID assignment. - status
     * released - not already assigned pid
     * 
     * @throws InvalidStatusException
     *             If item status is not released
     * @throws SystemException
     *             Thrown if instance of configuration throws exception.
     */
    private void checkItemVersionPidAssignable() throws InvalidStatusException,
        SystemException {

        checkStatus(Constants.STATUS_WITHDRAWN);
        checkVersionStatusNot(Constants.STATUS_WITHDRAWN);

        Boolean setPidAfterRelease;
        Boolean setPidBeforeRelease;
        try {
            setPidAfterRelease =
                Boolean.valueOf(EscidocConfiguration.getInstance().get(
                    "cmm.Item.versionPid.setPidAfterRelease"));
            setPidBeforeRelease =
                Boolean.valueOf(EscidocConfiguration.getInstance().get(
                    "cmm.Item.versionPid.setPidBeforeRelease"));
        }
        catch (final Exception e) {
            LOGGER.warn(e);
            throw new SystemException(e);
        }

        if (!setPidBeforeRelease) {
            checkVersionStatus(Constants.STATUS_RELEASED);
        }
        if (!setPidAfterRelease) {
            checkVersionStatusNot(Constants.STATUS_RELEASED);
        }

        final String pid = getItem().getVersionPid();
        if ((pid != null) && (pid.length() > 0)) {
            final String msg =
                "This object version (" + getItem().getVersionId()
                    + ") is already assigned with PID '" + pid
                    + "' and can not be reassigned.";
            LOGGER.debug(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * Check if item fulfills all requirements for PID assignment. - status
     * released - not already assigned pid
     * 
     * @throws InvalidStatusException
     *             If item status is not released
     * @throws SystemException
     *             Thrown if instance of configuration throws exception.
     */
    final void checkObjectPidAssignable() throws InvalidStatusException,
        SystemException {

        checkStatus(Constants.STATUS_WITHDRAWN);

        Boolean setPidAfterRelease;
        Boolean setPidBeforeRelease;
        try {
            setPidAfterRelease =
                Boolean.valueOf(EscidocConfiguration.getInstance().get(
                    "cmm.Item.objectPid.setPidAfterRelease"));
            setPidBeforeRelease =
                Boolean.valueOf(EscidocConfiguration.getInstance().get(
                    "cmm.Item.objectPid.setPidBeforeRelease"));
        }
        catch (final Exception e) {
            LOGGER.warn(e);
            throw new SystemException(e);
        }

        if (!setPidBeforeRelease) {
            checkNotStatus(Constants.STATUS_RELEASED);
        }
        if (!setPidAfterRelease) {
            checkStatus(Constants.STATUS_RELEASED);
        }

        final String pid = getItem().getObjectPid();
        if (pid != null) {
            final String msg =
                "This object (" + getItem().getFullId()
                    + ") is already assigned with PID '" + pid
                    + "' and can not be reassigned.";
            LOGGER.debug(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * Check if no object PID is assigned to item. (called floating PID before)
     * 
     * @throws InvalidStatusException
     *             If PID is assigned and part of the item.
     * @throws TripleStoreSystemException
     *             If the triple store reports an error.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    protected void checkNoObjectPidAssigned() throws InvalidStatusException,
        TripleStoreSystemException, WebserverSystemException {

        if (getItem().hasObjectPid()) {
            final String msg =
                "The object is already assigned with a objectPID "
                    + "and can not be reassigned.";
            LOGGER.debug(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * Prepare the assignment response message.
     * 
     * Preconditions: The TripleStore must be in sync with the repository.
     * 
     * @param pid
     *            The new assigned PID.
     * @return response message
     * 
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     * @throws TripleStoreSystemException
     *             Thrown in case of TripleStore error.
     * 
     */
    private String prepareResponse(final String pid)
        throws TripleStoreSystemException, WebserverSystemException {

        String lmd;
        try {
            lmd = getItem().getLastModificationDate();
        }
        catch (FedoraSystemException e) {
            throw new WebserverSystemException(e);
        }

        String result;
        try {
            DateTime t = new DateTime(lmd, DateTimeZone.UTC);
            result =
                getUtility().prepareReturnXml(t, "<pid>" + pid + "</pid>\n");
        }
        catch (SystemException e) {
            LOGGER.error(e);
            throw new WebserverSystemException(e);
        }
        return result;
    }
}
