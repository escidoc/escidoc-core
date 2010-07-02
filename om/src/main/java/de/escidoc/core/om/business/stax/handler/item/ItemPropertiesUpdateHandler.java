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
/**
 * 
 */
package de.escidoc.core.om.business.stax.handler.item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.Item;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * @author FRS
 * 
 */
public class ItemPropertiesUpdateHandler extends DefaultHandler {

    private Map<String, String> properties;

    private StaxParser parser = null;

    private String propertiesPath = null;

    private String itemId = null;

    private final Object lastVersionNumber;

    private final Object lastModificationDate;

    private final Object lastValidStatus;

    private final Object lastStatus;

    private final Object lastRevisionNumber;

    private final Object lastRevisionDate;

    // private boolean lockOwnerRequired = false;
    //
    // private boolean lockOwnerOccured = false;
    //
    // private String lockStatus = null;
    //
    // private boolean latestRevisionRequired = false;
    //
    // private boolean latestRevisionOccured = false;

    private List expected = null;

    // names of elements that must occur
    private static final String[] expectedElements = null;

    // { "creation-date",
    // "context", "content-type", "created-by", "lock-status", "status",
    // "version", "latest-version" };

    private static AppLogger log =
        new AppLogger(ItemPropertiesUpdateHandler.class.getName());

    public ItemPropertiesUpdateHandler(Item item, String propertiesPath,
        StaxParser parser) throws SystemException {
        this.itemId = item.getId();
        this.propertiesPath = propertiesPath;
        this.parser = parser;
        this.properties = new HashMap<String, String>();
        if (expectedElements != null) {
            this.expected = new Vector(Arrays.asList(expectedElements));
        }

        // TODO check this; was local var
        properties = item.getResourceProperties();

        this.lastVersionNumber =
            properties.get(TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);
        this.lastModificationDate =
            properties.get(TripleStoreUtility.PROP_LATEST_VERSION_DATE);
        this.lastValidStatus =
            properties.get(TripleStoreUtility.PROP_LATEST_VERSION_VALID_STATUS);
        this.lastStatus =
            properties.get(TripleStoreUtility.PROP_LATEST_VERSION_STATUS);

        // if (versionData.containsKey("revisionNo")) {
        this.lastRevisionNumber =
            properties.get(TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER);
        this.lastRevisionDate =
            properties.get(TripleStoreUtility.PROP_LATEST_RELEASE_DATE);

        /*
         * read-only elements are ignored now // pid is expected if set if
         * (properties.containsKey(TripleStoreUtility.PROP_PID) &&
         * properties.get(TripleStoreUtility.PROP_PID) != null && ((String)
         * properties.get(TripleStoreUtility.PROP_PID)).length() > 0) {
         * expected.add("pid"); } // latest-release.pid is expected if set if
         * (properties.containsKey(TripleStoreUtility.PROP_LATEST_RELEASE_PID) &&
         * properties.get(TripleStoreUtility.PROP_LATEST_RELEASE_PID) != null &&
         * ((String) properties
         * .get(TripleStoreUtility.PROP_LATEST_RELEASE_PID)).length() > 0) {
         * expected.add("latest-release.pid"); // current-version.pid is
         * expected if latest-release.pid is set and // current-version is
         * latest-release (this is sufficient because we // are in update which
         * is only allowed on latest version) if
         * (properties.get(TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER)
         * .equals( properties
         * .get(TripleStoreUtility.PROP_LATEST_VERSION_NUMBER))) {
         * expected.add("current-version.pid"); } }
         */
    }

    /*
     * read-only elements are ignored now
     */
    // public EndElement endElement(EndElement element) throws Exception {
    // String curPath = parser.getCurPath();
    // if (curPath.equals(propertiesPath)) {
    // if (!expected.isEmpty()) {
    // throw new ReadonlyElementViolationException("One of "
    // + expected + " is expected on update item.");
    // }
    // if (lockOwnerRequired) {
    // if (!lockOwnerOccured) {
    // throw new ReadonlyElementViolationException(
    // "Item properties has invalid lock-status or no lock-owner.");
    // }
    // }
    // else if (lockOwnerOccured) {
    // throw new ReadonlyElementViolationException(
    // "Item properties has invalid lock-status or must not have a
    // lock-owner.");
    // }
    // if (latestRevisionRequired) {
    // if (!latestRevisionOccured) {
    // throw new ReadonlyElementViolationException(
    // "Item properties has no latest-release.");
    // }
    // }
    // else if (latestRevisionOccured) {
    // throw new ReadonlyElementViolationException(
    // "Item properties has latest-release.");
    // }
    // }
    // return element;
    // }
    @Override
    public StartElement startElement(StartElement element) {
        String curPath = parser.getCurPath();
        if (curPath.startsWith(propertiesPath)) {
            // do my job

            // try {
            // if (curPath.equals(propertiesPath)) {
            // }
            // else if (curPath.equals(propertiesPath + "/context")) {
            // expected.remove("context");
            //
            // try {
            // String title = element.getAttribute(
            // Constants.XLINK_URI, "title").getValue();
            // if (!title
            // .equals(TripleStoreUtility.PROP_CONTEXT_TITLE)) {
            // if (log.isWarnEnabled()) {
            // String msg = "Item ("
            // + itemId
            // + ") properties.context.@title does not match.";
            // log.warn(msg);
            // }
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String linkType = element.getAttribute(
            // Constants.XLINK_URI, "type").getValue();
            // // LAX
            // // if (!"simple".equals(linkType)) {
            // // throw new ReadonlyAttributeViolationException(
            // // "Item properties context has invalid xlink:type.");
            // // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String href = element.getAttribute(Constants.XLINK_URI,
            // "href").getValue();
            // if (!href.equals(Constants.CONTEXT_URL_BASE
            // + properties
            // .get(TripleStoreUtility.PROP_CONTEXT_ID))) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties context has invalid xlink:href.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String objid = element.getAttribute(null, "objid")
            // .getValue();
            // if (!properties.get(TripleStoreUtility.PROP_CONTEXT_ID)
            // .equals(objid)) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties context has invalid objid.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/content-type")) {
            // expected.remove("content-type");
            //
            // try {
            // String title = element.getAttribute(
            // Constants.XLINK_URI, "title").getValue();
            // if (!title
            // .equals(TripleStoreUtility.PROP_CONTENT_TYPE_TITLE)) {
            // if (log.isWarnEnabled()) {
            // String msg = "Item ("
            // + itemId
            // + ") properties.content-type.@title does not match.";
            // log.warn(msg);
            // }
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String linkType = element.getAttribute(
            // Constants.XLINK_URI, "type").getValue();
            // // LAX
            // // if (!"simple".equals(linkType)) {
            // // throw new ReadonlyAttributeViolationException(
            // // "Item properties content-type has invalid
            // // xlink:type.");
            // // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String href = element.getAttribute(Constants.XLINK_URI,
            // "href").getValue();
            // if (!href.equals(Constants.CONTENT_TYPE_URL_BASE
            // + properties
            // .get(TripleStoreUtility.PROP_CONTENT_TYPE_ID))) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties content-type has invalid xlink:href.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String objid = element.getAttribute(null, "objid")
            // .getValue();
            // if (!properties.get(
            // TripleStoreUtility.PROP_CONTENT_TYPE_ID).equals(
            // objid)) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties content-type has invalid objid.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/creator")) {
            // expected.remove("creator");
            //
            // try {
            // String title = element.getAttribute(
            // Constants.XLINK_URI, "title").getValue();
            // if (!title
            // .equals(TripleStoreUtility.PROP_CREATOR_TITLE)) {
            // if (log.isWarnEnabled()) {
            // String msg = "Item ("
            // + itemId
            // + ") properties.creator.@title does not match.";
            // log.warn(msg);
            // }
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String linkType = element.getAttribute(
            // Constants.XLINK_URI, "type").getValue();
            // // LAX
            // // if (!"simple".equals(linkType)) {
            // // throw new ReadonlyAttributeViolationException(
            // // "Item properties creator has invalid xlink:type.");
            // // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String href = element.getAttribute(Constants.XLINK_URI,
            // "href").getValue();
            // if (!href.equals("/aa/user-account/"
            // + properties
            // .get(TripleStoreUtility.PROP_CREATOR_ID))) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties creator has invalid xlink:href.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String objid = element.getAttribute(null, "objid")
            // .getValue();
            // if (!properties.get(TripleStoreUtility.PROP_CREATOR_ID)
            // .equals(objid)) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties creator has invalid objid.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/lock-owner")) {
            // expected.remove("lock-owner");
            //
            // try {
            // String title = element.getAttribute(
            // Constants.XLINK_URI, "title").getValue();
            // if (!title.equals(LockHandler.getInstance()
            // .getLockOwnerTitle(itemId))) {
            // if (log.isWarnEnabled()) {
            // String msg = "Item ("
            // + itemId
            // + ") properties.lock-owner.@title does not match.";
            // log.warn(msg);
            // }
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String linkType = element.getAttribute(
            // Constants.XLINK_URI, "type").getValue();
            // // LAX
            // // if (!"simple".equals(linkType)) {
            // // throw new ReadonlyAttributeViolationException(
            // // "Item properties lock-owner has invalid
            // // xlink:type.");
            // // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String href = element.getAttribute(Constants.XLINK_URI,
            // "href").getValue();
            // if (!href.equals("/aa/user-account/"
            // + LockHandler.getInstance().getLockOwner(itemId))) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties lock-owner has invalid xlink:href.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String objid = element.getAttribute(null, "objid")
            // .getValue();
            // if (!LockHandler.getInstance().getLockOwner(itemId)
            // .equals(objid)) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties lock-owner has invalid objid.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/current-version")) {
            // expected.remove("current-version");
            //
            // try {
            // String title = element.getAttribute(
            // Constants.XLINK_URI, "title").getValue();
            // // TODO check title?
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String linkType = element.getAttribute(
            // Constants.XLINK_URI, "type").getValue();
            // // LAX
            // // if (!"simple".equals(linkType)) {
            // // throw new ReadonlyAttributeViolationException(
            // // "Item properties current-version has invalid
            // // xlink:type.");
            // // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // String currentVersionObjid = itemId
            // + ":"
            // + properties
            // .get(TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);
            // try {
            // String href = element.getAttribute(Constants.XLINK_URI,
            // "href").getValue();
            // if (!href.equals(Constants.ITEM_URL_BASE
            // + currentVersionObjid)) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties current-version has invalid xlink:href.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String objid = element.getAttribute(null, "objid")
            // .getValue();
            // if (!objid.equals(currentVersionObjid)) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties current-version has invalid objid.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            // }
            // else if (curPath.equals(propertiesPath
            // + "/current-version/modified-by")) {
            // // expected.remove("current-version.modified-by");
            //
            // // FIXME: This does not work with inetrface changes after
            // // AA/UM merge. UserAccountHAndler.retrieve() must be called
            // // to get the user account XML representation and the name
            // // has to be extracted from the xml data.
            // // Besided this, the check does not work, as userName is
            // // allways null.
            // // Therefore, the check has been disabled. If it is needed,
            // // it has to be uncommented and fixed.
            // // try {
            // // String title = element.getAttribute(
            // // Constants.XLINK_URI, "title").getValue();
            // // String userName = null;
            // // try {
            // // BeanLocator
            // // .locateUserAccountHandler()
            // // .retrieveUserAccount(
            // // (String) properties
            // // .get(TripleStoreUtility.PROP_LATEST_VERSION_USER))
            // // .getName();
            // // }
            // // catch (Exception e) {
            // // if (log.isWarnEnabled()) {
            // // String msg = "Can not retrieve
            // // properties.current-version.modified-by.@title from user
            // // id.";
            // // log.warn(msg, e);
            // // }
            // // }
            // // if (!title.equals(userName)) {
            // // if (log.isWarnEnabled()) {
            // // String msg = "Item ("
            // // + itemId
            // // + ") properties.current-version.modified-by.@title does
            // // not match.";
            // // log.warn(msg);
            // // }
            // // }
            // // }
            // // catch (NoSuchAttributeException e) {
            // // // LAX
            // // }
            //
            // try {
            // String linkType = element.getAttribute(
            // Constants.XLINK_URI, "type").getValue();
            // // LAX
            // // if (!"simple".equals(linkType)) {
            // // throw new ReadonlyAttributeViolationException(
            // // "Item properties modified-by has invalid
            // // xlink:type.");
            // // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String href = element.getAttribute(Constants.XLINK_URI,
            // "href").getValue();
            // if (!href
            // .equals("/aa/user-account/"
            // + properties
            // .get(TripleStoreUtility.PROP_LATEST_VERSION_USER))) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties modified-by has invalid xlink:href.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String objid = element.getAttribute(null, "objid")
            // .getValue();
            // if (!properties.get(
            // TripleStoreUtility.PROP_LATEST_VERSION_USER)
            // .equals(objid)) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties modified-by has invalid objid.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/latest-version")) {
            // expected.remove("latest-version");
            //
            // try {
            // String title = element.getAttribute(
            // Constants.XLINK_URI, "title").getValue();
            // // TODO check title?
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String linkType = element.getAttribute(
            // Constants.XLINK_URI, "type").getValue();
            // // LAX
            // // if (!"simple".equals(linkType)) {
            // // throw new ReadonlyAttributeViolationException(
            // // "Item properties latest-version has invalid
            // // xlink:type.");
            // // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // String latestVersionObjid = itemId
            // + ":"
            // + properties
            // .get(TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);
            //
            // try {
            // String href = element.getAttribute(Constants.XLINK_URI,
            // "href").getValue();
            // if (!href.equals(Constants.ITEM_URL_BASE
            // + latestVersionObjid)) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties latest-version has invalid xlink:href.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String objid = element.getAttribute(null, "objid")
            // .getValue();
            // if (!objid.equals(latestVersionObjid)) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties latest-version has invalid objid.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/latest-release")) {
            // expected.remove("latest-release");
            //
            // try {
            // String title = element.getAttribute(
            // Constants.XLINK_URI, "title").getValue();
            // // TODO check title?
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String linkType = element.getAttribute(
            // Constants.XLINK_URI, "type").getValue();
            // // LAX
            // // if (!"simple".equals(linkType)) {
            // // throw new ReadonlyAttributeViolationException(
            // // "Item properties latest-release has invalid
            // // xlink:type.");
            // // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // String latestReleaseObjid = itemId
            // + ":"
            // + properties
            // .get(TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER);
            //
            // try {
            // String href = element.getAttribute(Constants.XLINK_URI,
            // "href").getValue();
            // if (!href.equals(Constants.ITEM_URL_BASE
            // + latestReleaseObjid)) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties latest-release has invalid xlink:href.");
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // String objid = element.getAttribute(null, "objid")
            // .getValue();
            //
            // if (!objid.equals(latestReleaseObjid)) {
            // throw new ReadonlyAttributeViolationException(
            // "Item properties latest-release has invalid objid.");
            // }
            // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX all attributes
            // // throw new InvalidContentException("Missing attribute in "
            // // + curPath + ".", e);
            // }
        }

        return element;
    }

    public void setParser(StaxParser parser) {
        if (parser != null) {
            throw new UnsupportedOperationException(
                "Handler is already initialised.");
        }
        this.parser = parser;
    }

    public void setPropertiesPath(final String propertiesPath) {
        if (propertiesPath != null) {
            throw new UnsupportedOperationException(
                "Handler is already initialised.");
        }
        this.propertiesPath = propertiesPath;
    }

    public void setItemId(final String itemId) {
        if (itemId != null) {
            throw new UnsupportedOperationException(
                "Handler is already initialised.");
        }
        this.itemId = itemId;
    }

    @Override
    public String characters(String data, StartElement element) {
        String curPath = parser.getCurPath();

        if (curPath.startsWith(propertiesPath)) {
            // do my job
            // if (curPath.equals(propertiesPath + "/creation-date")) {
            // expected.remove("creation-date");
            // String curVal = TripleStoreUtility.getInstance()
            // .getCreationDate(itemId);
            // if (!data.equals(curVal)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid creation-date.");
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/lock-status")) {
            // expected.remove("lock-status");
            // if (LockHandler.getInstance().isLocked(itemId)) {
            // if (!data.equals(Constants.STATUS_LOCKED)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid lock-status.");
            // }
            // expected.add("lock-date");
            // expected.add("lock-owner");
            // }
            // else if (!data.equals(Constants.STATUS_UNLOCKED)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid lock-status.");
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/lock-date")) {
            // expected.remove("lock-date");
            // if (!data.equals(LockHandler.getInstance().getLockDate(itemId)))
            // {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid lock-date.");
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/status")) {
            // expected.remove("status");
            // if (!properties.get(TripleStoreUtility.PROP_STATUS)
            // .equals(data)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid status.");
            // }
            // if (data.equals(Constants.STATUS_WITHDRAWN)) {
            // expected.add("withdrawal-date");
            // expected.add("withdrawal-comment");
            // }
            // else if (data.equals(Constants.STATUS_RELEASED)) {
            // expected.add("latest-release");
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/pid")) {
            // expected.remove("pid");
            // if (!data.equals(properties.get(TripleStoreUtility.PROP_PID))) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid pid.");
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/withdrawal-date")) {
            // expected.remove("withdrawal-date");
            // throw new UnsupportedOperationException(
            // "Can not handle withdrawal-date in update.");
            // }
            // else if (curPath.equals(propertiesPath + "/withdrawal-comment"))
            // {
            // expected.remove("withdrawal-comment");
            // throw new UnsupportedOperationException(
            // "Can not handle withdrawal-comment in update.");
            // }
            // // current-version
            // else if (curPath.startsWith(propertiesPath + "/current-version"))
            // {
            // // elements inside current-version are required by xml-schema,
            // // except pid
            // if (curPath.equals(propertiesPath + "/current-version/number")) {
            // if (!data.equals(lastVersionNumber)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid "
            // + "current-version.number" + ".");
            // }
            // }
            // else if (curPath.equals(propertiesPath
            // + "/current-version/date")) {
            // // TODO check value
            // if (!data.equals(lastModificationDate)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid "
            // + "current-version.date" + ".");
            // }
            // }
            // else if (curPath.equals(propertiesPath
            // + "/current-version/version-status")) {
            // // check value
            // if (!data.equals(lastStatus)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid "
            // + "current-version.version-status" + ".");
            // }
            // }
            // else if (curPath
            // .equals(propertiesPath + "/current-version/pid")) {
            // expected.remove("current-version.pid");
            // if (!data.equals(properties
            // .get(TripleStoreUtility.PROP_LATEST_RELEASE_PID))) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid "
            // + "current-version.pid" + ".");
            // }
            // }
            // else if (curPath.equals(propertiesPath
            // + "/current-version/valid-status")) {
            // // check value
            // if (!data.equals(lastValidStatus)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid "
            // + "current-version.valid-status" + ".");
            // }
            // }
            // }
            // // latest-version
            // else if (curPath.startsWith(propertiesPath + "/latest-version"))
            // {
            // // elements inside latest-version are required by xml-schema,
            // if (curPath.equals(propertiesPath + "/latest-version/number")) {
            // // check value
            // if (!data.equals(lastVersionNumber)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid "
            // + "latest-version.number" + ".");
            // }
            // }
            // else if (curPath
            // .equals(propertiesPath + "/latest-version/date")) {
            // // check value
            // if (!data.equals(lastModificationDate)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid "
            // + "latest-version.date" + ".");
            // }
            // }
            // }
            // else if (curPath.startsWith(propertiesPath + "/latest-release"))
            // {
            // // elements inside latest-release are required by xml-schema
            // // except pid
            // if (curPath.equals(propertiesPath + "/latest-release/number")) {
            // // check value
            // if (!data.equals(lastRevisionNumber)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid "
            // + "latest-release.number" + ".");
            // }
            // }
            // else if (curPath
            // .equals(propertiesPath + "/latest-release/date")) {
            // // check value
            // if (!data.equals(lastRevisionDate)) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid "
            // + "latest-release.date" + ".");
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/latest-release/pid"))
            // {
            // expected.remove("latest-release.pid");
            // if (!data.equals(properties
            // .get(TripleStoreUtility.PROP_LATEST_RELEASE_PID))) {
            // throw new ReadonlyElementViolationException(
            // "Item properties has invalid "
            // + "latest-release.pid" + ".");
            // }
            // }
            // }
        }
        return data;
    }

    /**
     * @deprecated
     * @return
     */
    @Deprecated
    public HashMap<String, String> getProperties() {
        // this handler do not check every element/attribute any longer and has
        // not the whole properties
        throw new UnsupportedOperationException(
            "Can not return whole properties.");
        // return properties;
    }
}
