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
package de.escidoc.core.om.business.stax.handler.container;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.directory.NoSuchAttributeException;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.LockHandler;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class ContainerPropertiesUpdateHandler extends DefaultHandler {
    private HashMap properties = new HashMap();

    private StaxParser parser = null;

    private String propertiesPath = null;

    private String containerId = null;

    private List expected = null;

    // version properties
    private String latestVersionNumber;

    private String latestVersionDate;

    private String latestVersionStatus;

    private String latestVersionValidStatus;

    private String latestReleaseNumber = null;

    // private String latestReleaseDate;

    private Map versionProperties;

    private static AppLogger log =
        new AppLogger(ContainerPropertiesUpdateHandler.class.getName());

    // names of elements that must occur but not handled by xml-schema
    private static final String[] expectedElements =
        { "name", "description", "creation-date", "context",
            Elements.ELEMENT_CONTENT_MODEL,
            TripleStoreUtility.PROP_PUBLIC_STATUS,
            TripleStoreUtility.PROP_CREATED_BY_ID, "lock-status",
            "current-version", "latest-version" };

    // check for optional elements lock-owner if locked, latest-release if in
    // release

    public ContainerPropertiesUpdateHandler(String containerId,
        Map versionProperties, StaxParser parser) {
        this.containerId = containerId;
        this.propertiesPath = "/container/properties";
        this.parser = parser;
        this.versionProperties = versionProperties;
        init();
    }

    public ContainerPropertiesUpdateHandler(String containerId,
        Map versionProperties, String propertiesPath, StaxParser parser) {
        this.containerId = containerId;
        this.propertiesPath = propertiesPath;
        this.parser = parser;
        this.versionProperties = versionProperties;
        init();
    }

    private void init() {
        expected = new Vector(Arrays.asList(expectedElements));
        latestVersionNumber =
            (String) versionProperties.get("latest-version.number");
        latestVersionDate =
            (String) versionProperties.get("latest-version.date");
        latestVersionStatus =
            (String) versionProperties.get("latest-version.status");
        latestVersionValidStatus =
            (String) versionProperties.get("latest-version.valid-status");
        latestReleaseNumber =
            (String) versionProperties
                .get(TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER);
    }

    public StartElement startElement(StartElement element)
        throws ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, SystemException {
        String curPath = parser.getCurPath();

        if (curPath.startsWith(propertiesPath)) {
            // do something

            // properties root
            if (curPath.equals(propertiesPath)) {

                try {
                    String xlinkType =
                        element
                            .getAttribute(Constants.XLINK_URI, "type")
                            .getValue();
                    if (xlinkType == null || !xlinkType.equals("simple")) {
                        throw new ReadonlyAttributeViolationException(
                            "xlink:type is not simple.");
                    }

                    String xlinkTitle =
                        element
                            .getAttribute(Constants.XLINK_URI, "title")
                            .getValue();
                    if (xlinkTitle == null || !xlinkTitle.equals("Properties")) {
                        throw new ReadonlyAttributeViolationException(
                            "Properties title is not 'Properties'.");
                    }

                    String xlinkHref =
                        element
                            .getAttribute(Constants.XLINK_URI, "href")
                            .getValue();
                    if (xlinkHref == null
                        || !xlinkHref.equals("/ir/container/" + containerId
                            + "/properties")) {
                        throw new ReadonlyAttributeViolationException(
                            "Properties href is not '/ir/container/"
                                + containerId + "/properties'.");
                    }
                }
                catch (NoSuchAttributeException e) {
                    throw new ReadonlyAttributeViolationException(e);
                }
            }

            // context
            if (curPath.equals(propertiesPath + "/context")) {
                expected.remove("context");

                try {
                    String xlinkType =
                        element
                            .getAttribute(Constants.XLINK_URI, "type")
                            .getValue();
                    if (xlinkType == null || !xlinkType.equals("simple")) {
                        throw new ReadonlyAttributeViolationException(
                            "xlink:type is not simple.");
                    }

                    String xlinkTitle =
                        element
                            .getAttribute(Constants.XLINK_URI, "title")
                            .getValue();
                    checkAttributeValue("context-title", xlinkTitle);
                    properties.put("context-title", xlinkTitle);

                    String xlinkHref =
                        element
                            .getAttribute(Constants.XLINK_URI, "href")
                            .getValue();
                    checkAttributeValue("context", xlinkHref.replaceFirst(
                        "/ir/context/", ""));
                    properties.put("context", xlinkHref.replaceFirst(
                        "/ir/context/", ""));
                }
                catch (NoSuchAttributeException e) {
                    throw new ReadonlyAttributeViolationException(e);
                }
            }

            // content-type
            else if (curPath.equals(propertiesPath + "/content-model")) {
                expected.remove("content-model");

                try {
                    String xlinkType =
                        element
                            .getAttribute(Constants.XLINK_URI, "type")
                            .getValue();
                    if (xlinkType == null || !xlinkType.equals("simple")) {
                        throw new ReadonlyAttributeViolationException(
                            "xlink:type is not simple.");
                    }

                    String xlinkTitle =
                        element
                            .getAttribute(Constants.XLINK_URI, "title")
                            .getValue();
                    checkAttributeValue("content-model-title", xlinkTitle);
                    properties.put("content-model-title", xlinkTitle);

                    String xlinkHref =
                        element
                            .getAttribute(Constants.XLINK_URI, "href")
                            .getValue();
                    checkAttributeValue("content-model", xlinkHref
                        .replaceFirst("/ctm/content-model/", ""));
                    properties.put("content-model", xlinkHref.replaceFirst(
                        "/ctm/content-model/", ""));
                }
                catch (NoSuchAttributeException e) {
                    throw new ReadonlyAttributeViolationException(e);
                }
            }

            // creator
            else if (curPath.equals(propertiesPath + "/"
                + TripleStoreUtility.PROP_CREATED_BY_ID)) {
                expected.remove(TripleStoreUtility.PROP_CREATED_BY_ID);

                try {
                    String xlinkType =
                        element
                            .getAttribute(Constants.XLINK_URI, "type")
                            .getValue();
                    if (xlinkType == null || !xlinkType.equals("simple")) {
                        throw new ReadonlyAttributeViolationException(
                            "xlink:type is not simple.");
                    }

                    String xlinkTitle =
                        element
                            .getAttribute(Constants.XLINK_URI, "title")
                            .getValue();
                    checkAttributeValue(
                        TripleStoreUtility.PROP_CREATED_BY_TITLE, xlinkTitle);
                    properties.put(TripleStoreUtility.PROP_CREATED_BY_TITLE,
                        xlinkTitle);

                    String xlinkHref =
                        element
                            .getAttribute(Constants.XLINK_URI, "href")
                            .getValue();
                    checkAttributeValue(TripleStoreUtility.PROP_CREATED_BY_ID,
                        xlinkHref.replaceFirst("/aa/user-account/", ""));
                    properties.put(TripleStoreUtility.PROP_CREATED_BY_ID,
                        xlinkHref.replaceFirst("/aa/user-account/", ""));
                }
                catch (NoSuchAttributeException e) {
                    throw new ReadonlyAttributeViolationException(e);
                }
            }

            // current-version
            else if (curPath.equals(propertiesPath + "/current-version")) {
                expected.remove("current-version");

                try {
                    String xlinkType =
                        element
                            .getAttribute(Constants.XLINK_URI, "type")
                            .getValue();
                    if (xlinkType == null || !xlinkType.equals("simple")) {
                        throw new ReadonlyAttributeViolationException(
                            "xlink:type is not simple.");
                    }

                    String xlinkTitle =
                        element
                            .getAttribute(Constants.XLINK_URI, "title")
                            .getValue();
                    // TODO check title

                    String xlinkHref =
                        element
                            .getAttribute(Constants.XLINK_URI, "href")
                            .getValue();
                    if (!xlinkHref.equals("/ir/container/" + containerId + ":"
                        + latestVersionNumber)) {
                        throw new ReadonlyAttributeViolationException(
                            "Container.properties.current-version has invalid href.");
                    }
                }
                catch (NoSuchAttributeException e) {
                    throw new ReadonlyAttributeViolationException(e);
                }
            }

            // latest-version
            else if (curPath.equals(propertiesPath + "/latest-version")) {
                expected.remove("latest-version");

                try {
                    String xlinkType =
                        element
                            .getAttribute(Constants.XLINK_URI, "type")
                            .getValue();
                    if (xlinkType == null || !xlinkType.equals("simple")) {
                        throw new ReadonlyAttributeViolationException(
                            "xlink:type is not simple.");
                    }

                    String xlinkTitle =
                        element
                            .getAttribute(Constants.XLINK_URI, "title")
                            .getValue();
                    // TODO check title

                    String xlinkHref =
                        element
                            .getAttribute(Constants.XLINK_URI, "href")
                            .getValue();
                    if (!xlinkHref.equals("/ir/container/" + containerId + ":"
                        + latestVersionNumber)) {
                        throw new ReadonlyAttributeViolationException(
                            "Container.properties.latest-version has invalid href.");
                    }
                }
                catch (NoSuchAttributeException e) {
                    throw new ReadonlyAttributeViolationException(e);
                }
            }

            // latest-release
            else if (curPath.equals(propertiesPath + "/latest-release")) {
                expected.remove("latest-release");

                try {
                    String xlinkType =
                        element
                            .getAttribute(Constants.XLINK_URI, "type")
                            .getValue();
                    if (xlinkType == null || !xlinkType.equals("simple")) {
                        throw new ReadonlyAttributeViolationException(
                            "xlink:type is not simple.");
                    }

                    // String xlinkTitle = element.getAttribute(
                    // Constants.XLINK_URI, "title").getValue();
                    // TODO check title

                    String xlinkHref =
                        element
                            .getAttribute(Constants.XLINK_URI, "href")
                            .getValue();
                    if (!xlinkHref.equals("/ir/container/" + containerId + ":"
                        + latestReleaseNumber)) {
                        throw new ReadonlyAttributeViolationException(
                            "Container.properties.latest-version has invalid href.");
                    }
                }
                catch (NoSuchAttributeException e) {
                    throw new ReadonlyAttributeViolationException(e);
                }
            }

        }
        return element;
    }

    public String characters(String data, StartElement element)
        throws Exception {
        String curPath = parser.getCurPath();

        if (curPath.startsWith(propertiesPath)) {
            // do something

            // name
            if (curPath.equals(propertiesPath + "/name")) {
                expected.remove("name");
                checkElementValue("name", data);
                properties.put("name", data);
            }

            // description
            else if (curPath.equals(propertiesPath + "/description")) {
                expected.remove("description");
                checkElementValue("description", data);
                properties.put("description", data);
            }

            // creation-date
            else if (curPath.equals(propertiesPath + "/creation-date")) {
                expected.remove("creation-date");
                String curVal =
                    TripleStoreUtility.getInstance().getCreationDate(
                        containerId);
                if (!data.equals(curVal)) {
                    throw new ReadonlyElementViolationException(
                        "Item properties has invalid creation-date.");
                }
            }

            // status
            else if (curPath.equals(propertiesPath + "/"
                + TripleStoreUtility.PROP_PUBLIC_STATUS)) {
                expected.remove(TripleStoreUtility.PROP_PUBLIC_STATUS);
                if (data.equals(Constants.STATUS_RELEASED)) {
                    expected.add("latest-release");
                }
                checkElementValue(TripleStoreUtility.PROP_PUBLIC_STATUS, data);
                properties.put(TripleStoreUtility.PROP_PUBLIC_STATUS, data);
            }

            // lock-status
            else if (curPath.equals(propertiesPath + "/lock-status")) {
                expected.remove("lock-status");
                if (LockHandler.getInstance().isLocked(containerId)) {
                    if (data.equals(Constants.STATUS_LOCKED)) {
                        expected.add("lock-owner");
                    }
                    else {
                        throw new ReadonlyElementViolationException(
                            "Container properties has invalid lock-status.");
                    }
                }
                else if (data.equals(Constants.STATUS_LOCKED)) {
                    throw new ReadonlyElementViolationException(
                        "Container properties has invalid lock-status.");
                }

                properties.put("lock-status", data);
            }

            // lock-owner
            else if (curPath.equals(propertiesPath + "/lock-owner")) {
                expected.remove("lock-owner");
                if (!data.equals(LockHandler.getInstance().getLockOwner(
                    containerId))) {
                    throw new ReadonlyElementViolationException(
                        "Container properties has invalid lock-owner.");
                }
                properties.put("lock-owner", data);
            }

            // lock-date
            else if (curPath.equals(propertiesPath + "/lock-date")) {
                expected.remove("lock-date");
                if (!data.equals(LockHandler.getInstance().getLockDate(
                    containerId))) {
                    throw new ReadonlyElementViolationException(
                        "Container properties has invalid lock-date.");
                }
                properties.put("lock-date", data);
            }

            // current-version
            else if (curPath.equals(propertiesPath + "/current-version/number")) {
                if (!data.equals(latestVersionNumber)) {
                    throw new ReadonlyElementViolationException(
                        "Container properties.current-version.number has invalid value.");
                }
            }
            else if (curPath.equals(propertiesPath + "/current-version/date")) {
                if (!data.equals(latestVersionDate)) {
                    throw new ReadonlyElementViolationException(
                        "Container properties.current-version.date has invalid value.");
                }
            }
            else if (curPath.equals(propertiesPath
                + "/current-version/version-status")) {
                if (!data.equals(latestVersionStatus)) {
                    throw new ReadonlyElementViolationException(
                        "Container properties.current-version.version-status has invalid value.");
                }
            }
            else if (curPath.equals(propertiesPath
                + "/current-version/valid-status")) {
                if (!data.equals(latestVersionValidStatus)) {
                    throw new ReadonlyElementViolationException(
                        "Container properties.current-version.valid-status has invalid value.");
                }
            }

            // latest-version
            else if (curPath.equals(propertiesPath + "/latest-version/number")) {
                if (!data.equals(latestVersionNumber)) {
                    throw new ReadonlyElementViolationException(
                        "Container properties.latest-version.number has invalid value.");
                }
            }
            else if (curPath.equals(propertiesPath + "/latest-version/date")) {
                if (!data.equals(latestVersionDate)) {
                    throw new ReadonlyElementViolationException(
                        "Container properties.latest-version.date has invalid value.");
                }
            }

            // latest-release
            // else if (curPath.equals(propertiesPath +
            // "/latest-release/number")) {
            // if (!data.equals(latestVersionNumber)) {
            // throw new ReadonlyElementViolationException(
            // "Container properties.latest-release.number has invalid value.");
            // }
            // }
            // else if (curPath.equals(propertiesPath + "/latest-release/date"))
            // {
            // if (!data.equals(latestVersionDate)) {
            // throw new ReadonlyElementViolationException(
            // "Container properties.latest-release.date has invalid value.");
            // }
            // }
            else if (curPath.equals(propertiesPath + "/latest-release/pid")) {
                // TODO
            }

        }
        return data;
    }

    public EndElement endElement(EndElement element) throws Exception {
        String curPath = parser.getCurPath();
        if (curPath.equals(propertiesPath)) {
            if (!expected.isEmpty()) {
                throw new ReadonlyElementViolationException("One of "
                    + expected + " is expected on update component.");
            }
        }
        return element;
    }

    private void checkElementValue(String key, String val)
        throws ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, SystemException {
        checkValue(key, val, false);
    }

    private void checkAttributeValue(String key, String val)
        throws ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, SystemException {
        checkValue(key, val, true);
    }

    private void checkValue(String key, String val, boolean isAttribute)
        throws ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, SystemException {
        String curVal =
            TripleStoreUtility.getInstance().getPropertiesElements(containerId,
             Constants.CONTAINER_PROPERTIES_NAMESPACE_URI + key);
        if (curVal != null) { // TODO exception on null
            if (!curVal.equals(val)) {
                String msg =
                    "Propertie " + key + " can not be updated to " + val + ".";
                if (isAttribute) {
                    throw new ReadonlyAttributeViolationException(msg);
                }
                else {
                    throw new ReadonlyElementViolationException(msg);
                }
            }
        }

    }

    public HashMap getProperties() {
        return properties;
    }
}
