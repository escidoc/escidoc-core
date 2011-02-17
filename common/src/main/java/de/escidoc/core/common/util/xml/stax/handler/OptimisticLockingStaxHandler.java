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
package de.escidoc.core.common.util.xml.stax.handler;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.util.date.Iso8601Util;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

import javax.naming.directory.NoSuchAttributeException;
import java.text.ParseException;
import java.util.Date;

/**
 * Stax handler that handles the last modification attribute and checks the
 * optimistic locking criteria.
 * 
 * @author TTE
 * @common
 */
public class OptimisticLockingStaxHandler extends DefaultHandler {
    
    private Date expectedLastModificationDate;

    private boolean rootElementFound = false;

    /**
     * The constructor.
     * 
     * @param expectedLastModificationDate
     *            The expected last modification date. If this parameter is not
     *            <code>null</code>, the last modification date extracted
     *            from the xml is compared with this value. If this fails, an
     *            <code>OptimisticLockingException</code> is thrown.
     */
    public OptimisticLockingStaxHandler(final Date expectedLastModificationDate) {

        this.expectedLastModificationDate = expectedLastModificationDate;
    }
    
//  CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @throws OptimisticLockingException
     * @throws MissingAttributeValueException
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler
     *      #startElement
     *      (de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @um
     */
    public StartElement startElement(final StartElement element)
        throws EscidocException {

        final boolean notReadyFlag = isNotReady();
        
        // The last modification date attribute has to be fetched from the root
        // which is the first element that is processed during xml parsing.
        if (notReadyFlag && !rootElementFound) {
            
            rootElementFound = true;
            String lastModificationDateValue;
            Date lastModificationDate;
            try {
                lastModificationDateValue = element.getAttributeValue(null,
                    XmlUtility.NAME_LAST_MODIFICATION_DATE);
                
                try {
                    lastModificationDate = 
                        Iso8601Util
                        .parseIso8601(lastModificationDateValue);
                    if (expectedLastModificationDate != null
                        && !lastModificationDate.equals(
                        expectedLastModificationDate)) {
                        throw new OptimisticLockingException(StringUtility
                            .format(
                                    "Optimistic locking error", Iso8601Util
                                    .getIso8601(expectedLastModificationDate),
                                    lastModificationDateValue));
                    }
                }
                catch (ParseException e) {
                    // this should not happen as the date format has been
                    // validated during schema validation.
                }
            }
            catch (NoSuchAttributeException e) {
                XmlUtility.throwMissingAttributeValueException(element,
                    XmlUtility.NAME_LAST_MODIFICATION_DATE);
            }
        }

        return element;
    }

    // CHECKSTYLE:JAVADOC-ON
}
