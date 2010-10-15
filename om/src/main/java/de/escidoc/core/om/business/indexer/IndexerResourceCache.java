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
package de.escidoc.core.om.business.indexer;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.fedora.MIMETypedStream;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.servlet.invocation.BeanMethod;
import de.escidoc.core.common.servlet.invocation.MethodMapper;
import de.escidoc.core.common.servlet.invocation.exceptions.MethodNotFoundException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.ConnectionUtility;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.FoXmlProvider;
import de.escidoc.core.om.business.fedora.deviation.Constants;

/**
 * @author mih
 * 
 *         Singleton for caching Resources (items, container, fulltexts) For
 *         indexing by fedoragsearch
 * 
 * @om
 */
public final class IndexerResourceCache {

    private static IndexerResourceCache instance = null;

    /**
     * Fall back value if reading property
     * {@link <code>EscidocConfiguration.INDEXER_CACHE_SIZE</code>} fails.
     * 
     * @om
     */
    private static final int INDEXER_CACHE_SIZE_FALL_BACK = 30;

    private static int indexerCacheSize;

    private static final int BUFFER_SIZE = 0xFFFF;

    /** Holds identifier and object. */
    private Map<String, Object> resources = null;

    private MethodMapper methodMapper;

    private TripleStoreUtility tripleStoreUtility;

    private ConnectionUtility connectionUtility;

    private static AppLogger log =
        new AppLogger(IndexerResourceCache.class.getName());

    /**
     * private Constructor for Singleton.
     * 
     * @OM
     */
    private IndexerResourceCache() {
        try {
            methodMapper =
                (MethodMapper) BeanLocator.getBean("Common.spring.ejb.context",
                    "common.CommonMethodMapper");
            connectionUtility =
                (ConnectionUtility) BeanLocator.getBean(
                    "Common.spring.ejb.context",
                    "escidoc.core.common.util.service.ConnectionUtility");
            tripleStoreUtility = TripleStoreUtility.getInstance();

            //initialize map that holds cached Objects for indexing
            try {
                indexerCacheSize =
                    Integer.parseInt(EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.ESCIDOC_CORE_INDEXER_CACHE_SIZE));
            }
            catch (Exception e) {
                indexerCacheSize = INDEXER_CACHE_SIZE_FALL_BACK;
            }
            resources = new LRUMap(indexerCacheSize);
            
        }
        catch (Exception e) {
            log.debug(e);
        }

    }

    /**
     * Only initialize Object once. Check for old objects in cache.
     * 
     * @return IndexerResourceCache IndexerResourceCache
     * 
     * @om
     */
    public static synchronized IndexerResourceCache getInstance() {
        if (instance == null) {
            instance = new IndexerResourceCache();
        }
        return instance;
    }

    /**
     * Get resource with given identifier.
     * 
     * @param identifier
     *            identifier
     * @return Object resource-object
     * @throws SystemException
     *             e
     * 
     * @om
     */
    public Object getResource(final String identifier) throws SystemException {
        String href = getHref(identifier);
        if (getResourceWithInternalKey(href) == null) {
            if (identifier.startsWith("http")) {
                cacheExternalResource(href);
            }
            else {
                cacheInternalResource(href);
            }
        }
        return getResourceWithInternalKey(href);
    }

    /**
     * Set resource with given identifier.
     * 
     * @param identifier
     *            identifier
     * @param resource
     *            resource-object
     * @throws SystemException
     *             e
     * @om
     */
    public void setResource(final String identifier, final Object resource)
        throws SystemException {
        String href = getHref(identifier);
        synchronized (resources) {
            resources.put(href, resource);
        }
    }

    /**
     * Get resource with given identifier.
     * 
     * @param identifier
     *            identifier
     * @throws SystemException
     *             e
     * @return Resource with required identifier.
     * @om
     */
    private Object getResourceWithInternalKey(final String identifier)
        throws SystemException {

        return resources.get(identifier);
    }

    /**
     * delete resource with given identifier.
     * 
     * @param identifier
     *            identifier
     * @throws SystemException
     *             e
     * @om
     */
    public void deleteResource(final String identifier) throws SystemException {
        String href = getHref(identifier);
        synchronized (resources) {
            Collection<String> keys = new ArrayList<String>();
            for (String key : resources.keySet()) {
                if (key.startsWith(href)) {
                    keys.add(key);
                }
            }
            for (String key : keys) {
                resources.remove(key);
            }
        }
    }

    /**
     * delete resource with given identifier.
     * 
     * @param identifier
     *            identifier
     * @throws SystemException
     *             e
     * @om
     */
    public void replaceResource(
        final String identifier, 
        final Object resource) throws SystemException {
        String href = getHref(identifier);
        synchronized (resources) {
            Collection<String> keys = new ArrayList<String>();
            for (String key : resources.keySet()) {
                if (key.startsWith(href)) {
                    keys.add(key);
                }
            }
            for (String key : keys) {
                resources.remove(key);
            }
            resources.put(href, resource);
        }
    }

    /**
     * delete resource with given identifier.
     * 
     * @param resourceKey
     *            resourceKey
     * @om
     */
    private void deleteResourceWithInternalKey(final String resourceKey) {
        synchronized (resources) {
            resources.remove(resourceKey);
        }
    }

    /**
     * get resource with given identifier from framework and write it into
     * cache.
     * 
     * @param identifier
     *            identifier
     * @throws SystemException
     *             e
     * @om
     */
    private synchronized void cacheInternalResource(final String identifier)
        throws SystemException {
        try {
            BeanMethod method =
                methodMapper.getMethod(identifier, null, null, "GET", "");
            Object content =
                method.invokeWithProtocol(UserContext.getHandle(),
                    Constants.USE_REST_REQUEST_PROTOCOL);
            if (content != null
                && content.getClass().getSimpleName().equals(
                    "EscidocBinaryContent")) {
                EscidocBinaryContent escidocBinaryContent =
                    (EscidocBinaryContent) content;
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = escidocBinaryContent.getContent();
                if (in != null) {
                    try {
                        byte[] bytes = new byte[BUFFER_SIZE];
                        int i = -1;
                        while ((i = in.read(bytes)) > -1) {
                            out.write(bytes, 0, i);
                        }
                        out.flush();
                        MIMETypedStream stream =
                            new MIMETypedStream(escidocBinaryContent
                                .getMimeType(), out.toByteArray(), null);
                        setResource(identifier, stream);
                    }
                    catch (Exception e) {
                        log.error(e.toString());
                        throw new SystemException(e);
                    }
                    finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (Exception e) {}
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (Exception e) {}
                        }
                    }
                }
            }
            else if (content != null) {
                String xml = (String) content;
                setResource(identifier, xml);
            }
        }
        catch (InvocationTargetException e) {
            log.error(e);
            if (!e.getTargetException().getClass().getSimpleName().equals(
                "AuthorizationException")
                && !e.getTargetException().getClass().getSimpleName().equals(
                        "InvalidStatusException")) {
                throw new SystemException(e);
            }
        }
        catch (MethodNotFoundException e) {
            log.error(e);
        }
        catch (Exception e) {
            log.error(e);
            throw new SystemException(e);
        }
    }

    /**
     * get resource with given URL and write it into cache.
     * 
     * @param identifier
     *            identifier
     * @throws SystemException
     *             e
     * @om
     */
    private synchronized void cacheExternalResource(final String identifier)
        throws SystemException {
        HttpResponse httpResponse = null;
        ByteArrayOutputStream out = null;
        InputStream in = null;
        try {
            httpResponse =
                connectionUtility.getRequestURL(new URL(identifier));

            if (httpResponse != null) {
                String mimeType;

                // TODO testen ob header mitgeschickt wird
                Header ctype = httpResponse.getFirstHeader("Content-Type");
                if (ctype != null) {
                    mimeType = ctype.getValue();
                }
                else {
                    mimeType = FoXmlProvider.MIME_TYPE_APPLICATION_OCTET_STREAM;
                }

                out = new ByteArrayOutputStream();
                in = httpResponse.getEntity().getContent();
                int byteval;
                while ((byteval = in.read()) > -1) {
                    out.write(byteval);
                }
                MIMETypedStream stream =
                    new MIMETypedStream(mimeType, out.toByteArray(), null);
                setResource(identifier, stream);
            }
        }
        catch (Exception e) {
            log.error(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {}
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {}
            }
           
        }
    }

    /**
     * generate href out of pid.
     * 
     * @param identifier
     *            identifier
     * @return String href
     * @throws SystemException
     *             e
     * @om
     */
    private String getHref(final String identifier) throws SystemException {
        String href = identifier;
        if (!href.contains("/")) {
            // objectId provided, generate href
            // get object-type
            href = XmlUtility.getObjidWithoutVersion(href);
            String objectType = tripleStoreUtility.getObjectType(href);

            href = XmlUtility.getHref(objectType, identifier);
        }
        return href;
    }

}
