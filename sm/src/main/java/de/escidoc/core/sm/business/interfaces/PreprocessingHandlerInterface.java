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
package de.escidoc.core.sm.business.interfaces;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of an Preprocessing handler of the business layer.
 *
 * @author Michael Hoppe
 */
public interface PreprocessingHandlerInterface {

    /**
     * Preprocess Statistic raw data.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>Extract Aggregation Definition IDs.</li> <li>If no Aggregation Definition IDs are
     * provided, process all Aggregation Definitions.</li> <li>Extract startDate and endDate.</li> <li>Preprocess.</li>
     * </ul>
     *
     * @param aggregationDefinitionId the Aggregation Definition ID to preprocess.
     * @param xmlData                 The XML representation of the Preprocessing Information to be processed
     *                                corresponding to XML-schema "preprocessing-information.xsd".
     * @throws MissingMethodParameterException
     *                         ex
     * @throws SystemException ex
     */
    void preprocess(String aggregationDefinitionId, String xmlData) throws MissingMethodParameterException,
        SystemException;

}
