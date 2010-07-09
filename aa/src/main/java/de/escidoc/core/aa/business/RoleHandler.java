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

import de.escidoc.core.aa.business.authorisation.CustomPolicyBuilder;
import de.escidoc.core.aa.business.cache.PoliciesCache;
import de.escidoc.core.aa.business.filter.RoleFilter;
import de.escidoc.core.aa.business.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.aa.business.interfaces.RoleHandlerInterface;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.aa.business.renderer.interfaces.RoleRendererInterface;
import de.escidoc.core.aa.business.stax.handler.RolePropertiesStaxHandler;
import de.escidoc.core.aa.business.stax.handler.ScopeStaxHandler;
import de.escidoc.core.aa.business.stax.handler.XacmlStaxHandler;
import de.escidoc.core.aa.convert.XacmlParser;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.AccessRights;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.filter.SRURequest;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
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
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.stax.handler.filter.FilterHandler;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ExplainXmlProvider;
import de.escidoc.core.common.util.xml.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.handler.OptimisticLockingStaxHandler;

/**
 * Business layer implementation of a handler that manages eSciDoc roles.
 * 
 * @spring.bean id="business.RoleHandler"
 * @author TTE
 * 
 */
public class RoleHandler implements RoleHandlerInterface {

    /**
     * The logger.
     */
    private static AppLogger log = new AppLogger(RoleHandler.class.getName());

    private static final String ERROR_ROLE_NOT_FOUND = "Role not found";

    private static final String ERROR_ROLE_IN_USE =
        "Role can't be removed as it is referenced by at least one role grant.";

    public static final String FORBIDDEN_ROLE_NAME = "Default-User";

    private AccessRights accessRights;

    private EscidocRoleDaoInterface roleDao;

    private RoleRendererInterface renderer;

    private PolicyDecisionPointInterface pdp;

    private UserAccountDaoInterface userAccountDao;

    private XacmlParser xacmlParser;

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param xmlData
     * @return
     * @throws XmlCorruptedException
     * @throws UniqueConstraintViolationException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface#create(java.lang.String)
     * @aa
     */
    public String create(final String xmlData) throws XmlCorruptedException,
        UniqueConstraintViolationException, SystemException {

        EscidocRole role = new EscidocRole();
        ByteArrayInputStream in =
            XmlUtility.convertToByteArrayInputStream(xmlData);

        StaxParser sp = new StaxParser(XmlUtility.NAME_ROLE);

        RolePropertiesStaxHandler propertiesHandler =
            new RolePropertiesStaxHandler(role, roleDao);
        sp.addHandler(propertiesHandler);
        ScopeStaxHandler scopeHandler = new ScopeStaxHandler(role, roleDao);
        sp.addHandler(scopeHandler);
        XacmlStaxHandler xacmlHandler = new XacmlStaxHandler(role);
        sp.addHandler(xacmlHandler);

        try {
            sp.parse(in);
        }
        catch (InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (UniqueConstraintViolationException e) {
            throw e;
        }
        catch (SystemException e) {
            throw e;
        }
        catch (Exception e) {
            final StringBuffer msg =
                StringUtility.concatenate("Unexpected exception in ",
                    getClass().getName(), ".create: ", e.getClass().getName());
            log.error(msg.toString(), e);
            throw new WebserverSystemException(msg.toString(), e);
        }

        // check if policy is parsable by XACML-Parser
        try {
            role.setId("someId");
            CustomPolicyBuilder.buildXacmlRolePolicySet(role);
            role.setId(null);
        }
        catch (Exception e) {
            throw new XmlCorruptedException(
                "XACML-Parser couldnt parse policy", e);
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
     * @param id
     * @throws RoleNotFoundException
     * @throws RoleInUseViolationException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface
     *      #delete(java.lang.String)
     * @aa
     */
    public void delete(final String id) throws RoleNotFoundException,
        RoleInUseViolationException, SystemException {

        final EscidocRole role = fetchRole(id);

        List<RoleGrant> grants = userAccountDao.retrieveGrantsByRole(role);
        if (grants != null && !grants.isEmpty()) {
            throw new RoleInUseViolationException(ERROR_ROLE_IN_USE);
        }

        roleDao.deleteRole(role);

        // delete role from policy cache
        PoliciesCache.clearRole(id);

        // delete role from resource cache
        accessRights.deleteAccessRight(id);
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws RoleNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface
     *      #retrieve(java.lang.String)
     * @aa
     */
    public String retrieve(final String id) throws RoleNotFoundException,
        SystemException {

        return renderer.render(fetchRole(id));
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws RoleNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface
     *      #retrieveResources(java.lang.String)
     * @aa
     */
    public String retrieveResources(final String id)
        throws RoleNotFoundException, SystemException {

        return renderer.renderResources(fetchRole(id));
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @param xmlData
     * @return
     * @throws RoleNotFoundException
     * @throws XmlCorruptedException
     * @throws MissingAttributeValueException
     * @throws UniqueConstraintViolationException
     * @throws OptimisticLockingException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface#update(java.lang.String,
     *      java.lang.String)
     */
    public String update(final String id, final String xmlData)
        throws RoleNotFoundException, XmlCorruptedException,
        MissingAttributeValueException, UniqueConstraintViolationException,
        OptimisticLockingException, SystemException {

        // Check XACML Policy
        EscidocRole role = new EscidocRole();
        ByteArrayInputStream in =
            XmlUtility.convertToByteArrayInputStream(xmlData);

        StaxParser sp = new StaxParser(XmlUtility.NAME_ROLE);

        XacmlStaxHandler xacmlHandler = new XacmlStaxHandler(role);
        sp.addHandler(xacmlHandler);

        try {
            sp.parse(in);
        }
        catch (InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (Exception e) {
            final StringBuffer msg =
                StringUtility.concatenate("Unexpected exception in ",
                    getClass().getName(), ".update: ", e.getClass().getName());
            log.error(msg.toString(), e);
            throw new WebserverSystemException(msg.toString(), e);
        }
        // check if policy is parseable by XACML-Parser
        try {
            role.setId("someId");
            role.setRoleName("someName");
            CustomPolicyBuilder.buildXacmlRolePolicySet(role);
        }
        catch (Exception e) {
            throw new XmlCorruptedException(
                "XACML-Parser couldnt parse policy", e);
        }

        role = fetchRole(id);
        in = XmlUtility.convertToByteArrayInputStream(xmlData);

        sp = new StaxParser(XmlUtility.NAME_ROLE);

        OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(role.getLastModificationDate());
        sp.addHandler(optimisticLockingHandler);
        RolePropertiesStaxHandler propertiesHandler =
            new RolePropertiesStaxHandler(role, roleDao);
        sp.addHandler(propertiesHandler);
        ScopeStaxHandler scopeHandler = new ScopeStaxHandler(role, roleDao);
        sp.addHandler(scopeHandler);
        xacmlHandler = new XacmlStaxHandler(role);
        sp.addHandler(xacmlHandler);

        try {
            sp.parse(in);
        }
        catch (InvalidXmlException e) {
            throw new XmlCorruptedException(e);
        }
        catch (OptimisticLockingException e) {
            throw e;
        }
        catch (MissingAttributeValueException e) {
            throw e;
        }
        catch (UniqueConstraintViolationException e) {
            throw e;
        }
        catch (SystemException e) {
            throw e;
        }
        catch (Exception e) {
            final StringBuffer msg =
                StringUtility.concatenate("Unexpected exception in ",
                    getClass().getName(), ".update: ", e.getClass().getName());
            log.error(msg.toString(), e);
            throw new WebserverSystemException(msg.toString(), e);
        }

        setModificationValues(role);

        roleDao.saveOrUpdate(role);

        // delete role from policy cache
        PoliciesCache.clearRole(id);

        // update role in resource cache
        updateRole(role);

        return renderer.render(role);
    }

    /**
     * See Interface for functional description.
     * 
     * @param filter
     * @return
     * @throws InvalidXmlException
     * @throws SystemException
     * @throws InvalidContentException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface
     *      #retrieveRoles(java.lang.String)
     */
    public String retrieveRoles(final String filter)
        throws XmlCorruptedException, SystemException, InvalidContentException {

        String result = null;

        try {
            result = retrieveRoles((Object) filter);
        }
        catch (InvalidSearchQueryException e) {
            // cannot happen here
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param filter
     * @return
     * @throws InvalidSearchQueryException
     * @throws SqlDatabaseSystemException
     * @throws SystemException
     * @see de.escidoc.core.aa.service.interfaces.RoleHandlerInterface
     *      #retrieveRoles(java.util.Map)
     */
    public String retrieveRoles(final Map<String, String[]> filter)
        throws InvalidSearchQueryException, SystemException {
        String result = null;

        try {
            result = retrieveRoles((Object) filter);
        }
        catch (InvalidContentException e) {
            // cannot happen here
        }
        catch (InvalidXmlException e) {
            // cannot happen here
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param filter
     *            roleFilter
     * @return list of filtered roles
     * @throws XmlCorruptedException
     *             e
     * @throws SystemException
     *             e
     * @throws InvalidContentException
     *             e
     * @throws InvalidSearchQueryException
     *             e
     */
    @SuppressWarnings("unchecked")
    private String retrieveRoles(final Object filter)
        throws XmlCorruptedException, SystemException, InvalidContentException,
        InvalidSearchQueryException {
        boolean isXmlRequest = filter instanceof String;
        boolean explain = false;
        String query = null;
        String result = null;
        int offset = FilterHandler.DEFAULT_OFFSET;
        int limit = FilterHandler.DEFAULT_LIMIT;
        FilterHandler fh = null;
        Map<String, Object> filters = null;
        if (isXmlRequest) {
            de.escidoc.core.common.util.stax.StaxParser sp =
                new de.escidoc.core.common.util.stax.StaxParser();

            TaskParamHandler tph = new TaskParamHandler(sp);
            tph.setCheckLastModificationDate(false);
            sp.addHandler(tph);
            fh = new FilterHandler(sp);
            sp.addHandler(fh);
            try {
                sp.parse(new ByteArrayInputStream(((String) filter)
                    .getBytes(XmlUtility.CHARACTER_ENCODING)));
            }
            catch (InvalidContentException e) {
                throw e;
            }
            catch (Exception e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }

            filters = fh.getRules();

            if (filters.isEmpty()) {
                filters = null;
            }

            if (filters == null) {
                filters = new HashMap<String, Object>();
                filters.put(TripleStoreUtility.PROP_NAME, "%");
            }
            offset = fh.getOffset();
            limit = fh.getLimit();
        }
        else {
            SRURequest parameters =
                new SRURequest((Map<String, String[]>) filter);
            query = parameters.query;
            limit = parameters.limit;
            offset = parameters.offset;
            explain = parameters.explain;
        }

        if (explain) {
            Map<String, Object> values = new HashMap<String, Object>();

            values.put("PROPERTY_NAMES", new RoleFilter(null)
                .getPropertyNames());
            result = ExplainXmlProvider.getInstance().getExplainRoleXml(values);
        }
        else {
            int needed = offset + limit;
            int currentLimit = needed;
            int currentOffset = 0;
            final List<EscidocRole> permittedObjects =
                new ArrayList<EscidocRole>();
            final int size = permittedObjects.size();
            while (size <= needed) {
                List<EscidocRole> tmpObjects = null;

                if (isXmlRequest) {
                    tmpObjects =
                        roleDao.retrieveRoles(filters, currentOffset,
                            currentLimit, fh.getOrderBy(), fh.getSorting());
                }
                else {
                    tmpObjects =
                        roleDao.retrieveRoles(query, currentOffset,
                            currentLimit);
                }
                if (tmpObjects == null || tmpObjects.isEmpty()) {
                    break;
                }
                else {
                    Iterator<EscidocRole> objectIter = tmpObjects.iterator();
                    final List<String> ids =
                        new ArrayList<String>(tmpObjects.size());
                    while (objectIter.hasNext()) {
                        EscidocRole object = objectIter.next();
                        ids.add(object.getId());
                    }

                    try {
                        final List<String> tmpPermitted =
                            pdp.evaluateRetrieve("role", ids);
                        final int numberPermitted = tmpPermitted.size();
                        if (numberPermitted == 0) {
                            break;
                        }
                        else {
                            int permittedIndex = 0;
                            String currentPermittedId =
                                tmpPermitted.get(permittedIndex);
                            objectIter = tmpObjects.iterator();
                            while (objectIter.hasNext()) {
                                final EscidocRole object = objectIter.next();
                                if (currentPermittedId.equals(object.getId())) {
                                    permittedObjects.add(object);
                                    ++permittedIndex;
                                    if (permittedIndex < numberPermitted) {
                                        currentPermittedId =
                                            tmpPermitted.get(permittedIndex);
                                    }
                                    else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    catch (MissingMethodParameterException e) {
                        throw new SystemException("Unexpected exception "
                            + "during evaluating access rights.", e);
                    }
                    catch (ResourceNotFoundException e) {
                        throw new SystemException("Unexpected exception "
                            + "during evaluating access rights.", e);
                    }
                }
                currentOffset += currentLimit;
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
            result = renderer.renderRoles(offsetObjects, !isXmlRequest);

        }
        return result;
    }

    /**
     * Sets the creation date and the created-by user in the provided
     * <code>EscidocRole</code> object.<br/>
     * The values are set to the last modification date and modified-by values
     * of the provided role.<br/>
     * Before calling this method, the last modified values must be set.
     * 
     * @param role
     *            The <code>EscidocRole</code> object to modify.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    private void setCreationValues(final EscidocRole role)
        throws SystemException {

        // initialize creation-date value
        role.setCreationDate(role.getLastModificationDate());

        // initialize created-by values
        role.setUserAccountByCreatorId(role.getUserAccountByModifiedById());
    }

    /**
     * Sets the last modification date and the modified-by user in the provided
     * <code>EscidocRole</code> object.<br/>
     * The last modification date is set to the current time, and the
     * modified-by id and title to the values of the user account of the
     * current, authenticated user.
     * 
     * @param role
     *            The <code>EscidocRole</code> object to modify.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    private void setModificationValues(final EscidocRole role)
        throws SystemException {

        // initialized last-modification-date value
        role.setLastModificationDate(new Date(System.currentTimeMillis()));

        // initialize modified-by values
        final String userAccountId = UserContext.getId();
        if (userAccountId == null) {
            throw new WebserverSystemException(
                "System fault: Current user not set!");
        }
        role.setUserAccountByModifiedById(userAccountDao
            .retrieveUserAccountById(userAccountId));
    }

    /**
     * Fetches the role identified by the provided id.
     * 
     * @param id
     *            The id of the role.
     * @return Returns the fetched <code>EscidocRoleObject</code>.
     * @throws SystemException
     *             Thrown in case of an internal database error.
     * @throws RoleNotFoundException
     *             Thrown if a role with the provided id does not exist.
     * @aa
     */
    private EscidocRole fetchRole(final String id) throws SystemException,
        RoleNotFoundException {

        final EscidocRole role = roleDao.retrieveRole(id);
        if (role == null || FORBIDDEN_ROLE_NAME.equals(role.getRoleName())) {

            throw new RoleNotFoundException(StringUtility
                .concatenateWithBracketsToString(ERROR_ROLE_NOT_FOUND, id));
        }
        return role;
    }

    /**
     * Get the {@link XacmlParser}.
     */
    private XacmlParser getXacmlParser() throws WebserverSystemException {
        if (xacmlParser == null) {
            xacmlParser =
                (XacmlParser) BeanLocator.getBean(BeanLocator.AA_FACTORY_ID,
                    "convert.XacmlParser");
        }
        return xacmlParser;
    }

    /**
     * Injects the access rights object.
     * 
     * @spring.property ref="resource.DbAccessRights"
     * @param accessRights
     *            access rights from Spring
     */
    public void setAccessRights(final AccessRights accessRights) {
        this.accessRights = accessRights;
    }

    /**
     * Injects the data access object to access {@link UserAccount} objects from
     * the database.
     * 
     * @param userAccountDao
     *            The dao to set.
     * @spring.property ref="persistence.UserAccountDao"
     * @aa
     */
    public void setUserAccountDao(final UserAccountDaoInterface userAccountDao) {

        this.userAccountDao = userAccountDao;
    }

    /**
     * Injects the data access object to access {@link EscidocRole} objects from
     * the database.
     * 
     * @param roleDao
     *            The dao to set.
     * @spring.property ref="persistence.EscidocRoleDao"
     * @aa
     */
    public void setRoleDao(final EscidocRoleDaoInterface roleDao) {

        this.roleDao = roleDao;
    }

    /**
     * Injects the policy decision point bean.
     * 
     * @param pdp
     *            The {@link PolicyDecisionPoint}.
     * @spring.property ref="business.PolicyDecisionPoint"
     * @aa
     */
    public void setPdp(final PolicyDecisionPointInterface pdp) {

        this.pdp = pdp;
    }

    /**
     * Injects the renderer.
     * 
     * @spring.property 
     *                  ref="eSciDoc.core.aa.business.renderer.VelocityXmlRoleRenderer"
     * @param renderer
     *            The renderer to inject.
     * @aa
     */
    public void setRenderer(final RoleRendererInterface renderer) {

        this.renderer = renderer;
    }

    /**
     * Update the given role in the resource cache.
     * 
     * @param role
     *            role id
     * 
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database error.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    private void updateRole(final EscidocRole role)
        throws SqlDatabaseSystemException, WebserverSystemException {
        getXacmlParser().parse(role);
        for (ResourceType resourceType : ResourceType.values()) {
            // ensure the role is written to database
            roleDao.flush();

            String sqlStatement = getXacmlParser().getRules(resourceType);

            if ((sqlStatement != null) && (sqlStatement.length() > 0)) {
                log.info("update access right (" + role.getId() + ","
                    + resourceType + "," + sqlStatement + ")");
                accessRights.putAccessRight(resourceType, role.getId(),
                    sqlStatement);
            }
        }
    }
}
