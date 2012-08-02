package org.escidoc.core.persistence.impl;

import java.io.IOException;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.om.context.ContextDO;
import org.escidoc.core.business.domain.om.item.ItemDO;
import org.escidoc.core.business.util.aspect.ValidationProfile;
import org.escidoc.core.persistence.PersistenceImplementor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Realization of a PersistenceImplementor for Fedora storage
 * 
 * @author ruckus
 * 
 */
@Service("persistence.FedoraImplementor")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FedoraPersistenceImplementor implements PersistenceImplementor {
	
	public <T> void delete(ID id, Class<T> type) throws IOException {
		if (type == ItemDO.class){
			deleteItem(id);
		}else if (type == ContextDO.class){
			deleteContext(id);
		}else{
			throw new IOException("Unable to delete objects of type " + type.getName());
		}
	}

	private void deleteContext(ID id) throws IOException{
	}

	private boolean isContextInUse(ContextDO ctx) throws IOException{
	    return false;
	}

	private void deleteItem(ID id) throws IOException{
	}

	public <T> T load(ID id, Class<T> type) throws IOException {
		if (type == ContextDO.class) {
			return (T) loadContext(id);
		} else if (type == ItemDO.class) {
			return (T) loadItem(id);
		} else {
			throw new IOException("Unable to load objects of type " + type.getName());
		}
	}

	private ContextDO loadContext(ID id) throws IOException {
	    return new ContextDO();
	}

	private ItemDO loadItem(ID id) throws IOException {
	    ItemDO.Builder b = new ItemDO.Builder(ValidationProfile.EXISTS);
	    return b.id(new ID("1")).build();
	}

	public void save(final Object obj, final boolean overwrite) throws IOException {
		if (obj instanceof ContextDO) {
			saveContext((ContextDO) obj, overwrite);
		} else if (obj instanceof ItemDO) {
			saveItem((ItemDO) obj, overwrite);
		} else {
			throw new IOException("Unable to store objects of type " + obj.getClass());
		}
	}

	private void saveContext(final ContextDO context, final boolean overwrite) throws IOException {
	}

	private void saveItem(final ItemDO item, final boolean overwrite) throws IOException {
	}

}
