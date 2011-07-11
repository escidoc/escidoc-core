package org.escidoc.core.services.fedora.internal.cache;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import org.aopalliance.intercept.MethodInvocation;
import org.joda.time.DateTime;

/**
 * {@link CacheKeyGenerator} for listDatastreams-Operation in {@link org.escidoc.core.services.fedora.FedoraServiceClient}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class ListDatastreamsKeyGenerator implements CacheKeyGenerator<DatastreamCacheKey> {

    @Override
    public DatastreamCacheKey generateKey(final MethodInvocation methodInvocation) {
        return this.generateKey(methodInvocation.getArguments());
    }

    @Override
    public DatastreamCacheKey generateKey(final Object... objects) {
        if(objects.length > 1) {
            if(objects[0] instanceof String && objects[1] instanceof DateTime) {
                final String pid = (String) objects[0];
                final DateTime timestamp = (DateTime) objects[1];
                return new DatastreamCacheKey(pid, null, timestamp);
            }
        }
        return null;
    }

}
