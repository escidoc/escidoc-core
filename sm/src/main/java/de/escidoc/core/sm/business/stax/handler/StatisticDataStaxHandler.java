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
package de.escidoc.core.sm.business.stax.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Extracts data from statistic-data xml.
 *
 * @author Michael Hoppe
 */
public class StatisticDataStaxHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticDataStaxHandler.class);

    private String scopeId = null;

    /**
     * Handle startElement event.
     *
     * @param element startElement
     * @return StartElement startElement
     */
    @Override
    public StartElement startElement(final StartElement element) throws IntegritySystemException {
        if ("scope".equals(element.getLocalName())) {
            try {
                setScopeId(XmlUtility.getIdFromStartElement(element));
            }
            catch (final MissingAttributeValueException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Missing attribute value.", e);
                }
            }
        }
        return element;
    }

    /**
     * @return the scopeId
     */
    public String getScopeId() {
        return scopeId;
    }

    /**
     * @param scopeId the scopeId to set
     */
    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

}
