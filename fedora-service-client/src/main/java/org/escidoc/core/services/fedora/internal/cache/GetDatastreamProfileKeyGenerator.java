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

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import org.aopalliance.intercept.MethodInvocation;
import org.escidoc.core.services.fedora.GetDatastreamProfilePathParam;
import org.escidoc.core.services.fedora.GetDatastreamProfileQueryParam;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * {@link CacheKeyGenerator} for getDatastreamProfile-Operation in {@link org.escidoc.core.services.fedora
 * .FedoraServiceClient}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class GetDatastreamProfileKeyGenerator implements CacheKeyGenerator<DatastreamCacheKey> {

    @Override
    public DatastreamCacheKey generateKey(final MethodInvocation methodInvocation) {
        return this.generateKey(methodInvocation.getArguments());
    }

    @Override
    public DatastreamCacheKey generateKey(final Object... objects) {
        if(objects.length > 1) {
            if(objects[0] instanceof GetDatastreamProfilePathParam &&
                    objects[1] instanceof GetDatastreamProfileQueryParam) {
                final GetDatastreamProfilePathParam param = (GetDatastreamProfilePathParam) objects[0];
                final GetDatastreamProfileQueryParam query = (GetDatastreamProfileQueryParam) objects[1];
                DateTime timestamp = null;
                if(query.getAsOfDateTime() != null) {
                    timestamp = new DateTime(query.getAsOfDateTime(), DateTimeZone.UTC);
                }
                return new DatastreamCacheKey(param.getPid(), param.getDsID(), timestamp);
            }
        }
        return null;
    }

}
