package org.escidoc.core.services.fedora;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class AddDatastreamPathParam {

    private String pid;
    private String dsID;

    public String getPid() {
        return pid;
    }

    public void setPid(final String pid) {
        this.pid = pid;
    }

    public String getDsID() {
        return dsID;
    }

    public void setDsID(final String dsID) {
        this.dsID = dsID;
    }
}
