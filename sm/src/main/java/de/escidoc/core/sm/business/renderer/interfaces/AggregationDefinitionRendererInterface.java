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
package de.escidoc.core.sm.business.renderer.interfaces;

import de.escidoc.core.common.business.filter.RecordPacking;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition;

import java.util.Collection;

/**
 * Interface of an aggregation definition renderer.
 *
 * @author Michael Hoppe
 */
public interface AggregationDefinitionRendererInterface {

    /**
     * Gets the representation of an aggregation definition.
     *
     * @param aggregationDefinition The aggregation definition to render.
     * @return Returns the XML representation of the aggregation definition.
     * @throws SystemException Thrown in case of an internal error.
     */
    String render(final AggregationDefinition aggregationDefinition) throws SystemException;

    /**
     * Gets the representation of a list of the provided user accounts.
     *
     * @param aggregationDefinitions The {@code List} of {@link de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition}
     *                               objects to render.
     * @param recordPacking          A string to determine how the record should be escaped in the response. Defined
     *                               values are 'string' and 'xml'. The default is 'xml'.
     * @return Returns the XML representation of the list of aggregationDefinitions.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderAggregationDefinitions(
        final Collection<AggregationDefinition> aggregationDefinitions, final RecordPacking recordPacking)
        throws SystemException;

}
