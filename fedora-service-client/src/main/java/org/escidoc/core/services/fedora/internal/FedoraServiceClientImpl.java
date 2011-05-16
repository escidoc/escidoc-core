package org.escidoc.core.services.fedora.internal;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.TriggersRemove;
import net.sf.oval.guard.Guarded;
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
import org.esidoc.core.utils.VoidObject;
import org.esidoc.core.utils.io.Datastream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
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
        assertParametersNotNull = true, checkInvariants=true, inspectInterfaces = true)
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
    public ObjectProfileTO getObjectProfile(@NotNull final GetObjectProfilePathParam path,
                                            @NotNull final GetObjectProfileQueryParam query) {
        return this.fedoraService.getObjectProfile(path, query);
    }

    @Override
    @Async
    public Future<ObjectProfileTO> getObjectProfileAsync(@NotNull final GetObjectProfilePathParam path,
                                                         @NotNull final GetObjectProfileQueryParam query) {
        return new AsyncResult<ObjectProfileTO>(getObjectProfile(path, query));
    }

    @Override
    @TriggersRemove(cacheName = "Fedora.DatastreamsLists", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.AddDatastreamKeyGenerator"))
    public void addDatastream(@NotNull final AddDatastreamPathParam path,
                              @NotNull final AddDatastreamQueryParam query,
                              final Datastream datastream) {
        this.fedoraService.addDatastream(path, query, datastream);
    }

    @Override
    @Async
    public Future<VoidObject> addDatastreamAsync(@NotNull final AddDatastreamPathParam path,
                                     @NotNull final AddDatastreamQueryParam query,
                                     final Datastream datastream) {;
        addDatastream(path, query, datastream);
        return new AsyncResult<VoidObject>(VoidObject.getInstance());
    }

    @Override
    @Cacheable(cacheName = "Fedora.Datastreams", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.GetDatastreamKeyGenerator"))
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
    @TriggersRemove(cacheName = "Fedora.Datastreams", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.ModifyDatastreamKeyGenerator"))
    public void modifyDatastream(@NotNull final ModifiyDatastreamPathParam path,
                                 @NotNull final ModifyDatastreamQueryParam query,
                                 @NotNull final Datastream datastream) {
        this.fedoraService.modifyDatastream(path, query, datastream);
    }

    @Override
    @Async
    public Future modifyDatastreamAsync(@NotNull final ModifiyDatastreamPathParam path,
                                        @NotNull final ModifyDatastreamQueryParam query,
                                        @NotNull final Datastream datastream) {
        modifyDatastreamAsync(path, query, datastream);
        return new AsyncResult<VoidObject>(VoidObject.getInstance());
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
    public Future<ObjectDatastreamsTO> listDatastreamsAsync(@NotNull final ListDatastreamsPathParam path,
                                                            @NotNull final ListDatastreamsQueryParam query) {
        return new AsyncResult<ObjectDatastreamsTO>(listDatastreams(path, query));
    }

    @Override
    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.UpdateObjectKeyGenerator"))
    public void updateObject(@NotNull final UpdateObjectPathParam path,
                             @NotNull final UpdateObjectQueryParam query) {
        this.fedoraService.updateObject(path, query);
    }

    @Override
    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.UpdateObjectKeyGenerator"))
    public Future<VoidObject> updateObjectAsync(@NotNull final UpdateObjectPathParam path,
                                    @NotNull final UpdateObjectQueryParam query) {
        updateObject(path, query);
        return new AsyncResult<VoidObject>(VoidObject.getInstance());
    }

    @Override
    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.DeleteObjectKeyGenerator"))
    public void deleteObject(@NotNull final DeleteObjectPathParam path, @NotNull final DeleteObjectQueryParam query) {
        this.fedoraService.deleteObject(path, query);
    }

    @Override
    @TriggersRemove(cacheName = "Fedora.ObjectProfiles", keyGenerator = @KeyGenerator(
            name = "org.escidoc.core.services.fedora.internal.cache.DeleteObjectKeyGenerator"))
    public Future<VoidObject> deleteObjectAsync(@NotNull final DeleteObjectPathParam path,
                                    @NotNull final DeleteObjectQueryParam query) {
        deleteObject(path, query);
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

    @Override
    public DigitalObjectTO getObjectXML(@NotNull final GetObjectXMLPathParam path,
                                        @NotNull final GetObjectXMLQueryParam query) {
        return this.fedoraService.getObjectXML(path, query);
    }

    @Override
    @Async
    public Future<DigitalObjectTO> getObjectXMLAsync(@NotNull final GetObjectXMLPathParam path,
                                                     @NotNull final GetObjectXMLQueryParam query) {
        return new AsyncResult<DigitalObjectTO>(getObjectXML(path, query));
    }

}
