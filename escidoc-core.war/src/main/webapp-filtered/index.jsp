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
      <td height="80" width="34%" >
        <div align="right"><img src="/images/escidoc-logo.jpg" width="153" height="58"></div>
      </td>
      <td width="66%" ><b><font size="+3" face="Arial, Helvetica, sans-serif">Welcome 
        to eSciDoc</font></b></td>
    </tr>
    <tr> 
      <td></td>
      <td><div align="left"><font size="+1">Version Information</font></div></td>
    </tr>
     <tr> 
      <td> 
        <div align="right">VERSION</div>
      </td>
      <td> 
        <div align="left">${project.version}</div>
      </td>
    </tr>
    <tr> 
      <td> 
        <div align="right">DATE</div>
      </td>
      <td> 
        <div align="left">${buildtimestamp}</div>
      </td>
    </tr>
    <tr>
      <td colspan="2"><a href="<%= EscidocConfiguration.getInstance().get(EscidocConfiguration.ADMIN_TOOL_URL) %>">Admin Tool</a></td>
    </tr>
    <tr>
      <td colspan="2"><hr></td>
    </tr>
  </table>

</body>
</html>
