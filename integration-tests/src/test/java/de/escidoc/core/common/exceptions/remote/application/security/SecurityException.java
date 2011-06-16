package de.escidoc.core.common.exceptions.remote.application.security;

public class SecurityException extends de.escidoc.core.common.exceptions.remote.EscidocException {

    private static final long serialVersionUID = -5289757564660070041L;

    private String redirectLocation;

    public SecurityException() {
    }

    public SecurityException(int httpStatusCode, String httpStatusLine, String httpStatusMsg) {
        super(httpStatusCode, httpStatusLine, httpStatusMsg);
    }

    public SecurityException(int httpStatusCode, String httpStatusLine, String httpStatusMsg, String redirectLocation) {
        this(httpStatusCode, httpStatusLine, httpStatusMsg);
        this.redirectLocation = redirectLocation;
    }

    /**
     * Gets the redirectLocation value for this SecurityException.
     * 
     * @return redirectLocation
     */
    public String getRedirectLocation() {
        return redirectLocation;
    }

    /**
     * Sets the redirectLocation value for this SecurityException.
     * 
     * @param redirectLocation
     */
    public void setRedirectLocation(String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SecurityException))
            return false;
        SecurityException other = (SecurityException) obj;
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
            super.equals(obj)
                && ((this.redirectLocation == null && other.getRedirectLocation() == null) || (this.redirectLocation != null && this.redirectLocation
                    .equals(other.getRedirectLocation())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getRedirectLocation() != null) {
            _hashCode += getRedirectLocation().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }
}
