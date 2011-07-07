package org.escidoc.core.services.fedora;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class DatastreamVersionTypeListTO
        implements Collection<DatastreamVersionTypeTO>, List<DatastreamVersionTypeTO> {

    private final List<DatastreamVersionTypeTOListener> datastreamVersionTypeTOListener =
            new ArrayList<DatastreamVersionTypeTOListener>();
    private final List<DatastreamVersionTypeTO> internalArrayList = new ArrayList<DatastreamVersionTypeTO>();

    public void addDatastreamVersionTypeTOListener(final DatastreamVersionTypeTOListener listener) {
        this.datastreamVersionTypeTOListener.add(listener);
    }

    public void removeDatastreamVersionTypeTOListener(final DatastreamVersionTypeTOListener listener) {
        this.datastreamVersionTypeTOListener.remove(listener);
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
    public Iterator<DatastreamVersionTypeTO> iterator() {
        return internalArrayList.iterator();
    }

    @Override
    public ListIterator<DatastreamVersionTypeTO> listIterator() {
        return internalArrayList.listIterator();
    }

    @Override
    public ListIterator<DatastreamVersionTypeTO> listIterator(final int index) {
        return internalArrayList.listIterator(index);
    }

    @Override
    public List<DatastreamVersionTypeTO> subList(final int fromIndex, final int toIndex) {
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
    public <DatastreamVersionTypeTO> DatastreamVersionTypeTO[] toArray(final DatastreamVersionTypeTO[] a) {
        return internalArrayList.toArray(a);
    }

    @Override
    public boolean add(final DatastreamVersionTypeTO o) {
        DatastreamVersionTypeTO result = o;
        for(final DatastreamVersionTypeTOListener datastreamTypeTOListener : this.datastreamVersionTypeTOListener) {
            result = datastreamTypeTOListener.process(result);
        }
        if(result == null) {
            return true;
        }
        return this.internalArrayList.add(result);
    }

    @Override
    public DatastreamVersionTypeTO get(final int index) {
        return internalArrayList.get(index);
    }

    @Override
    public DatastreamVersionTypeTO set(final int index, final DatastreamVersionTypeTO element) {
        return this.internalArrayList.set(index, element);
    }

    @Override
    public void add(final int index, final DatastreamVersionTypeTO element) {
        this.internalArrayList.add(index, element);
    }

    @Override
    public DatastreamVersionTypeTO remove(final int index) {
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
