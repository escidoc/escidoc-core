package de.escidoc.core.om.business.scape;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service("business.ScapePIDService")
/**
 * Just a Simple PID generator using random UUIDs
 * This should be fine since, with good randomness, the 122bit of
 * UUIDs provide enough permutations.
 * Ideally it's approximation of collision probability is
 * p(n) = 1 - e^-((n^2)/(2*2^122))
 * p(2^30) is practically zero.
 * 
 * @author ruckus
 *
 */
public class ScapePIDService {
    long currentIndex = 1;

    private static final String namespace = "scape:";

    public String generatePID() {
        return namespace + UUID.randomUUID().toString();
    }
}
