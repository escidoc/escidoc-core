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
package de.escidoc.core.st.business;

import org.esidoc.core.utils.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represents a file in the staging area.
 *
 * @author Torsten Tetteroo
 */
public class StagingFile extends de.escidoc.core.st.business.persistence.StagingFile {

    /**
     * Checks if this staging file has an associated file in the file system.
     *
     * @return Returns {@code true} if an existing file in the file system has been associated to this staging
     *         file, {@code false} if there does not exist an associated file.
     */
    public boolean hasFile() {

        if (getReference() == null) {
            return false;
        }
        return new File(getReference()).exists();
    }

    /**
     * Checks if this file has an associated file in the file system that is readable.
     *
     * @return Returns {@code true} if an existing, readable file in the file system has been associated to this
     *         staging file.
     */
    public boolean canRead() {

        if (getReference() == null) {
            return false;
        }
        return new File(getReference()).canRead();
    }

    /**
     * Checks if this file has a reference set and this file would be writeable.
     *
     * @return Returns {@code true} if a reference to a file in the file system has been set and the file system's
     *         file would be writeable.
     */
    public boolean canWrite() {

        if (getReference() == null) {
            return false;
        }
        return new File(getReference()).canWrite();
    }

    /**
     * Checks if this staging file has been expired.
     *
     * @return Returns {@code true} if this staging file has been expired.
     */
    public boolean isExpired() {

        return getExpiryTs() <= System.currentTimeMillis();
    }

    /**
     * Gets the file referenced by this staging file.
     *
     * @return The file referenced by this staging file.
     * @throws IOException If file cannot be retrieved.
     */
    private File getFile() throws IOException {

        if (!hasFile()) {
            throw new IOException();
        }
        return new File(getReference());
    }

    /**
     * Creates the file referenced by this staging file.<br> If the destination directory does not exists, it will be
     * created.<br> If the file does exist, it will be overridden.
     *
     * @return The file referenced by this staging file.
     * @throws IOException If file cannot be retrieved.
     */
    public File createFile() throws IOException {

        if (getReference() == null) {
            throw new IOException();
        }
        final File file = new File(getReference());
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return file;
    }

    /**
     * Gets a input stream from this staging file.
     *
     * @return Returns an input stream to access the file associated to this staging file.
     * @throws IOException If file input stream cannot be retrieved.
     */
    public FileInputStream getFileInputStream() throws IOException {

        try {
            return new FileInputStream(getFile());
        }
        catch (final FileNotFoundException e) {
            throw new IOException(e);
        }
    }

    /**
     * Writes file content to given output stream.
     *
     * @param outputStream The stream to which the file content shall be written.
     * @throws IOException If operation fails.
     */
    public void write(final OutputStream outputStream) throws IOException {

        if (outputStream == null) {
            throw new IOException();
        }
        try {
            final InputStream inputStream = getFileInputStream();
            IOUtils.copyAndCloseInput(inputStream, outputStream);
        }
        finally {
            IOUtils.closeStream(outputStream);
        }
    }

    /**
     * Reads file content from given output stream and stores it as File in the file system using the reference value of
     * this staging file.<br> If the referenced destination directory does not exists, it will be created.<br> If the
     * referenced destination file exists, it will be overridden.
     *
     * @param inputStream The input stream to read the content from.
     * @throws IOException If operation fails.
     */
    public void read(final InputStream inputStream) throws IOException {

        if (inputStream == null) {
            throw new IOException();
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(createFile());
            IOUtils.copyAndCloseInput(inputStream, outputStream);
        }
        finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    /**
     * Removes the associated file from the file system and sets the internal file reference to {@code null}.<br>
     * If this staging file does not have an associated staging file, nothing is done.
     *
     * @throws IOException If clear fails.
     */
    public void clear() throws IOException {

        if (hasFile()) {
            getFile().delete();
            setReference(null);
        }
    }
}
