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
package de.escidoc.core.common.util.ehcache.listener;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

/**
 * @author Michael Hoppe
 *
 */
public class ResourcesCacheEventListenerFactory extends CacheEventListenerFactory {

    public CacheEventListener createCacheEventListener(Properties properties) {
        return new CacheEventListener() {

            /**
             * The logger.
             */
            private final Logger log = LoggerFactory.getLogger(ResourcesCacheEventListenerFactory.class);

            @Override
            public Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
                log.info("Element removed from the cache : {}", element.getObjectKey());
            }

            @Override
            public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
                log.info("Element put into the cache : {}", element.getObjectKey());
            }

            @Override
            public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
                log.info("Element updated in the cache : {}", element.getObjectKey());
            }

            @Override
            public void notifyElementExpired(Ehcache cache, Element element) {
                log.info("Element expired in the cache : {}", element.getObjectKey());
            }

            @Override
            public void notifyElementEvicted(Ehcache cache, Element element) {
                log.info("Element evicted from the cache : {}", element.getObjectKey());
            }

            @Override
            public void notifyRemoveAll(Ehcache cache) {
                log.info("Remove all elements from the cache");
            }

            @Override
            public void dispose() {
                log.info("Dispose the listener");
            }
        };
    }

}
