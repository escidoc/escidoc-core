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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.StagingFileNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.st.business.interfaces.StagingFileHandlerInterface;
import de.escidoc.core.st.business.persistence.StagingFileDao;
import org.joda.time.DateTime;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Staging File Handler implementation.
 * 
 * @spring.bean id="business.StagingFileHandler"
 * @author TTE
 * 
 */
public class StagingFileHandler implements StagingFileHandlerInterface {


    private static final AppLogger LOG = new AppLogger(StagingFileHandler.class.getName());
    private StagingFileDao dao;

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param binaryContent
     * @return
     * @throws MissingMethodParameterException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @see de.escidoc.core.st.service.interfaces.StagingFileHandlerInterface
     *      #create(de.escidoc.core.om.service.result.EscidocBinaryContent)
     * @st
     */
    @Override
    public String create(final EscidocBinaryContent binaryContent)
        throws MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        StagingFile stagingFile = null;
        String token = null;
        boolean bytesRead;
        try {
            if (binaryContent == null || binaryContent.getContent() == null) {
                throw new MissingMethodParameterException(
                    "Binary content must be provided.");
            }
            stagingFile = StagingUtil.generateStagingFile(true, dao);
            token = stagingFile.getToken();
            stagingFile.setReference(StagingUtil.concatenatePath(StagingUtil
                .getUploadStagingArea(), token));
            bytesRead = stagingFile.read(binaryContent.getContent());
        }
        catch (IOException e) {
            bytesRead = false;
        }
  
        if (!bytesRead || stagingFile == null) {
                     throw new MissingMethodParameterException(
                "Binary content must be provided.");
        }
        stagingFile.setMimeType(binaryContent.getMimeType());
        dao.update(stagingFile);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer;
        try {
            writer = XmlUtility.createXmlStreamWriter(out);

            XmlUtility.setCommonPrefixes(writer);
            writer.setDefaultNamespace(Constants.STAGING_FILE_NS_URI);
            writer.setPrefix("staging-file", Constants.STAGING_FILE_NS_URI);

            writer.writeStartElement("staging-file", "staging-file",
                Constants.STAGING_FILE_NS_URI);
            XmlUtility.addXmlBaseAttribute(writer);
            writer.writeDefaultNamespace(Constants.STAGING_FILE_NS_URI);
            writer
                .writeNamespace("staging-file", Constants.STAGING_FILE_NS_URI);
            XmlUtility.addCommonNamespaces(writer);
            XmlUtility.addXlinkAttributes(writer, null, "/st/staging-file/"+ token);
            XmlUtility.addLastModificationDateAttribute(writer, new DateTime());

            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();

            return out.toString(XmlUtility.CHARACTER_ENCODING);
        }
        catch (XMLStreamException e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }finally {
            try {
                if (binaryContent.getContent() != null) {
                    binaryContent.getContent().close();
                }
            }
            catch (IOException e) {
                LOG.error("error on closing stream", e);
            }
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @param stagingFileId
     * @return
     * @throws StagingFileNotFoundException
     * @throws AuthenticationException
     * @throws AuthorizationException
     * @throws MissingMethodParameterException
     * @see de.escidoc.core.st.service.interfaces.StagingFileHandlerInterface
     *      #retrieve(java.lang.String)
     * @st
     */
    @Override
    public EscidocBinaryContent retrieve(final String stagingFileId)
        throws StagingFileNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException,
        SystemException {

        StagingFile stagingFile = getStagingFile(stagingFileId);
        EscidocBinaryContent binaryContent = new EscidocBinaryContent();
        binaryContent.setMimeType(stagingFile.getMimeType());
        binaryContent.setFileName(stagingFile.getReference());
        try {
            binaryContent.setContent(stagingFile.getFileInputStream());
        }
        catch (IOException e) {
            throw new StagingFileNotFoundException(
                "Binary content of addressed staging file cannot be found.");
        }

        // finally, the staging file is set to expired to prevent further
        // accesses to the binary content.
        stagingFile.setExpiryTs(System.currentTimeMillis());
        dao.update(stagingFile);

        return binaryContent;
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * Setter for the dao.
     * 
     * @spring.property ref="persistence.StagingFileDao"
     * @param dao
     *            The data access object.
     * 
     * @um
     */
    public void setDao(final StagingFileDao dao) {
        this.dao = dao;
    }

    /**
     * Retrieve the staging file with the provided id.
     * 
     * @param stagingFileId
     *            The StagingFile id.
     * @return The staging file.
     * @throws MissingMethodParameterException
     *             Thrown in case of missing id.
     * @throws StagingFileNotFoundException
     *             Thrown if no staging file with provided id exists.
     * @throws SystemException
     *             Thrown in case of an internal database error.
     * @st
     */
    private StagingFile getStagingFile(final String stagingFileId)
        throws MissingMethodParameterException, StagingFileNotFoundException,
        SystemException {

        StagingFile result;
        if (stagingFileId == null) {
            throw new MissingMethodParameterException(
                "staging file id must be provided.");
        }

        result = dao.findStagingFile(stagingFileId);
        if ((result == null) || (result.isExpired())) {
            throw new StagingFileNotFoundException(StringUtility
                .format(
                        "Provided id does not match valid staging file.",
                        stagingFileId));
        }
        return result;
    }

}
