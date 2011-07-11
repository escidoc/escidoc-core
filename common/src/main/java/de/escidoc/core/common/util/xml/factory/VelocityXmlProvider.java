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

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlEscaper;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * XmlTemplateProviderConstants implementation using the velocity template engine.<br> This implementation uses the velocity
 * singleton pattern.
 *
 * @author Torsten Tetteroo
 */
public abstract class VelocityXmlProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityXmlProvider.class);

    /**
     * Protected constructor to prevent initialization.
     */
    protected VelocityXmlProvider() {
        // velocity logging configuration
        Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.tools.generic."
            + "log.CommonsLogLogSystem");
        Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new VelocityOutputLogger());
        // use class loader
        Velocity.setProperty("resource.loader", "class");
        // the classloader class, using own implementation
        Velocity.setProperty("class.resource.loader.class", VelocityClasspathResourceLoader.class.getName());
        Velocity.setProperty("class.resource.loader.cache", "true");
        Velocity.setProperty("directive.set.null.allowed", "true");
        // There is a problem with macros defined in template files
        // that shall be parsed while handling another template:
        // The parsed-in macro won't be available on the first
        // request.
        // see
        // http://mail-archives.apache.org/mod_mbox/velocity-user/
        // 200508.mbox/%3C42F9FADE.7050602@interstructure.ca%3E
        //
        // Solutions: see
        // http://velocity.apache.org/engine/devel/developer-guide.html
        // We try the velocimacro.library = macro.vm way
        Velocity.setProperty("velocimacro.library", "common/macros.vm");
        try {
            Velocity.init();
        }
        catch (final Exception e) {
            LOGGER.error("Error on initializing Velocity!", e);
        }
    }

    /**
     * See Interface for functional description.<br/> This implementation uses the velocity template engine to render
     * the output.<br/> For escaping during rendering attribute values or text content an XMLEscaper implementation is
     * used. If none is specified in the provided map (in property VAR_ESCAPER), a new XmlEscaper is created and added
     * to the map.
     *
     * @param resource
     * @param path
     * @param values
     * @see XmlTemplateProviderConstants #getXml(java.lang.String, java.lang.String, java.util.Map)
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @return
     */
    public String getXml(final String resource, final String path, final Map values) throws WebserverSystemException {
        // add escaper if none is set
        if (values.get(XmlTemplateProviderConstants.ESCAPER) == null) {
            values.put(XmlTemplateProviderConstants.ESCAPER, new XmlEscaper());
        }

        final String templateFileName = getTemplateFilename(path, resource);
        try {
            final Template template = Velocity.getTemplate(templateFileName, XmlUtility.CHARACTER_ENCODING);
            final Writer out = new StringWriter();
            final Context context = new VelocityContext(values);
            template.merge(context, out);
            return out.toString();
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
    }

    /**
     * @return
     * @throws WebserverSystemException
     */
    protected abstract String completePath();

    /**
     * @param path
     * @param resource
     * @return
     * @throws WebserverSystemException
     */
    private String getTemplateFilename(final String path, final String resource) {
        return path.startsWith("/") ? path.substring(1) + '/' + completePath() + '/' + resource + ".vm" : path + '/'
            + completePath() + '/' + resource + ".vm";
    }
}
