/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package org.escidoc.core.services.fedora.internal.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Configurable
public final class FedoraServiceClientCacheEventListener implements CacheEventListener, Cloneable {

    private static final String[] CACHE_NAMES =
            new String[]{"Fedora.Datastreams", "Fedora.DatastreamLists", "Fedora.DatastreamProfiles",
                    "Fedora.DatastreamHistories", "Fedora.DatastreamBinaryContent"};

    private CacheManager getCacheManager() {
        // Return the default cache manager.
        // It should be the first cache manager in the list.
        return CacheManager.ALL_CACHE_MANAGERS.get(0);
    }

    @Override
    public void notifyElementRemoved(final Ehcache cache, final Element element) {
        final String pid = (String) element.getKey();
        for(final String cacheName : CACHE_NAMES) {
            final Cache datastreamsCache = getCacheManager().getCache(cacheName);
            if(datastreamsCache != null) {
                final List datastreamCacheKeys = datastreamsCache.getKeys();
                for(final Object datastreamCacheKeyObject : datastreamCacheKeys) {
                    final DatastreamCacheKey datastreamCacheKey = (DatastreamCacheKey) datastreamCacheKeyObject;
                    if(pid.equals(datastreamCacheKey.getPid())) {
                        datastreamsCache.remove(datastreamCacheKey);
                    }
                }
            }
        }
    }

    @Override
    public void notifyElementPut(final Ehcache cache, final Element element) {
        // do nothing
    }

    @Override
    public void notifyElementUpdated(final Ehcache cache, final Element element) {
        // do nothing
    }

    @Override
    public void notifyElementExpired(final Ehcache cache, final Element element) {
        // do nothing
    }

    @Override
    public void notifyElementEvicted(final Ehcache cache, final Element element) {
        // do nothing
    }

    @Override
    public void notifyRemoveAll(final Ehcache cache) {
        for(final String cacheName : CACHE_NAMES) {
            final Cache datastreamsCache = getCacheManager().getCache(cacheName);
            if(datastreamsCache != null) {
                datastreamsCache.removeAll();
            }
        }
    }

    @Override
    public void dispose() {
        // do nothing
    }

    @Override
    public Object clone() {
        return this;
    }

}
