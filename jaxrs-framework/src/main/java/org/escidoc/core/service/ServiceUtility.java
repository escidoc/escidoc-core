/**
 * 
 */
package org.escidoc.core.service;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.esidoc.core.utils.io.Stream;

import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Marko Voß
 * 
 */
public class ServiceUtility {

    private ServiceUtility() {

    }

    // Note: This code is slow and only for migration!
    // TODO: Replace this code and use domain objects!
    public static final String toXML(final Object objectTO) throws SystemException {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(objectTO.getClass());
            final Marshaller marshaller = jaxbContext.createMarshaller();
            final Stream stream = new Stream();
            marshaller.marshal(objectTO, stream);
            stream.lock();
            stream.writeCacheTo(stringBuilder);
        }
        catch (final Exception e) {
            throw new SystemException("Error on marshalling object: " + objectTO.getClass().getName(), e);
        }
        return stringBuilder.toString();
    }

    // Note: This code is slow and only for migration!
    // TODO: Replace this code and use domain objects!
    public static final <T> T fromXML(final Class<T> classTO, final String xmlString) throws SystemException {

        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(classTO);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final Source src = new StreamSource(new StringReader(xmlString));
            return unmarshaller.unmarshal(src, classTO).getValue();
        }
        catch (final Exception e) {
            throw new SystemException("Error on unmarshalling XML.", e);
        }
    }
}