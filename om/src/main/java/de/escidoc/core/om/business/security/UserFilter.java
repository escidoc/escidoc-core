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
package de.escidoc.core.om.business.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.Container;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.RelsExtRefListExtractor;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Helper class to filter elements out where the user permissions are
 * restricted.
 * 
 * @author SWA
 * 
 */
public class UserFilter {

    /*
     * TODO I'm not pretty sure if this is a nice way. But for no helps to get
     * rid of aa packages from common.
     */

    /** The policy decision point used to check access privileges. */
    private PolicyDecisionPointInterface pdp;

    /**
     * UserFilter
     */
    public UserFilter() {

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
     * Gets the {@link PolicyDecisionPointInterface} implementation.
     * 
     * @return PolicyDecisionPointInterface
     */
    public PolicyDecisionPointInterface getPdp() {

        return pdp;
    }

    /**
     * 
     * @param objectType
     * @param roleCriteria
     * @param userCriteria
     * @param objectIds
     * @return
     * @throws WebserverSystemException
     * @throws MissingMethodParameterException
     */
    public List<String> filterUserRole(
        final String objectType, final String roleCriteria,
        final String userCriteria, final List<String> objectIds)
        throws WebserverSystemException, MissingMethodParameterException {

        List<String> resultIds = null;
        try {
            resultIds =
                getPdp().evaluateRoles(objectType, userCriteria, roleCriteria,
                    objectIds);
        }
        catch (final UserAccountNotFoundException e) {
            resultIds = new ArrayList<String>();
        }
        catch (final AuthenticationException e) {
            throw new WebserverSystemException(e);
        }
        catch (final AuthorizationException e) {
            throw new WebserverSystemException(e);
        }
        catch (final ResourceNotFoundException e) {
            resultIds = new ArrayList<String>();
        }
        catch (final SystemException e) {
            throw new WebserverSystemException(e);
        }
        return resultIds;
    }

    /**
     * 
     * @param objectType
     * @param userCriteria
     * @param roleCriteria
     * @return
     * @throws MissingMethodParameterException
     * @throws SystemException
     */
    public String getRoleUserWhereClause(
        final String objectType, final String userCriteria,
        final String roleCriteria) throws MissingMethodParameterException,
        SystemException {

        return getPdp().getRoleUserWhereClause(objectType, userCriteria,
            roleCriteria).toString();
    }

    /**
     * Retrieve member of Container filtered by user role.
     * 
     * @param containerId
     * @param filterParam
     * @return
     * @throws SystemException
     * @throws MissingMethodParameterException
     */
    public List<String> getContainerMemberList(
        final String containerId, final String filterParam)
        throws SystemException, MissingMethodParameterException {

        Map filter = XmlUtility.getFilterMap(filterParam);

        String userCriteria = null;
        String roleCriteria = null;
        String whereClause = null;
        if (filter != null) {
            // filter out user permissions
            userCriteria = (String) filter.get("user");
            roleCriteria = (String) filter.get("role");

            try {
                whereClause =
                    getPdp().getRoleUserWhereClause("container", userCriteria,
                        roleCriteria).toString();
            }
            catch (final SystemException e) {
                // FIXME: throw SystemException?
                throw new TripleStoreSystemException(
                    "Failed to retrieve clause for user and role criteria", e);
            }
        }

        List<String> resultList =
            TripleStoreUtility.getInstance().getContainerMemberList(
                containerId, filter, whereClause);

        if (!(userCriteria == null && roleCriteria == null)) {

            UserFilter ufilter = new UserFilter();
            resultList =
                ufilter.filterUserRole("member", roleCriteria, userCriteria,
                    resultList);
        }

        return resultList;

    }

    /**
     * Get the list of the member (structural relation) of the Container.
     * 
     * @param filter
     *            The memberRefList filter.
     * @return List of Container member (if <code>filter != null</code>
     *         filtered)
     * @throws SystemException
     * @throws MissingMethodParameterException
     */
    public List<String> getMemberRefList(
        final Container container, final String filter)
        throws MissingMethodParameterException, SystemException {

        List<String> memberRefs = null;

        if (container.getVersionNumber() == null) {

            memberRefs = getContainerMemberList(container.getId(), filter);
        }
        else {
            // A work around until Fedora makes restrictions on the FOXML-size:
            // RELS-EXT is now unversioned and therefore a Datastream
            // Escidoc_RELS_EXT
            // with a managed content must be parsed to fetch values for old
            // Container
            // versions
            final Vector<String> predicates = new Vector<String>();
            predicates.add(Constants.STRUCTURAL_RELATIONS_NS_URI + "member");
            final StaxParser sp = new StaxParser();
            final RelsExtRefListExtractor rerle =
                new RelsExtRefListExtractor(predicates, sp);
            sp.addHandler(rerle);
            try {
                sp.parse(container.getEscidocRelsExt().getStream());
            }
            catch (final Exception e) {
                throw new XmlParserSystemException("Unexpected exception.", e);
            }
            memberRefs =
                rerle.getEntries().get(
                    Constants.STRUCTURAL_RELATIONS_NS_URI + "member");

        }

        return memberRefs;
    }

}
