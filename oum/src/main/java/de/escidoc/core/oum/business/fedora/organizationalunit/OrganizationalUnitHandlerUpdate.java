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
package de.escidoc.core.oum.business.fedora.organizationalunit;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHasChildrenException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.oum.business.fedora.resources.OrganizationalUnit;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class contains common methods for all handler methods.
 * 
 * @author MSC
 * 
 */
public class OrganizationalUnitHandlerUpdate
    extends OrganizationalUnitHandlerCreate {

    /**
     * Set the md records datastream.
     * 
     * @param xml
     *            The datatstream.
     * @param mdAttributesMap
     *            The md attributes.
     * @param escidocMdRecordnsUri
     *            The ns uri of escidoc md record.
     * @throws StreamNotFoundException
     *             If any stream was noch found in fedora.
     * @throws SystemException
     *             If anything else fails.
     */
    void setMdRecords(
            final Map<String, ByteArrayOutputStream> xml,
            final Map<String, Map<String, String>> mdAttributesMap,
            final String escidocMdRecordnsUri) throws StreamNotFoundException,
        SystemException {
        Map<String, Datastream> updated = new HashMap<String, Datastream>();

        // iterate over md-record names (keys) with
        for (String s : xml.keySet()) {
            // for every retrieved md-record XML create a Datastream
            String name = s;
            Map<String, String> mdProperties = null;
            if (name.equals(OrganizationalUnit.ESCIDOC)) {
                mdProperties = new HashMap<String, String>();
                mdProperties.put(OrganizationalUnit.NS_URI,
                        escidocMdRecordnsUri);
            }
            Datastream ds =
                    new Datastream(name, getOrganizationalUnit().getId(), xml.get(
                            name).toByteArray(), Datastream.MIME_TYPE_TEXT_XML,
                            mdProperties);
            Map<String, String> mdRecordAttributes = mdAttributesMap.get(name);
            ds.addAlternateId(Datastream.METADATA_ALTERNATE_ID);
            ds.addAlternateId(mdRecordAttributes.get("type"));
            ds.addAlternateId(mdRecordAttributes.get("schema"));
            updated.put(name, ds);
        }
        // set Datastreams from retrieved md-record XML in OU
        getOrganizationalUnit().setMdRecords(updated);
    }

    /**
     * Set dc datastream.
     * 
     * @param dc
     *            The dc datastream.
     * @throws StreamNotFoundException
     *             If the dc datastream was not found.
     * @throws SystemException
     *             If anything else fails.
     * @throws UnsupportedEncodingException
     *             If dc datastream has wrong encoding.
     */
    protected void setDc(final String dc) throws StreamNotFoundException,
        SystemException, UnsupportedEncodingException {

        getOrganizationalUnit().setDc(
            new Datastream(Datastream.DC_DATASTREAM, getOrganizationalUnit()
                .getId(), dc.getBytes(XmlUtility.CHARACTER_ENCODING),
                Datastream.MIME_TYPE_TEXT_XML));
    }

    /**
     * Set a new state.
     * 
     * @param state
     *            The new state.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    void updateState(final String state) throws SystemException {

        Map<String, Object> values = new HashMap<String, Object>();
        String buildNumber = Utility.getInstance().getBuildNumber();
        values.put(XmlTemplateProvider.FRAMEWORK_BUILD_NUMBER, buildNumber);
        values.put(XmlTemplateProvider.PUBLIC_STATUS, state);
        values.put(XmlTemplateProvider.CREATED_BY_ID, getOrganizationalUnit()
            .getCreatedBy());
        values.put(XmlTemplateProvider.CREATED_BY_TITLE,
            getOrganizationalUnit().getCreatedByTitle());
        values.put(XmlTemplateProvider.MODIFIED_BY_ID, getOrganizationalUnit()
            .getModifiedBy());
        values.put(XmlTemplateProvider.MODIFIED_BY_TITLE,
            getOrganizationalUnit().getModifiedByTitle());

        getOrganizationalUnit().setRelsExt(
            getOrganizationalUnitRelsExt(getOrganizationalUnit().getId(),
                values, getOrganizationalUnit().getParents()));
    }

    /**
     * Check if the organizational unit was changed between the timestamp and
     * now.
     * 
     * @param timestamp
     *            The users last-modification-timestamp.
     * @throws OptimisticLockingException
     *             Thrown if the organizational unit was changed in the
     *             meantime.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     * @throws TripleStoreSystemException
     *             Thrown if request of TripleStore failed.
     */
    void checkUpToDate(final String timestamp)
        throws OptimisticLockingException, WebserverSystemException,
        TripleStoreSystemException, FedoraSystemException {

        getUtility().checkOptimisticLockingCriteria(
            getOrganizationalUnit().getLastModificationDate(), timestamp,
            "Organizational unit " + getOrganizationalUnit().getId());

    }

    /**
     * Check if the organizational unit is in the expected state.
     * 
     * @param methodText
     *            A text for exception.
     * @param state
     *            The expected state.
     * @throws InvalidStatusException
     *             If the organizational unit is in another state.
     */
    void checkInState(final String methodText, final String state)
        throws InvalidStatusException {

        if (!getOrganizationalUnit().getPublicStatus().equals(state)) {
            String message =
                "Organizational unit with id='"
                    + getOrganizationalUnit().getId() + "' cannot be "
                    + methodText + " because it is in status '"
                    + getOrganizationalUnit().getPublicStatus() + "'!";
            throw new InvalidStatusException(message);
        }
    }

    /**
     * Check if the parents of the organizational unit are in the expected
     * state.
     * 
     * @param methodText
     *            A text for exception.
     * @param state
     *            The expected state.
     * @throws InvalidStatusException
     *             If the organizational unit is in another state.
     * @throws SystemException
     *             If anything else fails.
     */
    void checkParentsInState(
            final String methodText, final String state)
        throws InvalidStatusException, SystemException {

        List<String> parents = getOrganizationalUnit().getParents();
        for (String parent1 : parents) {
            String parent = parent1;
            String parentState =
                    getTripleStoreUtility().getPropertiesElements(parent,
                            TripleStoreUtility.PROP_PUBLIC_STATUS);
            if (!state.equals(parentState)) {
                String message =
                        "Organizational unit with id='"
                                + getOrganizationalUnit().getId() + "' cannot be "
                                + methodText + " because parent with id='" + parent
                                + "' is in status '" + parentState + "'!";
                throw new InvalidStatusException(message);
            }
        }
    }

    /**
     * Check update rules for an organizational unit to be created.
     * 
     * @param parents
     *            The updated list of parents.
     * @throws InvalidStatusException
     *             If any update rule is not met.
     * @throws SystemException
     *             If anything else fails.
     */
    void checkCreateParentsConditions(final List<String> parents)
        throws InvalidStatusException, SystemException {

        // all parents must be in state created or opened
        for (String parent1 : parents) {
            String parent = parent1;
            String parentState =
                    getTripleStoreUtility().getPropertiesElements(parent,
                            TripleStoreUtility.PROP_PUBLIC_STATUS);
            if (!(Constants.STATUS_OU_CREATED.equals(parentState) || Constants.STATUS_OU_OPENED
                    .equals(parentState))) {
                String message =
                        "Organizational unit cannot be created  because parent with id='"
                                + parent + "' is in status '" + parentState + "'!";
                throw new InvalidStatusException(message);
            }
        }

    }

    /**
     * Check update rules for an organizational unit to be updated.
     * 
     * @param parents
     *            The updated list of parents.
     * @throws InvalidStatusException
     *             If any update rule is not met.
     * @throws SystemException
     *             If anything else fails.
     */
    void checkUpdateParentsConditions(final List<String> parents)
        throws InvalidStatusException, SystemException {

        String status = getOrganizationalUnit().getPublicStatus();
        if (Constants.STATUS_OU_CREATED.equals(status)) {
            // all parents must be in state created or opened
            for (String parent1 : parents) {
                String parent = parent1;
                String parentState =
                        getTripleStoreUtility().getPropertiesElements(parent,
                                TripleStoreUtility.PROP_PUBLIC_STATUS);
                if (!(Constants.STATUS_OU_CREATED.equals(parentState) || Constants.STATUS_OU_OPENED
                        .equals(parentState))) {
                    String message =
                            "Organizational unit with objid='"
                                    + getOrganizationalUnit().getId()
                                    + "' cannot be updated because parent with objid='"
                                    + parent + "' is in status '" + parentState + "'!";
                    throw new InvalidStatusException(message);
                }
            }
        }
        else {
            // check if parent list is changed, if so throw
            // InvalidStatusException
            List<String> currentParents = getOrganizationalUnit().getParents();
            if (currentParents.size() != parents.size()) {
                String message =
                    "Parent list of organizational unit with id='"
                        + getOrganizationalUnit().getId() + "' in status '"
                        + status + "' must not be updated!";
                throw new InvalidStatusException(message);
            }
            for (String parent : parents) {
                if (!currentParents.contains(parent)) {
                    String message =
                            "Parent list of organizational unit with id='"
                                    + getOrganizationalUnit().getId() + "' in status '"
                                    + status + "' must not be updated!";
                    throw new InvalidStatusException(message);
                }
            }
        }
    }

    /**
     * Check if the organizational unit has no children.
     * 
     * @param methodText
     *            A text for exception.
     * @throws OrganizationalUnitHasChildrenException
     *             If the organizational unit has children
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    void checkWithoutChildren(final String methodText)
        throws OrganizationalUnitHasChildrenException, SystemException {

        if (!getOrganizationalUnit().getChildrenIds().isEmpty()) {
            String message =
                "Organizational unit with id='"
                    + getOrganizationalUnit().getId() + "' cannot be "
                    + methodText + " because it has children:  '"
                    + getOrganizationalUnit().getChildrenIds() + "'!";
            throw new OrganizationalUnitHasChildrenException(message);
        }
    }

    /**
     * Check if the organizational unit has no children.
     * 
     * @param methodText
     *            A text for exception.
     * @throws InvalidStatusException
     *             Thrown if one of the children is not closed.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    void checkWithoutChildrenOrChildrenClosed(final String methodText)
        throws InvalidStatusException, SystemException {

        List<String> children = getOrganizationalUnit().getChildrenIds();
        if (!children.isEmpty()) {
            for (String aChildren : children) {
                String child = aChildren;
                String childState =
                        getTripleStoreUtility().getPropertiesElements(child,
                                TripleStoreUtility.PROP_PUBLIC_STATUS);

                if (!Constants.STATUS_OU_CLOSED.equals(childState)) {
                    String message =
                            "Organizational unit with id='"
                                    + getOrganizationalUnit().getId() + "' cannot be "
                                    + methodText + " because it has a child '" + child
                                    + "' not in state 'closed'!";

                    throw new InvalidStatusException(message);
                }
            }
        }
    }
}
