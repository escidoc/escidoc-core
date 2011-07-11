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
package de.escidoc.core.om.business.fedora.ingest;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.application.invalid.InvalidResourceException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.business.interfaces.IngestValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Implementation of IngestValidator. This class knows how to validate a given resource agains XML Schemas.
 *
 * @author Kai Strnad
 */
@Service("business.ingestValidator")
public class XmlIngestValidator implements IngestValidator {

    @Autowired
    @Qualifier("common.xml.XmlUtility")
    private XmlUtility xmlUtility;

    /**
     * Check if the given resource is valid. If it is not valid, an Exception gets thrown
     *
     * @param xmlData the xmlData
     * @param type    the type of the resource
     * @return if the data is valid or not
     */
    @Override
    public boolean isResourceValid(final String xmlData, final ResourceType type) throws WebserverSystemException,
        XmlCorruptedException, InvalidResourceException {
        try {
            xmlUtility.validate(xmlData, type);
        }
        catch (final XmlSchemaValidationException e) {
            throw new InvalidResourceException(e);
        }
        return true;
    }

}
