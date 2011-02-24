package de.escidoc.core.purge.internal;

import de.escidoc.core.purge.PurgeRequest;
import de.escidoc.core.purge.PurgeRequestBuilder;

/**
 * Default implementation for {@link PurgeRequestBuilder}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class PurgeRequestBuilderImpl extends PurgeRequestBuilder {

    private PurgeRequestImpl purgeRequest = new PurgeRequestImpl();

    /**
     * {@inheritDoc}
     */
    @Override
    public PurgeRequestBuilder withResourceId(final String resourceId) {
        this.purgeRequest.setResourceId(resourceId);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PurgeRequest build() {
        final PurgeRequest returnValue = this.purgeRequest;
        this.purgeRequest = new PurgeRequestImpl();
        return returnValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return "PurgeRequestBuilderImpl{" +
                "purgeRequest=" + purgeRequest +
                '}';
    }
}
