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

package org.escidoc.core.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.escidoc.core.util.regex.MatcherFactory;
import org.escidoc.core.util.regex.PatternFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
public final class PatternPerformanceAspect {

    private final String EMPTY_STRING = "";

    private PatternFactory patternFactory = PatternFactory.getInstance();
    private MatcherFactory matcherFactory = MatcherFactory.getInstance();

    /**
     * Package protected constructor to avoid instantion outside of this package.
     */
    protected PatternPerformanceAspect() {
    }

    @Around("call(public java.lang.String java.lang.String.replaceAll(java.lang.String, java.lang.String))" +
            " && !within(org.escidoc.core.aspects.PatternPerformanceAspect)" +
            " && !within(org.escidoc.core.util.regex..*)")
    public Object optimizeReplaceAllWithStrings(final ProceedingJoinPoint joinPoint) {
        final String target = (String) joinPoint.getTarget();
        final String patternString = (String) joinPoint.getArgs()[0];
        final String replacement = (String) joinPoint.getArgs()[1];
        final Matcher matcher = matcherFactory.createMatcher(patternString);
        return matcher.reset(target).replaceAll(replacement);
    }

    @Around("call(public java.lang.String java.lang.String.replaceAll(java.lang.CharSequence, java.lang.CharSequence))" +
            " && !within(org.escidoc.core.aspects.PatternPerformanceAspect)" +
            " && !within(org.escidoc.core.util.regex..*)")
    public Object optimizeReplaceAllWithCharSequences(final ProceedingJoinPoint joinPoint) {
        final String target = (String) joinPoint.getTarget();
        final CharSequence patternCharSequence = (CharSequence) joinPoint.getArgs()[0];
        final String patternString = patternCharSequence.toString();
        final CharSequence replacement = (CharSequence) joinPoint.getArgs()[1];
        final Matcher matcher = matcherFactory.createMatcher(patternString, Pattern.LITERAL);
        return matcher.reset(target).replaceAll(replacement.toString());
    }

    @Around("call(public java.lang.String java.lang.String.replace(java.lang.CharSequence, java.lang.CharSequence))" +
            " && !within(org.escidoc.core.aspects.PatternPerformanceAspect)" +
            " && !within(org.escidoc.core.util.regex..*)")
    public Object optimizeReplace(final ProceedingJoinPoint joinPoint) {
        final String target = (String) joinPoint.getTarget();
        final CharSequence patternCharSequence = (CharSequence) joinPoint.getArgs()[0];
        final String patternString = patternCharSequence.toString();
        final CharSequence replacement = (CharSequence) joinPoint.getArgs()[1];
        final Matcher matcher = matcherFactory.createMatcher(patternString, Pattern.LITERAL);
        return matcher.reset(target).replaceAll(Matcher.quoteReplacement(replacement.toString()));
    }

    @Around("call(public java.lang.String java.lang.String.replaceFirst(java.lang.String, java.lang.String))" +
            " && !within(org.escidoc.core.aspects.PatternPerformanceAspect)" +
            " && !within(org.escidoc.core.util.regex..*)")
    public Object optimizeReplaceFirst(final ProceedingJoinPoint joinPoint) {
        final String target = (String) joinPoint.getTarget();
        final String patternString = (String) joinPoint.getArgs()[0];
        final String replacement = (String) joinPoint.getArgs()[1];
        final Matcher matcher = matcherFactory.createMatcher(patternString);
        return matcher.reset(target).replaceFirst(replacement);
    }

    @Around("call(public java.lang.String java.lang.String.split(java.lang.String))" +
            " && !within(org.escidoc.core.aspects.PatternPerformanceAspect)" +
            " && !within(org.escidoc.core.util.regex..*)")
    public Object optimizeSplit(final ProceedingJoinPoint joinPoint) {
        final String target = (String) joinPoint.getTarget();
        final String patternString = (String) joinPoint.getArgs()[0];
        final Pattern pattern = patternFactory.createPattern(patternString);
        return pattern.split(target);
    }

    @Around("call(public java.lang.String java.lang.String.split(java.lang.String, int))" +
            " && !within(org.escidoc.core.aspects.PatternPerformanceAspect)" +
            " && !within(org.escidoc.core.util.regex..*)")
    public Object optimizeSplitWithLimit(final ProceedingJoinPoint joinPoint) {
        final String target = (String) joinPoint.getTarget();
        final String patternString = (String) joinPoint.getArgs()[0];
        final Integer limit = (Integer) joinPoint.getArgs()[1];
        final Pattern pattern = patternFactory.createPattern(patternString);
        return pattern.split(target, limit);
    }

}
