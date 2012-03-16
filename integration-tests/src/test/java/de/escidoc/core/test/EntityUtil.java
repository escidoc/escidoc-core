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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

/**
 * @author Michael Hoppe
 * 
 * Convenience-class that covers EntityUtils-Methods and methods on Entity-object
 * and checks for NotNull
 *
 */
public class EntityUtil {

    public static String toString(HttpEntity entity, String charEncoding) throws IOException {
        if (entity == null) {
            return "";
        }
        return EntityUtils.toString(entity, charEncoding);
    }

    public static void consumeContent(HttpEntity entity) throws IOException {
        if (entity != null) {
            entity.consumeContent();
        }
    }

    public static InputStream getContent(HttpEntity entity) throws IOException {
        if (entity != null) {
            return entity.getContent();
        }
        else {
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    public static byte[] toByteArray(HttpEntity entity) throws IOException {
        if (entity != null) {
            return EntityUtils.toByteArray(entity);
        }
        else {
            return new byte[0];
        }
    }

}
