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
package de.escidoc.core.om.business.fedora.container;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.PidSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.PIDSystem;
import de.escidoc.core.common.persistence.PIDSystemFactory;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;

/**
 * Persistent Identifier relevant methods for Container.
 * 
 * @author SWA
 * 
 */
public class ContainerHandlerPid extends ContainerHandlerCreate {

    private static final AppLogger log = new AppLogger(
        ContainerHandlerPid.class.getName());

    private PIDSystemFactory pidGenFactory = null;

    private PIDSystem pidGen = null;

    /*
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.om.business.interfaces.ContainerHandlerInterface
     * #assignObjectPid(java.lang.String, java.lang.String)
     */
    public String assignObjectPid(final String id, final String taskParam)
        throws InvalidStatusException, ContainerNotFoundException,
        LockingException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, XmlCorruptedException {

        setContainer(id);
        final TaskParamHandler taskParameter =
            XmlUtility.parseTaskParam(taskParam);
        checkLocked();
        checkStatusNot(Constants.STATUS_WITHDRAWN);
        checkNoObjectPidAssigned();

        getUtility().checkOptimisticLockingCriteria(
            getContainer().getLastModificationDate(),
            taskParameter.getLastModificationDate(), "Container " + id);

        String pid = taskParameter.getPid();
        if (pid == null) {
            // get PID from external PID System
            pid = getPid(id, taskParam);
        }
        else if (!getContainer().validPidStructure(pid)) {
            throw new XmlCorruptedException("Empty pid element of taskParam.");
        }

        getContainer().setObjectPid(pid);
        getContainer().persist();

        // recache
        fireContainerModified(getContainer().getId());

        return (prepareResponse(pid));
    }

    /**
     * Assign a persistent identifier to a version of container.
     * 
     * @param id
     *            Object identifier of container
     * @param taskParam
     *            XML snippet with PID parameter.
     * @return The assigned PID within XML structure.
     * @throws ContainerNotFoundException
     *             Thrown if the Container with the id could not be found.
     * @throws LockingException
     *             Thrown if the Container is locked.
     * @throws MissingMethodParameterException
     *             Thrown if taskParam is invalid.
     * @throws InvalidStatusException
     *             Thrown if Container version status is invalied.
     * @throws OptimisticLockingException
     *             Thrown if Container was altered through third instance mean
     *             while.
     * @throws XmlCorruptedException
     *             Thrown if taskParam is invalid.
     * @throws ReadonlyVersionException
     *             Thrown if a provided container version id is not a latest
     *             version.
     * @throws SystemException
     *             Thrown if internal error occures.
     * 
     */
    public String assignVersionPid(final String id, final String taskParam)
        throws ContainerNotFoundException, LockingException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException,
        XmlCorruptedException, ReadonlyVersionException {

        setContainer(id);
        if (!getContainer().isLatestVersion()) {
            String message =
                "The version " + getContainer().getVersionNumber()
                    + " is not a latest version of the container. "
                    + " Assignment of version PID is restricted to "
                    + "the latest version.";
            log.debug(message);
            throw new ReadonlyVersionException(message);
        }
        // check Container status/values
        checkLocked();
        checkStatusNot(Constants.STATUS_WITHDRAWN);
        checkVersionStatusNot(Constants.STATUS_WITHDRAWN);
        // FIXME check the escidoc.properties for release test
        checkVersionStatusNot(Constants.STATUS_RELEASED);
        checkVersionPidAssignable(id);

        final TaskParamHandler taskParameter =
            XmlUtility.parseTaskParam(taskParam);
        getUtility().checkOptimisticLockingCriteria(
            getContainer().getLastModificationDate(),
            taskParameter.getLastModificationDate(),
            "Container " + getContainer().getId());

        String pid = taskParameter.getPid();
        if (pid == null) {
            // get PID from external PID System
            pid = getPid(id, taskParam);
        }
        else if (!getContainer().validPidStructure(pid)) {
            throw new XmlCorruptedException("Empty pid element of taskParam.");
        }

        getContainer().setVersionPid(pid);
        getContainer().persist();

        fireContainerModified(getContainer().getId());

        return (prepareResponse(pid));
    }

    /**
     * Get Persistent Identifier from configured PID (Manager) service.
     * 
     * @param id
     *            Container ID
     * @param param
     *            XML snippet with PID Manager parameter.
     * @return Persistent Identifier
     * @throws PidSystemException
     *             Thrown if the communication with PID (Management) System
     *             fails.
     * @throws MissingMethodParameterException
     *             Thrown if necessary parameter not part of the param XML
     *             structure.
     * @throws WebserverSystemException
     *             Thrown by assignPid().
     */
    public String getPid(final String id, final String param)
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
     *             TODO
     */
    protected void checkPid() throws InvalidStatusException,
        TripleStoreSystemException, WebserverSystemException {
        // this is part of a content model (which is currently missing)

        if (!releasableObjectPid()) {
            throw new InvalidStatusException("ObjectPid is missing! "
                + "A released Container must have an objectPid.");
        }

        if (!releasableVersionPid()) {
            throw new InvalidStatusException("VersionPid is missing! "
                + "A released Container must have a versionPid.");
        }
    }

    /**
     * Check if container fulfills all requirements for PID assignment. - status
     * released - not already assigned pid
     * 
     * @param versionId
     *            The version ID of the container.
     * 
     * @throws InvalidStatusException
     *             If item status is not released
     * @throws TripleStoreSystemException
     */
    protected void checkVersionPidAssignable(final String versionId)
        throws InvalidStatusException {

        // String status = null;
        String pid;
        DocumentBuilder db;
        Document xmlDom;

        final XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            xmlDom =
                db.parse(new ByteArrayInputStream(getContainer()
                    .getWov().getStream()));

            // get status from version-history/version[@objid='id']/pid
            final String xpathPid =
                "/version-history/version[@objid='" + versionId + "']/pid";
            pid = xpath.evaluate(xpathPid, xmlDom);
        }
        catch (final Exception e) {
            log.error(e.toString());
            throw new InvalidStatusException(e);
        }

        // In first release, if object is once released no changes are allowed
        // if (!status.equals(Constants.STATUS_RELEASED)) {
        // String msg =
        // "The object is not in state '" + Constants.STATUS_RELEASED
        // + "' and can not be" + " changed.";
        // log.error(msg);
        // throw new InvalidStatusException(msg);
        // }
        // FIXME pid structure check ?
        if (pid.length() > 0) {
            final String msg =
                "This object version is already assigned with PID '" + pid
                    + "' and can not be reassigned.";
            log.info(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * Check if the Container has fulfilled all pre-conditions in relation to
     * PID for the release process.
     * 
     * @return true if all pre-conditions are fulfilled otherwise false.
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore request fails.
     * @throws WebserverSystemException
     *             Thrown if check of existing versionPID throws Exception.
     */
    protected boolean releasableObjectPid() throws TripleStoreSystemException,
        WebserverSystemException {
        if (Boolean.valueOf(System
            .getProperty("cmm.Container.objectPid.releaseWithoutPid"))) {
            return (true);
        }
        else { // objectPid is needed
            if (getContainer().hasObjectPid()) {
                return (true);
            }
        }
        return (false);
    }

    /**
     * Check if the Container has fulfilled all pre-conditions in relation to
     * PID for the release process.
     * 
     * @return true if all pre-conditions are fulfilled otherwise false.
     * @throws WebserverSystemException
     *             Thrown if check of existing versionPID throws Exception.
     */
    protected boolean releasableVersionPid() throws WebserverSystemException {
        if (Boolean.valueOf(System
            .getProperty("cmm.Container.versionPid.releaseWithoutPid"))) {
            return (true);
        }

        // versionPid is needed
        return (getContainer().hasVersionPid());
    }

    /**
     * Check pre-conditions to assign an ObjectPID.
     * 
     * @param versionId
     *            ID of container object
     * @throws InvalidStatusException
     *             thrown if assignment is forbidden
     * @throws SystemException
     *             thrown in case of error
     */
    protected void checkObjectPidAssignable(final String versionId)
        throws InvalidStatusException, SystemException {

        final String pid =
            getTripleStoreUtility().getPropertiesElements(
                getContainer().getId(),
                TripleStoreUtility.PROP_LATEST_RELEASE_PID);

        if ((pid != null) && (pid.length() > 0)) {
            final String msg =
                "The object is already assigned with PID '" + pid
                    + "' and can not be reassigned.";
            log.info(msg);
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
            lmd = getContainer().getLastModificationDate();
        }
        catch (FedoraSystemException e) {
            throw new WebserverSystemException(e);
        }

        String result;
        try {
            DateTime t = new DateTime(lmd, DateTimeZone.UTC);
            result = getUtility().prepareReturnXml(t, "<pid>" + pid + "</pid>\n");
        }
        catch (SystemException e) {
            throw new WebserverSystemException(e);
        }
        return result;
    }

}
