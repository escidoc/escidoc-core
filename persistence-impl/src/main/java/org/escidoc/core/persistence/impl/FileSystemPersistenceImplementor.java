package org.escidoc.core.persistence.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.om.context.ContextDO;
import org.escidoc.core.business.domain.om.item.ItemDO;
import org.escidoc.core.persistence.PersistenceImplementor;

/**
 * Realization of a PersistenceImplementor for FileSystem storage
 * 
 * @author ruckus
 * 
 */
public class FileSystemPersistenceImplementor implements PersistenceImplementor {
	
	private static File getDirectory(final String path) {
		File f = new File(path);
		if (!f.exists()) {
			f.mkdir();
		} else if (!f.isDirectory() || !f.canWrite() || !f.canRead()) {
			throw new RuntimeException("unable to write to directory " + f.getAbsolutePath());
		}
		return f;
	}

	private final Marshaller marshaller;
	private final Unmarshaller unmarshaller;
	private final File directory;
	private final File contextDirectory;

	private final File itemDirectory;

	FileSystemPersistenceImplementor(final String path) {
		super();
		try {
			JAXBContext ctx = JAXBContext.newInstance(ContextDO.class, ID.class, ItemDO.class);
			this.marshaller = ctx.createMarshaller();
			this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			this.unmarshaller = ctx.createUnmarshaller();
			this.directory = FileSystemPersistenceImplementor.getDirectory(path);
			this.contextDirectory = FileSystemPersistenceImplementor.getDirectory(path + "/context");
			this.itemDirectory = FileSystemPersistenceImplementor.getDirectory(path + "/item");
		} catch (JAXBException jaxb) {
			throw new RuntimeException("Unable to instantiate Marshaller", jaxb);
		}
	}

	private void assertDirectoryAccessible(File dir) throws IOException {
		if (!dir.exists() || !dir.canRead() || !dir.canWrite() || !dir.isDirectory()) {
			throw new IOException("Unable to read/write to item directory " + itemDirectory.getAbsolutePath());
		}
	}

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
		assertDirectoryAccessible(contextDirectory);
		ContextDO ctx=loadContext(id);
		if (isContextInUse(ctx)){
			throw new IOException("Unable to delete a used context");
		}
		File f=new File(contextDirectory,id.getValue().toString() + ".xml");
		deleteFile(f);
	}

	private boolean isContextInUse(ContextDO ctx) throws IOException{
		for (String name:itemDirectory.list()){
			if (name.endsWith(".xml")){
				File f=new File(itemDirectory.getAbsolutePath()  + "/" + name);
				InputStream is=null;
				try{
					is=new FileInputStream(f);
					ItemDO i = (ItemDO) unmarshal(is);
					if (i.getProperties().getContext().equals(ctx.getId())){
						return true;
					}
				}finally{
					IOUtils.closeQuietly(is);
				}
			}
		}
		return false;
	}

	private void deleteFile(File f) throws IOException{
		if (!f.exists() || !f.canWrite()){
			throw new IOException("Unable to delete file " + f.getAbsolutePath());
		}
		f.delete();
	}

	private void deleteItem(ID id) throws IOException{
		assertDirectoryAccessible(itemDirectory);
		File f = new File(itemDirectory,id.getValue().toString() + ".xml");
		deleteFile(f);
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
		assertDirectoryAccessible(contextDirectory);
		InputStream is = null;
		try {
			is = new FileInputStream(new File(contextDirectory, id.getValue().toString() + ".xml"));
			return (ContextDO) unmarshal(is);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	private ItemDO loadItem(ID id) throws IOException {
		assertDirectoryAccessible(itemDirectory);
		InputStream itemStream = null;
		try {
			itemStream = new FileInputStream(new File(itemDirectory, id.getValue().toString() + ".xml"));
			return (ItemDO) unmarshal(itemStream);
		} finally {
			IOUtils.closeQuietly(itemStream);
		}
	}

	private void marshal(final Object blob, final OutputStream out) {
		try {
			marshaller.marshal(blob, out);
		} catch (JAXBException e) {
			throw new RuntimeException("Unable to marshall oject", e);
		}
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
		final File f = new File(contextDirectory, context.getId().getValue().toString() + ".xml");
		assertDirectoryAccessible(contextDirectory);
		if (f.exists() && !overwrite) {
			throw new IOException(context.getId().toString() + " already exists in "
					+ contextDirectory.getAbsolutePath());
		}
		OutputStream os = null;
		try {
			os = new FileOutputStream(f);
			this.marshal(context, os);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}

	private void saveItem(final ItemDO item, final boolean overwrite) throws IOException {
		final File f = new File(itemDirectory, item.getId().getValue().toString() + ".xml");
		assertDirectoryAccessible(itemDirectory);
		if (f.exists() && !overwrite) {
			throw new IOException(item.getId().toString() + " already exists in "
					+ itemDirectory.getAbsolutePath());
		}
		OutputStream os = null;
		try {
			os = new FileOutputStream(f);
			this.marshal(item, os);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}

	private Object unmarshal(final InputStream is) {
		try {
			return unmarshaller.unmarshal(is);
		} catch (JAXBException e) {
			throw new RuntimeException("Unable to marshall oject", e);
		}
	}
}
