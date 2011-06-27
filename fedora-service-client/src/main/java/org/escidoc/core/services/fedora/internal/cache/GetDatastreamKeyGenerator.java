package org.escidoc.core.services.fedora.internal.cache;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import org.aopalliance.intercept.MethodInvocation;
import org.joda.time.DateTime;

/**
 * {@link CacheKeyGenerator} for getDatastream-Operation in {@link org.escidoc.core.services.fedora.FedoraServiceClient}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class GetDatastreamKeyGenerator implements CacheKeyGenerator<DatastreamCacheKey> {

    @Override
    public DatastreamCacheKey generateKey(final MethodInvocation methodInvocation) {
        return this.generateKey(methodInvocation.getArguments());
    }

    @Override
    public DatastreamCacheKey generateKey(final Object... objects) {
        if(objects.length > 2) {
            if(objects[0] instanceof String && objects[1] instanceof String) {
                final String pid = (String) objects[0];
                final String dsId = (String) objects[1];
                DateTime timestamp = null;
                if(objects[2] != null && objects[2] instanceof DateTime) {
                    timestamp = (DateTime) objects[2];
                }
                return new DatastreamCacheKey(pid, dsId, timestamp);
            }
        }
        return null;
    }
}
