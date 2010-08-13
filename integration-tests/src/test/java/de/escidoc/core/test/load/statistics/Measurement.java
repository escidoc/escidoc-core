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

/**
 * Mesurement.
 * 
 * @author MSC
 * 
 */
public class Measurement {

    private String label;

    private long start = 0;;

    private long end = 0;

    private long min = Long.MAX_VALUE;

    private long max = 0;

    private long sum = 0;

    private int measurings = 0;

    /**
     * Constructor.
     * 
     * @param label
     *            The label.
     * @common
     */
    public Measurement(final String label) {
        super();
        this.label = label;
        reset();
    }

    /**
     * Constructor.
     * 
     * @param label
     *            The label.
     * @param start
     *            The first start time.
     * @common
     */
    public Measurement(final String label, final long start) {
        super();
        this.label = label;
        reset();
        this.start = start;
    }

    /**
     * Discard all saved data.
     * 
     * @common
     * 
     */
    public void reset() {

        this.start = 0;
        this.end = 0;
        this.min = Long.MAX_VALUE;
        this.max = 0;
        this.sum = 0;
        this.measurings = 0;
    }

    /**
     * Get an xml representation of the collected statistics.
     * 
     * @return The xml representation of the collected statistics.
     * @common
     */
    public String getMessage() {
        String result =
            "<record " + getXmlAtt("label", this.label) + " "
                + getXmlAtt("invocations", this.measurings) + " "
                + getXmlAtt("avgTime", getAvg()) + " "
                + getXmlAtt("minTime", this.min) + " "
                + getXmlAtt("maxTime", this.max) + " />";
        return result;
    }

    /**
     * @return Returns the average time.
     * @common
     */
    public long getAvg() {
        if (this.measurings > 0) {
            return this.sum / this.measurings;
        }
        return 0;
    }

    /**
     * The representation of an xml attribute.
     * 
     * @param label
     *            The attribute label.
     * @param value
     *            The attrbute value.
     * @return The attribute.
     * @common
     */
    private String getXmlAtt(final String label, final long value) {
        return label + "=\"" + value + "\"";
    }

    /**
     * The representation of an xml attribute.
     * 
     * @param label
     *            The attribute label.
     * @param value
     *            The attrbute value.
     * @return The attribute.
     * @common
     */
    private String getXmlAtt(final String label, final String value) {
        return label + "=\"" + value + "\"";
    }

    /**
     * @return Returns the end.
     * @common
     */
    public long getEnd() {
        return end;
    }

    /**
     * @param end
     *            The end to set.
     * @common
     */
    public void setEnd(final long end) {

        this.end = end;
        long time = this.end - this.start;
        measurings += 1;
        sum += time;
        if (time < this.min) {
            this.min = time;
        }
        if (time > this.max) {
            this.max = time;
        }
    }

    /**
     * Merges the results of the given measurement with the results of this.
     * 
     * @param measurement
     *            The measurement.
     */
    public void merge(final Measurement measurement) {
        measurings += measurement.getMeasurings();
        sum += measurement.getSum();
        if (measurement.getMin() < this.min) {
            this.min = measurement.getMin();
        }
        if (measurement.getMax() > this.max) {
            this.max = measurement.getMax();
        }
    }

    /**
     * @return Returns the start.
     * @common
     */
    public long getStart() {
        return start;
    }

    /**
     * @return Returns the label.
     * @common
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return Returns the max.
     * @common
     */
    public long getMax() {
        return max;
    }

    /**
     * @return Returns the min.
     * @common
     */
    public long getMin() {
        return min;
    }

    /**
     * @return Returns the sum.
     * @common
     */
    public long getSum() {
        return sum;
    }

    /**
     * @param start
     *            The start to set.
     * @common
     */
    public void setStart(final long start) {
        this.start = start;
    }

    /**
     * @return the measurings
     */
    public int getMeasurings() {
        return measurings;
    }

}
