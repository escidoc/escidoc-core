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

<html>
<head>
<title>eSciDoc - Open ID Login page</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>

<body bgcolor="#FFFFFF" text="#000000">
<form name="form1" method="post" action="/aa/j_spring_openid_security_check">
  <table width="97%" border="0">
    <tr> 
      <td width="26%" height="69">
        <div align="right"><img src="../../images/escidoc-logo.jpg" width="153" height="58" /></div>
      </td>
      <td width="3%" height="69">&nbsp;</td>
      <td width="68%" height="69"><b><font size="+3" face="Arial, Helvetica, sans-serif">Welcome 
        to eSciDoc!</font></b></td>
      <td width="3%" height="69">&nbsp;</td>
    </tr>
    <tr> 
      <td width="26%">&nbsp;</td>
      <td width="3%">&nbsp;</td>
      <td width="68%">&nbsp;</td>
      <td width="3%">&nbsp;</td>
    </tr>
    
    <tr> 
      <td width="26%">&nbsp;</td>
      <td width="3%">&nbsp;</td>
      <td width="68%">
      	<%-- this form-login-page form is also used as the
         form-error-page to ask for a login again.
         --%>
        <%
        	if (request.getParameter("login_error") != null) {
        		out.println("<font face=\"Arial, Helvetica, sans-serif\" color=\"red\">"
        				+ "Your login attempt was not successful, try again.<br/><br/>"
        				+ "Reason: " + request.getParameter("message") + "</font>");
        	}
        %>
    </td>
      <td width="3%">&nbsp;</td>
    </tr>
    <tr> 
      <td width="26%" height="69">
        <div align="right"><img src="../../images/openid-icon.png" width="58" height="58" /></div>
      </td>
      <td width="3%">&nbsp;</td>
      <td width="68%"><font face="Arial, Helvetica, sans-serif">Please enter your 
        OpenID Identity</font></td>
      <td width="3%">&nbsp;</td>
    </tr>
    <tr> 
      <td width="26%">&nbsp;</td>
      <td width="3%">&nbsp;</td>
      <td width="68%">&nbsp;</td>
      <td width="3%">&nbsp;</td>
    </tr>
    <tr> 
      <td width="26%"> 
        <div align="right"><font face="Arial, Helvetica, sans-serif">OpenID Identity</font></div>
      </td>
      <td width="3%">&nbsp;</td>
      <td width="68%"> 
        <input type="text" size="50"  name="openid_identifier">
      </td>
      <td width="3%">&nbsp;</td>
    </tr>
    <tr> 
      <td width="26%"> 
        <div align="right"></div>
      </td>
      <td width="3%">&nbsp;</td>
      <td width="68%"> 
        <input type="submit" name="Abschicken" value="Submit">
      </td>
      <td width="3%">&nbsp;</td>
    </tr>
  </table>
</form>
</body>
</html>
