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

package de.escidoc.core.common.util.service;

/**
 * The SOAP mapping container class.
 * 
 * @author Bernhard Kraus (Accenture)
 */
public class BeanMapping {
    private String namespaceUri;

    private String namespace;

    private Class bean;

    /**
     * Returns the mappend Bean.
     * 
     * @return Class The
     */
    public Class getBean() {
        return bean;
    }

    /**
     * Sets the mapped bean.
     * 
     * @param bean
     *            Mapped bean
     */
    public void setBean(final Class bean) {
        this.bean = bean;
    }

    /**
     * Returns the NamespaceUri of the bean mapping.
     * 
     * @return String Returns the NamespaceUri
     */
    public String getNamespaceUri() {
        return namespaceUri;
    }

    /**
     * Sets the NamespaceUri of the bean mapping.
     * 
     * @param namespaceUri
     *            The NamespaceUri
     */
    public void setNamespaceUri(final String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }

    /**
     * Returns the Namespace of the bean mapping.
     * 
     * @return String The Namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the Namespace of the bean mapping.
     * 
     * @param namespace
     *            The Namespace
     */
    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

}
