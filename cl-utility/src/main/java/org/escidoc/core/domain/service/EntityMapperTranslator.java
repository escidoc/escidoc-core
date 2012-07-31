/**
 * 
 */
package org.escidoc.core.domain.service;


public abstract class EntityMapperTranslator<JAXBElement, DomainObject>
  {
      protected abstract DomainObject To2Do(JAXBElement value);
   
      protected abstract JAXBElement Do2To(DomainObject value);
  } 