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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.om.context;

import java.util.HashMap;

/**
 * @author Michael Hoppe
 *
 */
public class ContextThreadRunnable extends ContextTestBase implements Runnable {

    private HashMap<String, String> parameters;

    public String message = null;

    /**
     * @param transport The transport identifier.
     */
    public ContextThreadRunnable(final HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public void run() {
        try {
            for (;;) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                if (parameters.get("action").equals("update")) {
                    String contextXml = retrieve(parameters.get("id"));
                    contextXml =
                        contextXml.replaceFirst(parameters.get("searchString"), parameters.get("searchString") + "1");
                    update(parameters.get("id"), contextXml);
                }
                else if (parameters.get("action").equals("retrieve")) {
                    String contextXml = retrieve(parameters.get("id"));
                    if (contextXml.contains(parameters.get("searchString") + "1")) {
                        throw new Exception("context " + parameters.get("id") + " contains wrong data");
                    }
                }
            }
        }
        catch (Exception e) {
            message = e.toString();
        }
    }

}
