package org.escidoc.core.persistence.impl.fedora.resource;

import java.util.HashMap;

/**
 * @author FRS
 * 
 */
public class Components extends HashMap<String, Component> {
    private static final long serialVersionUID = -140266395042126176L;

    private boolean inherited = false;

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }
}
