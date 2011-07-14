/**
 * 
 */
package de.escidoc.core.common.business.fedora;

import java.io.InputStream;

/**
 * @author jhe
 *
 */
public class MimeInputStream {

    private InputStream inputStream = null;

    private String mimeType = null;

    /**
     * @return the stream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @set the stream
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * @set the mimeType
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
        result = prime * result + ((inputStream == null) ? 0 : inputStream.hashCode());
        return result;
    }
}
