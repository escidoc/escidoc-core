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

package de.escidoc.core.common.util;

import de.escidoc.core.common.util.xml.XmlUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class IOUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private IOUtils() {
    }

    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        return copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    public static int copyAndCloseInput(final InputStream input, final OutputStream output) throws IOException {
        try {
            return copy(input, output, DEFAULT_BUFFER_SIZE);
        }
        finally {
            closeStream(input);
        }
    }

    public static int copy(final InputStream input, final OutputStream output, int bufferSize) throws IOException {
        int avail = input.available();
        if (avail > 262144) {
            avail = 262144;
        }
        if (avail > bufferSize) {
            bufferSize = avail;
        }
        final byte[] buffer = new byte[bufferSize];
        int n = input.read(buffer);
        int total = 0;
        while (n != -1) {
            output.write(buffer, 0, n);
            total += n;
            n = input.read(buffer);
        }
        return total;
    }

    public static byte[] readBytesFromStream(final InputStream input) throws IOException {
        int i = input.available();
        if (i < DEFAULT_BUFFER_SIZE) {
            i = DEFAULT_BUFFER_SIZE;
        }
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(i);
        copy(input, bos);
        closeStream(input);
        return bos.toByteArray();
    }

    public static String readStringFromStream(final InputStream input) throws IOException, UnsupportedEncodingException {
        int i = input.available();
        if (i < DEFAULT_BUFFER_SIZE) {
            i = DEFAULT_BUFFER_SIZE;
        }
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(i);
        copy(input, bos);
        closeStream(input);
        return bos.toString(XmlUtility.CHARACTER_ENCODING);
    }

    public static String newStringFromBytes(final byte[] bytes, final String charsetName) {
        try {
            return new String(bytes, charsetName);
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Unexpected failure: Charset.forName(\"" + charsetName
                + "\") returns invalid name.", e);

        }
    }

    public static String newStringFromBytes(
        final byte[] bytes, final String charsetName, final int start, final int length) {
        try {
            return new String(bytes, start, length, charsetName);
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Unexpected failure: Charset.forName(\"" + charsetName
                + "\") returns invalid name.", e);

        }
    }

    public static String newStringFromBytes(final byte[] bytes, final int start, final int length) {
        return newStringFromBytes(bytes, UTF8_CHARSET.name(), start, length);
    }

    public static void closeStream(final Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        }
        catch (final IOException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on closing stream.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on closing stream.", e);
            }
        }
    }

    public static void closeWriter(final Writer writer) {
        if (writer == null) {
            return;
        }
        try {
            writer.close();
        }
        catch (final IOException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on closing writer.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on closing writer.", e);
            }
        }
    }

    public static void closeConnection(final Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        }
        catch (final SQLException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on closing connection.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on closing connection.", e);
            }
        }
    }

    public static void closeResultSet(final ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        }
        catch (final SQLException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on closing result set.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on closing result set.", e);
            }
        }
    }

}
