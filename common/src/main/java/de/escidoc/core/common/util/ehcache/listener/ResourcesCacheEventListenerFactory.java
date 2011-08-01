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
import java.util.regex.Pattern;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;

/**
 * @author Michael Hoppe
 *
 */
public class ResourcesCacheEventListenerFactory extends CacheEventListenerFactory {

    public CacheEventListener createCacheEventListener(Properties properties) {
        return new CacheEventListener() {

            @Override
            public Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
                String searchKey =
                    ((String) element.getObjectKey()).substring(0, ((String) element.getObjectKey()).length() - 1);

                @SuppressWarnings("unchecked")
                Results results =
                    cache
                        .createQuery().addCriteria(Query.KEY.between(searchKey + " ", searchKey + "z")).includeKeys()
                        .execute();
                if (results != null && results.size() > 0) {
                    Pattern searchPattern = Pattern.compile(Pattern.quote(searchKey) + "[0-9].*");
                    for (Result result : results.all()) {
                        if (!searchPattern.matcher((String) result.getKey()).matches()) {
                            cache.removeQuiet(result.getKey());
                        }
                    }
                }
            }

            @Override
            public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
            }

            @Override
            public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
            }

            @Override
            public void notifyElementExpired(Ehcache cache, Element element) {
            }

            @Override
            public void notifyElementEvicted(Ehcache cache, Element element) {
            }

            @Override
            public void notifyRemoveAll(Ehcache cache) {
            }

            @Override
            public void dispose() {
            }
        };
    }

}
