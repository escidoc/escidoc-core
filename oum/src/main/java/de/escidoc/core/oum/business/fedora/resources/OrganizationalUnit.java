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
package de.escidoc.core.oum.business.fedora.resources;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.GenericResource;
import de.escidoc.core.common.business.fedora.resources.Predecessor;
import de.escidoc.core.common.business.fedora.resources.PredecessorForm;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.oum.business.fedora.resources.interfaces.OrganizationalUnitInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Resource implementation of an organizational unit resource.
 * 
 * @author MSC
 * 
 */
public class OrganizationalUnit extends GenericResource
    implements OrganizationalUnitInterface {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationalUnit.class);

    public static final String ESCIDOC = "escidoc";

    public static final String NS_URI = "nsUri";

    private String name;

    private String description;

    private String publicStatus;

    private String createdByTitle;

    private String modifiedBy;

    private String modifiedByTitle;

    private List<String> parents = null;

    private List<Predecessor> predecessors = null;

    private List<Predecessor> successors = null;

    private boolean hasChildren;

    /**
     * Constructs the Context with the specified id. The datastreams are
     * instantiated and retrieved if the related getter is called.
     * 
     * @param id
     *            The id of an organizational unit managed in Fedora.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @throws ResourceNotFoundException
     *             Thrown if no organizational resource could be found under the
     *             provided id.
     */
    public OrganizationalUnit(final String id) throws SystemException,
        ResourceNotFoundException {

        super(id);
        if (id != null) {
            Utility.getInstance().checkIsOrganizationalUnit(id);
        }
        setHref(Constants.ORGANIZATIONAL_UNIT_URL_BASE + id);
        getSomeValuesFromFedora();
    }

    /**
     * Retrieve a property value from the triplestore.
     * 
     * @param property
     *            The name of the expected property.
     * @return The retrieved value of the property.
     * @throws TripleStoreSystemException
     *             If access to the triplestore fails.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    private String getPropertyFromTriplestore(final String property)
        throws TripleStoreSystemException, WebserverSystemException {

        return TripleStoreUtility.getInstance().getPropertiesElements(getId(),
            property);
    }

    /**
     * Get the values of the properties stored in RELS-EXT datastream. If
     * possible retrieve them directly from the triplestore.
     * 
     * @throws TripleStoreSystemException
     *             Thrown if access to TripleStore failed.
     * @throws WebserverSystemException
     *             If access to the backend (fedora or triplestore) fails.
     */
    protected void getSomeValuesFromFedora() throws TripleStoreSystemException,
        WebserverSystemException {

        // this.creationDate = getTripleStoreUtility().getCreationDate(getId());
        // this.createdBy =
        // getPropertyFromTriplestore(TripleStoreUtility.PROP_CREATED_BY_ID);
        this.createdByTitle =
            getPropertyFromTriplestore(TripleStoreUtility.PROP_CREATED_BY_TITLE);

        // this.lastModificationDate =
        // getTripleStoreUtility().getLastModificationDate(getId());
        this.modifiedBy =
            getPropertyFromTriplestore(TripleStoreUtility.PROP_MODIFIED_BY_ID);
        this.modifiedByTitle =
            getPropertyFromTriplestore(TripleStoreUtility.PROP_MODIFIED_BY_TITLE);
        this.publicStatus =
            getPropertyFromTriplestore(TripleStoreUtility.PROP_PUBLIC_STATUS);

        hasChildren = !TripleStoreUtility.getInstance().getChildren(getId()).isEmpty();
        this.name = TripleStoreUtility.getInstance().getTitle(getId());
        this.description =
            TripleStoreUtility.getInstance().getDescription(getId());
        this.parents = TripleStoreUtility.getInstance().getParents(getId());
        this.predecessors = getPredecessors(getId());

    }

    /**
     * Get list of predecessors of OU.
     * 
     * @param ouId
     *            Id of Organizational Unit.
     * @return List of predecessors for the selected OU.
     * @throws TripleStoreSystemException
     *             Thrown if request TripleStore failed.
     */
    public List<Predecessor> getPredecessors(final String ouId)
        throws TripleStoreSystemException {

        List<Predecessor> predecessors = new ArrayList<Predecessor>();

        try {
            // collect affiliations
            List<String> pred =
                TripleStoreUtility.getInstance().executeQueryId(ouId, false,
                    Constants.PREDECESSOR_AFFILIATION);
            Iterator<String> it = pred.iterator();
            while (it.hasNext()) {
                predecessors.add(new Predecessor(it.next(),
                    PredecessorForm.AFFILIATION));
            }

            // collect fusion
            pred =
                TripleStoreUtility.getInstance().executeQueryId(ouId, false,
                    Constants.PREDECESSOR_FUSION);
            it = pred.iterator();
            while (it.hasNext()) {
                predecessors.add(new Predecessor(it.next(),
                    PredecessorForm.FUSION));
            }

            // collect replacement
            pred =
                TripleStoreUtility.getInstance().executeQueryId(ouId, false,
                    Constants.PREDECESSOR_REPLACEMENT);
            it = pred.iterator();
            while (it.hasNext()) {
                predecessors.add(new Predecessor(it.next(),
                    PredecessorForm.REPLACEMENT));
            }

            // collect spin-off
            pred =
                TripleStoreUtility.getInstance().executeQueryId(ouId, false,
                    Constants.PREDECESSOR_SPIN_OFF);
            it = pred.iterator();
            while (it.hasNext()) {
                predecessors.add(new Predecessor(it.next(),
                    PredecessorForm.SPIN_OFF));
            }

            // collect splitting
            pred =
                TripleStoreUtility.getInstance().executeQueryId(ouId, false,
                    Constants.PREDECESSOR_SPLITTING);
            it = pred.iterator();
            while (it.hasNext()) {
                predecessors.add(new Predecessor(it.next(),
                    PredecessorForm.SPLITTING));
            }

        }
        catch (WebserverSystemException wse) {
            throw new TripleStoreSystemException(wse);
        }
        return predecessors;
    }

    /**
     * Get list of successors of OU.
     * 
     * @param ouId
     *            Id of Organizational Unit.
     * @return List of successors for the selected OU.
     * @throws TripleStoreSystemException
     *             Thrown if request TripleStore failed.
     */
    public List<Predecessor> getSuccessors(final String ouId)
        throws TripleStoreSystemException {

        List<Predecessor> successors = new ArrayList<Predecessor>();
        List<String> ids = new ArrayList<String>();
        ids.add(ouId);

        try {
            // collect affiliations
            List<String> pred =
                TripleStoreUtility.getInstance().executeQueryForList(ids, true,
                    Constants.PREDECESSOR_AFFILIATION);
            Iterator<String> it = pred.iterator();
            while (it.hasNext()) {
                successors.add(new Predecessor(XmlUtility.getIdFromURI(it
                    .next()), PredecessorForm.AFFILIATION));
            }

            // collect fusion
            pred =
                TripleStoreUtility.getInstance().executeQueryForList(ids, true,
                    Constants.PREDECESSOR_FUSION);
            it = pred.iterator();
            while (it.hasNext()) {
                successors.add(new Predecessor(XmlUtility.getIdFromURI(it
                    .next()), PredecessorForm.FUSION));
            }

            // collect replacement
            pred =
                TripleStoreUtility.getInstance().executeQueryForList(ids, true,
                    Constants.PREDECESSOR_REPLACEMENT);
            it = pred.iterator();
            while (it.hasNext()) {
                successors.add(new Predecessor(XmlUtility.getIdFromURI(it
                    .next()), PredecessorForm.REPLACEMENT));
            }

            // collect spin-off
            pred =
                TripleStoreUtility.getInstance().executeQueryForList(ids, true,
                    Constants.PREDECESSOR_SPIN_OFF);
            it = pred.iterator();
            while (it.hasNext()) {
                successors.add(new Predecessor(XmlUtility.getIdFromURI(it
                    .next()), PredecessorForm.SPIN_OFF));
            }

            // collect splitting
            pred =
                TripleStoreUtility.getInstance().executeQueryForList(ids, true,
                    Constants.PREDECESSOR_SPLITTING);
            it = pred.iterator();
            while (it.hasNext()) {
                successors.add(new Predecessor(XmlUtility.getIdFromURI(it
                    .next()), PredecessorForm.SPLITTING));
            }

        }
        catch (WebserverSystemException wse) {
            throw new TripleStoreSystemException(wse);
        }
        return successors;
    }

    /**
     * @return the createdByTitle
     */
    public String getCreatedByTitle() {
        return createdByTitle;
    }

    /**
     * @param createdByTitle
     *            the createdByTitle to set
     */
    public void setCreatedByTitle(final String createdByTitle) {
        this.createdByTitle = createdByTitle;
    }

    /**
     * @return the hasChildren
     */
    public boolean hasChildren() {
        return hasChildren;
    }

    /**
     * Get the list of children ids for this organizational unit from the
     * triplestore.
     * 
     * @return The list of children ids for this organizational unit.
     * @throws SystemException
     *             If access to the triplestore fails.
     */
    public List<String> getChildrenIds() throws SystemException {

        return TripleStoreUtility.getInstance().getChildren(getId());
    }

    /**
     * @param hasChildren
     *            the hasChildren to set
     */
    public void setHasChildren(final boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    /**
     * @return the modifiedBy
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * @param modifiedBy
     *            the modifiedBy to set
     */
    public void setModifiedBy(final String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * @return the modifiedByTitle
     */
    public String getModifiedByTitle() {
        return modifiedByTitle;
    }

    /**
     * @param modifiedByTitle
     *            the modifiedByTitle to set
     */
    public void setModifiedByTitle(final String modifiedByTitle) {
        this.modifiedByTitle = modifiedByTitle;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * OU title equals OU name.
     * 
     * @return the title
     */
    @Override
    public String getTitle() {
        return this.name;
    }

    /*
     * See Interface for functional description.
     * 
     * @param name
     * 
     * @return
     * 
     * @throws FedoraSystemException
     * 
     * @seede.escidoc.core.oum.business.fedora.resources.interfaces.
     * OrganizationalUnitInterface#getMdRecord(java.lang.String)
     */
    public Datastream getMdRecord(final String name)
        throws FedoraSystemException, StreamNotFoundException {

        return new Datastream(name, getId(), null);
    }

    /*
     * See Interface for functional description.
     * 
     * @return
     * 
     * @throws FedoraSystemException
     * 
     * @seede.escidoc.core.oum.business.fedora.resources.interfaces.
     * OrganizationalUnitInterface#getMdRecords()
     */
    public Map<String, Datastream> getMdRecords() throws FedoraSystemException,
        IntegritySystemException {

        final Map<String, Datastream> result =
            new HashMap<String, Datastream>();
        final org.fcrepo.server.types.gen.Datastream[] datastreams =
            getFedoraUtility().getDatastreamsInformation(getId(), null);
        final List<String> names = new ArrayList<String>();
        for (org.fcrepo.server.types.gen.Datastream datastream : datastreams) {
            final List<String> altIDs =
                    Arrays.asList(datastream.getAltIDs());
            if (altIDs != null
                    && altIDs.contains(Datastream.METADATA_ALTERNATE_ID)) {
                names.add(datastream.getID());
            }
        }
        for (String name1 : names) {
            final String name = name1;
            try {
                result.put(name, new Datastream(name, getId(), null));
            } catch (final StreamNotFoundException e) {
                throw new IntegritySystemException(
                        "Perhaps organizational unit with id '"
                                + getId()
                                + "' was changed during retrieval of metadata records! ",
                        e);
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param name
     * @param ds
     * @throws SystemException
     * @see de.escidoc.core.oum.business.fedora.resources.interfaces.OrganizationalUnitInterface#setMdRecord(java.lang.String,
     *      de.escidoc.core.common.business.fedora.datastream.Datastream)
     */
    public void setMdRecord(final String name, final Datastream ds)
        throws SystemException {
        String mimeType = ds.getMimeType();
        String type = Constants.DEFAULT_ALTID_TYPE;
        String schema = Constants.DEFAULT_ALTID_SCHEMA;
        if (ds.getAlternateIDs().size() >= 3) {
            type = ds.getAlternateIDs().get(1);
            schema = ds.getAlternateIDs().get(2);
        }

        try {
            final Datastream curDs = getMdRecord(name);
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
            final boolean contentChanged = !ds.equals(curDs);
            if (contentChanged || !type.equals(curType)
                || !schema.equals(curSchema) || !mimeType.equals(curMimeType)) {
                if (contentChanged && name.equals(OrganizationalUnit.ESCIDOC)) {

                    final Map<String, String> mdProperties = ds.getProperties();
                    if (mdProperties != null) {
                        if (mdProperties.containsKey(OrganizationalUnit.NS_URI)) {
                            final String dcNewContent =
                                XmlUtility.createDC(mdProperties
                                    .get(OrganizationalUnit.NS_URI), ds
                                    .toStringUTF8(), getId());
                            if (dcNewContent != null
                                && dcNewContent.trim().length() > 0) {
                                try {
                                    setDc(new Datastream(
                                        Datastream.DC_DATASTREAM,
                                        getId(),
                                        dcNewContent
                                            .getBytes(XmlUtility.CHARACTER_ENCODING),
                                        Datastream.MIME_TYPE_TEXT_XML));
                                }
                                catch (final UnsupportedEncodingException e) {
                                    throw new EncodingSystemException(e
                                        .getMessage(), e);
                                }
                            }
                        }
                        else {
                            final String message =
                                "namespace uri of 'escidoc' metadata"
                                    + " is not set in datastream.";
                            throw new IntegritySystemException(message);
                        }
                    }
                    else {
                        final String message =
                            "Properties of 'md-record' datastream"
                                + " with then name 'escidoc' do not exist";
                        throw new IntegritySystemException(message);
                    }
                }
                ds.merge();
            }
        }
        catch (final StreamNotFoundException e) {
            // this is not an update; its a create
            ds.addAlternateId(type);
            ds.addAlternateId(schema);
            ds.persist(false);
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @param mdRecords
     * @throws FedoraSystemException
     */
    public void setMdRecords(final Map<String, Datastream> mdRecords)
        throws SystemException {
        // Container.setMdRecords throws FedoraSystemException, WebserverSystemException,
        // TripleStoreSystemException, IntegritySystemException,
        // EncodingSystemException

        // get list of names of data streams with alternateId = "metadata"
        final Set<String> namesInFedora = getMdRecords().keySet();

        // delete Datastreams which are in Fedora but not in mdRecords
        for (String aNamesInFedora : namesInFedora) {
            final String nameInFedora = aNamesInFedora;
            if (!mdRecords.containsKey(nameInFedora)) {
                try {
                    Datastream fedoraDs = getMdRecord(nameInFedora);
                    if (fedoraDs != null) {
                        fedoraDs.delete();
                    }
                } catch (final StreamNotFoundException e) {
                    // Do nothing, datastream is already deleted.
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Unable to find datastream '" + nameInFedora
                                + "'.", e);
                    }
                }
            }
        }
        
        // create or update Datastreams which are send
        for (String s : mdRecords.keySet()) {
            final String name = s;
            if (!namesInFedora.contains(name)) {
                final Datastream currentMdRecord = mdRecords.get(name);
                byte[] stream = currentMdRecord.getStream();
                final List<String> altIds = currentMdRecord.getAlternateIDs();
                final String[] altIDs = new String[altIds.size()];
                for (int i = 0; i < altIds.size(); i++) {
                    altIDs[i] = altIds.get(i);
                }
                getFedoraUtility().addDatastream(getId(), name, altIDs,
                        XmlUtility.NAME_MDRECORD, false, stream, false);
                // TODO should new Datastream be put in list of md-records of this OU?
            } else {
                setMdRecord(name, mdRecords.get(name));
                namesInFedora.remove(name);
            }
        }
    }

    /**
     * Get DC datastream.
     * 
     * @return The DC datastream.
     * 
     * @throws StreamNotFoundException
     *             If there is no DC datastream and parentId in Fedora.
     * @throws FedoraSystemException
     *             Thrown in case of an internal system error caused by failed
     *             Fedora access.
     */
    public Datastream getDc() throws StreamNotFoundException,
        FedoraSystemException {

        return new Datastream(Datastream.DC_DATASTREAM, getId(), null);
    }

    /**
     * Set DC datastream.
     * 
     * @param ds
     *            DC datastream
     * @throws StreamNotFoundException
     *             If there is no datastream identified by name and parentId in
     *             Fedora.
     * @throws SystemException
     *             Thrown in case of an internal system error caused by failed
     *             fedora access.
     */
    public void setDc(final Datastream ds) throws StreamNotFoundException,
        SystemException {

        try {
            if (!ds.equals(getDc())) {
                ds.merge();
            }
        }
        catch (final StreamNotFoundException e) {
            throw new StreamNotFoundException("No DC for organizational-unit "
                + getId() + ".", e);
        }
        getSomeValuesFromFedora();
    }

    /**
     * @return the publicStatus
     */
    public String getPublicStatus() {
        return publicStatus;
    }

    /**
     * @param publicStatus
     *            the publicStatus to set
     */
    public void setPublicStatus(final String publicStatus) {
        this.publicStatus = publicStatus;
    }

    /**
     * @return the parentOus
     */
    public List<String> getParents() {
        return parents;
    }

    /**
     * Get predecessors of OU.
     * 
     * @return the predecessors of the OU
     */
    public List<Predecessor> getPredecessors() {
        return this.predecessors;
    }

    /**
     * Get successors of OU.
     * 
     * @return the successors of the OU
     * @throws WebserverSystemException
     *             Thrown if creating instance of TripleStoreUtility failed.
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore request failed.
     */
    public List<Predecessor> getSuccessors() throws TripleStoreSystemException,
        WebserverSystemException {
        if (this.successors == null) {
            this.successors = getSuccessors(getId());
        }
        return this.successors;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

}
