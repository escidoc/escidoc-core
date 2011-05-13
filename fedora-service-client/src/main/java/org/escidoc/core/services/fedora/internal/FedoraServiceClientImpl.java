package org.escidoc.core.services.fedora.internal;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.TriggersRemove;
import net.sf.oval.guard.Guarded;
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

import javax.validation.constraints.NotNull;
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
@Guarded
public final class FedoraServiceClientImpl implements FedoraServiceClient {

    public final static Logger LOG = LoggerFactory.getLogger(FedoraServiceClientImpl.class);

    @Autowired
    @Qualifier("fedoraServiceRestClient")
    private FedoraServiceRESTEndpoint fedoraService;

    @Autowired
    @Qualifier("taskExecutor")
    private AsyncTaskExecutor taskExecutor;

    @Override
    public PidListTO getNextPID(final String namespace) {
        return this.getNextPID(namespace, NextPIDQueryParam.DEFAULT_NUMBER_OF_PIDS);
    }

    @Override
    public PidListTO getNextPID(@NotNull final String namespace, final int numPIDs) {
        checkState(numPIDs > 0, "Number of PIDs must be > 0.");
        NextPIDPathParam path = new NextPIDPathParam();
        NextPIDQueryParam query = new NextPIDQueryParam();
        query.setNamespace(namespace);
        query.setNumPIDs(numPIDs);
        return this.fedoraService.getNextPID(path, query);
    }

    @Override
    public Future<PidListTO> getNextPIDAsync(@NotNull final String namespace) {
        return this.taskExecutor.submit(new Callable<PidListTO>() {
            public PidListTO call() throws Exception {
                return getNextPID(namespace);
            }
        });
    }

    @Override
    public Future<PidListTO> getNextPIDAsync(@NotNull final String namespace, final int numPIDs) {
        return this.taskExecutor.submit(new Callable<PidListTO>() {
            public PidListTO call() throws Exception {
                return getNextPID(namespace, numPIDs);
            }
        });
    }

    public void createObject(@NotNull final CreateObjectPathParam path,
                             @NotNull final CreateObjectQueryParam query) {
        this.fedoraService.createObject(path, query);
    }

    public Future createObjectAsync(@NotNull final CreateObjectPathParam path,
                                    @NotNull final CreateObjectQueryParam query) {
        return this.taskExecutor.submit(new Callable() {
            public PidListTO call() throws Exception {
                createObject(path, query);
                return null;
            }
        });
    }

    @Cacheable(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetObjectProfileKeyGenerator"))
    public ObjectProfileTO getObjectProfile(@NotNull final GetObjectProfilePathParam path,
                                            @NotNull final GetObjectProfileQueryParam query) {
        return this.fedoraService.getObjectProfile(path, query);
    }

    @Cacheable(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetObjectProfileKeyGenerator"))
    public Future<ObjectProfileTO> getObjectProfileAsync(@NotNull final GetObjectProfilePathParam path,
                                                         @NotNull final GetObjectProfileQueryParam query) {
        return this.taskExecutor.submit(new Callable<ObjectProfileTO>() {
            public ObjectProfileTO call() throws Exception {
                return getObjectProfile(path, query);
            }
        });
    }

    @TriggersRemove(cacheName = "Fedora.DatastreamsLists", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.AddDatastreamKeyGenerator"))
    public void addDatastream(@NotNull final AddDatastreamPathParam path,
                              @NotNull final AddDatastreamQueryParam query,
                              @NotNull final Datastream datastream) {
        this.fedoraService.addDatastream(path, query, datastream);
    }

    @TriggersRemove(cacheName = "Fedora.DatastreamLists", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.AddDatastreamKeyGenerator"))
    public Future addDatastreamAsync(@NotNull final AddDatastreamPathParam path,
                                     @NotNull final AddDatastreamQueryParam query,
                                     @NotNull final Datastream datastream) {;
        return this.taskExecutor.submit(new Callable() {
            public Object call() throws Exception {
                addDatastream(path, query, datastream);
                return null;
            }
        });
    }

    @Cacheable(cacheName = "Fedora.Datastreams", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetDatastreamKeyGenerator"))
    public Datastream getDatastream(@NotNull final GetDatastreamPathParam path,
                                    @NotNull final GetDatastreamQueryParam query) {
        return this.fedoraService.getDatastream(path, query);
    }

    @Cacheable(cacheName = "Fedora.Datastreams", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetDatastreamKeyGenerator"))
    public Future<Datastream> getDatastreamAsync(@NotNull final GetDatastreamPathParam path,
                                                 @NotNull final GetDatastreamQueryParam query) {
        return this.taskExecutor.submit(new Callable<Datastream>() {
            public Datastream call() throws Exception {
                return getDatastream(path, query);
            }
        });
    }

    @TriggersRemove(cacheName = "Fedora.Datastreams", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ModifyDatastreamKeyGenerator"))
    public void modifyDatastream(@NotNull final ModifiyDatastreamPathParam path,
                                 @NotNull final ModifyDatastreamQueryParam query,
                                 @NotNull final Datastream datastream) {
        this.fedoraService.modifyDatastream(path, query, datastream);
    }

    @TriggersRemove(cacheName = "Fedora.Datastreams", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ModifyDatastreamKeyGenerator"))
    public Future modifyDatastreamAsync(@NotNull final ModifiyDatastreamPathParam path,
                                        @NotNull final ModifyDatastreamQueryParam query,
                                        @NotNull final Datastream datastream) {
        return this.taskExecutor.submit(new Callable() {
            public Object call() throws Exception {
                modifyDatastreamAsync(path, query, datastream);
                return null;
            }
        });
    }

    @Cacheable(cacheName = "Fedora.DatastreamLists", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ListDatastreamsKeyGenerator"))
    public ObjectDatastreamsTO listDatastreams(@NotNull final ListDatastreamsPathParam path,
                                               @NotNull final ListDatastreamsQueryParam query) {
        return this.fedoraService.listDatastreams(path, query);
    }

    @Cacheable(cacheName = "Fedora.DatastreamLists", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ListDatastreamsKeyGenerator"))
    public Future<ObjectDatastreamsTO> listDatastreamsAsync(@NotNull final ListDatastreamsPathParam path,
                                                            @NotNull final ListDatastreamsQueryParam query) {
        return this.taskExecutor.submit(new Callable<ObjectDatastreamsTO>() {
            public ObjectDatastreamsTO call() throws Exception {
                return listDatastreams(path, query);
            }
        });
    }

    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.UpdateObjectKeyGenerator"))
    public void updateObject(@NotNull final UpdateObjectPathParam path,
                             @NotNull final UpdateObjectQueryParam query) {
        this.fedoraService.updateObject(path, query);
    }

    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.UpdateObjectKeyGenerator"))
    public Future updateObjectAsync(@NotNull final UpdateObjectPathParam path,
                                    @NotNull final UpdateObjectQueryParam query) {
        return this.taskExecutor.submit(new Callable() {
            public Object call() throws Exception {
                updateObject(path, query);
                return null;
            }
        });
    }

    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.DeleteObjectKeyGenerator"))
    public void deleteObject(@NotNull final DeleteObjectPathParam path, @NotNull final DeleteObjectQueryParam query) {
        this.fedoraService.deleteObject(path, query);
    }

    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.DeleteObjectKeyGenerator"))
    public Future deleteObjectAsync(@NotNull final DeleteObjectPathParam path,
                                    @NotNull final DeleteObjectQueryParam query) {
        return this.taskExecutor.submit(new Callable() {
            public Object call() throws Exception {
                deleteObject(path, query);
                return null;
            }
        });
    }

    public String ingest(@NotNull final IngestPathParam path,
                         @NotNull final IngestQueryParam query,
                         @NotNull final DigitalObjectTypeTOExtension digitalObjectTO) {
        return this.fedoraService.ingest(path, query, digitalObjectTO);
    }

    public Future<String> ingestAsync(@NotNull final IngestPathParam path,
                                      @NotNull final IngestQueryParam query,
                                      @NotNull final DigitalObjectTypeTOExtension digitalObjectTO) {
        return this.taskExecutor.submit(new Callable<String>() {
            public String call() throws Exception {
                return ingest(path, query, digitalObjectTO);
            }
        });
    }

    public DigitalObjectTO getObjectXML(@NotNull final GetObjectXMLPathParam path,
                                        @NotNull final GetObjectXMLQueryParam query) {
        return this.fedoraService.getObjectXML(path, query);
    }

    public Future<DigitalObjectTypeTOExtension> getObjectXMLAsync(@NotNull final GetObjectXMLPathParam path,
                                                                  @NotNull final GetObjectXMLQueryParam query) {
        return this.taskExecutor.submit(new Callable<DigitalObjectTypeTOExtension>() {
            public DigitalObjectTypeTOExtension call() throws Exception {
                return getObjectXML(path, query);
            }
        });
    }

}
