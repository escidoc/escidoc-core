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

package de.escidoc.core.common.util.xml.stax.handler;

import java.util.Date;

import javax.naming.directory.NoSuchAttributeException;

import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.LastModificationDateMissingException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

/**
 * Stax handler that handles the last modification attribute and checks the optimistic locking criteria.
 *
 * @author Torsten Tetteroo
 */
public class OptimisticLockingStaxHandler extends DefaultHandler {

    private final Date expectedLastModificationDate;

    private boolean rootElementFound;

    /**
     * The constructor.
     *
     * @param expectedLastModificationDate The expected last modification date. If this parameter is not
     *                                     {@code null}, the last modification date extracted from the xml is
     *                                     compared with this value. If this fails, an {@code OptimisticLockingException}
     *                                     is thrown.
     */
    public OptimisticLockingStaxHandler(final Date expectedLastModificationDate) {

        this.expectedLastModificationDate = expectedLastModificationDate;
    }

    //  CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * @throws XmlCorruptedException 
     * @throws LastModificationDateMissingException 
     *
     * @see DefaultHandler #startElement (de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element) throws MissingAttributeValueException,
        OptimisticLockingException, LastModificationDateMissingException {

        final boolean notReadyFlag = isNotReady();

        // The last modification date attribute has to be fetched from the root
        // which is the first element that is processed during xml parsing.
        if (notReadyFlag && !this.rootElementFound) {

            this.rootElementFound = true;
            try {
                final String lastModificationDateValue =
                    element.getAttributeValue(null, XmlUtility.NAME_LAST_MODIFICATION_DATE);
                Utility.checkOptimisticLockingCriteria(XmlUtility.normalizeDate(expectedLastModificationDate),
                    lastModificationDateValue, "resource");
            }
            catch (final NoSuchAttributeException e) {
                throw new LastModificationDateMissingException();
            }
            catch (final XmlCorruptedException e) {
                throw new LastModificationDateMissingException();
            }
        }

        return element;
    }

}
