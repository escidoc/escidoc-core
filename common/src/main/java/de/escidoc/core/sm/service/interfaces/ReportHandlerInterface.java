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
import de.escidoc.core.common.exceptions.application.invalid.InvalidSqlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of an Statistic Report Handler.
 *
 * @author Michael Hoppe
 */
public interface ReportHandlerInterface {

    /**
     * Retrieve a Statistic Report.<br/>
     * <p/>
     * Parameter for the Method is an xml corresponding to XML-schema "report-parameters.xsd".<br/>
     * <p/>
     * In this xml you can define:<br/>
     * <p/>
     * <ul> <li>The Report Definition the Statistic Report is based on</li> <li>Additional parameters that fill the
     * placeholders in the sql-statement of the Report Definition.</li> </ul>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Report Definition must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Report Definition is accessed using the provided reference.</li> <li>The
     * Statistic Report is created.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param xml The xml with parameters corresponding to XML-schema "report-parameters.xsd".
     * @return The XML representation of the retrieved Statistic Report corresponding to XML-schema "report.xsd".
     * @throws AuthenticationException      Thrown in case of failed authentication.
     * @throws AuthorizationException       Thrown in case of failed authorization.
     * @throws XmlCorruptedException        Thrown in case of provided invalid xml.
     * @throws XmlSchemaValidationException Thrown in case of provided xml not schema conform.
     * @throws ReportDefinitionNotFoundException
     *                                      e.
     * @throws MissingMethodParameterException
     *                                      e.
     * @throws InvalidSqlException          e.
     * @throws SystemException              e.
     */
    @Validate(param = 0, resolver = "getReportParametersSchemaLocation")
    String retrieve(String xml) throws AuthenticationException, AuthorizationException, XmlCorruptedException,
        XmlSchemaValidationException, ReportDefinitionNotFoundException, MissingMethodParameterException,
        InvalidSqlException, SystemException;

}
