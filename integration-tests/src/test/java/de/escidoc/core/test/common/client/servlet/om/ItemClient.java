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
package de.escidoc.core.test.common.client.servlet.om;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;
import de.escidoc.core.test.common.client.servlet.om.interfaces.SubmitReleaseReviseWithdrawClientInterface;
import de.escidoc.core.test.security.client.PWCallback;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Offers access methods to the escidoc interfaces of the item resource.
 *
 * @author Michael Schneider
 */
public class ItemClient extends ClientBase
    implements SubmitReleaseReviseWithdrawClientInterface, ResourceHandlerClientInterface {

    /**
     * Retrieve Items.
     *
     * @param filter The filter param.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveItems(final Map<String, String[]> filter) throws Exception {

        return callEsciDoc("Item.retrieveItems", METHOD_RETRIEVE_ITEMS, Constants.HTTP_METHOD_GET,
            Constants.ITEMS_BASE_URI, new String[] {}, filter);
    }

    /**
     * Create an item in the escidoc framework.
     *
     * @param itemXml The xml representation of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object create(final Object itemXml) throws Exception {

        return callEsciDoc("Item.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT, Constants.ITEM_BASE_URI,
            new String[] {}, changeToString(itemXml));
    }

    /**
     * Delete an item from the escidoc framework.
     *
     * @param id The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object delete(final String id) throws Exception {

        return callEsciDoc("Item.delete", METHOD_DELETE, Constants.HTTP_METHOD_DELETE, Constants.ITEM_BASE_URI,
            new String[] { id });
    }

    /**
     * Retrieve the xml representation of an item.
     *
     * @param id The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieve(final String id) throws Exception {

        return callEsciDoc("Item.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET, Constants.ITEM_BASE_URI,
            new String[] { id });
    }

    public Object addContentRelations(final String itemId, final String param) throws Exception {

        return callEsciDoc("Item.addContentRelations", METHOD_ADD_CONTENT_RELATIONS, Constants.HTTP_METHOD_POST,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_ADD_CONTENT_RELATIONS }, param);
    }

    public Object removeContentRelations(final String itemId, final String param) throws Exception {

        return callEsciDoc("Item.removeContentRelations", METHOD_REMOVE_CONTENT_RELATIONS, Constants.HTTP_METHOD_POST,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_REMOVE_CONTENT_RELATIONS }, param);
    }

    /**
     * Update an item in the escidoc framework.
     *
     * @param id      The id of the item.
     * @param itemXml The xml representation of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object update(final String id, final Object itemXml) throws Exception {

        return callEsciDoc("Item.update", METHOD_UPDATE, Constants.HTTP_METHOD_PUT, Constants.ITEM_BASE_URI,
            new String[] { id }, changeToString(itemXml));
    }

    /**
     * Add a Component to an Item.
     *
     * @param id        The id of the item.
     * @param component The new component.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object createComponent(final String id, final String component) throws Exception {

        return callEsciDoc("Item.createComponent", METHOD_CREATE_COMPONENT, Constants.HTTP_METHOD_PUT,
            Constants.ITEM_BASE_URI, new String[] { id, Constants.SUB_COMPONENT }, component);
    }

    /**
     * Retrieve a Component of an Item.
     *
     * @param itemId      The id of the item.
     * @param componentId The id of the component.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveComponent(final String itemId, final String componentId) throws Exception {

        return callEsciDoc("Item.retrieveComponent", METHOD_RETRIEVE_COMPONENT, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_COMPONENT, componentId });
    }

    /**
     * Delete a Component of an Item.
     *
     * @param itemId      The id of the item.
     * @param componentId The id of the component.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object deleteComponent(final String itemId, final String componentId) throws Exception {

        return callEsciDoc("Item.deleteComponent", METHOD_DELETE_COMPONENT, Constants.HTTP_METHOD_DELETE,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_COMPONENT, componentId });
    }

    /**
     * Update a Component of an Item.
     *
     * @param itemId      The id of the item.
     * @param componentId The id of the component.
     * @param component   The new component.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updateComponent(final String itemId, final String componentId, final String component)
        throws Exception {

        return callEsciDoc("Item.updateComponent", METHOD_UPDATE_COMPONENT, Constants.HTTP_METHOD_PUT,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_COMPONENT, componentId }, component);
    }

    /**
     * Retrieve the Content of an Item.
     *
     * @param itemId      The id of the item.
     * @param componentId The id of the component to get the content from.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveContent(final String itemId, final String componentId) throws Exception {

        return callEsciDoc("Item.retrieveContent", METHOD_RETRIEVE_CONTENT, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI,
            new String[] { itemId, Constants.SUB_COMPONENT, componentId, Constants.SUB_CONTENT });
    }

    /**
     * Retrieve transformed Content of an Item.
     *
     * @param itemId               The id of the item.
     * @param componentId          The id of the component to get the content from.
     * @param transformationSerice The service which transforms the content.
     * @param transformParams      The parameter for the transformation service.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveContent(
        final String itemId, final String componentId, final String transformationSerice, final String transformParams)
        throws Exception {

        return callEsciDoc("Item.retrieveContent", METHOD_RETRIEVE_CONTENT, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, transformParams, new String[] { itemId, Constants.SUB_COMPONENT, componentId,
                Constants.SUB_CONTENT, transformationSerice });
    }

    /**
     * Retrieve all Components of an Item.
     *
     * @param itemId The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveComponents(final String itemId) throws Exception {

        return callEsciDoc("Item.retrieveComponents", METHOD_RETRIEVE_COMPONENTS, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_COMPONENTS });
    }

    /**
     * Retrieve all Components of an Item.
     *
     * @param itemId      The id of the item.
     * @param componentId The id of the component.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveComponentProperties(final String itemId, final String componentId) throws Exception {

        return callEsciDoc("Item.retrieveComponentProperties", METHOD_RETRIEVE_COMPONENT_PROPERTIES,
            Constants.HTTP_METHOD_GET, Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_COMPONENT,
                componentId, Constants.SUB_PROPERTIES });
    }

    /**
     * Retrieve a Metadata Record of an Item.
     *
     * @param itemId     The id of the item.
     * @param mdRecordId The id of the metadata record.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveMdRecord(final String itemId, final String mdRecordId) throws Exception {

        return callEsciDoc("Item.retrieveMetadataRecord", METHOD_RETRIEVE_MD_RECORD, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_MD_RECORD, mdRecordId });
    }

    /**
     * Update a Metadata Record of an Item.
     *
     * @param itemId      The id of the item.
     * @param mdRecordId  The id of the metadata record.
     * @param mdRecordXml The xml representation of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updateMdRecord(final String itemId, final String mdRecordId, final Object mdRecordXml)
        throws Exception {

        return callEsciDoc("Item.updateMetadataRecord", METHOD_UPDATE_MD_RECORD, Constants.HTTP_METHOD_PUT,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_MD_RECORD, mdRecordId },
            changeToString(mdRecordXml));
    }

    /**
     * Retrieve the Set of Metadata Records of an Item.
     *
     * @param itemId The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveMdRecords(final String itemId) throws Exception {

        return callEsciDoc("Item.retrieveMetadataRecords", METHOD_RETRIEVE_MD_RECORDS, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_MD_RECORDS });

    }

    /**
     * Retrieve the list of Parents related to an Item.
     *
     * @param id The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveParents(final String id) throws Exception {

        return callEsciDoc("Item.retrieveParents", METHOD_RETRIEVE_PARENTS, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { id, Constants.SUB_RESOURCES + "/" + Constants.SUB_PARENTS });
    }

    /**
     * Retrieve the Set of content streams of an Item.
     *
     * @param itemId The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Deprecated
    public Object retrieveContentStreams(final String itemId) throws Exception {

        return callEsciDoc("Item.retrieveContentStreams", METHOD_RETRIEVE_CONTENT_STREAMS, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_CONTENT_STREAMS });

    }

    /**
     * Retrieve the content of a content streams of an Item.
     *
     * @param itemId            The id of the item.
     * @param contentStreamName The name of the content stream.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Deprecated
    public Object retrieveContentStreamContent(final String itemId, final String contentStreamName) throws Exception {

        return callEsciDoc("Item.retrieveContentStreamContent", METHOD_RETRIEVE_CONTENT_STREAM_CONTENT,
            Constants.HTTP_METHOD_GET, Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_CONTENT_STREAM,
                contentStreamName, "/content" });

    }

    /**
     * Retrieve a content stream of an Item.
     *
     * @param itemId            The id of the item.
     * @param contentStreamName The name of the content stream.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Deprecated
    public Object retrieveContentStream(final String itemId, final String contentStreamName) throws Exception {

        return callEsciDoc("Item.retrieveContentStream", METHOD_RETRIEVE_CONTENT_STREAM, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_CONTENT_STREAM, contentStreamName });

    }

    /**
     * Update the Set of Metadata Records of an Item.
     *
     * @param itemId          The id of the item.
     * @param metaDataRecords The Set of Metadata Records.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updateMdRecords(final String itemId, final String metaDataRecords) throws Exception {

        return callEsciDoc("Item.updateMetaDataRecords", METHOD_UPDATE_MD_RECORDS, Constants.HTTP_METHOD_PUT,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_MD_RECORDS }, metaDataRecords);

    }

    /**
     * Retrieve the Properties of an Item.
     *
     * @param id The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveProperties(final String id) throws Exception {

        return callEsciDoc("Item.retrieveProperties", METHOD_RETRIEVE_PROPERTIES, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { id, Constants.SUB_PROPERTIES });
    }

    /**
     * Retrieve the history of an Item.
     *
     * @param itemId The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveVersionHistory(final String itemId) throws Exception {

        return callEsciDoc("Item.retrieveVersionHistory", METHOD_RETRIEVE_VERSION_HISTORY, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_VERSION_HISTORY });
    }

    /**
     * Retrieve the list of available Resources of an Item.
     *
     * @param id The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveResources(final String id) throws Exception {

        return callEsciDoc("Item.retrieveResources", METHOD_RETRIEVE_RESOURCES, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { id, Constants.SUB_RESOURCES });
    }

    /**
     * Retrieve the list of content relations of an Item.
     *
     * @param id The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveRelations(final String id) throws Exception {

        return callEsciDoc("Item.retrieveRelations", METHOD_RETRIEVE_RELATIONS, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { id, Constants.SUB_RELATIONS });
    }

    /**
     * Retrieve the METS representation of an Item.
     *
     * @param itemId The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveMets(final String itemId) throws Exception {

        return callEsciDoc("Item.retrieveMets", METHOD_RETRIEVE_METS, Constants.HTTP_METHOD_GET,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_METS });
    }

    /**
     * Assigns a persistent identifier (pid) to a version of an item.
     *
     * @param itemId The id of the item.
     * @param param  The parameter structur with resolver entries as XML snippet.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object assignVersionPid(final String itemId, final String param) throws Exception {

        return callEsciDoc("Item.assignVersionPid", METHOD_ASSIGN_VERSION_PID, Constants.HTTP_METHOD_POST,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_ASSIGN_VERSION_PID }, param);
    }

    /**
     * Assigns a persistent identifier (pid) to the item. The pid represents the whole item identifier.
     *
     * @param itemId The id of the item.
     * @param param  The parameter structur with resolver entries as XML snippet.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object assignObjectPid(final String itemId, final String param) throws Exception {

        return callEsciDoc("Item.assignObjectPid", METHOD_ASSIGN_OBJECT_PID, Constants.HTTP_METHOD_POST,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_ASSIGN_OBJECT_PID }, param);
    }

    /**
     * Assigns a persistent identifier (pid) to content.
     *
     * @param itemId      The id of the item.
     * @param componentId The id of the component.
     * @param param       The parameter structure with resolver entries as XML snippet.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object assignContentPid(final String itemId, final String componentId, final String param) throws Exception {

        return callEsciDoc("Item.assignContentPid", METHOD_ASSIGN_CONTENT_PID, Constants.HTTP_METHOD_POST,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_COMPONENT, componentId,
                Constants.SUB_ASSIGN_CONTENT_PID }, param);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.test.common.client.servlet.om.interfaces.SubmitReleaseReviseWithdrawClientInterface
     *      #release(java.lang.String, java.lang.String)
     */
    public Object release(final String itemId, final String param) throws Exception {

        return (releaseWithoutPid(itemId, param));
    }

    /**
     * Release an Item with the therefore needed object or version PIDs.
     *
     * @param creatorUserHandle The user handle of the creator. This value is ignored if null.
     */
    public Object releaseWithPid(final String itemId, String creatorUserHandle) throws Exception {

        // Following code is necessary to reach the status 'released' if via
        // configuration a released item/container has to have a object/version
        // PID.
        String xpathObjectPid = "/item/properties/pid";
        String xpathVersionPid = "/item/properties/version/pid";
        String url = "http://somewhere/dir/";

        String lmd = null;
        String pidXml = null;

        Document resDoc = null;

        if (creatorUserHandle == null) {
            creatorUserHandle = PWCallback.getHandle();
        }
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        // only load document if necessary
        if (!getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")
            || !getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String objXml = handleXmlResult(retrieve(itemId));
            resDoc = EscidocAbstractTest.getDocument(objXml);
            lmd = getLastModificationDateValue(resDoc);
        }

        // assign objectPid
        if (!getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {
            // prevent re-assigning
            Node pid = selectSingleNode(resDoc, xpathObjectPid);
            if (pid == null) {
                String id = getObjidWithoutVersion(itemId);
                String pidParam = getPidParam2(lmd, url + id);
                pidXml = handleXmlResult(assignObjectPid(id, pidParam));

                Document pidDoc = EscidocAbstractTest.getDocument(pidXml);
                lmd = getLastModificationDateValue(pidDoc);
            }
        }

        // assign versionPid
        if (!getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {

            // prevent re-assigning
            Node pid = selectSingleNode(resDoc, xpathVersionPid);
            if (pid == null) {
                String versionNumber = getVersionNumber(itemId);
                String versionId = itemId;
                if (versionNumber == null) {
                    versionId = getLatestVersionObjidValue(resDoc);
                }
                String pidParam = getPidParam2(lmd, url + versionId);
                pidXml = handleResult(assignVersionPid(versionId, pidParam));

                Document pidDoc = EscidocAbstractTest.getDocument(pidXml);
                lmd = getLastModificationDateValue(pidDoc);
            }
        }

        String param = getTaskParam(lmd);

        PWCallback.setHandle(creatorUserHandle);
        // now the actually method: release
        return (releaseWithoutPid(itemId, param));
    }

    /**
     * Release an Item without indirect PID assignment to fulfill the configured PID values for the release.
     */
    public Object releaseWithoutPid(final String itemId, final String param) throws Exception {

        return callEsciDoc("Item.release", METHOD_RELEASE, Constants.HTTP_METHOD_POST, Constants.ITEM_BASE_URI,
            new String[] { itemId, Constants.SUB_RELEASE }, param);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.test.common.client.servlet.om.interfaces.SubmitReleaseReviseWithdrawClientInterface
     *      #submit(java.lang.String, java.lang.String)
     */
    public Object revise(final String itemId, final String param) throws Exception {

        return callEsciDoc("Item.revise", METHOD_REVISE, Constants.HTTP_METHOD_POST, Constants.ITEM_BASE_URI,
            new String[] { itemId, Constants.SUB_REVISE }, param);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.test.common.client.servlet.om.interfaces.SubmitReleaseReviseWithdrawClientInterface
     *      #submit(java.lang.String, java.lang.String)
     */
    public Object submit(final String itemId, final String param) throws Exception {

        return callEsciDoc("Item.submit", METHOD_SUBMIT, Constants.HTTP_METHOD_POST, Constants.ITEM_BASE_URI,
            new String[] { itemId, Constants.SUB_SUBMIT }, param);
    }

    /**
     * See Interface for functional description.
     *
     * @see de.escidoc.core.test.common.client.servlet.om.interfaces.SubmitReleaseReviseWithdrawClientInterface
     *      #withdraw(java.lang.String, java.lang.String)
     */
    public Object withdraw(final String itemId, final String param) throws Exception {

        return callEsciDoc("Item.withdraw", METHOD_WITHDRAW, Constants.HTTP_METHOD_POST, Constants.ITEM_BASE_URI,
            new String[] { itemId, Constants.SUB_WITHDRAW }, param);
    }

    /**
     * Lock an Item for offlien use.
     *
     * @param itemId The id of the item.
     * @param param  The timestamp of the last modification of the Item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object lock(final String itemId, final String param) throws Exception {

        return callEsciDoc("Item.lock", METHOD_LOCK, Constants.HTTP_METHOD_POST, Constants.ITEM_BASE_URI, new String[] {
            itemId, Constants.SUB_LOCK }, param);
    }

    /**
     * Unlock an Item.
     *
     * @param itemId The id of the item.
     * @param param  The timestamp of the last modification of the Item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object unlock(final String itemId, final String param) throws Exception {

        return callEsciDoc("Item.unlock", METHOD_UNLOCK, Constants.HTTP_METHOD_POST, Constants.ITEM_BASE_URI,
            new String[] { itemId, Constants.SUB_UNLOCK }, param);
    }

    /**
     * Move an Item to a new Context.
     *
     * @param itemId The id of the item.
     * @param param  The timestamp of the last modification of the Item and the new Context.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object moveToContext(final String itemId, final String param) throws Exception {

        return callEsciDoc("Item.moveToContext", METHOD_MOVE_TO_CONTEXT, Constants.HTTP_METHOD_POST,
            Constants.ITEM_BASE_URI, new String[] { itemId, Constants.SUB_MOVE_TO_CONTEXT }, param);
    }

}
