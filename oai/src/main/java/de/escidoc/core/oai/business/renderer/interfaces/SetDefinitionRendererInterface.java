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
package de.escidoc.core.oai.business.renderer.interfaces;

import de.escidoc.core.common.business.filter.RecordPacking;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.oai.business.persistence.SetDefinition;

import java.util.List;

/**
 * Interface of an user group renderer.
 *
 * @author Rozita Friedman
 */
public interface SetDefinitionRendererInterface {

    /**
     * Gets the representation of a set definition.
     *
     * @param setDefinition The set definition to render.
     * @return Returns the XML representation of the set definition.
     * @throws SystemException Thrown in case of an internal error.
     */
    String render(final SetDefinition setDefinition) throws SystemException;

    /**
     * Gets the representation of a list of the provided set definitions.
     *
     * @param setDefinitions The {@code List} of {@link SetDefinition} objects to render.
     * @param recordPacking  A string to determine how the record should be escaped in the response. Defined values are
     *                       'string' and 'xml'. The default is 'xml'.
     * @return Returns the XML representation of the list of set definitions.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderSetDefinitions(final List<SetDefinition> setDefinitions, final RecordPacking recordPacking)
        throws SystemException;
}
