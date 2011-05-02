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

package de.escidoc.core.sm.service.interfaces;

import de.escidoc.core.common.annotation.Validate;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of an Preprocessing Handler.
 *
 * @author Michael Hoppe
 */
public interface PreprocessingHandlerInterface {

    /**
     * Preprocess Statistic raw data.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>Extract startDate and endDate.</li> <li>Get data from database-table containing the
     * rae statistic-data (aa.statistic_data).</li> <li>Preprocess data according to aggregation-table desciption in
     * aggregation-definition.</li> <li>Write data into aggregation-tables.</li> </ul>
     *
     * @param aggregationDefinitionId Aggregation Definition ID to preprocess.
     * @param xmlData                 The XML representation of the Preprocessing Information to be processed
     *                                corresponding to XML-schema "preprocessing-information.xsd".
     * @throws AuthenticationException      Thrown in case of failed authentication.
     * @throws AuthorizationException       Thrown in case of failed authorization.
     * @throws XmlSchemaValidationException ex
     * @throws XmlCorruptedException        ex
     * @throws MissingMethodParameterException
     *                                      ex
     * @throws SystemException              ex
     */
    @Validate(param = 1, resolver = "getPreprocessingInformationSchemaLocation")
    void preprocess(String aggregationDefinitionId, String xmlData) throws AuthenticationException,
        AuthorizationException, XmlSchemaValidationException, XmlCorruptedException, MissingMethodParameterException,
        SystemException;

}
