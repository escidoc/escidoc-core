/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.util.service;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Class used to convert <br/> - objects that implement interface List (e.g. Vector, ArrayList) into typed arrays (e.g.
 * String []) <br/> - objects that implement interface Map (e.g. HashMap, Hashtable) into arrays of type KeyValuePair
 *
 * @author Roland Werner (Accenture)
 */
public final class CollectionsConverter {

    /**
     * Private Constructor, in order to prevent instantiation of this utility class.
     */
    private CollectionsConverter() {

    }

    /**
     * Generates an array of type KeyValuePair from the provided object of type Map.
     *
     * @param map The Map object to convert.
     * @return Array of type KeyValuePair, generated from the Map.
     */
    public static KeyValuePair[] convertMapToKeyValuePairs(final Map map) {
        // generate a new array of type KeyValuePair
        // with the same size as the Map
        final KeyValuePair[] keyValuePair = new KeyValuePair[map.size()];

        // iterate over the Map, get a key-value-pair each time
        int count = 0;

        for (final Object o : map.entrySet()) {

            // get an element as key-value pair
            final Entry entry = (Entry) o;

            // generate a new object of type KeyValuePair, set the values
            // from the Map.Entry and put the object at the next free place
            // in the keyValuePair array
            keyValuePair[count] = new KeyValuePair((String) entry.getKey(), (String) entry.getValue());
            count++;
        }

        return keyValuePair;
    }

    /**
     * Iterates over the objects of type KeyValuePair provided in keyValuePairs and puts them as key-value-pairs into a
     * new created object of the provided type (has to be a type that implements Map).
     *
     * @param keyValuePairs The array of KeyValuePairs to put into the Map.
     * @param objectClass   The concrete object type that should be created to fill with the array.
     * @return The new created type filled with the values from the array.
     */
    public static Map convertKeyValuePairsToMap(final KeyValuePair[] keyValuePairs, final Class objectClass) {
        // Generate new object of provided type
        final Map map;
        try {
            map = (Map) objectClass.getConstructor().newInstance();
        }
        catch (final IllegalAccessException e) {
            throw new RuntimeException("Cannot access constructor of class " + objectClass.getName(), e);
        }
        catch (final InstantiationException e) {
            throw new RuntimeException("Cannot instantiate class " + objectClass.getName(), e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot instantiate class " + objectClass.getName(), e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot instantiate class " + objectClass.getName(), e);
        }

        // iterate over the KeyValuePair array
        for (final KeyValuePair keyValuePair : keyValuePairs) {
            // put each KeyValuePair into the Map object
            map.put(keyValuePair.getKey(), keyValuePair.getValue());
        }

        return map;
    }

    /**
     * Converts the provided List into an array of the provided type.
     *
     * @param list        The List to convert.
     * @param objectClass The class type to create the array for.
     * @return The new create array.
     */
    public static Object[] convertListToArray(final Collection list, final Class objectClass) {
        final int length = list.size();
        Object[] array = (Object[]) Array.newInstance(objectClass, length);
        array = list.toArray(array);
        return array;
    }

    /**
     * Converts the provided array into an object of the provided type, which has to implement List (e.g. Vector).
     *
     * @param array       The array to convert.
     * @param objectClass The type of object to generate. Has to implement List.
     * @return The converted object or {@code null} if no array has been provided.
     */
    public static List convertArrayToList(final Object[] array, final Class objectClass) {

        return (List) convertArrayToCollection(array, objectClass);
    }

    /**
     * Converts the provided array into an object of the provided type, which has to implement Set (e.g. HashSet).
     *
     * @param array       The array to convert.
     * @param objectClass The type of object to generate. Has to implement Set.
     * @return The converted object or {@code null} if no array has been provided.
     */
    public static Set convertArrayToSet(final Object[] array, final Class objectClass) {

        return (Set) convertArrayToCollection(array, objectClass);
    }

    /**
     * Converts the provided array into an object of the provided type, which has to implement Collection (e.g.
     * HashSet).
     *
     * @param array       The array to convert.
     * @param objectClass The type of object to generate. Has to implement Collection.
     * @return The converted object or {@code null} if no array has been provided.
     */
    public static Collection convertArrayToCollection(final Object[] array, final Class objectClass) {

        if (array == null) {
            return null;
        }

        final List tempList = Arrays.asList(array);
        final Object[] params = { tempList };
        // Generate new object of provided type
        final Collection collection;
        try {
            // get the constructor of objectClass that takes a Collection
            // as input parameter
            final Class[] paramsTypes = { Collection.class };
            final Constructor constructor = objectClass.getConstructor(paramsTypes);
            // generates a new List object using the found constructor
            collection = (Collection) constructor.newInstance(params);
        }
        catch (final NoSuchMethodException e) {
            throw new RuntimeException("Cannot find constructor of class " + objectClass.getName()
                + " which accepts a Collection as argument.", e);
        }
        catch (final InvocationTargetException e) {
            throw new RuntimeException("Cannot execute constructor of class " + objectClass.getName()
                + " because it throws an Exception.", e);
        }
        catch (final IllegalAccessException e) {
            throw new RuntimeException("Cannot access constructor of class " + objectClass.getName(), e);
        }
        catch (final InstantiationException e) {
            throw new RuntimeException("Cannot instantiate class " + objectClass.getName(), e);
        }

        return collection;
    }

}
