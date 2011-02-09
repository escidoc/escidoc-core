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
package de.escidoc.core.common.axis;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.EJBProvider;

import de.escidoc.core.common.util.string.StringUtility;

/**
 * EJBProvider implementation that extends
 * org.apache.axis.providers.java.EJBProvider to forward the security context
 * from the SOAP data to the EJB.<br>
 * This implementation overrides the makeNewServiceObject method.
 * 
 * @author TTE
 * @common
 */
public class EscidocEjbProvider extends EscidocSpringProvider {

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.<br>
     * 
     * For providing "compatible" wsdl to previous implementation, the following
     * is initialized:
     * <ul>
     * <li>the default name space of the service description is initialized
     * using the value of option {@link EJBProvider.OPTION_REMOTEINTERFACENAME}.</li>
     * </ul>
     * Additionally, the following option should be defined properly in
     * deployment descriptor:
     * <ul>
     * <li>OPTION_WSDL_PORTTYPE</li>
     * <li>OPTION_WSDL_TARGETNAMESPACE</li>
     * <li>OPTION_WSDL_SERVICEELEMENT</li>
     * <li>OPTION_WSDL_SERVICEPORT</li>
     * </ul>
     * 
     * @param service
     * @param messageContext
     * @throws AxisFault
     * @see org.apache.axis.providers.java.JavaProvider
     *      #initServiceDesc(org.apache.axis.handlers.soap.SOAPService,
     *      org.apache.axis.MessageContext)
     * @common
     */
    @Override
    public void initServiceDesc(
        final SOAPService service, final MessageContext messageContext)
        throws AxisFault {

    	TypeMappingImpl.dotnet_soapenc_bugfix = true;
    	final ServiceDesc serviceDescription = service.getServiceDescription();
        final String targetnamespace =
            (String) service.getOption(OPTION_WSDL_TARGETNAMESPACE);
        if (targetnamespace == null) {
            throw new AxisFault(StringUtility.format(
                MISSING_MANDATORY_PARAMETER, OPTION_WSDL_TARGETNAMESPACE));
        }
        serviceDescription.setDefaultNamespace(targetnamespace);

        super.initServiceDesc(service, messageContext);
    }

    // CHECKSTYLE:JAVADOC-ON

}
