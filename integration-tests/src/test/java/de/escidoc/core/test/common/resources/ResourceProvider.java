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
package de.escidoc.core.test.common.resources;

import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provide loading of resource.
 */
public class ResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceProvider.class);

    private static final String DEFAULT_PACKAGE = "/data/";

    private static final int BUFFER_SIZE = 0x4FFF;

    public static InputStream getFileInputStream(final String filename) throws IOException {
        return getFileInputStreamFromResource(DEFAULT_PACKAGE, filename);
    }

    public static InputStream getFileInputStreamFromResource(final String path, final String filename)
        throws IOException {
        InputStream result = null;

        String search = concatenatePath(path, filename);
        // search = concatenatePath(search.replace(".", "/"), filename);
        result = filename.getClass().getResourceAsStream(search);
        if (result == null) {
            throw new IOException("Resource not found [" + search + "]");
        }

        return result;
    }

    /**
     * Get InputStream from File.
     *
     * @param path     Path ot file.
     * @param filename Name of file.
     * @return InputStream from file.
     * @throws IOException Thrown if open FileInputStream failed.
     */
    public static InputStream getFileInputStreamFromFile(final String path, final String filename) throws IOException {
        final PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver =
            new PathMatchingResourcePatternResolver();
        Resource resource = pathMatchingResourcePatternResolver.getResource("classpath:/" + path + "/" + filename);
        return resource.getInputStream();
    }

    /**
     * Save the content to the file described by path and filename.
     *
     * @param path     The path.
     * @param filename The filename.
     * @param content  The content.
     * @throws IOException If anything fails.
     */
    public static void saveToFile(final String path, final String filename, final String content) throws IOException {

        try {
            File filepath = new File(path);
            filepath.mkdirs();
            FileOutputStream f = new FileOutputStream(new File(ResourceProvider.concatenatePath(path, filename)));
            f.write(content.getBytes(HttpHelper.HTTP_DEFAULT_CHARSET));
            f.flush();
            f.close();
        }
        catch (final IOException e) {
            LOGGER.error("", e);
            throw e;
        }
    }

    /**
     * Concatenates the two given path segments and returns a valid path, i.e. the method takes care that there is only
     * one path separator between the path segments.
     *
     * @param path     The path.
     * @param appendix The path to append.
     * @return The concatenated path.
     */
    public static String concatenatePath(final String path, final String appendix) {
        String result = path;
        String append = appendix;
        result = result.replace("\\", "/");
        append = append.replace("\\", "/");
        if (!result.endsWith("/")) {
            if (!append.startsWith("/")) {
                result += "/" + append;
            }
            else {
                result += append;
            }
        }
        else {
            if (!append.startsWith("/")) {
                result += append;
            }
            else {
                result += append.substring(1);
            }
        }
        return result;
    }

    /**
     * Read InputStream and return the content as String. The InputStream in closed() if EOF.
     *
     * @param inputStream The InputStream.
     * @return The file content as String.
     * @throws IOException Thrown in case of I/O error.
     */
    public static String getContentsFromInputStream(final InputStream inputStream) throws IOException {

        byte[] buffer = new byte[BUFFER_SIZE];

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while ((b = inputStream.read(buffer)) > 0) {
            out.write(buffer, 0, b);
        }
        inputStream.close();
        return new String(out.toByteArray(), EscidocTestBase.DEFAULT_CHARSET);
    }
}
