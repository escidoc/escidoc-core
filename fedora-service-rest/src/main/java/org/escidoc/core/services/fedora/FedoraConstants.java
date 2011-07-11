package org.escidoc.core.services.fedora;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class FedoraConstants {

    public static final String FEDORA_DATE_TIME_PATTERN = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'";
    public static final String FOXML_VERSION = "1.1";
    public static final String STATE_ACTIVE = "Active";
    public static final String CONTROL_GROUP_E = "E";
    public static final String CONTROL_GROUP_M = "M";
    public static final String CONTROL_GROUP_R = "R";
    public static final String CONTROL_GROUP_X = "X";
    public static final String PROPERTY_NAME_STATE = "info:fedora/fedora-system:def/model#state";
    public static final String PROPERTY_NAME_LABEL = "info:fedora/fedora-system:def/model#label";
    public static final String PROPERTY_NAME_OWNER_ID = "info:fedora/fedora-system:def/model#ownerId";
    public static final String PROPERTY_NAME_CREATE_DATE = "info:fedora/fedora-system:def/model#createdDate";
    public static final String PROPERTY_NAME_LAST_MODIFIED_DATE = "info:fedora/fedora-system:def/view#lastModifiedDate";
    public static final String ESCIDOC_NAMESPACE = "info:fedora/";
    public static final QName RDF_RESOURCE_QNAME = new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource");

    /**
     * Private constructor to avoid instantiation.
     */
    private FedoraConstants() {
    }

}
