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

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.Indenter;
import com.sun.xacml.ParsingException;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicySet;
import com.sun.xacml.PolicyTreeElement;
import com.sun.xacml.TargetMatch;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.cond.FunctionTypeException;
import com.sun.xacml.ctx.ResponseCtx;
import de.escidoc.core.aa.business.persistence.EscidocPolicy;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.xacml.XacmlPolicySet;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionRoleIsGranted;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class used to build a policy object using the XACML API.
 *
 * @author Roland Werner (Accenture)
 */
@Service("authorisation.CustomPolicyBuilder")
public final class CustomPolicyBuilder {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomPolicyBuilder.class);

    /**
     * The property which is used to specify the schema file to validate against (if any).
     */
    private static final String POLICY_SCHEMA_PROPERTY = "com.sun.xacml.PolicySchema";

    /**
     * The default rule combining algorithm in case of condition is only defined for a role instead of complete policy
     * (set).
     */
    public static final String RULE_COMB_ALG =
        "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:" + "ordered-permit-overrides";

    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    /**
     * The regexp pattern used to insert xacml-context prefix into xacml parts.
     */
    private static final Pattern INSERT_XACML_PREFIX_PATTERN = Pattern.compile("(?s)(</{0,1})(\\s*[a-zA-Z])");

    /**
     * The replace value used to insert xacml-context prefix into xacml policy parts.
     */
    private static final String INSERT_XACML_PREFIX_POLICY_VALUE =
        "$1" + de.escidoc.core.common.business.Constants.XACML_POLICY_NS_PREFIX + ":$2";

    /**
     * The replace value used to insert xacml-context prefix into xacml context parts.
     */
    private static final String INSERT_XACML_PREFIX_CONTEXT_VALUE =
        "$1" + de.escidoc.core.common.business.Constants.XACML_CONTEXT_NS_PREFIX + ":$2";

    /**
     * The regexp pattern used to fix the ResourceId attribute in the xacml results.
     */
    private static final Pattern RESOURCE_ID_PATTERN = Pattern.compile("ResourceID=");

    private static final File SCHEMA_FILE;

    static {
        final String schemaName = System.getProperty(POLICY_SCHEMA_PROPERTY);
        SCHEMA_FILE = schemaName == null ? null : new File(schemaName);
    }

    private static final String UNSUPPORTED_ROOT_ELEMENT =
        "Unsupported root element found in database, expected either" + " PollicySet or Policy.";

    private static final String COMB_ALG_ID = XacmlPolicySet.URN_POLICY_COMBINING_ALGORITHM_ORDERED_PERMIT_OVERRIDES;

    private static final String DESCRIPTION = "Policies of role ";

    /**
     * Private constructor to prevent initialization.
     */
    private CustomPolicyBuilder() {
    }

    /**
     * Parses the provided xml data.
     *
     * @param xmlData The xml data to parse.
     * @return Returns the parsed {@code Document}.
     * @throws ParserConfigurationException Thrown in case of a parser configuration error.
     * @throws SAXException                 Thrown if parsing fails.
     * @throws IOException                  Thrown in case of an i/o error.
     */
    private static Document parseXml(final String xmlData) throws ParserConfigurationException, SAXException,
        IOException {

        // create the factory
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        // as of 1.2, we always are name space aware
        factory.setNamespaceAware(true);

        final DocumentBuilder db;
        if (SCHEMA_FILE != null) {
            // we're using a validating parser
            factory.setValidating(true);

            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            factory.setAttribute(JAXP_SCHEMA_SOURCE, SCHEMA_FILE);

            db = factory.newDocumentBuilder();
            db.setErrorHandler(new CustomErrorHandler());
        }
        else { // set the factory to work the way the system requires
            // we're not doing any validation
            factory.setValidating(false);

            db = factory.newDocumentBuilder();
        }

        return db.parse(new InputSource(new StringReader(xmlData)));
    }

    // FIXME: javadoc is wrong

    /**
     * Builds a {@code PolicySet} object from the provided {@code EscidocRole} object.<br> The provided
     * {@code EscidocRole} object may hold one or more {@code EscidocPolicy} objects, each of them may hold
     * the XML representation of an Xacml policy, an Xacml policy set, or an condition of an Xacml policy.<br> <ol>
     * <li>For each {@code EscidocPolicy} object representing an Xacml policy, its XML representation is parsed and
     * a {@code Policy} object is created from the parsed data.</li> <li>For each {@code EscidocPolicy} object
     * representing an Xacml policy set, its XML representation is parsed and a {@code PolicySet} object is created
     * from the parsed data.</li> <li>For each {@code EscidocPolicy} object representing an Xacml condition, its
     * XML representation is parsed and an {@code Apply} object is built from the parsed data. Then, a
     * {@code Rule} object is created: <ul> <li>The rule id is constructed from the name of the provided
     * {@code EscidocRole} object followed by the postfix "-policy-rule-" + index of the new rule.</li> <li>The
     * rule effect is set to permit.</li> <li>The rule target matches any subject, any resource, and any action of the
     * {@code EscidocPolicy} object's action set.</li> <li>The created {@code Apply} object is used as the
     * rule condition.</li> </ul> If an {@code EscidocPolicy} object does not hold any Xml data, a rule is
     * constructed without a condition.<br> <br> The created {@code Rule} objects are combined in a new
     * {@code Policy} object as follows: <ul> <li>The policy id is constructed from the name of the provided
     * {@code EscidocRole} object followed by the postfix "-policy".</li> <li>The policy target matches any
     * subject, any resource, and any action of any created {@code Rule} object.</li> <li>The created
     * {@code Rule} objects are used as the rules of the policy.</li> <li>The rule combining algorithm is set to
     * ordered-permit-overrides.</li> </ul> </li> <li>If more than one {@code Policy} object has been created by
     * the previous steps, they are combined in a {@code PolicySet} object as follows <ul> <li>The policy set id is
     * constructed from the name of the provided {@code EscidocRole} object with the postfix "-policies".</li>
     * <li>The policy combining algorithm is set to ordered-permit-overrides.</li> <li>The policy set target matches any
     * subject, any resource, and action.</li> </ul> </li> <li>Finally, a {@code PolicySet} object is created as
     * follows and returned: <ul> <li>The policy set id is constructed from the name of the provided
     * {@code EscidocRole} object.</li> <li>The policy combining algorithm is set to ordered-permit-overrides.</li>
     * <li>The policy set target matches any subject, any action, and <ul> <li>any resource, if the provided
     * {@code EscidocRole} object represents the default policies,</li> <li>any resource, for that the
     * {@code EscidocRole} has been granted to the subject.</li> </ul> <li>The Xacml policy/policy set of the one
     * and only created {@code Policy}/{@code PolicySet} object is the policy or policy set created in one of
     * the previous steps.</li> </ul> </li> </ol>
     *
     * @param escidocRole The {@code EscidocRole} object for that the Xacml {@code PolicySet} object be
     *                    built.<br>
     * @return The generated {@code PolicySet} object.
     * @throws WebserverSystemException   Thrown in case of an internal error.
     * @throws URISyntaxException         Thrown if a URI syntax error occurred.
     * @throws UnknownIdentifierException Thrown in case of an unknown identifier.
     * @throws FunctionTypeException      Thrown in case of an error with a function type.
     */
    public static PolicySet buildXacmlRolePolicySet(final EscidocRole escidocRole) throws WebserverSystemException,
        URISyntaxException, UnknownIdentifierException, FunctionTypeException {

        // final String policyIdValue =
        // StringUtility.concatenateToString(escidocRole.getRoleName(),
        // "-policy");
        final List<AbstractPolicy> xacmlPolicies = new ArrayList<AbstractPolicy>();
        // List<Rule> rules = new ArrayList<Rule>();
        // List<Action> rulesActions = new Vector<Action>();
        for (final EscidocPolicy escidocPolicy : escidocRole.getEscidocPolicies()) {
            final String xmlData = escidocPolicy.getXml();
            final Element root;
            final String name;
            if (xmlData != null && xmlData.trim().length() != 0) {
                try {
                    final Document doc = parseXml(xmlData);

                    // handle the policy, if it's a known type
                    root = doc.getDocumentElement();
                    name = root.getLocalName();
                }
                catch (final Exception e) {
                    final String msg = StringUtility.format("Error during parsing policy data.", xmlData);
                    LOGGER.error(msg, e);
                    throw new WebserverSystemException(msg, e);
                }
            }
            else {
                root = null;
                name = "N/A";
            }

            if ("PolicySet".equals(name)) {
                try {
                    xacmlPolicies.add(PolicySet.getInstance(root));
                }
                catch (final ParsingException e) {
                    final String msg = StringUtility.format("Exception while parsing policy", xmlData);
                    LOGGER.error(msg, e);
                    throw new WebserverSystemException(msg, e);
                }
            }
            else if ("Policy".equals(name)) {
                try {
                    xacmlPolicies.add(Policy.getInstance(root));
                }
                catch (final ParsingException e) {
                    final String msg = StringUtility.format("Exception while parsing policy", xmlData);
                    LOGGER.error(msg, e);
                    throw new WebserverSystemException(msg, e);
                }
            }
            else {
                throw new WebserverSystemException(StringUtility.format(UNSUPPORTED_ROOT_ELEMENT, name));
            }
        }
        final XacmlPolicySet xacmlRolePolicySet =
            new XacmlPolicySet(escidocRole.getId(), COMB_ALG_ID, DESCRIPTION + escidocRole.getRoleName(), null,
                generateTargetResources(escidocRole), xacmlPolicies);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(xacmlRolePolicySet.toString());
        }

        return xacmlRolePolicySet;
    }

    /**
     * Generates the resources part of the target of a role policy set for the provided role.<br> The generated
     * resources part of the target asserts that the (limited) role is granted to the user by "calling" the implemented
     * function {@link XacmlFunctionRoleIsGranted}.<br> In case of the default policies, the generated target matches
     * any resource, as this "role" is granted to any user.
     *
     * @param role The role name/id.
     * @return Returns the resources of the target.
     * @throws FunctionTypeException      Thrown in case of an problem with a function type.
     * @throws URISyntaxException         Thrown if a provided URI is invalid.
     * @throws UnknownIdentifierException Thrown if the provided combining algorithm id is unknown.
     * @see XacmlFunctionRoleIsGranted
     */
    static List<Collection<TargetMatch>> generateTargetResources(final EscidocRole role) throws URISyntaxException,
        UnknownIdentifierException, FunctionTypeException {

        // null is the needed default value in case of default (user) policies.
        final List<Collection<TargetMatch>> policyResources;
        if (EscidocRole.DEFAULT_USER_ROLE_ID.equals(role.getId())) {
            policyResources = null;
        }
        else {

            policyResources = new ArrayList<Collection<TargetMatch>>();
            final Collection<TargetMatch> policyResource = new ArrayList<TargetMatch>();
            policyResource.add(CustomTargetBuilder.generateResourceMatch(XacmlFunctionRoleIsGranted.NAME, role.getId(),
                AttributeIds.URN_RESOURCE_ID, Constants.XMLSCHEMA_STRING));
            policyResources.add(policyResource);
        }

        return policyResources;
    }

    /**
     * Generates the resources part of the target of a role policy set for the provided role.<br> The generated
     * resources part of the target asserts that the (limited) role is granted to the user by "calling" the implemented
     * function {@link XacmlFunctionRoleIsGranted}.<br> In case of the default policies, the generated target matches
     * any resource, as this "role" is granted to any user.
     *
     * @param policyId The policyId.
     * @return Returns the resources of the target.
     * @throws FunctionTypeException      Thrown in case of an problem with a function type.
     * @throws URISyntaxException         Thrown if a provided URI is invalid.
     * @throws UnknownIdentifierException Thrown if the provided combining algorithm id is unknown.
     * @see XacmlFunctionRoleIsGranted
     */
    static List<Collection<TargetMatch>> generateTargetResources(final String policyId) throws URISyntaxException,
        UnknownIdentifierException, FunctionTypeException {

        final List<Collection<TargetMatch>> policyResources = new ArrayList<Collection<TargetMatch>>();
        final Collection<TargetMatch> policyResource = new ArrayList<TargetMatch>();
        policyResource.add(CustomTargetBuilder.generateResourceMatch(XacmlFunctionRoleIsGranted.NAME, policyId,
            AttributeIds.URN_RESOURCE_ID, Constants.XMLSCHEMA_STRING));
        policyResources.add(policyResource);
        return policyResources;
    }

    /**
     * Regenerates the XacmlPolicySet with a new policyId.
     *
     * @param xacmlPolicySet The xacmlPolicySet.
     * @param policyId       The new policyId.
     * @return Returns the new XacmlPolicySet.
     * @throws URISyntaxException         Thrown if a provided URI is invalid.
     * @throws UnknownIdentifierException Thrown if the provided combining algorithm id is unknown.
     * @throws FunctionTypeException      Thrown in case of an problem with a function type.
     */
    public static XacmlPolicySet regeneratePolicySet(final XacmlPolicySet xacmlPolicySet, final String policyId)
        throws URISyntaxException, UnknownIdentifierException, FunctionTypeException {
        if (xacmlPolicySet.getId().toString().equals(EscidocRole.DEFAULT_USER_ROLE_ID)) {
            return xacmlPolicySet;
        }
        return new XacmlPolicySet(policyId, COMB_ALG_ID, DESCRIPTION + policyId, null,
            generateTargetResources(policyId), xacmlPolicySet.getChildren());

    }

    /**
     * Encodes the provided {@link AbstractPolicy} to a {@link String} containing the xml representation.
     *
     * @param policy The policy to encode.
     * @return Returns the xml representation.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static String encode(final PolicyTreeElement policy) throws WebserverSystemException {

        final ByteArrayOutputStream writer = new ByteArrayOutputStream();
        policy.encode(writer, new Indenter());
        String ret = null;
        try {
            ret = writer.toString(XmlUtility.CHARACTER_ENCODING);
            writer.close();
        }
        catch (final UnsupportedEncodingException e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
        catch (final IOException e) {
            // Ignore exceptions
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encoded AbstractPolicy");
        }

        ret = insertXacmlPrefix(ret);
        return ret;
    }

    /**
     * Inserts the xacml-policy prefix into the provided xml data.
     *
     * @param ret The xml data to insert the prefix.
     * @return Returns the provided xml data with injected xacml prefix.
     */
    public static String insertXacmlPrefix(final CharSequence ret) {

        return INSERT_XACML_PREFIX_PATTERN.matcher(ret).replaceAll(INSERT_XACML_PREFIX_POLICY_VALUE);
    }

    /**
     * Encodes the provided {@link ResponseCtx} to a {@link String} containing the xml representation.
     *
     * @param ctx The {@link ResponseCtx} to encode.
     * @return Returns the xml representation.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public static String encode(final ResponseCtx ctx) throws WebserverSystemException {

        final ByteArrayOutputStream writer = new ByteArrayOutputStream();
        ctx.encode(writer, new Indenter());
        String ret = null;
        try {
            ret = writer.toString(XmlUtility.CHARACTER_ENCODING);
            writer.close();
        }
        catch (final UnsupportedEncodingException e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
        catch (final IOException e) {
            // Ignore exception
        }
        // There is an error in ResponseCtx.encode(): The attribute
        // ResourceID is written instead of ResourceId. This must be
        // changed to create valid xml data. Additionally, xacml-context
        // prefix has to be added, here.
        return INSERT_XACML_PREFIX_PATTERN
            .matcher(RESOURCE_ID_PATTERN.matcher(ret).replaceAll("ResourceId=")).replaceAll(
                INSERT_XACML_PREFIX_CONTEXT_VALUE);
    }

}
