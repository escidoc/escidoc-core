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

package de.escidoc.core.common.util.xml.factory;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import java.io.InputStream;

/**
 * ResourceLoader implementation that wraps a {@code ClasspathResourceLoader} and changes the resource string by
 * adding {@code TEMPLATES_BASE} as prefix.
 *
 * @author Torsten Tetteroo
 */
public class VelocityClasspathResourceLoader extends ResourceLoader {

    private static final String TEMPLATES_BASE = "/META-INF/templates/";

    private final ClasspathResourceLoader loader;

    /**
     * The constructor.
     */
    public VelocityClasspathResourceLoader() {
        this.loader = new ClasspathResourceLoader();
    }

    /**
     * See Interface for functional description.
     *
     * @see ResourceLoader #getLastModified(org.apache.velocity.runtime.resource.Resource)
     */
    @Override
    public long getLastModified(final Resource resource) {

        resource.setName(TEMPLATES_BASE + resource.getName());
        return loader.getLastModified(resource);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public InputStream getResourceStream(final String s) {

        final String source = TEMPLATES_BASE + s;
        return loader.getResourceStream(source);
    }

    /**
     * See Interface for functional description.
     *
     * @see ResourceLoader #init(org.apache.commons.collections.ExtendedProperties)
     */
    @Override
    public void init(final ExtendedProperties extendedproperties) {

        setCachingOn(true);
    }

    /**
     * See Interface for functional description.
     *
     * @see ResourceLoader #isSourceModified(org.apache.velocity.runtime.resource.Resource)
     */
    @Override
    public boolean isSourceModified(final Resource resource) {

        resource.setName(TEMPLATES_BASE + resource.getName());
        return loader.isSourceModified(resource);
    }

}
