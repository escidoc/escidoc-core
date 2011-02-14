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

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.listener.ResourceListener;
import de.escidoc.core.common.business.filter.LuceneRequestParameters;
import de.escidoc.core.common.business.filter.SRURequest;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.business.indexing.IndexingHandler;
import de.escidoc.core.common.exceptions.application.invalid.ContextNotEmptyException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
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
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.business.fedora.contentRelation.FedoraContentRelationHandler;
import de.escidoc.core.om.business.interfaces.ContextHandlerInterface;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @spring.bean id="business.FedoraContextHandler" scope="prototype"
 * @author FRS
 * 
 */
public class FedoraContextHandler extends ContextHandlerUpdate
    implements ContextHandlerInterface {

    private static final AppLogger LOG = new AppLogger(
        FedoraContextHandler.class.getName());

    private final List<ResourceListener> contextListeners =
        new Vector<ResourceListener>();

    private FedoraContentRelationHandler contentRelationHandler;

    /** The policy decision point used to check access privileges. */
    private PolicyDecisionPointInterface pdp;

    /** SRU request. */
    private SRURequest sruRequest = null;

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
     * returns either the representation of the resource or its id.
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
            LOG.error("Created resource not found.", e);
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
        final String id, final String resourceName,
        final Map<String, String[]> parameters)
        throws OperationNotFoundException, ContextNotFoundException,
        SystemException {

        EscidocBinaryContent content = new EscidocBinaryContent();
        content.setMimeType("text/xml");

        if (resourceName.equals("members")) {
            try {
                content.setContent(new ByteArrayInputStream(retrieveMembers(id,
                    new LuceneRequestParameters(parameters)).getBytes(
                    XmlUtility.CHARACTER_ENCODING)));
                return content;
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

    /**
     * (non-Javadoc).
     */
    public String retrieveContexts(final SRURequestParameters parameters)
        throws SystemException {
        StringWriter result = new StringWriter();

        if (parameters.isExplain()) {
            sruRequest.explain(result, ResourceType.CONTEXT);
        }
        else {
            sruRequest.searchRetrieve(result,
                new ResourceType[] { ResourceType.CONTEXT }, parameters.getQuery(),
                parameters.getLimit(), parameters.getOffset(), parameters.getUser(),
                parameters.getRole());
        }
        return result.toString();
    }

    /**
     * (non-Javadoc).
     */
    public String retrieveMembers(
        final String id, final SRURequestParameters parameters)
        throws ContextNotFoundException, SystemException {
        StringWriter result = new StringWriter();

        Utility.getInstance().checkIsContext(id);
        if (parameters.isExplain()) {
            // Items and containers are in the same index.
            sruRequest.explain(result, ResourceType.ITEM);
        }
        else {
            String query = "\"/properties/context/id\"=" + id;

            if (parameters.getQuery() != null) {
                query += " AND " + parameters.getQuery();
            }
            sruRequest.searchRetrieve(result, new ResourceType[] {
                ResourceType.CONTAINER, ResourceType.ITEM }, query,
                parameters.getLimit(), parameters.getOffset(), parameters.getUser(),
                parameters.getRole());
        }
        return result.toString();
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

        return getUtility().prepareReturnXmlFromLastModificationDate(
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

        return getUtility().prepareReturnXmlFromLastModificationDate(
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
        LOG.error("FedoraContextHandler.updateAdminDescriptor "
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

        String searchResponse =
            contentRelationHandler
                .retrieveContentRelations(new LuceneRequestParameters(
                    filterParams));
        result = transformSearchResponse2relations(searchResponse);

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
     * Set the SRURequest object.
     * 
     * @param sruRequest
     *            SRURequest
     * 
     * @spring.property ref="de.escidoc.core.common.business.filter.SRURequest"
     */
    public void setSruRequest(final SRURequest sruRequest) {
        this.sruRequest = sruRequest;
    }

    /**
     * Injects the triple store utility bean.
     * 
     * @param tsu
     *            The {@link TripleStoreUtility}.
     * @spring.property ref="business.TripleStoreUtility"
     * 
     */
    public void setTripleStoreUtility(final TripleStoreUtility tsu) {
        super.setTripleStoreUtility(tsu);
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
     * Injects the indexing handler.
     * 
     * @spring.property ref="common.business.indexing.IndexingHandler"
     * @param indexingHandler
     *            The indexing handler.
     */
    public void setIndexingHandler(final IndexingHandler indexingHandler) {
        addContextListener(indexingHandler);
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
