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
package de.escidoc.core.aa.business.persistence;

import com.sun.xacml.PolicySet;
import de.escidoc.core.aa.business.authorisation.CustomPolicyBuilder;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A role in eSciDoc.
 *
 * @author Torsten Tetteroo
 */
public class EscidocRole extends EscidocRoleBase {

    /**
     * The serial uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the dummy role holding the default policies.
     */
    public static final String DEFAULT_USER_ROLE_ID = "escidoc:role-default-user";

    private transient PolicySet policySet;

    private List<String> objectTypes;

    /**
     * Checks if this is a limited role or an unlimited role.
     *
     * @return Returns {@code true} if this is a limited role, {@code false} else.
     */
    public boolean isLimited() {
        return getScopeDefs() != null && !getScopeDefs().isEmpty();
    }

    /**
     * Gets the XACML policy set representing this eSciDoc role.<br> The policies of this role are returned in a
     * {@code XacmlRolePolicySet} with the policy combining algorithm set to ordered-permit-overrides. The
     * {@code PolicySet} objects are cached.
     *
     * @return Returns the {@code XacmlRolePolicySet} of this role.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public PolicySet getXacmlPolicySet() throws WebserverSystemException {

        if (this.policySet == null) {
            try {
                this.policySet = CustomPolicyBuilder.buildXacmlRolePolicySet(this);
            }
            catch (final Exception e) {
                throw new WebserverSystemException(StringUtility.format(
                    "Error during getting of the role's policy set", e.getMessage()), e);
            }
        }

        return this.policySet;
    }

    /**
     * Gets the policy set id (XACML).
     *
     * @return Returns the policy set id of this role.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public URI getPolicySetId() throws WebserverSystemException {

        return getXacmlPolicySet().getId();
    }

    /**
     * Gets the objectTypes of the role's scope definitions.
     *
     * @return Returns the objectTypes in a {@code List}.
     */
    public Collection<String> getObjectTypes() {
        if (this.objectTypes == null) {
            if (isLimited()) {
                final Collection<ScopeDef> scopeDefs = getScopeDefs();
                this.objectTypes = new ArrayList<String>(scopeDefs.size());
                for (final ScopeDef scopeDef : scopeDefs) {
                    objectTypes.add(scopeDef.getObjectType());
                }
            }
            else {
                this.objectTypes = new ArrayList<String>(0);
            }
        }

        return this.objectTypes;
    }

    /**
     * Gets the href for this role.
     *
     * @return Returns the href of this role.
     */
    public String getHref() {

        return XmlUtility.getRoleHref(this.getId());
    }

    /**
     * See Interface for functional description.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {

        ToStringBuilder toStringBuilder =
            new ToStringBuilder(this).append("roleName", getRoleName()).append("id", getId());
        if (isLimited()) {
            toStringBuilder = toStringBuilder.append("# Limitation Conditions", getScopeDefs().size());
        }
        return toStringBuilder.toString();
    }
}
