package org.escidoc.core.services.fedora;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class ExportQueryParam {

    private String format;
    private String context;
    private String encoding;

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }
    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }
    /**
     * @return the context
     */
    public String getContext() {
        return context;
    }
    /**
     * @param context the context to set
     */
    public void setContext(String context) {
        this.context = context;
    }
    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }
    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

}
