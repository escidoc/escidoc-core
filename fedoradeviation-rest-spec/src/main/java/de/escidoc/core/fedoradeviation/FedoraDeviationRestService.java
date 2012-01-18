/**
 * 
 */
package de.escidoc.core.fedoradeviation;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.escidoc.core.utils.io.EscidocBinaryContent;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Michael Hoppe
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface FedoraDeviationRestService {

    /**
     * Overwrites the Fedora Method-Call export. Variable pid contains uri to resource. Calls Method-mapper with given
     * uri to retrieve object as xml. return xml-string as byte[].
     *
     * @param id        uri to the resource.
     * @return String with the fedora-object as escidoc-xml
     * @throws SystemException ex
     */
    @GET
    @Path("/objects/{id}/export")
    String export(@PathParam("id") String id) throws SystemException;


    /**
     * Overwrites the Fedora Method-Call getDatastreamDissemination. Variable dsID contains uri to component-content .
     * Calls Method-mapper with given uri to retrieve content as byte[]. Fill EscidocBinaryContent with byte[] and
     * mime-type.
     *
     * @param id        unused.
     * @param dsId       uri to component-content
     * @return EscidocBinaryContent escidocBinaryContent
     * @throws SystemException ex
     */
    @GET
    @Path("/objects/{id}/datastreams/{ds-id}/content")
    EscidocBinaryContent getDatastreamDissemination(@PathParam("id") String id, @PathParam("ds-id") String dsId)
        throws SystemException;

    /**
     * Overwrites the Fedora http-Call /describe. Executes http-request to /describe and returns String.
     *
     * @param xml request parameters.
     * @return String response
     * @throws Exception ex
     */
    @GET
    @Path("/describe")
    String getFedoraDescription(@QueryParam("xml") String xml) throws Exception;

}
