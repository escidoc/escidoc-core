
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

<%@ page
  import="de.escidoc.core.common.util.SpringApplicationContextHolder,de.escidoc.core.aa.openid.EscidocOpenidFilter,java.util.Map"%>
<% final String submitUrl = "../j_spring_openid_security_check"; %> 
<html>
<head>
<title>eSciDoc - Open ID Login page</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>

<body bgcolor="#FFFFFF" text="#000000">
  <table width="97%" border="0">
    <tr>
      <td width="26%" height="69">
        <div align="right">
          <img src="../../../images/escidoc-logo.jpg" width="153" height="58" />
        </div>
      </td>
      <td width="3%" height="69">&nbsp;</td>
      <td width="68%" height="69"><b><font size="+3" face="Arial, Helvetica, sans-serif">Welcome to
            eSciDoc!</font> </b></td>
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
       --%> <%
  if (request.getParameter("login_error") != null) {
    out.println("<font face=\"Arial, Helvetica, sans-serif\" color=\"red\">"
        + "Your login attempt was not successful, try again.<br/><br/>"
        + "Reason: "
        + request.getParameter("message")
        + "</font>");
  }
 %>
      </td>
      <td width="3%">&nbsp;</td>
    </tr>
    <tr>
      <td width="26%" height="69">
        <div align="right">
          <img src="../../../images/openid-icon.png" width="58" height="58" />
        </div>
      </td>
      <td width="3%">&nbsp;</td>
      <td width="68%"><font face="Arial, Helvetica, sans-serif">eSciDoc OpenId Login</font></td>
      <td width="3%">&nbsp;</td>
    </tr>
  </table>
    <%
    EscidocOpenidFilter filter = (EscidocOpenidFilter) SpringApplicationContextHolder
    .getContext().getBean("openidFilter");
    if (filter != null && filter.getLoginTextInfos() != null) {
        for (final Map<String, String> loginTextInfo : filter
                .getLoginTextInfos()) {
          out.println("  <form method=\"post\" action=\"" + submitUrl + "\">");
          out.println("    <table width=\"97%\" border=\"0\">");
          out.println("      <tr>");
          out.println("        <td width=\"26%\">&nbsp;</td>");
          out.println("        <td width=\"3%\">&nbsp;</td>");
          out.println("        <td width=\"68%\">&nbsp;</td>");
          out.println("        <td width=\"3%\">&nbsp;</td>");
          out.println("      </tr>");
          out.println("      <tr>");
          out.println("        <td width=\"26%\">");
          out.println("          <div align=\"right\">");
          if (loginTextInfo.get("image") != null) {
              out.println("          <img src=\""
                      + loginTextInfo.get("image") + "\" height=\""
                      + loginTextInfo.get("height") + "\" width=\""
                      + loginTextInfo.get("width")
                      + "\" border=\"0\" />");
            }
          if (loginTextInfo.get("label") != null) {
            out.println("            <font face=\"Arial, Helvetica, sans-serif\">" + loginTextInfo.get("label") + "</font>");
          }
          out.println("          </div>");
          out.println("        </td>");
          out.println("        <td width=\"3%\">&nbsp;</td>");
          out.println("        <td width=\"68%\">");
          out.print("          <input type=\"text\" size=\"50\" name=\"openid_identifier\"");
          if (loginTextInfo.get("text") != null) {
            out.print(" value=\"" + loginTextInfo.get("text") + "\"");
          }
          out.print(" />");
          out.println();
          out.println("        </td>");
          out.println("        <td width=\"3%\">&nbsp;</td>");
          out.println("      </tr>");
          out.println("      <tr>");
          out.println("        <td width=\"26%\">");
          out.println("          <div align=\"right\"></div>");
          out.println("        </td>");
          out.println("        <td width=\"3%\">&nbsp;</td>");
          out.println("        <td width=\"68%\">");
          out.println("          <input type=\"submit\" name=\"Abschicken\" value=\"Submit\" />");
          out.println("        </td>");
          out.println("        <td width=\"3%\">&nbsp;</td>");
          out.println("      </tr>");
          out.println("    </table>");
          out.println("  </form>");
        }
      }
      if (filter != null && filter.getLoginButtonInfos() != null) {
        out.println("  <table width=\"97%\" border=\"0\">");
        out.println("    <tr>");
        out.println("      <td width=\"26%\">&nbsp;</td>");
        out.println("      <td width=\"3%\">&nbsp;</td>");
        out.println("      <td width=\"68%\">&nbsp;</td>");
        out.println("      <td width=\"3%\">&nbsp;</td>");
        out.println("    </tr>");
        out.println("    <tr>");
        out.println("      <td width=\"26%\">");
        out.println("        <div align=\"right\">");
        out.println("          <font face=\"Arial, Helvetica, sans-serif\">Direct login via:</font>");
        out.println("        </div>");
        out.println("      </td>");
        out.println("      <td width=\"3%\">&nbsp;</td>");
        out.println("      <td width=\"68%\">");
        for (final Map<String, String> loginButtonInfo : filter
            .getLoginButtonInfos()) {
            out.println("      <a href=\"" + submitUrl + "?openid_identifier="
                    + loginButtonInfo.get("url") + "\"><img src=\""
                    + loginButtonInfo.get("image") + "\" height=\""
                    + loginButtonInfo.get("height") + "\" width=\""
                    + loginButtonInfo.get("width") + "\" title=\""
                    + loginButtonInfo.get("url")
                    + "\" border=\"0\" alt=\""
                    + loginButtonInfo.get("url") + "\" /></a>&nbsp;");
        }
        out.println("      </td>");
        out.println("      <td width=\"3%\">&nbsp;</td>");
        out.println("    </tr>");
        out.println("  </table>");
      }
    %>
</body>
</html>
