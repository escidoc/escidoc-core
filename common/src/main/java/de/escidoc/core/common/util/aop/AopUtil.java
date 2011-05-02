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

package de.escidoc.core.common.util.aop;

/**
 * Utility class for aop in eSciDoc.<br> Besides providing utility methods, the order of the interceptors (advises) is
 * defined here.
 *
 * @author Torsten Tetteroo
 */
public final class AopUtil {

    public static final int PRECEDENCE_STATISTIC_INTERCEPTOR = 0;

    public static final int PRECEDENCE_AUTHENTICATION_INTERCEPTOR = 1;

    public static final int PRECEDENCE_PARAMETER_CHECK_INTERCEPTOR = 2;

    public static final int PRECEDENCE_XML_VALIDATION_INTERCEPTOR = 3;

    public static final int PRECEDENCE_SECURITY_INTERCEPTOR = 4;

    public static final int PRECEDENCE_XML_HEADER_INTERCEPTOR = 5;

    /**
     * Private constructor to prevent initialization.
     */
    private AopUtil() {
    }

}
