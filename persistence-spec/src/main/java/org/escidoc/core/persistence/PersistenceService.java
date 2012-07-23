package org.escidoc.core.persistence;

import java.io.IOException;

import org.escidoc.core.business.domain.base.ID;

/**
 * PersistenceService is the user interface for the persistence layer
 * @author fasseg
 *
 */
public interface PersistenceService {
	public <T> void save(T object,boolean overwrite) throws IOException;
	public <T> T load(ID id,Class<T> type) throws IOException;
	public <T> void delete(ID id,Class<T> type) throws IOException;
}
