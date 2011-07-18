<%
 /*
  * CDDL HEADER START
  *
  * The contents of this file are subject to the terms of the
  * Common Development and Distribution License, Version 1.0 only
  * (the "License").  You may not use this file except in compliance
  * with the License.
  *
  * You can obtain a copy of the license at license/ESCIDOC.LICENSE
  * or http://www.escidoc.de/license.
  * See the License for the specific language governing permissions
  * and limitations under the License.
  *
  * When distributing Covered Code, include this CDDL HEADER in each
  * file and include the License file at license/ESCIDOC.LICENSE.
  * If applicable, add the following below this CDDL HEADER, with the
  * fields enclosed by brackets "[]" replaced with your own identifying
  * information: Portions Copyright [yyyy] [name of copyright owner]
  *
  * CDDL HEADER END
  */

 /*
  * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
  * fuer wissenschaftlich-technische Information mbH and Max-Planck-
  * Gesellschaft zur Foerderung der Wissenschaft e.V.
  * All rights reserved.  Use is subject to license terms.
  */
%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Map.Entry"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="javax.servlet.Filter"%>
<%@ page import="de.escidoc.core.common.servlet.EscidocServlet"%>
<%@ page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="org.springframework.security.web.FilterChainProxy"%>
<%!

private List<String> getLoginMethods() throws Exception {
	List<String> methodList = new ArrayList<String>();
    WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
    String[] filterBeans = context.getBeanNamesForType(FilterChainProxy.class);
    if (filterBeans != null) {
        for (int i = 0; i < filterBeans.length; i++) {
            try {
            	FilterChainProxy filterChainProxy = (FilterChainProxy)context.getBean(filterBeans[i]);
            	Map<String, List<Filter>> filterChainMap = filterChainProxy.getFilterChainMap();
            	if (filterChainMap != null) {
                    for (Entry<String, List<Filter>> entry : filterChainMap.entrySet()) {
                        if (!entry.getValue().isEmpty()) {
                            methodList.add(entry.getKey().replaceFirst(".*?\\/aa\\/login\\/([A-Za-z]*).*", "$1"));
                        }
                    }
            	}
            }
            catch (Throwable e) {}
        }    	
    }
    return methodList;
  }

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Login Select</title>
</head>

<body bgcolor="#eeeeee">
		<table>
		<%
            List<String> loginMethods = getLoginMethods();

		    String queryString = request.getQueryString();
		    if (queryString != null && !queryString.isEmpty()) {
		    	queryString = "?" + queryString;
		    } else {
		    	queryString = "?target=";
		    }

		    if (loginMethods.size() == 1) {
		        EscidocServlet.doRedirect(response, null, "<html><body><a href=\"/aa/login/"
		                + loginMethods.iterator().next()
		                + queryString
		                + "\">Resource available under this location: "
		                + loginMethods.iterator().next() + queryString +"</a></body></html>",
		                loginMethods.iterator().next() + queryString,
		                HttpServletResponse.SC_MOVED_PERMANENTLY, true);
		    }
		    
		    if (loginMethods.isEmpty()) {
                %>
                <tr>
                  <td>&nbsp;</td>
                  <td>
                    No Login-Method configured
                  </td>
                </tr>
	    		<%
		    	
		    }

		    for (String loginMethod : loginMethods) {
                    %>
                    <tr>
                      <td>&nbsp;</td>
                      <td>
                        <a href="/aa/login/<%= loginMethod %><%= queryString %>"><%= loginMethod %></a>
                      </td>
                    </tr>
		    <%
            }
            %>
                  </table>
</body>
</html>
