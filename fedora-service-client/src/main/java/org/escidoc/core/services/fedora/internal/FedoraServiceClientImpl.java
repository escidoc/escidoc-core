package org.escidoc.core.services.fedora.internal;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.TriggersRemove;
import net.sf.oval.guard.Guarded;
import org.apache.cxf.jaxrs.client.Client;
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
import org.escidoc.core.services.fedora.PidListTO;
import org.escidoc.core.services.fedora.UpdateObjectPathParam;
import org.escidoc.core.services.fedora.UpdateObjectQueryParam;
import org.escidoc.core.services.fedora.access.ObjectDatastreamsTO;
import org.escidoc.core.services.fedora.access.ObjectProfileTO;
import org.escidoc.core.services.fedora.management.DatastreamProfileTO;
import org.esidoc.core.utils.VoidObject;
import org.esidoc.core.utils.io.Datastream;
import org.esidoc.core.utils.io.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.concurrent.Future;

import static org.esidoc.core.utils.Preconditions.checkNotNull;
import static org.esidoc.core.utils.Preconditions.checkState;

/**
 * Default implementation for {@link org.escidoc.core.services.fedora.FedoraServiceClient}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Service("fedoraServiceClient")
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = false, checkInvariants=true, inspectInterfaces = true)
public class FedoraServiceClientImpl implements FedoraServiceClient {

    public final static Logger LOG = LoggerFactory.getLogger(FedoraServiceClientImpl.class);

    private FedoraServiceRESTEndpoint fedoraService;

    public void setFedoraService(final FedoraServiceRESTEndpoint fedoraService) {
        this.fedoraService = fedoraService;
    }

    @Override
    public PidListTO getNextPID(final String namespace) {
        return this.getNextPID(namespace, NextPIDQueryParam.DEFAULT_NUMBER_OF_PIDS);
    }

    @Override
    @Async
    public Future<PidListTO> getNextPIDAsync(@NotNull final String namespace) {
        return new AsyncResult<PidListTO>(this.getNextPID(namespace));
    }

    @Override
    public PidListTO getNextPID(@NotNull final String namespace, final int numPIDs) {
        checkState(numPIDs > 0, "Number of PIDs must be > 0.");
        final NextPIDPathParam path = new NextPIDPathParam();
        final NextPIDQueryParam query = new NextPIDQueryParam();
        query.setNamespace(namespace);
        query.setNumPIDs(numPIDs);
        return this.fedoraService.getNextPID(path, query);
    }

    @Override
    @Async
    public Future<PidListTO> getNextPIDAsync(@NotNull final String namespace, final int numPIDs) {
        return new AsyncResult<PidListTO>(this.getNextPID(namespace, numPIDs));
    }

    @Override
    public void createObject(@NotNull final CreateObjectPathParam path,
                             @NotNull final CreateObjectQueryParam query) {
        this.fedoraService.createObject(path, query);
    }

    @Override
    @Async
    public Future<VoidObject> createObjectAsync(@NotNull final CreateObjectPathParam path,
                                    @NotNull final CreateObjectQueryParam query) {
        this.createObject(path, query);
        return new AsyncResult<VoidObject>(VoidObject.getInstance());
    }

    @Override
    @Cacheable(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetObjectProfileKeyGenerator"))
    public ObjectProfileTO getObjectProfile(@NotNull final String pid) {
        final GetObjectProfilePathParam path = new GetObjectProfilePathParam();
        path.setPid(pid);
        final GetObjectProfileQueryParam query = new GetObjectProfileQueryParam();
        return this.fedoraService.getObjectProfile(path, query);
    }

    @Override
    @Async
    @Cacheable(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetObjectProfileKeyGenerator"))
    public Future<ObjectProfileTO> getObjectProfileAsync(@NotNull final String pid) {
        return new AsyncResult<ObjectProfileTO>(getObjectProfile(pid));
    }

    @Override
    @Cacheable(cacheName = "Fedora.DigitalObjects", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetObjectXMLKeyGenerator"))
    public DigitalObjectTO getObjectXML(@NotNull final String pid) {
        final GetObjectXMLPathParam path = new GetObjectXMLPathParam();
        path.setPid(pid);
        final GetObjectXMLQueryParam query = new GetObjectXMLQueryParam();
        return this.fedoraService.getObjectXML(path, query);
    }

    @Override
    @Async
    @Cacheable(cacheName = "Fedora.DigitalObjects", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetObjectXMLKeyGenerator"))
    public Future<DigitalObjectTO> getObjectXMLAsync(@NotNull final String pid) {
        return new AsyncResult<DigitalObjectTO>(getObjectXML(pid));
    }

    @Override
    public InputStream getObjectXMLAsStream(@NotNull final String pid) {
        final WebClient client = WebClient.fromClient(WebClient.client(this.fedoraService));
        final Response response = client.path("/objects/" + pid + "/objectXML").accept(MimeTypes.TEXT_XML)
                .type(MimeTypes.TEXT_XML).get();
        return (InputStream)response.getEntity();
    }

    @Override
    @Async
    public Future<InputStream> getObjectXMLAsStreamAsync(@NotNull final String pid) {
        return new AsyncResult<InputStream>(getObjectXMLAsStream(pid));
    }

    @Override
    @Cacheable(cacheName = "Fedora.DatastreamLists", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ListDatastreamsKeyGenerator"))
    public ObjectDatastreamsTO listDatastreams(@NotNull final ListDatastreamsPathParam path,
                                               @NotNull final ListDatastreamsQueryParam query) {
        return this.fedoraService.listDatastreams(path, query);
    }

    @Override
    @Async
    @Cacheable(cacheName = "Fedora.DatastreamLists", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ListDatastreamsKeyGenerator"))
    public Future<ObjectDatastreamsTO> listDatastreamsAsync(@NotNull final ListDatastreamsPathParam path,
                                                            @NotNull final ListDatastreamsQueryParam query) {
        return new AsyncResult<ObjectDatastreamsTO>(listDatastreams(path, query));
    }

    @Override
    public Datastream getDatastream(@NotNull final GetDatastreamPathParam path,
                                    @NotNull final GetDatastreamQueryParam query) {
        return this.fedoraService.getDatastream(path, query);
    }

    @Override
    @Async
    public Future<Datastream> getDatastreamAsync(@NotNull final GetDatastreamPathParam path,
                                                 @NotNull final GetDatastreamQueryParam query) {
        return new AsyncResult<Datastream>(getDatastream(path, query));
    }

    @Override
    @TriggersRemove(cacheName = {"Fedora.ObjectProfiles", "Fedora.DigitalObjects", "Fedora.DatastreamLists"},
            keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.AddDatastreamKeyGenerator"))
    public DatastreamProfileTO addDatastream(@NotNull final AddDatastreamPathParam path,
                              @NotNull final AddDatastreamQueryParam query,
                              final Datastream datastream) {
        return this.fedoraService.addDatastream(path, query, datastream);
    }

    @Override
    @Async
    @TriggersRemove(cacheName = {"Fedora.ObjectProfiles", "Fedora.DigitalObjects", "Fedora.DatastreamLists"},
            keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.AddDatastreamKeyGenerator"))
    public Future<DatastreamProfileTO> addDatastreamAsync(@NotNull final AddDatastreamPathParam path,
                                     @NotNull final AddDatastreamQueryParam query,
                                     final Datastream datastream) {;
        return new AsyncResult<DatastreamProfileTO>(addDatastream(path, query, datastream));
    }



    @Override
    @TriggersRemove(cacheName = {"Fedora.ObjectProfiles", "Fedora.DigitalObjects", "Fedora.DatastreamLists"},
            keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ModifyDatastreamKeyGenerator"))
    public DatastreamProfileTO modifyDatastream(@NotNull final ModifiyDatastreamPathParam path,
                                 @NotNull final ModifyDatastreamQueryParam query,
                                 final Datastream datastream) {
        if(datastream != null) {
            return this.fedoraService.modifyDatastream(path, query, datastream);
        } else {
            return this.fedoraService.modifyDatastream(path, query, new Datastream());
        }
    }

    @Override
    @Async
    @TriggersRemove(cacheName = {"Fedora.ObjectProfiles", "Fedora.DigitalObjects", "Fedora.DatastreamLists"},
            keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ModifyDatastreamKeyGenerator"))
    public Future<DatastreamProfileTO> modifyDatastreamAsync(@NotNull final ModifiyDatastreamPathParam path,
                                        @NotNull final ModifyDatastreamQueryParam query,
                                        final Datastream datastream) {
        return new AsyncResult<DatastreamProfileTO>(modifyDatastream(path, query, datastream));
    }

    @Override
    @TriggersRemove(cacheName = {"Fedora.ObjectProfiles", "Fedora.DigitalObjects", "Fedora.DatastreamLists"},
            keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.UpdateObjectKeyGenerator"))
    public void updateObject(@NotNull final UpdateObjectPathParam path,
                             @NotNull final UpdateObjectQueryParam query) {
        this.fedoraService.updateObject(path, query);
    }

    @Override
    @TriggersRemove(cacheName = {"Fedora.ObjectProfiles", "Fedora.DigitalObjects", "Fedora.DatastreamLists"},
            keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.UpdateObjectKeyGenerator"))
    public Future<VoidObject> updateObjectAsync(@NotNull final UpdateObjectPathParam path,
                                    @NotNull final UpdateObjectQueryParam query) {
        updateObject(path, query);
        return new AsyncResult<VoidObject>(VoidObject.getInstance());
    }

    @Override
    @TriggersRemove(cacheName = {"Fedora.ObjectProfiles", "Fedora.DigitalObjects", "Fedora.DatastreamLists"},
            keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.DeleteObjectKeyGenerator"))
    public void deleteObject(@NotNull final String pid) {
        final DeleteObjectPathParam path = new DeleteObjectPathParam();
        path.setPid(pid);
        final DeleteObjectQueryParam query = new DeleteObjectQueryParam();
        this.fedoraService.deleteObject(path, query);
    }

    @Override
    @TriggersRemove(cacheName = {"Fedora.ObjectProfiles", "Fedora.DigitalObjects", "Fedora.DatastreamLists"},
            keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.DeleteObjectKeyGenerator"))
    public Future<VoidObject> deleteObjectAsync(@NotNull final String pid) {
        deleteObject(pid);
        return new AsyncResult<VoidObject>(VoidObject.getInstance());
    }

    @Override
    public String ingest(@NotNull final IngestPathParam path,
                         @NotNull final IngestQueryParam query,
                         @NotNull final DigitalObjectTypeTOExtension digitalObjectTO) {
        return this.fedoraService.ingest(path, query, digitalObjectTO);
    }

    @Override
    @Async
    public Future<String> ingestAsync(@NotNull final IngestPathParam path,
                                      @NotNull final IngestQueryParam query,
                                      @NotNull final DigitalObjectTypeTOExtension digitalObjectTO) {
        return new AsyncResult<String>(ingest(path, query, digitalObjectTO));
    }

}
