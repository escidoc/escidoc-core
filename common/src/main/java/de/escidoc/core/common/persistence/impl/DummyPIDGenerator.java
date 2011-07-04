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

package de.escidoc.core.common.persistence.impl;

import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.PidSystemException;
import de.escidoc.core.common.persistence.PIDSystem;

/**
 * Delivering of fake identifier of a PID System to check implementation without creating real Persistent Identifier.
 *
 * @author Frank Schwichtenberg
 */
public class DummyPIDGenerator implements PIDSystem {

    private String pidNamespace = "hdl";

    private String globalPrefix;

    private String separator = "/";

    private String localPrefix = "test";

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.common.persistence.PIDSystem#assignPID(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public String assignPID(final String systemID, final String param) throws PidSystemException,
        MissingMethodParameterException {

        if (param == null) {
            throw new MissingMethodParameterException("Invalid param structure.");
        }

        return generatePID(systemID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.common.persistence.PIDSystem#generatePID(java.lang.String)
     */
    @Override
    public String generatePID(final String systemID) throws PidSystemException {
        String result = this.pidNamespace + ':' + this.globalPrefix + this.separator;
        if (this.localPrefix != null && localPrefix.length() > 0) {
            result += this.localPrefix + this.separator;
        }
        result += systemID;

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.common.persistence.PIDSystem#neverGeneratePID(java.lang.String)
     */
    @Override
    public void neverGeneratePID(final String pid) {
        throw new UnsupportedOperationException("Method neverGeneratePID() not supported by DummyPIDGenerator.");
    }

    /**
     * Set the globalPrefix for generated PIDs.
     *
     * @param globalPrefix The globalPrefix for generated PIDs
     * @throws MissingMethodParameterException
     *          If {@code globalPrefix} is null.
     */
    public void setGlobalPrefix(final String globalPrefix) throws MissingMethodParameterException {
        Utility.checkNotNull(globalPrefix, "global prefix for PID");
        this.globalPrefix = globalPrefix;
    }

    /**
     * Set the localPrefix for generated PIDs. This a part of the PID between the global prefix and the system id.
     * Default is "test" to indicate that the generated (Dummy-)PIDs are not registered.
     *
     * @param localPrefix The localPrefix for generated PIDs
     */
    public void setLocalPrefix(final String localPrefix) {
        this.localPrefix = localPrefix;
    }

    /**
     * Set the separator between the parts of generated PIDs. Default is "/".
     *
     * @param separator The separator for generated PIDs
     * @throws MissingMethodParameterException
     *          If {@code separator} is null.
     */
    public void setSeparator(final String separator) throws MissingMethodParameterException {
        Utility.checkNotNull(separator, "separator");
        this.separator = separator;
    }

    /**
     * Set the separator between the parts of generated PIDs. Default is "/".
     *
     * @param pidNamespace The name space for generated PIDs
     * @throws MissingMethodParameterException
     *          If {@code pidNamespace} is null.
     */
    public void setPidNamespace(final String pidNamespace) throws MissingMethodParameterException {
        Utility.checkNotNull(pidNamespace, "namespace for PID");
        this.pidNamespace = pidNamespace;
    }

}
