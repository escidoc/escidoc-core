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

    private final static Logger LOG = LoggerFactory.getLogger(IOUtils.class);

    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private IOUtils() {
    }

    public static int copy(final InputStream input,
                           final OutputStream output)
            throws IOException {
        return copy(input, output, DEFAULT_BUFFER_SIZE);
    }

    public static int copyAndCloseInput(final InputStream input,
                                        final OutputStream output) throws IOException {
        try {
            return copy(input, output, DEFAULT_BUFFER_SIZE);
        } finally {
            closeStream(input);
        }
    }

    public static int copyAndCloseInput(final InputStream input,
                                        final OutputStream output,
                                        final int bufferSize) throws IOException {
        try {
            return copy(input, output, bufferSize);
        } finally {
            closeStream(input);
        }
    }

    public static int copy(final InputStream input,
                           final OutputStream output,
                           int bufferSize) throws IOException {
        int avail = input.available();
        if (avail > 262144) {
            avail = 262144;
        }
        if (avail > bufferSize) {
            bufferSize = avail;
        }
        final byte[] buffer = new byte[bufferSize];
        int n;
        n = input.read(buffer);
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
        ByteArrayOutputStream bos = new ByteArrayOutputStream(i);
        copy(input, bos);
        closeStream(input);
        return bos.toByteArray();
    }

    public static String readStringFromStream(final InputStream input) throws IOException {
        int i = input.available();
        if (i < DEFAULT_BUFFER_SIZE) {
            i = DEFAULT_BUFFER_SIZE;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(i);
        copy(input, bos);
        closeStream(input);
        return bos.toString(XmlUtility.CHARACTER_ENCODING);
    }

    public static String newStringFromBytes(final byte[] bytes, final String charsetName) {
        try {
            return new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw
                    new RuntimeException("Unexpected failure: Charset.forName(\""
                            + charsetName + "\") returns invalid name.", e);

        }
    }

    public static String newStringFromBytes(byte[] bytes) {
        return newStringFromBytes(bytes, UTF8_CHARSET.name());
    }

    public static String newStringFromBytes(final byte[] bytes,
                                            final String charsetName,
                                            final int start,
                                            final int length) {
        try {
            return new String(bytes, start, length, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw
                    new RuntimeException("Unexpected failure: Charset.forName(\""
                            + charsetName + "\") returns invalid name.", e);

        }
    }

    public static String newStringFromBytes(final byte[] bytes, final int start, final int length) {
        return newStringFromBytes(bytes, UTF8_CHARSET.name(), start, length);
    }

    public static void closeStream(Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (final IOException e) {
            LOG.debug("Error on closing stream.", e);
        }
    }

    public static void closeWriter(Writer writer) {
        if (writer == null) {
            return;
        }
        try {
            writer.close();
        } catch (final IOException e) {
            LOG.debug("Error on closing writer.", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (final SQLException e) {
            LOG.debug("Error on closing connection.", e);
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        } catch (final SQLException e) {
            LOG.debug("Error on closing result set.", e);
        }
    }

}

