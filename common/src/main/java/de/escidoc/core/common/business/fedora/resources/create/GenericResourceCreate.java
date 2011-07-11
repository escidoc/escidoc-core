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

package de.escidoc.core.common.business.fedora.resources.create;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generic Resource for Create.
 *
 * @author Steffen Wagner
 */
@Configurable
public abstract class GenericResourceCreate {

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    private String objid;

    private String buildNumber;

    protected TripleStoreUtility getTripleStoreUtility() {
        return tripleStoreUtility;
    }

    /**
     * Generate FoXML for all MetadataRecords.
     *
     * @param mdRecords Vector with MdRecordCreate.
     * @return MetadataReocrd FoXML.
     */
    protected List<Map<String, String>> getMetadataRecordsMap(final Iterable<MdRecordCreate> mdRecords) {

        final List<Map<String, String>> values = new ArrayList<Map<String, String>>();

        if (mdRecords != null) {
            for (final MdRecordCreate mdRecord : mdRecords) {
                values.add(mdRecord.getValueMap());
            }
        }

        return values;
    }

    /**
     * Get DC.
     *
     * @param mdRecord       Metadata record which is to map to DC.
     * @param contentModelId ID of the content model to look for transformation instruction.
     * @return DC or null if default metadata is missing).
     * @throws WebserverSystemException Thrown if an error occurs during DC creation.
     */
    public String getDC(final MdRecordCreate mdRecord, final String contentModelId) throws WebserverSystemException {
        return XmlUtility.createDC(mdRecord.getNameSpace(), mdRecord.getContent(), this.objid, contentModelId);
    }

    /**
     * Check status of a Context. An invalidStatusException is thrown if the Context has not the requested status.
     *
     * @param contextId The Id of the Context.
     * @param status    The expected status of the Context.
     * @throws WebserverSystemException   Thrown in case of an internal error.
     * @throws InvalidStatusException     Thrown if the Context is not in the requested status.
     * @throws TripleStoreSystemException If the triple store reports an error.
     */
    protected void checkContextStatus(final String contextId, final String status) throws InvalidStatusException,
        TripleStoreSystemException, WebserverSystemException {

        if (contextId == null || status == null) {
            throw new WebserverSystemException("Context id and status must not be 'null' for check "
                + "context status.");
        }
        final String curStatus =
            this.tripleStoreUtility.getPropertiesElements(contextId, TripleStoreUtility.PROP_PUBLIC_STATUS);
        if (curStatus == null || curStatus.length() == 0) {
            throw new WebserverSystemException("Can not get status of context " + contextId + '.');
        }
        // In first release, if object is once released no changes are allowed
        if (!curStatus.equals(status)) {
            throw new InvalidStatusException("The Context '" + contextId + "' is in state '" + curStatus
                + "' and not in status " + status + '.');
        }
    }

    /**
     * @param objid the objid to set
     */
    public void setObjid(final String objid) {
        this.objid = objid;
    }

    /**
     * @return the objid
     */
    public String getObjid() {
        return this.objid;
    }

    /**
     * @param buildNumber the buildNumber to set
     */
    public void setBuildNumber(final String buildNumber) {
        this.buildNumber = buildNumber;
    }

    /**
     * Get the version/build number of framework.
     *
     * @return the buildNumber
     * @throws WebserverSystemException Thrown by Utility instance.
     */
    public String getBuildNumber() throws WebserverSystemException {
        if (this.buildNumber == null) {
            this.buildNumber = Utility.getBuildNumber();
        }
        return this.buildNumber;
    }

}
