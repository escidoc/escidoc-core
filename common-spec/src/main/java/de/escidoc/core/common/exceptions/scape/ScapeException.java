package de.escidoc.core.common.exceptions.scape;

import de.escidoc.core.common.exceptions.EscidocException;

public class ScapeException extends EscidocException {
	public ScapeException(String msg) {
		super(msg);
	}

	public ScapeException(String msg, Throwable e) {
		super(msg, e);
	}
}
