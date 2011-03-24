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

package de.escidoc.core.common.binding;

import de.escidoc.core.common.exceptions.system.XmlParserSystemException;

/**
 * Interface of an Binding Handler of the business layer.
 * 
 * @author Michael Hoppe
 * 
 *
 */
public interface BindingHandlerInterface {

    /**
     * Unmarshals given xml-data to java-binding-object. Needs contextPath
     * (package-name of binding-object).
     * 
     * @param xml
     *            xml.
     * @param contextPath
     *            contextPath.
     * @return Returns Java binding Object.
     * 
     * @throws XmlParserSystemException
     *             ex
     * 
     *
     */
    Object unmarshal(final String xml, final String contextPath)
        throws XmlParserSystemException;

    /**
     * Marshals given Object to xml-String.
     * 
     * @param bindingObject
     *            bindingObject.
     * @return String xml.
     * 
     * @throws XmlParserSystemException
     *             ex
     * 
     *
     */
    String marshal(final Object bindingObject) throws XmlParserSystemException;
    
}
