package de.escidoc.core.common.business.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;

/**
 * 
 * This interface marks all extending interfaces as "ingestable", this is a
 * means of integrating all handlers so that they all comply to this contract.
 * 
 * @author KST
 * 
 */
public interface IngestableResource {

    /**
     * Ingest a resource consisting of an xml string.
     * 
     * @param xmlData
     *            XML representation of the resource.
     * @return Returns the identifier given to this particular resource.
     * @throws EscidocException
     *             Any exception within the Escidoc realm. This is necessary due
     *             to the different handlers all throwing different exceptions
     *             for the same operation.
     * 
     */
    String ingest(String xmlData) throws EscidocException;

}
