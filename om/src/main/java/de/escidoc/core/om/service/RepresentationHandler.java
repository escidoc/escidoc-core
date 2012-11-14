package de.escidoc.core.om.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.om.business.scape.ScapeUtil;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
import de.escidoc.core.om.service.interfaces.RepresentationHandlerInterface;
import de.escidoc.core.resources.om.item.Item;
import eu.scapeproject.model.Representation;
import eu.scapeproject.model.Representation.Builder;
import eu.scapeproject.model.mets.SCAPEMarshaller;

@Service("service.RepresentationHandler")
public class RepresentationHandler implements RepresentationHandlerInterface {

	@Autowired
	@Qualifier("business.RepresentationHandler")
	private de.escidoc.core.om.business.interfaces.RepresentationHandlerInterface handler;
	
	@Override
	public String getRepresentation(String id) throws EscidocException {
		return handler.getRepresentation(id);
	}

	@Override
	public String updateRepresentation(String xml) throws EscidocException{
		return handler.updateRepresentation(xml);
	}
}
