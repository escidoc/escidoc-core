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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.stax.handler.context.DcUpdateHandler;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.PidAlreadyAssignedException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.AddNewSubTreesToDatastream;
import de.escidoc.core.common.util.stax.handler.ItemRelsExtUpdateHandler;
import de.escidoc.core.common.util.stax.handler.MultipleExtractor;
import de.escidoc.core.common.util.stax.handler.OptimisticLockingHandler;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithText;
import de.escidoc.core.om.business.stax.handler.context.ContextPropertiesUpdateHandler;

/**
 * 
 * @author SWA
 * 
 */
public class ContextHandlerUpdate extends ContextHandlerDelete {

    private static AppLogger log = new AppLogger(
        ContextHandlerUpdate.class.getName());

    private static final String XPATH_ADMIN_DESCRIPTORS =
        "/context/admin-descriptors/admin-descriptor";

    private static final String XPATH_RESOURCES = "/context/resources";

    /**
     * Update Context.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @param xmlData
     *            Context update XML representation.
     * @return if resource was udated true, false otherwise
     * 
     * @throws ContextNotFoundException
     *             Thrown if Context could not be found.
     * @throws InvalidStatusException
     *             Thrown if context is in invalid status.
     * @throws OptimisticLockingException
     *             Thrown if context resource is altered on update.
     * @throws ReadonlyAttributeViolationException
     *             Thrown if read-only attributes should be altered.
     * @throws ReadonlyElementViolationException
     *             Thrown if read-only elements should be altered.
     * @throws ContextNameNotUniqueException
     *             Thrown if new name of context is not unique.
     * @throws MissingElementValueException
     *             Thrown if value of element is missing.
     * @throws InvalidContentException
     *             Thrown if the xmlData parameter has invalid content.
     * @throws SystemException
     *             Thrown if anything else fails.
     * @throws InvalidContentException
     */
    public boolean update(
        final FedoraContextHandler contextHandler, final String xmlData)
        throws ContextNotFoundException, InvalidStatusException,
        OptimisticLockingException, ReadonlyAttributeViolationException,
        ReadonlyElementViolationException, ContextNameNotUniqueException,
        MissingElementValueException, SystemException, InvalidContentException {

        final String startTimeStamp =
            getContext().getLastFedoraModificationDate();
        final StaxParser sp = new StaxParser();
        sp.addHandler(new OptimisticLockingHandler(getContext().getId(),
            Constants.CONTEXT_OBJECT_TYPE, startTimeStamp, sp));
        final ContextPropertiesUpdateHandler cpuh =
            new ContextPropertiesUpdateHandler(getContext().getId(), sp);
        sp.addHandler(cpuh);

        final String status = getContext().getStatus();

        if (status.equals(Constants.STATUS_CONTEXT_CLOSED)) {
            throw new InvalidStatusException("Status is closed");
        }

        final HashMap<String, String> extractPathes =
            new HashMap<String, String>();

        extractPathes.put(XPATH_RESOURCES, null);
        extractPathes.put(XPATH_ADMIN_DESCRIPTORS, Elements.ATTRIBUTE_NAME);

        final MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
        sp.addHandler(me);

        try {
            sp.parse(xmlData);
        }
        catch (final TripleStoreSystemException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final LockingException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final ContentModelNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final MissingContentException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final MissingAttributeValueException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final InvalidContentException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final AlreadyExistsException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final ReferencedResourceNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final RelationPredicateNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final OrganizationalUnitNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final ContentRelationNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final XMLStreamException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final PidAlreadyAssignedException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final InvalidXmlException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final MissingMdRecordException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final TmeException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        final HashMap<String, Object> streams = me.getOutputStreams();
        streams.remove("resources");

        boolean adminDescriptorsUpdated = handleAdminDescriptors(streams);

        getContext().setOrganizationalUnits(cpuh.getOrganizationalUnits());

        // RELS-EXT ----------------------------------------

        boolean dcUpdated =
            updateDc(cpuh.getChangedValuesInDc(), cpuh.getPropertiesToRemove(),
                cpuh.getPropertiesToAdd());
        HashMap<String, String> changedValues =
            cpuh.getChangedValuesInRelsExt();
        if (changedValues.size() > 0 || dcUpdated || adminDescriptorsUpdated
            || getContext().isOuUpdated()) {
            String oldModifiedBy =
                getTripleStoreUtility().getProperty(getContext().getId(),
                    Constants.STRUCTURAL_RELATIONS_NS_URI + "modified-by");
            final String[] currentUser = getUtility().getCurrentUser();
            if (!oldModifiedBy.equals(currentUser[0])) {
                changedValues.put("modifiedBy", currentUser[0]);
                changedValues.put("modifiedByTitle", currentUser[1]);
            }
            String buildNumber = Utility.getInstance().getBuildNumber();
            changedValues.put("build", buildNumber);
            updateRelsExt(changedValues);
            getContext().persist();
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Set Context status to open.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @param taskParam
     *            The parameter structure.
     * @throws ContextNotFoundException
     *             Thrown if Context resource could not be found.
     * @throws InvalidStatusException
     *             Thrown if Context has invalid status.
     * @throws InvalidXmlException
     *             Thrown if parameter is invalid XML.
     * @throws OptimisticLockingException
     *             Thrown if context resource is altered on open.
     * @throws SystemException
     *             Thrown if anything else fails.
     * @throws StreamNotFoundException
     *             Thrown if RELS-EXT datastream could not be retrieved.
     * @throws LockingException
     *             Thrown if Context is locked.
     */
    public void open(
        final FedoraContextHandler contextHandler, final String taskParam)
        throws ContextNotFoundException, InvalidStatusException,
        InvalidXmlException, OptimisticLockingException, SystemException,
        LockingException, StreamNotFoundException {

        checkStatus(Constants.STATUS_CONTEXT_CREATED);
        TaskParamHandler taskParamHandler = null;
        try {
            taskParamHandler = XmlUtility.parseTaskParam(taskParam);
        }
        catch (final SystemException e) {
            throw new XmlCorruptedException(e.getMessage(), e);
        }

        getUtility().checkOptimisticLockingCriteria(
            getContext().getLastModificationDate(),
            taskParamHandler.getLastModificationDate(),
            "Context " + getContext().getId());

        final Map<String, StartElementWithChildElements> updateElementsRelsExt =
            new TreeMap<String, StartElementWithChildElements>();

        final StartElementWithChildElements modifiedBy =
            new StartElementWithChildElements(Elements.ELEMENT_MODIFIED_BY,
                Constants.STRUCTURAL_RELATIONS_NS_URI,
                Constants.STRUCTURAL_RELATIONS_NS_PREFIX, null, getUtility()
                    .getCurrentUserId(), null);
        final Attribute resourceAttribute =
            new Attribute("resource", Constants.RDF_NAMESPACE_URI,
                Constants.RDF_NAMESPACE_PREFIX, "info:fedora/"
                    + getUtility().getCurrentUserId());
        modifiedBy.addAttribute(resourceAttribute);
        updateElementsRelsExt.put(Elements.ELEMENT_MODIFIED_BY, modifiedBy);

        updateElementsRelsExt.put(Elements.ELEMENT_MODIFIED_BY_TITLE,
            new StartElementWithChildElements(
                Elements.ELEMENT_MODIFIED_BY_TITLE,
                Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX,
                null, getUtility().getCurrentUserRealName(), null));

        String buildNumber = Utility.getInstance().getBuildNumber();
        updateElementsRelsExt.put("build", new StartElementWithChildElements(
            "build", "http://escidoc.de/core/01/system/", "system", null,
            buildNumber, null));

        updateElementsRelsExt.put(Elements.ELEMENT_PUBLIC_STATUS,
            new StartElementWithChildElements(Elements.ELEMENT_PUBLIC_STATUS,
                Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX,
                null, Constants.STATUS_CONTEXT_OPENED, null));

        String comment = taskParamHandler.getComment();
        if (comment == null || comment.length() == 0) {
            comment = "Context " + getContext().getId() + " opened.";
        }
        updateElementsRelsExt.put(Elements.ELEMENT_PUBLIC_STATUS_COMMENT,
            new StartElementWithChildElements(
                Elements.ELEMENT_PUBLIC_STATUS_COMMENT,
                Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX,
                null, comment, null));

        final StaxParser sp = new StaxParser();
        final ItemRelsExtUpdateHandler itemRelsExtUpdateHandler =
            new ItemRelsExtUpdateHandler(updateElementsRelsExt, sp);
        sp.addHandler(itemRelsExtUpdateHandler);
        final HashMap<String, String> pathes = new HashMap<String, String>();
        pathes.put("/RDF", null);
        final MultipleExtractor me = new MultipleExtractor(pathes, sp);
        sp.addHandler(me);

        try {
            sp.parse(getContext().getRelsExt().getStream());
        }
        catch (final Exception e) {
            throw new SystemException(e);
        }

        final ByteArrayOutputStream relsExt =
            (ByteArrayOutputStream) me.getOutputStreams().get("RDF");
        try {
            getContext().setRelsExt(
                relsExt.toString(XmlUtility.CHARACTER_ENCODING));
            getContext().persist();
        }
        catch (final UnsupportedEncodingException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Set Context status to close.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @param taskParam
     *            The parameter structure.
     * @throws ContextNotFoundException
     *             Thrown if Context resource could not be found.
     * @throws InvalidStatusException
     *             Thrown if Context has invalid status.
     * @throws InvalidXmlException
     *             Thrown if parameter is invalid XML.
     * @throws OptimisticLockingException
     *             Thrown if context resource is altered on open.
     * @throws SystemException
     *             Thrown if anything else fails.
     * @throws StreamNotFoundException
     *             Thrown if RELS-EXT datastream could not be retrieved.
     * @throws LockingException
     *             Thrown if Context is locked.
     */
    public void close(
        final FedoraContextHandler contextHandler, final String taskParam)
        throws ContextNotFoundException, InvalidStatusException,
        InvalidXmlException, OptimisticLockingException, SystemException,
        LockingException, StreamNotFoundException {

        checkStatus(Constants.STATUS_CONTEXT_OPENED);
        TaskParamHandler taskParamHandler = null;
        try {
            taskParamHandler = XmlUtility.parseTaskParam(taskParam);
        }
        catch (final SystemException e) {
            throw new XmlCorruptedException(e.getMessage(), e);
        }

        getUtility().checkOptimisticLockingCriteria(
            getContext().getLastModificationDate(),
            taskParamHandler.getLastModificationDate(),
            "Context " + getContext().getId());

        // update RELS-EXT
        final Map<String, StartElementWithChildElements> updateElementsRelsExt =
            new TreeMap<String, StartElementWithChildElements>();

        final StartElementWithChildElements modifiedBy =
            new StartElementWithChildElements(Elements.ELEMENT_MODIFIED_BY,
                Constants.STRUCTURAL_RELATIONS_NS_URI,
                Constants.STRUCTURAL_RELATIONS_NS_PREFIX, null, getUtility()
                    .getCurrentUserId(), null);
        final Attribute resourceAttribute =
            new Attribute("resource", Constants.RDF_NAMESPACE_URI,
                Constants.RDF_NAMESPACE_PREFIX, "info:fedora/"
                    + getUtility().getCurrentUserId());
        modifiedBy.addAttribute(resourceAttribute);
        updateElementsRelsExt.put(Elements.ELEMENT_MODIFIED_BY, modifiedBy);

        updateElementsRelsExt.put(Elements.ELEMENT_MODIFIED_BY_TITLE,
            new StartElementWithChildElements(
                Elements.ELEMENT_MODIFIED_BY_TITLE,
                Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX,
                null, getUtility().getCurrentUserRealName(), null));

        String buildNumber = Utility.getInstance().getBuildNumber();
        updateElementsRelsExt.put("build", new StartElementWithChildElements(
            "build", "http://escidoc.de/core/01/system/", "system", null,
            buildNumber, null));

        updateElementsRelsExt.put(Elements.ELEMENT_PUBLIC_STATUS,
            new StartElementWithChildElements(Elements.ELEMENT_PUBLIC_STATUS,
                Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX,
                null, Constants.STATUS_CONTEXT_CLOSED, null));

        String comment = taskParamHandler.getComment();
        if (comment == null || comment.length() == 0) {
            comment = "Context " + getContext().getId() + " closed.";
        }
        updateElementsRelsExt.put(Elements.ELEMENT_PUBLIC_STATUS_COMMENT,
            new StartElementWithChildElements(
                Elements.ELEMENT_PUBLIC_STATUS_COMMENT,
                Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX,
                null, comment, null));

        final StaxParser sp = new StaxParser();
        final ItemRelsExtUpdateHandler itemRelsExtUpdateHandler =
            new ItemRelsExtUpdateHandler(updateElementsRelsExt, sp);
        sp.addHandler(itemRelsExtUpdateHandler);
        final HashMap<String, String> pathes = new HashMap<String, String>();
        pathes.put("/RDF", null);
        final MultipleExtractor me = new MultipleExtractor(pathes, sp);
        sp.addHandler(me);

        try {
            sp.parse(getContext().getRelsExt().getStream());
        }
        catch (final Exception e) {
            throw new SystemException(e);
        }

        final ByteArrayOutputStream relsExt =
            (ByteArrayOutputStream) me.getOutputStreams().get("RDF");
        try {
            getContext().setRelsExt(
                relsExt.toString(XmlUtility.CHARACTER_ENCODING));
            getContext().persist();
        }
        catch (final UnsupportedEncodingException e) {
            throw new SystemException(e);
        }
    }

    // /**
    // * Close Context.
    // *
    // * @param contextHandler
    // * FedoraContextHandler
    // */
    // public void close(final FedoraContextHandler contextHandler) {
    // // TODO implement
    // throw new UnsupportedOperationException(
    // "ContextHandlerUpdate.close not yet implemented");
    // }

    /**
     * Update AdminDescriptor.
     * 
     * @param contextHandler
     *            FedoraContextHandler
     * @param xmlData
     *            XML representation of new AdminDescriptor.
     */
    public void updateAdminDescriptor(
        final FedoraContextHandler contextHandler, final String xmlData) {
        // TODO implement
        throw new UnsupportedOperationException(
            "ContextHandlerUpdate.updateAdminDescriptor not yet implemented");
    }

    /**
     * Replace updated values in RELS-EXT.
     * 
     * @param changedValues
     *            HashMap of changed values.
     * @throws XmlParserSystemException
     *             In case of parser error.
     * @throws ContextNameNotUniqueException
     *             In case of context name is already in use.
     * @throws WebserverSystemException
     *             In case of an internal error in the webserver.
     * @throws TripleStoreSystemException
     *             In case of an internal error in the triple store.
     */
    private void updateRelsExt(final HashMap<String, String> changedValues)
        throws XmlParserSystemException, ContextNameNotUniqueException,
        TripleStoreSystemException, WebserverSystemException {

        if (changedValues.size() < 1) {
            return;
        }

        final TreeMap<String, StartElementWithText> updateElementsRelsExt =
            new TreeMap<String, StartElementWithText>();

        final Iterator<String> it = changedValues.keySet().iterator();
        while (it.hasNext()) {
            final String key = it.next();
            if (key.equals("build")) {
                updateElementsRelsExt.put("build",
                    new StartElementWithChildElements(key,
                        "http://escidoc.de/core/01/system/", "system", null,
                        changedValues.get(key), null));
            }
            else if (key.equals("modifiedBy")) {
                StartElementWithChildElements modifiedBy =
                    new StartElementWithChildElements(
                        Elements.ELEMENT_MODIFIED_BY,
                        Constants.STRUCTURAL_RELATIONS_NS_URI,
                        Constants.STRUCTURAL_RELATIONS_NS_PREFIX, null, "",
                        null);
                Attribute resourceAttribute =
                    new Attribute("resource", Constants.RDF_NAMESPACE_URI,
                        Constants.RDF_NAMESPACE_PREFIX, "info:fedora/"
                            + changedValues.get(key));
                modifiedBy.addAttribute(resourceAttribute);
                updateElementsRelsExt.put(Elements.ELEMENT_MODIFIED_BY,
                    modifiedBy);
            }
            else if (key.equals("modifiedByTitle")) {
                updateElementsRelsExt.put(
                    Elements.ELEMENT_MODIFIED_BY_TITLE,
                    new StartElementWithChildElements(
                        Elements.ELEMENT_MODIFIED_BY_TITLE,
                        Constants.PROPERTIES_NS_URI,
                        Constants.PROPERTIES_NS_PREFIX, null, changedValues
                            .get(key), null));
            }
            else {
                updateElementsRelsExt.put(key, new StartElementWithText(key,
                    Constants.PROPERTIES_NS_URI,
                    Constants.PROPERTIES_NS_PREFIX, changedValues.get(key),
                    null));
            }
        }

        final StaxParser sp = new StaxParser();
        sp.clearHandlerChain();

        final ItemRelsExtUpdateHandler itemRelsExtUpdateHandler =
            new ItemRelsExtUpdateHandler(updateElementsRelsExt, sp);
        sp.addHandler(itemRelsExtUpdateHandler);

        final HashMap<String, String> pathes = new HashMap<String, String>();
        pathes.put("/RDF", null);
        final MultipleExtractor me = new MultipleExtractor(pathes, sp);
        sp.addHandler(me);

        try {
            sp.parse(getContext().getRelsExtAsString());
            final ByteArrayOutputStream relsExt =
                (ByteArrayOutputStream) me.getOutputStreams().get("RDF");
            getContext().setRelsExt(relsExt);
        }
        catch (final Exception e) {
            throw new XmlParserSystemException(e);
        }
    }

    /**
     * Replaces updated values in DC, removes/adds provided properties from/to
     * DC and write DC datastream to Fedora.
     * 
     * @return true if dc was updated, false otherwise.
     * @param changedValues
     *            HashMap of changed values.
     * @param propertiesToRemove
     *            properties to remove.
     * @param propertiesToAdd
     *            properties to add.
     * @throws ContextNameNotUniqueException
     *             In case of context name is already in use.
     * @throws SystemException
     *             In case of an internal error in the webserver.
     */
    private boolean updateDc(
        final HashMap<String, String> changedValues,
        final Vector<String> propertiesToRemove,
        final HashMap<String, String> propertiesToAdd)
        throws ContextNameNotUniqueException, SystemException {
        if (((changedValues == null) || (changedValues.size() == 0))
            && ((propertiesToRemove == null) || (propertiesToRemove.size() == 0))
            && ((propertiesToAdd == null) || (propertiesToAdd.size() == 0))) {
            return false;
        }
        boolean updatedDcProperties = false;
        Datastream dc = null;
        try {
            dc = getContext().getDc();
        }
        catch (final StreamNotFoundException e1) {
            throw new IntegritySystemException("Datastream dc not found.", e1);
        }
        ByteArrayInputStream dcIs;
        dcIs = new ByteArrayInputStream(dc.getStream());
        byte[] dcNewBytes = null;
        final StaxParser sp = new StaxParser();

        if (propertiesToRemove.size() > 0) {

            if (changedValues.size() > 0) {
                updatedDcProperties = true;
                final TreeMap<String, StartElementWithText> updateElementsDc =
                    updateDcProperties(changedValues);

                final DcUpdateHandler dcUpdateHandler =
                    new DcUpdateHandler(updateElementsDc, sp);

                sp.addHandler(dcUpdateHandler);
            }
            final HashMap<String, String> extractPathes =
                new HashMap<String, String>();
            final MultipleExtractor me =
                new MultipleExtractor(extractPathes, sp);
            extractPathes.put("/dc", null);
            sp.addHandler(me);

            final TreeMap<String, Vector<StartElementWithChildElements>> toRemove =
                new TreeMap<String, Vector<StartElementWithChildElements>>();
            final Iterator<String> iterator = propertiesToRemove.iterator();
            HashMap<String, Vector<StartElementWithChildElements>> propertiesVectorAssignment =
                new HashMap<String, Vector<StartElementWithChildElements>>();
            while (iterator.hasNext()) {
                final String property = iterator.next();

                final StartElementWithChildElements propertyToDelete =
                    new StartElementWithChildElements();
                propertyToDelete.setLocalName(property);
                propertyToDelete.setPrefix(Constants.DC_NS_PREFIX);
                propertyToDelete.setNamespace(Constants.DC_NS_URI);
                propertyToDelete.setChildrenElements(null);

                if (propertiesVectorAssignment.containsKey(property)) {
                    Vector<StartElementWithChildElements> vector =
                        propertiesVectorAssignment.get(property);
                    vector.add(propertyToDelete);
                }
                else {
                    Vector<StartElementWithChildElements> vector =
                        new Vector<StartElementWithChildElements>();
                    vector.add(propertyToDelete);
                    propertiesVectorAssignment.put(property, vector);
                }
            }
            Set<String> keySet = propertiesVectorAssignment.keySet();
            Iterator<String> iteratorKeys = keySet.iterator();
            while (iteratorKeys.hasNext()) {
                String property = iteratorKeys.next();
                Vector<StartElementWithChildElements> elements =
                    propertiesVectorAssignment.get(property);
                toRemove.put("/dc/" + property, elements);

            }
            me.removeElements(toRemove);

            try {
                sp.parse(dcIs);
                sp.clearHandlerChain();
                final ByteArrayOutputStream dcUpdated =
                    (ByteArrayOutputStream) me.getOutputStreams().get("dc");
                dcNewBytes = dcUpdated.toByteArray();

            }
            catch (final Exception e) {
                throw new XmlParserSystemException(e);
            }
        }
        if (propertiesToAdd.size() > 0) {

            if (!updatedDcProperties && changedValues.size() > 0) {
                updatedDcProperties = true;

                final TreeMap<String, StartElementWithText> updateElementsDc =
                    updateDcProperties(changedValues);

                final DcUpdateHandler dcUpdateHandler =
                    new DcUpdateHandler(updateElementsDc, sp);

                sp.addHandler(dcUpdateHandler);

            }

            final AddNewSubTreesToDatastream addNewEntriesHandler =
                new AddNewSubTreesToDatastream("/dc", sp);
            final Vector<StartElementWithChildElements> elementsToAdd =
                new Vector<StartElementWithChildElements>();

            final Set<String> keysToAdd = propertiesToAdd.keySet();
            final Iterator<String> iterator = keysToAdd.iterator();
            while (iterator.hasNext()) {
                final StartElementWithChildElements newPropertyElement =
                    new StartElementWithChildElements();
                final String propertyKey = iterator.next();
                newPropertyElement.setLocalName(propertyKey);
                newPropertyElement
                    .setPrefix(de.escidoc.core.common.business.Constants.DC_NS_PREFIX);
                newPropertyElement
                    .setNamespace(de.escidoc.core.common.business.Constants.DC_NS_URI);
                newPropertyElement.setElementText(propertiesToAdd
                    .get(propertyKey));
                elementsToAdd.add(newPropertyElement);
            }
            final StartElement pointer = new StartElement();
            pointer.setLocalName("dc");
            pointer.setPrefix(Constants.OAI_DC_NS_PREFIX);
            pointer.setNamespace(Constants.OAI_DC_NS_URI);

            addNewEntriesHandler.setPointerElement(pointer);

            addNewEntriesHandler.setSubtreeToInsert(elementsToAdd);

            sp.addHandler(addNewEntriesHandler);

            try {
                if (dcNewBytes != null) {
                    sp.parse(new ByteArrayInputStream(dcNewBytes));
                }
                else {
                    sp.parse(dcIs);
                }
                sp.clearHandlerChain();
                final ByteArrayOutputStream dcUpdated =
                    addNewEntriesHandler.getOutputStreams();
                dcNewBytes = dcUpdated.toByteArray();
                // setDc(dc.toString(XmlUtility.CHARACTER_ENCODING));
            }
            catch (final Exception e) {
                throw new XmlParserSystemException(e);
            }
        }
        if (!updatedDcProperties && changedValues.size() > 0) {

            final TreeMap<String, StartElementWithText> updateElementsDc =
                updateDcProperties(changedValues);

            final DcUpdateHandler dcUpdateHandler =
                new DcUpdateHandler(updateElementsDc, sp);

            sp.addHandler(dcUpdateHandler);
            final HashMap<String, String> extractPathes =
                new HashMap<String, String>();
            final MultipleExtractor me =
                new MultipleExtractor(extractPathes, sp);
            extractPathes.put("/dc", null);
            sp.addHandler(me);
            try {
                if (dcNewBytes != null) {
                    sp.parse(new ByteArrayInputStream(dcNewBytes));
                }
                else {
                    sp.parse(dcIs);
                }
                sp.clearHandlerChain();
                final ByteArrayOutputStream dcUpdated =
                    (ByteArrayOutputStream) me.getOutputStreams().get("dc");
                dcNewBytes = dcUpdated.toByteArray();
            }
            catch (final Exception e) {
                throw new XmlParserSystemException(e);
            }
        }
        String dcNew = null;
        try {
            dcNew = new String(dcNewBytes, XmlUtility.CHARACTER_ENCODING);
        }
        catch (final UnsupportedEncodingException e) {
            log.error(e);
            throw new EncodingSystemException(e);
        }

        setDc(dcNew);
        return true;
    }

    /**
     * A help method prepares a Map with elements to instantiate a
     * DcUpdateHandler.
     * 
     * @param changedValues
     *            Map of all changed values.
     * @return a Map with elements to update
     * @throws TripleStoreSystemException
     * @throws ContextNameNotUniqueException
     * @throws WebserverSystemException
     * 
     */
    private TreeMap<String, StartElementWithText> updateDcProperties(
        final HashMap<String, String> changedValues)
        throws TripleStoreSystemException, ContextNameNotUniqueException,
        WebserverSystemException {

        final TreeMap<String, StartElementWithText> updateElementsDc =
            new TreeMap<String, StartElementWithText>();

        final Iterator<String> it = changedValues.keySet().iterator();
        while (it.hasNext()) {
            final String key = it.next();

            // if name was altered alter the title too. (title is used
            // only internally)
            if (key.equals(Elements.ELEMENT_NAME)) {
                // check if new name of Context is unique !
                // name must be unique
                if (getTripleStoreUtility().getContextForName(
                    changedValues.get(key)) != null) {
                    throw new ContextNameNotUniqueException();
                }

                updateElementsDc.put(Elements.ELEMENT_DC_TITLE,
                    new StartElementWithText(Elements.ELEMENT_DC_TITLE,
                        Constants.DC_NS_URI, Constants.DC_NS_PREFIX,
                        changedValues.get(key), null));
            }

            updateElementsDc.put(key,
                new StartElementWithText(key, Constants.DC_NS_URI,
                    Constants.DC_NS_PREFIX, changedValues.get(key), null));

        }

        return updateElementsDc;

    }

    /**
     * Write DC datastream.
     * 
     * @param xml
     *            New DC representation.
     * @throws SystemException
     *             If anything fails.
     */
    private void setDc(final String xml) throws SystemException {
        try {
            final Datastream oldDs = getContext().getDc();
            final Datastream newDs =
                new Datastream("DC", getContext().getId(),
                    xml.getBytes(XmlUtility.CHARACTER_ENCODING), "text/xml");
            if (!oldDs.equals(newDs)) {
                // TODO check if update is allowed
                getContext().setDc(newDs);
            }
        }

        catch (final UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }
        catch (final StreamNotFoundException e) {
            throw new IntegritySystemException(
                "Error accessing dc datastream of context '"
                    + getContext().getId() + "'!");
        }
    }

    /**
     * Handle update of admin-descriptors datastreams.
     * 
     * @return true if admindescriptors where updated.
     * @param streams
     *            Map of Datastreams with name of admin-descriptor as key.
     * @throws SystemException
     *             TODO
     */
    boolean handleAdminDescriptors(final HashMap<String, Object> streams)
        throws SystemException {
        boolean updated = false;
        final Iterator<String> it = streams.keySet().iterator();

        HashMap<String, Datastream> adminDescriptors =
            getContext().getAdminDescriptorsMap();

        while (it.hasNext()) {
            final String name = it.next();
            // final String id = name.replace(" ", "_");
            final String label = name;
            Boolean newDS = true;
            if (adminDescriptors.containsKey(name)) {
                Datastream oldDs = adminDescriptors.get(name);
                Datastream newDs =
                    new Datastream(name, getContext().getId(),
                        ((ByteArrayOutputStream) streams.get(name))
                            .toByteArray(), "text/xml");
                newDs
                    .addAlternateId(de.escidoc.core.common.business.fedora.Constants.ADMIN_DESCRIPTOR_ALT_ID);

                if (oldDs.equals(newDs)
                    && oldDs.getMimeType().equals("text/xml")) {
                    log.debug("Datastreams identical; updated of Context "
                        + getContext().getId() + " with admin-descriptor "
                        + name + " skipped.");
                }
                else {
                    getContext().setAdminDescriptor(newDs);
                    log.debug("updated Context " + getContext().getId()
                        + " with admin-descriptor " + name);
                    updated = true;
                }
                newDS = false;
                adminDescriptors.remove(name);
            }

            if (newDS) {

                getFedoraUtility()
                    .addDatastream(
                        getContext().getId(),
                        name,
                        new String[] { de.escidoc.core.common.business.fedora.Constants.ADMIN_DESCRIPTOR_ALT_ID },
                        label,
                        true,
                        ((ByteArrayOutputStream) streams.get(name))
                            .toByteArray(), false);
                // it.remove();
                log.debug("add to Context " + getContext().getId()
                    + " new admin-descriptor " + name);
                updated = true;
            }
        }

        // remove datastreams
        Iterator<String> toDelete = adminDescriptors.keySet().iterator();
        while (toDelete.hasNext()) {
            String nextName = toDelete.next();
            Datastream nextDatastream = adminDescriptors.get(nextName);
            nextDatastream.delete();
            log.debug("Admin-descriptor datastream '" + nextName
                + "' of Context " + getContext().getId() + " deleted.");
            updated = true;
        }
        return updated;

    }
}
