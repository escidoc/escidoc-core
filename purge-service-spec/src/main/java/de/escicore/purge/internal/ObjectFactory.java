package de.escicore.purge.internal;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This class contains factory methods for each Java content interface and Java element interface generated in the
 * de.escicore.purge.internal package.
 * <p>An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@XmlRegistry
public class ObjectFactory  {

    /**
     * Creates a new instance of {@link de.escicore.purge.PurgeRequest}.
     *
     * @return a new instance of {@link de.escicore.purge.PurgeRequest}
     */
    public PurgeRequestImpl createIndexRequest() {
        return new PurgeRequestImpl();
    }

}
