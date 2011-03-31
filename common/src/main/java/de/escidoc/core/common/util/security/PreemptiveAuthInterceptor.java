/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
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

package de.escidoc.core.common.util.security;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class PreemptiveAuthInterceptor implements HttpRequestInterceptor {

    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        final AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

        // If no auth scheme avaialble yet, try to initialize it preemptively
        if (authState.getAuthScheme() == null) {
            final AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
            final CredentialsProvider credsProvider =
                (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
            final HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
            if (authScheme != null) {
                final Credentials creds =
                    credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
                if (creds == null) {
                    throw new HttpException("No credentials for preemptive authentication.");
                }
                authState.setAuthScheme(authScheme);
                authState.setCredentials(creds);
            }
        }

    }

}
