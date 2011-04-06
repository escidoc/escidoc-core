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

import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.ParsingException;
import com.sun.xacml.cond.FunctionFactory;
import com.sun.xacml.cond.FunctionFactoryProxy;
import com.sun.xacml.cond.StandardFunctionFactory;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.AttributeFinderModule;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.impl.CurrentEnvModule;
import com.sun.xacml.finder.impl.SelectorModule;
import de.escidoc.core.aa.business.xacml.finder.CheckProvidedAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.DatabasePolicyFinderModule;
import de.escidoc.core.aa.business.xacml.finder.GrantAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.LockOwnerAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.NewOuParentsAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.ObjectTypeAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.PartlyResolveableAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.ResourceAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.ResourceIdentifierAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.ResourceNotFoundAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.RoleAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.SmAttributesFinderModule;
import de.escidoc.core.aa.business.xacml.finder.TripleStoreAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.UserAccountAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.finder.UserGroupAttributeFinderModule;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionContains;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionIsIn;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionOneAttributeInBothLists;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionRoleInList;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionRoleIsGranted;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is a custom XACML PDP. It sets up an eSciDoc specific configuration of the Sun-specific XACML PDP.<p/>
 * <p/>
 * For setup see constructor definition.
 *
 * @author Roland Werner (Accenture)
 */
public class CustomPdp {

    // this is the actual PDP object we'll use for evaluation
    private PDP pdp;

    private CheckProvidedAttributeFinderModule checkProvidedAttrFinder;

    private ResourceNotFoundAttributeFinderModule resourceNotFoundAttrFinder;

    private PartlyResolveableAttributeFinderModule partlyResolveableAttrFinder;

    private ObjectTypeAttributeFinderModule objectTypeAttrFinder;

    private TripleStoreAttributeFinderModule tripleStoreAttrFinder;

    private UserAccountAttributeFinderModule userAccountAttrFinder;

    private UserGroupAttributeFinderModule userGroupAttrFinder;

    private GrantAttributeFinderModule grantAttrFinder;

    private ResourceAttributeFinderModule resourceAttrFinder;

    private RoleAttributeFinderModule roleAttrFinder;

    private SmAttributesFinderModule smAttributesFinderModule;

    private LockOwnerAttributeFinderModule lockOwnerAttributeFinderModule;

    private NewOuParentsAttributeFinderModule newOuParentsAttributeFinderModule;

    private ResourceIdentifierAttributeFinderModule resourceIdAttrFinderModule;

    private PDPConfig pdpConfig;

    private DatabasePolicyFinderModule databasePolicyFinder;

    private XacmlFunctionRoleIsGranted xacmlFunctionRoleIsGranted;

    /**
     * Default constructor. This creates a CustomPdp programmatically. <p/>
     * <p/>
     * The configuration in detail: <ul> <li>As a policy finder the <code>DatabasePolicyFinderModule</code> is used</li>
     * <li>In order to retrieve system-specific subject attributes, the <code>UserAccountAttributeFinderModule</code>
     * Attribute Finder Module is used.</li> <li>In order to retrieve system-specific resource attributes, the
     * <code>ResourceAttributeFinderModule</code> Attribute Finder Module is used.</li> </ul>
     *
     * @throws Exception exception thrown if something during setup of the configuration goes wrong.
     * @see UserAccountAttributeFinderModule
     * @see ResourceAttributeFinderModule
     * @see DatabasePolicyFinderModule
     */
    public CustomPdp() throws Exception {

    }

    /**
     * Initilizes the pdp.<br> This must be called before the first access to the wrapped pdp.
     *
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private void init() throws WebserverSystemException {

        // setup the PolicyFinder that this PDP will use
        final PolicyFinder policyFinder = new PolicyFinder();
        databasePolicyFinder.setPolicyFinder(policyFinder);
        final Set<PolicyFinderModule> policyModules = new HashSet<PolicyFinderModule>();
        policyModules.add(this.databasePolicyFinder);
        policyFinder.setModules(policyModules);

        // now setup attribute finder modules
        // Setup the AttributeFinder just like we setup the PolicyFinder. Note
        // that unlike with the policy finder, the order matters here. See the
        // the javadocs for more details.
        final AttributeFinder attributeFinder = new AttributeFinder();
        final List<AttributeFinderModule> attributeModules = new ArrayList<AttributeFinderModule>();
        // first the standard XACML Modules
        attributeModules.add(new CurrentEnvModule());
        attributeModules.add(new SelectorModule());
        // now the custom escidoc Modules

        // the CheckProvidedAttributeFinderModule must be the first eSciDoc
        // specific finder module in the chain
        attributeModules.add(this.checkProvidedAttrFinder);

        attributeModules.add(this.resourceNotFoundAttrFinder);
        // the PartlyResolveableAttributeFinderModule must be the second eSciDoc
        // specific finder module in the chain
        attributeModules.add(this.partlyResolveableAttrFinder);
        attributeModules.add(this.objectTypeAttrFinder);
        attributeModules.add(this.tripleStoreAttrFinder);
        attributeModules.add(this.userAccountAttrFinder);
        attributeModules.add(this.userGroupAttrFinder);
        attributeModules.add(this.grantAttrFinder);
        attributeModules.add(this.lockOwnerAttributeFinderModule);
        attributeModules.add(this.newOuParentsAttributeFinderModule);
        attributeModules.add(this.resourceAttrFinder);
        attributeModules.add(this.roleAttrFinder);

        attributeModules.add(this.smAttributesFinderModule);
        attributeModules.add(this.resourceIdAttrFinderModule);
        attributeFinder.setModules(attributeModules);

        // Setup the FunctionFactory
        final FunctionFactoryProxy proxy = StandardFunctionFactory.getNewFactoryProxy();
        final FunctionFactory factory = proxy.getTargetFactory();
        factory.addFunction(new XacmlFunctionContains());
        factory.addFunction(new XacmlFunctionIsIn());
        factory.addFunction(new XacmlFunctionRoleInList());
        factory.addFunction(new XacmlFunctionOneAttributeInBothLists());
        factory.addFunction(this.xacmlFunctionRoleIsGranted);

        FunctionFactory.setDefaultFactory(proxy);

        this.pdpConfig = new PDPConfig(attributeFinder, policyFinder, null);
        this.pdp = new PDP(this.pdpConfig);
    }

    /**
     * Evaluates the given request and returns the Response that the PDP will hand back to the PEP.
     *
     * @param requestFile the name of a file that contains a Request
     * @return the result of the evaluation
     * @throws ParsingException         if the Request is invalid
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws java.io.FileNotFoundException
     */
    public ResponseCtx evaluate(final String requestFile) throws ParsingException, WebserverSystemException,
        FileNotFoundException {

        if (this.pdp == null) {
            init();
        }

        // setup the request based on the file
        final RequestCtx request = RequestCtx.getInstance(new FileInputStream(requestFile));

        // evaluate the request
        return pdp.evaluate(request);
    }

    /**
     * Evaluates the given request and returns the Response that the PDP will hand back to the PEP.<p/>
     * <p/>
     * This is the method currently in use for the AA component.
     *
     * @param request the request to evaluate
     * @return the result of the evaluation
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public ResponseCtx evaluate(final RequestCtx request) throws WebserverSystemException {

        if (this.pdp == null) {
            init();
        }

        // evaluate the request
        return pdp.evaluate(request);
    }

    /**
     * Injects the <code>DatabasePolicyFinderModule</code> if "called" via Spring.
     *
     * @param databasePolicyFinder <code>DatabasePolicyFinderModule</code> object to inject.
     */
    public void setDatabasePolicyFinder(final DatabasePolicyFinderModule databasePolicyFinder) {
        this.databasePolicyFinder = databasePolicyFinder;
    }

    /**
     * Injects the <code>{@link CheckProvidedAttributeFinderModule}</code> if "called" via Spring.
     *
     * @param checkProvidedAttrFinder <code>CheckProvidedAttributeFinderModule</code> object to inject.
     */
    public void setCheckProvidedAttrFinder(final CheckProvidedAttributeFinderModule checkProvidedAttrFinder) {
        this.checkProvidedAttrFinder = checkProvidedAttrFinder;
    }

    /**
     * Injects the <code>ResourceNotFoundAttributeFinderModule</code> if "called" via Spring.
     *
     * @param resourceNotFoundAttrFinder <code>ResourceNotFoundAttributeFinderModule</code> object to inject.
     */
    public void setResourceNotFoundAttrFinder(final ResourceNotFoundAttributeFinderModule resourceNotFoundAttrFinder) {
        this.resourceNotFoundAttrFinder = resourceNotFoundAttrFinder;
    }

    /**
     * Injects the <code>PartlyResolveableAttributeFinderModule</code> if "called" via Spring.
     *
     * @param partlyResolveableAttrFinder <code>PartlyResolveableAttributeFinderModule</code> object to inject.
     */
    public void setPartlyResolveableAttrFinder(final PartlyResolveableAttributeFinderModule partlyResolveableAttrFinder) {
        this.partlyResolveableAttrFinder = partlyResolveableAttrFinder;
    }

    /**
     * Injects the <code>ObjectTypeAttributeFinderModule</code> if "called" via Spring.
     *
     * @param objectTypeAttrFinder <code>ObjectTypeAttributeFinderModule</code> object to inject.
     */
    public void setObjectTypeAttrFinder(final ObjectTypeAttributeFinderModule objectTypeAttrFinder) {
        this.objectTypeAttrFinder = objectTypeAttrFinder;
    }

    /**
     * Injects the <code>ResourceAttributeFinderModule</code> if "called" via Spring.
     *
     * @param resourceAttrFinder <code>ResourceAttributeFinderModule</code> object to inject.
     */
    public void setResourceAttrFinder(final ResourceAttributeFinderModule resourceAttrFinder) {
        this.resourceAttrFinder = resourceAttrFinder;
    }

    /**
     * Injects the <code>ResourceIdentifierAttributeFinderModule</code> if "called" via Spring.
     *
     * @param resourceIdAttrFinder <code>ResourceIdentifierAttributeFinderModule</code> object to inject.
     */
    public void setResourceIdAttrFinderModule(final ResourceIdentifierAttributeFinderModule resourceIdAttrFinder) {
        this.resourceIdAttrFinderModule = resourceIdAttrFinder;
    }

    /**
     * Injects the <code>RoleAttributeFinderModule</code> if "called" via Spring.
     *
     * @param roleAttrFinder <code>RoleAttributeFinderModule</code> object to inject.
     */
    public void setRoleAttrFinder(final RoleAttributeFinderModule roleAttrFinder) {
        this.roleAttrFinder = roleAttrFinder;
    }

    /**
     * Injects the <code>TripleStoreAttributeFinderModule</code> if "called" via Spring.
     *
     * @param tripleStoreAttrFinder <code>tripleStoreAttributeFinderModule</code> object to inject.
     */
    public void setTripleStoreAttrFinder(final TripleStoreAttributeFinderModule tripleStoreAttrFinder) {
        this.tripleStoreAttrFinder = tripleStoreAttrFinder;
    }

    /**
     * Injects the <code>UserAccountAttributeFinderModule</code> if "called" via Spring.
     *
     * @param userAccountAttrFinder <code>UserAccountAttributeFinderModule</code> object to inject.
     */
    public void setUserAccountAttrFinder(final UserAccountAttributeFinderModule userAccountAttrFinder) {
        this.userAccountAttrFinder = userAccountAttrFinder;
    }

    /**
     * Injects the <code>UserGroupAttributeFinderModule</code> if "called" via Spring.
     *
     * @param userGroupAttrFinder <code>UserGroupAttributeFinderModule</code> object to inject.
     */
    public void setUserGroupAttrFinder(final UserGroupAttributeFinderModule userGroupAttrFinder) {
        this.userGroupAttrFinder = userGroupAttrFinder;
    }

    /**
     * Injects the <code>GrantAttributeFinderModule</code> if "called" via Spring.
     *
     * @param grantAttrFinder <code>GrantAttributeFinderModule</code> object to inject.
     */
    public void setGrantAttrFinder(final GrantAttributeFinderModule grantAttrFinder) {
        this.grantAttrFinder = grantAttrFinder;
    }

    /**
     * Gets the pdp configuration used in this PDP.
     *
     * @return Returns the <code>PDPConfig</code> of this PDP.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public PDPConfig getPdpConfig() throws WebserverSystemException {

        if (this.pdpConfig == null) {
            init();
        }
        return this.pdpConfig;
    }

    /**
     * Injects the {@link XacmlFunctionRoleIsGranted}.
     *
     * @param xacmlFunctionRoleIsGranted the {@link XacmlFunctionRoleIsGranted} to inject.
     */
    public void setXacmlFunctionRoleIsGranted(final XacmlFunctionRoleIsGranted xacmlFunctionRoleIsGranted) {
        this.xacmlFunctionRoleIsGranted = xacmlFunctionRoleIsGranted;
    }

    /**
     * Injects the {@link SmAttributesFinderModule}.
     *
     * @param smAttributesFinderModule the {@link SmAttributesFinderModule} to inject.
     */
    public void setSmAttributesFinderModule(final SmAttributesFinderModule smAttributesFinderModule) {
        this.smAttributesFinderModule = smAttributesFinderModule;
    }

    /**
     * Injects the {@link LockOwnerAttributeFinderModule}.
     *
     * @param lockOwnerAttributeFinderModule the {@link LockOwnerAttributeFinderModule} to inject.
     */
    public void setLockOwnerAttributeFinderModule(final LockOwnerAttributeFinderModule lockOwnerAttributeFinderModule) {
        this.lockOwnerAttributeFinderModule = lockOwnerAttributeFinderModule;
    }

    /**
     * Injects the {@link NewOuParentsAttributeFinderModule}.
     *
     * @param newOuParentsAttributeFinderModule
     *         the {@link NewOuParentsAttributeFinderModule} to inject.
     */
    public void setNewOuParentsAttributeFinderModule(
        final NewOuParentsAttributeFinderModule newOuParentsAttributeFinderModule) {
        this.newOuParentsAttributeFinderModule = newOuParentsAttributeFinderModule;
    }

}
