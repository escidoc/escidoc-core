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
package de.escidoc.core.aa.business.authorisation;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.ctx.Subject;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for a finder module.<br> This class provides some methods used in all finder modules of the AA
 * component.
 *
 * @author Torsten Tetteroo
 */
public final class FinderModuleHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FinderModuleHelper.class);

    private static final String ERROR_EXCEPTION_INSTANTIATION = "Could not create ResourceNotFoundException instance.";

    private static final Pattern PATTERN_PARSE_STATUS = Pattern.compile(AttributeIds.STATUS_PREFIX + "(.*)");

    /**
     * Pattern used to convert the object type retrieved from the triple store.
     */
    private static final Pattern PATTERN_CONVERT_TRIPLESTORE_OBJECT_TYPE =
        Pattern.compile('(' + de.escidoc.core.common.business.Constants.RESOURCES_NS_URI + "){0,1}([A-Z])([^A-Z]*)");

    private static final int GROUP_NUMBER_TAILING_CHARACTERS = 3;

    private static final int GROUP_NUMBER_FIRST_CHARACTER = 2;

    /**
     * Constructor.
     */
    private FinderModuleHelper() {
    }

    /**
     * Checks if the provided resource id is the dummy id for new resources.
     *
     * @param resourceId The id to check.
     * @return Returns {@code true} if the provided id is the dummy id of new resources.
     */
    public static boolean isNewResourceId(final String resourceId) {

        return "".equals(resourceId);
    }

    /**
     * Retrieves the resource attribute with type http://www.w3.org/2001/XMLSchema#string and the provided id from the
     * provided {@code EvaluationCtx} object.
     *
     * @param context                   The EvaluationCtx to retrieve the attribute from
     * @param idUri                     The id of the attribute
     * @param forceEmptyResultException Flag indicating if in case of an empty result bag an exception shall be thrown
     *                                  ({@code true}) or {@code null} shall be returned
     *                                  ({@code false}).
     * @return The retrieved attribute value
     * @throws ResourceNotFoundException Thrown if the attribute fetching failed because the resource from that it
     *                                   should be fetched does not exist.
     * @throws WebserverSystemException  Thrown if an internal error occurred.
     */
    public static String retrieveSingleResourceAttribute(
        final EvaluationCtx context, final URI idUri, final boolean forceEmptyResultException)
        throws ResourceNotFoundException, WebserverSystemException {

        return extractSingleResult(idUri, context.getResourceAttribute(Constants.URI_XMLSCHEMA_STRING, idUri, null),
            forceEmptyResultException);
    }

    /**
     * Retrieves the subject attribute with type http://www.w3.org/2001/XMLSchema#string and the provided id from the
     * provided {@code EvaluationCtx} object.
     *
     * @param context                   The EvaluationCtx to retrieve the attribute from
     * @param idUri                     The id of the attribute
     * @param forceEmptyResultException Flag indicating if in case of an empty result bag an exception shall be thrown
     *                                  ({@code true}) or {@code null} shall be returned
     *                                  ({@code false}).
     * @return The retrieved attribute value
     * @throws ResourceNotFoundException Thrown if the attribute fetching failed because the resource from that it
     *                                   should be fetched does not exist.
     * @throws WebserverSystemException  Thrown if an internal error occurred.
     */
    public static String retrieveSingleSubjectAttribute(
        final EvaluationCtx context, final URI idUri, final boolean forceEmptyResultException)
        throws ResourceNotFoundException, WebserverSystemException {

        return extractSingleResult(idUri, context.getSubjectAttribute(Constants.URI_XMLSCHEMA_STRING, idUri,
            Subject.DEFAULT_CATEGORY), forceEmptyResultException);
    }

    /**
     * Extracts a single string result from the provided bag.
     *
     * @param idUri                     The attribute id URI for message in case of an error.
     * @param result                    The {@code EvaluationResult} object from that the single result shall be
     *                                  extracted.
     * @param forceEmptyResultException Flag indicating if in case of an empty result bag an exception shall be thrown
     *                                  ({@code true}) or {@code null} shall be returned
     *                                  ({@code false}).
     * @return Returns the extracted single string.
     * @throws ResourceNotFoundException Thrown if the attribute fetching failed because the resource from that it
     *                                   should be fetched does not exist.
     * @throws WebserverSystemException  Thrown if an internal error occurred.
     */
    private static String extractSingleResult(
        final URI idUri, final EvaluationResult result, final boolean forceEmptyResultException)
        throws ResourceNotFoundException, WebserverSystemException {

        final BagAttribute bag = (BagAttribute) result.getAttributeValue();
        // there has to be exactly one
        if (bag == null || bag.size() != 1) {
            // check for error
            final Status status = result.getStatus();
            if (status != null) {
                // status exists, must be checked
                final String statusCode = (String) status.getCode().get(0);
                if (!statusCode.equals(Status.STATUS_OK)) {
                    convertToException(status.getMessage(), statusCode);
                }
            }

            // handle result that is empty
            if (bag == null || bag.isEmpty()) {
                if (forceEmptyResultException) {
                    final StringBuffer errorMsg = new StringBuffer(idUri.toString());
                    errorMsg.append(", bag is null or empty");
                    throw new WebserverSystemException(StringUtility.format(
                        "There should be exactly one attribute in the bag!", errorMsg));
                }
                else {
                    return null;
                }
            }
            // handle result that contains more than one value
            else {
                final StringBuffer errorMsg = new StringBuffer(idUri.toString());
                errorMsg.append(", bag.size = ");
                errorMsg.append(bag.size());
                throw new WebserverSystemException(StringUtility.format(
                    "There should be exactly one attribute in the bag!", errorMsg));
            }
        }

        return ((StringAttribute) bag.iterator().next()).getValue();
    }

    /**
     * Retrieves the resource attribute with type http://www.w3.org/2001/XMLSchema#string and the provided id from the
     * provided {@code EvaluationCtx} object.
     *
     * @param context                   The EvaluationCtx to retrieve the attribute from
     * @param idUri                     The id of the attribute
     * @param forceEmptyResultException Flag indicating if in case of an empty result bag an exception shall be thrown
     *                                  ({@code true}) or {@code null} shall be returned
     *                                  ({@code false}).
     * @return The retrieved attribute values as HashSet
     * @throws ResourceNotFoundException Thrown if the attribute fetching failed because the resource from that it
     *                                   should be fetched does not exist.
     * @throws WebserverSystemException  Thrown if an internal error occurred.
     */
    public static Set<String> retrieveMultiResourceAttribute(
        final EvaluationCtx context, final URI idUri, final boolean forceEmptyResultException)
        throws ResourceNotFoundException, WebserverSystemException {

        return extractMultiResult(idUri, context.getResourceAttribute(Constants.URI_XMLSCHEMA_STRING, idUri, null),
            forceEmptyResultException);
    }

    /**
     * Extracts a multi string result from the provided bag.
     *
     * @param idUri                     The attribute id URI for message in case of an error.
     * @param result                    The {@code EvaluationResult} object from that the single result shall be
     *                                  extracted.
     * @param forceEmptyResultException Flag indicating if in case of an empty result bag an exception shall be thrown
     *                                  ({@code true}) or {@code null} shall be returned
     *                                  ({@code false}).
     * @return Returns the extracted strings as HashSet.
     * @throws ResourceNotFoundException Thrown if the attribute fetching failed because the resource from that it
     *                                   should be fetched does not exist.
     * @throws WebserverSystemException  Thrown if an internal error occurred.
     */
    private static Set<String> extractMultiResult(
        final URI idUri, final EvaluationResult result, final boolean forceEmptyResultException)
        throws ResourceNotFoundException, WebserverSystemException {

        final Set<String> returnHash = new HashSet<String>();
        final BagAttribute bag = (BagAttribute) result.getAttributeValue();

        if (bag == null || bag.isEmpty()) {
            // check for error
            final Status status = result.getStatus();
            if (status != null) {
                // status exists, must be checked
                final String statusCode = (String) status.getCode().get(0);
                if (!statusCode.equals(Status.STATUS_OK)) {
                    convertToException(status.getMessage(), statusCode);
                }
            }

            // handle result that is empty
            if (forceEmptyResultException) {
                final StringBuffer errorMsg = new StringBuffer(idUri.toString());
                errorMsg.append(", bag is null or empty");
                throw new WebserverSystemException(StringUtility.format(
                    "There should be at least one attribute in the bag!", errorMsg));
            }
            else {
                return returnHash;
            }
        }
        for (Iterator<StringAttribute> iterator = bag.iterator(); iterator.hasNext();) {
            returnHash.add(iterator.next().getValue());
        }

        return returnHash;
    }

    /**
     * Converts the status code of the provided status to the corresponding exception.<br> If the status indicates the
     * attribute fetching has failed because the resource to fetch the attribute from could not be found, a
     * {@code ResourceNotFoundException} is thrown. All other errors are thrown in a
     * {@code WebserverSystemException}.
     *
     * @param msg        The error message.
     * @param statusCode The status code.
     * @throws WebserverSystemException  Thrown in case of an internal error.
     * @throws ResourceNotFoundException Thrown if a resource does not exist.
     */
    private static void convertToException(final String msg, final String statusCode) throws WebserverSystemException,
        ResourceNotFoundException {

        final Matcher matcher = PATTERN_PARSE_STATUS.matcher(statusCode);
        if (matcher.find()) {
            // found escidoc error status containing an escidoc
            // exception. Currently, only ResourceNotFoundExceptions
            // are sent with an escidoc status, all other exceptions
            // are sent with processing error status and should be
            // handled as WebserverSystemException
            final String exceptionClassName = matcher.group(1);
            final ResourceNotFoundException exceptionInstance;
            try {
                exceptionInstance =
                    (ResourceNotFoundException) Class.forName(exceptionClassName).getConstructor(
                        new Class[] { String.class }).newInstance(msg);
            }
            catch (final Exception e) {
                throw new WebserverSystemException(StringUtility.format(ERROR_EXCEPTION_INSTANTIATION,
                    exceptionClassName), e);
            }
            LOGGER.error(exceptionClassName + ' ' + msg + ' ' + statusCode);
            throw exceptionInstance;
        }
        else {
            // found unexpected "error" status
            final String emsg = StringUtility.format("Error during attribute fetching", msg);
            throw new WebserverSystemException(emsg);
        }
    }

    /**
     * Gets the resource id from the provided {@code EvaluationCtx}.<br>
     *
     * @param context The {@code EvaluationCtx} to get the resource id from.
     * @return Returns the resource id from the {@code EvaluationCtx} as a {@code String}.
     */
    public static String getResourceId(final EvaluationCtx context) {

        final AttributeValue resourceIdAttr = context.getResourceId();
        return ((StringAttribute) resourceIdAttr).getValue();
    }

    /**
     * Retrieves values from the triple store using the provided where clause.
     *
     * @param targetIsSubject targetIsSubject
     * @param whereClause     The where clause to "select" the values that shall be returned. This clause should be
     *                        created by using one of the appropriate methods provided by this class.
     * @param objectId        The id of the resource object for that the values hall be retrieved.
     * @param predicateId
     * @param tsu             The {@link TripleStoreUtility} to use.
     * @return Returns the specified attribute of the specified resource.<br> This is a list of string values and may be
     *         empty.
     * @throws ResourceNotFoundException Thrown if a resource with the provided id cannot be found in the triple store.
     * @throws SystemException           Thrown in case of an internal error.
     */
    public static List<String> retrieveFromTripleStore(
        final boolean targetIsSubject, final StringBuffer whereClause, final String objectId, final String predicateId,
        final TripleStoreUtility tsu) throws ResourceNotFoundException, SystemException {

        final StringBuffer query = tsu.getRetrieveSelectClause(targetIsSubject, predicateId).append(whereClause);
        final List<String> result = tsu.retrieve(query.toString());

        return result == null || result.isEmpty() ? handleAttributeFromTripleStoreNotFound(objectId, tsu) : result;
    }

    /**
     * If an attribute for a resource is not found, this can happen, because an existing resource does not own the
     * searched attribute, or because the resource itself does not exist. In the first case, an empty list is returned.
     * In the second case, a {@code ResourceNotFoundException} must be thrown.
     *
     * @param id  The resource id for that the attribute could not be found.
     * @param tsu The {@link TripleStoreUtility} to use.
     * @return Returns an empty list or throws an exception.
     * @throws ResourceNotFoundException Thrown if a resource with the provided id is not found.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    private static List<String> handleAttributeFromTripleStoreNotFound(final String id, final TripleStoreUtility tsu)
        throws ResourceNotFoundException, TripleStoreSystemException {

        if (tsu.exists(id)) {
            return new ArrayList<String>();
        }
        else {
            throw new ResourceNotFoundException(StringUtility.format("Resource not found", id));
        }
    }

    /**
     * Converts from value stored in triple store (e.g. http://escidoc.de/core/01/resources/OrganizationalUnit) to value
     * used in attribute-ids and scope-defs (e.g. organizational-unit)
     *
     * @param objectType    The object type to convert. If this is {@code null}, {@code null} is returned.
     * @param failOnNoMatch If this is {@code true}, an exception is thrown if the provided object type is not in
     *                      the format used in triple store. Otherwise the provided value is returned.
     * @return Returns the converted value or the provided value, if conversion fails and no exception shall be thrown.
     * @throws IntegritySystemException Thrown if the provided value is not in expected format and failOnNoMatch is
     *                                  {@code true}.
     */
    public static String convertObjectType(final String objectType, final boolean failOnNoMatch)
        throws IntegritySystemException {

        if (objectType != null) {
            final Matcher matcher = PATTERN_CONVERT_TRIPLESTORE_OBJECT_TYPE.matcher(objectType);
            if (matcher.find()) {
                final StringBuilder ret = new StringBuilder();
                boolean hasNext;
                do {
                    ret.append(matcher.group(GROUP_NUMBER_FIRST_CHARACTER).toLowerCase(Locale.ENGLISH));
                    ret.append(matcher.group(GROUP_NUMBER_TAILING_CHARACTERS));
                    hasNext = matcher.find(matcher.end());
                    if (hasNext) {
                        ret.append('-');
                    }
                }
                while (hasNext);

                return ret.toString();
            }
            else if (failOnNoMatch) {
                throw new IntegritySystemException(StringUtility.format("Unexpected object type retrieved", objectType));
            }
        }

        return objectType;
    }

}
