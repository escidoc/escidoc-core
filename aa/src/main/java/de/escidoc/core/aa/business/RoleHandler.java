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
package de.escidoc.core.aa.business;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.aa.business.authorisation.CustomPolicyBuilder;
import de.escidoc.core.aa.business.filter.AccessRights;
import de.escidoc.core.aa.business.filter.RoleFilter;
import de.escidoc.core.aa.business.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.aa.business.interfaces.RoleHandlerInterface;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.aa.business.renderer.interfaces.RoleRendererInterface;
import de.escidoc.core.aa.business.stax.handler.RolePropertiesStaxHandler;
import de.escidoc.core.aa.business.stax.handler.ScopeStaxHandler;
import de.escidoc.core.aa.business.stax.handler.XacmlStaxHandler;
import de.escidoc.core.aa.convert.XacmlParser;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.filter.DbRequestParameters;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.LastModificationDateMissingException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.RoleInUseViolationException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ExplainXmlProvider;
import de.escidoc.core.common.util.xml.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.handler.OptimisticLockingStaxHandler;

/**
 * Business layer implementation of a handler that manages eSciDoc roles.
 *
 * @author Torsten Tetteroo
 */
@Service("business.RoleHandler")
public class RoleHandler implements RoleHandlerInterface {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleHandler.class);

    private static final String ERROR_ROLE_NOT_FOUND = "Role not found";

    private static final String ERROR_ROLE_IN_USE =
        "Role can't be removed as it is referenced by at least one role grant.";

    public static final String FORBIDDEN_ROLE_NAME = "Default-User";

    @Autowired
    @Qualifier("resource.AccessRights")
    private AccessRights accessRights;

    @Autowired
    @Qualifier("persistence.EscidocRoleDao")
    private EscidocRoleDaoInterface roleDao;

    @Autowired
    @Qualifier("eSciDoc.core.aa.business.renderer.VelocityXmlRoleRenderer")
    private RoleRendererInterface renderer;

    @Autowired
    @Qualifier("business.PolicyDecisionPoint")
    private PolicyDecisionPointInterface pdp;

    @Autowired
    @Qualifier("persistence.UserAccountDao")
    private UserAccountDaoInterface userAccountDao;

    @Autowired
    @Qualifier("convert.XacmlParser")
    private XacmlParser xacmlParser;

    @Autowired
    @Qualifier("security.SecurityHelper")
    private SecurityHelper securityHelper;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected RoleHandler() {
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface#create(String)
     */
    @Override
    public String create(final String xmlData) throws XmlCorruptedException, UniqueConstraintViolationException,
        SystemException {

        final EscidocRole role = new EscidocRole();
        final ByteArrayInputStream in = XmlUtility.convertToByteArrayInputStream(xmlData);

        final StaxParser sp = new StaxParser(XmlUtility.NAME_ROLE);

        final RolePropertiesStaxHandler propertiesHandler = new RolePropertiesStaxHandler(role, this.roleDao);
        sp.addHandler(propertiesHandler);
        final ScopeStaxHandler scopeHandler = new ScopeStaxHandler(role);
        sp.addHandler(scopeHandler);
        final XacmlStaxHandler xacmlHandler = new XacmlStaxHandler(role);
        sp.addHandler(xacmlHandler);

        try {
            sp.parse(in);
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (final UniqueConstraintViolationException e) {
            throw e;
        }
        catch (final SystemException e) {
            throw e;
        }
        catch (final Exception e) {
            final String msg = "Unexpected exception in " + getClass().getName() + ".create: " + e.getClass().getName();
            throw new WebserverSystemException(msg, e);
        }

        // check if policy is parsable by XACML-Parser
        try {
            role.setId("someId");
            CustomPolicyBuilder.buildXacmlRolePolicySet(role);
            role.setId(null);
        }
        catch (final Exception e) {
            throw new XmlCorruptedException("XACML-Parser couldnt parse policy", e);
        }

        setModificationValues(role);
        setCreationValues(role);

        roleDao.saveOrUpdate(role);

        // create role in resource cache
        updateRole(role);

        return renderer.render(role);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface #delete(java.lang.String)
     */
    @Override
    public void delete(final String id) throws RoleNotFoundException, RoleInUseViolationException, SystemException {

        final EscidocRole role = fetchRole(id);
        if (FORBIDDEN_ROLE_NAME.equals(role.getRoleName())) {
            throw new RoleNotFoundException(StringUtility.format(ERROR_ROLE_NOT_FOUND, id));
        }

        final List<RoleGrant> grants = userAccountDao.retrieveGrantsByRole(role);
        if (grants != null && !grants.isEmpty()) {
            throw new RoleInUseViolationException(ERROR_ROLE_IN_USE);
        }

        roleDao.deleteRole(role);

        // delete role from policy cache
        securityHelper.clearRole(id);

        // delete role from resource cache
        accessRights.deleteAccessRight(id);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface #retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String id) throws RoleNotFoundException, WebserverSystemException,
        SqlDatabaseSystemException {

        return renderer.render(fetchRole(id));
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface #retrieveResources(java.lang.String)
     */
    @Override
    public String retrieveResources(final String id) throws RoleNotFoundException, WebserverSystemException,
        SqlDatabaseSystemException {

        return renderer.renderResources(fetchRole(id));
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface#update(String, String)
     */
    @Override
    public String update(final String id, final String xmlData) throws RoleNotFoundException, XmlCorruptedException,
        MissingAttributeValueException, UniqueConstraintViolationException, OptimisticLockingException, SystemException {

        // Check XACML Policy
        EscidocRole role = new EscidocRole();
        ByteArrayInputStream in = XmlUtility.convertToByteArrayInputStream(xmlData);

        StaxParser sp = new StaxParser(XmlUtility.NAME_ROLE);

        XacmlStaxHandler xacmlHandler = new XacmlStaxHandler(role);
        sp.addHandler(xacmlHandler);

        try {
            sp.parse(in);
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (final Exception e) {
            final String msg = "Unexpected exception in " + getClass().getName() + ".update: " + e.getClass().getName();
            throw new WebserverSystemException(msg, e);
        }
        // check if policy is parseable by XACML-Parser
        try {
            role.setId("someId");
            role.setRoleName("someName");
            CustomPolicyBuilder.buildXacmlRolePolicySet(role);
        }
        catch (final Exception e) {
            throw new XmlCorruptedException("XACML-Parser couldnt parse policy", e);
        }

        role = fetchRole(id);
        if (FORBIDDEN_ROLE_NAME.equals(role.getRoleName())) {

            throw new RoleNotFoundException(StringUtility.format(ERROR_ROLE_NOT_FOUND, id));
        }
        in = XmlUtility.convertToByteArrayInputStream(xmlData);

        sp = new StaxParser(XmlUtility.NAME_ROLE);

        final OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(role.getLastModificationDate());
        sp.addHandler(optimisticLockingHandler);
        final RolePropertiesStaxHandler propertiesHandler = new RolePropertiesStaxHandler(role, this.roleDao);
        sp.addHandler(propertiesHandler);
        final ScopeStaxHandler scopeHandler = new ScopeStaxHandler(role);
        sp.addHandler(scopeHandler);
        xacmlHandler = new XacmlStaxHandler(role);
        sp.addHandler(xacmlHandler);

        try {
            sp.parse(in);
        }
        catch (final InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (LastModificationDateMissingException e) {
            throw new MissingAttributeValueException(e);
        }
        catch (final OptimisticLockingException e) {
            throw e;
        }
        catch (final MissingAttributeValueException e) {
            throw e;
        }
        catch (final UniqueConstraintViolationException e) {
            throw e;
        }
        catch (final SystemException e) {
            throw e;
        }
        catch (final Exception e) {
            final String msg = "Unexpected exception in " + getClass().getName() + ".update: " + e.getClass().getName();
            throw new WebserverSystemException(msg, e);
        }

        setModificationValues(role);

        roleDao.saveOrUpdate(role);

        // delete role from policy cache
        securityHelper.clearRole(id);

        // update role in resource cache
        updateRole(role);

        return renderer.render(role);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface #retrieveRoles(java.util.Map)
     */
    @Override
    public String retrieveRoles(final Map<String, String[]> filter) throws InvalidSearchQueryException, SystemException {
        final SRURequestParameters parameters = new DbRequestParameters(filter);
        final String query = parameters.getQuery();
        final int limit = parameters.getMaximumRecords();
        final int offset = parameters.getStartRecord();
        final boolean explain = parameters.isExplain();

        final String result;
        if (explain) {
            final Map<String, Object> values = new HashMap<String, Object>();

            values.put("PROPERTY_NAMES", new RoleFilter(null).getPropertyNames());
            result = ExplainXmlProvider.getInstance().getExplainRoleXml(values);
        }
        else if (limit == 0) {
            result = renderer.renderRoles(new ArrayList<EscidocRole>(0), parameters.getRecordPacking());
        }
        else {
            final int needed = offset + limit;
            int currentOffset = 0;
            final List<EscidocRole> permittedObjects = new ArrayList<EscidocRole>();
            final int size = permittedObjects.size();
            while (size <= needed) {

                final List<EscidocRole> tmpObjects = roleDao.retrieveRoles(query, currentOffset, needed);
                if (tmpObjects == null || tmpObjects.isEmpty()) {
                    break;
                }
                Iterator<EscidocRole> objectIter = tmpObjects.iterator();
                final List<String> ids = new ArrayList<String>(tmpObjects.size());
                while (objectIter.hasNext()) {
                    final EscidocRole object = objectIter.next();
                    ids.add(object.getId());
                }

                try {
                    final List<String> tmpPermitted = pdp.evaluateRetrieve("role", ids);
                    final int numberPermitted = tmpPermitted.size();
                    if (numberPermitted == 0) {
                        break;
                    }
                    else {
                        int permittedIndex = 0;
                        String currentPermittedId = tmpPermitted.get(permittedIndex);
                        objectIter = tmpObjects.iterator();
                        while (objectIter.hasNext()) {
                            final EscidocRole object = objectIter.next();
                            if (currentPermittedId.equals(object.getId())) {
                                permittedObjects.add(object);
                                ++permittedIndex;
                                if (permittedIndex < numberPermitted) {
                                    currentPermittedId = tmpPermitted.get(permittedIndex);
                                }
                                else {
                                    break;
                                }
                            }
                        }
                    }
                }
                catch (final MissingMethodParameterException e) {
                    throw new SystemException("Unexpected exception " + "during evaluating access rights.", e);
                }
                catch (final ResourceNotFoundException e) {
                    throw new SystemException("Unexpected exception " + "during evaluating access rights.", e);
                }
                currentOffset += needed;
            }

            final List<EscidocRole> offsetObjects;
            final int numberPermitted = permittedObjects.size();
            if (offset < numberPermitted) {
                offsetObjects = new ArrayList<EscidocRole>(limit);
                for (int i = offset; i < numberPermitted && i < needed; i++) {
                    offsetObjects.add(permittedObjects.get(i));
                }
            }
            else {
                offsetObjects = new ArrayList<EscidocRole>(0);
            }
            result = renderer.renderRoles(offsetObjects, parameters.getRecordPacking());

        }
        return result;
    }

    /**
     * Sets the creation date and the created-by user in the provided {@code EscidocRole} object.<br/> The values
     * are set to the last modification date and modified-by values of the provided role.<br/> Before calling this
     * method, the last modified values must be set.
     *
     * @param role The {@code EscidocRole} object to modify.
     * @throws SystemException Thrown in case of an internal error.
     */
    private static void setCreationValues(final EscidocRole role) {

        // initialize creation-date value
        role.setCreationDate(role.getLastModificationDate());

        // initialize created-by values
        role.setUserAccountByCreatorId(role.getUserAccountByModifiedById());
    }

    /**
     * Sets the last modification date and the modified-by user in the provided {@code EscidocRole} object.<br/>
     * The last modification date is set to the current time, and the modified-by id and title to the values of the user
     * account of the current, authenticated user.
     *
     * @param role The {@code EscidocRole} object to modify.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException
     */
    private void setModificationValues(final EscidocRole role) throws SqlDatabaseSystemException,
        WebserverSystemException {

        // initialized last-modification-date value
        role.setLastModificationDate(new Date(System.currentTimeMillis()));

        // initialize modified-by values
        final String userAccountId = UserContext.getId();
        if (userAccountId == null) {
            throw new WebserverSystemException("System fault: Current user not set!");
        }
        role.setUserAccountByModifiedById(userAccountDao.retrieveUserAccountById(userAccountId));
    }

    /**
     * Fetches the role identified by the provided id.
     *
     * @param id The id of the role.
     * @return Returns the fetched {@code EscidocRoleObject}.
     * @throws RoleNotFoundException Thrown if a role with the provided id does not exist.
     * @throws de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException
     */
    private EscidocRole fetchRole(final String id) throws RoleNotFoundException, SqlDatabaseSystemException {

        final EscidocRole role = roleDao.retrieveRole(id);
        if (role == null) {
            throw new RoleNotFoundException(StringUtility.format(ERROR_ROLE_NOT_FOUND, id));
        }
        return role;
    }

    /**
     * Update the given role in the resource cache.
     *
     * @param role role id
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     * @throws WebserverSystemException   Thrown in case of an internal error.
     */
    private void updateRole(final EscidocRole role) throws SqlDatabaseSystemException, WebserverSystemException {
        this.xacmlParser.parse(role);
        for (final ResourceType resourceType : ResourceType.values()) {
            // ensure the role is written to database
            roleDao.flush();

            final String scopeRules = xacmlParser.getScopeRules(resourceType);
            final String policyRules = xacmlParser.getPolicyRules(resourceType);

            LOGGER.info("create access right (" + role.getId() + ',' + resourceType + ',' + scopeRules + ','
                + policyRules + ')');
            accessRights.putAccessRight(resourceType, role.getId(), scopeRules, policyRules);
        }
    }
}
