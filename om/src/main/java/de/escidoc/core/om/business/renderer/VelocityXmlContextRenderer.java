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
package de.escidoc.core.om.business.renderer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.interfaces.FedoraResource;
import de.escidoc.core.common.exceptions.application.missing.MissingParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ContextXmlProvider;
import de.escidoc.core.om.business.fedora.container.FedoraContainerHandler;
import de.escidoc.core.om.business.fedora.context.Context;
import de.escidoc.core.om.business.fedora.context.FedoraContextHandler;
import de.escidoc.core.om.business.fedora.item.FedoraItemHandler;
import de.escidoc.core.om.business.renderer.interfaces.ContextRendererInterface;

/**
 * @author Steffen Wagner
 */
@Service
public class VelocityXmlContextRenderer implements ContextRendererInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityXmlContextRenderer.class);

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    @Autowired
    @Qualifier("business.FedoraItemHandler")
    private FedoraItemHandler itemHandler;

    @Autowired
    @Qualifier("business.FedoraContainerHandler")
    private FedoraContainerHandler containerHandler;

    /**
     * Private constructor to prevent initialization.
     */
    protected VelocityXmlContextRenderer() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.renderer.interfaces.ContextRendererInterface
     * #render(de.escidoc.core.om.business.fedora.resources.Context)
     */
    @Override
    public String render(final FedoraContextHandler contextHandler) throws SystemException {

        final Context context = contextHandler.getContext();
        final Map<String, Object> values = new HashMap<String, Object>();

        addCommonValues(context, values);
        addPropertiesValues(context, values);
        addResourcesValues(context, values);
        renderAdminDescriptors(contextHandler, values);

        values.put(XmlTemplateProviderConstants.IS_ROOT_SUB_RESOURCE, XmlTemplateProviderConstants.FALSE);

        return ContextXmlProvider.getInstance().getContextXml(values);
    }

    /**
     * Render AdminDescriptors.
     *
     * @param contextHandler ContextHandler.
     * @param values         Context value map.
     * @return XML representation of admin-descriptors
     * @throws FedoraSystemException    Thrown if retrieving admin-descriptors datastream from Fedora failed.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws EncodingSystemException  Thrown if character encoding failed.
     */
    @Override
    public String renderAdminDescriptors(final FedoraContextHandler contextHandler, final Map<String, Object> values)
        throws FedoraSystemException, WebserverSystemException, EncodingSystemException {

        addCommonValues(contextHandler.getContext(), values);

        final Map<String, Datastream> admDescs = contextHandler.getContext().getAdminDescriptorsMap();

        final Set<String> keys = admDescs.keySet();

        if (!admDescs.isEmpty()) {
            final Iterator<String> it = keys.iterator();
            final Collection<String> admDescriptors = new ArrayList<String>();

            while (it.hasNext()) {
                final String name = it.next();
                final Datastream adm = admDescs.get(name);
                admDescriptors.add(renderAdminDescriptor(contextHandler, name, adm, false));
            }

            values.put("admsContent", admDescriptors);
        }

        final Context context = contextHandler.getContext();
        values.put("admsHref", XmlUtility.getContextHref(context.getId()) + "/admin-descriptors");
        values.put("admsTitle", "Admin Descriptors");
        values.put(XmlTemplateProviderConstants.IS_ROOT_SUB_RESOURCE, XmlTemplateProviderConstants.TRUE);

        return ContextXmlProvider.getInstance().getAdminDescriptorsXml(values);
    }

    /**
     * Render one admin-descriptor datastream.
     *
     * @param contextHandler The contextHandler.
     * @param name           Name of the datastream (unique).
     * @param admDesc        The datastream.
     * @param isRoot         Set true if admin-descriptor is to render as root element.
     * @return XML representation of one admin-descriptor datastream.
     * @throws EncodingSystemException  Thrown if encoding convertion fails.
     * @throws WebserverSystemException Thrown if anything else fails.
     */

    @Override
    public String renderAdminDescriptor(
        final FedoraContextHandler contextHandler, final String name, final Datastream admDesc, final boolean isRoot)
        throws EncodingSystemException, WebserverSystemException {
        if (admDesc.isDeleted()) {
            return "";
        }
        final Map<String, Object> values = new HashMap<String, Object>();
        addCommonValues(contextHandler.getContext(), values);
        values.put("admHref", XmlUtility.getContextHref(contextHandler.getContext().getId())
            + "/admin-descriptors/admin-descriptor/" + name);
        values.put("admName", name);
        values.put("admRecordTitle", name + " admin descriptor.");
        values.put(XmlTemplateProviderConstants.IS_ROOT_RESOURCES, isRoot);

        final String admContent;
        try {
            admContent = new String(admDesc.getStream(), XmlUtility.CHARACTER_ENCODING);
        }
        catch (final UnsupportedEncodingException e) {
            throw new EncodingSystemException(e);
        }
        values.put("admRecordContent", admContent);
        return ContextXmlProvider.getInstance().getAdminDescriptorXml(values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.renderer.interfaces.ContextRendererInterface
     * #renderProperties(de.escidoc.core.om.business.fedora.resources.Context)
     */
    @Override
    public String renderProperties(final FedoraContextHandler contextHandler) throws WebserverSystemException {

        final Context context = contextHandler.getContext();
        final Map<String, Object> values = new HashMap<String, Object>();

        addCommonValues(context, values);
        addNamespaceValues(values);
        values.put(XmlTemplateProviderConstants.IS_ROOT_PROPERTIES, XmlTemplateProviderConstants.TRUE);
        try {
            addPropertiesValues(context, values);
        }
        catch (final SystemException e) {
            throw new WebserverSystemException(e);
        }

        return ContextXmlProvider.getInstance().getPropertiesXml(values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.renderer.interfaces.ContextRendererInterface
     * #renderResources(de.escidoc.core.om.business.fedora.resources.Context)
     */
    @Override
    public String renderResources(final FedoraContextHandler contextHandler) throws WebserverSystemException {

        final Context context = contextHandler.getContext();
        final Map<String, Object> values = new HashMap<String, Object>();

        addCommonValues(context, values);
        values.put(XmlTemplateProviderConstants.IS_ROOT_SUB_RESOURCE, XmlTemplateProviderConstants.TRUE);
        addNamespaceValues(values);
        addResourcesValues(context, values);

        return ContextXmlProvider.getInstance().getResourcesXml(values);
    }

    /**
     * Render Context MemberList.
     *
     * @param contextHandler FedoraContextHandler
     * @param memberList     List of members.
     * @return XML representation with list of Context members.
     * @throws SystemException        If anything fails.
     * @throws AuthorizationException Thrown if access to origin Item is restricted.
     */
    @Override
    public String renderMemberList(final FedoraContextHandler contextHandler, final List<String> memberList)
        throws SystemException, AuthorizationException {

        final Map<String, Object> values = new HashMap<String, Object>();
        final Context context = contextHandler.getContext();
        addCommonValues(context, values);
        addMemberListValues(values, memberList);

        return ContextXmlProvider.getInstance().getMemberListXml(values);
    }

    /**
     * See Interface for functional description.
     *
     * @param contextHandler The ContextHandler
     * @param memberList     The list with members
     * @return XML representation of member reference list
     * @throws SystemException        Thrown if internal error occurs.
     * @throws AuthorizationException Thrown if access to origin Item is restricted.
     */
    @Override
    public String renderMemberRefList(final FedoraContextHandler contextHandler, final List<String> memberList)
        throws SystemException, AuthorizationException {

        final Map<String, Object> values = new HashMap<String, Object>();
        final Context context = contextHandler.getContext();
        addCommonValues(context, values);
        addMemberListValues(values, memberList);

        return ContextXmlProvider.getInstance().getMemberRefListXml(values);
    }

    /**
     * Adds the common values to the provided map.
     *
     * @param context The Context for that data shall be created.
     * @param values  The map to add values to.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private void addCommonValues(final Context context, final Map<String, Object> values)
        throws WebserverSystemException {

        try {
            final DateTime lastModDate = context.getLastModificationDate();
            values.put(XmlTemplateProviderConstants.VAR_LAST_MODIFICATION_DATE, lastModDate.toString());
        }
        catch (FedoraSystemException e) {
            throw new WebserverSystemException("Error on getting last modification date.", e);
        }

        values.put("contextId", context.getId());
        values.put("contextTitle", context.getTitle());
        values.put("contextHref", context.getHref());

        addXlinkValues(values);
        addNamespaceValues(values);
    }

    /**
     * Add xlink values to value Map.
     *
     * @param values Map where parameter to add.
     */
    private static void addXlinkValues(final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.VAR_ESCIDOC_BASE_URL, System
            .getProperty(EscidocConfiguration.ESCIDOC_CORE_BASEURL));
        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE_PREFIX, Constants.XLINK_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE, Constants.XLINK_NS_URI);
    }

    /**
     * Add namespace values to Map.
     *
     * @param values Map where parameter to add.
     */
    private void addNamespaceValues(final Map<String, Object> values) {

        values.put("contextNamespacePrefix", Constants.CONTEXT_PROPERTIES_PREFIX);
        values.put("contextNamespace", Constants.CONTEXT_NAMESPACE_URI);

        addPropertiesNamespaceValues(values);
        addStructuralRelationsNamespaceValues(values);
    }

    /**
     * Add name relations spaces.
     *
     * @param values Value Map for Velocity
     */
    protected static void addStructuralRelationsNamespaceValues(final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
    }

    /**
     * Add name properties spaces.
     *
     * @param values Value Map for Velocity
     */
    protected static void addPropertiesNamespaceValues(final Map<String, Object> values) {
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);
    }

    /**
     * Adds the properties values to the provided map.
     *
     * @param context .
     * @param values  Map with property values. New values are added to this Map.
     * @throws SystemException If anything fails.
     */
    private void addPropertiesValues(final Context context, final Map<String, Object> values) throws SystemException {

        values.put(XmlTemplateProviderConstants.VAR_PROPERTIES_TITLE, "Properties");
        values.put(XmlTemplateProviderConstants.VAR_PROPERTIES_HREF, XmlUtility.getContextPropertiesHref(context
            .getId()));

        values.put("contextName", context.getTitle());
        final String description =
            this.tripleStoreUtility.getPropertiesElements(context.getId(), Constants.DC_NS_URI + "description");
        if (description != null) {
            values.put("contextDescription", description);
        }
        values.put("contextCreationDate", context.getCreationDate());

        values.put("contextStatus", context.getProperty(PropertyMapKeys.PUBLIC_STATUS));
        values.put("contextStatusComment", context.getProperty(PropertyMapKeys.PUBLIC_STATUS_COMMENT));
        values.put("contextType", context.getProperty(PropertyMapKeys.CONTEXT_TYPE));
        values.put("contextObjid", context.getId());

        values.put("contextCreatedById", context.getCreatedBy());
        values.put("contextCreatedByHref", Constants.USER_ACCOUNT_URL_BASE + context.getCreatedBy());
        values.put("contextCreatedByTitle", context.getProperty(PropertyMapKeys.CREATED_BY_TITLE));

        values.put("contextCurrentVersionModifiedById", context.getModifiedBy());
        values.put("contextCurrentVersionModifiedByHref", Constants.USER_ACCOUNT_URL_BASE + context.getModifiedBy());
        values.put("contextCurrentVersionModifiedByTitle", context
            .getProperty(PropertyMapKeys.LATEST_VERSION_MODIFIED_BY_TITLE));

        values.put("organizational-units", getOrganizationalUnitsContext(context.getOrganizationalUnitObjids()));
    }

    /**
     * Maybe a bad play. Better use renderOrganizationalUntitsRefs()
     *
     * @param ouids Vector with IDs of OUs.
     * @return Vector with OU description (id, title, href)
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    public Collection<Map<String, String>> getOrganizationalUnitsContext(final Iterable<String> ouids)
        throws TripleStoreSystemException {

        final Collection<Map<String, String>> ousContext = new ArrayList<Map<String, String>>();

        for (final String ouid : ouids) {
            ousContext.add(getOrganizationalUnitContext(ouid));
        }

        return ousContext;
    }

    /**
     * OU context (id, title, href).
     *
     * @param id The Id of the Organizational Unit.
     * @return HashMap with (id, title, href)
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    public Map<String, String> getOrganizationalUnitContext(final String id) throws TripleStoreSystemException {
        final Map<String, String> ouContext = new HashMap<String, String>();

        ouContext.put("id", id);
        ouContext.put("title", this.tripleStoreUtility.getPropertiesElements(id, TripleStoreUtility.PROP_DC_TITLE));
        ouContext.put("href", XmlUtility.getOrganizationalUnitHref(id));
        return ouContext;
    }

    /**
     * Adds the resource values to the provided map.
     *
     * @param context The context for that data shall be created.
     * @param values  The map to add values to.
     */
    private static void addResourcesValues(final FedoraResource context, final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.RESOURCES_TITLE, "Resources");
        values.put("resourcesHref", XmlUtility.getContextResourcesHref(context.getId()));
        values.put("membersHref", XmlUtility.getContextHref(context.getId()) + "/resources/members");
        values.put("membersTitle", "Members ");
    }

    /**
     * Add the namespace values to the provided map.
     *
     * @param values The map to add values to.
     */
    private static void addListNamespaceValues(final Map<String, Object> values) {

        values.put("organizationalUnitsNamespacePrefix", Constants.ORGANIZATIONAL_UNIT_LIST_PREFIX);
        values.put("organizationalUnitsNamespace", Constants.ORGANIZATIONAL_UNIT_LIST_NAMESPACE_URI);
    }

    /**
     * Add the values to render the member list.
     *
     * @param values
     * @param memberList
     * @throws AuthorizationException Thrown if access to origin Item is restricted.
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    private void addMemberListValues(final Map<String, Object> values, final Iterable<String> memberList)
        throws SystemException, AuthorizationException {

        values.put("memberListNamespacePrefix", Constants.MEMBER_LIST_PREFIX);
        values.put("memberListNamespace", Constants.MEMBER_LIST_NAMESPACE_URI);
        values.put("memberListPrefix", Constants.XLINK_PREFIX);

        final StringBuilder sb = new StringBuilder();

        for (final String objectId : memberList) {
            final String objectType = this.tripleStoreUtility.getObjectType(objectId);
            try {
                if (Constants.ITEM_OBJECT_TYPE.equals(objectType)) {
                    sb.append(itemHandler.retrieve(objectId));
                }
                else if (Constants.CONTAINER_OBJECT_TYPE.equals(objectType)) {
                    sb.append(containerHandler.retrieve(objectId));
                }
                else {
                    final String msg =
                        "FedoraContextHandler.retrieveMemberRefs:" + " can not return object with unknown type: "
                            + objectId + ". Write comment.";
                    sb.append("<!-- ").append(msg).append(" -->");
                    LOGGER.error(msg);
                }
            }
            catch (final ItemNotFoundException e) {
                final Map<String, Object> extValues = new HashMap<String, Object>();
                addXlinkValues(extValues);
                addListNamespaceValues(extValues);
                extValues.put("href", "/ir/" + objectType + '/' + objectId);
                extValues.put("objid", objectId);
                final String msg = "FedoraContextHandler.retrieveMemberRefs:" + " can not retrieve object";
                extValues.put("msg", msg);
                sb.append(ContextXmlProvider.getInstance().getWithdrawnMessageXml(extValues));
                LOGGER.debug(msg, e);
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn(msg);
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(msg, e);
                }
            }
            catch (final ComponentNotFoundException e) {
                final Map<String, Object> extValues = new HashMap<String, Object>();
                addXlinkValues(extValues);
                addListNamespaceValues(extValues);
                extValues.put("href", "/ir/" + objectType + '/' + objectId);
                extValues.put("objid", objectId);
                final String msg = "FedoraContextHandler.retrieveMemberRefs:can not retrieve object";
                extValues.put("msg", msg);
                sb.append(ContextXmlProvider.getInstance().getWithdrawnMessageXml(extValues));
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn(msg);
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(msg, e);
                }
            }
            catch (final MissingParameterException e) {
                throw new SystemException("Should not occure in FedoraContextHandler.retrieveMembers", e);
            }
            catch (final ContainerNotFoundException e) {
                final Map<String, Object> extValues = new HashMap<String, Object>();
                addXlinkValues(extValues);
                addListNamespaceValues(extValues);
                extValues.put("href", "/ir/" + objectType + '/' + objectId);
                extValues.put("objid", objectId);
                final String msg = "FedoraContextHandler.retrieveMembers:can not retrieve object";
                extValues.put("msg", msg);
                sb.append(ContextXmlProvider.getInstance().getWithdrawnMessageXml(extValues));
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn(msg);
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(msg, e);
                }
            }
            catch (final EncodingSystemException e) {
                throw new SystemException("Should not occure in FedoraContextHandler.retrieveMembers", e);
            }

        }
        values.put("memberList", sb.toString());
    }

    /**
     * @param id The id of the context.
     * @return Returns the name of a context.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    public String getName(final String id) throws TripleStoreSystemException {
        return getProperty(id, TripleStoreUtility.PROP_NAME);
    }

    /**
     * @param id       The id of the context.
     * @param property The name of the property.
     * @return Returns a value of a property of an organizational unit.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    private String getProperty(final String id, final String property) throws TripleStoreSystemException {
        return this.tripleStoreUtility.getPropertiesElements(id, property);
    }
}
