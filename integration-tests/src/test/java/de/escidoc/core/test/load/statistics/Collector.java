/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.load.statistics;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Collector for invocation statistics.
 * 
 * @author MSC
 * 
 */
public final class Collector {

    private static SortedMap records = new TreeMap();

    private static long started = System.currentTimeMillis();

    /**
     * Avoids creating an instance of this utility class.
     */
    private Collector() {
    }

    /**
     * Delete all series of measurements and reset the runtime..
     * 
     * @common
     */
    public static void reset() {

        records = new TreeMap();
        started = System.currentTimeMillis();
    }

    /**
     * Reset the specified series of measurements.
     * 
     * @param label
     *            The label of the series of measurements .
     * @common
     */
    public static void reset(final String label) {

        records.remove(label);
    }

    /**
     * Set the start time for the series of measurements to now.
     * 
     * @param caller
     *            The caller.
     * @param callee
     *            The callee.
     * @param calledMethodName
     *            The name of the called method for which the measurement shall
     *            be started.
     * @param postfix
     *            The optional postfix of the label.
     * @return Returns the label created for the started measurement:
     *         &quot;[callerClassName] invokes [calleeClassName.methodName]
     *         postfix&quot;
     * @common
     */
    public static String setStart(
        final Object caller, final Object callee,
        final String calledMethodName, final String postfix) {

        String callerClassName = caller.getClass().getSimpleName();
        String calleeClassName = callee.getClass().getSimpleName();

        String label =
            "[" + callerClassName + "] invokes [" + calleeClassName + "."
                + calledMethodName + "]";
        if (postfix != null) {
            label = label + postfix;
        }
        Collector.setStart(label, System.currentTimeMillis());
        return label;
    }

    /**
     * Set the start time for the series of measurements to now.
     * 
     * @param callee
     *            The called object.
     * @param postfix
     *            The optional postfix of the label.
     * @return Returns the label created for the started measurement:<br>
     *         &quot;[calleeClassName] postfix&quot;
     * @common
     */
    public static String setStart(final Object callee, final String postfix) {

        String calleeClassName = callee.getClass().getSimpleName();
        String label = "[" + calleeClassName + "] ";
        if (postfix != null) {
            label = label + postfix;
        }
        Collector.setStart(label, System.currentTimeMillis());
        return label;
    }

    /**
     * Set the start time for the series of measurements to now.
     * 
     * @param label
     *            The label of the series of measurements .
     * @common
     */
    public static void setStart(final String label) {

        Collector.setStart(label, System.currentTimeMillis());
    }

    /**
     * Set the start time for the series of measurements.
     * 
     * @param label
     *            The label of the series of measurements .
     * @param time
     *            The start time.
     * @common
     */
    public static void setStart(final String label, final long time) {

        Measurement measuring = (Measurement) records.get(label);
        if (measuring == null) {
            measuring = new Measurement(label, time);
        }
        else {
            measuring.setStart(time);
        }
        records.put(label, measuring);
    }

    /**
     * Set the end time for the series of measurements to now.
     * 
     * @param label
     *            The label of the series of measurements.
     * @common
     */
    public static void setEnd(final String label) {

        Collector.setEnd(label, System.currentTimeMillis());
    }

    /**
     * Set the end time for the series of measurements.
     * 
     * @param label
     *            The label of the series of measurements .
     * @param time
     *            The end time.
     * @common
     */
    public static void setEnd(final String label, final long time) {

        Measurement measuring = (Measurement) records.get(label);
        if (measuring != null) {
            measuring.setEnd(time);
            records.put(label, measuring);
        }

    }

    /**
     * Get an xml representation of the collected statistics.
     * 
     * @return The xml representation of the collected statistics.
     * @common
     */
    public static String getMessage() {

        long runtime = System.currentTimeMillis() - started;
        // String result = "<?xml version=\"1.0\" encoding=\"UTF-8\""
        // + " standalone=\"yes\"?>\n<statistics application=\"escidoc\""
        // + " runtime=\"" + runtime + "\">\n";
        String result =
            "<statistics application=\"Escidoc Tests\"" + " runtime=\""
                + runtime + "\">\n";
        synchronized (records) {
            Iterator recordIter = records.values().iterator();
            while (recordIter.hasNext()) {
                Measurement m = (Measurement) recordIter.next();
                result += "  " + m.getMessage() + "\n";
            }
        }
        result += "</statistics>";
        return result;
    }

    /**
     * Merges all measurements which have a key containing substring.
     * 
     * @param substring
     *            The substring to find in the keys.
     * @param label
     *            The label of the mergs measurement.
     * @param keep
     *            Indicates if the old measurements should be kept after
     *            merging.
     */
    public static void merge(
        final String substring, final String label, final boolean keep) {
        synchronized (records) {
            boolean merged = false;
            Measurement measuring = (Measurement) records.get(label);
            if (measuring == null) {
                measuring = new Measurement(label);
            }
            Iterator keyIter = records.keySet().iterator();
            while (keyIter.hasNext()) {
                String key = (String) keyIter.next();
                if (key.indexOf(substring) != -1) {
                    measuring.merge((Measurement) records.get(key));
                    merged = true;
                    if (!keep) {
                        records.remove(key);
                    }
                }
            }
            if (merged) {
                records.put(label, measuring);
            }
        }
    }
}
