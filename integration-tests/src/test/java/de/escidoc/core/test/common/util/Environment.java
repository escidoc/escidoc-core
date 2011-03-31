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
package de.escidoc.core.test.common.util;

import org.joda.time.DateTime;

/**
 * Test environment description.
 *
 * @author Steffen Wagner
 */
public class Environment {

    /*
     * Measurment environment values.
     */
    private DateTime date = null;

    private String hostname = null;

    private String fwSeries = null;

    private String fwBuild = null;

    private int noOfCpus = 1;

    private int mhz = 1;

    private String cpuType = null;

    private long memory = 1;

    private String javaVersion = null;

    private String fedoraVersion = null;

    private String tripleStore = null;

    private String jbossVersion = null;

    private String javaOpts = null;

    private String methodParameter = null;

    private String description = null;

    /**
     * @param date the date to set
     */
    public void setDate(final DateTime date) {
        this.date = date;
    }

    /**
     * @return the date
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * @param hostname the hostname to set
     */
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param fwSeries the fwSeries to set
     */
    public void setFwSeries(final String fwSeries) {
        this.fwSeries = fwSeries;
    }

    /**
     * @return the fwSeries
     */
    public String getFwSeries() {
        return fwSeries;
    }

    /**
     * @param fwBuild the fwBuild to set
     */
    public void setFwBuild(final String fwBuild) {
        this.fwBuild = fwBuild;
    }

    /**
     * @return the fwBuild
     */
    public String getFwBuild() {
        return fwBuild;
    }

    /**
     * @param noOfCpus the noOfCpus to set
     */
    public void setNoOfCpus(final int noOfCpus) {
        this.noOfCpus = noOfCpus;
    }

    /**
     * @return the noOfCpus
     */
    public int getNoOfCpus() {
        return noOfCpus;
    }

    /**
     * @param mhz the mhz to set
     */
    public void setMhz(final int mhz) {
        this.mhz = mhz;
    }

    /**
     * @return the mhz
     */
    public int getMhz() {
        return mhz;
    }

    /**
     * @param cpuType the cpuType to set
     */
    public void setCpuType(final String cpuType) {
        this.cpuType = cpuType;
    }

    /**
     * @return the cpuType
     */
    public String getCpuType() {
        return cpuType;
    }

    /**
     * @param memory the memory to set
     */
    public void setMemory(final long memory) {
        this.memory = memory;
    }

    /**
     * @return the memory
     */
    public long getMemory() {
        return memory;
    }

    /**
     * @param javaVersion the javaVersion to set
     */
    public void setJavaVersion(final String javaVersion) {
        this.javaVersion = javaVersion;
    }

    /**
     * @return the javaVersion
     */
    public String getJavaVersion() {
        return javaVersion;
    }

    /**
     * @param fedoraVersion the fedoraVersion to set
     */
    public void setFedoraVersion(final String fedoraVersion) {
        this.fedoraVersion = fedoraVersion;
    }

    /**
     * @return the fedoraVersion
     */
    public String getFedoraVersion() {
        return fedoraVersion;
    }

    /**
     * @param tripleStore the tripleStore to set
     */
    public void setTripleStore(final String tripleStore) {
        this.tripleStore = tripleStore;
    }

    /**
     * @return the tripleStore
     */
    public String getTripleStore() {
        return tripleStore;
    }

    /**
     * @param jbossVersion the jbossVersion to set
     */
    public void setJbossVersion(final String jbossVersion) {
        this.jbossVersion = jbossVersion;
    }

    /**
     * @return the jbossVersion
     */
    public String getJbossVersion() {
        return jbossVersion;
    }

    /**
     * @param javaOpts the javaOpts to set
     */
    public void setJavaOpts(final String javaOpts) {
        this.javaOpts = javaOpts;
    }

    /**
     * @return the javaOpts
     */
    public String getJavaOpts() {
        return javaOpts;
    }

    /**
     * @param methodParameter the methodParameter to set
     */
    public void setMethodParameter(final String methodParameter) {
        this.methodParameter = methodParameter;
    }

    /**
     * @return the methodParameter
     */
    public String getMethodParameter() {
        return methodParameter;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

}
