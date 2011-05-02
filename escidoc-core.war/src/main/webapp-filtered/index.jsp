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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
   "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head profile="http://purl.org/NET/erdf/profile">
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <title>eSciDoc-Framework</title>
  <link rel="schema.es" href="http://escidoc.de/core/01/system/" />
  <meta name="es.version" content="${project.version}" />
  <meta name="es.build-date" content="${buildtimestamp}" />
  <style type="text/css">
    <!--
  	body {
		font-family: Arial, Helvetica, sans-serif;
	}
	table {
		border-spacing:10px;
		width: 90%;
	}
	-->
  </style>
</head>

<body bgcolor="#FFFFFF">

  <table align="center" border="0">
    <tr>
        <td>
            <a href="https://www.escidoc.org/">
                <div align="center">
                    <img src="/images/escidoc-logo.jpg" width="153" height="58">
                </div>
            </a>
        </td>
	</tr>
	<tr>
        <td>
            <div align="center">
                <b><font size="+3" face="Arial, Helvetica, sans-serif">Welcome to eSciDoc</font></b>
            </div>
        </td>
	</tr>
	<tr>
        <td>
            <div align="center"><b>Version Information</b></div>
        </td>
	</tr>
	<tr>
        <td>
            <div align="center">${project.version}</div>
        </td>
	</tr>
	<tr>
        <td>
            <div align="center">${buildtimestamp}</div>
        </td>
	</tr>
	<tr>
        <td>
   <%
    	String adminToolUrl = EscidocConfiguration.getInstance().get(EscidocConfiguration.ADMIN_TOOL_URL);
    	if(adminToolUrl != null) {
    		out.println("<a href=\"" + adminToolUrl + "\">Admin Tool</a>");
    	}
   %>
		</td>
	</tr>
	<tr>
	    <td>
	        <div align="center">
	            <p><b>Mailinglists</b></p>
                <p><a href="https://www.escidoc.org/mailman/listinfo/infrastructure-user">infrastructure-user</a></p>
                <p><a href="https://listserv.gwdg.de/mailman/listinfo/escidoc-dev">escidoc-dev</a></p>
            </div>
	    </td>
	</tr>
	<tr>
	    <td>
	        <div align="center">
	            <p><b>Webseite</b></p>
                <p><a href="http://www.escidoc.org/">http://www.escidoc.org/</a></p>
            </div>
	    </td>
	</tr>
	<tr>
		<td colspan="2">
		<div align="center">
		<p>eSciDoc is free open source software.</p>
		<p>The software is distributed under the
		<a href="http://www.opensource.org/licenses/cddl1.txt">Common Development and Distribution License (CDDL)</a>
		in version 1.0.</p>
		<p>This license is OSI-certified - find it on
		<a href="http://www.opensource.org/licenses/cddl1.txt">OpenSource.org</a>!</p>
		<p>CDDL has been created by Sun Microsystems and is based on the Mozilla 1.1 license, but addresses several
        legal issues for non-US projects like eSciDoc.</p>
        <p>You will find more background information on the CDDL, a
        critizism of the Mozilla 1.1 license, and a description of the modifications at
        <a href="http://www.sun.com/cddl/">Sun Microsystems CDDL website</a>.
		</div>
        </p>
         </td>
	</tr>
  </table>

</body>
</html>