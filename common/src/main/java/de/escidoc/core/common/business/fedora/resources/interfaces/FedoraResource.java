/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

/**
 *
 */
package de.escidoc.core.common.business.fedora.resources.interfaces;

import org.joda.time.DateTime;

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;

/**
 * Interface for Fedora Object which consist of datastreams managed in Fedora
 * Digital Repository System.
 * 
 * @author Frank Schwichtenberg
 */
public interface FedoraResource {

    /**
     * Gets the unique id of the fedora object which is represented by this
     * object. The id contains no version specific part.
     * 
     * @return The unique Fedora id.
     */
    String getId();

    /**
     * Get the href of the Feodra object.
     * 
     * @return The href.
     */
    String getHref();

    /**
     * Get RELS-EXT datastream.
     * 
     * @return RELS-EXT
     * @throws StreamNotFoundException
     *             Thrown if datastream with name RELS-EXT doesn't exists within
     *             the Fedora object.
     * @throws FedoraSystemException
     *             Thrown if connection or request to Fedora failed.
     */
    Datastream getRelsExt() throws StreamNotFoundException, FedoraSystemException;

    /**
     * Set RELS-EXT datastream.
     * 
     * @param ds
     *            The new RELS-EXT datastream.
     * @throws StreamNotFoundException
     *             Thrown if datastream with name RELS-EXT doesn't exists within
     *             the Fedora object.
     * @throws LockingException
     *             Thrown if Resource is locked.
     * @throws FedoraSystemException
     *             Thrown if connection or request to Fedora failed.
     * @throws TripleStoreSystemException
     *             Thrown if requesting TripleStore failed.
     * @throws XmlParserSystemException
     *             Thrown if parsing of RELS-EXT datastream failed.
     * @throws WebserverSystemException
     *             In case of internal errors.
     */
    void setRelsExt(Datastream ds) throws StreamNotFoundException, LockingException, FedoraSystemException,
        TripleStoreSystemException, XmlParserSystemException, WebserverSystemException;

    /**
     * Set RELS-EXT datastream.
     * 
     * @param ds
     *            The new RELS-EXT datastream.
     * @throws StreamNotFoundException
     *             Thrown if datastream with name RELS-EXT doesn't exists within
     *             the Fedora object.
     * @throws LockingException
     *             Thrown if Resource is locked.
     * @throws FedoraSystemException
     *             Thrown if connection or request to Fedora failed.
     * @throws TripleStoreSystemException
     *             Thrown if requesting TripleStore failed.
     * @throws XmlParserSystemException
     *             Thrown if parsing of RELS-EXT datastream failed.
     * @throws WebserverSystemException
     *             In case of internal errors.
     */
    void setRelsExt(final byte[] ds) throws StreamNotFoundException, LockingException, FedoraSystemException,
        WebserverSystemException, TripleStoreSystemException, XmlParserSystemException;

    /**
     * Set RELS-EXT datastream.
     * 
     * @param ds
     *            The new RELS-EXT datastream.
     * @throws StreamNotFoundException
     *             Thrown if datastream with name RELS-EXT doesn't exists within
     *             the Fedora object.
     * @throws EncodingSystemException
     *             Thrown if String contains invalid characters for the default
     *             character set.
     * @throws LockingException
     *             Thrown if Resource is locked.
     * @throws FedoraSystemException
     *             Thrown if connection or request to Fedora failed.
     * @throws TripleStoreSystemException
     *             Thrown if requesting TripleStore failed.
     * @throws XmlParserSystemException
     *             Thrown if parsing of RELS-EXT datastream failed.
     * @throws WebserverSystemException
     *             In case of internal errors.
     */
    void setRelsExt(final String ds) throws StreamNotFoundException, LockingException, FedoraSystemException,
        TripleStoreSystemException, XmlParserSystemException, EncodingSystemException, WebserverSystemException;

    /**
     * Get the last modification date of the Fedora object. This value differs
     * from the last modification date of the resource.
     * 
     * @return last modification date (Fedora object)
     * @throws TripleStoreSystemException
     *             Thrown in case of TripleStore failures.
     * @throws FedoraSystemException
     *             Thrown in case of Fedora failures.
     * @throws WebserverSystemException
     *             Thrown in case of internal errors.
     */
    DateTime getLastFedoraModificationDate() throws TripleStoreSystemException, FedoraSystemException,
        WebserverSystemException;
}
