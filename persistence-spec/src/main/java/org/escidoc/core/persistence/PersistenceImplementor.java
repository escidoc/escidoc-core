package org.escidoc.core.persistence;

import java.io.IOException;

import org.escidoc.core.business.domain.base.ID;

/**
 * The implementor interface for the Persistence Service
 * @author ruckus
 *
 */
public interface PersistenceImplementor {
	public void save(Object blob,boolean overwrite) throws IOException;
	public <T> void delete(ID id,Class<T> type) throws IOException;
	public <T> T load(ID id,Class<T> type) throws IOException;
}
