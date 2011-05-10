/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
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

/**
 * EscidocException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package de.escidoc.core.common.exceptions.remote;

public class EscidocException extends org.apache.axis.AxisFault implements java.io.Serializable {
    private int httpStatusCode;

    private String httpStatusLine;

    private String httpStatusMsg;

    public EscidocException() {
    }

    public EscidocException(int httpStatusCode, String httpStatusLine, String httpStatusMsg) {
        this.httpStatusCode = httpStatusCode;
        this.httpStatusLine = httpStatusLine;
        this.httpStatusMsg = httpStatusMsg;
    }

    /**
     * Gets the httpStatusCode value for this EscidocException.
     * 
     * @return httpStatusCode
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Sets the httpStatusCode value for this EscidocException.
     * 
     * @param httpStatusCode
     */
    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Gets the httpStatusLine value for this EscidocException.
     * 
     * @return httpStatusLine
     */
    public String getHttpStatusLine() {
        return httpStatusLine;
    }

    /**
     * Sets the httpStatusLine value for this EscidocException.
     * 
     * @param httpStatusLine
     */
    public void setHttpStatusLine(String httpStatusLine) {
        this.httpStatusLine = httpStatusLine;
    }

    /**
     * Gets the httpStatusMsg value for this EscidocException.
     * 
     * @return httpStatusMsg
     */
    public String getHttpStatusMsg() {
        return httpStatusMsg;
    }

    /**
     * Sets the httpStatusMsg value for this EscidocException.
     * 
     * @param httpStatusMsg
     */
    public void setHttpStatusMsg(String httpStatusMsg) {
        this.httpStatusMsg = httpStatusMsg;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof EscidocException))
            return false;
        EscidocException other = (EscidocException) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals =
            true
                && this.httpStatusCode == other.getHttpStatusCode()
                && ((this.httpStatusLine == null && other.getHttpStatusLine() == null) || (this.httpStatusLine != null && this.httpStatusLine
                    .equals(other.getHttpStatusLine())))
                && ((this.httpStatusMsg == null && other.getHttpStatusMsg() == null) || (this.httpStatusMsg != null && this.httpStatusMsg
                    .equals(other.getHttpStatusMsg())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getHttpStatusCode();
        if (getHttpStatusLine() != null) {
            _hashCode += getHttpStatusLine().hashCode();
        }
        if (getHttpStatusMsg() != null) {
            _hashCode += getHttpStatusMsg().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EscidocException.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://exceptions.common.core.escidoc.de",
            "EscidocException"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("httpStatusCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "httpStatusCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("httpStatusLine");
        elemField.setXmlName(new javax.xml.namespace.QName("", "httpStatusLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("httpStatusMsg");
        elemField.setXmlName(new javax.xml.namespace.QName("", "httpStatusMsg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
        String mechType, Class _javaType, javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
        String mechType, Class _javaType, javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
    }

    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context)
        throws java.io.IOException {
        context.serialize(qname, null, this);
    }
}
