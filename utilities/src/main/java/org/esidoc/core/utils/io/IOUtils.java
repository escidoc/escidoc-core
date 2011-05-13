package org.esidoc.core.utils.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
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
            closeInputStream(input);
        }
    }

    public static int copyAndCloseInput(final InputStream input,
                                        final OutputStream output,
                                        final int bufferSize) throws IOException {
        try {
            return copy(input, output, bufferSize);
        } finally {
            closeInputStream(input);
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
        while (-1 != n) {
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
        closeInputStream(input);
        return bos.toByteArray();
    }

    public static String newStringFromBytes(final byte[] bytes, final String charsetName) {
        try {
            return new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw
                    new RuntimeException("Impossible failure: Charset.forName(\""
                            + charsetName + "\") returns invalid name.");

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
                    new RuntimeException("Impossible failure: Charset.forName(\""
                            + charsetName + "\") returns invalid name.");

        }
    }

    public static String newStringFromBytes(final byte[] bytes, final int start, final int length) {
        return newStringFromBytes(bytes, UTF8_CHARSET.name(), start, length);
    }

    public static void closeInputStream(InputStream input) {
        if (input == null) {
            return;
        }
        try {
            input.close();
        } catch (final IOException e) {
            LOG.error("Error on closing input stream.", e);
        }
    }

    public static void closeOutputStream(OutputStream input) {
        if (input == null) {
            return;
        }
        try {
            input.close();
        } catch (final IOException e) {
            LOG.error("Error on closing output stream.", e);
        }
    }

}
