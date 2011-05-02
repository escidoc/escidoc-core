package de.escidoc.core.index;

import de.escidoc.core.index.internal.IndexRequestBuilderImpl;

/**
 * Builder for {@link IndexRequest}.
 */
public abstract class IndexRequestBuilder {

    /**
     * Create a new {@link IndexRequest}.
     *
     * @return a new IndexRequestBuilder instance.
     */
    public static IndexRequestBuilder createIndexRequest() {
        return new IndexRequestBuilderImpl();
    }

    /**
     * Create a new {@link IndexRequest} with the givin action.
     *
     * @param action the indexing action
     * @return a the current IndexRequestBuilder instance.
     */
    public abstract IndexRequestBuilder withAction(String action);

    /**
     * Create a new {@link IndexRequest} with the given index name.
     *
     * @param indexName the index name
     * @return a the current IndexRequestBuilder instance.
     */
    public abstract IndexRequestBuilder withIndexName(String indexName);

    /**
     * Create a new {@link IndexRequest} with the given resource name.
     *
     * @param resource the resource name
     * @return a the current IndexRequestBuilder instance.
     */
    public abstract IndexRequestBuilder withResource(String resource);

    /**
     * Create a new {@link IndexRequest} with the given object type.
     *
     * @param objectType the object type
     * @return a the current IndexRequestBuilder instance.
     */
    public abstract IndexRequestBuilder withObjectType(String objectType);

    /**
     * Create a new {@link IndexRequest} with the given data.
     *
     * @param data the data to index
     * @return a the current IndexRequestBuilder instance.
     */
    public abstract IndexRequestBuilder withData(String data);

    /**
     * Create a new {@link IndexRequest} with the given isReindexerCaller-property.
     *
     * @param isReindexerCaller the isReindexerCaller-property
     * @return a the current IndexRequestBuilder instance.
     */
    public abstract IndexRequestBuilder withIsReindexerCaller(boolean isReindexerCaller);

    /**
     * Builds a new {@link IndexRequest} instance.
     *
     * @return a new {@link IndexRequest} instance
     */
    public abstract IndexRequest build();

}
