/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
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

package de.escidoc.core.common.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Wrapper to always return a reference to the Spring Application Context from within non-Spring enabled beans. Unlike
 * Spring MVC's WebApplicationContextUtils we do not need a reference to the Servlet context for this. All we need is
 * for this bean to be initialized during application startup.
 */
@Component
public class SpringApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * This method is called from within the ApplicationContext once it is done starting up, it will stick a reference
     * to itself into this bean.
     *
     * @param newContext a reference to the ApplicationContext.
     */
    @Override
    public void setApplicationContext(final ApplicationContext newContext) {
        context = newContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
