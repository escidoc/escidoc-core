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

package de.escidoc.core.test.common;

import java.net.URL;

/**
 * Parameter for assign PID methods.
 * 
 * @author SWA
 *
 */
public class AssignParam {

    private URL url;

    private String pid;

    /**
     * 
     */
    public AssignParam() {

    }

    /**
     * 
     * @return
     */
    public URL getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * 
     * @return
     */
    public String getPid() {
        return pid;
    }

    /**
     * 
     * @param pid
     */
    public void setPid(String pid) {
        this.pid = pid;
    }
}
