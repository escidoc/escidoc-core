package org.escidoc.core.persistence.impl.fedora.resource;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author FRS
 * 
 */
public class Relation {
    private Predicate predicate;

    private Target target;

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
