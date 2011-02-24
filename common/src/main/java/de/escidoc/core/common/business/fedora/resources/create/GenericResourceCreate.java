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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.fedora.resources.create;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generic Resource for Create.
 * 
 * @author SWA
 * 
 */
public abstract class GenericResourceCreate {

    private static final AppLogger LOG =
        new AppLogger(GenericResourceCreate.class.getName());

    private String objid = null;

    private String buildNumber = null;

    /**
     * Generate FoXML for all MetadataRecords.
     * 
     * @param mdRecords
     *            Vector with MdRecordCreate.
     * @return MetadataReocrd FoXML.
     * @throws SystemException
     *             Thrown if converting of characters to default character set
     *             failed.
     */
    protected final List<Map<String, String>> getMetadataRecordsMap(
            final Iterable<MdRecordCreate> mdRecords) throws SystemException {

        final List<Map<String, String>> values =
            new ArrayList<Map<String, String>>();

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
     * @param mdRecord
     *            Metadata record which is to map to DC.
     * @param contentModelId
     *            ID of the content model to look for transformation
     *            instruction.
     * @return DC or null if default metadata is missing).
     * @throws WebserverSystemException
     *             Thrown if an error occurs during DC creation.
     * @throws EncodingSystemException
     *             Thrown if the conversion to default encoding failed.
     */
    public final String getDC(
            final MdRecordCreate mdRecord, final String contentModelId)
        throws WebserverSystemException, EncodingSystemException {
        return XmlUtility.createDC(mdRecord.getNameSpace(), mdRecord.getContent(), this.objid, contentModelId);
    }

    /**
     * Check status of a Context. An invalidStatusException is thrown if the
     * Context has not the requested status.
     * 
     * @param contextId
     *            The Id of the Context.
     * @param status
     *            The expected status of the Context.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws InvalidStatusException
     *             Thrown if the Context is not in the requested status.
     * @throws TripleStoreSystemException
     *             If the triple store reports an error.
     */
    protected void checkContextStatus(
        final String contextId, final String status)
        throws InvalidStatusException, TripleStoreSystemException,
        WebserverSystemException {

        if (contextId == null || status == null) {
            throw new WebserverSystemException(
                "Context id and status must not be 'null' for check "
                    + "context status.");
        }
        final String curStatus =
            TripleStoreUtility.getInstance().getPropertiesElements(contextId,
                TripleStoreUtility.PROP_PUBLIC_STATUS);
        if (curStatus == null || curStatus.length() == 0) {
            final String msg =
                "Can not get status of context " + contextId + '.';
            LOG.debug(msg);
            throw new WebserverSystemException(msg);
        }
        // In first release, if object is once released no changes are allowed
        if (!curStatus.equals(status)) {
            final String msg =
                "The Context '" + contextId + "' is in state '" + curStatus
                    + "' and not in status " + status + '.';
            LOG.debug(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * @param objid
     *            the objid to set
     */
    public final void setObjid(final String objid) {
        this.objid = objid;
    }

    /**
     * @return the objid
     */
    public final String getObjid() {
        return objid;
    }

    /**
     * @param buildNumber
     *            the buildNumber to set
     */
    public final void setBuildNumber(final String buildNumber) {
        this.buildNumber = buildNumber;
    }

    /**
     * Get the version/build number of framework.
     * 
     * @return the buildNumber
     * @throws WebserverSystemException
     *             Thrown by Utility instance.
     */
    public final String getBuildNumber() throws WebserverSystemException {
        if (this.buildNumber == null) {
            this.buildNumber = Utility.getInstance().getBuildNumber();
        }
        return buildNumber;
    }

}
