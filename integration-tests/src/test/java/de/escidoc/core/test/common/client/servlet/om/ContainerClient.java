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
 * Offers access methods to the escidoc interfaces of the container resource.
 *
 * @author Michael Schneider
 */
public class ContainerClient extends ClientBase
    implements SubmitReleaseReviseWithdrawClientInterface, ResourceHandlerClientInterface {

    /**
     * Create a container in the escidoc framework.
     *
     * @param containerXml The xml representation of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object create(final Object containerXml) throws Exception {

        return callEsciDoc("Container.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT, Constants.CONTAINER_BASE_URI,
            new String[] {}, changeToString(containerXml));
    }

    /**
     * Retrieve the xml representation of a container.
     *
     * @param id The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieve(final String id) throws Exception {

        return callEsciDoc("Container.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id });
    }

    /**
     * Delete a container from the escidoc framework.
     *
     * @param id The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object delete(final String id) throws Exception {

        return callEsciDoc("Container.delete", METHOD_DELETE, Constants.HTTP_METHOD_DELETE,
            Constants.CONTAINER_BASE_URI, new String[] { id });
    }

    /**
     * Update a container in the escidoc framework.
     *
     * @param id           The id of the container.
     * @param containerXml The xml representation of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object update(final String id, final Object containerXml) throws Exception {

        return callEsciDoc("Container.update", METHOD_UPDATE, Constants.HTTP_METHOD_PUT, Constants.CONTAINER_BASE_URI,
            new String[] { id }, changeToString(containerXml));
    }

    /**
     * Retrieve the Containers of a Container.
     *
     * @param filter The filter parameter.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveContainers(final Map<String, String[]> filter) throws Exception {

        return callEsciDoc("Container.retrieveContainers", METHOD_RETRIEVE_CONTAINERS, Constants.HTTP_METHOD_GET,
            Constants.CONTAINERS_BASE_URI, new String[] {}, filter);
    }

    /**
     * Retrieve the Items of a Container.
     *
     * @param id The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveItems(final String id) throws Exception {

        return callEsciDoc("Container.retrieveItems", METHOD_RETRIEVE_ITEMS, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_ITEMS });
    }

    /**
     * Retrieve the list of Containers and Items related to a Container.
     *
     * @param id     The id of the container.
     * @param filter filter as CQL query
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveMembers(final String id, final Map<String, String[]> filter) throws Exception {

        return callEsciDoc("Container.retrieveMembers", METHOD_RETRIEVE_MEMBERS, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id,
                Constants.SUB_RESOURCES + "/" + Constants.SUB_CONTAINER_MEMBERS }, filter);
    }

    /**
     * Retrieve the list of Parents related to a Container.
     *
     * @param id The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveParents(final String id) throws Exception {

        return callEsciDoc("Container.retrieveParents", METHOD_RETRIEVE_PARENTS, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_RESOURCES + "/" + Constants.SUB_PARENTS });
    }

    /**
     * Retrieve the list of Items of content model toc related to a Container.
     *
     * @param id     The id of the container.
     * @param filter filter as CQL query
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveTocs(final String id, final Map<String, String[]> filter) throws Exception {

        return callEsciDoc("Container.retrieveTocs", METHOD_RETRIEVE_TOCS, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_CONTAINER_TOCS }, filter);
    }

    public Object retrieveStructMap(final String id) throws Exception {

        return callEsciDoc("Container.retrieveStructMap", METHOD_RETRIEVE_STRUCT_MAP, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_STRUCT_MAP });
    }

    public Object retrieveToc(final String id) throws Exception {

        return callEsciDoc("Container.retrieveToc", METHOD_RETRIEVE_TOC, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_TOC });
    }

    public Object retrieveTocView(final String id) throws Exception {

        return callEsciDoc("Container.retrieveTocView", METHOD_RETRIEVE_TOC_VIEW, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_TOC_VIEW });
    }

    /**
     * Retrieve a Metadata Record of a Container.
     *
     * @param itemId     The id of the container.
     * @param mdRecordId The id of the metadata record.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveMdRecord(final String itemId, final String mdRecordId) throws Exception {

        return callEsciDoc("Container.retrieveMetadataRecord", METHOD_RETRIEVE_MD_RECORD, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { itemId, Constants.SUB_MD_RECORD, mdRecordId });
    }

    /**
     * Update a Metadata Record of a Container.
     *
     * @param itemId      The id of the container.
     * @param mdRecordId  The id of the metadata record.
     * @param mdRecordXml The xml representation of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updateMetaDataRecord(final String itemId, final String mdRecordId, final Object mdRecordXml)
        throws Exception {

        return callEsciDoc("Container.updateMetadataRecord", METHOD_UPDATE_MD_RECORD, Constants.HTTP_METHOD_PUT,
            Constants.CONTAINER_BASE_URI, new String[] { itemId, Constants.SUB_MD_RECORD, mdRecordId },
            changeToString(mdRecordXml));
    }

    /**
     * Retrieve the Set of Metadata Records of a Container.
     *
     * @param id The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveMetaDataRecords(final String id) throws Exception {

        return callEsciDoc("Container.retrieveMetadataRecords", METHOD_RETRIEVE_MD_RECORDS, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_MD_RECORDS });

    }

    /**
     * Update the Set of Metadata Records of a Container.
     *
     * @param id              The id of the container.
     * @param metaDataRecords The Set of Metadata Records.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updateMetaDataRecords(final String id, final String metaDataRecords) throws Exception {

        return callEsciDoc("Container.updateMetaDataRecords", METHOD_UPDATE_MD_RECORDS, Constants.HTTP_METHOD_PUT,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_MD_RECORDS }, metaDataRecords);
    }

    /**
     * Retrieve the Properties of a Container.
     *
     * @param id The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveProperties(final String id) throws Exception {

        return callEsciDoc("Container.retrieveProperties", METHOD_RETRIEVE_PROPERTIES, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_PROPERTIES });
    }

    /**
     * Retrieve the list of virtual Resources available for a Container.
     *
     * @param id The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveResources(final String id) throws Exception {

        return callEsciDoc("Container.retrieveResources", METHOD_RETRIEVE_RESOURCES, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_RESOURCES });
    }

    /**
     * Retrieve the list of virtual Resources available for a Container.
     *
     * @param id The id of the container.
     * @param methodName The name of the method.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveResource(final String id, final String methodName) throws Exception {

        return callEsciDoc("Container.retrieveResource", METHOD_RETRIEVE_RESOURCE, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_RESOURCES, methodName });
    }

    /**
     * Retrieve the list of Relations available for a Container.
     *
     * @param id The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveRelations(final String id) throws Exception {

        return callEsciDoc("Container.retrieveRelations", METHOD_RETRIEVE_RELATIONS, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_RELATIONS });
    }

    /**
     * Create a Toc of a Container.
     *
     * @param id     The id of the container.
     * @param tocXml The xml representation of the toc of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object createToc(final String id, final Object tocXml) throws Exception {

        return callEsciDoc("Container.createToc", METHOD_CREATE_TOC, Constants.HTTP_METHOD_PUT,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_TOC }, changeToString(tocXml));
    }

    /**
     * Delete a Toc from a Container.
     *
     * @param id    The id of the container.
     * @param id The id of the toc.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object deleteToc(final String id) throws Exception {

        return callEsciDoc("Container.deleteToc", METHOD_DELETE_TOC, Constants.HTTP_METHOD_DELETE,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_TOC });
    }

    /**
     * Retrieve a Toc of a Container.
     *
     * @param containerId The id of the container.
     * @param tocId       The id of the toc.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveToc(final String containerId, final String tocId) throws Exception {

        return callEsciDoc("Container.retrieveToc", METHOD_RETRIEVE_TOC, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_TOC, tocId });
    }

    /**
     * Update a Toc of a Container.
     *
     * @param id     The id of the container.
     * @param tocXml The xml representation of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updateToc(final String id, final Object tocXml) throws Exception {

        return callEsciDoc("Container.updateToc", METHOD_UPDATE_TOC, Constants.HTTP_METHOD_PUT,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_TOC }, changeToString(tocXml));
    }

    /**
     * Retrieve the Tocs of a Container.
     *
     * @param containerId The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveTocs(final String containerId) throws Exception {

        return callEsciDoc("Container.retrieveTocs", METHOD_RETRIEVE_TOCS, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_TOC });
    }

    /**
     * Update the Tocs of a Container.
     *
     * @param id      The id of the container.
     * @param tocsXml The xml representation of the tocs of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updateTocs(final String id, final Object tocsXml) throws Exception {

        return callEsciDoc("Container.updateTocs", METHOD_UPDATE_TOCS, Constants.HTTP_METHOD_PUT,
            Constants.CONTAINER_BASE_URI, new String[] { id, Constants.SUB_TOC }, changeToString(tocsXml));
    }

    /**
     * Retrieve the history of a Container.
     *
     * @param containerId The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveVersionHistory(final String containerId) throws Exception {

        return callEsciDoc("Container.retrieveVersionHistory", METHOD_RETRIEVE_VERSION_HISTORY,
            Constants.HTTP_METHOD_GET, Constants.CONTAINER_BASE_URI, new String[] { containerId,
                Constants.SUB_VERSION_HISTORY });
    }

    /**
     * Retrieve the METS representation of a Container.
     *
     * @param containerId The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveMets(final String containerId) throws Exception {

        return callEsciDoc("Container.retrieveMets", METHOD_RETRIEVE_METS, Constants.HTTP_METHOD_GET,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_METS });
    }

    public Object addTocs(final String containerId, final String taskParam) throws Exception {

        return callEsciDoc("Container.addTocs", METHOD_ADD_TOCS, Constants.HTTP_METHOD_POST,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_ADD_TOCS }, taskParam);
    }

    public Object addMembers(final String containerId, final String taskParam) throws Exception {

        return callEsciDoc("Container.addMembers", METHOD_ADD_MEMBERS, Constants.HTTP_METHOD_POST,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_ADD_MEMBERS }, taskParam);
    }

    public Object removeMembers(final String containerId, final String taskParam) throws Exception {

        return callEsciDoc("Container.removeMembers", METHOD_REMOVE_MEMBERS, Constants.HTTP_METHOD_POST,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_REMOVE_MEMBERS }, taskParam);
    }

    public Object createItem(final String containerId, final String itemXml) throws Exception {

        return callEsciDoc("Container.createItem", METHOD_CREATE_ITEM, Constants.HTTP_METHOD_POST,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_CREATE_ITEM }, itemXml);
    }

    public Object createContainer(final String containerId, final String containerXml) throws Exception {

        return callEsciDoc("Container.createContainer", METHOD_CREATE_CONTAINER, Constants.HTTP_METHOD_POST,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_CREATE_CONTAINER }, containerXml);
    }

    /**
     * Release a Container.
     *
     * @param containerId The id of the Container.
     * @param param       The timestamp of the last modification of the Container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object release(final String containerId, final String param) throws Exception {

        return (releaseWithoutPid(containerId, param));
    }

    /**
     * Release a Container with indirect PID assignment to fulfill the required PIDs for the release.
     *
     * @param containerId       The id of the Container.
     * @param creatorUserHandle The user handle of the creator. The PID assignment will be done with higher rights and
     *                          set afterward back to the creatorUserHandle.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object releaseWithPid(final String containerId, final String creatorUserHandle) throws Exception {

        // Following code is necessary to reach the status 'released' if via
        // configuration a released item/container has to have a object/version
        // PID.
        String xpathObjectPid = "/container/properties/pid";
        String xpathVersionPid = "/container/properties/version/pid";
        String url = "http://somewhere/dir/";

        Document resDoc = null;

        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);

        // only load document if necessary
        if (!getPidConfig("cmm.Container.objectPid.releaseWithoutPid", "false")
            || !getPidConfig("cmm.Container.versionPid.releaseWithoutPid", "false")) {
            String objXml = handleXmlResult(retrieve(containerId));
            resDoc = EscidocAbstractTest.getDocument(objXml);
        }

        // assign objectPid
        if (!getPidConfig("cmm.Container.objectPid.releaseWithoutPid", "false")) {
            // prevent re-assigning
            Node pid = selectSingleNode(resDoc, xpathObjectPid);
            if (pid == null) {
                String id = getObjidWithoutVersion(containerId);
                String pidParam = getPidParam(id, url + id);
                assignObjectPid(id, pidParam);
            }
        }

        // assign versionPid
        if (!getPidConfig("cmm.Container.versionPid.releaseWithoutPid", "false")) {

            // prevent re-assigning
            Node pid = selectSingleNode(resDoc, xpathVersionPid);
            if (pid == null) {
                String versionNumber = getVersionNumber(containerId);
                String versionId = containerId;
                if (versionNumber == null) {
                    versionId = getLatestVersionObjidValue(resDoc);
                }
                String pidParam = getPidParam(versionId, url + versionId);
                assignVersionPid(versionId, pidParam);
            }
        }

        String containerXml = handleResult(retrieve(containerId));
        Document document = EscidocAbstractTest.getDocument(containerXml);
        String param = getTaskParam(getRootElementAttributeValue(document, "last-modification-date"));

        PWCallback.setHandle(creatorUserHandle);
        // now the actually method: release
        return (releaseWithoutPid(containerId, param));
    }

    /**
     * Release a Container without indirect PID assignment to fulfill the configured PID values for the release.
     *
     * @param containerId The id of the Container.
     * @param param       The timestamp of the last modification of the Container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object releaseWithoutPid(final String containerId, final String param) throws Exception {

        return callEsciDoc("Container.release", METHOD_RELEASE, Constants.HTTP_METHOD_POST,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_RELEASE }, param);
    }

    /**
     * Revise a Container.
     *
     * @param containerId The id of the Container.
     * @param param       The timestamp of the last modification of the Container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object revise(final String containerId, final String param) throws Exception {

        return callEsciDoc("Container.revise", METHOD_REVISE, Constants.HTTP_METHOD_POST, Constants.CONTAINER_BASE_URI,
            new String[] { containerId, Constants.SUB_REVISE }, param);
    }

    /**
     * Submit a Container.
     *
     * @param containerId The id of the Container.
     * @param param       The timestamp of the last modification of the Container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object submit(final String containerId, final String param) throws Exception {

        return callEsciDoc("Container.submit", METHOD_SUBMIT, Constants.HTTP_METHOD_POST, Constants.CONTAINER_BASE_URI,
            new String[] { containerId, Constants.SUB_SUBMIT }, param);
    }

    /**
     * Withdraw a Container.
     *
     * @param containerId The id of the Container.
     * @param param       The timestamp of the last modification of the Container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object withdraw(final String containerId, final String param) throws Exception {

        return callEsciDoc("Container.withdraw", METHOD_WITHDRAW, Constants.HTTP_METHOD_POST,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_WITHDRAW }, param);
    }

    /**
     * Lock an Item for offlien use.
     *
     * @param containerId The id of the Container.
     * @param param       The timestamp of the last modification of the Container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object lock(final String containerId, final String param) throws Exception {

        return callEsciDoc("Container.lock", METHOD_LOCK, Constants.HTTP_METHOD_POST, Constants.CONTAINER_BASE_URI,
            new String[] { containerId, Constants.SUB_LOCK }, param);
    }

    /**
     * Unlock a Container.
     *
     * @param containerId The id of the Container.
     * @param param       The timestamp of the last modification of the Container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object unlock(final String containerId, final String param) throws Exception {

        return callEsciDoc("Container.unlock", METHOD_UNLOCK, Constants.HTTP_METHOD_POST, Constants.CONTAINER_BASE_URI,
            new String[] { containerId, Constants.SUB_UNLOCK }, param);
    }

    /**
     *
     * @param containerId
     * @param param
     * @return
     * @throws Exception
     */
    public Object addContentRelations(final String containerId, final String param) throws Exception {

        return callEsciDoc("Container.addContentRelations", METHOD_ADD_CONTENT_RELATIONS, Constants.HTTP_METHOD_POST,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_ADD_CONTENT_RELATIONS }, param);
    }

    /**
     *
     * @param containerId
     * @param param
     * @return
     * @throws Exception
     */
    public Object removeContentRelations(final String containerId, final String param) throws Exception {

        return callEsciDoc("Container.removeContentRelations", METHOD_REMOVE_CONTENT_RELATIONS,
            Constants.HTTP_METHOD_POST, Constants.CONTAINER_BASE_URI, new String[] { containerId,
                Constants.SUB_REMOVE_CONTENT_RELATIONS }, param);
    }

    public Object assignObjectPid(final String containerId, final String param) throws Exception {

        return callEsciDoc("Container.assignObjectPid", METHOD_ASSIGN_OBJECT_PID, Constants.HTTP_METHOD_POST,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_ASSIGN_OBJECT_PID }, param);
    }

    public Object assignVersionPid(final String containerId, final String param) throws Exception {

        return callEsciDoc("Container.assignVersionPid", METHOD_ASSIGN_VERSION_PID, Constants.HTTP_METHOD_POST,
            Constants.CONTAINER_BASE_URI, new String[] { containerId, Constants.SUB_ASSIGN_VERSION_PID }, param);
    }

}
