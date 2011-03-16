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

package de.escidoc.core.common.servlet;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.BeanLocator;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.regex.Pattern;

/**
 * Servlet that initializes the AA beans during startup.
 * 
 * @author TTE
 * 
 */
public class InitBeansServlet extends HttpServlet {

    /**
     * Pattern used to split the comma separated list of bean ids.
     */
    private static final Pattern PATTERN_SPLIT_IDS = Pattern.compile(",\\s*");
    private static final long serialVersionUID = -1471080999315442967L;

    private String factoryId;

    /**
     * See Interface for functional description.
     * 
     * @throws ServletException
     *             Thrown in case of an error.
     * 
     * @see GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {

        super.init();

        factoryId = getServletConfig().getInitParameter("factoryId");
        final String beanIds = getServletConfig().getInitParameter("beanIds");

        if (factoryId == null || beanIds == null) {
            throw new ServletException(
                "factory id and bean ids must be specified as init parameters.");
        }

        try {
            final String[] splitted = PATTERN_SPLIT_IDS.split(beanIds);
            for (final String beanId : splitted) {
                BeanLocator.getBean(factoryId, beanId);
            }
        }
        catch (final WebserverSystemException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @see GenericServlet#destroy()
     */
    @Override
    public void destroy() {

        super.destroy();
    }

}
