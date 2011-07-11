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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.renderer;

import de.escidoc.core.aa.business.persistence.UnsecuredActionList;
import de.escidoc.core.aa.business.renderer.interfaces.ActionRendererInterface;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.factory.ActionXmlProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Action renderer implementation using the velocity template engine.
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.business.renderer.VelocityXmlActionRenderer")
public class VelocityXmlActionRenderer extends AbstractRenderer implements ActionRendererInterface {

    /**
     * Pattern used to detect white spaces.
     */
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s");

    /**
     * See Interface for functional description.
     *
     * @see RoleRendererInterface #renderUnsecuredActionList(de.escidoc.core.aa.business.persistence.UnsecuredActionList)
     */
    @Override
    public String renderUnsecuredActionList(final UnsecuredActionList actions) throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        addRdfValues(values);
        values.put("contextId", actions.getContextId());

        final List<String> actionIdList;
        if (actions.getActionIds() != null) {
            final String[] actionIds = PATTERN_WHITESPACE.split(actions.getActionIds());
            actionIdList = new ArrayList<String>(actionIds.length);
            actionIdList.addAll(Arrays.asList(actionIds));
        }
        else {
            actionIdList = new ArrayList<String>(0);
        }
        values.put("actionIds", actionIdList);

        return getActionXmlProvider().getUnsecuredActionsXml(values);
    }

    /**
     * Gets the {@link ActionXmlProvider} object.
     *
     * @return Returns the {@link ActionXmlProvider} object.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private static ActionXmlProvider getActionXmlProvider() {

        return ActionXmlProvider.getInstance();
    }
}
