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

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.st.business.persistence.StagingFileDao;

/**
 * Implementation of some staging util.
 *
 * @author Michael Schneider
 */
public final class StagingUtil {

    private static final String STAGING_AREA_BASE_PATH = "catalina.home";

    private static final String STAGING_AREA = "staging";

    private static final String STAGING_AREA_DOWNLOAD = "download";

    private static final String STAGING_AREA_UPLOAD = "upload";

    private static String downloadStagingArea;

    private static String uploadStagingArea;

    /**
     * The duration during which a token is valid, i.e. uploading/downloading data is possible.<br> The value is 1000
     * seconds.
     */
    private static final int TOKEN_VALID_DURATION = 1000000;

    /**
     * Private constructor to avoid instantiation.
     */
    private StagingUtil() {
    }

    /**
     * @return The path to the download staigng area.
     */
    public static String getDownloadStagingArea() {
        if (downloadStagingArea == null) {
            final String systemProperty = System.getProperty(STAGING_AREA_BASE_PATH);
            downloadStagingArea = concatenatePath(systemProperty, STAGING_AREA);
            downloadStagingArea = concatenatePath(downloadStagingArea, STAGING_AREA_DOWNLOAD);
        }
        return downloadStagingArea;
    }

    /**
     * @return The path to the upload staigng area.
     */
    public static String getUploadStagingArea() {
        if (uploadStagingArea == null) {
            uploadStagingArea = concatenatePath(System.getProperty(STAGING_AREA_BASE_PATH), STAGING_AREA);
            uploadStagingArea = concatenatePath(uploadStagingArea, STAGING_AREA_UPLOAD);
        }
        return uploadStagingArea;
    }

    /**
     * Concatenates the two given path segments and returns a valid path, i.e. the method takes care that there is only
     * one path seperator between the path segments, and converts ':' to '_' in the appendix
     *
     * @param path     The path.
     * @param appendix The path to append.
     * @return The concatenated path.
     */
    public static String concatenatePath(final String path, final String appendix) {
        String result = path;
        String append = appendix.replaceAll(":", "_");
        result = result.replace("\\", "/");
        append = append.replace("\\", "/");
        result +=
            result.endsWith("/") ? append.startsWith("/") ? append.substring(1) : append : append.startsWith("/") ? append : '/' + append;
        return result;
    }

    /**
     * Generates a token.<br> This method creates a new staging file object and stores it in the database. The staging
     * file's token is automatially generated during saving this staging file object.<br> In the case of using the
     * hibernate persistence layer, this is currently automatically done by hibernate using the
     * {@code TokenGenerator} class which is an implementation a hibernate id generator.
     *
     * @param isUpload       Flag indicating if this token shall be generated for an upload to the framework
     *                       ({@code true}) or a download from the framework ({@code false}).
     * @param stagingFileDao The data access object used to store the staging file.
     * @return The generated token.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    public static String generateToken(final boolean isUpload, final StagingFileDao stagingFileDao)
        throws SqlDatabaseSystemException {

        final StagingFile stagingFile = generateStagingFile(isUpload, stagingFileDao);
        return stagingFile.getToken();
    }

    /**
     * Creates a new Staging file with initialized token and stores it into the database.
     *
     * @param isUpload       Flag indicating if this token shall be generated for an upload to the framework
     *                       ({@code true}) or a download from the framework ({@code false}).
     * @param stagingFileDao The data access object used to store the staging file.
     * @return Returns the created {@code StagingFile} object.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    public static StagingFile generateStagingFile(final boolean isUpload, final StagingFileDao stagingFileDao)
        throws SqlDatabaseSystemException {

        final long timestamp = System.currentTimeMillis();

        final StagingFile stagingFile = new StagingFile();
        stagingFile.setReference(null);
        stagingFile.setExpiryTs(timestamp + (long) TOKEN_VALID_DURATION);
        stagingFile.setUpload(isUpload);
        stagingFileDao.save(stagingFile);
        return stagingFile;
    }

}
