package org.escidoc.core.utils.xml;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.escidoc.core.utils.io.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.IOException;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class StreamHandler implements DomHandler<StreamElement, StreamResult> {

    private static final Logger LOG = LoggerFactory.getLogger(StreamHandler.class);
    private String systemId;

    @Override
    public StreamResult createUnmarshaller(ValidationEventHandler errorHandler) {
        return new StreamResult(new Stream());
    }

    @Override
    public StreamElement getElement(StreamResult rt) {
        Stream stream = (Stream)rt.getOutputStream();
        this.systemId = rt.getSystemId();
        try {
            stream.lock();
        } catch (IOException e) {
            LOG.warn("Unable to lock stream: " + e.getMessage(), e);
        }
        return new StreamElement(stream);
    }

    @Override
    public Source marshal(StreamElement streamElement, ValidationEventHandler errorHandler) {
        try {
            InputSource in = new InputSource(streamElement.getStream().getInputStream());
            in.setSystemId(this.systemId);
            return new SAXSource(in);
        } catch (IOException e) {
            LOG.warn("Unable to get InputStream: " + e.getMessage(), e);
        }
        return null; // TODO: maybe empty source?
    }
}