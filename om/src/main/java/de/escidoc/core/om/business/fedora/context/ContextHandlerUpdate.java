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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;

import org.escidoc.core.services.fedora.AddDatastreamPathParam;
import org.escidoc.core.services.fedora.AddDatastreamQueryParam;
import org.esidoc.core.utils.io.MimeTypes;
import org.esidoc.core.utils.io.Stream;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.common.business.Constants;
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
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.PidAlreadyAssignedException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.AddNewSubTreesToDatastream;
import de.escidoc.core.common.util.stax.handler.ItemRelsExtUpdateHandler;
import de.escidoc.core.common.util.stax.handler.MultipleExtractor;
import de.escidoc.core.common.util.stax.handler.OptimisticLockingHandler;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithText;
import de.escidoc.core.om.business.stax.handler.context.ContextPropertiesUpdateHandler;

/**
 * @author Steffen Wagner
 */
public class ContextHandlerUpdate extends ContextHandlerDelete {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextHandlerUpdate.class);

    private static final String XPATH_ADMIN_DESCRIPTORS = "/context/admin-descriptors/admin-descriptor";

    private static final String XPATH_RESOURCES = "/context/resources";

    /**
     * Update Context.
     *
     * @param contextHandler FedoraContextHandler
     * @param xmlData        Context update XML representation.
     * @return if resource was udated true, false otherwise
     * @throws ContextNotFoundException      Thrown if Context could not be found.
     * @throws InvalidStatusException        Thrown if context is in invalid status.
     * @throws OptimisticLockingException    Thrown if context resource is altered on update.
     * @throws ReadonlyAttributeViolationException
     *                                       Thrown if read-only attributes should be altered.
     * @throws ReadonlyElementViolationException
     *                                       Thrown if read-only elements should be altered.
     * @throws ContextNameNotUniqueException Thrown if new name of context is not unique.
     * @throws MissingElementValueException  Thrown if value of element is missing.
     * @throws InvalidContentException       Thrown if the xmlData parameter has invalid content.
     * @throws SystemException               Thrown if anything else fails.
     */
    public boolean update(final FedoraContextHandler contextHandler, final String xmlData)
        throws ContextNotFoundException, InvalidStatusException, OptimisticLockingException,
        ReadonlyAttributeViolationException, ReadonlyElementViolationException, ContextNameNotUniqueException,
        MissingElementValueException, SystemException, InvalidContentException {

        final DateTime startTimeStamp = getContext().getLastFedoraModificationDate();
        final StaxParser sp = new StaxParser();
        sp
            .addHandler(new OptimisticLockingHandler(getContext().getId(), Constants.CONTEXT_OBJECT_TYPE,
                startTimeStamp));
        final ContextPropertiesUpdateHandler cpuh = new ContextPropertiesUpdateHandler(getContext().getId(), sp);
        sp.addHandler(cpuh);

        final String status = getContext().getStatus();

        if (status.equals(Constants.STATUS_CONTEXT_CLOSED)) {
            throw new InvalidStatusException("Status is closed");
        }

        final HashMap<String, String> extractPathes = new HashMap<String, String>();

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

        final Map<String, Object> streams = me.getOutputStreams();
        streams.remove("resources");

        final boolean adminDescriptorsUpdated = handleAdminDescriptors(streams);

        getContext().setOrganizationalUnits(cpuh.getOrganizationalUnits());

        // RELS-EXT ----------------------------------------

        final boolean dcUpdated =
            updateDc(cpuh.getChangedValuesInDc(), cpuh.getPropertiesToRemove(), cpuh.getPropertiesToAdd());
        final Map<String, String> changedValues = cpuh.getChangedValuesInRelsExt();
        if (!changedValues.isEmpty() || dcUpdated || adminDescriptorsUpdated || getContext().isOuUpdated()) {
            final String oldModifiedBy =
                getTripleStoreUtility().getProperty(getContext().getId(),
                    Constants.STRUCTURAL_RELATIONS_NS_URI + "modified-by");
            final String[] currentUser = Utility.getCurrentUser();
            if (!oldModifiedBy.equals(currentUser[0])) {
                changedValues.put("modifiedBy", currentUser[0]);
                changedValues.put("modifiedByTitle", currentUser[1]);
            }
            final String buildNumber = Utility.getBuildNumber();
            changedValues.put(XmlTemplateProviderConstants.BUILD_NUMBER, buildNumber);
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
     * @param contextHandler FedoraContextHandler
     * @param taskParam      The parameter structure.
     * @throws ContextNotFoundException   Thrown if Context resource could not be found.
     * @throws InvalidStatusException     Thrown if Context has invalid status.
     * @throws OptimisticLockingException Thrown if context resource is altered on open.
     * @throws SystemException            Thrown if anything else fails.
     * @throws StreamNotFoundException    Thrown if RELS-EXT datastream could not be retrieved.
     * @throws LockingException           Thrown if Context is locked.
     * @throws de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException
     */
    public void open(final FedoraContextHandler contextHandler, final String taskParam) throws InvalidStatusException,
        OptimisticLockingException, SystemException, XmlCorruptedException {

        checkStatus(Constants.STATUS_CONTEXT_CREATED);
        final TaskParamHandler taskParamHandler;
        try {
            taskParamHandler = XmlUtility.parseTaskParam(taskParam);
        }
        catch (final SystemException e) {
            throw new XmlCorruptedException(e.getMessage(), e);
        }

        Utility.checkOptimisticLockingCriteria(getContext().getLastModificationDate(), taskParamHandler
            .getLastModificationDate(), "Context " + getContext().getId());

        final Map<String, StartElementWithChildElements> updateElementsRelsExt =
            new TreeMap<String, StartElementWithChildElements>();

        final StartElementWithChildElements modifiedBy =
            new StartElementWithChildElements(Elements.ELEMENT_MODIFIED_BY, Constants.STRUCTURAL_RELATIONS_NS_URI,
                Constants.STRUCTURAL_RELATIONS_NS_PREFIX, null, getUtility().getCurrentUserId(), null);
        final Attribute resourceAttribute =
            new Attribute("resource", Constants.RDF_NAMESPACE_URI, Constants.RDF_NAMESPACE_PREFIX, "info:fedora/"
                + getUtility().getCurrentUserId());
        modifiedBy.addAttribute(resourceAttribute);
        updateElementsRelsExt.put(Elements.ELEMENT_MODIFIED_BY, modifiedBy);

        updateElementsRelsExt.put(Elements.ELEMENT_MODIFIED_BY_TITLE, new StartElementWithChildElements(
            Elements.ELEMENT_MODIFIED_BY_TITLE, Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX, null,
            getUtility().getCurrentUserRealName(), null));

        final String buildNumber = Utility.getBuildNumber();
        updateElementsRelsExt.put(XmlTemplateProviderConstants.BUILD_NUMBER, new StartElementWithChildElements(
            XmlTemplateProviderConstants.BUILD_NUMBER, "http://escidoc.de/core/01/system/", "system", null,
            buildNumber, null));

        updateElementsRelsExt.put(Elements.ELEMENT_PUBLIC_STATUS, new StartElementWithChildElements(
            Elements.ELEMENT_PUBLIC_STATUS, Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX, null,
            Constants.STATUS_CONTEXT_OPENED, null));

        String comment = taskParamHandler.getComment();
        if (comment == null || comment.length() == 0) {
            comment = "Context " + getContext().getId() + " opened.";
        }
        updateElementsRelsExt.put(Elements.ELEMENT_PUBLIC_STATUS_COMMENT, new StartElementWithChildElements(
            Elements.ELEMENT_PUBLIC_STATUS_COMMENT, Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX, null,
            comment, null));

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

        final ByteArrayOutputStream relsExt = (ByteArrayOutputStream) me.getOutputStreams().get("RDF");
        try {
            getContext().setRelsExt(relsExt.toString(XmlUtility.CHARACTER_ENCODING));
            getContext().persist();
        }
        catch (final UnsupportedEncodingException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Set Context status to close.
     *
     * @param contextHandler FedoraContextHandler
     * @param taskParam      The parameter structure.
     * @throws ContextNotFoundException   Thrown if Context resource could not be found.
     * @throws InvalidStatusException     Thrown if Context has invalid status.
     * @throws OptimisticLockingException Thrown if context resource is altered on open.
     * @throws SystemException            Thrown if anything else fails.
     * @throws StreamNotFoundException    Thrown if RELS-EXT datastream could not be retrieved.
     * @throws LockingException           Thrown if Context is locked.
     * @throws de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException
     */
    public void close(final FedoraContextHandler contextHandler, final String taskParam) throws InvalidStatusException,
        OptimisticLockingException, SystemException, XmlCorruptedException {

        checkStatus(Constants.STATUS_CONTEXT_OPENED);
        final TaskParamHandler taskParamHandler;
        try {
            taskParamHandler = XmlUtility.parseTaskParam(taskParam);
        }
        catch (final SystemException e) {
            throw new XmlCorruptedException(e.getMessage(), e);
        }

        Utility.checkOptimisticLockingCriteria(getContext().getLastModificationDate(), taskParamHandler
            .getLastModificationDate(), "Context " + getContext().getId());

        // update RELS-EXT
        final Map<String, StartElementWithChildElements> updateElementsRelsExt =
            new TreeMap<String, StartElementWithChildElements>();

        final StartElementWithChildElements modifiedBy =
            new StartElementWithChildElements(Elements.ELEMENT_MODIFIED_BY, Constants.STRUCTURAL_RELATIONS_NS_URI,
                Constants.STRUCTURAL_RELATIONS_NS_PREFIX, null, getUtility().getCurrentUserId(), null);
        final Attribute resourceAttribute =
            new Attribute("resource", Constants.RDF_NAMESPACE_URI, Constants.RDF_NAMESPACE_PREFIX, "info:fedora/"
                + getUtility().getCurrentUserId());
        modifiedBy.addAttribute(resourceAttribute);
        updateElementsRelsExt.put(Elements.ELEMENT_MODIFIED_BY, modifiedBy);

        updateElementsRelsExt.put(Elements.ELEMENT_MODIFIED_BY_TITLE, new StartElementWithChildElements(
            Elements.ELEMENT_MODIFIED_BY_TITLE, Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX, null,
            getUtility().getCurrentUserRealName(), null));

        final String buildNumber = Utility.getBuildNumber();
        updateElementsRelsExt.put(XmlTemplateProviderConstants.BUILD_NUMBER, new StartElementWithChildElements(
            XmlTemplateProviderConstants.BUILD_NUMBER, "http://escidoc.de/core/01/system/", "system", null,
            buildNumber, null));

        updateElementsRelsExt.put(Elements.ELEMENT_PUBLIC_STATUS, new StartElementWithChildElements(
            Elements.ELEMENT_PUBLIC_STATUS, Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX, null,
            Constants.STATUS_CONTEXT_CLOSED, null));

        String comment = taskParamHandler.getComment();
        if (comment == null || comment.length() == 0) {
            comment = "Context " + getContext().getId() + " closed.";
        }
        updateElementsRelsExt.put(Elements.ELEMENT_PUBLIC_STATUS_COMMENT, new StartElementWithChildElements(
            Elements.ELEMENT_PUBLIC_STATUS_COMMENT, Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX, null,
            comment, null));

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

        final ByteArrayOutputStream relsExt = (ByteArrayOutputStream) me.getOutputStreams().get("RDF");
        try {
            getContext().setRelsExt(relsExt.toString(XmlUtility.CHARACTER_ENCODING));
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
     * @param contextHandler FedoraContextHandler
     * @param xmlData        XML representation of new AdminDescriptor.
     */
    public void updateAdminDescriptor(final FedoraContextHandler contextHandler, final String xmlData) {
        // TODO implement
        throw new UnsupportedOperationException("ContextHandlerUpdate.updateAdminDescriptor not yet implemented");
    }

    /**
     * Replace updated values in RELS-EXT.
     *
     * @param changedValues HashMap of changed values.
     * @throws XmlParserSystemException      In case of parser error.
     * @throws ContextNameNotUniqueException In case of context name is already in use.
     * @throws WebserverSystemException      In case of an internal error in the webserver.
     * @throws TripleStoreSystemException    In case of an internal error in the triple store.
     */
    private void updateRelsExt(final Map<String, String> changedValues) throws XmlParserSystemException {

        if (changedValues.size() < 1) {
            return;
        }

        final TreeMap<String, StartElementWithText> updateElementsRelsExt = new TreeMap<String, StartElementWithText>();
        final Set<Entry<String, String>> changedValuesEntrySet = changedValues.entrySet();
        for (final Entry<String, String> entry : changedValuesEntrySet) {
            if (XmlTemplateProviderConstants.BUILD_NUMBER.equals(entry.getKey())) {
                updateElementsRelsExt.put(XmlTemplateProviderConstants.BUILD_NUMBER, new StartElementWithChildElements(
                    entry.getKey(), "http://escidoc.de/core/01/system/", "system", null, entry.getValue(), null));
            }
            else if ("modifiedBy".equals(entry.getKey())) {
                final StartElementWithChildElements modifiedBy =
                    new StartElementWithChildElements(Elements.ELEMENT_MODIFIED_BY,
                        Constants.STRUCTURAL_RELATIONS_NS_URI, Constants.STRUCTURAL_RELATIONS_NS_PREFIX, null, "", null);
                final Attribute resourceAttribute =
                    new Attribute("resource", Constants.RDF_NAMESPACE_URI, Constants.RDF_NAMESPACE_PREFIX,
                        "info:fedora/" + entry.getValue());
                modifiedBy.addAttribute(resourceAttribute);
                updateElementsRelsExt.put(Elements.ELEMENT_MODIFIED_BY, modifiedBy);
            }
            else if ("modifiedByTitle".equals(entry.getKey())) {
                updateElementsRelsExt.put(Elements.ELEMENT_MODIFIED_BY_TITLE, new StartElementWithChildElements(
                    Elements.ELEMENT_MODIFIED_BY_TITLE, Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX,
                    null, entry.getValue(), null));
            }
            else {
                updateElementsRelsExt.put(entry.getKey(), new StartElementWithText(entry.getKey(),
                    Constants.PROPERTIES_NS_URI, Constants.PROPERTIES_NS_PREFIX, entry.getValue(), null));
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
            final ByteArrayOutputStream relsExt = (ByteArrayOutputStream) me.getOutputStreams().get("RDF");
            getContext().setRelsExt(relsExt);
        }
        catch (final Exception e) {
            throw new XmlParserSystemException(e);
        }
    }

    /**
     * Replaces updated values in DC, removes/adds provided properties from/to DC and write DC datastream to Fedora.
     *
     * @param changedValues      HashMap of changed values.
     * @param propertiesToRemove properties to remove.
     * @param propertiesToAdd    properties to add.
     * @return true if dc was updated, false otherwise.
     * @throws ContextNameNotUniqueException In case of context name is already in use.
     * @throws SystemException               In case of an internal error in the webserver.
     */
    private boolean updateDc(
        final Map<String, String> changedValues, final List<String> propertiesToRemove,
        final Map<String, String> propertiesToAdd) throws ContextNameNotUniqueException, SystemException {
        if ((changedValues == null || changedValues.isEmpty())
            && (propertiesToRemove == null || propertiesToRemove.isEmpty())
            && (propertiesToAdd == null || propertiesToAdd.isEmpty())) {
            return false;
        }
        final Datastream dc;
        try {
            dc = getContext().getDc();
        }
        catch (final StreamNotFoundException e1) {
            throw new IntegritySystemException("Stream dc not found.", e1);
        }
        final ByteArrayInputStream dcIs = new ByteArrayInputStream(dc.getStream());
        byte[] dcNewBytes = null;
        final StaxParser sp = new StaxParser();

        boolean updatedDcProperties = false;
        if (!propertiesToRemove.isEmpty()) {

            if (!changedValues.isEmpty()) {
                updatedDcProperties = true;
                final Map<String, StartElementWithText> updateElementsDc = updateDcProperties(changedValues);

                final DcUpdateHandler dcUpdateHandler = new DcUpdateHandler(updateElementsDc, sp);

                sp.addHandler(dcUpdateHandler);
            }
            final HashMap<String, String> extractPathes = new HashMap<String, String>();
            final MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
            extractPathes.put("/dc", null);
            sp.addHandler(me);

            final Map<String, List<StartElementWithChildElements>> toRemove =
                new TreeMap<String, List<StartElementWithChildElements>>();
            final Iterator<String> iterator = propertiesToRemove.iterator();
            final HashMap<String, List<StartElementWithChildElements>> propertiesVectorAssignment =
                new HashMap<String, List<StartElementWithChildElements>>();
            while (iterator.hasNext()) {
                final String property = iterator.next();

                final StartElementWithChildElements propertyToDelete = new StartElementWithChildElements();
                propertyToDelete.setLocalName(property);
                propertyToDelete.setPrefix(Constants.DC_NS_PREFIX);
                propertyToDelete.setNamespace(Constants.DC_NS_URI);
                propertyToDelete.setChildrenElements(null);

                if (propertiesVectorAssignment.containsKey(property)) {
                    final List<StartElementWithChildElements> vector = propertiesVectorAssignment.get(property);
                    vector.add(propertyToDelete);
                }
                else {
                    final List<StartElementWithChildElements> vector = new ArrayList<StartElementWithChildElements>();
                    vector.add(propertyToDelete);
                    propertiesVectorAssignment.put(property, vector);
                }
            }
            final Set<Entry<String, List<StartElementWithChildElements>>> propertiesVectorAssignmentEntrySet =
                propertiesVectorAssignment.entrySet();
            for (final Entry<String, List<StartElementWithChildElements>> entry : propertiesVectorAssignmentEntrySet) {
                final List<StartElementWithChildElements> elements = entry.getValue();
                toRemove.put("/dc/" + entry.getKey(), elements);
            }
            me.removeElements(toRemove);

            try {
                sp.parse(dcIs);
                sp.clearHandlerChain();
                final ByteArrayOutputStream dcUpdated = (ByteArrayOutputStream) me.getOutputStreams().get("dc");
                dcNewBytes = dcUpdated.toByteArray();

            }
            catch (final Exception e) {
                throw new XmlParserSystemException(e);
            }
        }
        if (!propertiesToAdd.isEmpty()) {

            if (!updatedDcProperties && !changedValues.isEmpty()) {
                updatedDcProperties = true;

                final Map<String, StartElementWithText> updateElementsDc = updateDcProperties(changedValues);

                final DcUpdateHandler dcUpdateHandler = new DcUpdateHandler(updateElementsDc, sp);

                sp.addHandler(dcUpdateHandler);

            }

            final AddNewSubTreesToDatastream addNewEntriesHandler = new AddNewSubTreesToDatastream("/dc", sp);
            final List<StartElementWithChildElements> elementsToAdd = new ArrayList<StartElementWithChildElements>();
            for (final Entry<String, String> stringStringEntry : propertiesToAdd.entrySet()) {
                final StartElementWithChildElements newPropertyElement = new StartElementWithChildElements();
                newPropertyElement.setLocalName(stringStringEntry.getKey());
                newPropertyElement.setPrefix(Constants.DC_NS_PREFIX);
                newPropertyElement.setNamespace(Constants.DC_NS_URI);
                newPropertyElement.setElementText(stringStringEntry.getValue());
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
                final ByteArrayOutputStream dcUpdated = addNewEntriesHandler.getOutputStreams();
                dcNewBytes = dcUpdated.toByteArray();
                // setDc(dc.toString(XmlUtility.CHARACTER_ENCODING));
            }
            catch (final Exception e) {
                throw new XmlParserSystemException(e);
            }
        }
        if (!updatedDcProperties && !changedValues.isEmpty()) {

            final Map<String, StartElementWithText> updateElementsDc = updateDcProperties(changedValues);

            final DcUpdateHandler dcUpdateHandler = new DcUpdateHandler(updateElementsDc, sp);

            sp.addHandler(dcUpdateHandler);
            final HashMap<String, String> extractPathes = new HashMap<String, String>();
            final MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
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
                final ByteArrayOutputStream dcUpdated = (ByteArrayOutputStream) me.getOutputStreams().get("dc");
                dcNewBytes = dcUpdated.toByteArray();
            }
            catch (final Exception e) {
                throw new XmlParserSystemException(e);
            }
        }
        final String dcNew;
        try {
            dcNew = new String(dcNewBytes, XmlUtility.CHARACTER_ENCODING);
        }
        catch (final UnsupportedEncodingException e) {
            throw new EncodingSystemException(e);
        }

        setDc(dcNew);
        return true;
    }

    /**
     * A help method prepares a Map with elements to instantiate a DcUpdateHandler.
     *
     * @param changedValues Map of all changed values.
     * @return a Map with elements to update
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException
     */
    private Map<String, StartElementWithText> updateDcProperties(final Map<String, String> changedValues)
        throws TripleStoreSystemException, ContextNameNotUniqueException {

        final Map<String, StartElementWithText> updateElementsDc = new TreeMap<String, StartElementWithText>();

        final Set<Entry<String, String>> changedValuesEntrySet = changedValues.entrySet();
        for (final Entry<String, String> entry : changedValuesEntrySet) {
            // if name was altered alter the title too. (title is used
            // only internally)
            if (entry.getKey().equals(Elements.ELEMENT_NAME)) {
                // check if new name of Context is unique !
                // name must be unique
                if (getTripleStoreUtility().getContextForName(entry.getValue()) != null) {
                    throw new ContextNameNotUniqueException();
                }

                updateElementsDc.put(Elements.ELEMENT_DC_TITLE, new StartElementWithText(Elements.ELEMENT_DC_TITLE,
                    Constants.DC_NS_URI, Constants.DC_NS_PREFIX, entry.getValue(), null));
            }

            updateElementsDc.put(entry.getKey(), new StartElementWithText(entry.getKey(), Constants.DC_NS_URI,
                Constants.DC_NS_PREFIX, entry.getValue(), null));

        }

        return updateElementsDc;

    }

    /**
     * Write DC datastream.
     *
     * @param xml New DC representation.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    private void setDc(final String xml) throws EncodingSystemException, IntegritySystemException,
        FedoraSystemException, WebserverSystemException {
        try {
            final Datastream oldDs = getContext().getDc();
            final Datastream newDs =
                new Datastream("DC", getContext().getId(), xml.getBytes(XmlUtility.CHARACTER_ENCODING),
                    MimeTypes.TEXT_XML);
            if (!oldDs.equals(newDs)) {
                // TODO check if update is allowed
                getContext().setDc(newDs);
            }
        }
        catch (final UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }
        catch (final StreamNotFoundException e) {
            throw new IntegritySystemException("Error accessing dc datastream of context '" + getContext().getId()
                + "'!", e);
        }
    }

    /**
     * Handle update of admin-descriptors datastreams.
     *
     * @param streams Map of Datastreams with name of admin-descriptor as key.
     * @return true if admindescriptors where updated.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     */
    boolean handleAdminDescriptors(final Map<String, Object> streams) throws FedoraSystemException,
        WebserverSystemException {
        boolean updated = false;
        final Set<Entry<String, Object>> streamsEntrySet = streams.entrySet();

        final Map<String, Datastream> adminDescriptors = getContext().getAdminDescriptorsMap();

        for (final Entry<String, Object> entry : streamsEntrySet) {
            final String name = entry.getKey();
            Boolean newDS = true;
            if (adminDescriptors.containsKey(name)) {
                final Datastream oldDs = adminDescriptors.get(name);
                final Datastream newDs =
                    new Datastream(name, getContext().getId(),
                        ((ByteArrayOutputStream) entry.getValue()).toByteArray(), MimeTypes.TEXT_XML);
                newDs.addAlternateId(de.escidoc.core.common.business.fedora.Constants.ADMIN_DESCRIPTOR_ALT_ID);

                if (oldDs.equals(newDs) && MimeTypes.TEXT_XML.equals(oldDs.getMimeType())) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Datastreams identical; updated of Context " + getContext().getId()
                            + " with admin-descriptor " + name + " skipped.");
                    }
                }
                else {
                    getContext().setAdminDescriptor(newDs);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("updated Context " + getContext().getId() + " with admin-descriptor " + name);
                    }
                    updated = true;
                }
                newDS = false;
                adminDescriptors.remove(name);
            }

            if (newDS) {
                final AddDatastreamPathParam path = new AddDatastreamPathParam(getContext().getId(), name);
                final AddDatastreamQueryParam query = new AddDatastreamQueryParam();
                query
                    .setAltIDs(Arrays.asList(de.escidoc.core.common.business.fedora.Constants.ADMIN_DESCRIPTOR_ALT_ID));
                query.setDsLabel(name);
                query.setVersionable(Boolean.TRUE);
                final Stream stream = new Stream();
                try {
                    stream.write(((ByteArrayOutputStream) streams.get(name)).toByteArray());
                    stream.lock();
                }
                catch (IOException e) {
                    throw new WebserverSystemException(e);
                }
                getFedoraServiceClient().addDatastream(path, query, stream);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("add to Context " + getContext().getId() + " new admin-descriptor " + name);
                }
                updated = true;
            }
        }

        // remove datastreams
        final Set<Entry<String, Datastream>> adminDescriptorsEntrySet = adminDescriptors.entrySet();
        for (final Entry<String, Datastream> entry : adminDescriptorsEntrySet) {
            final Datastream nextDatastream = entry.getValue();
            nextDatastream.delete();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Admin-descriptor datastream '" + entry.getKey() + "' of Context " + getContext().getId()
                    + " deleted.");
            }
            updated = true;
        }
        return updated;

    }
}
