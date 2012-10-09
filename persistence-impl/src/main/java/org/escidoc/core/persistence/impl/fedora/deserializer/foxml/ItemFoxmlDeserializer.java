package org.escidoc.core.persistence.impl.fedora.deserializer.foxml;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.http.auth.AuthenticationException;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.ContentHandler;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.ContentStreamHandler;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.DCHandler;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.ItemRelsExtHandler;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.ItemVersionHistoryHandler;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.MdRecordHandler;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.ObjectPropertiesHandler;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.RelsExtHandler;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.RelsExtHandler.RelsExtValue;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.RelsExtHandler.RelsExtValues;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.VersionHistoryHandler;
import org.escidoc.core.persistence.impl.fedora.deserializer.foxml.parser.VersionedDatastreamHandler;
import org.escidoc.core.persistence.impl.fedora.resource.Component;
import org.escidoc.core.persistence.impl.fedora.resource.ComponentContent;
import org.escidoc.core.persistence.impl.fedora.resource.ComponentProperties;
import org.escidoc.core.persistence.impl.fedora.resource.Components;
import org.escidoc.core.persistence.impl.fedora.resource.ContentStream;
import org.escidoc.core.persistence.impl.fedora.resource.ContentStreams;
import org.escidoc.core.persistence.impl.fedora.resource.Item;
import org.escidoc.core.persistence.impl.fedora.resource.ItemProperties;
import org.escidoc.core.persistence.impl.fedora.resource.MdRecord;
import org.escidoc.core.persistence.impl.fedora.resource.MdRecords;
import org.escidoc.core.persistence.impl.fedora.resource.Relation;
import org.escidoc.core.persistence.impl.fedora.resource.Relations;
import org.escidoc.core.persistence.impl.fedora.resource.Resources;
import org.escidoc.core.persistence.impl.fedora.resource.Target;
import org.escidoc.core.persistence.impl.fedora.resource.Version;
import org.escidoc.core.persistence.impl.fedora.resource.VersionHistory;
import org.escidoc.core.persistence.impl.fedora.util.HttpUtil;



import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.stax.StaxParser;

public class ItemFoxmlDeserializer extends AbstractFoxmlDeserializer {
    private static final String INTERNAL_FEDORA_URL =
        "http://local.fedora.server/fedora/";

    public ItemFoxmlDeserializer(final HttpUtil fedoraClient,
        final String fedoraUrl) {
        super(fedoraClient, fedoraUrl);
    }

    /**
     * Get the latest version of the item.
     * 
     * @param id
     *            item id
     * @return
     */
    public Item create(final String id) throws AuthenticationException,
        IOException, EscidocException, XMLStreamException {
        return create(id, 0);
    }

    /**
     * Get a certain version of the item.
     * 
     * @param id
     *            item id
     * @param versionNumber
     *            item version number, may be 0 to get the latest version
     * @return
      */
    public Item create(final String id, final int versionNumber)
        throws AuthenticationException, IOException,
        EscidocException, XMLStreamException {
        Item result = new Item();

        // get Foxml from Fedora
        final byte[] foxml = getFoxml(id);

        // System.out.println(new String(foxml));

        // new PrintStream(new FileOutputStream("/tmp/" + id + ".foxml"))
        // .print(new String(foxml));

        // first pass
        final VersionHistory versionHistory = getVersionHistory(foxml);

        // System.out.println("version-history: " + versionHistory);

        // version history entry for current version
        final Version version;

        if (versionNumber != 0) {
            version = versionHistory.get(id + ":" + versionNumber);
        }
        else {
            version = versionHistory.lastEntry().getValue();
        }

        // System.out.println("version: " + version);

        // second pass
        StaxParser sp = new StaxParser();

        // get content-model-specific
        VersionedDatastreamHandler cmsHandler =
            new VersionedDatastreamHandler(sp, "content-model-specific",
                version.getDate());

        // get content streams
        ContentStreamHandler csHandler =
            new ContentStreamHandler(sp, version.getDate());

        // get DC
        DCHandler dcHandler = new DCHandler(sp, version.getDate());

        // get MD records
        MdRecordHandler mdRecordHandler =
            new MdRecordHandler(sp, version.getDate());

        // get object properties
        ObjectPropertiesHandler propertiesHandler =
            new ObjectPropertiesHandler(sp);

        // get RELS-EXT for version $VERSION and latest version
        RelsExtHandler relsExtHandler =
            new ItemRelsExtHandler(sp, version.getDate());

        sp.addHandler(cmsHandler);
        sp.addHandler(csHandler);
        sp.addHandler(dcHandler);
        sp.addHandler(mdRecordHandler);
        sp.addHandler(propertiesHandler);
        sp.addHandler(relsExtHandler);
        sp.parse(foxml);

        RelsExtValues relsExtValues = relsExtHandler.getValues();
        RelsExtValues latestVersionRelsExtValues =
            relsExtHandler.getLatestVersionValues();
        boolean isLatestVersion =
            (versionNumber == 0)
                || latestVersionRelsExtValues
                    .getFirst("version:number").equals(
                        String.valueOf(versionNumber));

        // System.out.println("relsExtValues: " + relsExtValues);
        // System.out.println("latestVersionRelsExtValues: "
        // + latestVersionRelsExtValues);

        // get resource types for all relation targets
        // FIXME This may be a performance issue!
        Relations relations = relsExtValues.getRelations();

        for (Relation relation : relations) {
            final Target target = relation.getTarget();

            target.setType(getResourceType(target.getId(), null));
        }
        // System.out.println("contentRelations: " + relations);

        ContentStreams contentStreams = csHandler.getContentStreams();

        if (contentStreams.size() > 0) {
            result.setContentStreams(contentStreams);
        }
        else {
            result.setContentStreams(null);
        }
        result.setMdRecords(mdRecordHandler.getMdRecords());
        result.setObjid(id);

        ItemProperties properties = new ItemProperties();

        properties
            .setContentModel(relsExtValues.getFirst("srel:content-model"));
        properties.setContentModelSpecificElement(cmsHandler.getXmlContent());
        properties.setContentModelTitle(relsExtValues
            .getFirst("prop:content-model-title"));
        properties.setContext(relsExtValues.getFirst("srel:context"));
        properties
            .setContextTitle(relsExtValues.getFirst("prop:context-title"));
        properties.setCreatedBy(relsExtValues.getFirst("srel:created-by"));
        properties.setCreatedByTitle(relsExtValues
            .getFirst("prop:created-by-title"));
        properties.setCreationDate(relsExtHandler.geFirstVersionTimestamp());
        properties.setDescription(dcHandler.getDcDescription());

        Version latestRelease = new Version();

        latestRelease.setDate(latestVersionRelsExtValues
            .getFirst("release:date"));
        latestRelease.setNumber(latestVersionRelsExtValues
            .getFirst("release:number"));
        latestRelease
            .setPid(latestVersionRelsExtValues.getFirst("release:pid"));
        properties.setLatestRelease(latestRelease);

        Version latestVersion = new Version();

        latestVersion.setDate(latestVersionRelsExtValues
            .getFirst("version:date"));
        latestVersion.setNumber(latestVersionRelsExtValues
            .getFirst("version:number"));
        properties.setLatestVersion(latestVersion);
        // TODO Locking
        // LockHandler lockHandler = LockHandler.getInstance();
        //
        // if (lockHandler.isLocked(id)) {
        // properties.setLockStatus(LockStatus.LOCKED);
        // properties.setLockDate(lockHandler.getLockDate(id));
        // properties.setLockOwner(lockHandler.getLockOwner(id));
        // properties.setLockOwnerTitle(lockHandler.getLockOwnerTitle(id));
        // }
        properties.setName(dcHandler.getDcTitle());
        properties.setPid(latestVersionRelsExtValues.getFirst("prop:pid"));
        properties.setPublicStatus(latestVersionRelsExtValues
            .getFirst("prop:public-status"));
        properties.setPublicStatusComment(latestVersionRelsExtValues
            .getFirst("prop:public-status-comment"));
        if (isLatestVersion) {
            version
                .setDate(latestVersionRelsExtValues.getFirst("version:date"));
            version.setStatus(latestVersionRelsExtValues
                .getFirst("version:status"));
        }
        version.setModifiedBy(relsExtValues.getFirst("srel:modified-by"));
        version.setModifiedByTitle(relsExtValues
            .getFirst("prop:modified-by-title"));
        properties.setVersion(version);

        result.setProperties(properties);
        result.setRelations(relations);
        result.setResources(new Resources() {
            private static final long serialVersionUID = 5723177508505955979L;

            {
                add(versionHistory);
            }
        });

        // last-modification-date
        if (version.getNumber().equals(
            properties.getLatestVersion().getNumber())) {
            result.setLastModificationDate(version.getDate());
        }
        else {
            result.setLastModificationDate(propertiesHandler
                .getLastModificationDate());
        }

        // components
        setComponents(result, relsExtValues);

        // origin
        setOriginValues(result, relsExtValues);

        // System.out.println(result);
        return result;
    }

    private Component getComponent(final String id, final String date)
        throws EscidocException, AuthenticationException,
        IOException {
        Component result = new Component();
        StaxParser sp = new StaxParser();
        try {
            // get content
            ContentHandler contentHandler = new ContentHandler(sp, date);

            // get DC
            DCHandler dcHandler = new DCHandler(sp, date);

            // get MD records
            MdRecordHandler mdRecordHandler = new MdRecordHandler(sp, date);

            // get object properties
            ObjectPropertiesHandler propertiesHandler =
                new ObjectPropertiesHandler(sp);

            // get RELS-EXT
            RelsExtHandler relsExtHandler = new ItemRelsExtHandler(sp, date);

            sp.addHandler(contentHandler);
            sp.addHandler(dcHandler);
            sp.addHandler(mdRecordHandler);
            sp.addHandler(propertiesHandler);
            sp.addHandler(relsExtHandler);
            sp.parse(getFoxmlAsStream(id));

            RelsExtValues relsExtValues = relsExtHandler.getValues();
            // System.out.println("relsExtValues: " + relsExtValues);

            MdRecords mdRecords = mdRecordHandler.getMdRecords();

            if (mdRecords.size() > 0) {
                result.setMdRecords(mdRecords);
            }
            result.setObjid(id);

            ComponentProperties properties = new ComponentProperties();

//            properties.setChecksum(contentHandler.getChecksum());
//            properties.setChecksumAlgorithm(contentHandler.getChecksumAlgorithm());
            properties.setContentCategory(relsExtValues
                .getFirst("prop:content-category"));
            properties.setCreatedBy(relsExtValues.getFirst("srel:created-by"));
            properties.setCreatedByTitle(relsExtValues
                .getFirst("prop:created-by-title"));
            properties.setCreationDate(propertiesHandler.getCreationDate());
            properties.setDescription(dcHandler.getDcDescription());
            if (mdRecords.containsKey("escidoc")) {
                properties.setFileName(dcHandler.getDcTitle());
            }
            properties.setMimeType(relsExtValues.getFirst("prop:mime-type"));
            properties.setName(dcHandler.getDcTitle());
            properties.setPid(relsExtValues.getFirst("prop:pid"));
            properties.setValidStatus(relsExtValues.getFirst("prop:valid-status"));
            properties.setVisibility(relsExtValues.getFirst("prop:visibility"));

            result.setProperties(properties);

            ComponentContent content = new ComponentContent();

//            content.setLocation(contentHandler.getContentLocation().replace(
//                INTERNAL_FEDORA_URL, fedoraUrl));
            content.setName(dcHandler.getDcTitle());
            content.setStorage(contentHandler.getStorage());

            result.setContent(content);

            // System.out.println(result);
        }
        catch (XMLStreamException e) {
            throw new SystemException(e);
        }
        return result;
    }

    private VersionHistory getVersionHistory(final byte[] foxml)
        throws EscidocException, XMLStreamException {
        StaxParser sp = new StaxParser();
        VersionHistoryHandler versionHistoryHandler =
            new ItemVersionHistoryHandler(sp);

        sp.addHandler(versionHistoryHandler);
        sp.parse(foxml);
        return versionHistoryHandler.getVersionHistory();
    }

    private void setComponents(
        final Item item, final RelsExtValues relsExtValues)
        throws EscidocException, AuthenticationException,
        XMLStreamException, IOException {
        RelsExtValue componentIds = relsExtValues.get("srel:component");
        Components components = new Components();

        if (componentIds != null) {
            for (String componentId : componentIds) {
                components.put(
                    componentId,
                    getComponent(componentId, item
                        .getProperties().getVersion().getDate()));
            }
        }
        item.setComponents(components);
    }

    private void setOriginValues(
        final Item item, final RelsExtValues relsExtValues)
        throws EscidocException,
        IOException, XMLStreamException, AuthenticationException {
        // FIXME performance problem
        final ItemProperties properties = item.getProperties();
        final String originId = relsExtValues.getFirst("srel:origin");

        if (originId != null) {
            properties.setOrigin(originId);

            String originVersionNumber =
                relsExtValues.getFirst("version-number");

            if (originVersionNumber == null) {
                originVersionNumber =
                    create(originId)
                        .getProperties().getLatestRelease().getNumber();
            }

            Item origin =
                create(originId, Integer.parseInt(originVersionNumber));

            properties.setOriginTitle(origin.getProperties().getName());

            // overwrite components
            Components components = origin.getComponents();

            item.setComponents(components);
            if (components != null) {
                components.setInherited(true);
                for (Component component : components.values()) {
                    component.setInherited(true);
                    for (MdRecord mdRecord : component.getMdRecords().values()) {
                        mdRecord.setInherited(true);
                    }
                }
            }

            // overwrite content streams
            ContentStreams contentStreams = origin.getContentStreams();

            item.setContentStreams(contentStreams);
            if (contentStreams != null) {
                contentStreams.setInherited(true);
                for (ContentStream contentStream : contentStreams.values()) {
                    contentStream.setInherited(true);
                }
            }

            // merge mdRecords
            MdRecords originMdRecords = origin.getMdRecords();
            MdRecords itemMdRecords = item.getMdRecords();

            if (originMdRecords != null) {
                for (MdRecord originMdRecord : originMdRecords.values()) {
                    final String originMdRecordName = originMdRecord.getName();

                    if (!itemMdRecords.containsKey(originMdRecordName)) {
                        originMdRecord.setInherited(true);
                        itemMdRecords.put(originMdRecordName, originMdRecord);
                    }
                }
            }
        }
    }
}
