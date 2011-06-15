package de.escidoc.core.common.exceptions.remote;

public class EscidocException extends Exception {

    private static final long serialVersionUID = 1254972454320093984L;

    private int httpStatusCode;

    private String httpStatusLine;

    private String httpStatusMsg;

    public EscidocException() {
    }

    public EscidocException(int httpStatusCode, String httpStatusLine, String httpStatusMsg) {
        super(httpStatusMsg);
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
}
