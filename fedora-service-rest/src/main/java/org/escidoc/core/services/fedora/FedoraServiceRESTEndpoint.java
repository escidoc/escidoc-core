package org.escidoc.core.services.fedora;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.escidoc.core.services.fedora.access.ObjectDatastreamsTO;
import org.escidoc.core.services.fedora.access.ObjectProfileTO;
import org.escidoc.core.services.fedora.management.DatastreamHistoryTO;
import org.escidoc.core.services.fedora.management.DatastreamProfileTO;
import org.escidoc.core.services.fedora.management.DatastreamProfilesTO;
import org.escidoc.core.utils.io.MimeTypes;
import org.escidoc.core.utils.io.Stream;

/**
 * Service to access the Fedora repository.
 * 
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public interface FedoraServiceRESTEndpoint {

    @POST
    @Path("/objects/nextPID")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    PidListTO getNextPID(@QueryParam("namespace") String namespace,
                        @QueryParam("numPIDs") int numPIDs,
                        @QueryParam("format") String format);

    @GET
    @Path("/objects/{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    ObjectProfileTO getObjectProfile(
        @NotNull @PathParam("pid") String pid,
        @QueryParam("format") String format);

    @PUT
    @Path("/objects/{pid}")
    @Consumes(MimeTypes.TEXT_XML)
    void updateObject(
        @NotNull @PathParam("pid") String pid, @QueryParam("label") String label,
        @QueryParam("logMessage") String logMessage,
        @QueryParam("ownerId") String ownerId,
        @QueryParam("state") String state,
        @QueryParam("lastModifiedDate") String lastModifiedDate);

    @DELETE
    @Path("/objects/{pid}")
    @Consumes(MimeTypes.TEXT_XML)
    void deleteObject(
        @NotNull @PathParam("pid") String pid, @QueryParam("logMessage") String logMessage);

    @GET
    @Path("/objects/{pid}/export")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    DigitalObjectTO export(@NotNull @PathParam("pid") String pid, @QueryParam("format") String format,
        @QueryParam("context") String context,@QueryParam("encoding") String encoding);

    @GET
    @Path("/objects/{pid}/export")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    Stream exportAsStream(@NotNull @PathParam("pid") String pid, @QueryParam("format") String format,
        @QueryParam("context") String context,@QueryParam("encoding") String encoding);

    @GET
    @Path("/objects/{pid}/objectXML")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    DigitalObjectTO getObjectXML(
        @NotNull @PathParam("pid") String pid);

    @GET
    @Path("/objects/{pid}/objectXML")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    Stream getObjectXMLAsStream(
        @NotNull @PathParam("pid") String pid);

    @POST
    @Path("/objects/{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    String ingest(
        @NotNull @PathParam("pid") String pid, @QueryParam("label") String label,
        @QueryParam("format") String format,@QueryParam("encoding") String encoding,
        @QueryParam("namespace") String namespace,@QueryParam("ownerId") String ownerId,
        @QueryParam("logMessage") String logMessage,@QueryParam("ignoreMime") Boolean ignoreMime,
        @NotNull DigitalObjectTO digitalObjectTO);

    @POST
    @Path("/objects/{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    Stream ingest(
        @NotNull @PathParam("pid") String pid, @QueryParam("label") String label,
        @QueryParam("format") String format,@QueryParam("encoding") String encoding,
        @QueryParam("namespace") String namespace,@QueryParam("ownerId") String ownerId,
        @QueryParam("logMessage") String logMessage,@QueryParam("ignoreMime") Boolean ignoreMime,
        @NotNull Stream foxml);

    @POST
    @Path("/objects/{pid}/datastreams/{dsID}")
    @Produces(MimeTypes.ALL)
    @Consumes(MimeTypes.TEXT_XML)
    DatastreamProfileTO addDatastream(
        @NotNull @PathParam("pid") String pid, @NotNull @PathParam("dsID") String dsID, @QueryParam("controlGroup") String controlGroup,
        @QueryParam("dsLocation") String dsLocation,@QueryParam("altIDs") List<String> altIDs,
        @QueryParam("dsLabel") String dsLabel,@QueryParam("versionable") Boolean versionable,
        @QueryParam("dsState") String dsState,@QueryParam("formatURI") String formatURI,
        @QueryParam("checksumType") String checksumType,@QueryParam("checksum") String checksum,
        @QueryParam("mimeType") String mimeType,@QueryParam("logMessage") String logMessage,
        Stream inputStream);

    @GET
    @Path("/objects/{pid}/datastreams/{dsID}/content")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.ALL)
    Stream getDatastream(
        @NotNull @PathParam("pid") String pid, @NotNull @PathParam("dsID") String dsID, @QueryParam("asOfDateTime") String asOfDateTime,
        @QueryParam("download") String download);

    @GET
    @Path("/objects/{pid}/datastreams/{dsID}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    DatastreamProfileTO getDatastreamProfile(
        @NotNull @PathParam("pid") String pid, @NotNull @PathParam("dsID") String dsID,
        @QueryParam("asOfDateTime") String asOfDateTime,
        @QueryParam("format") String format,
        @QueryParam("validateChecksum") String validateChecksum);

    @PUT
    @Path("/objects/{pid}/datastreams/{dsID}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.ALL)
    DatastreamProfileTO modifyDatastream(
        @NotNull @PathParam("pid") String pid, @NotNull @PathParam("dsID") String dsID,
        @QueryParam("dsLocation") String dsLocation,
        @QueryParam("dsLabel") String dsLabel,@QueryParam("altIDs") List<String> altIDs,
        @QueryParam("versionable") Boolean versionable,
        @QueryParam("dsState") String dsState,@QueryParam("formatURI") String formatURI,
        @QueryParam("checksumType") String checksumType,
        @QueryParam("mimeType") String mimeType,@QueryParam("logMessage") String logMessage,
        @QueryParam("ignoreContent") Boolean ignoreContent,@QueryParam("lastModifiedDate") String lastModifiedDate,@NotNull Stream stream);

    @DELETE
    @Path("/objects/{pid}/datastreams/{dsID}")
    @Consumes(MimeTypes.APPLICATION_JSON)
    void deleteDatastream(
        @NotNull @PathParam("pid") String pid, @NotNull @PathParam("dsID") String dsID,
        @QueryParam("startDT") String startDT,
        @QueryParam("endDT") String endDT, @QueryParam("logMessage") String logMessage);

    @GET
    @Path("/objects/{pid}/datastreams")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    ObjectDatastreamsTO listDatastreams(
        @NotNull @PathParam("pid") String pid, @QueryParam("asOfDateTime") String asOfDateTime,
        @QueryParam("format") String format);

    @GET
    @Path("/objects/{pid}/datastreams/infolist")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    DatastreamProfilesTO listProfiles(
        @NotNull @PathParam("pid") String pid,
        @QueryParam("asOfDateTime") String asOfDateTime,
        @QueryParam("format") String format);

    @GET
    @Path("/objects/{pid}/datastreams/{dsID}/history")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    DatastreamHistoryTO getDatastreamHistory(
        @NotNull @PathParam("pid") String pid, @NotNull @PathParam("dsID") String dsID,
        @QueryParam("format") String format);

    @GET
    @Encoded
    @Path("/risearch")
    @Produces(MimeTypes.ALL)
    @Consumes(MimeTypes.ALL)
    Stream risearch(@QueryParam("type") String type,
        @QueryParam("lang") String lang,@QueryParam("format") String format,
        @QueryParam("query") String query,
        @QueryParam("flush") String flush);

    @GET
    @Path("/get/{pid}/{dsID}/{versionDate}")
    @Produces(MimeTypes.ALL)
    @Consumes(MimeTypes.ALL)
    Stream getBinaryContent(
        @NotNull @PathParam("pid") String pid, @NotNull @PathParam("dsID") String dsID,
        @NotNull @PathParam("versionDate") String versionDate);

    @GET
    @Path("/objects/{pid}/methods/{sdefPid}/{method}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    Stream getDissemination(
        @NotNull @PathParam("pid") String pid, @NotNull @PathParam("sdefPid") String sdefPid,
        @NotNull @PathParam("method") String method);

}