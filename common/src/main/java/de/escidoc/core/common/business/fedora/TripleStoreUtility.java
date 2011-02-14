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
package de.escidoc.core.common.business.fedora;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.nsdl.mptstore.query.QueryException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * The abstract TripleStoreUtility. <code>getInstance</code> returns a
 * implementation of the subclass registered as business.TripleStoreUtility.
 * 
 * @author ROF
 */
public abstract class TripleStoreUtility extends JdbcDaoSupport
    implements TripleStoreFilterUtility {

    public static final String Fedora_Creation_Date_Predicate =
        "info:fedora/fedora-system:def/model#createdDate";

    public static final String PROP_COMPONENT =
        Constants.STRUCTURAL_RELATIONS_NS_URI + "component";

    public static final String PROP_PART_OF =
        Constants.STRUCTURAL_RELATIONS_NS_URI + "part-of";

    public static final String PROP_DC_DESCRIPTION = Constants.DC_NS_URI
        + "description";

    public static final String PROP_DC_IDENTIFIER = Constants.DC_NS_URI
        + "identifier";

    public static final String PROP_DC_TITLE = Constants.DC_NS_URI + "title";

    public static final String NSDL_ONTOLOGIES_OBJECT_TYPE =
        "http://www.nsdl.org/ontologies/relationships/objectType";

    public static final String PROP_VERSION_TIMESTAMP = "timestamp";

    public static final String PROP_VERSION_DATE = "date";

    public static final String PROP_VERSION_NUMBER = "number";

    public static final String PROP_VERSION_STATUS = "version-status";

    public static final String PROP_VERSION_VALIDITY = "valid-status";

    public static final String PROP_VERSION_COMMENT = "comment";

    public static final String PROP_VERSION_MODIFIED_BY_TITLE =
        "modified-by.title";

    public static final String PROP_VERSION_MODIFIED_BY_HREF =
        "modified-by.href";

    public static final String PROP_FRAMEWORK_BUILD =
        "http://escidoc.de/core/01/system/build";

    /*
     * Current Version Values
     */

    public static final String PROP_CURRENT_VERSION_NUMBER =
        "current-version.number";

    public static final String PROP_CURRENT_VERSION_DATE =
        "current-version.date";

    public static final String PROP_CURRENT_VERSION_STATUS =
        "current-version.status";

    public static final String PROP_CURRENT_VERSION_VALID_STATUS =
        "current-version.valid-status";

    public static final String PROP_CURRENT_VERSION_USER =
        "current-version.user";

    public static final String PROP_CURRENT_VERSION_USER_TITLE =
        "current-version.user.title";

    public static final String PROP_CURRENT_VERSION_COMMENT =
        "current-version.comment";

    /*
     *
     */

    public static final String PROP_NAME = Constants.PROPERTIES_NS_URI + "name";

    public static final String PROP_TITLE = Constants.PROPERTIES_NS_URI
        + "title";

    public static final String PROP_OBJECT_TYPE = Constants.RDF_NAMESPACE_URI
        + "type";

    public static final String PROP_COMPONENT_CONTENT_CATEGORY =
        Constants.PROPERTIES_NS_URI
            + Elements.ELEMENT_COMPONENT_CONTENT_CATEGORY;

    public static final String PROP_CONTENT_CATEGORY =
        Constants.PROPERTIES_NS_URI
            + Elements.ELEMENT_COMPONENT_CONTENT_CATEGORY;

    public static final String PROP_CONTENT_MODEL_ID =
        Constants.STRUCTURAL_RELATIONS_NS_URI + Elements.ELEMENT_CONTENT_MODEL;

    public static final String PROP_CONTENT_MODEL_TITLE =
        Constants.PROPERTIES_NS_URI + Elements.ELEMENT_CONTENT_MODEL + "-title";

    public static final String PROP_CONTENT_TYPE_TITLE =
        Constants.PROPERTIES_NS_URI + Elements.ELEMENT_CONTENT_MODEL + "-title";

    public static final String PROP_CONTEXT_ID =
        Constants.STRUCTURAL_RELATIONS_NS_URI + "context";

    public static final String PROP_CONTEXT_TITLE = Constants.PROPERTIES_NS_URI
        + "context-title";

    public static final String PROP_CREATED_BY_ID =
        Constants.STRUCTURAL_RELATIONS_NS_URI + Elements.ELEMENT_CREATED_BY;

    public static final String PROP_CREATED_BY_TITLE =
        Constants.PROPERTIES_NS_URI + Elements.ELEMENT_CREATED_BY_TITLE;

    public static final String PROP_CREATION_DATE =
        "info:fedora/fedora-system:def/model#createdDate";

    public static final String PROP_LAST_MODIFICATION_DATE =
        "info:fedora/fedora-system:def/view#lastModifiedDate";

    public static final String PROP_PARENT =
        Constants.STRUCTURAL_RELATIONS_NS_URI + "parent";

    public static final String PROP_DESCRIPTION = Constants.DC_NS_URI
        + "description";

    public static final String PROP_EXTERNAL_ID = XmlUtility.NAME_EXTERNAL_ID;

    public static final String PROP_FILENAME = Constants.PROPERTIES_NS_URI
        + "file-name";

    public static final String PROP_FILESIZE = Constants.PROPERTIES_NS_URI
        + "file-size";

    public static final String PROP_LOCATOR_URL = Constants.PROPERTIES_NS_URI
        + "locator-url";

    public static final String PROP_VALID_STATUS = Constants.PROPERTIES_NS_URI
        + "valid-status";

    public static final String PROP_MIME_TYPE = Constants.PROPERTIES_NS_URI
        + "mime-type";

    public static final String PROP_PUBLIC_STATUS = Constants.PROPERTIES_NS_URI
        + Elements.ELEMENT_PUBLIC_STATUS;

    public static final String PROP_PUBLIC_STATUS_COMMENT =
        Constants.PROPERTIES_NS_URI + Elements.ELEMENT_PUBLIC_STATUS_COMMENT;

    public static final String PROP_MODIFIED_BY_ID =
        Constants.STRUCTURAL_RELATIONS_NS_URI + Elements.ELEMENT_MODIFIED_BY;

    public static final String PROP_STATUS = PROP_PUBLIC_STATUS; // "status";

    public static final String PROP_MODIFIED_BY_TITLE =
        Constants.PROPERTIES_NS_URI + Elements.ELEMENT_MODIFIED_BY_TITLE;

    public static final String PROP_OBJECT_PID = Constants.PROPERTIES_NS_URI
        + Elements.ELEMENT_PID;

    public static final String PROP_COMPONENT_PID = PROP_OBJECT_PID;

    public static final String PROP_MEMBER =
        Constants.STRUCTURAL_RELATIONS_NS_URI + "member";

    /*
     * Latest Version Values
     */

    public static final String PROP_LATEST_VERSION_NUMBER =
        Constants.VERSION_NS_URI + "number";

    public static final String PROP_LATEST_VERSION_DATE =
        Constants.VERSION_NS_URI + "date";

    public static final String PROP_LATEST_VERSION_PID =
        Constants.VERSION_NS_URI + "pid";

    public static final String PROP_LATEST_VERSION_STATUS =
        Constants.VERSION_NS_URI + "status";

    public static final String PROP_LATEST_VERSION_VALID_STATUS =
        Constants.VERSION_NS_URI + "valid-status";

    public static final String PROP_LATEST_VERSION_USER_ID =
        PROP_MODIFIED_BY_ID;

    public static final String PROP_LATEST_VERSION_USER_TITLE =
        PROP_MODIFIED_BY_TITLE;

    public static final String PROP_ORIGIN =
        Constants.STRUCTURAL_RELATIONS_NS_URI + "origin";

    public static final String PROP_ORIGIN_VERSION = Constants.ORIGIN_NS_URI
        + "version-number";

    public static final String PROP_LATEST_VERSION_USER_HREF =
        PROP_LATEST_VERSION_USER_ID + "/href";

    public static final String PROP_LATEST_VERSION_COMMENT =
        Constants.VERSION_NS_URI + "comment";

    public static final String PROP_LATEST_RELEASE_NUMBER =
        Constants.RELEASE_NS_URI + "number";

    public static final String PROP_LATEST_RELEASE_DATE =
        Constants.RELEASE_NS_URI + "date";

    public static final String PROP_LATEST_RELEASE_PID =
        Constants.RELEASE_NS_URI + Elements.ELEMENT_PID;

    /*
     *
     */

    public static final String PROP_VISIBILITY = Constants.PROPERTIES_NS_URI
        + "visibility";

    public static final String PROP_ABBREVIATION = Constants.PROPERTIES_NS_URI
        + "abbreviation";

    public static final String PROP_CONTEXT_TYPE = Constants.PROPERTIES_NS_URI
        + "type";

    public static final String PROP_CONTEXT_CREATION_DATE =
        Constants.PROPERTIES_NS_URI + Elements.ELEMENT_CONTEXT;

    public static final String PROP_ORGANIZATIONAL_UNIT =
        Constants.STRUCTURAL_RELATIONS_NS_URI
            + Elements.ELEMENT_ORGANIZATIONAL_UNIT;

    /*
     * Content Relation
     */
    public static final String PROP_CONTENT_RELATION_SUBJECT =
        Constants.RDF_NAMESPACE_URI + "subject";

    public static final String PROP_CONTENT_RELATION_SUBJECT_VERSION =
        Constants.CONTENT_RELATION_NAMESPACE_URI + "/subject-version-number";

    public static final String PROP_CONTENT_RELATION_OBJECT =
        Constants.RDF_NAMESPACE_URI + "object";

    public static final String PROP_CONTENT_RELATION_OBJECT_VERSION =
        Constants.CONTENT_RELATION_NAMESPACE_URI + "/object-version-number";

    public static final String PROP_CONTENT_RELATION_DESCRIPTION =
        Constants.PROPERTIES_NS_URI + "description";

    public static final String PROP_CONTENT_RELATION_TYPE =
        Constants.RDF_NAMESPACE_URI + "predicate";

    /*
     * 
     */
    private static final Pattern PATTERN_RESOURCE_NS = Pattern.compile(".*"
        + Constants.RESOURCES_NS_URI + ".*"); // TODO

    private static final Pattern PATTERN_LOWER_THAN_GREATER_THAN = Pattern
        .compile("&lt;(.*)&gt;");

    private static TripleStoreUtility mptu = null;

    /** The logger. */
    private static AppLogger logger = new AppLogger(
        TripleStoreFilterUtility.class.getName());

    /**
     * Executes a simple query for the predicate values of an object identified
     * by the provided id.<br>
     * This method builds a BasicTriplePattern expressing the query. The first
     * or last Node of this triple pattern (depending on the position of the
     * target, must match the provided id.
     * 
     * @param id
     *            The id of the object for that a query shall be executed.
     * @param targetIsSubject
     *            A flag indicating if the target is the subject (
     *            <code>true</code>), i.e. the target is placed in the first
     *            BasicNode of the BasicTriplePattern, or if it is the object,
     *            i.e. the target is placed in the last Basic Node of the
     *            BasicTriplePattern.
     * @param predicate
     *            The uri string of the predicate for that the value(s) shall be
     *            retrieved.
     * @return Returns the results of the query in a <code>QueryResults</code>
     *         object.
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public abstract List<String> executeQueryId(
        final String id, final boolean targetIsSubject, final String predicate)
        throws TripleStoreSystemException;

    public abstract List<String> executeQueryForList(
        final Collection<String> ids, final boolean targetIsSubject,
        final String predicate) throws TripleStoreSystemException;

    protected abstract String executeQueryEarliestCreationDate()
        throws TripleStoreSystemException;

    /**
     * Executes a simple query for the predicate values.<br>
     * This method builds a BasicTriplePattern expressing the query. The first
     * or last Node of this triple pattern (depending on the position of the
     * target, must match the provided literal.
     * 
     * @param literal
     *            The literal value.
     * @param targetIsSubject
     *            A flag indicating if the target is the subject (
     *            <code>true</code>), i.e. the target is placed in the first
     *            BasicNode of the BasicTriplePattern, or if it is the object,
     *            i.e. the target is placed in the last Basic Node of the
     *            BasicTriplePattern.
     * @param predicate
     *            The uri string of the predicate for that the value(s) shall be
     *            retrieved.
     * @return Returns the results of the query in a <code>QueryResults</code>
     *         object.
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     * @throws QueryException
     *             Thrown in case of a failed query execution.
     */
    protected abstract List<String> executeQueryLiteral(
        final String literal, final boolean targetIsSubject,
        final String predicate) throws TripleStoreSystemException,
        QueryException;

    /**
     * 
     * @param id
     *            object id
     * @return id list of children objects
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public Vector<String> getChildren(final String id)
        throws TripleStoreSystemException {

        final Vector<String> result = new Vector<String>();
        List<String> results = null;

        results = executeQueryId(id, true, PROP_PARENT);
        final Iterator<String> it = results.iterator();
        while (it.hasNext()) {
            // List<Node> row = results.next();
            // String entry = row.get(0).getValue();
            String entry = it.next();
            entry = XmlUtility.getIdFromURI(entry);
            result.add(entry);
        }

        return result;
    }

    /**
     * 
     * @param ids
     *            object ids
     * @return id list of children objects
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public Vector<String> getChildren(final Collection<String> ids)
        throws TripleStoreSystemException {

        final Vector<String> result = new Vector<String>();
        List<String> results = null;

        results = executeQueryForList(ids, true, PROP_PARENT);
        final Iterator<String> it = results.iterator();
        while (it.hasNext()) {
            // List<Node> row = results.next();
            // String entry = row.get(0).getValue();
            String entry = it.next();
            entry = XmlUtility.getIdFromURI(entry);
            result.add(entry);
        }

        return result;
    }

    /**
     * 
     * @param ids
     *            object ids
     * @param totalList
     *            list with all children down the tree
     * @return id list of children objects down the tree
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public Vector<String> getChildrenPath(
        final Collection<String> ids, final Vector<String> totalList)
        throws TripleStoreSystemException {

        Vector<String> result = totalList;
        List<String> results = null;
        Collection<String> parentsList = new ArrayList<String>();

        results = executeQueryForList(ids, true, PROP_PARENT);
        if (results != null && !results.isEmpty()) {
            final Iterator<String> it = results.iterator();
            while (it.hasNext()) {
                String entry = it.next();
                entry = XmlUtility.getIdFromURI(entry);
                result.add(entry);
                parentsList.add(entry);
            }
            result = getChildrenPath(parentsList, result);
        }

        return result;
    }

    /**
     * 
     * @param id
     *            object id
     * @return id list of container objects the object is member of
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public Vector<String> getContainers(final String id)
        throws TripleStoreSystemException {

        final Vector<String> result = new Vector<String>();
        final Vector<String> queryIDs = new Vector<String>();
        queryIDs.add(id);
        List<String> results = null;

        results = executeQueryForList(queryIDs, true, PROP_MEMBER);
        final Iterator<String> it = results.iterator();
        while (it.hasNext()) {
            // List<Node> row = results.next();
            // String entry = row.get(0).getValue();
            String entry = it.next();
            entry = XmlUtility.getIdFromURI(entry);
            result.add(entry);
        }

        return result;
    }

    /**
     * Get all child containers of the given container.
     * 
     * @param id
     *            container id
     * @return id list of all child containers
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public abstract List<String> getAllChildContainers(final String id)
        throws TripleStoreSystemException;

    /**
     * Get all child OUs of the given organizational unit.
     * 
     * @param id
     *            OU id
     * @return id list of all child OUs
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public abstract List<String> getAllChildOUs(final String id)
        throws TripleStoreSystemException;

    /**
     * 
     * @param pid
     *            object id
     * @return id list of parent objects
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public Vector<String> getParents(final String pid)
        throws TripleStoreSystemException {

        return getPropertiesElementsVector(pid, PROP_PARENT);
    }

    /**
     * Get the id of the context associated with the resource identified by the
     * given id. Uses getPropertiesElements.
     * 
     * @param id
     *            The resource id
     * @return context id
     * 
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public String getContext(final String id) throws TripleStoreSystemException {

        return getPropertiesElements(id, PROP_CONTEXT_ID);
    }

    /**
     * Get the id of the content model associated with the resource identified
     * by the given id. Uses getPropertiesElements.
     * 
     * @param id
     *            The resource id
     * @return content model id
     * 
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public String getContentModel(final String id)
        throws TripleStoreSystemException {

        return getPropertiesElements(id, PROP_CONTENT_MODEL_ID);
    }

    /**
     * 
     * @param objid
     *            The id of the resource to get the last modification date for.
     * @param fedoraUtility
     *            The {@link FedoraUtility} data access object to use if access
     *            to data storage is needed.
     * 
     * @return Returns the last modification date as s {@link String}.
     * @throws TripleStoreSystemException
     *             If access to the triple store/storage back end fails.
     */
    public String getLastModificationDate(
        final String objid, final FedoraUtility fedoraUtility)
        throws TripleStoreSystemException {

        // may use getPropertiesElements(pid,

        String date = null;

        // use fedora to avoid TripleStore syncs
        try {
            date = fedoraUtility.getLastModificationDate(objid);
        }
        catch (final FedoraSystemException e) {
            throw new TripleStoreSystemException(
                "While redirect call to FedoraUtility: ", e);
        }
        return date;
    }

    /**
     * Get creation date (the Fedora creation date) from resource.
     * 
     * @param pid
     *            Objid of resource.
     * @return creation date of Fedora resource.
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public String getCreationDate(final String pid)
        throws TripleStoreSystemException {

        String result = null;
        List<String> results = executeQueryId(pid, false, PROP_CREATION_DATE);
        if (results.size() == 1) {
            result = results.get(0);
        }
        else if (results.size() == 0) {
            throw new TripleStoreSystemException(
                "Creation date not found for resource '" + pid + "'.");
        }
        else {
            throw new TripleStoreSystemException(
                "More than one creation date found for resource '" + pid + "'.");
        }
        return result;
    }

    /**
     * 
     * @return
     * @throws TripleStoreSystemException
     */
    public String getEarliestCreationDate() throws TripleStoreSystemException {
        return executeQueryEarliestCreationDate();

    }

    /**
     * 
     * @param id
     * 
     * @return
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public String getItemForComponent(final String id)
        throws TripleStoreSystemException {

        String item = null;
        List<String> results = null;

        results = executeQueryId(id, true, PROP_COMPONENT);
        final Iterator<String> it = results.iterator();
        if (it.hasNext()) {
            // TODO throw exception if more than one result
            // List<Node> row = results.next();
            // String entry = row.get(0).getValue();
            String entry = it.next();
            entry = XmlUtility.getIdFromURI(entry);
            item = entry;
        }

        return item;

    }

    /**
     * Retrieve all ou ids for ous whith teh given name.
     * 
     * @param name
     *            The name of the expected ous.
     * @return The list of ous.
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public Vector<String> getOusForName(final String name)
        throws TripleStoreSystemException {

        final Vector<String> result = new Vector<String>();
        List<String> results = null;
        try {
            results = executeQueryLiteral(name, true, PROP_DC_TITLE);
            final Iterator<String> it = results.iterator();
            while (it.hasNext()) {
                // List<Node> row = results.next();
                // row.get(0).getValue()
                final String entry = it.next();
                result.add(XmlUtility.getIdFromURI(entry));
            }
        }
        catch (final QueryException e) {
            throw new TripleStoreSystemException(e.getMessage());
        }
        // finally {
        // closeAndRelease(results);
        // }
        return result;
    }

    /**
     * Get Context by name.
     * 
     * @param name
     *            Name of Context
     * @return Context with provided name
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public abstract String getContextForName(final String name)
        throws TripleStoreSystemException;

    /**
     * 
     * @param pid
     *            Fedora object id.
     * 
     * @return Title of object
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public String getTitle(final String pid) throws TripleStoreSystemException {

        return getPropertiesElements(pid, TripleStoreUtility.PROP_DC_TITLE);
    }

    /**
     * 
     * @param pid
     *            Fedora object id.
     * 
     * @return Description of object
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public String getDescription(final String pid)
        throws TripleStoreSystemException {

        return getPropertiesElements(pid,
            TripleStoreUtility.PROP_DC_DESCRIPTION);
    }

    /**
     * @deprecated Because it is only a fallback method till OU object have a
     *             valid title property.
     * 
     * @param ouId
     *            The id of an organizational unit object.
     * @return The name of the organizational unit object.
     * @throws TripleStoreSystemException
     *             If an error occurs.
     */
    @Deprecated
    public String getOuName(final String ouId)
        throws TripleStoreSystemException {

        return getPropertiesElements(ouId, Constants.PROPERTIES_NS_URI + "name");
    }

    /**
     * 
     * @param parentId
     * @param memberId
     * 
     * @return
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public boolean isMemberOf(final String parentId, final String memberId)
        throws TripleStoreSystemException {

        boolean isMember = false;
        final List<String> entries =
            getPropertiesElementsVector(parentId,
                Constants.STRUCTURAL_RELATIONS_NS_URI + "member");
        final Iterator<String> it = entries.iterator();
        while (it.hasNext()) {
            final String entry = it.next();
            if (entry.equals(memberId)) {
                isMember = true;
                break;
            }
        }
        return isMember;

    }

    /**
     * 
     * @param ouId
     * @param parentId
     * 
     * @return
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public boolean isParentOfOu(final String ouId, final String parentId)
        throws TripleStoreSystemException {

        boolean isParent = false;
        final List<String> entries =
            getPropertiesElementsVector(ouId, PROP_PARENT);
        final Iterator<String> it = entries.iterator();
        while (it.hasNext()) {
            final String entry = it.next();
            if (entry.equals(parentId)) {
                isParent = true;
                break;
            }
        }
        return isParent;

    }

    /**
     * 
     * @param pid
     *            Objid/PID of the object.
     * @param properties
     * @param namespaceUri
     * 
     * @return
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public abstract Map<String, String> getProperties(
        String pid, Collection<String> fullqualifiedNamedProperties)
        throws TripleStoreSystemException;

    /**
     * Returns the value of a property entry in RELS-EXT by adding a slash ('/')
     * to the namespace. In order to retrieve an entry without an additional
     * slash at the end of the namespace use
     * <code>TripleStoreUtility.getRelation</code>.
     * 
     * @param pid
     * @param property
     * @param namespaceUri
     * 
     * @return value of property
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public String getProperty(
        final String pid, final String fullQualifiedNameProperty)
        throws TripleStoreSystemException {

        return this.getRelation(pid, fullQualifiedNameProperty);

    }

    /**
     * Gets the public status of the resource identified by the given id. Uses
     * getPropertiesElements.
     * 
     * @param id
     *            The id of the resource.
     * 
     * @return Returns the public status of the resource.
     * 
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public String getPublicStatus(final String id)
        throws TripleStoreSystemException {

        return getPropertiesElements(id, PROP_PUBLIC_STATUS);
    }

    /**
     * Returns the value of a relation entry in RELS-EXT. In contrast to
     * getProperty the namespace is used as it is. (Without an additional '/')
     * 
     * @param pid
     * @param property
     * @param namespaceUri
     * 
     * @return
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public abstract String getRelation(
        final String pid, final String fullQualifiedPropertyName)
        throws TripleStoreSystemException;

    public abstract String getObjectList(
        final String objectType, final Map filterMap, final String whereClause)
        throws InvalidContentException, TripleStoreSystemException,
        MissingMethodParameterException;

    /**
     * Retrieves values from the triple store using the provided query.
     * 
     * @param query
     *            The query to execute.
     * @return Returns the result list of the query.
     * @throws TripleStoreSystemException
     *             Thrown in case of an internal triple store error.
     * @common
     */
    public abstract List<String> retrieve(final String query)
        throws TripleStoreSystemException;

    /**
     * Get properties element value from the TripleStore.
     * 
     * @param pid
     *            The Id of the object.
     * @param fullqualifiedPropertyName
     *            The full qualified property name.
     * 
     * @return Value of property element.
     * @throws TripleStoreSystemException
     *             If access to the triple store fails. TODO refactor to
     *             getPropertiesElement
     */
    public String getPropertiesElements(
        final String pid, final String fullqualifiedPropertyName)
        throws TripleStoreSystemException {

        String value = null;
        final List<String> results =
            executeQueryId(pid, false, fullqualifiedPropertyName);

        final Iterator<String> it = results.iterator();
        // work around for more than one dc:identifier
        while (it.hasNext()) {
            value = it.next();
        }

        return value;
    }

    /**
     * 
     * @param pid
     * @param elementName
     * @param namespaceUri
     * 
     * @return
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public Vector<String> getPropertiesElementsVector(
        final String pid, final String fullPropertyElementName)
        throws TripleStoreSystemException {

        return new Vector<String>(executeQueryId(pid, false,
            fullPropertyElementName));
    }

    /**
     * 
     * @param pid
     * @param fullPropertyElementName
     * @return
     */
    public Vector<String> getPropertiesElementsVector(
        final String pid, final String fullPropertyElementName,
        final boolean targetIsSubject) throws TripleStoreSystemException {

        Vector<String> result = new Vector<String>();
        if (!targetIsSubject) {
            result = getPropertiesElementsVector(pid, fullPropertyElementName);
        }
        else {
            result =
                new Vector<String>(executeQueryId(pid, targetIsSubject,
                    fullPropertyElementName));
        }
        return result;

    }

    /**
     * Check if the object with the identifier <code>pid</code> exists.
     * 
     * @param pid
     *            The id of the requested object.
     * 
     * @return true if the object exists, false otherwise.
     * 
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public abstract boolean exists(final String pid)
        throws TripleStoreSystemException;

    // public boolean exists(final String pid) throws TripleStoreSystemException
    // {
    //
    // boolean exists = false;
    // String result = null;
    //
    // result =
    // getPropertiesElements(pid,
    // "http://purl.org/dc/elements/1.1/identifier");
    // if (result != null && result.length() > 0) {
    // exists = true;
    // }
    // return exists;
    // }

    /**
     * Retrieves the object type of the identified object.
     * 
     * @param pid
     *            The id of the object to get the type for.
     * 
     * @return Returns the object type of the identified object or
     *         <code>null</code>.
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public String getObjectType(final String pid)
        throws TripleStoreSystemException {

        String result = null;
        final List<String> results =
            getPropertiesElementsVector(pid, PROP_OBJECT_TYPE);
        final Iterator<String> typeIter = results.iterator();
        // TODO what should we do if more than one object type is found for one
        // id?
        while (typeIter.hasNext()) {
            result = typeIter.next();
            if (PATTERN_RESOURCE_NS.matcher(result).matches()) {
                result =
                    PATTERN_LOWER_THAN_GREATER_THAN.matcher(result).replaceAll(
                        "$1");

                break;
            }
            result = null;
        }
        return result;
    }

    /**
     * The method returns component ids of the last version of the item with a
     * provided id.
     * 
     * @param pid
     *            The id of the Component.
     * 
     * @return list of component ids.
     * @throws TripleStoreSystemException
     *             If access to the triple store fails.
     */
    public Vector<String> getComponents(final String pid)
        throws TripleStoreSystemException {
        return getPropertiesElementsVector(pid, PROP_COMPONENT);
    }

    /**
     * Builds the starting clause of a query to the triple store to retrieve
     * objects.
     * 
     * @param targetIsSubject
     *            targetIsSubject
     * @param predicateId
     *            predicateId
     * @throws TripleStoreSystemException
     *             e
     * @return Returns the starting clause.
     */
    public abstract StringBuffer getRetrieveSelectClause(
        boolean targetIsSubject, String predicateId)
        throws TripleStoreSystemException;

    /**
     * Builds the starting clause of a query to the triple store.
     * 
     * @param targetIsSubject
     *            Flag indicating that the target to search for is the subject (
     *            <code>true</code>) or the object (<code>false</code>) of the
     *            specified predicate.
     * @param predicateId
     *            The predicate id
     * @param expectedValue
     *            The value that must be matched by the specified predicate. If
     *            <code>targetIsSubject</code> is <code>true</code>, the object
     *            of the predicate must match the value. Otherwise the subject
     *            must match the value.
     * @param targetResourceType
     *            The object type of the target of the query. If this is
     *            <code>null</code>, no restriction for expected resource type
     *            is added.
     * @param contentModelTitleId
     *            The id of the predicate pointing to the title of the content
     *            model. If this is <code>null</code>, targets of any content
     *            model are searched.
     * @param contentModelTitle
     *            The content model title that the subject must match. This must
     *            not be <code>null</code>, if contentModelTitleId is not
     *            <code>null</code>.
     * 
     * 
     * @return Returns the where clause searching for the specified subjects.
     */
    public abstract StringBuffer getRetrieveWhereClause(
        boolean targetIsSubject, final String predicateId,
        final String expectedValue, final String targetResourceType,
        final String contentModelTitleId, final String contentModelTitle)
        throws TripleStoreSystemException;

    /**
     * Gets the triple store utility spring bean.<br>
     * Note: This method should only be used to get the bean if spring's
     * dependency injection is not possible.
     * 
     * @return Returns the spring bean with id "business.TripleStoreUtility".
     * @throws WebserverSystemException
     *             If getting TripleStore Bean failed.
     */
    public static TripleStoreUtility getInstance()
        throws WebserverSystemException {

        if (TripleStoreUtility.mptu == null) {
            TripleStoreUtility.mptu = BeanLocator.locateTripleStoreUtility();
        }
        return TripleStoreUtility.mptu;
    }

    // FIXME don't use triplestore?
    public Vector<String> getMethodNames(String id)
        throws TripleStoreSystemException {
        Vector<String> methodNames = new Vector<String>();

        String cmPid = getContentModel(id);
        Vector<String> sdefPids =
            getPropertiesElementsVector(cmPid,
                "info:fedora/fedora-system:def/model#hasService");
        Iterator<String> it = sdefPids.iterator();
        while (it.hasNext()) {
            methodNames.add(getProperty(it.next(),
                "info:fedora/fedora-system:def/model#definesMethod"));
        }

        return methodNames;
    }

    /**
     * Get list of surrogates pointing to the item with a provided id.
     * 
     * @param id
     *            Id of the original item.
     * @return List of surrogates for the provided original item.
     * @throws TripleStoreSystemException
     *             Thrown if request TripleStore failed.
     */
    public List<String> getSurrogates(final String id)
        throws TripleStoreSystemException {
        List<String> surrogates = new Vector<String>();
        List<String> surrogateIds =
            executeQueryId(id, true, TripleStoreUtility.PROP_ORIGIN);
        final Iterator<String> it = surrogateIds.iterator();
        if (it.hasNext()) {
            String entry = it.next();
            entry = XmlUtility.getIdFromURI(entry);
            surrogates.add(entry);
        }
        return surrogates;
    }

    /**
     * @return the logger
     */
    public static AppLogger getLogger() {
        return logger;
    }

    public boolean hasReferringResource(String id)
        throws TripleStoreSystemException {

        List<String> results = null;
        results = executeQueryId(id, true, PROP_CONTENT_MODEL_ID);
        final Iterator<String> it = results.iterator();
        return it.hasNext();
    }
}
