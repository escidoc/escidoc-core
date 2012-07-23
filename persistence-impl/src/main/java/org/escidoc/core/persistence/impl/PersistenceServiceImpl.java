package org.escidoc.core.persistence.impl;

import java.io.IOException;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.persistence.PersistenceImplementor;
import org.escidoc.core.persistence.PersistenceService;

/**
 * Implements the bridge pattern by decoupling abstraction and API
 * @author fasseg
 * This could be changed to PersistanceServiceImpl<T> in order to provide
 * different services for different objects types. E.g. Context use in PersistanceServiceImpl<Context>
 * while Items use PersistanceServiceImpl<Item>. But this would mean having as much services as data 
 * transfer objects required to be persisted via the API
 */
public final class PersistenceServiceImpl implements PersistenceService {
	private final PersistenceImplementor implementor;

	public PersistenceServiceImpl(PersistenceImplementor implementor) {
		this.implementor = implementor;
	}

	public <T> void delete(ID id,Class<T> type) throws IOException {
		implementor.delete(id,type);
	}

	public <T> T load(ID id, Class<T> type) throws IOException {
		return implementor.load(id,type);
	}

	public <T> void save(T object,boolean overwrite) throws IOException {
		implementor.save(object,overwrite);
	}

}
