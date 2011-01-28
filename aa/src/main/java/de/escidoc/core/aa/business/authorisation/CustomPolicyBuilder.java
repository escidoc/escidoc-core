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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.Indenter;
import com.sun.xacml.ParsingException;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicySet;
import com.sun.xacml.TargetMatch;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.combine.CombiningAlgFactory;
import com.sun.xacml.combine.RuleCombiningAlgorithm;
import com.sun.xacml.cond.FunctionTypeException;
import com.sun.xacml.ctx.ResponseCtx;

import de.escidoc.core.aa.business.persistence.EscidocPolicy;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.xacml.XacmlPolicySet;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionRoleIsGranted;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Class used to build a policy object using the XACML API.
 * 
 * @author Roland Werner (Accenture)
 * @spring.bean id="authorisation.CustomPolicyBuilder" lazy-init = "true"
 * @aa
 * 
 */
public final class CustomPolicyBuilder {

    /** The logger. */
    private static AppLogger log = new AppLogger(
        CustomPolicyBuilder.class.getName());

    /**
     * The property which is used to specify the schema file to validate against
     * (if any).
     */
    public static final String POLICY_SCHEMA_PROPERTY =
        "com.sun.xacml.PolicySchema";

    /**
     * The default rule combining algorithm in case of condition is only defined
     * for a role instead of complete policy (set).
     */
    public static final String RULE_COMB_ALG =
        "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:"
            + "ordered-permit-overrides";

    public static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    public static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";

    public static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

    /**
     * The regexp pattern used to insert xacml-context prefix into xacml parts.
     */
    private static final Pattern INSERT_XACML_PREFIX_PATTERN = Pattern
        .compile("(?s)(</{0,1})(\\s*[a-zA-Z])");

    /**
     * The replace value used to insert xacml-context prefix into xacml policy
     * parts.
     */
    private static final String INSERT_XACML_PREFIX_POLICY_VALUE = "$1"
        + de.escidoc.core.common.business.Constants.XACML_POLICY_NS_PREFIX
        + ":$2";

    /**
     * The replace value used to insert xacml-context prefix into xacml context
     * parts.
     */
    private static final String INSERT_XACML_PREFIX_CONTEXT_VALUE = "$1"
        + de.escidoc.core.common.business.Constants.XACML_CONTEXT_NS_PREFIX
        + ":$2";

    /**
     * The regexp pattern used to fix the ResourceId attribute in the xacml
     * results.
     */
    private static final Pattern RESOURCE_ID_PATTERN = Pattern
        .compile("ResourceID=");

    private static URI COMBINING_ALG_ID;

    static {
        try {
            COMBINING_ALG_ID = new URI(RULE_COMB_ALG);
        } catch (URISyntaxException e) {
            log.error("Error on initializing combining algorithm id.", e);
        }
    }

    private static File schemaFile;

    private static final String UNSUPPORTED_ROOT_ELEMENT =
        "Unsupported root element found in database, expected either"
            + " PollicySet or Policy.";

    static final String COMB_ALG_ID =
        XacmlPolicySet.URN_POLICY_COMBINING_ALGORITHM_ORDERED_PERMIT_OVERRIDES;

    static final String DESCRIPTION = "Policies of role ";

    /**
     * Private constructor to prevent initialization.
     * 
     * @aa
     */
    private CustomPolicyBuilder() {

        String schemaName = System.getProperty(POLICY_SCHEMA_PROPERTY);

        if (schemaName == null) {
            schemaFile = null;
        }
        else {
            schemaFile = new File(schemaName);
        }
    }

    /**
     * Parses the provided xml data.
     * 
     * @param xmlData
     *            The xml data to parse.
     * @return Returns the parsed <code>Document</code>.
     * @throws ParserConfigurationException
     *             Thrown in case of a parser configuration error.
     * @throws SAXException
     *             Thrown if parsing fails.
     * @throws IOException
     *             Thrown in case of an i/o error.
     * @aa
     */
    private static Document parseXml(final String xmlData)
        throws ParserConfigurationException, SAXException, IOException {

        // create the factory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        // as of 1.2, we always are name space aware
        factory.setNamespaceAware(true);

        DocumentBuilder db = null;
        if (schemaFile != null) {
            // we're using a validating parser
            factory.setValidating(true);

            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            factory.setAttribute(JAXP_SCHEMA_SOURCE, schemaFile);

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

    /**
     * Creates the default rule combining algorithm.
     * 
     * @return Returns the created <code>RuleCombiningAlgorithm</code>.
     * @throws URISyntaxException
     *             Thrown in case of an invalid algorithm id.
     * @throws UnknownIdentifierException
     *             Thrown in case of an unknown algorithm id.
     * @aa
     */
    private static RuleCombiningAlgorithm createDefaultRuleCombiningAlgorithm()
        throws URISyntaxException, UnknownIdentifierException {
        RuleCombiningAlgorithm combiningAlg =
            (RuleCombiningAlgorithm) (CombiningAlgFactory.getInstance()
                .createAlgorithm(COMBINING_ALG_ID));
        return combiningAlg;
    }

    // FIXME: javadoc is wrong
    /**
     * Builds a <code>PolicySet</code> object from the provided
     * <code>EscidocRole</code> object.<br>
     * The provided <code>EscidocRole</code> object may hold one or more
     * <code>EscidocPolicy</code> objects, each of them may hold the XML
     * representation of an Xacml policy, an Xacml policy set, or an condition
     * of an Xacml policy.<br>
     * <ol>
     * <li>For each <code>EscidocPolicy</code> object representing an Xacml
     * policy, its XML representation is parsed and a <code>Policy</code> object
     * is created from the parsed data.</li>
     * <li>For each <code>EscidocPolicy</code> object representing an Xacml
     * policy set, its XML representation is parsed and a <code>PolicySet</code>
     * object is created from the parsed data.</li>
     * <li>For each <code>EscidocPolicy</code> object representing an Xacml
     * condition, its XML representation is parsed and an <code>Apply</code>
     * object is built from the parsed data. Then, a <code>Rule</code> object is
     * created:
     * <ul>
     * <li>The rule id is constructed from the name of the provided
     * <code>EscidocRole</code> object followed by the postfix "-policy-rule-" +
     * index of the new rule.</li>
     * <li>The rule effect is set to permit.</li>
     * <li>The rule target matches any subject, any resource, and any action of
     * the <code>EscidocPolicy</code> object's action set.</li>
     * <li>The created <code>Apply</code> object is used as the rule condition.</li>
     * </ul>
     * If an <code>EscidocPolicy</code> object does not hold any Xml data, a
     * rule is constructed without a condition.<br>
     * <br>
     * The created <code>Rule</code> objects are combined in a new
     * <code>Policy</code> object as follows:
     * <ul>
     * <li>The policy id is constructed from the name of the provided
     * <code>EscidocRole</code> object followed by the postfix "-policy".</li>
     * <li>The policy target matches any subject, any resource, and any action
     * of any created <code>Rule</code> object.</li>
     * <li>The created <code>Rule</code> objects are used as the rules of the
     * policy.</li>
     * <li>The rule combining algorithm is set to ordered-permit-overrides.</li>
     * </ul>
     * </li>
     * <li>If more than one <code>Policy</code> object has been created by the
     * previous steps, they are combined in a <code>PolicySet</code> object as
     * follows
     * <ul>
     * <li>The policy set id is constructed from the name of the provided
     * <code>EscidocRole</code> object with the postfix "-policies".</li>
     * <li>The policy combining algorithm is set to ordered-permit-overrides.</li>
     * <li>The policy set target matches any subject, any resource, and action.</li>
     * </ul>
     * </li>
     * <li>Finally, a <code>PolicySet</code> object is created as follows and
     * returned:
     * <ul>
     * <li>The policy set id is constructed from the name of the provided
     * <code>EscidocRole</code> object.</li>
     * <li>The policy combining algorithm is set to ordered-permit-overrides.</li>
     * <li>The policy set target matches any subject, any action, and
     * <ul>
     * <li>any resource, if the provided <code>EscidocRole</code> object
     * represents the default policies,</li>
     * <li>any resource, for that the <code>EscidocRole</code> has been granted
     * to the subject.</li>
     * </ul>
     * <li>The Xacml policy/policy set of the one and only created
     * <code>Policy</code>/<code>PolicySet</code> object is the policy or policy
     * set created in one of the previous steps.</li>
     * </ul>
     * </li>
     * </ol>
     * 
     * @param escidocRole
     *            The <code>EscidocRole</code> object for that the Xacml
     *            <code>PolicySet</code> object be built.<br>
     * @return The generated <code>PolicySet</code> object.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws URISyntaxException
     *             Thrown if a URI syntax error occurred.
     * @throws UnknownIdentifierException
     *             Thrown in case of an unknown identifier.
     * @throws FunctionTypeException
     *             Thrown in case of an error with a function type.
     * @aa
     */
    public static PolicySet buildXacmlRolePolicySet(
        final EscidocRole escidocRole) throws WebserverSystemException,
        URISyntaxException, UnknownIdentifierException, FunctionTypeException {

        // final String policyIdValue =
        // StringUtility.concatenateToString(escidocRole.getRoleName(),
        // "-policy");
        List<AbstractPolicy> xacmlPolicies = new Vector<AbstractPolicy>();
        Iterator<EscidocPolicy> iter =
            escidocRole.getEscidocPolicies().iterator();
        // List<Rule> rules = new ArrayList<Rule>();
        // List<Action> rulesActions = new Vector<Action>();
        while (iter.hasNext()) {
            EscidocPolicy escidocPolicy = iter.next();
            final String xmlData = escidocPolicy.getXml();
            final Element root;
            final String name;
            if (xmlData != null && !xmlData.trim().equals("")) {
                try {
                    Document doc = parseXml(xmlData);

                    // handle the policy, if it's a known type
                    root = doc.getDocumentElement();
                    name = root.getLocalName();
                }
                catch (Exception e) {
                    final String msg =
                        StringUtility.concatenateWithBracketsToString(
                            "Error during parsing policy data.", xmlData);
                    log.error(msg, e);
                    throw new WebserverSystemException(msg, e);
                }
            }
            else {
                root = null;
                name = "N/A";
            }

            if (name.equals("PolicySet")) {
                try {
                    xacmlPolicies.add(PolicySet.getInstance(root));
                }
                catch (ParsingException e) {
                    final StringBuffer msg =
                        StringUtility.concatenateWithBrackets(
                            "Exception while parsing policy", xmlData);
                    log.error(msg.toString(), e);
                    throw new WebserverSystemException(msg.toString(), e);
                }
            }
            else if (name.equals("Policy")) {
                try {
                    xacmlPolicies.add(Policy.getInstance(root));
                }
                catch (ParsingException e) {
                    final StringBuffer msg =
                        StringUtility.concatenateWithBrackets(
                            "Exception while parsing policy", xmlData);
                    log.error(msg.toString(), e);
                    throw new WebserverSystemException(msg.toString(), e);
                }
            }
            else {
                throw new WebserverSystemException(
                    StringUtility.concatenateWithBracketsToString(
                        UNSUPPORTED_ROOT_ELEMENT, name));
            }
        }

        // if (!rules.isEmpty()) {
        // xacmlPolicies.add(new XacmlPolicy(new URI(policyIdValue),
        // createDefaultRuleCombiningAlgorithm(), null, null, null, rules,
        // escidocRole.getRoleName(), rulesActions));
        // }

        // if (xacmlPolicies.size() > 1) {
        // XacmlPolicySet tmpPolicySet =
        // new XacmlPolicySet(StringUtility.concatenateToString(
        // escidocRole.getRoleName(), "-policies"),
        // CustomPolicyBuilder.COMB_ALG_ID, null, null, null,
        // xacmlPolicies);
        // xacmlPolicies = new Vector<AbstractPolicy>();
        // xacmlPolicies.add(tmpPolicySet);
        // }

        XacmlPolicySet xacmlRolePolicySet =
            new XacmlPolicySet(escidocRole.getId(),
                CustomPolicyBuilder.COMB_ALG_ID,
                CustomPolicyBuilder.DESCRIPTION + escidocRole.getRoleName(),
                null, CustomPolicyBuilder.generateTargetResources(escidocRole),
                xacmlPolicies);

        if (log.isDebugEnabled()) {
            log.debug(xacmlRolePolicySet.toString());
        }

        return xacmlRolePolicySet;
    }

    /**
     * Generates the resources part of the target of a role policy set for the
     * provided role.<br>
     * The generated resources part of the target asserts that the (limited)
     * role is granted to the user by "calling" the implemented function
     * {@link XacmlFunctionRoleIsGranted}.<br>
     * In case of the default policies, the generated target matches any
     * resource, as this "role" is granted to any user.
     * 
     * @param role
     *            The role name/id.
     * @return Returns the resources of the target.
     * @throws FunctionTypeException
     *             Thrown in case of an problem with a function type.
     * @throws URISyntaxException
     *             Thrown if a provided URI is invalid.
     * @throws UnknownIdentifierException
     *             Thrown if the provided combining algorithm id is unknown.
     * @see XacmlFunctionRoleIsGranted
     * @aa
     */
    static List<Collection<TargetMatch>> generateTargetResources(
        final EscidocRole role) throws URISyntaxException,
        UnknownIdentifierException, FunctionTypeException {

        // null is the needed default value in case of default (user) policies.
        List<Collection<TargetMatch>> policyResources = null;
        if (!EscidocRole.DEFAULT_USER_ROLE_ID.equals(role.getId())) {

            policyResources = new ArrayList<Collection<TargetMatch>>();
            List<TargetMatch> policyResource = new ArrayList<TargetMatch>();
            policyResource.add(CustomTargetBuilder.generateResourceMatch(
                XacmlFunctionRoleIsGranted.NAME, role.getId(),
                AttributeIds.URN_RESOURCE_ID, Constants.XMLSCHEMA_STRING));
            policyResources.add(policyResource);
        }
        else {
            policyResources = null;
        }

        return policyResources;
    }

    /**
     * Generates the resources part of the target of a role policy set for the
     * provided role.<br>
     * The generated resources part of the target asserts that the (limited)
     * role is granted to the user by "calling" the implemented function
     * {@link XacmlFunctionRoleIsGranted}.<br>
     * In case of the default policies, the generated target matches any
     * resource, as this "role" is granted to any user.
     * 
     * @param policyId
     *            The policyId.
     * @return Returns the resources of the target.
     * @throws FunctionTypeException
     *             Thrown in case of an problem with a function type.
     * @throws URISyntaxException
     *             Thrown if a provided URI is invalid.
     * @throws UnknownIdentifierException
     *             Thrown if the provided combining algorithm id is unknown.
     * @see XacmlFunctionRoleIsGranted
     * @aa
     */
    static List<Collection<TargetMatch>> generateTargetResources(
        final String policyId) throws URISyntaxException,
        UnknownIdentifierException, FunctionTypeException {

        List<Collection<TargetMatch>> policyResources = null;
        policyResources = new ArrayList<Collection<TargetMatch>>();
        List<TargetMatch> policyResource = new ArrayList<TargetMatch>();
        policyResource.add(CustomTargetBuilder.generateResourceMatch(
            XacmlFunctionRoleIsGranted.NAME, policyId,
            AttributeIds.URN_RESOURCE_ID, Constants.XMLSCHEMA_STRING));
        policyResources.add(policyResource);
        return policyResources;
    }

    /**
     * Regenerates the XacmlPolicySet with a new policyId.
     * 
     * @param xacmlPolicySet
     *            The xacmlPolicySet.
     * @param policyId
     *            The new policyId.
     * @return Returns the new XacmlPolicySet.
     * @throws URISyntaxException
     *             Thrown if a provided URI is invalid.
     * @throws UnknownIdentifierException
     *             Thrown if the provided combining algorithm id is unknown.
     * @throws FunctionTypeException
     *             Thrown in case of an problem with a function type.
     * @aa
     */
    public static XacmlPolicySet regeneratePolicySet(
        final XacmlPolicySet xacmlPolicySet, final String policyId)
        throws URISyntaxException, UnknownIdentifierException,
        FunctionTypeException {
        if (xacmlPolicySet
            .getId().toString().equals(EscidocRole.DEFAULT_USER_ROLE_ID)) {
            return xacmlPolicySet;
        }
        return new XacmlPolicySet(policyId, CustomPolicyBuilder.COMB_ALG_ID,
            CustomPolicyBuilder.DESCRIPTION + policyId, null,
            generateTargetResources(policyId), xacmlPolicySet.getChildren());

    }

    /**
     * Encodes the provided {@link AbstractPolicy} to a {@link String}
     * containing the xml representation.
     * 
     * @param policy
     *            The policy to encode.
     * @return Returns the xml representation.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    public static String encode(final AbstractPolicy policy)
        throws WebserverSystemException {

        long start = System.nanoTime();

        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        policy.encode(writer, new Indenter());
        String ret = null;
        try {
            ret = writer.toString(XmlUtility.CHARACTER_ENCODING);
            writer.close();
        }
        catch (UnsupportedEncodingException e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
        catch (IOException e) {
            writer = null;
        }

        if (log.isDebugEnabled()) {
            log.debug("Encoded AbstractPolicy in "
                + Long.valueOf(System.nanoTime() - start) + "ns");
        }

        ret = insertXacmlPrefix(ret);
        return ret;
    }

    /**
     * Inserts the xacml-policy prefix into the provided xml data.
     * 
     * @param ret
     *            The xml data to insert the prefix.
     * @return Returns the provided xml data with injected xacml prefix.
     */
    public static String insertXacmlPrefix(final String ret) {

        return INSERT_XACML_PREFIX_PATTERN.matcher(ret).replaceAll(
            INSERT_XACML_PREFIX_POLICY_VALUE);
    }

    /**
     * Encodes the provided {@link ResponseCtx} to a {@link String} containing
     * the xml representation.
     * 
     * @param ctx
     *            The {@link ResponseCtx} to encode.
     * @return Returns the xml representation.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    public static String encode(final ResponseCtx ctx)
        throws WebserverSystemException {

        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        ctx.encode(writer, new Indenter());
        String ret = null;
        try {
            ret = writer.toString(XmlUtility.CHARACTER_ENCODING);
            writer.close();
        }
        catch (UnsupportedEncodingException e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
        catch (IOException e) {
            writer = null;
        }
        // There is an error in ResponseCtx.encode(): The attribute
        // ResourceID is written instead of ResourceId. This must be
        // changed to create valid xml data. Additionally, xacml-context
        // prefix has to be added, here.
        return INSERT_XACML_PREFIX_PATTERN
            .matcher(RESOURCE_ID_PATTERN.matcher(ret).replaceAll("ResourceId="))
            .replaceAll(INSERT_XACML_PREFIX_CONTEXT_VALUE);
    }

}
