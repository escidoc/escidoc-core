package de.escidoc.core.om.service.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;

public interface LifeCycleHandlerInterface {
	public String getLifecycleStatus(String id) throws EscidocException;
}
