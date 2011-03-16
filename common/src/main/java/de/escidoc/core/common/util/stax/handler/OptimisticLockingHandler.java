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

package de.escidoc.core.common.util.stax.handler;

import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;

/**
 * Verifies the attribute <code>last-modification-date</code> of a xml request.
 * The specified value have to be the <code>LastModificationDate</code> of the
 * handled Fedora Object. If a root-element is found the value of
 * <code>last-modification-date</code> is compared with the value provided
 * during creation of the handler. If the values are not equal a
 * <code>LockingException</code> is thrown.
 * 
 * @author FRS
 * 
 */
public class OptimisticLockingHandler extends DefaultHandler {

    private String objid;

    private String objectType;

    private boolean done;

    private String requestedModificationDate;

    private String lastModifiedDate;

    private static final String MODIFIED_DATE_ATT_NAME =
        "last-modification-date";

    /**
     * Creates a instance of OptimisticLockingHandler.
     * 
     * @param objid
     *            The unique identifier of the handled object.
     * @param objectType
     *            Type of Resource (required to set name of resource in case of
     *            Exception).
     * @param lastModificationDate
     *            The last modification of the stores object.
     * @param parser
     *            The parser this handler is added.
     */
    public OptimisticLockingHandler(final String objid,
        final String objectType, final String lastModificationDate,
        final StaxParser parser) {
        this.objid = objid;
        this.objectType = objectType;
        this.lastModifiedDate = lastModificationDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#startElement
     * (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws OptimisticLockingException, MissingAttributeValueException,
        WebserverSystemException, InvalidContentException {
        if (!done) {
            final Attribute requestedDate;
            try {
                requestedDate =
                    element.getAttribute(null, MODIFIED_DATE_ATT_NAME);

            }
            catch (final NoSuchAttributeException e) {
                throw new MissingAttributeValueException(
                    "Attribute \"last-modification-date\" of the element "
                        + element.getLocalName() + " is missing.", e);
            }

            requestedModificationDate = requestedDate.getValue();
            if (lastModifiedDate != null) {
                Utility.getInstance().checkOptimisticLockingCriteria(
                    lastModifiedDate, requestedModificationDate,
                    objectType + " with id " + objid);
            }
            done = true;

        }
        return element;
    }

}
