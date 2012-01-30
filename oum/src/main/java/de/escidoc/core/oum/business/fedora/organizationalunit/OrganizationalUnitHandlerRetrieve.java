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

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains methods for rendering the xml representations of organizational units, it's sub resources, and
 * lists.
 *
 * @author Michael Schneider
 */
public class OrganizationalUnitHandlerRetrieve extends OrganizationalUnitHandlerBase {

    /**
     * The policy decision point used to check access privileges.
     */
    @Autowired
    @Qualifier("service.PolicyDecisionPoint")
    private PolicyDecisionPointInterface pdp;

    /**
     * Gets the {@link PolicyDecisionPointInterface} implementation.
     *
     * @return PolicyDecisionPointInterface
     */
    protected PolicyDecisionPointInterface getPdp() {

        return this.pdp;
    }

    /**
     * Get the xml representation of an organizational.
     *
     * @return The xml representation of an organizational unit.
     * @throws SystemException If anything fails while rendering the xml representation.
     */
    protected String getOrganizationalUnitXml() throws SystemException, MdRecordNotFoundException {

        return getRenderer().render(getOrganizationalUnit());
    }

    /**
     * Get the xml representation of the properties of an organizational unit.
     *
     * @return The xml representation of the properties of an organizational unit.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    protected String getPropertiesXml() throws WebserverSystemException {

        return getRenderer().renderProperties(getOrganizationalUnit());
    }

    /**
     * Get the xml representation of the resources of an organizational unit.
     *
     * @return The xml representation of an organizational unit
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    protected String getResourcesXml() throws WebserverSystemException {

        return getRenderer().renderResources(getOrganizationalUnit());
    }

    /**
     * Get the xml representation of the md-records.
     *
     * @return The xml representation of the md-records of an organizational unit.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    protected String getMdRecordsXml() throws WebserverSystemException, MdRecordNotFoundException {

        return getRenderer().renderMdRecords(getOrganizationalUnit());
    }

    /**
     * Get the xml representation of the parent ous of an organizational unit.
     *
     * @return The xml representation of the organization details of an organizational unit.
     * @throws SystemException If anything fails while rendering the xml representation.
     */
    protected String getParentsXml() throws SystemException {

        return getRenderer().renderParents(getOrganizationalUnit());
    }

    /**
     * Get the xml representation of the children of an organizational unit.
     *
     * @return The xml representation of the parents of an organizational unit.
     * @throws SystemException If anything fails while rendering the xml representation.
     */
    protected String getChildObjectsXml() throws SystemException {
        final List<String> children = new ArrayList<String>();
        final List<String> chidrenIds = getOrganizationalUnit().getChildrenIds();
        final String parentId = getOrganizationalUnit().getId();
        for (final String chidrenId : chidrenIds) {
            String childId = null;
            try {
                childId = chidrenId;
                setOrganizationalUnit(childId);
                children.add(getOrganizationalUnitXml());
            }
            catch (final OrganizationalUnitNotFoundException e) {
                throw new IntegritySystemException("Referenced child organizational unit '" + childId
                    + "' could not be retrieved! ", e);
            }
            catch (final MdRecordNotFoundException e2) {
                throw new IntegritySystemException("Referenced md-record not found.", e2);
            }
        }
        try {
            setOrganizationalUnit(parentId);
        }
        catch (final OrganizationalUnitNotFoundException e) {
            throw new IntegritySystemException("Parent organizational unit '" + parentId + "' is not available! ", e);
        }
        return getRenderer().renderChildObjects(getOrganizationalUnit(), children);
    }

    /**
     * Get the xml representation of the parents of an organizational unit.
     *
     * @return The xml representation of the children of an organizational unit.
     * @throws SystemException If anything fails while rendering the xml representation.
     */
    protected String getParentObjectsXml() throws SystemException {

        final List<String> parents = new ArrayList<String>();
        final List<String> parentsIds = getOrganizationalUnit().getParents();
        final String childId = getOrganizationalUnit().getId();
        for (final String parentsId : parentsIds) {
            String parentId = null;
            try {
                parentId = parentsId;
                setOrganizationalUnit(parentId);
                parents.add(getOrganizationalUnitXml());
            }
            catch (final OrganizationalUnitNotFoundException e) {
                throw new IntegritySystemException("Referenced parent organizational unit '" + parentId
                    + "' could not be retrieved! ", e);
            }
            catch (final MdRecordNotFoundException e2) {
                throw new IntegritySystemException("Referenced md-record not found.", e2);
            }
        }
        try {
            setOrganizationalUnit(childId);
        }
        catch (final OrganizationalUnitNotFoundException e) {
            throw new IntegritySystemException("Child organizational unit '" + childId + "' is not available! ", e);
        }
        return getRenderer().renderParentObjects(getOrganizationalUnit(), parents);
    }

    /**
     * Compute the path list of an organizational and return the xml representation of the path list.
     *
     * @return The xml representation of the path list of an organizational unit.
     * @throws SystemException If anything fails while rendering the xml representation.
     */
    protected String getPathListXml() throws SystemException {

        return getRenderer().renderPathList(getOrganizationalUnit(), computePathes());
    }

    /**
     * Compute the pathes of the actual organizaional unit.
     *
     * @return The pathes.
     * @throws SystemException If anything fails while computing the pathes.
     */
    private List<List<String>> computePathes() throws SystemException {

        initPathes();
        final List<List<String>> result = new ArrayList<List<String>>();
        final List<String> initialPath = new ArrayList<String>();
        initialPath.add(getOrganizationalUnit().getId());
        expandPaths(initialPath);

        while (!getPathes().empty()) {
            final List<String> path = getPathes().pop();
            final String topParentOu = path.get(path.size() - 1);

            if (getTripleStoreUtility().getParents(topParentOu).isEmpty()) {
                result.add(path);
            }
            else {
                expandPaths(path);
            }
        }
        return result;
    }

}
