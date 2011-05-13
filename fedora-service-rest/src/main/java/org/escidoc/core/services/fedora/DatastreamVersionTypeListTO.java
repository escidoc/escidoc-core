package org.escidoc.core.services.fedora;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class DatastreamVersionTypeListTO implements Collection<DatastreamVersionTypeTO>, List<DatastreamVersionTypeTO> {

    private List<DatastreamVersionTypeTOListener> datastreamVersionTypeTOListener = new ArrayList<DatastreamVersionTypeTOListener>();
    private final ArrayList<DatastreamVersionTypeTO> internalArrayList = new ArrayList<DatastreamVersionTypeTO>();

    public void addDatastreamVersionTypeTOListener(DatastreamVersionTypeTOListener listener) {
        this.datastreamVersionTypeTOListener.add(listener);
    }

    public void removeDatastreamVersionTypeTOListener(DatastreamVersionTypeTOListener listener) {
        this.datastreamVersionTypeTOListener.remove(listener);
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

    public Iterator<DatastreamVersionTypeTO> iterator() {
        return internalArrayList.iterator();
    }

    public ListIterator<DatastreamVersionTypeTO> listIterator() {
        return internalArrayList.listIterator();
    }

    public ListIterator<DatastreamVersionTypeTO> listIterator(int index) {
        return internalArrayList.listIterator(index);
    }

    public List<DatastreamVersionTypeTO> subList(int fromIndex, int toIndex) {
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

    public <DatastreamVersionTypeTO> DatastreamVersionTypeTO[] toArray(DatastreamVersionTypeTO[] a) {
        return internalArrayList.toArray(a);
    }

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

    public DatastreamVersionTypeTO get(int index) {
        return internalArrayList.get(index);
    }

    public DatastreamVersionTypeTO set(int index, DatastreamVersionTypeTO element) {
        return this.internalArrayList.set(index, element);
    }

    public void add(int index, DatastreamVersionTypeTO element) {
        this.internalArrayList.add(index, element);
    }

    public DatastreamVersionTypeTO remove(int index) {
        return internalArrayList.remove(index);
    }

    public boolean remove(Object o) {
        return internalArrayList.remove(o);
    }

    public void clear() {
        internalArrayList.clear();
    }
}
