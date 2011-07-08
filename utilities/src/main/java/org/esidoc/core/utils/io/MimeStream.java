/**
 * 
 */
package org.esidoc.core.utils.io;

/**
 * @author jhe
 *
 */
public class MimeStream {
    
    private Stream stream;
    
    private String mimeType;

    /**
     * @param stream
     * @param mimet
     */
    public MimeStream(Stream stream, String mimeType) {
        super();
        this.stream = stream;
        this.mimeType = mimeType;
    }

    /**
     * @return the stream
     */
    public Stream getStream() {
        return stream;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
        result = prime * result + ((stream == null) ? 0 : stream.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MimeStream other = (MimeStream) obj;
        if (mimeType == null) {
            if (other.mimeType != null)
                return false;
        }
        else if (!mimeType.equals(other.mimeType))
            return false;
        if (stream == null) {
            if (other.stream != null)
                return false;
        }
        else if (!stream.equals(other.stream))
            return false;
        return true;
    };     

}
