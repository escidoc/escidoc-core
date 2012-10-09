package org.escidoc.core.persistence.impl.fedora.deserializer.foxml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.http.auth.AuthenticationException;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.ItemRelsExtHandler;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.RelsExtHandler;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.RelsExtHandler.RelsExtValues;
import org.escidoc.core.persistence.impl.fedora.util.HttpUtil;



import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.util.stax.StaxParser;

public class AbstractFoxmlDeserializer {
    private final HttpUtil fedoraClient;

    protected final String fedoraUrl;

    protected AbstractFoxmlDeserializer(final HttpUtil fedoraClient, final String fedoraUrl) {
        this.fedoraClient = fedoraClient;
        this.fedoraUrl = fedoraUrl;
    }

    protected InputStream getDatastream(
        final String id, final String datastreamId, final String date)
        throws IOException, AuthenticationException {
        String url =
            fedoraUrl + "objects/" + id + "/datastreams/" + datastreamId
                + "/content";

        if (date != null) {
            url += "?asOfDateTime=" + date;
        }
        return fedoraClient.getAsStream(url);
    }

    protected byte[] getFoxml(final String id) throws IOException,
        AuthenticationException {
        return fedoraClient.getAsByteArray(fedoraUrl + "objects/" + id
            + "/objectXML");
    }

    protected InputStream getFoxmlAsStream(final String id) throws IOException,
        AuthenticationException {
        return fedoraClient.getAsStream(fedoraUrl + "objects/" + id
            + "/objectXML");
    }

    protected ResourceType getResourceType(final String id, final String date)
        throws XMLStreamException, EscidocException, AuthenticationException {
        ResourceType result = null;

        try {
            // get resource type from RELS-EXT
            StaxParser sp = new StaxParser();
            RelsExtHandler relsExtHandler = new ItemRelsExtHandler(sp, date);

            sp.addHandler(relsExtHandler);
            sp.parse(getFoxmlAsStream(id));

            RelsExtValues relsExtValues = relsExtHandler.getValues();

            result =
                ResourceType.getResourceTypeFromUri(relsExtValues
                    .getFirst("rdf:type"));
        }
        catch (IOException e) {
        }
        return result;
    }
}
