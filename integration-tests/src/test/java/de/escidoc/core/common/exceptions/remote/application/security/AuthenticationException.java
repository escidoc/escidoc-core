package de.escidoc.core.common.exceptions.remote.application.security;

public class AuthenticationException
    extends de.escidoc.core.common.exceptions.remote.application.security.SecurityException {

    private static final long serialVersionUID = -1464710640780755919L;

    public AuthenticationException() {
    }

    public AuthenticationException(int httpStatusCode, String httpStatusLine, String httpStatusMsg) {
        super(httpStatusCode, httpStatusLine, httpStatusMsg);
    }

    public AuthenticationException(int httpStatusCode, String httpStatusLine, String httpStatusMsg,
        String redirectLocation) {
        super(httpStatusCode, httpStatusLine, httpStatusMsg, redirectLocation);
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof AuthenticationException))
            return false;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj);
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
        __hashCodeCalc = false;
        return _hashCode;
    }
}
