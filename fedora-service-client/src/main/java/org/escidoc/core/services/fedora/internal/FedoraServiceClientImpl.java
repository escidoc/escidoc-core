package org.escidoc.core.services.fedora.internal;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.TriggersRemove;
import org.apache.cxf.jaxrs.client.WebClient;
import org.escidoc.core.services.fedora.AddDatastreamPathParam;
import org.escidoc.core.services.fedora.AddDatastreamQueryParam;
import org.escidoc.core.services.fedora.CreateObjectPathParam;
import org.escidoc.core.services.fedora.CreateObjectQueryParam;
import org.escidoc.core.services.fedora.DeleteObjectPathParam;
import org.escidoc.core.services.fedora.DeleteObjectQueryParam;
import org.escidoc.core.services.fedora.DigitalObjectTO;
import org.escidoc.core.services.fedora.DigitalObjectTypeTOExtension;
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.escidoc.core.services.fedora.FedoraServiceRESTEndpoint;
import org.escidoc.core.services.fedora.GetDatastreamPathParam;
import org.escidoc.core.services.fedora.GetDatastreamQueryParam;
import org.escidoc.core.services.fedora.GetObjectProfilePathParam;
import org.escidoc.core.services.fedora.GetObjectProfileQueryParam;
import org.escidoc.core.services.fedora.GetObjectXMLPathParam;
import org.escidoc.core.services.fedora.GetObjectXMLQueryParam;
import org.escidoc.core.services.fedora.IngestPathParam;
import org.escidoc.core.services.fedora.IngestQueryParam;
import org.escidoc.core.services.fedora.ListDatastreamsPathParam;
import org.escidoc.core.services.fedora.ListDatastreamsQueryParam;
import org.escidoc.core.services.fedora.ModifiyDatastreamPathParam;
import org.escidoc.core.services.fedora.ModifyDatastreamQueryParam;
import org.escidoc.core.services.fedora.NextPIDPathParam;
import org.escidoc.core.services.fedora.NextPIDQueryParam;
import org.escidoc.core.services.fedora.ObjectVersionRequestTO;
import org.escidoc.core.services.fedora.ObjectVersionResponseTO;
import org.escidoc.core.services.fedora.PidListTO;
import org.escidoc.core.services.fedora.UpdateObjectPathParam;
import org.escidoc.core.services.fedora.UpdateObjectQueryParam;
import org.escidoc.core.services.fedora.VersionFilter;
import org.escidoc.core.services.fedora.VersionHistoryFilter;
import org.escidoc.core.services.fedora.XmlContentTypeDatastreamHolderTO;
import org.escidoc.core.services.fedora.access.ObjectDatastreamsTO;
import org.escidoc.core.services.fedora.access.ObjectProfileTO;
import org.esidoc.core.utils.io.Datastream;
import org.esidoc.core.utils.io.IOUtils;
import org.esidoc.core.utils.io.MimeTypes;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static org.esidoc.core.utils.Preconditions.checkNotNull;
import static org.esidoc.core.utils.Preconditions.checkState;

/**
 * Default implementation for {@link org.escidoc.core.services.fedora.FedoraServiceClient}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Service
public final class FedoraServiceClientImpl implements FedoraServiceClient {

    public final static Logger LOG = LoggerFactory.getLogger(FedoraServiceClientImpl.class);

    @Autowired
    @Qualifier("fedoraServiceRestClient")
    private FedoraServiceRESTEndpoint fedoraService;

    @Autowired
    @Qualifier("taskExecutor")
    private AsyncTaskExecutor taskExecutor;

    public void setFedoraService(final FedoraServiceRESTEndpoint fedoraService) {
        this.fedoraService = checkNotNull(fedoraService, "Fedora Service can not be null.");
    }

    public void setTaskExecutor(final AsyncTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public PidListTO getNextPID(final String namespace) {
        return this.getNextPID(namespace, NextPIDQueryParam.DEFAULT_NUMBER_OF_PIDS);
    }

    @Override
    public PidListTO getNextPID(final String namespace, final int numPIDs) {
        checkNotNull(namespace, "Namespace parameter can not be null.");
        checkState(numPIDs > 0, "Number of PIDs must be > 0.");
        NextPIDPathParam path = new NextPIDPathParam();
        NextPIDQueryParam query = new NextPIDQueryParam();
        query.setNamespace(namespace);
        query.setNumPIDs(numPIDs);
        return this.fedoraService.getNextPID(path, query);
    }

    @Override
    public Future<PidListTO> getNextPIDAsync(final String namespace) {
        return this.taskExecutor.submit(new Callable<PidListTO>() {
            public PidListTO call() throws Exception {
                return getNextPID(namespace);
            }
        });
    }

    @Override
    public Future<PidListTO> getNextPIDAsync(final String namespace, final int numPIDs) {
        return this.taskExecutor.submit(new Callable<PidListTO>() {
            public PidListTO call() throws Exception {
                return getNextPID(namespace, numPIDs);
            }
        });
    }

    public void createObject(final CreateObjectPathParam path,
                             final CreateObjectQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        this.fedoraService.createObject(path, query);
    }

    public Future createObjectAsync(final CreateObjectPathParam path,
                                    final CreateObjectQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        return this.taskExecutor.submit(new Callable() {
            public PidListTO call() throws Exception {
                createObject(path, query);
                return null;
            }
        });
    }

    @Cacheable(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetObjectProfileKeyGenerator"))
    public ObjectProfileTO getObjectProfile(final GetObjectProfilePathParam path,
                                            final GetObjectProfileQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        return this.fedoraService.getObjectProfile(path, query);
    }

    @Cacheable(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetObjectProfileKeyGenerator"))
    public Future<ObjectProfileTO> getObjectProfileAsync(final GetObjectProfilePathParam path,
                                                         final GetObjectProfileQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        return this.taskExecutor.submit(new Callable<ObjectProfileTO>() {
            public ObjectProfileTO call() throws Exception {
                return getObjectProfile(path, query);
            }
        });
    }

    @TriggersRemove(cacheName = "Fedora.DatastreamsLists", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.AddDatastreamKeyGenerator"))
    public void addDatastream(final AddDatastreamPathParam path,
                              final AddDatastreamQueryParam query,
                              final Datastream datastream) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        checkNotNull(datastream, "Datastream can not be null.");
        this.fedoraService.addDatastream(path, query, datastream);
    }

    @TriggersRemove(cacheName = "Fedora.DatastreamLists", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.AddDatastreamKeyGenerator"))
    public Future addDatastreamAsync(final AddDatastreamPathParam path,
                                     final AddDatastreamQueryParam query,
                                     final Datastream datastream) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        checkNotNull(datastream, "Datastream can not be null.");
        return this.taskExecutor.submit(new Callable() {
            public Object call() throws Exception {
                addDatastream(path, query, datastream);
                return null;
            }
        });
    }

    @Cacheable(cacheName = "Fedora.Datastreams", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetDatastreamKeyGenerator"))
    public Datastream getDatastream(final GetDatastreamPathParam path,
                                    final GetDatastreamQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        return this.fedoraService.getDatastream(path, query);
    }

    @Cacheable(cacheName = "Fedora.Datastreams", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetDatastreamKeyGenerator"))
    public Future<Datastream> getDatastreamAsync(final GetDatastreamPathParam path,
                                                 final GetDatastreamQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        return this.taskExecutor.submit(new Callable<Datastream>() {
            public Datastream call() throws Exception {
                return getDatastream(path, query);
            }
        });
    }

    @TriggersRemove(cacheName = "Fedora.Datastreams", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ModifyDatastreamKeyGenerator"))
    public void modifyDatastream(final ModifiyDatastreamPathParam path,
                                 final ModifyDatastreamQueryParam query,
                                 final Datastream datastream) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        checkNotNull(datastream, "Datastream can not be null.");
        this.fedoraService.modifyDatastream(path, query, datastream);
    }

    @TriggersRemove(cacheName = "Fedora.Datastreams", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ModifyDatastreamKeyGenerator"))
    public Future modifyDatastreamAsync(final ModifiyDatastreamPathParam path,
                                        final ModifyDatastreamQueryParam query,
                                        final Datastream datastream) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        checkNotNull(datastream, "Datastream can not be null.");
        return this.taskExecutor.submit(new Callable() {
            public Object call() throws Exception {
                modifyDatastreamAsync(path, query, datastream);
                return null;
            }
        });
    }

    @Cacheable(cacheName = "Fedora.DatastreamLists", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ListDatastreamsKeyGenerator"))
    public ObjectDatastreamsTO listDatastreams(final ListDatastreamsPathParam path,
                                               final ListDatastreamsQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        return this.fedoraService.listDatastreams(path, query);
    }

    @Cacheable(cacheName = "Fedora.DatastreamLists", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ListDatastreamsKeyGenerator"))
    public Future<ObjectDatastreamsTO> listDatastreamsAsync(final ListDatastreamsPathParam path,
                                                            final ListDatastreamsQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        return this.taskExecutor.submit(new Callable<ObjectDatastreamsTO>() {
            public ObjectDatastreamsTO call() throws Exception {
                return listDatastreams(path, query);
            }
        });
    }

    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.UpdateObjectKeyGenerator"))
    public void updateObject(final UpdateObjectPathParam path, final UpdateObjectQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        this.fedoraService.updateObject(path, query);
    }

    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.UpdateObjectKeyGenerator"))
    public Future updateObjectAsync(final UpdateObjectPathParam path, final UpdateObjectQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        return this.taskExecutor.submit(new Callable() {
            public Object call() throws Exception {
                updateObject(path, query);
                return null;
            }
        });
    }

    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.DeleteObjectKeyGenerator"))
    public void deleteObject(final DeleteObjectPathParam path, final DeleteObjectQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        this.fedoraService.deleteObject(path, query);
    }

    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.DeleteObjectKeyGenerator"))
    public Future deleteObjectAsync(final DeleteObjectPathParam path, final DeleteObjectQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        return this.taskExecutor.submit(new Callable() {
            public Object call() throws Exception {
                deleteObject(path, query);
                return null;
            }
        });
    }

    public String ingest(final IngestPathParam path,
                         final IngestQueryParam query,
                         final DigitalObjectTypeTOExtension digitalObjectTO) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        checkNotNull(digitalObjectTO, "DigitalObjectTO can not be null.");
        return this.fedoraService.ingest(path, query, digitalObjectTO);
    }

    public Future<String> ingestAsync(final IngestPathParam path,
                                      final IngestQueryParam query,
                                      final DigitalObjectTypeTOExtension digitalObjectTO) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        checkNotNull(digitalObjectTO, "DigitalObjectTO can not be null.");
        return this.taskExecutor.submit(new Callable<String>() {
            public String call() throws Exception {
                return ingest(path, query, digitalObjectTO);
            }
        });
    }

    public DigitalObjectTO getObjectXML(final GetObjectXMLPathParam path, final GetObjectXMLQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        return this.fedoraService.getObjectXML(path, query);
    }

    public Future<DigitalObjectTypeTOExtension> getObjectXMLAsync(final GetObjectXMLPathParam path,
                                                                  final GetObjectXMLQueryParam query) {
        checkNotNull(path, "Path parameter can not be null.");
        checkNotNull(query, "Query parameter can not be null.");
        return this.taskExecutor.submit(new Callable<DigitalObjectTypeTOExtension>() {
            public DigitalObjectTypeTOExtension call() throws Exception {
                return getObjectXML(path, query);
            }
        });
    }

    private DigitalObjectTO parseObjectVersion(Datastream datastream, DateTime versionDate) throws IOException {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(DigitalObjectTO.class);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final VersionFilter versionFilter = new VersionFilter(datastream.getInputStream(), versionDate);
            unmarshaller.setListener(versionFilter);
            return (DigitalObjectTO) unmarshaller.unmarshal(versionFilter.getFilteredXmlStreamReader());
        } catch (JAXBException e) {
            throw new IOException("Error on parsing object version.", e);
        }
    }

    private Datastream getVersionHistoryDatastream(Datastream datastream) throws IOException {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(DigitalObjectTO.class);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final VersionHistoryFilter versionHistoryFilter = new VersionHistoryFilter(datastream.getInputStream());
            unmarshaller.setListener(versionHistoryFilter);
            final DigitalObjectTO digitalObjectTO =
                    (DigitalObjectTO) unmarshaller.unmarshal(versionHistoryFilter.getFilteredXmlStreamReader());
            if (digitalObjectTO.getDatastream() != null
                    && digitalObjectTO.getDatastream().get(0) != null
                    && digitalObjectTO.getDatastream().get(0).getDatastreamVersion() != null
                    && digitalObjectTO.getDatastream().get(0).getDatastreamVersion().get(0) != null) {
                final XmlContentTypeDatastreamHolderTO versionHistory = (XmlContentTypeDatastreamHolderTO)
                        digitalObjectTO.getDatastream().get(0).getDatastreamVersion().get(0).getXmlContent();
                if (versionHistory != null) {
                    return versionHistory.getDatastream();
                }
            }
        } catch (final JAXBException e) {
            throw new IOException("Error on parsing version history datastream.", e);
        }
        return null;
    }

    private Datastream copyResponseToDatastream(Response fedoraResponse) throws IOException {
        final InputStream inputStream = (InputStream) fedoraResponse.getEntity();
        final Datastream datastream = new Datastream();
        IOUtils.copy(inputStream, datastream);
        return datastream;
    }

}
