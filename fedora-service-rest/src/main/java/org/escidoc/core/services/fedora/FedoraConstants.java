package org.escidoc.core.services.fedora;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class FedoraConstants {

    public final static String FEDORA_DATE_TIME_PATTERN = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'";
    public final static String FOXML_VERSION = "1.1";
    public final static String STATE_ACTIVE = "Active";
    public final static String CONTROL_GROUP_E = "E";
    public final static String CONTROL_GROUP_M = "M";
    public final static String CONTROL_GROUP_R = "R";
    public final static String CONTROL_GROUP_X = "X";
    public final static String PROPERTY_NAME_STATE = "info:fedora/fedora-system:def/model#state";
    public final static String PROPERTY_NAME_LABEL = "info:fedora/fedora-system:def/model#label";
    public final static String PROPERTY_NAME_OWNER_ID = "info:fedora/fedora-system:def/model#ownerId";
    public final static String PROPERTY_NAME_CREATE_DATE = "info:fedora/fedora-system:def/model#createdDate";
    public final static String PROPERTY_NAME_LAST_MODIFIED_DATE = "info:fedora/fedora-system:def/view#lastModifiedDate";
    public final static String ESCIDOC_NAMESPACE = "info:fedora/";
    public final static QName RDF_RESOURCE_QNAME = new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource");

    /**
     * Private constructor to avoid instantiation.
     */
    private FedoraConstants() {
    }

}
