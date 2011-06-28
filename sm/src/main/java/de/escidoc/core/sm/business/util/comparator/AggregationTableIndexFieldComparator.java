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
 * Copyright 2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */

package de.escidoc.core.sm.business.util.comparator;

import de.escidoc.core.sm.business.persistence.hibernate.AggregationTableIndexField;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Sorts AggregationTableIndexField-Objects according to their listIndex.
 *
 * @author Michael Hoppe
 */
public class AggregationTableIndexFieldComparator implements Comparator<AggregationTableIndexField>, Serializable {

    private static final long serialVersionUID = 811973896034842587L;

    /**
     * compares listIndex.
     *
     * @param a1 AggregationTableIndexField1
     * @param a2 AggregationTableIndexField2
     * @return Returns compare result.
     */
    @Override
    public int compare(final AggregationTableIndexField a1, final AggregationTableIndexField a2) {
        return Integer.toString(a1.getListIndex()).compareTo(Integer.toString(a2.getListIndex()));
    }
}
