package org.escidoc.core.services.fedora;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class DatastreamTypeListTO implements Collection<DatastreamTypeTO>, List<DatastreamTypeTO> {

    private final List<DatastreamTypeTOListener> datastreamTypeTOListener = new ArrayList<DatastreamTypeTOListener>();
    private final ArrayList<DatastreamTypeTO> internalArrayList = new ArrayList<DatastreamTypeTO>();

    public void addDatastreamTypeTOListener(DatastreamTypeTOListener listener) {
        this.datastreamTypeTOListener.add(listener);
    }

    public void removeDatastreamTypeTOListener(DatastreamTypeTOListener listener) {
        this.datastreamTypeTOListener.remove(listener);
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
    public Iterator<DatastreamTypeTO> iterator() {
        return internalArrayList.iterator();
    }

    @Override
    public ListIterator<DatastreamTypeTO> listIterator() {
        return internalArrayList.listIterator();
    }

    @Override
    public ListIterator<DatastreamTypeTO> listIterator(int index) {
        return internalArrayList.listIterator(index);
    }

    @Override
    public List<DatastreamTypeTO> subList(int fromIndex, int toIndex) {
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
    public <DatastreamTypeTO> DatastreamTypeTO[] toArray(DatastreamTypeTO[] a) {
        return internalArrayList.toArray(a);
    }

    @Override
    public boolean add(DatastreamTypeTO o) {
        DatastreamTypeTO result = o;
        for(DatastreamTypeTOListener datastreamTypeTOListener : this.datastreamTypeTOListener) {
            result = datastreamTypeTOListener.process(result);
        }
        if(result == null) {
            return true;
        }
        return this.internalArrayList.add(result);
    }

    @Override
    public DatastreamTypeTO get(int index) {
        return internalArrayList.get(index);
    }

    @Override
    public DatastreamTypeTO set(int index, DatastreamTypeTO element) {
        return this.internalArrayList.set(index, element);
    }

    @Override
    public void add(int index, DatastreamTypeTO element) {
        this.internalArrayList.add(index, element);
    }

    @Override
    public DatastreamTypeTO remove(int index) {
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
