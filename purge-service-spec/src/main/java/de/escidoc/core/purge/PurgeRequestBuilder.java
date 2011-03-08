package de.escidoc.core.purge;

import de.escidoc.core.purge.internal.PurgeRequestBuilderImpl;

/**
 * Builder for {@link PurgeRequest}.
 */
public abstract class PurgeRequestBuilder {

    /**
     * Create a new {@link PurgeRequest}.
     *
     * @return a new {@link PurgeRequestBuilder} instance.
     */
    public static PurgeRequestBuilder createPurgeRequest() {
        return new PurgeRequestBuilderImpl();
    }

    /**
     * Create a new {@link PurgeRequest} with the resource identifier.
     *
     * @param resourceId the resource identifier
     * @return a the current {@link PurgeRequestBuilder} instance.
     */
    public abstract PurgeRequestBuilder withResourceId(final String resourceId);

    /**
     * Builds a new {@link PurgeRequest} instance.
     *
     * @return a new {@link PurgeRequest} instance
     */
    public abstract PurgeRequest build();

}
