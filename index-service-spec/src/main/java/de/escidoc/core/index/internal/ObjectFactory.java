package de.escidoc.core.index.internal;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This class contains factory methods for each Java content interface and Java element interface generated in the
 * de.escidoc.core.index.internal package. <p>An ObjectFactory allows you to programatically construct new instances of
 * the Java representation for XML content. The Java representation of XML content can consist of schema derived
 * interfaces and classes representing the binding of schema type definitions, element declarations and model groups.
 * Factory methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Creates a new instance of {@link IndexRequest}.
     *
     * @return a new instance of {@link IndexRequest}
     */
    public IndexRequestImpl createIndexRequest() {
        return new IndexRequestImpl();
    }

}
