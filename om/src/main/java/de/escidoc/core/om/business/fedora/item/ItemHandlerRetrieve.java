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
package de.escidoc.core.om.business.fedora.item;

import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.Item;
import de.escidoc.core.common.business.fedora.resources.Relation;
import de.escidoc.core.common.business.fedora.resources.interfaces.FedoraResource;
import de.escidoc.core.common.business.fedora.resources.item.Component;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.WovContentRelationsRetrieveHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.FoXmlProviderConstants;
import de.escidoc.core.common.util.xml.factory.ItemXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import de.escidoc.core.om.business.renderer.VelocityXmlCommonRenderer;
import de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This is a class, indeed.
 *
 * @author Michael Schneider
 */
public class ItemHandlerRetrieve extends ItemHandlerBase implements ItemRendererInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemHandlerRetrieve.class);

    @Autowired
    private VelocityXmlCommonRenderer velocityXmlCommonRenderer;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface
     * #render()
     */
    @Override
    public String render() throws SystemException, ComponentNotFoundException, ItemNotFoundException {

        final Map<String, Object> values = new HashMap<String, Object>();

        final Map<String, String> commonValues = getCommonValues(getItem());

        values.putAll(getPropertiesValues(getItem()));
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORDS_CONTENT, renderMdRecords(commonValues, false));
        values.put(XmlTemplateProviderConstants.CONTENT_STREAMS, renderContentStreams(commonValues, false));
        values.put(XmlTemplateProviderConstants.VAR_COMPONENTS_CONTENT, renderComponents(commonValues, false));
        values.putAll(getRelationValues(getItem()));
        values.putAll(getResourcesValues(getItem()));
        values.putAll(commonValues);
        return ItemXmlProvider.getInstance().getItemXml(values);

    }

    /**
     * Get XML representation of Components.
     *
     * @param isRoot Set true if Components is XML root element, false if it is XML sub-element.
     * @return XML representation of Components
     * @throws ComponentNotFoundException Thrown if Component was not found
     * @throws SystemException            Thrown if an unexpected error occurs
     */
    @Override
    public String renderComponents(final boolean isRoot) throws ComponentNotFoundException, SystemException {

        return renderComponents(getCommonValues(getItem()), isRoot);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface
     * #renderComponents(boolean)
     */
    public String renderComponents(final Map<String, String> commonValues, final boolean isRoot) throws SystemException {

        final Map<String, String> values = new HashMap<String, String>();

        if (isRoot) {
            values.put(XmlTemplateProviderConstants.IS_ROOT_SUB_RESOURCE, XmlTemplateProviderConstants.TRUE);
        }

        final Collection<String> componentIds;
        final String originObjectId = getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        if (originObjectId != null) {
            values.put(XmlTemplateProviderConstants.ORIGIN, XmlTemplateProviderConstants.TRUE);
            values.putAll(getCommonValues(getOriginItem()));
            values.put("componentsTitle", "Components of Item " + getOriginId());
            values.put("componentsHref", de.escidoc.core.common.business.Constants.ITEM_URL_BASE + getOriginId()
                + "/components");

            componentIds = getOriginItem().getComponentIds();
        }
        else {

            values.putAll(commonValues);
            values.put("componentsTitle", "Components of Item " + getItem().getId());
            values.put("componentsHref", getItem().getHref() + "/components");

            componentIds = getItem().getComponentIds();
        }
        if (!componentIds.isEmpty()) {
            final StringBuilder renderedComponents = new StringBuilder();
            for (final String componentId : componentIds) {
                try {
                    renderedComponents.append(renderComponent(componentId, commonValues, false));
                }
                catch (final ComponentNotFoundException e) {
                    throw new IntegritySystemException(e);
                }
            }
            values.put("components", renderedComponents.toString());
        }
        return ItemXmlProvider.getInstance().getComponentsXml(values);
    }

    /**
     * Get XML representation of Component.
     *
     * @param id     objid of Component
     * @param isRoot Set true if Component is XML root element, false if it is XML sub-element.
     * @return XML representation of Component with provided id
     * @throws ComponentNotFoundException Thrown if Component was not found
     * @throws SystemException            Thrown if an unexpected error occurs
     */
    @Override
    public String renderComponent(final String id, final boolean isRoot) throws ComponentNotFoundException,
        SystemException {

        return renderComponent(id, getCommonValues(getItem()), isRoot);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface
     * #renderComponent(java.lang.String, boolean)
     */
    public String renderComponent(final String id, final Map<String, String> commonValues, final boolean isRoot)
        throws ComponentNotFoundException, SystemException {

        final Component component = getComponent(id);

        final Map<String, String> values = new HashMap<String, String>();
        if (isRoot) {
            values.put(XmlTemplateProviderConstants.IS_ROOT, XmlTemplateProviderConstants.TRUE);
        }
        final String originObjectId = getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        if (originObjectId != null) {
            values.put(XmlTemplateProviderConstants.ORIGIN, XmlTemplateProviderConstants.TRUE);
            values.put("componentHref", de.escidoc.core.common.business.Constants.ITEM_URL_BASE + getOriginId()
                + component.getHrefPart());
            values.putAll(getCommonValues(getOriginItem()));
        }
        else {
            values.put("componentHref", getItem().getHref() + component.getHrefPart());
            values.putAll(getCommonValues(getItem()));
        }
        values.put(XmlTemplateProviderConstants.MD_RECRORDS_NAMESPACE_PREFIX,
            de.escidoc.core.common.business.Constants.METADATARECORDS_NAMESPACE_PREFIX);
        values.put(XmlTemplateProviderConstants.MD_RECORDS_NAMESPACE,
            de.escidoc.core.common.business.Constants.METADATARECORDS_NAMESPACE_URI);
        values.put("componentTitle", component.getTitle());
        values.put("componentHref", getItem().getHref() + component.getHrefPart());
        values.put("componentId", component.getId());
        values.putAll(commonValues);
        values.putAll(getComponentPropertiesValues(component));
        values.put("componentContentTitle", component.getTitle());

        final Datastream content = component.getContent();
        final String storage = content.getControlGroup();
        if (storage.equals(FoXmlProviderConstants.CONTROL_GROUP_M)) {
            values.put("storage", Constants.STORAGE_INTERNAL_MANAGED);
            if (getOriginItem() != null) {
                values.put("componentContentHref", de.escidoc.core.common.business.Constants.ITEM_URL_BASE
                    + getOriginId() + component.getHrefPart() + "/content");
            }
            else {
                values.put("componentContentHref", getItem().getHref() + component.getHrefPart() + "/content");
            }
        }
        else if (storage.equals(FoXmlProviderConstants.CONTROL_GROUP_E)) {
            values.put("storage", Constants.STORAGE_EXTERNAL_MANAGED);
            if (getOriginItem() != null) {
                values.put("componentContentHref", de.escidoc.core.common.business.Constants.ITEM_URL_BASE
                    + getOriginId() + component.getHrefPart() + "/content");
            }
            else {
                values.put("componentContentHref", getItem().getHref() + component.getHrefPart() + "/content");
            }
        }
        else if (storage.equals(FoXmlProviderConstants.CONTROL_GROUP_R)) {
            values.put("storage", Constants.STORAGE_EXTERNAL_URL);
            values.put("componentContentHref", content.getLocation());
        }

        final String mdRecordsContent = renderComponentMdRecords(component.getId(), commonValues, false);
        if (mdRecordsContent.length() > 0) {
            values.put("componentMdRecordsContent", mdRecordsContent);
        }
        return ItemXmlProvider.getInstance().getComponentXml(values);
    }

    public String renderComponentProperties(final String id) throws ComponentNotFoundException, FedoraSystemException,
        TripleStoreSystemException, WebserverSystemException, IntegritySystemException, XmlParserSystemException {

        final Component component;
        final Map<String, String> values = new HashMap<String, String>();

        if (getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN) != null) {
            component = getComponent(id);
            values.put(XmlTemplateProviderConstants.ORIGIN, XmlTemplateProviderConstants.TRUE);
            values.put("componentHref", de.escidoc.core.common.business.Constants.ITEM_URL_BASE + getOriginId()
                + component.getHrefPart());
            values.putAll(getCommonValues(getOriginItem()));
        }
        else {

            component = getComponent(id);
            values.putAll(getCommonValues(getItem()));
        }

        values.put(XmlTemplateProviderConstants.IS_ROOT_PROPERTIES, XmlTemplateProviderConstants.TRUE);
        values.put("componentHref", getItem().getHref() + component.getHrefPart());
        values.put("componentId", component.getId());
        values.putAll(getComponentPropertiesValues(component));
        return ItemXmlProvider.getInstance().getComponentPropertiesXml(values);
    }

    @Override
    public String renderMdRecords(final boolean isRoot) throws WebserverSystemException, EncodingSystemException,
        FedoraSystemException, IntegritySystemException, TripleStoreSystemException {
        return renderMdRecords(getCommonValues(getItem()), isRoot);
    }

    public String renderMdRecords(final Map<String, String> commonValues, final boolean isRoot)
        throws WebserverSystemException, EncodingSystemException, TripleStoreSystemException {

        final Map<String, Datastream> mdRecords = getItem().getMdRecords();

        final StringBuilder content = new StringBuilder();
        final Map<String, String> values = new HashMap<String, String>();

        final Iterator<String> namesIter = mdRecords.keySet().iterator();
        while (namesIter.hasNext()) {
            final String mdRecordName = namesIter.next();
            try {
                final String mdRecordContent = renderMdRecord(mdRecordName, commonValues, false, false);
                if (mdRecordContent.length() == 0) {
                    namesIter.remove();
                }
                else {
                    content.append(mdRecordContent);
                }
            }
            catch (final MdRecordNotFoundException e) {
                throw new WebserverSystemException("Metadata record previously found in list not found.", e);
            }
        }

        if (getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN) != null) {
            values.put(XmlTemplateProviderConstants.ORIGIN, XmlTemplateProviderConstants.TRUE);
            final Map<String, Datastream> originMdRecords = getOriginItem().getMdRecords();
            for (final String mdRecordName : originMdRecords.keySet()) {
                if (!mdRecords.keySet().contains(mdRecordName)) {
                    try {
                        content.append(renderMdRecord(mdRecordName, commonValues, true, false));
                    }
                    catch (final MdRecordNotFoundException e) {
                        throw new WebserverSystemException("Metadata record previously found in list not found.", e);
                    }
                }
            }
        }
        if (content.length() == 0) {
            return "";
        }

        if (isRoot) {
            values.put(XmlTemplateProviderConstants.IS_ROOT_SUB_RESOURCE, XmlTemplateProviderConstants.TRUE);
        }
        values.putAll(commonValues);
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORDS_HREF, getItem().getHref()
            + de.escidoc.core.common.business.Constants.MD_RECORDS_URL_PART);
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORDS_TITLE, "Metadata Records of Item " + getItem().getId());
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORDS_CONTENT, content.toString());

        return ItemXmlProvider.getInstance().getMdRecordsXml(values);
    }

    /**
     * Render MD Record whereas the common values are not to recompile.
     *
     * @param name         Name of md-record.
     * @param commonValues Common render values.
     * @param isOrigin     set true if Item is origin Item, false otherwise
     * @param isRoot       Set true is md-record is to render with XML root element
     * @return XMl representation of md-record.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    public String renderMdRecord(
        final String name, final Map<String, String> commonValues, final boolean isOrigin, final boolean isRoot)
        throws WebserverSystemException, EncodingSystemException, MdRecordNotFoundException {

        final Map<String, String> values = new HashMap<String, String>();
        final Datastream ds;
        if (isOrigin) {
            ds = getOriginItem().getMdRecord(name);
            values.put(XmlTemplateProviderConstants.ORIGIN, XmlTemplateProviderConstants.TRUE);
            values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_HREF,
                de.escidoc.core.common.business.Constants.ITEM_URL_BASE + getOriginId()
                    + de.escidoc.core.common.business.Constants.MD_RECORD_URL_PART + '/' + name);
            if (name.equals(XmlTemplateProviderConstants.DEFAULT_METADATA_FOR_DC_MAPPING)) {
                commonValues.put(XmlTemplateProviderConstants.TITLE, getOriginItem().getTitle());
            }
        }
        else {
            ds = getItem().getMdRecord(name);
            values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_HREF, getItem().getHref()
                + de.escidoc.core.common.business.Constants.MD_RECORD_URL_PART + '/' + name);
        }

        if (ds.isDeleted()) {
            return "";
        }

        final List<String> altIds = ds.getAlternateIDs();
        if (altIds.size() > 1 && !de.escidoc.core.common.business.Constants.UNKNOWN.equals(altIds.get(1))) {
            values.put(XmlTemplateProviderConstants.MD_RECORD_TYPE, altIds.get(1));
        }
        if (altIds.size() > 2 && !de.escidoc.core.common.business.Constants.UNKNOWN.equals(altIds.get(2))) {
            values.put(XmlTemplateProviderConstants.MD_RECORD_SCHEMA, altIds.get(2));
        }
        try {
            values.put(XmlTemplateProviderConstants.MD_RECORD_CONTENT, ds.toString(XmlUtility.CHARACTER_ENCODING));
        }
        catch (final EncodingSystemException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }

        if (isRoot) {
            values.put(XmlTemplateProviderConstants.IS_ROOT_MD_RECORD, XmlTemplateProviderConstants.TRUE);
        }
        values.putAll(commonValues);
        values.put(XmlTemplateProviderConstants.MD_RECORD_NAME, name);
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_TITLE, name);
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_HREF, getItem().getHref()
            + de.escidoc.core.common.business.Constants.MD_RECORD_URL_PART + '/' + name);

        return ItemXmlProvider.getInstance().getMdRecordXml(values);
    }

    /**
     * Render MD Record whereas the common values are not to recompile.
     *
     * @param name     Name of md-record.
     * @param isOrigin set true if Item is origin Item, false otherwise
     * @param isRoot   Set true is md-record is to render with XML root element
     * @return XMl representation of md-record.
     */
    @Override
    public String renderMdRecord(final String name, final boolean isOrigin, final boolean isRoot)
        throws WebserverSystemException, IntegritySystemException, EncodingSystemException, MdRecordNotFoundException,
        TripleStoreSystemException {

        return renderMdRecord(name, getCommonValues(getItem()), isOrigin, isRoot);
    }

    @Deprecated
    public String retrieveMdRecord(final String name, final boolean isOrigin) throws MdRecordNotFoundException {
        final Datastream mdRecord = isOrigin ? getOriginItem().getMdRecord(name) : getItem().getMdRecord(name);
        if (mdRecord.isDeleted()) {
            throw new MdRecordNotFoundException("Metadata record with name " + name + " not found in item "
                + getItem().getId() + '.');
        }

        return mdRecord.toString();
    }

    public String renderContentStreams(final boolean isRoot) throws WebserverSystemException,
        TripleStoreSystemException {

        return renderContentStreams(getCommonValues(getItem()), isRoot);
    }

    public String renderContentStreams(final Map<String, String> commonValues, final boolean isRoot)
        throws WebserverSystemException, TripleStoreSystemException {

        final Map<String, String> values = new HashMap<String, String>();
        final StringBuilder content = new StringBuilder();
        if (getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN) != null) {
            values.put(XmlTemplateProviderConstants.ORIGIN, XmlTemplateProviderConstants.TRUE);
            for (final String contentStreamName : getOriginItem().getContentStreams().keySet()) {
                content.append(renderContentStream(contentStreamName, false));
            }
            values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAMS_HREF,
                de.escidoc.core.common.business.Constants.ITEM_URL_BASE + getOriginId()
                    + Constants.CONTENT_STREAMS_URL_PART);
            values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAMS_TITLE, "Content streams of Item "
                + getOriginId());
        }
        else {
            for (final String contentStreamName : getItem().getContentStreams().keySet()) {
                content.append(renderContentStream(contentStreamName, commonValues, false));
            }
            values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAMS_HREF, getItem().getHref()
                + Constants.CONTENT_STREAMS_URL_PART);
            values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAMS_TITLE, "Content streams of Item "
                + getItem().getId());

        }
        if (content.length() == 0) {
            return "";
        }

        if (isRoot) {
            values.put(XmlTemplateProviderConstants.IS_ROOT_SUB_RESOURCE, XmlTemplateProviderConstants.TRUE);
        }
        values.putAll(commonValues);
        values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAMS_CONTENT, content.toString());
        return ItemXmlProvider.getInstance().getContentStreamsXml(values);
    }

    public String renderContentStream(final String name, final boolean isRoot) throws WebserverSystemException,
        TripleStoreSystemException {

        return renderContentStream(name, getCommonValues(getItem()), isRoot);
    }

    public String renderContentStream(final String name, final Map<String, String> commonValues, final boolean isRoot)
        throws WebserverSystemException, TripleStoreSystemException {
        final Map<String, String> values = new HashMap<String, String>();

        if (isRoot) {
            values.put("isRootContentStream", XmlTemplateProviderConstants.TRUE);
        }
        final String originObjectId = getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        final Datastream ds;
        if (originObjectId != null) {
            ds = getOriginItem().getContentStream(name);
            values.put(XmlTemplateProviderConstants.ORIGIN, XmlTemplateProviderConstants.TRUE);
        }
        else {
            ds = getItem().getContentStream(name);
        }
        values.putAll(commonValues);
        values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAM_NAME, ds.getName());
        values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAM_TITLE, ds.getLabel());
        String location = ds.getLocation();
        if ("M".equals(ds.getControlGroup()) || "X".equals(ds.getControlGroup())) {
            values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAM_STORAGE, Constants.STORAGE_INTERNAL_MANAGED);
            location =
                getItem().getHref() + Constants.CONTENT_STREAM_URL_PART + '/' + ds.getName()
                    + Constants.CONTENT_STREAM_CONTENT_URL_EXTENSION;
            if ("X".equals(ds.getControlGroup())) {
                try {
                    values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAM_CONTENT, ds.toStringUTF8());
                }
                catch (final EncodingSystemException e) {
                    throw new WebserverSystemException(e);
                }
            }
        }
        else if ("E".equals(ds.getControlGroup())) {
            values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAM_STORAGE, Constants.STORAGE_EXTERNAL_MANAGED);
            location =
                getItem().getHref() + Constants.CONTENT_STREAM_URL_PART + '/' + ds.getName()
                    + Constants.CONTENT_STREAM_CONTENT_URL_EXTENSION;

        }
        else if ("R".equals(ds.getControlGroup())) {
            values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAM_STORAGE, Constants.STORAGE_EXTERNAL_URL);
        }
        values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAM_MIME_TYPE, ds.getMimeType());
        values.put(XmlTemplateProviderConstants.VAR_CONTENT_STREAM_HREF, location);

        return ItemXmlProvider.getInstance().getContentStreamXml(values);
    }

    public String renderComponentMdRecords(final String componentId, final boolean isRoot)
        throws ComponentNotFoundException, SystemException {

        return renderComponentMdRecords(componentId, getCommonValues(getItem()), isRoot);
    }

    public String renderComponentMdRecords(
        final String componentId, final Map<String, String> commonValues, final boolean isRoot)
        throws ComponentNotFoundException, SystemException {

        final Component component;
        final Map<String, String> values = new HashMap<String, String>();
        final String originObjectId = getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        if (originObjectId != null) {
            component = getComponent(componentId);
            values.put(XmlTemplateProviderConstants.ORIGIN, XmlTemplateProviderConstants.TRUE);
            values.put(XmlTemplateProviderConstants.VAR_MD_RECORDS_HREF,
                de.escidoc.core.common.business.Constants.ITEM_URL_BASE + getOriginId() + component.getHrefPart()
                    + de.escidoc.core.common.business.Constants.MD_RECORDS_URL_PART);
            values.putAll(getCommonValues(getOriginItem()));
        }
        else {
            component = getComponent(componentId);
            values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_HREF, getItem().getHref() + component.getHrefPart()
                + de.escidoc.core.common.business.Constants.MD_RECORDS_URL_PART);
            values.putAll(getCommonValues(getItem()));
        }
        final Map<String, Datastream> mdRecords = component.getMdRecords();
        final StringBuilder content = new StringBuilder();
        for (final String mdRecordName : mdRecords.keySet()) {
            content.append(renderComponentMdRecord(componentId, mdRecordName, commonValues, false));
        }
        if (!isRoot && content.length() == 0) {
            return "";
        }

        if (isRoot) {
            values.put(XmlTemplateProviderConstants.IS_ROOT_SUB_RESOURCE, XmlTemplateProviderConstants.TRUE);
        }
        values.putAll(commonValues);
        values.put("mdRecordsHref", getItem().getHref() + component.getHrefPart()
            + de.escidoc.core.common.business.Constants.MD_RECORDS_URL_PART);
        values.put("mdRecordsTitle", "Metadata Records of Component " + component.getId());
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORDS_CONTENT, content.toString());
        return ItemXmlProvider.getInstance().getMdRecordsXml(values);
    }

    public String renderComponentMdRecord(final String componentId, final String name, final boolean isRoot)
        throws ComponentNotFoundException, FedoraSystemException, WebserverSystemException, EncodingSystemException,
        TripleStoreSystemException, IntegritySystemException, XmlParserSystemException {

        return renderComponentMdRecord(componentId, name, getCommonValues(getItem()), isRoot);
    }

    public String renderComponentMdRecord(
        final String componentId, final String name, final Map<String, String> commonValues, final boolean isRoot)
        throws ComponentNotFoundException, FedoraSystemException, TripleStoreSystemException, EncodingSystemException,
        WebserverSystemException, IntegritySystemException, XmlParserSystemException {

        final Component component;
        final Map<String, String> values = new HashMap<String, String>();
        final String originObjectId = getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        if (originObjectId != null) {
            component = getComponent(componentId);
            values.put(XmlTemplateProviderConstants.ORIGIN, XmlTemplateProviderConstants.TRUE);
            values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_HREF,
                de.escidoc.core.common.business.Constants.ITEM_URL_BASE + getOriginId() + component.getHrefPart()
                    + de.escidoc.core.common.business.Constants.MD_RECORD_URL_PART + '/' + name);
            values.putAll(getCommonValues(getOriginItem()));
        }
        else {
            component = getComponent(componentId);
            values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_HREF, getItem().getHref() + component.getHrefPart()
                + de.escidoc.core.common.business.Constants.MD_RECORD_URL_PART + '/' + name);
            values.putAll(commonValues);
        }

        final Datastream ds = component.getMdRecord(name);
        if (ds.isDeleted()) {
            return "";
        }
        final List<String> altIds = ds.getAlternateIDs();
        if (altIds.size() > 1 && !de.escidoc.core.common.business.Constants.UNKNOWN.equals(altIds.get(1))) {
            values.put(XmlTemplateProviderConstants.MD_RECORD_TYPE, altIds.get(1));
        }
        if (altIds.size() > 2 && !de.escidoc.core.common.business.Constants.UNKNOWN.equals(altIds.get(2))) {
            values.put(XmlTemplateProviderConstants.MD_RECORD_SCHEMA, altIds.get(2));
        }
        try {
            values.put(XmlTemplateProviderConstants.MD_RECORD_CONTENT, ds.toString(XmlUtility.CHARACTER_ENCODING));
        }
        catch (final EncodingSystemException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }

        if (isRoot) {
            values.put(XmlTemplateProviderConstants.IS_ROOT_MD_RECORD, XmlTemplateProviderConstants.TRUE);
        }
        values.put(XmlTemplateProviderConstants.MD_RECORD_NAME, name);
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_TITLE, name);
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_HREF, getItem().getHref() + component.getHrefPart()
            + de.escidoc.core.common.business.Constants.MD_RECORD_URL_PART + '/' + name);
        return ItemXmlProvider.getInstance().getMdRecordXml(values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface
     * #renderProperties(de.escidoc.core.common.business.fedora.resources.Item)
     */
    @Override
    public String renderProperties() throws WebserverSystemException, TripleStoreSystemException,
        IntegritySystemException, XmlParserSystemException, EncodingSystemException, FedoraSystemException,
        ItemNotFoundException {

        final Map<String, String> values = new HashMap<String, String>();
        values.put(XmlTemplateProviderConstants.IS_ROOT_PROPERTIES, XmlTemplateProviderConstants.TRUE);
        if (getOriginItem() != null) {
            values.put(XmlTemplateProviderConstants.ORIGIN, XmlTemplateProviderConstants.TRUE);
        }
        values.putAll(getCommonValues(getItem()));
        values.putAll(getPropertiesValues(getItem()));
        return ItemXmlProvider.getInstance().getItemPropertiesXml(values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface
     * #renderRelations(de.escidoc.core.common.business.fedora.resources.Item)
     */
    @Override
    public String renderRelations() throws WebserverSystemException, FedoraSystemException, IntegritySystemException,
        XmlParserSystemException, TripleStoreSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootRelations", XmlTemplateProviderConstants.TRUE);
        values.putAll(getCommonValues(getItem()));
        values.putAll(getRelationValues(getItem()));
        return ItemXmlProvider.getInstance().getItemRelationsXml(values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface
     * #renderResources(de.escidoc.core.common.business.fedora.resources.Item)
     */
    @Override
    public String renderResources() throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.put(XmlTemplateProviderConstants.IS_ROOT_PROPERTIES, XmlTemplateProviderConstants.TRUE);
        values.putAll(getCommonValues(getItem()));
        values.putAll(getResourcesValues(getItem()));

        // set this true if the reources should point to the
        // digilib client (digicat). The item XML Schema is currently not
        // compaptible with the this extension.
        values.put("showContentTransformer", XmlTemplateProviderConstants.FALSE);
        return ItemXmlProvider.getInstance().getItemResourcesXml(values);
    }

    /**
     * Gets the representation of the virtual resource {@code parents} of an item/container.
     *
     * @param itemId
     * @return Returns the XML representation of the virtual resource {@code parents} of an container.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    public String renderParents(final String itemId) throws WebserverSystemException, TripleStoreSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        addXlinkValues(values);
        addStructuralRelationsValues(values);
        values.put("isRootParents", XmlTemplateProviderConstants.TRUE);
        values.put(XmlTemplateProviderConstants.VAR_LAST_MODIFICATION_DATE, ISODateTimeFormat.dateTime().withZone(
            DateTimeZone.UTC).print(System.currentTimeMillis()));
        addParentsValues(values, itemId);
        addParentsNamespaceValues(values);
        return ItemXmlProvider.getInstance().getParentsXml(values);
    }

    /**
     * Adds the parents values to the provided map.
     *
     * @param values The map to add values to.
     * @param itemId
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    private void addParentsValues(final Map<String, Object> values, final String itemId)
        throws TripleStoreSystemException {
        values.put("parentsHref", XmlUtility.getItemParentsHref(XmlUtility.getItemHref(itemId)));
        values.put("parentsTitle", "parents of item " + itemId);

        final StringBuffer query =
            getTripleStoreUtility().getRetrieveSelectClause(true, TripleStoreUtility.PROP_MEMBER);

        if (query.length() > 0) {
            query.append(getTripleStoreUtility().getRetrieveWhereClause(true, TripleStoreUtility.PROP_MEMBER, itemId,
                null, null, null));
            List<String> ids = new ArrayList<String>();
            try {
                ids = getTripleStoreUtility().retrieve(query.toString());
            }
            catch (final TripleStoreSystemException e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Error on retrieving data from triple store.");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error on retrieving data from triple store.", e);
                }
            }
            final Iterator<String> idIter = ids.iterator();
            final Collection<Map<String, String>> entries = new ArrayList<Map<String, String>>(ids.size());
            while (idIter.hasNext()) {
                final Map<String, String> entry = new HashMap<String, String>(3);
                final String id = idIter.next();
                entry.put("id", id);
                entry.put("href", XmlUtility.getContainerHref(id));
                entry.put("title", getTripleStoreUtility().getTitle(id));

                entries.add(entry);
            }
            if (!entries.isEmpty()) {
                values.put(XmlTemplateProviderConstants.VAR_PARENTS, entries);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface
     * #renderItems(java.util.List)
     */
    @Override
    public String renderItems(final List<String> items) throws SystemException {

        final Collection<String> renderedEntries = new ArrayList<String>();
        final Map<String, Object> values = new HashMap<String, Object>();

        for (final String itemId : items) {
            try {
                setItem(itemId);
                renderedEntries.add(render());
            }
            catch (final ResourceNotFoundException e) {
                throw new WebserverSystemException("FedoraItemHandler.retrieveItems: can not retrieve object " + itemId
                    + ". ResourceNotFoundException: " + e.getCause() + '.', e);
            }
        }
        values.put(XmlTemplateProviderConstants.VAR_ITEM_LIST_MEMBERS, renderedEntries);

        values.put(XmlTemplateProviderConstants.VAR_ITEM_LIST_TITLE, "list of items");
        values.put(XmlTemplateProviderConstants.VAR_ITEM_LIST_NAMESPACE,
            de.escidoc.core.common.business.Constants.ITEM_LIST_NAMESPACE_URI);
        values.put(XmlTemplateProviderConstants.VAR_ITEM_LIST_NAMESPACE_PREFIX,
            de.escidoc.core.common.business.Constants.ITEM_LIST_NAMESPACE_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_ESCIDOC_BASE_URL, XmlUtility.getEscidocBaseUrl());

        return getItemXmlProvider().getItemListXml(values);
    }

    /**
     * Prepare properties values from item resource as velocity values.
     *
     * @param item The Item.
     * @return Map with properties values (for velocity template)
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    public Map<String, String> getPropertiesValues(final Item item) throws TripleStoreSystemException,
        WebserverSystemException, IntegritySystemException, EncodingSystemException, ItemNotFoundException {

        // retrieve properties from resource (the resource decided where are the
        // data to load, TripleStore or Wov)

        final Map<String, String> properties = item.getResourceProperties();

        final Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProviderConstants.VAR_PROPERTIES_TITLE, "Properties");
        values.put(XmlTemplateProviderConstants.VAR_PROPERTIES_HREF, item.getHref() + Constants.PROPERTIES_URL_PART);

        // surrogate item
        String origin = properties.get(PropertyMapKeys.ORIGIN);
        if (origin != null) {
            values.put(XmlTemplateProviderConstants.ORIGIN, XmlTemplateProviderConstants.TRUE);
            final String originVersion = properties.get(PropertyMapKeys.ORIGIN_VERSION);
            if (originVersion != null) {
                origin = origin + ':' + originVersion;
            }
            values.put(XmlTemplateProviderConstants.VAR_ORIGIN_ID, origin);
            values.put(XmlTemplateProviderConstants.VAR_ITEM_ORIGIN_HREF,
                de.escidoc.core.common.business.Constants.ITEM_URL_BASE + getOriginId());
            values.put(XmlTemplateProviderConstants.VAR_ITEM_ORIGIN_TITLE, getOriginItem().getTitle());
        }

        try {
            final String creationDate = item.getCreationDate();
            values.put(XmlTemplateProviderConstants.VAR_ITEM_CREATION_DATE, creationDate);
        }
        catch (final TripleStoreSystemException e) {
            throw new ItemNotFoundException(e);
        }

        values.put(XmlTemplateProviderConstants.VAR_ITEM_CREATED_BY_TITLE, properties
            .get(PropertyMapKeys.CREATED_BY_TITLE));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CREATED_BY_HREF,
            de.escidoc.core.common.business.Constants.USER_ACCOUNT_URL_BASE
                + properties.get(PropertyMapKeys.CREATED_BY_ID));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CREATED_BY_ID, properties.get(PropertyMapKeys.CREATED_BY_ID));

        values.put(XmlTemplateProviderConstants.VAR_ITEM_CONTEXT_TITLE, properties
            .get(PropertyMapKeys.CURRENT_VERSION_CONTEXT_TITLE));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CONTEXT_HREF,
            de.escidoc.core.common.business.Constants.CONTEXT_URL_BASE
                + properties.get(PropertyMapKeys.CURRENT_VERSION_CONTEXT_ID));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CONTEXT_ID, properties
            .get(PropertyMapKeys.CURRENT_VERSION_CONTEXT_ID));

        values.put(XmlTemplateProviderConstants.VAR_ITEM_CONTENT_MODEL_TITLE, properties
            .get(PropertyMapKeys.CURRENT_VERSION_CONTENT_MODEL_TITLE));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CONTENT_MODEL_HREF,
            de.escidoc.core.common.business.Constants.CONTENT_MODEL_URL_BASE
                + properties.get(PropertyMapKeys.CURRENT_VERSION_CONTENT_MODEL_ID));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CONTENT_MODEL_ID, properties
            .get(PropertyMapKeys.CURRENT_VERSION_CONTENT_MODEL_ID));

        values.put(XmlTemplateProviderConstants.VAR_ITEM_STATUS, item.getStatus());
        values.put(XmlTemplateProviderConstants.VAR_ITEM_STATUS_COMMENT, XmlUtility
            .escapeForbiddenXmlCharacters(properties.get(PropertyMapKeys.PUBLIC_STATUS_COMMENT)));

        if (item.hasObjectPid()) {
            values.put(XmlTemplateProviderConstants.VAR_ITEM_OBJECT_PID, item.getObjectPid());
        }

        if (item.isLocked()) {
            values.put(XmlTemplateProviderConstants.VAR_ITEM_LOCK_STATUS,
                de.escidoc.core.common.business.Constants.STATUS_LOCKED);
            values.put(XmlTemplateProviderConstants.VAR_ITEM_LOCK_DATE, item.getLockDate());
            final String lockOwnerId = item.getLockOwner();
            values.put(XmlTemplateProviderConstants.VAR_ITEM_LOCK_OWNER_ID, lockOwnerId);
            values.put(XmlTemplateProviderConstants.VAR_ITEM_LOCK_OWNER_HREF,
                de.escidoc.core.common.business.Constants.USER_ACCOUNT_URL_BASE + lockOwnerId);
            values.put(XmlTemplateProviderConstants.VAR_ITEM_LOCK_OWNER_TITLE, item.getLockOwnerTitle());
            // TODO lock-date
        }
        else {
            values.put(XmlTemplateProviderConstants.VAR_ITEM_LOCK_STATUS,
                de.escidoc.core.common.business.Constants.STATUS_UNLOCKED);
        }

        // version
        final StringBuilder versionIdBase = new StringBuilder(item.getId()).append(':');

        values.put(XmlTemplateProviderConstants.VAR_ITEM_CURRENT_VERSION_HREF, item.getVersionHref());
        // de.escidoc.core.common.business.Constants.ITEM_URL_BASE
        // + currentVersionId);
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CURRENT_VERSION_ID, item.getFullId());
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CURRENT_VERSION_TITLE, "This Version");
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CURRENT_VERSION_NUMBER, item.getVersionId());
        // properties.get(TripleStoreUtility.PROP_CURRENT_VERSION_NUMBER));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CURRENT_VERSION_DATE, item.getVersionDate().toString());
        // properties.get(TripleStoreUtility.PROP_VERSION_DATE));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CURRENT_VERSION_STATUS, item.getVersionStatus());
        // properties.get(TripleStoreUtility.PROP_CURRENT_VERSION_STATUS));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CURRENT_VERSION_VALID_STATUS, properties
            .get(PropertyMapKeys.CURRENT_VERSION_VALID_STATUS));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CURRENT_VERSION_COMMENT, XmlUtility
            .escapeForbiddenXmlCharacters(properties.get(PropertyMapKeys.CURRENT_VERSION_VERSION_COMMENT)));

        values.put(XmlTemplateProviderConstants.VAR_ITEM_CURRENT_VERSION_MODIFIED_BY_ID, properties
            .get(PropertyMapKeys.CURRENT_VERSION_MODIFIED_BY_ID));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CURRENT_VERSION_MODIFIED_BY_TITLE, properties
            .get(PropertyMapKeys.CURRENT_VERSION_MODIFIED_BY_TITLE));

        // href is rest only value
        values.put(XmlTemplateProviderConstants.VAR_ITEM_CURRENT_VERSION_MODIFIED_BY_HREF,
            de.escidoc.core.common.business.Constants.USER_ACCOUNT_URL_BASE
                + properties.get(PropertyMapKeys.CURRENT_VERSION_MODIFIED_BY_ID));

        // PID ---------------------------------------------------
        if (item.hasVersionPid()) {
            values.put(XmlTemplateProviderConstants.VAR_ITEM_VERSION_PID, item.getVersionPid());
        }

        final String latestVersionId = item.getLatestVersionId();
        values.put(XmlTemplateProviderConstants.VAR_ITEM_LATEST_VERSION_HREF,
            de.escidoc.core.common.business.Constants.ITEM_URL_BASE + latestVersionId);
        values.put(XmlTemplateProviderConstants.VAR_ITEM_LATEST_VERSION_TITLE, "Latest Version");
        values.put(XmlTemplateProviderConstants.VAR_ITEM_LATEST_VERSION_ID, latestVersionId);
        values.put(XmlTemplateProviderConstants.VAR_ITEM_LATEST_VERSION_NUMBER, properties
            .get(PropertyMapKeys.LATEST_VERSION_NUMBER));
        values.put(XmlTemplateProviderConstants.VAR_ITEM_LATEST_VERSION_DATE, properties
            .get(PropertyMapKeys.LATEST_VERSION_DATE));

        // if item is released -------------------------------------------------
        if (properties.get(PropertyMapKeys.LATEST_RELEASE_VERSION_NUMBER) != null) {

            values.put(XmlTemplateProviderConstants.VAR_ITEM_LATEST_RELEASE_NUMBER, properties
                .get(PropertyMapKeys.LATEST_RELEASE_VERSION_NUMBER));

            // ! changes versionIdBase
            final String latestRevisonId =
                versionIdBase.append(properties.get(PropertyMapKeys.LATEST_RELEASE_VERSION_NUMBER)).toString();
            values.put(XmlTemplateProviderConstants.VAR_ITEM_LATEST_RELEASE_HREF,
                de.escidoc.core.common.business.Constants.ITEM_URL_BASE + latestRevisonId);
            values.put(XmlTemplateProviderConstants.VAR_ITEM_LATEST_RELEASE_TITLE, "Latest public version");
            values.put(XmlTemplateProviderConstants.VAR_ITEM_LATEST_RELEASE_ID, latestRevisonId);
            values.put(XmlTemplateProviderConstants.VAR_ITEM_LATEST_RELEASE_DATE, properties
                .get(PropertyMapKeys.LATEST_RELEASE_VERSION_DATE));

            final String latestReleasePid = item.getLatestReleasePid();
            if (latestReleasePid != null) {
                values.put(XmlTemplateProviderConstants.VAR_ITEM_LATEST_RELEASE_PID, latestReleasePid);
            }
        }

        final Datastream contentModelSpecific = item.getCts();
        if (contentModelSpecific != null) {
            values.put(XmlTemplateProviderConstants.VAR_ITEM_CONTENT_MODEL_SPECIFIC, contentModelSpecific
                .toStringUTF8());
        }
        values.put(XmlTemplateProviderConstants.VAR_NAMESPACE_PREFIX,
            de.escidoc.core.common.business.Constants.ITEM_PROPERTIES_NAMESPACE_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_NAMESPACE,
            de.escidoc.core.common.business.Constants.ITEM_PROPERTIES_NAMESPACE_URI);

        return values;
    }

    /**
     * Get the content type specific properties.
     *
     * @return The content type specific properties.
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    protected String getContentTypeSpecificPropertiesXml() throws EncodingSystemException {

        return getItem().getCts().toStringUTF8();
    }

    public Set checkRelations(final String versionDate, final Map relations) throws TripleStoreSystemException,
        WebserverSystemException, FedoraSystemException, IntegritySystemException {
        final Set relationsData = relations.entrySet();
        final Iterator it = relationsData.iterator();
        while (it.hasNext()) {
            final Entry relData = (Entry) it.next();
            final String id = (String) relData.getKey();
            final Relation relation;
            try {
                relation = new Relation(id);
            }
            catch (final ResourceNotFoundException e) {
                throw new WebserverSystemException("unreachable", e);
            }
            final byte[] wov;
            try {
                wov = relation.getWov().getStream();
            }
            catch (final StreamNotFoundException e) {
                throw new IntegritySystemException("unreachable", e);
            }
            final StaxParser sp = new StaxParser();

            final WovContentRelationsRetrieveHandler wovHandler =
                new WovContentRelationsRetrieveHandler(sp, versionDate);
            sp.addHandler(wovHandler);
            try {
                sp.parse(wov);
                sp.clearHandlerChain();
            }
            catch (final Exception e) {
                throw new WebserverSystemException("unreachable", e);
            }
            final String status = wovHandler.getStatus();
            if ("inactive".equals(status)) {
                it.remove();
            }

        }
        return relationsData;
    }

    /**
     * Get Common values from Item.
     *
     * @param item The item.
     * @return Map with common Item values.
     * @throws WebserverSystemException Thrown if values extracting failed.
     */
    private Map<String, String> getCommonValues(final Item item) throws WebserverSystemException {

        final Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProviderConstants.OBJID, getItem().getId());

        // If later while rendering of md-records the mandatory md-record
        // will be imported from the original item, the
        // title will be overridden in a this map by a title of a original item
        values.put(XmlTemplateProviderConstants.TITLE, getItem().getTitle());

        values.put(XmlTemplateProviderConstants.HREF, getItem().getHref());

        try {
            values.put(XmlTemplateProviderConstants.VAR_LAST_MODIFICATION_DATE, item
                .getLastModificationDate().toString());
        }
        catch (final FedoraSystemException e) {
            throw new WebserverSystemException(e);
        }

        values.put("itemNamespacePrefix", de.escidoc.core.common.business.Constants.ITEM_NAMESPACE_PREFIX);
        values.put("itemNamespace", de.escidoc.core.common.business.Constants.ITEM_NAMESPACE_URI);
        values.put(XmlTemplateProviderConstants.VAR_ESCIDOC_BASE_URL, XmlUtility.getEscidocBaseUrl());
        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE_PREFIX,
            de.escidoc.core.common.business.Constants.XLINK_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE,
            de.escidoc.core.common.business.Constants.XLINK_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX,
            de.escidoc.core.common.business.Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS,
            de.escidoc.core.common.business.Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX,
            de.escidoc.core.common.business.Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS,
            de.escidoc.core.common.business.Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put("versionNamespacePrefix", de.escidoc.core.common.business.Constants.VERSION_NS_PREFIX);
        values.put("versionNamespace", de.escidoc.core.common.business.Constants.VERSION_NS_URI);
        values.put("releaseNamespacePrefix", de.escidoc.core.common.business.Constants.RELEASE_NS_PREFIX);
        values.put("releaseNamespace", de.escidoc.core.common.business.Constants.RELEASE_NS_URI);
        values.put("componentsNamespacePrefix", de.escidoc.core.common.business.Constants.COMPONENTS_NAMESPACE_PREFIX);
        values.put("componentsNamespace", de.escidoc.core.common.business.Constants.COMPONENTS_NAMESPACE_URI);

        values.put(XmlTemplateProviderConstants.MD_RECRORDS_NAMESPACE_PREFIX,
            de.escidoc.core.common.business.Constants.METADATARECORDS_NAMESPACE_PREFIX);
        values.put(XmlTemplateProviderConstants.MD_RECORDS_NAMESPACE,
            de.escidoc.core.common.business.Constants.METADATARECORDS_NAMESPACE_URI);

        values.put("contentStreamsNamespacePrefix",
            de.escidoc.core.common.business.Constants.CONTENT_STREAMS_NAMESPACE_PREFIX);
        values.put("contentStreamsNamespace", de.escidoc.core.common.business.Constants.CONTENT_STREAMS_NAMESPACE_URI);

        return values;
    }

    private Map<String, Object> getRelationValues(final Item item) throws FedoraSystemException,
        IntegritySystemException, XmlParserSystemException, WebserverSystemException, TripleStoreSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("contentRelationsNamespacePrefix",
            de.escidoc.core.common.business.Constants.CONTENT_RELATIONS_NAMESPACE_PREFIX);
        values.put("contentRelationsNamespace",
            de.escidoc.core.common.business.Constants.CONTENT_RELATIONS_NAMESPACE_URI);
        this.velocityXmlCommonRenderer.addRelationsValues(item.getRelations(), item.getHref(), values);
        values.put("contentRelationsTitle", "Relations of Item");

        return values;
    }

    protected void addParentsNamespaceValues(final Map values) {
        values.put("parentsNamespacePrefix", de.escidoc.core.common.business.Constants.PARENTS_NAMESPACE_PREFIX);
        values.put("parentsNamespace", de.escidoc.core.common.business.Constants.PARENTS_NAMESPACE_URI);

    }

    protected void addXlinkValues(final Map values) {

        values.put(XmlTemplateProviderConstants.VAR_ESCIDOC_BASE_URL, XmlUtility.getEscidocBaseUrl());
        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE_PREFIX,
            de.escidoc.core.common.business.Constants.XLINK_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE,
            de.escidoc.core.common.business.Constants.XLINK_NS_URI);
    }

    protected void addStructuralRelationsValues(final Map values) {
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX,
            de.escidoc.core.common.business.Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS,
            de.escidoc.core.common.business.Constants.STRUCTURAL_RELATIONS_NS_URI);
    }

    private Map<String, Object> getResourcesValues(final FedoraResource item) throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.put(XmlTemplateProviderConstants.RESOURCES_TITLE, "Resources");
        values.put("resourcesHref", item.getHref() + "/resources");

        // add operations from Fedora service definitions
        // FIXME use item properties instead of triplestore util
        try {
            values.put("resourceOperationNames", getTripleStoreUtility().getMethodNames(item.getId()));
        }
        catch (final TripleStoreSystemException e) {
            throw new WebserverSystemException(e);
        }

        return values;
    }

    /**
     * Get properties values of the Component.
     *
     * @param component The Component.
     * @return Map with component properties values.
     * @throws TripleStoreSystemException Thrown if TripleStore request failed.
     * @throws WebserverSystemException   Thrown in case of internal failure.
     */
    private Map<String, String> getComponentPropertiesValues(final Component component)
        throws TripleStoreSystemException, WebserverSystemException {

        final String baseHRef = getItem().getHref() + component.getHrefPart();
        // TODO version
        final Map<String, String> values = new HashMap<String, String>();
        values.put(XmlTemplateProviderConstants.VAR_COMPONENT_PROPERTIES_TITLE, "Properties");
        values
            .put(XmlTemplateProviderConstants.VAR_COMPONENT_PROPERTIES_HREF, baseHRef + Constants.PROPERTIES_URL_PART);

        if (component.getProperty(de.escidoc.core.common.business.Constants.DC_NS_URI + Elements.ELEMENT_DESCRIPTION) != null
            && component.getMdRecords().containsKey(XmlTemplateProviderConstants.DEFAULT_METADATA_FOR_DC_MAPPING)
            && !component.getMdRecord(XmlTemplateProviderConstants.DEFAULT_METADATA_FOR_DC_MAPPING).isDeleted()) {
            values.put(XmlTemplateProviderConstants.VAR_COMPONENT_DESCRIPTION, component
                .getProperty(de.escidoc.core.common.business.Constants.DC_NS_URI + Elements.ELEMENT_DESCRIPTION));
        }
        values.put(XmlTemplateProviderConstants.VAR_COMPONENT_CREATION_DATE, component.getCreationDate());
        values.put(XmlTemplateProviderConstants.VAR_COMPONENT_CREATED_BY_TITLE, component
            .getProperty(TripleStoreUtility.PROP_CREATED_BY_TITLE));
        values.put(XmlTemplateProviderConstants.VAR_COMPONENT_CREATED_BY_HREF,
            de.escidoc.core.common.business.Constants.USER_ACCOUNT_URL_BASE
                + component.getProperty(TripleStoreUtility.PROP_CREATED_BY_ID));
        values.put(XmlTemplateProviderConstants.VAR_COMPONENT_CREATED_BY_ID, component
            .getProperty(TripleStoreUtility.PROP_CREATED_BY_ID));

        if (component.getProperty(TripleStoreUtility.PROP_VALID_STATUS) != null) {
            values.put(XmlTemplateProviderConstants.VAR_COMPONENT_VALID_STATUS, component
                .getProperty(TripleStoreUtility.PROP_VALID_STATUS));
        }

        values.put(XmlTemplateProviderConstants.VAR_COMPONENT_VISIBILITY, component
            .getProperty(TripleStoreUtility.PROP_VISIBILITY));

        values.put(XmlTemplateProviderConstants.VAR_COMPONENT_CONTENT_CATEGORY, component
            .getProperty(TripleStoreUtility.PROP_CONTENT_CATEGORY));

        if (component.getProperty(TripleStoreUtility.PROP_MIME_TYPE) != null) {
            values.put(XmlTemplateProviderConstants.VAR_COMPONENT_MIME_TYPE, component
                .getProperty(TripleStoreUtility.PROP_MIME_TYPE));
        }

        if (component.getProperty(de.escidoc.core.common.business.Constants.DC_NS_URI + Elements.ELEMENT_DC_TITLE) != null
            && component.getMdRecords().containsKey(XmlTemplateProviderConstants.DEFAULT_METADATA_FOR_DC_MAPPING)
            && !component.getMdRecord(XmlTemplateProviderConstants.DEFAULT_METADATA_FOR_DC_MAPPING).isDeleted()) {
            values.put(XmlTemplateProviderConstants.VAR_COMPONENT_FILE_NAME, component
                .getProperty(de.escidoc.core.common.business.Constants.DC_NS_URI + Elements.ELEMENT_DC_TITLE));
        }
        if (component.getProperty(TripleStoreUtility.PROP_COMPONENT_PID) != null) {
            values.put(XmlTemplateProviderConstants.VAR_COMPONENT_PID, component
                .getProperty(TripleStoreUtility.PROP_COMPONENT_PID));
        }

        values.put(XmlTemplateProviderConstants.CONTENT_CHECKSUM_ALGORITHM, component
            .getProperty(Elements.ELEMENT_COMPONENT_CONTENT_CHECKSUM_ALGORITHM));
        values.put(XmlTemplateProviderConstants.CONTENT_CHECKSUM, component
            .getProperty(Elements.ELEMENT_COMPONENT_CONTENT_CHECKSUM));

        return values;
    }
}
