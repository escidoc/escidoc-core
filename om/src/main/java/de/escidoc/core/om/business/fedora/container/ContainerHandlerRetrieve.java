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
package de.escidoc.core.om.business.fedora.container;

import java.io.UnsupportedEncodingException;
import java.util.List;

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.Container;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.business.fedora.item.FedoraItemHandler;
import de.escidoc.core.om.business.security.UserFilter;

/**
 * The class contains methods, which invoke the rendering methods for container
 * its subresources and lists and methods which retrieve constituent parts to
 * render from.
 * 
 * @author ROF
 * 
 */
public class ContainerHandlerRetrieve extends ContainerHandlerBase {
    /**
     * Retrieves the container xml representation.
     * 
     * @param container
     *            instance of Container
     * @return String with container xml
     * @throws SystemException
     */
    protected String getContainerXml(final Container container)
        throws SystemException {

        return getRenderer().render(container);
    }

    /**
     * Retrieves a content-model-specific datastream.
     * 
     * @return content-model-specific datastream
     * @throws FedoraSystemException
     *             If Fedora reports an error.
     * @throws StreamNotFoundException
     *             If content-model-specific datastream does not exist.
     * @throws EncodingSystemException
     *             if "UTF-8" encoding is not supported
     * @throws WebserverSystemException
     */
    public String getCts() throws FedoraSystemException,
        StreamNotFoundException, EncodingSystemException,
        WebserverSystemException {

        Container container = getContainer();
        Datastream cts = container.getCts();
        String xml;
        try {
            xml = new String(cts.getStream(), XmlUtility.CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException(e);
        }
        return xml.trim();
    }

    /**
     * Retrieves a xml representation of container subresource "relations".
     * 
     * @param container
     *            instance of Container
     * @return String with relations xml
     * @throws WebserverSystemException
     * @throws SystemException
     */
    protected String getRelationsXml(final Container container)
        throws WebserverSystemException, SystemException {

        return getRenderer().renderRelations(container);
    }

    /**
     * Retrieves a xml representation of container subresource "md-record".
     * 
     * @param mdRecordId
     *            The name of the mdrecord element.
     * @return String with md-record xml
     * @throws EncodingSystemException
     * @throws FedoraSystemException
     * @throws IntegritySystemException
     * @throws WebserverSystemException
     */
    protected String getMetadataRecordXml(final String mdRecordId)
        throws EncodingSystemException, FedoraSystemException,
        WebserverSystemException, MdRecordNotFoundException {

        Datastream mdRecord = null;
        try {
            mdRecord = getContainer().getMdRecord(mdRecordId);
            String metadataRecord =
                getRenderer().renderMetadataRecord(getContainer(), mdRecord,
                    true);
            if (metadataRecord.length() == 0) {
                throw new MdRecordNotFoundException("Md-record with a name  "
                    + mdRecordId + " does not exist in the Container with Id "
                    + getContainer().getId());
            }
            return metadataRecord;

        }
        catch (StreamNotFoundException e) {
            throw new MdRecordNotFoundException("Md-record with a name  "
                + mdRecordId + " does not exist in the Container with Id "
                + getContainer().getId());
        }

    }

    /**
     * Retrieves a xml representation of container subresource "md-records".
     * 
     * @return String with md-records xml
     * @throws EncodingSystemException
     * @throws FedoraSystemException
     * @throws WebserverSystemException
     */
    protected String getMetadataRecordsXml() throws EncodingSystemException,
        FedoraSystemException, WebserverSystemException,
        IntegritySystemException {
        Container container = getContainer();
        return getRenderer().renderMetadataRecords(container);

    }

    public String retrieveMdRecord(final String name)
        throws FedoraSystemException, WebserverSystemException {
        try {
            return getContainer().getMdRecord(name).toString();
        }
        catch (StreamNotFoundException e) {
            throw new WebserverSystemException(e);
        }
    }

    public String retrieveDc(final String name) throws FedoraSystemException,
        WebserverSystemException {
        try {
            return getContainer().getDc().toString();
        }
        catch (StreamNotFoundException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Retrieves a xml representation of container subresource "properties".
     * 
     * @param container
     *            instance of Container
     * @return String with properties xml
     * @throws WebserverSystemException
     * @throws SystemException
     */
    protected String getPropertiesXml(final Container container)
        throws WebserverSystemException, SystemException {

        return getRenderer().renderProperties(container);
    }

    /**
     * Retrieves a xml representation of container subresource "resources".
     * 
     * @return String with resources xml
     * @throws WebserverSystemException
     */
    protected String getResourcesXml() throws WebserverSystemException {

        return getRenderer().renderResources(getContainer());
    }

    /**
     * Retrieves a xml representation of container subresource "struct-map".
     * 
     * @param container
     *            instance of Container
     * @return String with struct-map xml
     * @throws WebserverSystemException
     * @throws SystemException
     */
    protected String getStructMapXml(final Container container)
        throws WebserverSystemException, SystemException {

        return getRenderer().renderStructMap(container);
    }

    /**
     * Retrieves a xml representation of the list with references to container
     * members, which satisfy the filter criteria.
     * 
     * @param container
     *            instance of Container
     * @param filter
     *            xml with filter criteria
     * @return String xml with a list of member references
     * @throws WebserverSystemException
     * @throws SystemException
     * @throws MissingMethodParameterException
     */
    protected String getMemberRefsXml(
        final Container container, final String filter)
        throws WebserverSystemException, SystemException,
        MissingMethodParameterException {

        return getRenderer().renderMemberRefs(container, filter);
    }

    /**
     * Retrieves a xml representation of the list with references to existing
     * containers, which satisfy the filter criteria.
     * 
     * @param filter
     *            xml with filter criteria
     * @return String xml with a list of containers references
     * @throws WebserverSystemException
     * @throws SystemException
     * @throws MissingMethodParameterException
     */
    protected String getContainerRefsXml(final String filter)
        throws WebserverSystemException, SystemException,
        MissingMethodParameterException {

        return getRenderer().renderContainerRefs(filter);
    }

    /**
     * Retrieves a xml representation of the list with container members, which
     * satisfy the filter criteria.
     * 
     * @param containerHandler
     *            instance of FedoraContainerHandler
     * @param itemHandler
     *            instance of FedoraItemHandler
     * @param filter
     *            xml with filter criteria
     * @return String xml with a list of members
     * @throws WebserverSystemException
     * @throws SystemException
     * @throws MissingMethodParameterException
     */
    protected String getMembersXml(
        final FedoraContainerHandler containerHandler,
        final FedoraItemHandler itemHandler, final String filter)
        throws WebserverSystemException, SystemException,
        MissingMethodParameterException, AuthorizationException {

        return getRenderer().renderMembers(containerHandler, itemHandler,
            filter);
    }

    /**
     * Retrieves a xml representation of the list with existing containers,
     * which satisfy the filter criteria.
     * 
     * @param filter
     *            xml with filter criteria
     * @param containerHandler
     *            instance of FedoraContainerHandler
     * @return String xml with a list of containers
     * @throws WebserverSystemException
     * @throws SystemException
     * @throws MissingMethodParameterException
     */
    protected String getContainersXml(
        final String filter, final FedoraContainerHandler containerHandler)
        throws WebserverSystemException, SystemException,
        MissingMethodParameterException {

        return getRenderer().renderContainers(filter, containerHandler);
    }

    /**
     * Retrieves List container members ids, which satisfy the filter criteria.
     * 
     * @param filter
     *            xml with filter criteria
     * @return List container members ids, which satisfy the filter criteria
     * @throws SystemException
     *             Thrown in case of internal error.
     * @throws MissingMethodParameterException
     *             Thrown if filter parameter is missing.
     */
    public List<String> getMemberRefsList(final String filter)
        throws SystemException, MissingMethodParameterException {

        UserFilter ufilter = new UserFilter();

        List<String> memberIds =
            ufilter.getMemberRefList(getContainer(), filter);

        return memberIds;
    }

}
