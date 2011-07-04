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

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.Container;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;

import java.io.UnsupportedEncodingException;

/**
 * The class contains methods, which invoke the rendering methods for container its subresources and lists and methods
 * which retrieve constituent parts to render from.
 *
 * @author Rozita Friedman
 */
public class ContainerHandlerRetrieve extends ContainerHandlerBase {

    /**
     * Retrieves the container xml representation.
     *
     * @param container instance of Container
     * @return String with container xml
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    protected String getContainerXml(final Container container) throws SystemException {

        return this.getContainerRenderer().render(container);
    }

    /**
     * Retrieves a content-model-specific datastream.
     *
     * @return content-model-specific datastream
     * @throws FedoraSystemException   If Fedora reports an error.
     * @throws StreamNotFoundException If content-model-specific datastream does not exist.
     * @throws EncodingSystemException if "UTF-8" encoding is not supported
     */
    public String getCts() throws FedoraSystemException, StreamNotFoundException, EncodingSystemException {

        final Container container = getContainer();
        final Datastream cts = container.getCts();
        final String xml;
        try {
            xml = new String(cts.getStream(), XmlUtility.CHARACTER_ENCODING);
        }
        catch (final UnsupportedEncodingException e) {
            throw new EncodingSystemException(e);
        }
        return xml.trim();
    }

    /**
     * Retrieves a xml representation of container subresource "relations".
     *
     * @param container instance of Container
     * @return String with relations xml
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    protected String getRelationsXml(final Container container) throws SystemException {

        return this.getContainerRenderer().renderRelations(container);
    }

    /**
     * Retrieves a xml representation of container subresource "md-record".
     *
     * @param mdRecordId The name of the mdrecord element.
     * @return String with md-record xml
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    protected String getMetadataRecordXml(final String mdRecordId) throws EncodingSystemException,
        FedoraSystemException, WebserverSystemException, MdRecordNotFoundException {

        try {
            final Datastream mdRecord = getContainer().getMdRecord(mdRecordId);
            final String metadataRecord =
                this.getContainerRenderer().renderMetadataRecord(getContainer(), mdRecord, true);
            if (metadataRecord.length() == 0) {
                throw new MdRecordNotFoundException("Md-record with a name  " + mdRecordId
                    + " does not exist in the Container with Id " + getContainer().getId());
            }
            return metadataRecord;

        }
        catch (final StreamNotFoundException e) {
            throw new MdRecordNotFoundException("Md-record with a name  " + mdRecordId
                + " does not exist in the Container with Id " + getContainer().getId(), e);
        }

    }

    /**
     * Retrieves a xml representation of container subresource "md-records".
     *
     * @return String with md-records xml
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    protected String getMetadataRecordsXml() throws EncodingSystemException, FedoraSystemException,
        WebserverSystemException, IntegritySystemException {
        final Container container = getContainer();
        return this.getContainerRenderer().renderMetadataRecords(container);

    }

    public String retrieveMdRecord(final String name) throws FedoraSystemException, WebserverSystemException {
        try {
            return getContainer().getMdRecord(name).toString();
        }
        catch (final StreamNotFoundException e) {
            throw new WebserverSystemException(e);
        }
    }

    public String retrieveDc() throws FedoraSystemException, WebserverSystemException {
        try {
            return getContainer().getDc().toString();
        }
        catch (final StreamNotFoundException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Retrieves a xml representation of container subresource "properties".
     *
     * @param container instance of Container
     * @return String with properties xml
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    protected String getPropertiesXml(final Container container) throws SystemException {

        return this.getContainerRenderer().renderProperties(container);
    }

    /**
     * Retrieves a xml representation of container subresource "resources".
     *
     * @return String with resources xml
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    protected String getResourcesXml() throws WebserverSystemException {

        return this.getContainerRenderer().renderResources(getContainer());
    }

    /**
     * Retrieves a xml representation of container subresource "struct-map".
     *
     * @param container instance of Container
     * @return String with struct-map xml
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    protected String getStructMapXml(final Container container) throws SystemException {

        return this.getContainerRenderer().renderStructMap(container);
    }
}
