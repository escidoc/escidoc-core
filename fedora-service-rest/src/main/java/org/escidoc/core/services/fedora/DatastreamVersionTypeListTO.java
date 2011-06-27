package org.escidoc.core.services.fedora;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class DatastreamVersionTypeListTO
        implements Collection<DatastreamVersionTypeTO>, List<DatastreamVersionTypeTO> {

    private List<DatastreamVersionTypeTOListener> datastreamVersionTypeTOListener =
            new ArrayList<DatastreamVersionTypeTOListener>();
    private final ArrayList<DatastreamVersionTypeTO> internalArrayList = new ArrayList<DatastreamVersionTypeTO>();

    public void addDatastreamVersionTypeTOListener(DatastreamVersionTypeTOListener listener) {
        this.datastreamVersionTypeTOListener.add(listener);
    }

    public void removeDatastreamVersionTypeTOListener(DatastreamVersionTypeTOListener listener) {
        this.datastreamVersionTypeTOListener.remove(listener);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return internalArrayList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection c) {
        return internalArrayList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return internalArrayList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return internalArrayList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
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
    public ListIterator<DatastreamVersionTypeTO> listIterator(int index) {
        return internalArrayList.listIterator(index);
    }

    @Override
    public List<DatastreamVersionTypeTO> subList(int fromIndex, int toIndex) {
        return internalArrayList.subList(fromIndex, toIndex);
    }

    public boolean equals(Object o) {
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
    public boolean contains(Object o) {
        return internalArrayList.contains(o);
    }

    @Override
    public int indexOf(Object o) {
        return internalArrayList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return internalArrayList.lastIndexOf(o);
    }

    @Override
    public Object[] toArray() {
        return internalArrayList.toArray();
    }

    @Override
    public <DatastreamVersionTypeTO> DatastreamVersionTypeTO[] toArray(DatastreamVersionTypeTO[] a) {
        return internalArrayList.toArray(a);
    }

    @Override
    public boolean add(DatastreamVersionTypeTO o) {
        DatastreamVersionTypeTO result = o;
        for(DatastreamVersionTypeTOListener datastreamTypeTOListener : this.datastreamVersionTypeTOListener) {
            result = datastreamTypeTOListener.process(result);
        }
        if(result == null) {
            return true;
        }
        return this.internalArrayList.add(result);
    }

    @Override
    public DatastreamVersionTypeTO get(int index) {
        return internalArrayList.get(index);
    }

    @Override
    public DatastreamVersionTypeTO set(int index, DatastreamVersionTypeTO element) {
        return this.internalArrayList.set(index, element);
    }

    @Override
    public void add(int index, DatastreamVersionTypeTO element) {
        this.internalArrayList.add(index, element);
    }

    @Override
    public DatastreamVersionTypeTO remove(int index) {
        return internalArrayList.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        return internalArrayList.remove(o);
    }

    @Override
    public void clear() {
        internalArrayList.clear();
    }
}
