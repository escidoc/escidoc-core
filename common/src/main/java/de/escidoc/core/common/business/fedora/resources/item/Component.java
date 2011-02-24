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
package de.escidoc.core.common.business.fedora.resources.item;

import de.escidoc.core.common.business.fedora.Constants;
import de.escidoc.core.common.business.fedora.Triple;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.GenericResourcePid;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.interfaces.ComponentInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.DcReadHandler;
import de.escidoc.core.common.util.stax.handler.RelsExtReadHandler;
import de.escidoc.core.common.util.stax.handler.item.ComponentPropertiesUpdateHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.renderer.VelocityXmlItemFoXmlRenderer;
import de.escidoc.core.common.util.xml.renderer.interfaces.ItemFoXmlRendererInterface;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Component resource of eSciDoc.
 * 
 * @author FRS
 * 
 */
public class Component extends GenericResourcePid implements ComponentInterface {

    private static final AppLogger log = new AppLogger(Component.class.getName());

    private Datastream dc = null;

    private Map<String, Datastream> mdRecords = null;

    private String parent = null;

    private Datastream content;

    private String parentVersionDate = null;

    private ItemFoXmlRendererInterface foxmlRenderer = null;

    /**
     * Constructs the Component with the specified id from the Repository. The
     * datastreams are instantiated and retrieved if the related getter is
     * called.
     * 
     * @param id
     *            The id of an item managed in Fedora.
     * @param parentId
     *            The id of the parent object.
     * @param timestamp
     *            The timestamp which specifies the version of the datastreams
     *            to retrieve.
     * @throws SystemException
     *             Thrown in case of an internal system error.
     * @throws ResourceNotFoundException
     *             Thrown if the Component resource was not found.
     */
    public Component(final String id, final String parentId,
        final String timestamp) throws SystemException,
        ResourceNotFoundException {

        super(id);

        this.parent = parentId;
        this.parentVersionDate = timestamp;
        setHref(Constants.COMPONENT_URL_PART + id);

        initDatastreams();
        getSomeValuesFromFedora();

        if (!checkResourceType(ResourceType.COMPONENT)) {
            throw new ItemNotFoundException(
                "Component with the provided objid '" + id + "' does not exit.");
        }

    }

    /**
     * Init all Datastreams of the Component.
     * 
     * @throws FedoraSystemException
     *             Thrown if access to Repository or retrieve failed.
     */
    private void initDatastreams() throws FedoraSystemException {

        // initialize datastreams with Fedora Datastream Informations
        org.fcrepo.server.types.gen.Datastream[] datastreamInfos =
            getFedoraUtility().getDatastreamsInformation(getId(),
                this.parentVersionDate);

        this.mdRecords = new HashMap<String, Datastream>();

        for (org.fcrepo.server.types.gen.Datastream datastreamInfo : datastreamInfos) {

            List<String> altIDs = Arrays.asList(datastreamInfo.getAltIDs());
            String name = datastreamInfo.getID();
            String label = datastreamInfo.getLabel();
            String controlGroupValue =
                    datastreamInfo.getControlGroup().getValue();
            String mimeType = datastreamInfo.getMIMEType();
            String location = datastreamInfo.getLocation();

            Datastream ds;
            if (altIDs.contains(Datastream.METADATA_ALTERNATE_ID)) {
                // found md-record
                ds =
                        new Datastream(name, getId(), this.parentVersionDate,
                                mimeType, location, controlGroupValue);
                ds.setAlternateIDs(new ArrayList<String>(altIDs));
                ds.setLabel(label);
                this.mdRecords.put(name, ds);
            } else {
                // RELS-EXT
                if (name.equals(Datastream.RELS_EXT_DATASTREAM)) {
                    ds =
                            new Datastream(name, getId(), this.parentVersionDate,
                                    mimeType, location, controlGroupValue);
                    ds.setAlternateIDs(new ArrayList<String>(altIDs));
                    ds.setLabel(label);
                    this.relsExt = ds;
                }
                // DC
                else if ("DC".equals(name)) {
                    ds =
                            new Datastream(name, getId(), this.parentVersionDate,
                                    mimeType, location, controlGroupValue);
                    ds.setAlternateIDs(new ArrayList<String>(altIDs));
                    ds.setLabel(label);
                    this.dc = ds;
                }
                // content
                else if ("content".equals(name)) {
                    ds =
                            new Datastream(name, getId(), this.parentVersionDate,
                                    mimeType, location, controlGroupValue,
                                    datastreamInfo.getChecksumType(),
                                    datastreamInfo.getChecksum());
                    ds.setAlternateIDs(new ArrayList<String>(altIDs));
                    ds.setLabel(label);
                    this.content = ds;
                } else {
                    log.warn("Datastream " + getId() + '/' + name
                            + " not instanziated in Item.<init>.");
                }
            }

        }
    }

    /**
     * Retrieving some values from Fedora & TripleStore and keep it in internal
     * HashMap.
     * 
     * @throws TripleStoreSystemException
     *             Thrown if request to TripleStore failed.
     * @throws XmlParserSystemException
     *             Thrown if parsing of RELS-EXT failed.
     * @throws WebserverSystemException
     *             Thrown if request to TripleStore failed.
     */
    private void getSomeValuesFromFedora() throws TripleStoreSystemException,
        WebserverSystemException, XmlParserSystemException {

        Map<String, String> properties = new HashMap<String, String>();
        properties.putAll(obtainRelsExtValues());

        final StaxParser sp = new StaxParser();
        final DcReadHandler dch = new DcReadHandler(sp);
        sp.addHandler(dch);
        try {
            sp.parse(new ByteArrayInputStream(this.getDc().getStream()));
        }
        catch (final Exception e) {
            String msg = "Unexpected exception during DC datastream parsing.";
            log.info(msg + e);
            throw new XmlParserSystemException(msg, e);
        }

        properties.put(de.escidoc.core.common.business.Constants.DC_NS_URI
            + Elements.ELEMENT_DC_TITLE,
            dch.getPropertiesMap().get(Elements.ELEMENT_DC_TITLE));

        final String description =
            dch.getPropertiesMap().get(Elements.ELEMENT_DESCRIPTION);
        if ((description != null) && !(description.length() == 0)) {
            properties.put(de.escidoc.core.common.business.Constants.DC_NS_URI
                + Elements.ELEMENT_DESCRIPTION, description);
        }

        // }

        String title =
            properties.get(de.escidoc.core.common.business.Constants.DC_NS_URI
                + Elements.ELEMENT_DC_TITLE);
        if ((title == null) || title.length() == 0) {
            setTitle("Component " + getId());
        }
        else {
            setTitle(title);
        }

        properties.put(Elements.ELEMENT_COMPONENT_CONTENT_CHECKSUM_ALGORITHM,
            this.content.getChecksumMethod());
        properties.put(Elements.ELEMENT_COMPONENT_CONTENT_CHECKSUM,
            this.content.getChecksum());
        addResourceProperties(properties);
    }

    /**
     * Get Content as Datastream.
     * 
     * @return content
     * @throws StreamNotFoundException
     * @throws FedoraSystemException
     */
    public Datastream getContent() {
        return this.content;
    }

    public String getChecksum() throws TripleStoreSystemException,
        WebserverSystemException {
        return getResourcePropertiesValue(Elements.ELEMENT_COMPONENT_CONTENT_CHECKSUM);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.common.business.fedora.resources.GenericResource#setRelsExt
     * (de.escidoc.core.common.business.fedora.datastream.Datastream)
     */
    @Override
    public void setRelsExt(final Datastream ds) throws FedoraSystemException,
        WebserverSystemException {

        super.setRelsExt(ds);
        if (this.isNeedSync()) {
            getFedoraUtility().touchObject(this.parent, true);
        }
    }

    public void setContent(final Datastream ds) {
        throw new UnsupportedOperationException("Component.setContent.");
    }

    public void notifySetContent() throws FedoraSystemException,
        WebserverSystemException, TripleStoreSystemException {

        getFedoraUtility().touchObject(this.parent, true);
        this.content = null;
    }

    public Datastream getLicenses() {
        throw new UnsupportedOperationException(
            "Licenses are not yet available.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.ComponentInterface
     * #getMdRecords()
     */
    @Override
    public Map<String, Datastream> getMdRecords() {
        return this.mdRecords;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.ComponentInterface
     * #setMdRecords(java.util.HashMap)
     */
    @Override
    public void setMdRecords(final Map<String, Datastream> ds)
        throws FedoraSystemException, WebserverSystemException,
        EncodingSystemException, IntegritySystemException {
        // compare
        // with
        // Item.setMdRecords
        // check if mdRecords is set, contains all metadata
        // datastreams, is equal to given mdRecords and save every
        // changed datastream to fedora
        boolean modified = false;
        // get list of names of data streams with alternateId = "metadata"
        final Set<String> namesInFedora = getMdRecords().keySet();
        // delete data streams which are in fedora but not in mdRecords
        for (String nameInFedora : namesInFedora) {
            if (!ds.containsKey(nameInFedora)) {
                Datastream fedoraDs = getMdRecord(nameInFedora);
                fedoraDs.delete();
                if ("escidoc".equals(fedoraDs.getName())) {
                    // Datastream dcDs = getDc();
                    final ItemFoXmlRendererInterface iri =
                            new VelocityXmlItemFoXmlRenderer();
                    final String dcContent = iri.renderDefaultDc(getId());
                    Datastream newDc;
                    try {
                        newDc =
                                new Datastream("DC", getId(),
                                        dcContent
                                                .getBytes(XmlUtility.CHARACTER_ENCODING),
                                        "text/xml");
                    } catch (final UnsupportedEncodingException e) {
                        throw new EncodingSystemException(e);
                    }
                    setDc(newDc);
                }
                modified = true;
            }
        }
        
        // FIXME: What is mdRecordsToAdd for?
        final Map<String, Datastream> mdRecordsToAdd =
            new HashMap<String, Datastream>();
        
        final Iterator<Entry<String, Datastream>> nameIt = ds.entrySet().iterator();
        // create/activate data streams which are in mdRecords but not in fedora
        while (nameIt.hasNext()) {
            Entry<String, Datastream> mapEntry = nameIt.next();
            final String name = mapEntry.getKey();
            final Datastream currentMdRecord = mapEntry.getValue();
            setMdRecord(name, currentMdRecord);
            if (!namesInFedora.contains(name)) {
                nameIt.remove();
            }
        }

        if (modified) {
            getFedoraUtility().touchObject(this.parent, true);
        }
        this.mdRecords.putAll(mdRecordsToAdd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.ComponentInterface
     * #getMdRecord(java.lang.String)
     */
    @Override
    public Datastream getMdRecord(final String name) {
        return this.mdRecords.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.ComponentInterface
     * #setMdRecord(java.lang.String,
     * de.escidoc.core.common.business.fedora.datastream.Datastream)
     */
    @Override
    public void setMdRecord(final String name, final Datastream ds)
        throws WebserverSystemException, EncodingSystemException,
        IntegritySystemException, FedoraSystemException {
        // check if the metadata datastream is set, is equal to ds and save to
        // fedora

        // don't trust the handler
        // ds.addAlternateId("metadata");
        final String type = ds.getAlternateIDs().get(1);
        final String schema = ds.getAlternateIDs().get(2);
        String mimeType = ds.getMimeType();
        try {
            boolean contentChanged = false;
            boolean isNew = false;

            final Datastream curDs = getMdRecord(name);
            if (curDs == null) {
                isNew = true;
            }
            else {
                String curMimeType = curDs.getMimeType();
                String curType = "";
                String curSchema = "";
                final List<String> altIds = curDs.getAlternateIDs();
                if (altIds.size() > 1) {
                    curType = altIds.get(1);
                    if (altIds.size() > 2) {
                        curSchema = altIds.get(2);
                    }
                }
                if (!ds.equals(curDs) || !type.equals(curType)
                    || !schema.equals(curSchema)
                    || !mimeType.equals(curMimeType)) {
                    contentChanged = true;
                }
            }

            if (contentChanged || isNew) {
                if ("escidoc".equals(name)) {

                    final Map<String, String> mdProperties =
                        ds.getProperties();
                    if (mdProperties != null) {
                        // if (mdProperties.get("nsUri") != null) {
                        if (mdProperties.containsKey("nsUri")) {
                            final String nsUri = mdProperties.get("nsUri");
                            // FIXME get content model ID from Item Object (see
                            // Item. and Container.setMdRecord)

                            // no content model id for component dc-mapping,
                            // default mapping
                            // should be applied
                            String dcNewContent =
                                XmlUtility.createDC(nsUri, ds.toStringUTF8(),
                                    getId(), null);
                            if (dcNewContent != null
                                && dcNewContent.trim().length() > 0) {
                                Datastream dcNew;
                                try {
                                    dcNew =
                                        new Datastream(
                                            "DC",
                                            getId(),
                                            dcNewContent
                                                .getBytes(XmlUtility.CHARACTER_ENCODING),
                                            "text/xml");
                                }
                                catch (final UnsupportedEncodingException e) {
                                    throw new EncodingSystemException(e);
                                }
                                setDc(dcNew);
                            }
                        }
                        else {
                            final String message =
                                "namespace uri of 'escidoc' metadata"
                                    + " does not set in datastream.";

                            log.error(message);
                            throw new IntegritySystemException(message);
                        }
                    }
                    else {
                        final String message =
                            "Properties of 'md-record' datastream"
                                + " with then name 'escidoc' does not exist";
                        log.error(message);
                        throw new IntegritySystemException(message);
                    }
                }

                // isNew does not indicate that the datastream does not exist
                // in fedora, it may be deleted
                this.mdRecords.put(name, ds);
                ds.merge();
                getFedoraUtility().touchObject(this.parent, true);
            }
        }
        catch (final FedoraSystemException e) {
            // this is not an update; its a create
            ds.addAlternateId(type);
            ds.addAlternateId(schema);
            this.mdRecords.put(name, ds);
            ds.persist(false);
            getFedoraUtility().touchObject(this.parent, true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.FedoraResource
     * #setRelsExt(de.escidoc.core.common.business.fedora.datastream.Datastream)
     */
    public void setDc(final Datastream ds) throws FedoraSystemException,
        WebserverSystemException {
        final Datastream curDs = getDc();
        if (!ds.equals(curDs)) {
            this.dc = ds;
            ds.merge();
            // FedoraUtility.getInstance().sync();
            getFedoraUtility().touchObject(this.parent, true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.FedoraResource
     * #getRelsExt()
     */
    public Datastream getDc() {

        return this.dc;
    }

    /**
     * @deprecated This href does not contain the items version number. Use
     *             Item.getHref() + Component.getHrefPart() instead.
     * 
     * @return the href
     */
    @Override
    @Deprecated
    public String getHref() {
        return de.escidoc.core.common.business.Constants.ITEM_URL_BASE
            + this.parent + super.getHref();
    }

    /**
     * 
     * @return the components part of the href, after the item id
     */
    public String getHrefPart() {
        return super.getHref();
    }

    /**
     * Set the Component properties. ! Side effect: the RELS-EXT of the
     * Component is updated with the new obtained values.
     * 
     * Once meant as setter-like method. Compare to setMdRecord, setMdRecords,
     * setContent (FRS).
     * 
     * @param xml
     *            The XML with properties section of Component.
     * @param itemId
     *            The id of the Item.
     * @return Map of Component properties.
     * 
     * @throws InvalidContentException
     * @throws ComponentNotFoundException
     * @throws SystemException
     */
    public Map<String, String> setProperties(
        final String xml, final String itemId) throws InvalidContentException,
        ComponentNotFoundException, SystemException {

        StaxParser sp = new StaxParser();
        ComponentPropertiesUpdateHandler cpuh =
            new ComponentPropertiesUpdateHandler(getResourceProperties(), '/'
                + Elements.ELEMENT_PROPERTIES, sp);
        sp.addHandler(cpuh);
        try {
            sp.parse(xml);
        }
        catch (InvalidContentException e) {
            throw e;
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        Map<String, String> properties = cpuh.getProperties();
        setRelsExt(getFoxmlRenderer().renderComponentRelsExt(getId(),
            properties, false));

        return properties;
    }

    /**
     * 
     * @return The foxml renderer.
     */
    public ItemFoXmlRendererInterface getFoxmlRenderer() {

        if (this.foxmlRenderer == null) {
            this.foxmlRenderer = new VelocityXmlItemFoXmlRenderer();
        }
        return this.foxmlRenderer;
    }

    /**
     * Obtains values from RelsExt.
     * 
     * @return map with predictate and objects
     * @throws XmlParserSystemException
     *             Thrown if parse of RELS-EXT failed.
     */
    private Map<String, String> obtainRelsExtValues()
        throws XmlParserSystemException {

        final StaxParser sp = new StaxParser();

        final RelsExtReadHandler eve = new RelsExtReadHandler(sp);
        eve.cleanIdentifier(true);
        sp.addHandler(eve);
        try {
            sp.parse(getRelsExt().getStream());
        }
        catch (final IntegritySystemException e) {
            String msg = "Unexpected exception during RELS-EXT parsing.";
            log.warn(msg + e);
            throw new XmlParserSystemException(e);
        }
        catch (final Exception e) {
            String msg = "Unexpected exception during RELS-EXT parsing.";
            log.warn(msg + e);
            throw new XmlParserSystemException(msg, e);
        }
        final Map<String, String> properties = new HashMap<String, String>();
        final List<Triple> triples = eve.getElementValues().getTriples();
        for (Triple triple : triples) {
            properties.put(triple.getPredicate(), triple.getObject());
        }
        return properties;
    }
}
