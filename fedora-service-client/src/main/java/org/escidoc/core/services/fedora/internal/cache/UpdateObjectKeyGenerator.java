package org.escidoc.core.services.fedora.internal.cache;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import org.aopalliance.intercept.MethodInvocation;
import org.escidoc.core.services.fedora.UpdateObjectPathParam;

/**
 * {@link CacheKeyGenerator} for updateObject-Operation in {@link org.escidoc.core.services.fedora.FedoraServiceClient}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class UpdateObjectKeyGenerator implements CacheKeyGenerator<String> {

    @Override
    public String generateKey(final MethodInvocation methodInvocation) {
        return this.generateKey(methodInvocation.getArguments());
    }

    @Override
    public String generateKey(final Object... objects) {
        if(objects.length > 0) {
            if(objects[0] instanceof UpdateObjectPathParam) {
                final UpdateObjectPathParam updateObjectPathParam = (UpdateObjectPathParam) objects[0];
                return updateObjectPathParam.getPid();
            }
        }
        return null;
    }
}
