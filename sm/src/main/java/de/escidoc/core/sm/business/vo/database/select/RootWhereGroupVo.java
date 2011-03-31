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
package de.escidoc.core.sm.business.vo.database.select;

import java.util.Collection;

/**
 * Holds all values needed to represent a group of root where-clauses (enclosed by brackets in the resulting
 * sql-statement).
 *
 * @author Michael Hoppe
 */
public class RootWhereGroupVo {

    private RootWhereFieldVo rootWhereFieldVo;

    private Collection<AdditionalWhereFieldVo> additionalWhereFieldVos;

    /**
     * @return the additionalWhereFieldVos
     */
    public Collection<AdditionalWhereFieldVo> getAdditionalWhereFieldVos() {
        return this.additionalWhereFieldVos;
    }

    /**
     * @param additionalWhereFieldVos the additionalWhereFieldVos to set
     */
    public void setAdditionalWhereFieldVos(final Collection<AdditionalWhereFieldVo> additionalWhereFieldVos) {
        this.additionalWhereFieldVos = additionalWhereFieldVos;
    }

    /**
     * @return the rootWhereFieldVo
     */
    public RootWhereFieldVo getRootWhereFieldVo() {
        return this.rootWhereFieldVo;
    }

    /**
     * @param rootWhereFieldVo the rootWhereFieldVo to set
     */
    public void setRootWhereFieldVo(final RootWhereFieldVo rootWhereFieldVo) {
        this.rootWhereFieldVo = rootWhereFieldVo;
    }
}
