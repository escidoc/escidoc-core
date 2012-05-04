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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.adm.internal;

import de.escidoc.core.adm.AdminRestService;
import de.escidoc.core.adm.service.interfaces.AdminHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.escidoc.core.domain.properties.java.JavaUtilPropertiesTO;
import org.escidoc.core.domain.result.ResultTO;
import org.escidoc.core.domain.sb.IndexConfigurationTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.taskparam.deleteobjects.DeleteObjectsTaskParamTO;
import org.escidoc.core.domain.taskparam.reindex.ReindexTaskParamTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.PathParam;

/**
 * @author Michael Hoppe
 *
 */
@Transactional
public class AdminRestServiceImpl implements AdminRestService {

    @Autowired
    @Qualifier("service.AdminHandler")
    private AdminHandlerInterface adminHandler;
    
    @Autowired
    private ServiceUtility serviceUtility;

    /**
     * 
     */
    public AdminRestServiceImpl() {
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.adm.AdminRestService#getPurgeStatus()
     */
    @Override
    public ResultTO getPurgeStatus() throws AuthenticationException, AuthorizationException, SystemException {
        return serviceUtility.fromXML(ResultTO.class, this.adminHandler.getPurgeStatus());
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.adm.AdminRestService#deleteObjects(org.escidoc.core.domain.taskparam.IdSetTaskParamTO)
     */
    @Override
    public ResultTO deleteObjects(DeleteObjectsTaskParamTO ids) throws AuthenticationException, AuthorizationException,
        InvalidXmlException, SystemException {
        return serviceUtility.fromXML(ResultTO.class, this.adminHandler.deleteObjects(serviceUtility.toXML(ids)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.adm.AdminRestService#getReindexStatus()
     */
    @Override
    public ResultTO getReindexStatus() throws AuthenticationException, AuthorizationException, SystemException {
        return serviceUtility.fromXML(ResultTO.class, this.adminHandler.getReindexStatus());
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.adm.AdminRestService#reindex(org.escidoc.core.domain.taskparam.ReindexTaskParamTO)
     */
    @Override
    public ResultTO reindex(ReindexTaskParamTO taskParam) throws AuthenticationException, AuthorizationException, InvalidXmlException, SystemException {
        return serviceUtility.fromXML(ResultTO.class, this.adminHandler.reindex(serviceUtility.toXML(taskParam)));
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.adm.AdminRestService#decreaseReindexStatus(java.util.String)
     */
    @Override
    public void decreaseReindexStatus(String objectType) throws AuthenticationException, AuthorizationException, InvalidXmlException, SystemException {
        this.adminHandler.decreaseReindexStatus(objectType);
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.adm.AdminRestService#getRepositoryInfo()
     */
    @Override
    public JavaUtilPropertiesTO getRepositoryInfo() throws AuthenticationException, AuthorizationException, SystemException {
        String xml = this.adminHandler.getRepositoryInfo();
        xml = xml.replaceFirst("<\\!.*?>", "");
        xml = xml.replaceFirst("(<[^\\?\\!]*?)([\\/\\s>])", "$1 xmlns=\"http://java.sun.com/dtd/properties.dtd\"$2");
        return serviceUtility.fromXML(JavaUtilPropertiesTO.class, xml);
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.adm.AdminRestService#getIndexConfiguration()
     */
    @Override
    public IndexConfigurationTO getIndexConfiguration() throws AuthenticationException, AuthorizationException, SystemException {
        return serviceUtility.fromXML(IndexConfigurationTO.class, this.adminHandler.getIndexConfiguration());
    }

    /* (non-Javadoc)
     * @see de.escidoc.core.adm.AdminRestService#loadExamples(java.util.String)
     */
    @Override
    public ResultTO loadExamples(@PathParam("type") String type) throws AuthenticationException, AuthorizationException, InvalidSearchQueryException, SystemException {
        return serviceUtility.fromXML(ResultTO.class, this.adminHandler.loadExamples(type));
    }

}
