package de.escidoc.core.common.business.fedora;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Abstract base class for handlers.
 * 
 * @author tte
 * @common
 */
public class HandlerBase implements InitializingBean {

    private FedoraUtility fedoraUtility = null;

    private TripleStoreUtility tripleStoreUtility = null;

    private EscidocIdProvider idProvider = null;

    private Utility utility = null;

    protected String transformSearchResponse2relations(String searchResponse)
        throws SystemException {

        try {
            TransformerFactory tf = TransformerFactory.newInstance();

            URL xsltUrl =
                new URL(EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_SELFURL)
                    + "/xsl/searchResponse2relations.xsl");
            HttpURLConnection conn =
                (HttpURLConnection) xsltUrl.openConnection();
            Transformer t =
                tf.newTransformer(new StreamSource(conn.getInputStream()));
            t.setParameter("XSLT", EscidocConfiguration.getInstance().get(
                EscidocConfiguration.ESCIDOC_CORE_XSLT_STD));

            // searchResponse is already a String; so, no effort to stream
            StringWriter sw = new StringWriter();
            t.transform(new StreamSource(new ByteArrayInputStream(
                searchResponse.getBytes(XmlUtility.CHARACTER_ENCODING))),
                new StreamResult(sw));
            return sw.toString();

        }
        catch (IOException e) {
            throw new SystemException(
                "Convertion of search response to relations failed.");
        }
        catch (TransformerException e) {
            throw new SystemException(
                "Convertion of search response to relations failed.");
        }
    }

    /**
     * Gets the {@link FedoraUtility}.
     * 
     * @return FedoraUtility Returns the {@link FedoraUtility} object.
     */
    protected FedoraUtility getFedoraUtility() {

        return this.fedoraUtility;
    }

    /**
     * Injects the {@link FedoraUtility}.
     * 
     * @param fedoraUtility
     *            The {@link FedoraUtility} to set
     */
    public void setFedoraUtility(final FedoraUtility fedoraUtility) {

        this.fedoraUtility = fedoraUtility;
    }

    /**
     * Gets the {@link TripleStoreUtility}.
     * 
     * @return TripleStoreUtility Returns the {@link TripleStoreUtility} object.
     */
    protected TripleStoreUtility getTripleStoreUtility() {

        return this.tripleStoreUtility;
    }

    /**
     * Injects the {@link TripleStoreUtility}.
     * 
     * @param tripleStoreUtility
     *            The {@link TripleStoreUtility} to set
     */
    public void setTripleStoreUtility(
        final TripleStoreUtility tripleStoreUtility) {

        this.tripleStoreUtility = tripleStoreUtility;
    }

    /**
     * Gets the {@link EscidocIdProvider}.
     * 
     * @return Returns the {@link EscidocIdProvider} object.
     * @common
     */
    protected EscidocIdProvider getIdProvider() {

        return this.idProvider;
    }

    /**
     * Injects the {@link EscidocIdProvider}.
     * 
     * @param idProvider
     *            The {@link EscidocIdProvider} to set.
     * @common
     */
    public void setIdProvider(final EscidocIdProvider idProvider) {

        this.idProvider = idProvider;
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @throws Exception
     * @see org.springframework.beans.factory.InitializingBean
     *      #afterPropertiesSet()
     * @common
     */
    public void afterPropertiesSet() throws Exception {

        if (this.fedoraUtility == null) {
            throw new BeanInitializationException(
                "Fedora utility has not been set");
        }
        // TSU is used just for Item and Container
        // if (this.tripleStoreUtility == null) {
        // throw new BeanInitializationException(
        // "TripleStore utility has not been set");
        // }
        if (this.idProvider == null) {
            throw new BeanInitializationException(
                "Id provider has not been set");
        }
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * @return Returns the utility.
     */
    protected Utility getUtility() {
        if (utility == null) {
            utility = Utility.getInstance();
        }
        return utility;
    }
}
