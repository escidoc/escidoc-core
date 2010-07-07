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
package de.escidoc.core.om.business.fedora.context;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.resources.CqlFilter;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.XmlFilter;
import de.escidoc.core.common.business.fedora.resources.interfaces.FilterInterface;
import de.escidoc.core.common.business.fedora.resources.interfaces.ResourceCacheInterface;
import de.escidoc.core.common.business.fedora.resources.listener.ResourceListener;
import de.escidoc.core.common.business.filter.SRURequest;
import de.escidoc.core.common.exceptions.application.invalid.ContextNotEmptyException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AdminDescriptorNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ExplainXmlProvider;
import de.escidoc.core.om.business.fedora.contentRelation.FedoraContentRelationHandler;
import de.escidoc.core.om.business.interfaces.ContextHandlerInterface;

/**
 * @spring.bean id="business.FedoraContextHandler" scope="prototype"
 * @author FRS
 * 
 */
public class FedoraContextHandler extends ContextHandlerUpdate
    implements ContextHandlerInterface {

    private static AppLogger log = new AppLogger(
        FedoraContextHandler.class.getName());

    private ResourceCacheInterface containerCache = null;

    private ResourceCacheInterface contextCache = null;

    private ResourceCacheInterface itemCache = null;

    private final List<ResourceListener> contextListeners =
        new Vector<ResourceListener>();

    private FedoraContentRelationHandler contentRelationHandler;

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
     * This is a wrapper class for the create method. It takes a xml string and
     * returns either the respresentation of the resource or its id.
     * 
     * @param xmlData
     *            the string that contains the resource
     * @param isCreate
     *            set true if Context is created via create method, set to false
     *            if Context is created via ingest method
     * @return id the string that contains the id of the created resource
     * 
     * @throws ContextNameNotUniqueException
     *             e
     * @throws SystemException
     *             e
     * @throws ContentModelNotFoundException
     *             e
     * @throws ReadonlyElementViolationException
     *             e
     * @throws MissingAttributeValueException
     *             e
     * @throws MissingElementValueException
     *             e
     * @throws ReadonlyAttributeViolationException
     *             e
     * @throws InvalidContentException
     *             e
     * @throws OrganizationalUnitNotFoundException
     *             e
     * @throws InvalidStatusException
     *             e
     */
    private String doCreate(final String xmlData, final boolean isCreate)
        throws ContextNameNotUniqueException, SystemException,
        ContentModelNotFoundException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, InvalidContentException,
        OrganizationalUnitNotFoundException, InvalidStatusException {

        String id = createContext(xmlData);
        try {
            setContext(id);
        }
        catch (ContextNotFoundException e) {
            log.error("Created resource not found.", e);
            throw new SystemException("Created resource not found.", e);
        }
        String contextXml = getContextXml(this);
        fireContextCreated(id, contextXml);
        if (isCreate) {
            return contextXml;
        }
        else {
            return id;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.interfaces.ContextHandlerInterface#create
     * (java.lang.String)
     */
    public String create(final String xmlData)
        throws ContextNameNotUniqueException, SystemException,
        ContentModelNotFoundException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, InvalidContentException,
        OrganizationalUnitNotFoundException, InvalidStatusException {
        return doCreate(xmlData, true);
    }

    /**
     * @see de.escidoc.core.common.business.interfaces.IngestableResource#ingest(String)
     */
    public String ingest(final String xmlData)
        throws ContextNameNotUniqueException, SystemException,
        ContentModelNotFoundException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, InvalidContentException,
        OrganizationalUnitNotFoundException, InvalidStatusException {
        return doCreate(xmlData, false);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.interfaces.ContextHandlerInterface#retrieve
     * (java.lang.String)
     */
    public String retrieve(final String id) throws ContextNotFoundException,
        SystemException {
        setContext(id);
        return getContextXml(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ContextHandlerInterface#retrieveResource(java.lang.String,
     * java.lang.String)
     */
    public EscidocBinaryContent retrieveResource(
        final String id, final String resourceName, final Map<?, ?> parameters)
        throws OperationNotFoundException, ContextNotFoundException,
        SystemException {

        EscidocBinaryContent content = new EscidocBinaryContent();
        content.setMimeType("text/xml");

        if (resourceName.equals("members")) {
            try {
                content.setContent(new ByteArrayInputStream(retrieveMembers(id,
                    parameters).getBytes(XmlUtility.CHARACTER_ENCODING)));
                return content;
            }
            catch (InvalidSearchQueryException e) {
                throw new WebserverSystemException(e);
            }
            catch (InvalidXmlException e) {
                throw new WebserverSystemException(e);
            }
            catch (UnsupportedEncodingException e) {
                throw new WebserverSystemException(e);
            }
        }
        else if (resourceName.equals("relations")) {
            try {
                content.setContent(new ByteArrayInputStream(
                    retrieveContentRelations(id).getBytes(
                        XmlUtility.CHARACTER_ENCODING)));
                return content;
            }
            catch (UnsupportedEncodingException e) {
                throw new WebserverSystemException(e);
            }
        }
        else {
            throw new OperationNotFoundException(
                "no virtual resource with that name defined");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ContextHandlerInterface#retrieveResources(java.lang.String)
     */
    public String retrieveResources(final String id)
        throws ContextNotFoundException, SystemException {

        setContext(id);
        return getResourcesXml(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ContextHandlerInterface#retrieveProperties(java.lang.String)
     * 
     * @oum
     */
    public String retrieveProperties(final String id)
        throws ContextNotFoundException, SystemException {

        setContext(id);
        return getPropertiesXml(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.business.interfaces.ContextHandlerInterface#
     * retrieveContexts(java.lang.String)
     */
    public String retrieveContexts(final String filterString)
        throws InvalidSearchQueryException, InvalidXmlException,
        SystemException {
        return retrieveContexts((Object) filterString);
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.business.interfaces.ContextHandlerInterface#
     * retrieveContexts(java.util.Map)
     */
    public String retrieveContexts(final Map<String, String[]> filter)
        throws InvalidSearchQueryException, SystemException {
        String result = null;

        try {
            result = retrieveContexts((Object) filter);
        }
        catch (InvalidXmlException e) {
            // cannot happen here
        }
        return result;
    }

    /**
     * (non-Javadoc).
     */
    private String retrieveContexts(final Object filterObject)
        throws InvalidSearchQueryException, InvalidXmlException,
        SystemException {
        String result = null;
        FilterInterface filter = null;
        String format = null;
        boolean explain = false;

        if (filterObject instanceof String) {
            TaskParamHandler taskParameter =
                XmlUtility.parseTaskParam((String) filterObject, false);

            filter = new XmlFilter((String) filterObject);
            format = taskParameter.getFormat();
        }
        else if (filterObject instanceof Map<?, ?>) {
            SRURequest parameters =
                new SRURequest((Map<String, String[]>) filterObject);

            if (parameters.query != null) {
                filter = new CqlFilter(parameters.query);
            }
            else {
                filter = new CqlFilter();
            }
            filter.setLimit(parameters.limit);
            filter.setOffset(parameters.offset);
            format = "srw";
            explain = parameters.explain;
        }
        filter.setObjectType(ResourceType.CONTEXT);

        if ((format == null) || (format.length() == 0)
            || (format.equalsIgnoreCase("full"))) {
            StringWriter output = new StringWriter();
            String restRootAttributes = "";

            if (UserContext.isRestAccess()) {
                restRootAttributes =
                    "xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                        + "xlink:type=\"simple\" xlink:title=\"list of "
                        + "contexts\" xml:base=\""
                        + XmlUtility.getEscidocBaseUrl() + "\" ";
            }
            output.write("<?xml version=\"1.0\" encoding=\""
                + XmlUtility.CHARACTER_ENCODING
                + "\"?>"
                + "<context-list:context-list xmlns:context-list=\""
                + Constants.CONTEXT_LIST_NAMESPACE_URI
                + "\" "
                + restRootAttributes
                + "limit=\""
                + filter.getLimit()
                + "\" offset=\""
                + filter.getOffset()
                + "\" number-of-records=\""
                + getContextCache().getNumberOfRecords(
                    getUtility().getCurrentUserId(), filter) + "\">");
            getContextCache().getResourceList(output,
                getUtility().getCurrentUserId(), filter, null);
            output.write("</context-list:context-list>");
            result = output.toString();
        }
        else if ((format != null) && (format.equalsIgnoreCase("deleteParam"))) {
            BufferedReader reader = null;

            try {
                StringBuffer idList = new StringBuffer();
                StringWriter output = new StringWriter();

                getContextCache().getResourceIds(output,
                    getUtility().getCurrentUserId(), filter);
                reader =
                    new BufferedReader(new StringReader(output.toString()));

                String id;

                while ((id = reader.readLine()) != null) {
                    idList.append("<id>");
                    idList.append(id);
                    idList.append("</id>\n");
                }
                result = "<param>" + idList.toString() + "</param>";
            }
            catch (IOException e) {
                throw new SystemException(e);
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException e) {
                    }
                }
            }
        }
        else if ((format != null) && (format.equalsIgnoreCase("srw"))) {
            if (explain) {
                Map<String, Object> values = new HashMap<String, Object>();

                values.put("PROPERTY_NAMES", getContextCache()
                    .getPropertyNames());
                result =
                    ExplainXmlProvider.getInstance().getExplainContextXml(
                        values);
            }
            else {
                StringWriter output = new StringWriter();
                long numberOfRecords =
                    getContextCache().getNumberOfRecords(
                        getUtility().getCurrentUserId(), filter);

                output.write("<?xml version=\"1.0\" encoding=\""
                    + XmlUtility.CHARACTER_ENCODING + "\"?>"
                    + "<zs:searchRetrieveResponse "
                    + "xmlns:zs=\"http://www.loc.gov/zing/srw/\">"
                    + "<zs:version>1.1</zs:version>" + "<zs:numberOfRecords>"
                    + numberOfRecords + "</zs:numberOfRecords>");
                if (numberOfRecords > 0) {
                    output.write("<zs:records>");
                }
                getContextCache().getResourceList(output,
                    getUtility().getCurrentUserId(), filter, "srw");
                if (numberOfRecords > 0) {
                    output.write("</zs:records>");
                }
                output.write("</zs:searchRetrieveResponse>");
                result = output.toString();
            }
        }
        else {
            // FIXME exception type
            throw new WebserverSystemException("Invalid list format.");
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.business.interfaces.ContextHandlerInterface#
     * retrieveMembers(java.lang.String, java.lang.String)
     */
    public String retrieveMembers(final String id, final String filterString)
        throws ContextNotFoundException, InvalidSearchQueryException,
        InvalidXmlException, SystemException {
        return retrieveMembers(id, (Object) filterString);
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.business.interfaces.ContextHandlerInterface#
     * retrieveMembers(java.lang.String, java.util.Map)
     */
    public String retrieveMembers(
        final String id, final Map<String, String[]> filter)
        throws ContextNotFoundException, InvalidSearchQueryException,
        SystemException {
        String result = null;

        try {
            result = retrieveMembers(id, (Object) filter);
        }
        catch (InvalidXmlException e) {
            // cannot happen here
        }
        return result;
    }

    /**
     * (non-Javadoc).
     */
    private String retrieveMembers(final String id, final Object filterObject)
        throws ContextNotFoundException, InvalidSearchQueryException,
        InvalidXmlException, SystemException {
        String result = null;
        FilterInterface filter = null;
        StringWriter output = new StringWriter();
        String format = null;
        boolean explain = false;

        Utility.getInstance().checkIsContext(id);

        if (filterObject instanceof String) {
            TaskParamHandler taskParameter =
                XmlUtility.parseTaskParam((String) filterObject, false);

            filter = new XmlFilter((String) filterObject);
            format = taskParameter.getFormat();
        }
        else if (filterObject instanceof Map<?, ?>) {
            SRURequest parameters =
                new SRURequest((Map<String, String[]>) filterObject);

            if (parameters.query != null) {
                filter = new CqlFilter(parameters.query);
            }
            else {
                filter = new CqlFilter();
            }
            filter.setLimit(parameters.limit);
            filter.setOffset(parameters.offset);
            format = "srw";
            explain = parameters.explain;
        }

        filter.addRestriction(TripleStoreUtility.PROP_CONTEXT_ID, id);

        if ((format == null) || (format.length() == 0)
            || (format.equalsIgnoreCase("full"))) {
            String restRootAttributes = "";

            if (UserContext.isRestAccess()) {
                restRootAttributes =
                    "xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                        + "xlink:type=\"simple\" xlink:title=\"list of members\" ";
            }
            output
                .write("<?xml version=\"1.0\" encoding=\""
                    + XmlUtility.CHARACTER_ENCODING
                    + "\"?>"
                    + "<member-list:member-list xmlns:member-list=\""
                    + Constants.MEMBER_LIST_NAMESPACE_URI
                    + "\" "
                    + restRootAttributes
                    + "limit=\""
                    + filter.getLimit()
                    + "\" offset=\""
                    + filter.getOffset()
                    + "\" number-of-records=\""
                    + (getItemCache().getNumberOfRecords(
                        getUtility().getCurrentUserId(), filter) + getContainerCache()
                        .getNumberOfRecords(getUtility().getCurrentUserId(),
                            filter)) + "\">");
            getItemCache().getResourceList(output,
                getUtility().getCurrentUserId(), filter, null);
            getContainerCache().getResourceList(output,
                getUtility().getCurrentUserId(), filter, null);
            output.write("</member-list:member-list>");
            result = output.toString();
        }
        else if ((format != null) && (format.equalsIgnoreCase("srw"))) {
            if (explain) {
                Map<String, Object> values = new HashMap<String, Object>();
                Set<String> propertyNames = getItemCache().getPropertyNames();

                propertyNames.addAll(getContainerCache().getPropertyNames());
                values.put("PROPERTY_NAMES", propertyNames);
                result =
                    ExplainXmlProvider
                        .getInstance().getExplainContextMembersXml(values);
            }
            else {
                long numberOfRecords =
                    getItemCache().getNumberOfRecords(
                        getUtility().getCurrentUserId(), filter)
                        + getContainerCache().getNumberOfRecords(
                            getUtility().getCurrentUserId(), filter);

                output.write("<?xml version=\"1.0\" encoding=\""
                    + XmlUtility.CHARACTER_ENCODING + "\"?>"
                    + "<zs:searchRetrieveResponse "
                    + "xmlns:zs=\"http://www.loc.gov/zing/srw/\">"
                    + "<zs:version>1.1</zs:version>" + "<zs:numberOfRecords>"
                    + numberOfRecords + "</zs:numberOfRecords>");
                if (numberOfRecords > 0) {
                    output.write("<zs:records>");
                }
                getItemCache().getResourceList(output,
                    getUtility().getCurrentUserId(), filter, "srw");
                getContainerCache().getResourceList(output,
                    getUtility().getCurrentUserId(), filter, "srw");
                if (numberOfRecords > 0) {
                    output.write("</zs:records>");
                }
                output.write("</zs:searchRetrieveResponse>");
                result = output.toString();
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.business.interfaces.ContextHandlerInterface#
     * retrieveAdminDescriptor(java.lang.String, java.lang.String)
     */
    public String retrieveAdminDescriptor(final String id, final String name)
        throws ContextNotFoundException, SystemException,
        AdminDescriptorNotFoundException {

        setContext(id);
        return (getAdminDescriptorXml(this, name, true));
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.business.interfaces.ContextHandlerInterface#
     * retrieveAdminDescriptors(java.lang.String)
     */
    public String retrieveAdminDescriptors(final String id)
        throws ContextNotFoundException, SystemException {

        setContext(id);
        return (getAdminDescriptorsXml(this));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.interfaces.ContextHandlerInterface#update
     * (java.lang.String, java.lang.String)
     */
    public String update(final String id, final String xmlData)
        throws ContextNotFoundException, InvalidStatusException,
        OptimisticLockingException, ReadonlyAttributeViolationException,
        ReadonlyElementViolationException, SystemException,
        ContextNameNotUniqueException, MissingElementValueException,
        InvalidContentException {

        setContext(id);
        String context = null;
        if (update(this, xmlData)) {
            // otherwise we get the pre-update version
            setContext(id);
            context = getContextXml(this);
            fireContextModified(id, context);
        }
        else {
            context = getContextXml(this);
        }

        return context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.interfaces.ContextHandlerInterface#open(java
     * .lang.String, java.lang.String)
     */
    public String open(final String id, final String taskParam)
        throws ContextNotFoundException, InvalidStatusException,
        InvalidXmlException, OptimisticLockingException, SystemException,
        LockingException, StreamNotFoundException {

        setContext(id);
        open(this, taskParam);
        fireContextModified(id, retrieve(getContext().getId()));

        return getUtility().prepareReturnXml(
            getContext().getLastModificationDate());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.interfaces.ContextHandlerInterface#close(
     * java.lang.String, java.lang.String)
     */
    public String close(final String id, final String taskParam)
        throws ContextNotFoundException, OptimisticLockingException,
        InvalidXmlException, InvalidStatusException, SystemException,
        LockingException, StreamNotFoundException {

        setContext(id);
        close(this, taskParam);
        fireContextModified(id, retrieve(getContext().getId()));

        return getUtility().prepareReturnXml(
            getContext().getLastModificationDate());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.interfaces.ContextHandlerInterface#delete
     * (java.lang.String)
     */
    public void delete(final String id) throws ContextNotEmptyException,
        ContextNotFoundException, InvalidStatusException, SystemException {

        setContext(id);
        remove(this);

        fireContextDeleted(id);
    }

    /**
     * Get filtered Contexts list with full Context representations.
     * 
     * @param filter
     *            Contexts filter.
     * @return Filtered Contexts.
     * @throws SystemException
     *             Thrown if anything else fails.
     */
    public String getContexts(final String filterXml) throws SystemException {

        StringBuffer sb = new StringBuffer();
        List<String> contextIds = null;

        Map filterMap = XmlUtility.getFilterMap(filterXml);

        String userCriteria = null;
        String roleCriteria = null;
        String whereClause = null;
        if (filterMap != null) {
            // filter out user permissions
            userCriteria = (String) filterMap.get("user");
            roleCriteria = (String) filterMap.get("role");

            try {
                whereClause =
                    getPdp().getRoleUserWhereClause("container", userCriteria,
                        roleCriteria).toString();
            }
            catch (final SystemException e) {
                // FIXME: throw SystemException?
                throw new SystemException(
                    "Failed to retrieve clause for user and role criteria", e);
            }
            catch (MissingMethodParameterException e) {
                throw new SystemException(
                    "Failed to retrieve clause for user and role criteria", e);
            }
        }

        try {
            List<String> list =
                TripleStoreUtility
                    .getInstance().evaluate(Constants.CONTEXT_OBJECT_TYPE,
                        filterMap, null, whereClause);

            try {
                contextIds = getPdp().evaluateRetrieve("context", list);
            }
            catch (final Exception e) {
                throw new WebserverSystemException(e);
            }
        }
        catch (MissingMethodParameterException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        Iterator<String> it = contextIds.iterator();
        while (it.hasNext()) {
            try {
                sb.append(retrieve(it.next()));
            }
            catch (ContextNotFoundException e) {
                throw new SystemException(
                    "Should not occur in FedoraContextHandler.retrieveContexts",
                    e);
            }
            catch (EncodingSystemException e) {
                throw new SystemException(
                    "Should not occur in FedoraContextHandler.retrieveContexts",
                    e);
            }
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.escidoc.core.om.business.interfaces.ContextHandlerInterface#
     * updateAdminDescriptor(java.lang.String, java.lang.String)
     */
    public String updateAdminDescriptor(final String id, final String xmlData)
        throws ContextNotFoundException, OptimisticLockingException,
        AdminDescriptorNotFoundException {

        // check status(closed)
        // FIXME implement updateAdminDescriptor
        log.error("FedoraContextHandler.updateAdminDescriptor "
            + "not yet implemented");
        throw new UnsupportedOperationException(
            "FedoraContextHandler.updateAdminDescriptor not implemented yet");
    }

    /**
     * Notify the listeners that a context was modified.
     * 
     * @param id
     *            context id
     * @param xmlData
     *            complete context XML
     * 
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    private void fireContextModified(final String id, final String xmlData)
        throws SystemException {
        String restXml = null;
        String soapXml = null;

        if (UserContext.isRestAccess()) {
            restXml = xmlData;
            soapXml = getAlternateForm(id);
        }
        else {
            restXml = getAlternateForm(id);
            soapXml = xmlData;
        }
        for (int index = 0; index < contextListeners.size(); index++) {
            (contextListeners.get(index))
                .resourceModified(id, restXml, soapXml);
        }
    }

    /**
     * Notify the listeners that an context was created.
     * 
     * @param id
     *            context id
     * @param xmlData
     *            complete context XML
     * 
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    private void fireContextCreated(final String id, final String xmlData)
        throws SystemException {
        String restXml = null;
        String soapXml = null;

        if (UserContext.isRestAccess()) {
            restXml = xmlData;
            soapXml = getAlternateForm(id);
        }
        else {
            restXml = getAlternateForm(id);
            soapXml = xmlData;
        }
        for (int index = 0; index < contextListeners.size(); index++) {
            (contextListeners.get(index)).resourceCreated(id, restXml, soapXml);
        }
    }

    /**
     * Notify the listeners that an context was deleted.
     * 
     * @param id
     *            context id
     * 
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    private void fireContextDeleted(final String id) throws SystemException {
        for (int index = 0; index < contextListeners.size(); index++) {
            (contextListeners.get(index)).resourceDeleted(id);
        }
    }

    /**
     * Get the alternate form of a context representation. If the current
     * request came in via REST, then the SOAP form will be returned here and
     * vice versa.
     * 
     * @param id
     *            context id
     * 
     * @return alternate form of the context
     * @throws SystemException
     *             An internal error occurred.
     */
    private String getAlternateForm(final String id) throws SystemException {
        String result = null;
        boolean isRestAccess = UserContext.isRestAccess();

        try {
            if (isRestAccess) {
                UserContext.setRestAccess(false);
                result = getContextXml(this);
            }
            else {
                UserContext.setRestAccess(true);
                result = getContextXml(this);
            }
        }
        catch (WebserverSystemException e) {
            throw new SystemException(e);
        }
        catch (Exception e) {
            // should not happen here
            throw new SystemException(e);
        }
        finally {
            UserContext.setRestAccess(isRestAccess);
        }
        return result;
    }

    /**
     * Retrieve all content relation in which the current resource is subject or
     * object.
     * 
     * @param id
     *            context id
     * 
     * @return list of content relations
     * @throws ContextNotFoundException
     *             Thrown if an item with the specified id could not be found.
     * @throws SystemException
     *             If an error occurs.
     */
    private String retrieveContentRelations(final String id)
        throws ContextNotFoundException, SystemException {
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        String result = null;

        setContext(id);
        filterParams.put("query", new String[] { "\"/subject/id\"="
            + getContext().getId() + " or " +
            // "\"/subject/id\"=" + getContext().getFullId() + " or " +
            // "\"/object/id\"=" + getContext().getFullId() + " or " +
            "\"/object/id\"=" + getContext().getId() });

        try {
            String searchResponse =
                contentRelationHandler.retrieveContentRelations(filterParams);
            result = transformSearchResponse2relations(searchResponse);
        }
        catch (InvalidSearchQueryException e) {
            throw new SystemException(e);
        }

        return result;

    }

    /**
     * Injects the content relation handler.
     * 
     * @param contentRelationHandler
     *            The {@link FedoraContentRelationHandler}.
     * @spring.property ref="business.FedoraContentRelationHandler"
     * 
     */
    public void setContentRelationHandler(
        final FedoraContentRelationHandler contentRelationHandler) {
        this.contentRelationHandler = contentRelationHandler;
    }

    /**
     * See Interface for functional description.
     * 
     * @param fedoraUtility
     *            Fedora utility
     * @see de.escidoc.core.common.business.fedora.HandlerBase
     *      #setFedoraUtility(de.escidoc.core.common.business.fedora.FedoraUtility)
     * 
     * @spring.property ref="escidoc.core.business.FedoraUtility"
     */
    @Override
    public void setFedoraUtility(final FedoraUtility fedoraUtility) {

        super.setFedoraUtility(fedoraUtility);
    }

    /**
     * See Interface for functional description.
     * 
     * @param idProvider
     *            id provider
     * @see de.escidoc.core.common.business.fedora.HandlerBase
     *      #setIdProvider(de.escidoc.core.common.persistence.EscidocIdProvider)
     * 
     * @spring.property ref="escidoc.core.business.EscidocIdProvider"
     */
    @Override
    public void setIdProvider(final EscidocIdProvider idProvider) {

        super.setIdProvider(idProvider);
    }

    /**
     * Get the container cache.
     * 
     * @return The ContainerCache.
     * 
     * @throws WebserverSystemException
     *             Thrown if a framework internal error occurs.
     */
    private ResourceCacheInterface getContainerCache()
        throws WebserverSystemException {
        if (containerCache == null) {
            containerCache =
                (ResourceCacheInterface) BeanLocator.getBean(
                    BeanLocator.AA_FACTORY_ID, "container.DbContainerCache");
        }
        return containerCache;
    }

    /**
     * Get the context cache.
     * 
     * @return context cache
     */
    private ResourceCacheInterface getContextCache()
        throws WebserverSystemException {
        if (contextCache == null) {
            contextCache =
                (ResourceCacheInterface) BeanLocator.getBean(
                    BeanLocator.AA_FACTORY_ID, "context.DbContextCache");
            addContextListener(contextCache);
        }
        return contextCache;
    }

    /**
     * Get the item cache.
     * 
     * @return The ItemCache.
     * 
     * @throws WebserverSystemException
     *             Thrown if a framework internal error occurs.
     */
    private ResourceCacheInterface getItemCache()
        throws WebserverSystemException {
        if (itemCache == null) {
            itemCache =
                (ResourceCacheInterface) BeanLocator.getBean(
                    BeanLocator.AA_FACTORY_ID, "item.DbItemCache");
        }
        return itemCache;
    }

    /**
     * Register an context listener.
     * 
     * @param listener
     *            listener which will be added to the list
     */
    public void addContextListener(final ResourceListener listener) {
        contextListeners.add(listener);
    }
}
