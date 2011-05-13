package org.escidoc.core.services.fedora;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class DatastreamTypeListTO implements Collection<DatastreamTypeTO>, List<DatastreamTypeTO> {

    private List<DatastreamTypeTOListener> datastreamTypeTOListener = new ArrayList<DatastreamTypeTOListener>();
    private final ArrayList<DatastreamTypeTO> internalArrayList = new ArrayList<DatastreamTypeTO>();

    public void addDatastreamTypeTOListener(DatastreamTypeTOListener listener) {
        this.datastreamTypeTOListener.add(listener);
    }

    public void removeDatastreamTypeTOListener(DatastreamTypeTOListener listener) {
        this.datastreamTypeTOListener.remove(listener);
    }

    public boolean containsAll(Collection<?> c) {
        return internalArrayList.containsAll(c);
    }

    public boolean addAll(Collection c) {
        return this.addAll(c);
    }

    public boolean addAll(int index, Collection c) {
        return this.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return internalArrayList.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return internalArrayList.retainAll(c);
    }

    public Iterator<DatastreamTypeTO> iterator() {
        return internalArrayList.iterator();
    }

    public ListIterator<DatastreamTypeTO> listIterator() {
        return internalArrayList.listIterator();
    }

    public ListIterator<DatastreamTypeTO> listIterator(int index) {
        return internalArrayList.listIterator(index);
    }

    public List<DatastreamTypeTO> subList(int fromIndex, int toIndex) {
        return internalArrayList.subList(fromIndex, toIndex);
    }

    public boolean equals(Object o) {
        return internalArrayList.equals(o);
    }

    public int hashCode() {
        return internalArrayList.hashCode();
    }

    public int size() {
        return internalArrayList.size();
    }

    public boolean isEmpty() {
        return internalArrayList.isEmpty();
    }

    public boolean contains(Object o) {
        return internalArrayList.contains(o);
    }

    public int indexOf(Object o) {
        return internalArrayList.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return internalArrayList.lastIndexOf(o);
    }

    public Object[] toArray() {
        return internalArrayList.toArray();
    }

    public <DatastreamTypeTO> DatastreamTypeTO[] toArray(DatastreamTypeTO[] a) {
        return internalArrayList.toArray(a);
    }

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

    public DatastreamTypeTO get(int index) {
        return internalArrayList.get(index);
    }

    public DatastreamTypeTO set(int index, DatastreamTypeTO element) {
        return this.internalArrayList.set(index, element);
    }

    public void add(int index, DatastreamTypeTO element) {
        this.internalArrayList.add(index, element);
    }

    public DatastreamTypeTO remove(int index) {
        return internalArrayList.remove(index);
    }

    public boolean remove(Object o) {
        return internalArrayList.remove(o);
    }

    public void clear() {
        internalArrayList.clear();
    }
}
