package de.escidoc.core.om.business.scape;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service("business.ScapePIDService")
public class ScapePIDService {
    long currentIndex = 1;

    private static final String namespace = "scape:";

    public String generatePID() {
        return namespace + UUID.randomUUID().toString();
    }
}
