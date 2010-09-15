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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * This class contains methods for rendering the xml representations of
 * organizational units, it's sub resources, and lists.
 * 
 * @author MSC
 * 
 */
public class OrganizationalUnitHandlerRetrieve
    extends OrganizationalUnitHandlerBase {

    /** The policy decision point used to check access privileges. */
    private PolicyDecisionPointInterface pdp;

    /**
     * Gets the {@link PolicyDecisionPointInterface} implementation.
     * 
     * @return PolicyDecisionPointInterface
     */
    protected PolicyDecisionPointInterface getPdp() {

        return pdp;
    }

    /**
     * Injects the {@link PolicyDecisionPointInterface} implementation.
     * 
     * @param pdp
     *            the {@link PolicyDecisionPointInterface} to be injected.
     * @spring.property ref="service.PolicyDecisionPointBean"
     */
    public void setPdp(final PolicyDecisionPointInterface pdp) {

        this.pdp = pdp;
    }

    /**
     * Get the xml representation of an organizational unit in REST or SOAP
     * format. Whether REST or SOAP format is delivered is dependent on the
     * transport of the original request.
     * 
     * @return The xml representation of an organizational unit.
     * @throws SystemException
     *             If anything fails while rendering the xml representation.
     */
    protected String getOrganizationalUnitXml() throws SystemException {

        return getRenderer().render(getOrganizationalUnit());
    }

    /**
     * Get the xml representation of the properties of an organizational unit in
     * REST or SOAP format. Whether REST or SOAP format is delivered is
     * dependent on the transport of the original request.
     * 
     * @return The xml representation of the properties of an organizational
     *         unit.
     * @throws SystemException
     *             If anything fails while rendering the xml representation.
     */
    protected String getPropertiesXml() throws SystemException {

        return getRenderer().renderProperties(getOrganizationalUnit());
    }

    /**
     * Get the xml representation of the resources of an organizational unit.
     * 
     * @return The xml representation in REST or SOAP format of an
     *         organizational unit.
     * @throws SystemException
     *             If anything fails while rendering the xml representation.
     */
    protected String getResourcesXml() throws SystemException {

        return getRenderer().renderResources(getOrganizationalUnit());
    }

    /**
     * Get the xml representation of the md-records of an md-records in REST or
     * SOAP format. Whether REST or SOAP format is delivered is dependent on the
     * transport of the original request.
     * 
     * @return The xml representation of the md-records of an organizational
     *         unit.
     * @throws SystemException
     *             If anything fails while rendering the xml representation.
     */
    protected String getMdRecordsXml() throws SystemException {

        return getRenderer().renderMdRecords(getOrganizationalUnit());
    }

    /**
     * Get the xml representation of a single md-record in REST or SOAP format.
     * Whether REST or SOAP format is delivered is dependent on the transport of
     * the original request.
     * 
     * @param name
     *            The name of teh md-record.
     * @return The xml representation of the md-record of an organizational
     *         unit.
     * @throws SystemException
     *             If anything fails while rendering the xml representation.
     */
    protected String getMdRecordXml(final String name) throws SystemException {

        return getRenderer().renderMdRecord(getOrganizationalUnit(), name);
    }

    /**
     * Get the xml representation of the parent ous of an organizational unit in
     * REST or SOAP format. Whether REST or SOAP format is delivered is
     * dependent on the transport of the original request.
     * 
     * @return The xml representation of the organization details of an
     *         organizational unit.
     * @throws SystemException
     *             If anything fails while rendering the xml representation.
     */
    protected String getParentsXml() throws SystemException {

        return getRenderer().renderParents(getOrganizationalUnit());
    }

    /**
     * Get the xml representation of the children of an organizational unit in
     * REST or SOAP format. Whether REST or SOAP format is delivered is
     * dependent on the transport of the original request.
     * 
     * @return The xml representation of the parents of an organizational unit.
     * @throws SystemException
     *             If anything fails while rendering the xml representation.
     */
    protected String getChildObjectsXml() throws SystemException {
        List<String> children = new Vector<String>();
        List<String> chidrenIds = getOrganizationalUnit().getChildrenIds();
        String parentId = getOrganizationalUnit().getId();
        Iterator<String> idIter = chidrenIds.iterator();
        while (idIter.hasNext()) {
            String childId = null;
            try {
                childId = idIter.next();
                setOrganizationalUnit(childId);
                children.add(getOrganizationalUnitXml());
            }
            catch (OrganizationalUnitNotFoundException e) {
                throw new IntegritySystemException(
                    "Referenced child organizational unit '" + childId
                        + "' could not be retrieved! ", e);
            }
        }
        try {
            setOrganizationalUnit(parentId);
        }
        catch (OrganizationalUnitNotFoundException e) {
            throw new IntegritySystemException("Parent organizational unit '"
                + parentId + "' is not available! ", e);
        }
        return getRenderer().renderChildObjects(getOrganizationalUnit(),
            children);
    }

    /**
     * Get the xml representation of the parents of an organizational unit in
     * REST or SOAP format. Whether REST or SOAP format is delivered is
     * dependent on the transport of the original request.
     * 
     * @return The xml representation of the children of an organizational unit.
     * @throws SystemException
     *             If anything fails while rendering the xml representation.
     */
    protected String getParentObjectsXml() throws SystemException {

        List<String> parents = new Vector<String>();
        List<String> parentsIds = getOrganizationalUnit().getParents();
        String childId = getOrganizationalUnit().getId();
        Iterator<String> idIter = parentsIds.iterator();
        while (idIter.hasNext()) {
            String parentId = null;
            try {
                parentId = idIter.next();
                setOrganizationalUnit(parentId);
                parents.add(getOrganizationalUnitXml());
            }
            catch (OrganizationalUnitNotFoundException e) {
                throw new IntegritySystemException(
                    "Referenced parent organizational unit '" + parentId
                        + "' could not be retrieved! ", e);
            }
        }
        try {
            setOrganizationalUnit(childId);
        }
        catch (OrganizationalUnitNotFoundException e) {
            throw new IntegritySystemException("Child organizational unit '"
                + childId + "' is not available! ", e);
        }
        return getRenderer().renderParentObjects(getOrganizationalUnit(),
            parents);
    }

    /**
     * Compute the path list of an organizational and return the xml
     * representation of the path list in REST or SOAP format. Whether REST or
     * SOAP format is delivered is dependent on the transport of the original
     * request.
     * 
     * @return The xml representation of the path list of an organizational
     *         unit.
     * @throws SystemException
     *             If anything fails while rendering the xml representation.
     */
    protected String getPathListXml() throws SystemException {

        return getRenderer().renderPathList(getOrganizationalUnit(),
            computePathes());
    }

    /**
     * Get the xml representation of a filtered list of organizational units in
     * REST or SOAP format. Whether REST or SOAP format is delivered is
     * dependent on the transport of the original request.
     * 
     * @param filter
     *            The filter criteria.
     * @return The xml representation of the list of organizational units.
     * @throws SystemException
     *             If anything fails while rendering the xml representation.
     */
    protected String getOrganizationalUnitsXml(final String filter)
        throws SystemException {

        List<String> organizationalUnits = new Vector<String>();
        List<String> organizationalUnitIds = getFilteredList(filter);
        Iterator<String> idIter = organizationalUnitIds.iterator();
        while (idIter.hasNext()) {
            String id = null;
            try {
                id = idIter.next();
                setOrganizationalUnit(id);
                organizationalUnits.add(getOrganizationalUnitXml());
            }
            catch (OrganizationalUnitNotFoundException e) {
                throw new IntegritySystemException("Organizational unit '" + id
                    + "' from filter result could not be retrieved! ", e);
            }
        }
        return getRenderer().renderOrganizationalUnits(organizationalUnits);
    }

    /**
     * Get the xml representation of a filtered list of references to
     * organizational units in REST or SOAP format. Whether REST or SOAP format
     * is delivered is dependent on the transport of the original request.
     * 
     * @param filter
     *            The filter criteria.
     * @return The xml representation of the list of of references to
     *         organizational units.
     * @throws SystemException
     *             If anything fails while rendering the xml representation.
     */
    protected String getOrganizationalUnitRefsXml(final String filter)
        throws SystemException {

        return getRenderer().renderOrganizationalUnitRefs(
            getFilteredList(filter));
    }

    /**
     * Compute the pathes of the actual organizaional unit.
     * 
     * @return The pathes.
     * @throws SystemException
     *             If anything fails while computing the pathes.
     */
    private List<List<String>> computePathes() throws SystemException {

        super.initPathes();
        List<List<String>> result = new Vector<List<String>>();
        List<String> initialPath = new Vector<String>();
        initialPath.add(getOrganizationalUnit().getId());
        expandPaths(initialPath);

        while (!getPathes().empty()) {
            Vector<String> path = getPathes().pop();
            String topParentOu = path.lastElement();

            if (TripleStoreUtility
                .getInstance().getParents(topParentOu).isEmpty()) {
                result.add(path);
            }
            else {
                expandPaths(path);
            }
        }
        return result;
    }

    /**
     * Get the list of organizational unit ids filtered by the given filter
     * criteria.
     * 
     * @param filterXml
     *            The filter criteria.
     * @return The list of organizational filtered by the given filter criteria.
     * @throws SystemException
     *             If anything fails getting the filtered list of ids.
     */
    private List<String> getFilteredList(final String filterXml)
        throws SystemException {

        try {
            Map<String, Object> filterMap = XmlUtility.getFilterMap(filterXml);

            String userCriteria = null;
            String roleCriteria = null;
            String whereClause = null;
            if (filterMap != null) {
                // filter out user permissions
                userCriteria = (String) filterMap.get("user");
                roleCriteria = (String) filterMap.get("role");

                try {
                    whereClause =
                        getPdp().getRoleUserWhereClause("container",
                            userCriteria, roleCriteria).toString();
                }
                catch (final SystemException e) {
                    // FIXME: throw SystemException?
                    throw new SystemException(
                        "Failed to retrieve clause for user and role criteria",
                        e);
                }
                catch (MissingMethodParameterException e) {
                    throw new SystemException(
                        "Failed to retrieve clause for user and role criteria",
                        e);
                }
            }

            List<String> list =
                TripleStoreUtility.getInstance().evaluate(
                    Constants.ORGANIZATIONAL_UNIT_OBJECT_TYPE, filterMap, null,
                    whereClause);

            List<String> ids;
            try {
                ids =
                    getPdp().evaluateRetrieve(
                        Constants.ORGANIZATIONAL_UNIT_OBJECT_TYPE, list);
            }
            catch (final Exception e) {
                throw new WebserverSystemException(e);
            }

            return ids;
        }
        catch (MissingMethodParameterException e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
    }
}
