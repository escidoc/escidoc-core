/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
/**
 * 
 */
package de.escidoc.core.common.util.stax;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.xml.stream.XMLStreamException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This parser should be able to insert event in the handlerchain. Untested!
 * Note: This parser does not check if the added events are in a correct order
 * (StartElement than characters than EndElement).
 * 
 * @author FRS
 * 
 */
public class StaxAddParser extends StaxParser {

    private final Queue<Object> adds = new LinkedBlockingQueue<Object>();

    /**
     * Add Event object.
     * 
     * @param event
     *            TODO
     */
    public void addEvent(final Object event) {
        adds.offer(event);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.common.util.stax.StaxParser#handle(de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    protected void handle(final StartElement startElement)
        throws XMLStreamException, ContentModelNotFoundException,
        ContextNotFoundException, LockingException,
        MissingAttributeValueException, ReadonlyAttributeViolationException,
        ReadonlyElementViolationException, MissingContentException,
        InvalidContentException, OptimisticLockingException,
        AlreadyExistsException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException,
        OrganizationalUnitNotFoundException, TripleStoreSystemException,
        WebserverSystemException, EncodingSystemException,
        XmlParserSystemException, IntegritySystemException, TmeException,
        XmlCorruptedException {
        StartElement element = startElement;
        int chainSize = getHandlerChain().size();
        for (int i = 0; i < chainSize; i++) {
            DefaultHandler handler = getHandlerChain().get(i);
            if (handler != null) {
                try {
                    // pause handlerChain and
                    // send added events to next handlers
                    while (!adds.isEmpty()) {
                        Object event = adds.poll();
                        for (int j = i; j < chainSize; j++) {
                            DefaultHandler extraHandler =
                                getHandlerChain().get(i);
                            if (extraHandler != null) {
                                if (event instanceof StartElement) {
                                    event =
                                        extraHandler
                                            .startElement((StartElement) event);
                                }
                                else if (event instanceof String) {
                                    event =
                                        extraHandler.characters((String) event,
                                            null);
                                }
                                else if (event instanceof EndElement) {
                                    event =
                                        extraHandler
                                            .endElement((EndElement) event);
                                }
                            }
                        }
                    }
                    // go ahead with handlerChain
                    element = handler.startElement(element);
                }
                catch (ContentModelNotFoundException ctnf) {
                    throw ctnf;
                }
                catch (ContextNotFoundException cnf) {
                    throw cnf;
                }

                catch (XMLStreamException xse) {
                    throw xse;
                }
                catch (InvalidContentException ice) {
                    throw new XMLStreamException(ice.getMessage(), ice);
                }
                catch (LockingException le) {
                    throw le;
                }
                catch (Exception ex) {
                    // should be catched before
                    throw new WebserverSystemException(ex);
                }
            }
        }
        super.handle(startElement);
    }

}
