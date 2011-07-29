/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.util.xml.factory;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.create.ContentRelationCreate;
import de.escidoc.core.common.business.fedora.resources.create.MdRecordCreate;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XML Provider of ContentRelation.
 *
 * @author Steffen Wagner
 */
@Service
public final class ContentRelationXmlProvider extends InfrastructureXmlProvider {

    private static final String CONTENT_RELATION_PATH = "/content-relation";

    private static final String RESOURCES_PATH = CONTENT_RELATION_PATH;

    private static final String DATA_PATH = CONTENT_RELATION_PATH;

    private static final String CONTENT_RELATION_RESOURCE_NAME = "content-relation";

    private static final String RESOURCE_WITHDRAWN_NAME = "withdrawn";

    private static final String PROPERTIES_RESOURCE_NAME = "properties";

    private static final String RESOURCES_RESOURCE_NAME = "resources";

    private static final String COMMON_PATH = "/common";

    private static final String MD_RECORDS_RESOURCE_NAME = "md-records";

    private static final String MD_RECORDS_PATH = COMMON_PATH;

    public static final String MD_RECORD_PATH = COMMON_PATH;

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected ContentRelationXmlProvider() {
    }

    /**
     * Render XML representation of Content Relation.
     *
     * @param cr ContentRelation
     * @return XML representation
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    public String getContentRelationXml(final ContentRelationCreate cr) throws TripleStoreSystemException,
        WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        // put all relevant values from object into value Map
        final Map<String, String> commonValues = getCommonValues();

        values.put(XmlTemplateProviderConstants.IS_ROOT_PROPERTIES, XmlTemplateProviderConstants.FALSE);
        values.put(XmlTemplateProviderConstants.VAR_PROPERTIES_HREF, "/ir" + CONTENT_RELATION_PATH + '/'
            + cr.getObjid() + "/properties");
        values.put(XmlTemplateProviderConstants.VAR_PROPERTIES_TITLE, "Content Relation Properties");

        values.put(XmlTemplateProviderConstants.VAR_MD_RECORDS_CONTENT, renderMdRecords(cr, commonValues, false));
        values.putAll(getResourceValues(cr));
        values.putAll(getLockValues(cr));
        values.putAll(commonValues);
        values.putAll(getRelationValues(cr));

        return getXml(CONTENT_RELATION_RESOURCE_NAME, CONTENT_RELATION_PATH, values);
    }

    /**
     * Get XML representation of ContentRelation md-records.
     *
     * @param cr ContentRelation
     * @return XML representation of md-records.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    public String getContentRelationMdRecords(final ContentRelationCreate cr) throws WebserverSystemException {

        final Map<String, String> commonValues = getCommonValues();
        return renderMdRecords(cr, commonValues, true);
    }

    /**
     * Get XML representation of ContentRelation md-record with a provided name.
     *
     * @param cr ContentRelation
     * @param mr meta date record
     * @return XML representation of md-record.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException
     */
    public String getContentRelationMdRecord(final ContentRelationCreate cr, final MdRecordCreate mr)
        throws MdRecordNotFoundException, WebserverSystemException {

        final Map<String, String> commonValues = getCommonValues();
        final String mdRecord = renderMdRecord(cr, mr, commonValues, true);
        if (mdRecord.length() == 0) {
            throw new MdRecordNotFoundException("A content relation with id " + cr.getObjid()
                + "does not contain a md-record with a name " + mr.getName() + " .");

        }
        return mdRecord;
    }

    /**
     * Render XML representation of Content Relation.
     *
     * @param cr ContentRelation
     * @return XML representation
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    public String getContentRelationPropertiesXml(final ContentRelationCreate cr) throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        // put all relevant values from object into value Map
        final Map<String, String> commonValues = getCommonValues();

        values.putAll(getResourceValues(cr));
        values.putAll(getLockValues(cr));
        values.putAll(commonValues);
        values.put(XmlTemplateProviderConstants.IS_ROOT_PROPERTIES, XmlTemplateProviderConstants.TRUE);
        values.put(XmlTemplateProviderConstants.VAR_PROPERTIES_HREF, "/ir" + CONTENT_RELATION_PATH + '/'
            + cr.getObjid() + "/properties");
        values.put(XmlTemplateProviderConstants.VAR_PROPERTIES_TITLE, "Content Relation Properties");

        return getPropertiesXml(values);
    }

    /**
     * Get XML representation of ContentRelation properties.
     *
     * @param values Map of values.
     * @return XML representation of Properties.
     * @throws WebserverSystemException If anything fails.
     */
    public String getPropertiesXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(PROPERTIES_RESOURCE_NAME, CONTENT_RELATION_PATH, values);
    }

    /**
     * Get XML representation of ContentRelation (virtual-)resources.
     *
     * @param cr
     * @return XML representation of resources.
     * @throws WebserverSystemException If anything fails.
     */
    public String getContentRelationResourcesXml(final ContentRelationCreate cr) throws WebserverSystemException {

        final Map<String, String> values = getResourceValues(cr);
        values.putAll(getCommonValues());
        values.put(XmlTemplateProviderConstants.IS_ROOT_PROPERTIES, XmlTemplateProviderConstants.TRUE);
        return getXml(RESOURCES_RESOURCE_NAME, CONTENT_RELATION_PATH, values);
    }

    /**
     * Get XML representation of Data.
     *
     * @param values Map of values.
     * @return XML representation of context list.
     * @throws WebserverSystemException If anything fails.
     */
    public String getDataXml(final Map<String, Object> values) throws WebserverSystemException {

        return getXml(CONTENT_RELATION_RESOURCE_NAME, DATA_PATH, values);
    }

    /**
     * Get withdrawn message as XML snippet.
     *
     * @param values Value Map.
     * @return XML snippet with withdrawn message.
     * @throws WebserverSystemException If anything fails.
     */
    public String getWithdrawnMessageXml(final Map<String, Object> values) throws WebserverSystemException {
        return getXml(RESOURCE_WITHDRAWN_NAME, RESOURCES_PATH, values);
    }

    /**
     * Get common values for ContentRelation.
     *
     * @return Map with common ContentRelation values.
     * @throws WebserverSystemException Thrown if values extracting failed.
     */
    private static Map<String, String> getCommonValues() {

        final Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_NAMESPACE_PREFIX, Constants.RELATION_NAMESPACE_PREFIX);
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_NAMESPACE, Constants.CONTENT_RELATION_NAMESPACE_URI);

        values.put(XmlTemplateProviderConstants.VAR_ESCIDOC_BASE_URL, XmlUtility.getEscidocBaseUrl());
        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE_PREFIX, Constants.XLINK_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE, Constants.XLINK_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);

        values.put(XmlTemplateProviderConstants.MD_RECRORDS_NAMESPACE_PREFIX,
            Constants.METADATARECORDS_NAMESPACE_PREFIX);
        values.put(XmlTemplateProviderConstants.MD_RECORDS_NAMESPACE, Constants.METADATARECORDS_NAMESPACE_URI);

        return values;
    }

    /**
     * Get values from ContentRelation.
     *
     * @param cr The ContentRelation.
     * @return Map with common ContentRelation values.
     * @throws WebserverSystemException Thrown if values extracting failed.
     */
    private static Map<String, String> getResourceValues(final ContentRelationCreate cr) {

        final Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProviderConstants.OBJID, cr.getObjid());
        values.put(XmlTemplateProviderConstants.TITLE, cr.getProperties().getTitle());
        values.put(XmlTemplateProviderConstants.HREF, "/ir" + CONTENT_RELATION_PATH + '/' + cr.getObjid());

        values.put(XmlTemplateProviderConstants.RESOURCES_TITLE, "Virtual Resources");
        values.put("resourcesHref", XmlUtility.getContentRelationHref(cr.getObjid()) + "/resources");

        values.put(XmlTemplateProviderConstants.CREATED_BY_ID, cr.getProperties().getCreatedById());
        values.put(XmlTemplateProviderConstants.CREATED_BY_HREF, Constants.USER_ACCOUNT_URL_BASE
            + cr.getProperties().getCreatedById());
        values.put(XmlTemplateProviderConstants.CREATED_BY_TITLE, cr.getProperties().getCreatedByName());

        values.put(XmlTemplateProviderConstants.MODIFIED_BY_ID, cr.getProperties().getModifiedById());
        values.put(XmlTemplateProviderConstants.MODIFIED_BY_HREF, Constants.USER_ACCOUNT_URL_BASE
            + cr.getProperties().getModifiedById());
        values.put(XmlTemplateProviderConstants.MODIFIED_BY_TITLE, cr.getProperties().getModifiedByName());

        values.put(XmlTemplateProviderConstants.VAR_LAST_MODIFICATION_DATE, cr
            .getProperties().getLastModificationDate().withZone(DateTimeZone.UTC).toString(Constants.TIMESTAMP_FORMAT));
        values.put(XmlTemplateProviderConstants.VAR_CREATION_DATE, cr.getProperties().getCreationDate().withZone(
            DateTimeZone.UTC).toString(Constants.TIMESTAMP_FORMAT));

        if (cr.getProperties().getDescription() != null) {
            values.put(XmlTemplateProviderConstants.CONTENT_RELATION_DESCRIPTION, cr.getProperties().getDescription());
        }

        values.put(XmlTemplateProviderConstants.PUBLIC_STATUS, cr.getProperties().getStatus().toString());
        values.put(XmlTemplateProviderConstants.PUBLIC_STATUS_COMMENT, XmlUtility.escapeForbiddenXmlCharacters(cr
            .getProperties().getStatusComment()));
        values.put(XmlTemplateProviderConstants.OBJECT_PID, cr.getProperties().getPid());

        return values;
    }

    /**
     * Prepare values for resource lock in map.
     *
     * @param cr ContentRelation
     * @return Map with velocity keys
     */
    private static Map<String, String> getLockValues(final ContentRelationCreate cr) {

        final Map<String, String> values = new HashMap<String, String>();
        values.put(XmlTemplateProviderConstants.LOCK_STATUS, cr.getProperties().getLockStatus().toString());

        if (cr.getProperties().isLocked()) {
            values.put(XmlTemplateProviderConstants.VAR_CONTENT_MODEL_LOCK_OWNER_ID, cr
                .getProperties().getLockOwnerId());
            values.put(XmlTemplateProviderConstants.VAR_CONTENT_MODEL_LOCK_OWNER_HREF, Constants.USER_ACCOUNT_URL_BASE
                + cr.getProperties().getLockOwnerId());
            values.put(XmlTemplateProviderConstants.VAR_CONTENT_MODEL_LOCK_OWNER_TITLE, cr
                .getProperties().getLockOwnerName());
            values.put(XmlTemplateProviderConstants.VAR_CONTENT_MODEL_LOCK_DATE, cr
                .getProperties().getLockDate().withZone(DateTimeZone.UTC).toString(Constants.TIMESTAMP_FORMAT));
        }

        return values;

    }

    /**
     * Get relation values from ContentRelation.
     *
     * @param cr The ContentRelation.
     * @return Map with common ContentRelation values.
     * @throws WebserverSystemException   Thrown if values extracting failed.
     * @throws TripleStoreSystemException Thrown if obtaining resource type failed.
     */
    private Map<String, String> getRelationValues(final ContentRelationCreate cr) throws TripleStoreSystemException {

        final Map<String, String> values = new HashMap<String, String>();

        // type -----------------
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_TYPE, cr.getType().toString());

        // subject -----------------
        String objid = cr.getSubject();
        String version = cr.getSubjectVersion();
        String subjectId = objid;
        if (version != null) {
            subjectId = subjectId + ':' + version;
        }
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_SUBJECT_ID, subjectId);
        final String subjectType = this.tripleStoreUtility.getObjectType(objid);
        String subHref = this.tripleStoreUtility.getHref(subjectType, subjectId);
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_SUBJECT_HREF, subHref);
        // TODO: the title of an old version in the case of a fixed reference
        // to the old subject version
        // WRONG!!!!!
        values
            .put(XmlTemplateProviderConstants.CONTENT_RELATION_SUBJECT_TITLE, this.tripleStoreUtility.getTitle(objid));

        // object -----------------
        objid = cr.getObject();
        version = cr.getObjectVersion();
        String objectId = objid;
        if (version != null) {
            objectId = objectId + ':' + version;
        }
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_OBJECT_ID, objectId);
        final String objectType = this.tripleStoreUtility.getObjectType(objid);
        subHref = this.tripleStoreUtility.getHref(objectType, objectId);
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_OBJECT_HREF, subHref);
        // TODO: the title of an old version in the case of a fixed reference
        // to the old subject version
        // WRONG!!!!!
        values.put(XmlTemplateProviderConstants.CONTENT_RELATION_OBJECT_TITLE, this.tripleStoreUtility.getTitle(objid));

        return values;
    }

    /**
     * Render MD Record whereas the common values are not to recompile.
     *
     * @param cr           ContentRelationCreate
     * @param mdRecord     md-record.
     * @param commonValues Common render values.
     * @param isRoot       Set true is md-record is to render with XML root element
     * @return XMl representation of md-record.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    public String renderMdRecord(
        final ContentRelationCreate cr, final MdRecordCreate mdRecord, final Map<String, String> commonValues,
        final boolean isRoot) throws WebserverSystemException {

        final Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_HREF, Constants.CONTENT_RELATION_URL_BASE + cr.getObjid()
            + Constants.MD_RECORD_URL_PART + '/' + mdRecord.getName());
        if (!mdRecord.getType().equals(Constants.UNKNOWN)) {
            values.put(XmlTemplateProviderConstants.MD_RECORD_TYPE, mdRecord.getType());
        }
        if (!mdRecord.getSchema().equals(Constants.UNKNOWN)) {
            values.put(XmlTemplateProviderConstants.MD_RECORD_SCHEMA, mdRecord.getSchema());
        }

        // get md-record content
        final String content = mdRecord.getContent();
        if (content != null) {
            values.put(XmlTemplateProviderConstants.MD_RECORD_CONTENT, content);
        }

        if (isRoot) {
            values.put(XmlTemplateProviderConstants.IS_ROOT_MD_RECORD, XmlTemplateProviderConstants.TRUE);
        }
        values.putAll(commonValues);
        values.put(XmlTemplateProviderConstants.MD_RECORD_NAME, mdRecord.getName());
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_TITLE, mdRecord.getName());
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORD_HREF, Constants.CONTENT_RELATION_URL_BASE + cr.getObjid()
            + Constants.MD_RECORD_URL_PART + '/' + mdRecord.getName());

        return ItemXmlProvider.getInstance().getMdRecordXml(values);
    }

    /**
     * @param cr
     * @param commonValues
     * @param isRoot
     * @return XML representation off //md-records
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    public String renderMdRecords(
        final ContentRelationCreate cr, final Map<String, String> commonValues, final boolean isRoot)
        throws WebserverSystemException {

        final StringBuilder content = new StringBuilder();

        final List<MdRecordCreate> mdRecords = cr.getMetadataRecords();
        if (mdRecords != null) {
            for (final MdRecordCreate mdRecord : mdRecords) {
                final String mdRecordContent = renderMdRecord(cr, mdRecord, commonValues, false);
                content.append(mdRecordContent);
            }
        }
        if (content.length() == 0) {
            return "";
        }

        // prepare value map for Velocity
        final Map<String, String> values = new HashMap<String, String>();

        if (isRoot) {
            values.put(XmlTemplateProviderConstants.IS_ROOT_SUB_RESOURCE, XmlTemplateProviderConstants.TRUE);
        }
        values.putAll(commonValues);
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORDS_HREF, Constants.CONTENT_RELATION_URL_BASE
            + cr.getObjid() + Constants.MD_RECORDS_URL_PART);
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORDS_TITLE, "Metadata Records of Content Relation "
            + cr.getObjid());
        values.put(XmlTemplateProviderConstants.VAR_MD_RECORDS_CONTENT, content.toString());

        return getXml(MD_RECORDS_RESOURCE_NAME, MD_RECORDS_PATH, values);
    }

}
