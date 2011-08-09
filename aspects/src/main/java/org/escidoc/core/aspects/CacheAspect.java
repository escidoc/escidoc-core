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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.MethodSignature;

import com.googlecode.ehcache.annotations.CacheAttributeSource;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.interceptor.EhCacheInterceptor;

@SuppressWarnings("unused")
@Aspect
public final class CacheAspect {

	private EhCacheInterceptor interceptor = new EhCacheInterceptor();
	private CacheAttributeSource cacheAttributeSource;


    /**
     * Package protected constructor to avoid instantion outside of this package.
     */
    CacheAspect(){
    }

	@SuppressAjWarnings("adviceDidNotMatch")
	@Around("execution(@Cacheable * * (..))")
	public Object aroundCache(final ProceedingJoinPoint jp) throws Throwable {
		AspectJAopAllianceMethod m = new AspectJAopAllianceMethod(jp);
		return interceptor.invoke(m);
	}

	@SuppressAjWarnings("adviceDidNotMatch")
	@Around("execution(@TriggersRemove * * (..))")
	public Object aroundTriggersRemove(final ProceedingJoinPoint jp) throws Throwable {
		AspectJAopAllianceMethod m = new AspectJAopAllianceMethod(jp);
		return interceptor.invoke(m);
	}

	protected CacheAttributeSource getCacheAttributeSource() {
		return cacheAttributeSource;
	}

	public void setCacheAttributeSource(CacheAttributeSource cacheAttributeSource) {
		this.cacheAttributeSource = cacheAttributeSource;
		interceptor.setCacheAttributeSource(cacheAttributeSource);
	}

	private static class AspectJAopAllianceMethod implements MethodInvocation {

		private ProceedingJoinPoint jp;

		public AspectJAopAllianceMethod(ProceedingJoinPoint jp) {
			super();
			this.jp = jp;
		}

		public Object[] getArguments() {
			return jp.getArgs();
		}

		public Object proceed() throws Throwable {
			return jp.proceed();
		}

		public Object getThis() {
			return jp.getThis();
		}

		public AccessibleObject getStaticPart() {
			throw new UnsupportedOperationException("getStaticPart is not yet supported");
		}

		public Method getMethod() {
			MethodSignature methodSignature = (MethodSignature) jp.getSignature();
			return methodSignature.getMethod();
		}

	}
}
