/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package org.escidoc.core.util.regex.internal;

import org.escidoc.core.util.regex.MatcherFactory;
import org.escidoc.core.util.regex.PatternFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CachedMatcherFactory extends MatcherFactory {

    private static final ThreadLocal<Map<PatternCacheKey, Matcher>> MATCHERS_CACHE = new ThreadLocal<Map<PatternCacheKey, Matcher>>() {
        protected Map<PatternCacheKey, Matcher> initialValue() {
            return new HashMap<PatternCacheKey, Matcher>();
        }
    };

    private final PatternFactory patternFactory = PatternFactory.getInstance();

    @Override
    public Matcher createMatcher(final String patternString) {
        return createMatcher(patternString, 0);
    }

    @Override
    public Matcher createMatcher(final String patternString, final int flags) {
        final PatternCacheKey patternCacheKey = new PatternCacheKey(patternString, flags);
        Matcher matcher = MATCHERS_CACHE.get().get(patternCacheKey);
        if(matcher == null) {
            final Pattern pattern = patternFactory.createPattern(patternString, flags);
            matcher = pattern.matcher("");
            MATCHERS_CACHE.get().put(patternCacheKey, matcher);
        }
        return matcher;
    }
}
