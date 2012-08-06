/**
 * 
 */
package org.escidoc.core.domain.service;

import de.escidoc.core.common.exceptions.system.SystemException;


public abstract class EntityMapperTranslator<JAXBElement, DomainObject>
  {
      protected abstract DomainObject To2Do(JAXBElement value, String validationProfile) throws SystemException;
   
      protected abstract JAXBElement Do2To(DomainObject value) throws SystemException;
  } 