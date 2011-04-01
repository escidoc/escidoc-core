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
package de.escidoc.core.om.business.interfaces;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.EscidocException;

/**
 * This interface abstracts the validation process from the ingestion. The validation process and the information the
 * validation is based on may very well be subject to change as eSciDoc evolves, so the validation should be flexible
 * enough.
 *
 * @author Kai Strnad
 */
public interface IngestValidator {

    /**
     * Check if the given resource is valid assuming the given resource type.
     *
     * @param xmlData
     * @param resourceType
     * @return if the resource is valid.
     * @throws de.escidoc.core.common.exceptions.EscidocException
     */
    boolean isResourceValid(String xmlData, ResourceType resourceType) throws EscidocException;

}
