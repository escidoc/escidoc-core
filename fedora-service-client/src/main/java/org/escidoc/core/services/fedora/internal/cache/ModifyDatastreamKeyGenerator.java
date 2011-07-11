package org.escidoc.core.services.fedora.internal.cache;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import org.aopalliance.intercept.MethodInvocation;
import org.escidoc.core.services.fedora.ModifiyDatastreamPathParam;

/**
 * {@link CacheKeyGenerator} for modifyDatastream-Operation in {@link org.escidoc.core.services.fedora.FedoraServiceClient}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class ModifyDatastreamKeyGenerator implements CacheKeyGenerator<String> {

    @Override
    public String generateKey(final MethodInvocation methodInvocation) {
        return this.generateKey(methodInvocation.getArguments());
    }

    @Override
    public String generateKey(final Object... objects) {
        if(objects.length > 0) {
            if(objects[0] instanceof ModifiyDatastreamPathParam) {
                final ModifiyDatastreamPathParam modifiyDatastreamPathParam = (ModifiyDatastreamPathParam) objects[0];
                return modifiyDatastreamPathParam.getPid();
            }
        }
        return null;
    }
}