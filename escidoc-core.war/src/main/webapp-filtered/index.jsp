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
  * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
  * fuer wissenschaftlich-technische Information mbH and Max-Planck-
  * Gesellschaft zur Foerderung der Wissenschaft e.V.
  * All rights reserved.  Use is subject to license terms.
  */
%>

<%@ page import="de.escidoc.core.common.util.configuration.EscidocConfiguration"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head profile="http://purl.org/NET/erdf/profile">
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <title>eSciDoc Framework</title>
  <link rel="stylesheet" type="text/css" href="css/default.css">
  <link rel="schema.es" href="http://escidoc.de/core/01/system/" />
  <meta name="es.version" content="${project.version}" />
  <meta name="es.build-date" content="${buildtimestamp}" />
</head>

<body>
<div id="head"></div>
<div id="menu">
  <ul class="menu_bg">
    <li>Applications
      <ul>
        
   <%
    	String adminToolUrl = EscidocConfiguration.getInstance().get(EscidocConfiguration.ADMIN_TOOL_URL);
    	if(adminToolUrl != null) {
    		out.println("<li><a href=\"" + adminToolUrl + "\">Admin Tool</a></li>");
    	}
   %>
   <%
    	String escidocBrowserUrl = EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_BROWSER_URL);
    	if(escidocBrowserUrl != null) {
    		out.println("<li><a href=\"" + escidocBrowserUrl + "\">eSciDoc Browser</a></li>");
    	}
   %>
      </ul>
    </li>
    <li>Website
      <ul>
        <li><a href="http://www.escidoc.org/" target="_self">http://www.escidoc.org</a></li>
      </ul>
    </li>
    <li>Mailinglists
      <ul>
        <li><a href="https://www.escidoc.org/mailman/listinfo/infrastructure-user" target="_self">infrastructure-user</a></li>
        <li><a href="https://listserv.gwdg.de/mailman/listinfo/escidoc-dev" target="_self">escidoc-dev</a></li>
      </ul>
    </li>
  </ul>
</div>
<br/><br/>
<div id="content"><h2>Welcome to eSciDoc</h2>
Version ${project.version}<br/>
${buildtimestamp}<br/><br/>
eSciDoc is free open source software.<br/>
<br/>
The software is distributed under the<br/>
<a href="http://www.opensource.org/licenses/cddl1.txt" target="_self">Common Development and Distribution License (CDDL)</a> in version 1.0.
</div>
</body>
</html>