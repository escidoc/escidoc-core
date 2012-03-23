package org.escidoc.core.util.xml.internal;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.escidoc.core.utils.io.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class StreamHandler implements DomHandler<Stream, StreamResult> {

    private static final Logger LOG = LoggerFactory.getLogger(StreamHandler.class);
    
    @Override 
    public StreamResult createUnmarshaller(ValidationEventHandler errorHandler) {
        return new StreamResult(new Stream());
    }

    @Override 
    public Stream getElement(StreamResult rt) {
        Stream stream = (Stream)rt.getOutputStream();
        try {
            stream.lock();
        } catch (IOException e) {
            LOG.warn("Unable to lock stream: " + e.getMessage(), e);
        }
        return stream;
    }

    @Override 
    public Source marshal(Stream stream, ValidationEventHandler errorHandler) {
        try {
            return new StreamSource(stream.getInputStream());
        } catch (IOException e) {
            LOG.warn("Unable to get InputStream: " + e.getMessage(), e);
        }
        return null; // TODO: maybe empty source?
    }
}
