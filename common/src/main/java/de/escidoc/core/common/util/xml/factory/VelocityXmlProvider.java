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
package de.escidoc.core.common.util.xml.factory;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlEscaper;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * XmlTemplateProvider implementation using the velocity template engine.<br>
 * This implementation uses the velocity singleton pattern.
 * 
 * @author TTE
 * @common
 */
public abstract class VelocityXmlProvider extends XmlTemplateProvider {

    private static final AppLogger LOG =
        new AppLogger(VelocityXmlProvider.class.getName());

    private static Boolean initialized = Boolean.FALSE;

    /**
     * Protected constructor to prevent initialization.
     * 
     * @common
     */
    protected VelocityXmlProvider() {

    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.<br/>
     * This implementation uses the velocity template engine to render the
     * output.<br/>
     * For escaping during rendering attribute values or text content an
     * XMLEscaper implementation is used. If none is specified in the provided
     * map (in property VAR_ESCAPER), a new XmlEscaper is created and added to
     * the map.
     * 
     * @param resource
     * @param path
     * @param values
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.common.util.xml.factory.XmlTemplateProvider
     *      #getXml(java.lang.String, java.lang.String, java.util.Map)
     * @common
     */
    @Override
    public String getXml(
        final String resource, final String path, final Map values)
        throws WebserverSystemException {

        long start = System.nanoTime();

        if (!initialized) {
            synchronized (this) {
                if (!initialized) {

                    // velocity logging configuration
                    Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS,
                        "org.apache.velocity.tools.generic."
                            + "log.CommonsLogLogSystem");
                    Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM,
                        new VelocityOutputLogger());

                    // use class loader
                    Velocity.setProperty("resource.loader", "class");
                    // the classloader class, using own implementation
                    Velocity.setProperty("class.resource.loader.class",
                        VelocityClasspathResourceLoader.class.getName());

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
                    Velocity.setProperty("velocimacro.library",
                        "common/macros.vm");
                    try {
                        Velocity.init();
                        initialized = true;
                    }
                    catch (Exception e) {
                        throw new WebserverSystemException(e.getMessage(), e);
                    }
                }
            }
        }

        // add escaper if none is set
        if (values.get(ESCAPER) == null) {
            values.put(XmlTemplateProvider.ESCAPER, new XmlEscaper());
        }

        final String templateFileName = getTemplateFilename(path, resource);
        Template template = null;
        try {
            template =
                Velocity.getTemplate(templateFileName,
                    XmlUtility.CHARACTER_ENCODING);
            Writer out = new StringWriter();
            VelocityContext context = new VelocityContext(values);
            synchronized (template) {
                template.merge(context, out);
            }
            final String ret = out.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Processed template " + templateFileName + " in " + (System.nanoTime() - start) + "ns");
            }
            return ret;
        }
        catch (Exception e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
    }

    /**
     * 
     * @return
     * @throws WebserverSystemException
     */
    protected abstract String completePath() throws WebserverSystemException;

    /**
     * 
     * @param path
     * @param resource
     * @return
     * @throws WebserverSystemException
     */
    private String getTemplateFilename(final String path, final String resource)
        throws WebserverSystemException {
        String templateFileName = null;
        if (path.startsWith("/")) {
            templateFileName =
                path.substring(1) + "/" + completePath() + "/" + resource
                    + ".vm";
        }
        else {
            templateFileName =
                path + "/" + completePath() + "/" + resource + ".vm";
        }
        return templateFileName;
    }
}
