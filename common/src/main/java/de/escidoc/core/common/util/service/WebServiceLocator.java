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
package de.escidoc.core.common.util.service;

import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import java.rmi.Remote;
import java.util.List;

/**
 * WebServiceLocator.java is used to connect to WebSevices using
 * service-endpoint classes.
 * 
 * @author Bernhard Kraus (Accenture)
 */
public class WebServiceLocator extends Service {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 7521229680302906384L;

    private String serviceUrl = "http://localhost:8080/axis/services/";

    private String serviceName = "Service";

    private String serviceAddress = null;

    private final Class serviceInterface;

    /**
     * The standard constructor with class parameter.
     * 
     * @param serviceInterface
     *            The Webservice interface class
     * @param serviceName
     *            The name of the service
     * @param serviceUrl
     *            The URL to the service as String
     * @param config
     *            The wss4j security configuration
     */
    public WebServiceLocator(final Class serviceInterface,
        final String serviceName, final String serviceUrl,
        final EngineConfiguration config) {

        // Enable configuration setting for WSS4J
        super(config);
        this.serviceInterface = serviceInterface;
        this.serviceName = serviceName;
        this.serviceUrl = serviceUrl;
        serviceAddress = serviceUrl + serviceName;
    }

    /**
     * The constructor with class parameter. takes serviceUrl from config
     * 
     * @param serviceInterface
     *            The Webservice interface class
     * @param serviceName
     *            The name of the service
     * @param config
     *            The wss4j security configuration
     * @throws ConfigurationException
     *             e
     */
    public WebServiceLocator(final Class serviceInterface,
        final String serviceName, final EngineConfiguration config)
        throws ConfigurationException {

        // Enable configuration setting for WSS4J
        super(config);
        this.serviceInterface = serviceInterface;
        this.serviceName = serviceName;
        this.serviceUrl =
            (String) config.getService(new QName(serviceName)).getOption(
                "serviceUrl");
        serviceAddress = serviceUrl + serviceName;
    }

    /**
     * Get the service address.
     * 
     * @return String Returns the address of the service
     */
    public String getServiceAddress() {
        return serviceAddress;
    }

    /**
     * Get the WSDD Service name.
     * 
     * @return String the WSDD service name
     */
    public String getServiceWSDDServiceName() {
        return serviceName;
    }

    /**
     * Set the WSDD Service name.
     * 
     * @param name
     *            The WSDD name
     */
    public void setServiceWSDDServiceName(final String name) {
        serviceName = name;
        serviceAddress = serviceUrl + serviceName;
    }

    /**
     * Get the Service.
     * 
     * @return Remote The webservice Remote client
     * @throws ServiceException
     *             Thrown if service is not available
     */
    public Remote getService() throws ServiceException {

        final Call call = (Call) this.createCall();
        final Remote stub =
            call.getService().getPort(serviceAddress, serviceInterface);

        try {
            final Stub msg = (Stub) stub;
            // WSS4J security (technical user for the webservice)
            msg._setProperty(WSHandlerConstants.USER, "NotProvided");
            msg._setProperty(WSConstants.PASSWORD_LN, "");
            // JAXRPC security (The user)
            /*
             * msg._setProperty(Stub.USERNAME_PROPERTY, "fedoraAdmin");
             * msg._setProperty(Stub.PASSWORD_PROPERTY, "fedoraAdmin");
             */
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stub;
    }

    /**
     * Get the Service.
     * 
     * @param username
     *            The username
     * @param password
     *            The password
     * @return Remote The webservice Remote client
     * @throws ServiceException
     *             Thrown if service is not available
     */
    public Remote getService(final String username, final String password)
        throws ServiceException {

        final Call call = (Call) this.createCall();
        final Remote stub =
            call.getService().getPort(serviceAddress, serviceInterface);

        try {
            final Stub msg = (Stub) stub;
            msg._setProperty(WSHandlerConstants.USER, username);
            msg._setProperty(WSConstants.PASSWORD_LN, password);
            /*
             * // WSS4J security (technical user for the webservice)
             * msg._setProperty(WSHandlerConstants.USER, "wss4j");
             * msg._setProperty(WSConstants.PASSWORD_LN, "Password"); // JAXRPC
             * security (The user) msg._setProperty(Stub.USERNAME_PROPERTY,
             * username); msg._setProperty(Stub.PASSWORD_PROPERTY, password);
             */
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stub;
    }

//    /**
//     * Get the Service with one type mapping.
//     * 
//     * @param namespace
//     *            String of the type mapping
//     * @param mapping
//     *            Class of the type mapping
//     * @return Remote The webservice Remote client
//     * @throws ServiceException
//     *             Thrown if service is not available
//     */
    /*
     * public Remote getService(final String namespace, final Class mapping)
     * throws ServiceException {
     * 
     * Call call = (Call) this.createCall();
     * call.setProperty(UsernameToken.PASSWORD_TYPE,
     * WSConstants.PASSWORD_DIGEST);
     * 
     * PWCallback pwCallback = new PWCallback();
     * call.setProperty(WSHandlerConstants.USER, "wss4j");
     * call.setProperty(WSHandlerConstants.PW_CALLBACK_REF, pwCallback); QName
     * poqn = new QName(namespace, mapping.getSimpleName());
     * 
     * call.registerTypeMapping(mapping, poqn, new
     * BeanSerializerFactory(mapping, poqn), new
     * BeanDeserializerFactory(mapping, poqn)); Remote stub =
     * call.getService().getPort(serviceAddress, serviceInterface);
     * 
     * return stub; }
     */

    /**
     * Get the Service with one type mapping.
     * 
     * @param mappings
     *            Hashtable of the type mappings as namespace/class pair
     * @return Remote The webservice Remote client
     * @throws ServiceException
     *             Thrown if service is not available
     */
    // TODO: remove this method later. only use getService(mappings, handle)
    public final Remote getService(final Iterable<BeanMapping> mappings) throws ServiceException {
        final Call call = (Call) this.createCall();
        for (final BeanMapping mapping : mappings) {
            // Class mapping = (Class) mappings.get(namespace);
            final QName poqn =
                    new QName(mapping.getNamespaceUri(), mapping.getNamespace());
            call.registerTypeMapping(mapping.getBean(), poqn,
                    new BeanSerializerFactory(mapping.getBean(), poqn),
                    new BeanDeserializerFactory(mapping.getBean(), poqn));
        }
        final Remote stub =
            call.getService().getPort(serviceAddress, serviceInterface);

        try {
            final Stub msg = (Stub) stub;
            msg._setProperty(WSHandlerConstants.USER, "NotProvided");
            msg._setProperty(WSConstants.PASSWORD_LN, "");
            /*
             * // WSS4J security (technical user for the webservice)
             * msg._setProperty(WSHandlerConstants.USER, "wss4j");
             * msg._setProperty(WSConstants.PASSWORD_LN, "Password"); // JAXRPC
             * security (The user) msg._setProperty(Stub.USERNAME_PROPERTY,
             * "NotProvided"); msg._setProperty(Stub.PASSWORD_PROPERTY, "");
             */
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stub;

    }

    /**
     * Get the Service with a Vector of type mappings and a security handle.
     * 
     * @param mappings
     *            Vector of the type mappings
     * @param handle
     *            The security handle to use (will be set as password, username
     *            set to "ShibbolethUser")
     * @return Remote The webservice Remote client
     * @throws ServiceException
     *             Thrown if service is not available
     */
    public Remote getService(final List<BeanMapping> mappings, final String handle)
        throws ServiceException {

        // get the stub
        final Remote stub = getService(mappings);

        // reset the password
        try {
            final Stub msg = (Stub) stub;
            msg._setProperty(WSHandlerConstants.USER, "ShibbolethUser");
            msg._setProperty(WSConstants.PASSWORD_LN, handle);

            // WSS4J security (technical user for the webservice)
            // msg._setProperty(WSHandlerConstants.USER, "wss4j");
            // msg._setProperty(WSConstants.PASSWORD_LN, "Password");
            // JAXRPC security (The user)
            // msg._setProperty(Stub.USERNAME_PROPERTY, "ShibbolethUser");
            // msg._setProperty(Stub.PASSWORD_PROPERTY, handle);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stub;

    }

    /**
     * Sets the endpoint address.
     * 
     * @param address
     *            String for the endpoint address
     */
    public void setServiceEndpointAddress(final String address) {
        serviceAddress = address;
    }

//    /**
//     * For the given interface, get the stub implementation. If this service has
//     * no port for the given interface, then ServiceException is thrown.
//     * 
//     * @param portName
//     *            The Axis QName object
//     * @param serviceEndpointInterface
//     *            The endpoint class
//     * @return Remote the Webservice
//     * @throws ServiceException
//     *             Thrown if Webservice is not available
//     */
    /*
     * public Remote getPort(final QName portName, final Class
     * serviceEndpointInterface) throws ServiceException {
     * 
     * if (portName == null) { return getPort(serviceEndpointInterface); }
     * String inputPortName = portName.getLocalPart(); if
     * (serviceName.equals(inputPortName)) { return getService(); } else {
     * Remote stub = getPort(serviceEndpointInterface); ((Stub)
     * stub).setPortName(portName); return stub; } }
     */

    /**
     * Builds the QName objetc out of the parameters.
     * 
     * @return QName Returns the Apache Axis QName object
     */
    @Override
    public final QName getServiceName() {
        return new QName(serviceAddress, serviceName);
    }

}
