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

package de.escidoc.core.common.util.xml.stax;

import org.codehaus.stax2.io.EscapingWriterFactory;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * {@code EscapingWriterFactory} implementation that returns escaping writers used for escaping attributes.
 *
 * @author Torsten Tetteroo
 */
public class StaxAttributeEscapingWriterFactory implements EscapingWriterFactory {

    /**
     * See Interface for functional description.
     *
     * @see EscapingWriterFactory #createEscapingWriterFor(java.io.Writer, java.lang.String)
     */
    @Override
    public Writer createEscapingWriterFor(final Writer writer, final String enc) throws UnsupportedEncodingException {

        return new StaxEscapingWriter(writer);
    }

    /**
     * See Interface for functional description.
     *
     * @see EscapingWriterFactory #createEscapingWriterFor(java.io.OutputStream, java.lang.String)
     */
    @Override
    public Writer createEscapingWriterFor(final OutputStream out, final String enc) throws UnsupportedEncodingException {

        throw new UnsupportedOperationException("createEscapingWriterFor(OutputStream out, enc) not implemented");
    }

}
