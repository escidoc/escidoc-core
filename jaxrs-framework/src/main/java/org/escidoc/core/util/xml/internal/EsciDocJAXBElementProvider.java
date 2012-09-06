package org.escidoc.core.util.xml.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;

import org.apache.cxf.jaxrs.ext.Nullable;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.cxf.jaxrs.utils.AnnotationUtils;
import org.apache.cxf.jaxrs.utils.InjectionUtils;
import org.apache.cxf.jaxrs.utils.schemas.SchemaHandler;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import org.apache.cxf.staxutils.DepthExceededStaxException;
import org.apache.cxf.staxutils.transform.TransformUtils;
import org.escidoc.core.domain.properties.java.PropertiesTypeTO;

/**
 * eSciDoc specific implementation of {@link JAXBElementProvider}. Generates stylesheet-header with given
 * stylesheet-path
 *
 * @author Michael Hoppe
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Produces({"application/xml", "application/*+xml", "text/xml"})
@Consumes({"application/xml", "application/*+xml", "text/xml"})
@Provider
public class EsciDocJAXBElementProvider extends JAXBElementProvider<Object> {

    private final String XML_HEADERS_PATH = "com.sun.xml.bind.xmlHeaders";

    private final String PROPERTIES_DOCTYPE =
        "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n";

    private JAXBContextProvider jaxbContextProvider;

//    private SchemaHandler schemaHandler;

    private Map<String, Object> maProperties;

    private Map<String, Object> maPropertiesWithDoctype;

    /**
     * Save 2 properties-sets.
     * One without DOCTYPE-element and one With DOCTYPE-element for JavaUtilPropertiesTO.
     */
    @Override
    public void setMarshallerProperties(Map<String, Object> marshallProperties) {
        overrideXmlHeadersProperty(marshallProperties);
        super.setMarshallerProperties(marshallProperties);
        maProperties = marshallProperties;
        maPropertiesWithDoctype = new HashMap<String, Object>(marshallProperties);
        if (maPropertiesWithDoctype.get(XML_HEADERS_PATH) != null) {
            maPropertiesWithDoctype.put(XML_HEADERS_PATH, maPropertiesWithDoctype.get(XML_HEADERS_PATH)
                + PROPERTIES_DOCTYPE);
        }
        else {
            maPropertiesWithDoctype.put(XML_HEADERS_PATH, PROPERTIES_DOCTYPE);
        }
    }

//    @Override
//    public void setSchemaHandler(SchemaHandler schemaHandler) {
//        this.schemaHandler = schemaHandler;
//    }

    @Override
    public Schema getSchema() {
        return super.getSchema();
    }

    public void setJaxbContextProvider(final JAXBContextProvider jaxbContextProvider) {
        this.jaxbContextProvider = jaxbContextProvider;
    }

    public JAXBContext getJAXBContext(Class<?> type, Type genericType)
        throws JAXBException {
        if (this.jaxbContextProvider != null) {
            return this.jaxbContextProvider.getJAXBContext();
        } else {
            return super.getJAXBContext(type, genericType);
        }
    }

//    protected Unmarshaller createUnmarshaller(Class<?> cls, Type genericType, boolean isCollection)
//        throws JAXBException {
//        if (super.getSchema() == null) {
//            super.setSchema(getSchema());
//        }
//        return super.createUnmarshaller(cls, genericType, isCollection);
//    }

    /**
     * set DOCTYPE-element in xml if xml has schema java.util.properties
     *
     */
    @Override
    protected void marshal(
        Object obj, Class<?> cls, Type genericType, String enc, OutputStream os, MediaType mt, Marshaller ms)
        throws Exception {
        if (obj instanceof JAXBElement<?> && ((JAXBElement<?>) obj).getValue() instanceof PropertiesTypeTO) {
            try {
                super.setMarshallerProperties(maPropertiesWithDoctype);
                super.marshal(obj, cls, genericType, enc, os, mt, ms);
            }
            finally {
                super.setMarshallerProperties(maProperties);
            }
        }
        else {
            super.marshal(obj, cls, genericType, enc, os, mt, ms);
        }
    }

//    protected void validateObjectIfNeeded(Marshaller marshaller, Object obj)
//        throws JAXBException {
//        if (super.getSchema() == null) {
//            super.setSchema(getSchema());
//        }
//        super.validateObjectIfNeeded(marshaller, obj);
//    }

    /**
     * eSciDoc maintains a configurable property escidoc-core.xslt.std that holds the name of an xml-header stylesheet.
     * This name is set as marshallProperty "com.sun.xml.bind.xmlHeaders" for the cxf JAXBElementProvider in
     * applicationContext-cxf.xml. But property has to get surrounded with <?xml-stylesheet type=... or set to empty if
     * escidoc-core.xslt.std is not defined.
     *
     * @param marshallProperties marshallProperties
     */
    private void overrideXmlHeadersProperty(Map<String, Object> marshallProperties) {
        String xmlHeaders = (String) marshallProperties.get(XML_HEADERS_PATH);
        if (xmlHeaders != null && !xmlHeaders.isEmpty()) {
            if (!(xmlHeaders.startsWith("http://") || xmlHeaders.startsWith("https://"))) {
                String baseurl = EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_BASEURL);
                if (!baseurl.endsWith("/")) {
                    baseurl += "/";
                }

                if (xmlHeaders.startsWith("./")) {
                    xmlHeaders = baseurl + xmlHeaders.substring(2, xmlHeaders.length());
                } else if (xmlHeaders.startsWith("/")) {
                    xmlHeaders = baseurl + xmlHeaders.substring(1, xmlHeaders.length());
                }
            }
            marshallProperties
                .put(XML_HEADERS_PATH, "<?xml-stylesheet type=\"text/xsl\" href=\"" + xmlHeaders + "\"?>\n");
        }
    }
}