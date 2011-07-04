package org.escidoc.core.services.fedora;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class DatastreamTypeListTO implements Collection<DatastreamTypeTO>, List<DatastreamTypeTO> {

    private final List<DatastreamTypeTOListener> datastreamTypeTOListener = new ArrayList<DatastreamTypeTOListener>();
    private final List<DatastreamTypeTO> internalArrayList = new ArrayList<DatastreamTypeTO>();

    public void addDatastreamTypeTOListener(final DatastreamTypeTOListener listener) {
        this.datastreamTypeTOListener.add(listener);
    }

    public void removeDatastreamTypeTOListener(final DatastreamTypeTOListener listener) {
        this.datastreamTypeTOListener.remove(listener);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return internalArrayList.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection c) {
        return internalArrayList.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection c) {
        return internalArrayList.addAll(index, c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return internalArrayList.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return internalArrayList.retainAll(c);
    }

    @Override
    public Iterator<DatastreamTypeTO> iterator() {
        return internalArrayList.iterator();
    }

    @Override
    public ListIterator<DatastreamTypeTO> listIterator() {
        return internalArrayList.listIterator();
    }

    @Override
    public ListIterator<DatastreamTypeTO> listIterator(final int index) {
        return internalArrayList.listIterator(index);
    }

    @Override
    public List<DatastreamTypeTO> subList(final int fromIndex, final int toIndex) {
        return internalArrayList.subList(fromIndex, toIndex);
    }

    public boolean equals(final Object o) {
        return internalArrayList.equals(o);
    }

    public int hashCode() {
        return internalArrayList.hashCode();
    }

    @Override
    public int size() {
        return internalArrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return internalArrayList.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return internalArrayList.contains(o);
    }

    @Override
    public int indexOf(final Object o) {
        return internalArrayList.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return internalArrayList.lastIndexOf(o);
    }

    @Override
    public Object[] toArray() {
        return internalArrayList.toArray();
    }

    @Override
    public <DatastreamTypeTO> DatastreamTypeTO[] toArray(final DatastreamTypeTO[] a) {
        return internalArrayList.toArray(a);
    }

    @Override
    public boolean add(final DatastreamTypeTO o) {
        DatastreamTypeTO result = o;
        for(final DatastreamTypeTOListener datastreamTypeTOListener : this.datastreamTypeTOListener) {
            result = datastreamTypeTOListener.process(result);
        }
        if(result == null) {
            return true;
        }
        return this.internalArrayList.add(result);
    }

    @Override
    public DatastreamTypeTO get(final int index) {
        return internalArrayList.get(index);
    }

    @Override
    public DatastreamTypeTO set(final int index, final DatastreamTypeTO element) {
        return this.internalArrayList.set(index, element);
    }

    @Override
    public void add(final int index, final DatastreamTypeTO element) {
        this.internalArrayList.add(index, element);
    }

    @Override
    public DatastreamTypeTO remove(final int index) {
        return internalArrayList.remove(index);
    }

    @Override
    public boolean remove(final Object o) {
        return internalArrayList.remove(o);
    }

    @Override
    public void clear() {
        internalArrayList.clear();
    }
}
