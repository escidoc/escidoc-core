package org.escidoc.core.business.domain.om.item;

import java.net.URI;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.guard.Guarded;

/**
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true, inspectInterfaces = true)
public class RelationDO {

    private URI predicate;

	private URI resource;

    public RelationDO(@AssertFieldConstraints final URI predicate,
        @AssertFieldConstraints final URI resource) {
        this.predicate = predicate;
        this.resource = resource;
    }

    /**
	 * @return the predicate
	 */
	public URI getPredicate() {
		return predicate;
	}

	/**
	 * @param predicate the predicate to set
	 */
	public void setPredicate(URI predicate) {
		this.predicate = predicate;
	}

	/**
	 * @return the resource
	 */
	public URI getResource() {
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(URI resource) {
		this.resource = resource;
	}

}
