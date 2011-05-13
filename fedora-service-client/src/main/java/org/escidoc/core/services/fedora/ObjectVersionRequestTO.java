package org.escidoc.core.services.fedora;

public class ObjectVersionRequestTO {

    private String pid;
    private Integer versionNumber;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }
}
