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
package de.escidoc.core.common.servlet;

import javax.servlet.http.HttpServlet;

import de.escidoc.core.common.util.logger.EscidocRepositorySelector;

/**
 * Servlet to initialize eSciDoc web application specific logging.<br>
 * Each web application that shall use its own log4j configuration instead of
 * the jboss log4j configuration should use this servlet and load it on startup:<br>
 * 
 * &lt;servlet&gt;<br>
 * &lt;servlet-name&gt;EsciDocLog4jInitServlet&lt;/servlet-name&gt;<br>
 * &lt;servlet-class&gt;<br>
 * de.escidoc.core.common.servlet.EsciDocLog4jInitServlet&lt;/servlet-class&gt;<br>
 * &lt;load-on-startup&gt;1&lt;/load-on-startup&gt; &lt;/servlet&gt;<br>
 * 
 * @author TTE
 * 
 */
public class EsciDocLog4jInitServlet extends HttpServlet {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 3419357476396430834L;

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() {

        EscidocRepositorySelector.init();
    }

    /**
     * See Interface for functional description.
     * 
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy() {

        EscidocRepositorySelector.remove();
        super.destroy();
    }

    // CHECKSTYLE:JAVADOC-ON

}
