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

import de.escidoc.core.common.util.xml.XmlUtility;

import java.io.IOException;
import java.io.Writer;

/**
 * {@code Writer} implementation that is used to escape special XML characters.<br/> For escaping, the method
 * escapeForbiddenXmlCharacters of {@link XmlUtility} is used.
 *
 * @author Torsten Tetteroo
 */
public class StaxEscapingWriter extends Writer {

    private final Writer writer;

    /**
     * Constructs a {@code StaxEscapingWriter}.
     *
     * @param writer          The {@code Writer} used for output.
     */
    public StaxEscapingWriter(final Writer writer) {

        this.writer = writer;
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public void close() throws IOException {

        writer.close();
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public void flush() throws IOException {

        writer.flush();
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {

        writer.write(XmlUtility.escapeForbiddenXmlCharacters(new String(cbuf, off, len)));
    }

}
