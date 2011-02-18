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
/**
 * 
 */
package de.escidoc.core.om.business.renderer;

import de.escidoc.core.common.business.fedora.resources.GenericResource;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.List;

/**
 * @deprecated Item renderer interface is implemented by item retrieve handler.
 * 
 * @author FRS
 * 
 */
class VelocityXmlItemRenderer {

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#render(de.escidoc.core.common.business.fedora.resources.Item)
     */
    // public String render(Item item) throws SystemException {
    // String result = null;
    //
    // Map<String, String> values = new HashMap<String, String>();
    // values.put(XmlTemplateProvider.VAR_OBJID, item.getId());
    // values.put(XmlTemplateProvider.VAR_TITLE, item.getTitle());
    // values.put(XmlTemplateProvider.VAR_HREF, item.getHref());
    // // values.put(XmlTemplateProvider.VAR_LAST_MODIFICATION_DATE, item
    // // .getLastModifiedDate());
    // // values.put(XmlTemplateProvider.VAR_NAMESPACE,
    // // de.escidoc.core.common.business.Constants.ITEM_NAMESPACE_URI);
    // // values.put(XmlTemplateProvider.VAR_NAMESPACE_PREFIX,
    // // de.escidoc.core.common.business.Constants.ITEM_NAMESPACE_PREFIX);
    // // values.put(XmlTemplateProvider.VAR_ESCIDOC_BASE_URL, XmlUtility
    // // .getEscidocBaseUrl());
    // values.putAll(getCommonValues(item));
    // values.putAll(getPropertiesValues(item));
    // values.put(XmlTemplateProvider.VAR_MD_RECORDS_CONTENT,
    // renderMdRecords(item));
    // values.put(XmlTemplateProvider.VAR_COMPONENTS_CONTENT,
    // renderComponents(item));
    // values.putAll(getRelationValues(item));
    // values.putAll(getResourcesValues(item));
    //
    // result = ItemXmlProvider.getInstance().getItemXml(values);
    //
    // return result;
    //
    // }
    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderComponent(de.escidoc.core.common.business.fedora.resources.Item)
     */
    public static String renderComponent(GenericResource item) throws WebserverSystemException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderComponents(de.escidoc.core.common.business.fedora.resources.Item)
     */

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderItemRefs(java.util.List)
     */
    public static String renderItemRefs(List<String> itemRefs) throws SystemException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderItems(java.util.List)
     */
    public static String renderItems(List<String> items) throws SystemException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderMdRecord(de.escidoc.core.common.business.fedora.resources.Item)
     */
    // public String renderMdRecord(Item item) throws WebserverSystemException,
    // IntegritySystemException {
    // Map<String, String> values = new HashMap<String, String>();
    // values.putAll(getCommonValues(item));
    // values.put(XmlTemplateProvider.VAR_MD_RECORD_NAME, "escidoc");
    // values.put(XmlTemplateProvider.VAR_MD_RECORD_HREF, item.getHref()
    // + "/md-records/md-record/escidoc");
    // try {
    // values.put(XmlTemplateProvider.VAR_MD_RECORD_CONTENT, item.getMdRecord(
    // "escidoc").toString());
    // }
    // catch (FedoraSystemException e) {
    // throw new IntegritySystemException("Can not retrieve md-record
    // escidoc.");
    // }
    // catch (StreamNotFoundException e) {
    // throw new IntegritySystemException("Can not retrieve md-record
    // escidoc.");
    // }
    // return null;
    // }
    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderMdRecords(de.escidoc.core.common.business.fedora.resources.Item)
     */

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderProperties(de.escidoc.core.common.business.fedora.resources.Item)
     */
    public static String renderProperties(GenericResource item) throws WebserverSystemException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderRelations(de.escidoc.core.common.business.fedora.resources.Item)
     */
    public static String renderRelations(GenericResource item) throws WebserverSystemException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.renderer.interfaces.ItemRendererInterface#renderResources(de.escidoc.core.common.business.fedora.resources.Item)
     */
    public static String renderResources(GenericResource item) throws WebserverSystemException {
        // TODO Auto-generated method stub
        return null;
    }

}
