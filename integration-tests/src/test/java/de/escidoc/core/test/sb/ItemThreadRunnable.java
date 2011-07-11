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
package de.escidoc.core.test.sb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created on 01.06.2005
 * 
 */

/**
 * @author Michael Hoppe
 */
public class ItemThreadRunnable implements Runnable {

    /**
     * runs the Thread.
     */
    public void run() {
        ItemHelper item = new ItemHelper();
        try {
            String xml = item.create(item.getTemplateAsString("escidoc_item_198_for_create.xml"));
            String id = getId(xml);
            xml = item.retrieve(id);
        }
        catch (final Exception e) {
        }
    }

    /**
     * extract id out of item-xml.
     *
     * @param xml String xml
     * @return String id
     */
    private String getId(final String xml) {
        String id = null;
        Pattern objidAttributePattern = Pattern.compile("objid=\"([^\"]*)\"");
        Matcher m = objidAttributePattern.matcher(xml);
        if (m.find()) {
            id = m.group(1);
        }
        return id;
    }

}
