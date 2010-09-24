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

<%@ page import="de.escidoc.core.common.servlet.EscidocServlet"%>
<%@ page import="de.escidoc.core.common.util.configuration.EscidocConfiguration"%>
<%@ page import="de.escidoc.core.common.util.service.BeanLocator"%>
<%@ page import="de.escidoc.core.common.util.string.StringUtility"%>

<%@ page import="java.net.MalformedURLException"%>
<%@ page import="java.net.URL"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Set"%>

<%!
  private static Map<String, HashMap<String, String>> INDEX_CONFIGURATIONS = null;
  private static Map<String, HashMap<String, HashMap<String, Object>>> OBJECT_TYPE_PARAMETERS = null;

  private static Map<String, HashMap<String, String>> getIndexConfigurations() throws Exception {
      if (INDEX_CONFIGURATIONS == null) {
          INDEX_CONFIGURATIONS = BeanLocator.locateGsearchHandler().getIndexConfigurations();
      }
      return INDEX_CONFIGURATIONS;
  }

  private static Map<String, HashMap<String, HashMap<String, Object>>> getObjectTypeParameters() throws Exception {
      if (OBJECT_TYPE_PARAMETERS == null) {
          OBJECT_TYPE_PARAMETERS = BeanLocator.locateIndexingHandler().getObjectTypeParameters();
      }
      return OBJECT_TYPE_PARAMETERS;
  }
%>

<%
  String queryString = EscidocServlet.addCookie(request, response);

  if (queryString != null) {
      final StringBuffer location = request.getRequestURL();

      if (queryString.length() > 0) {
          location.append("?");
          location.append(queryString);
      }

      final String locationString = location.toString();

      EscidocServlet.doRedirect(response, null, "<html><body><a href=\""
                            + locationString
                            + "\">Resource available under this location: "
                            + locationString + "</a></body></html>",
                            locationString,
                            HttpServletResponse.SC_MOVED_PERMANENTLY);
      return;
  }
  else {
      response.setHeader("Pragma", "no-cache");
      response.setHeader("Cache-Control", "no-cache");
      response.setHeader("Cache-Control", "no-store");
      response.setDateHeader("Expires", 0);
  }
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Admin Tool</title>
<style type="text/css">
table.sample {
	border-width: 1px 1px 1px 1px;
	border-spacing: 0px;
	border-style: outset outset outset outset;
	border-color: black black black black;
	border-collapse: separate;
}

table.sample th {
	border-width: 1px 1px 1px 1px;
	padding: 1px 1px 1px 1px;
	border-style: outset outset outset outset;
	border-color: black black black black;
}

table.sample th th {
	border-style: none;
}

table.sample td {
	border-width: 1px 1px 1px 1px;
	padding: 1px 1px 1px 1px;
	border-style: outset outset outset outset;
	border-color: black black black black;
}

table.sample td td {
	border-style: none;
}
</style>

<script type="text/javascript">
      String.prototype.startsWith = function(str)
      {
        return (this.match("^"+str)==str)
      }

    /*
     * GET
     */
      function adminGetMethod(methodName)
      {
        showResultText("Please wait ...");

        var request = createRequest();

        request.onreadystatechange = function() {
          if (request.readyState == 4) {
            if (request.status && (request.status == 200)) {
              showResultXml(request);
            }
            else {
              showResultText(request.statusText + " (" + request.status + "): " + request.responseText);
            }
            document.body.style.cursor = 'default';
          }
        }
        request.open("GET", getBaseURL() + "/adm/admin/" + methodName, true);
        request.setRequestHeader("Connection", "close");
        request.send();
        document.body.style.cursor = 'wait';
      }

    /*
     * POST
     */
      function adminPostMethod(methodName, requestXml)
      {
        showResultText("Please wait ...");

        var request = createRequest();

        request.onreadystatechange = function() {
          if (request.readyState == 4) {
            if (request.status && (request.status == 200)) {
              if (methodName.startsWith("deleteobjects")) {
                refreshPurgeStatus();
              }
              else if (methodName.startsWith("recache")) {
                refreshRecacheStatus();
              }
              else if (methodName.startsWith("reindex")) {
                refreshReindexStatus();
              }
              else {
                document.body.style.cursor = 'default';
              }
              showResultXml(request);
            }
            else {
              document.body.style.cursor = 'default';
              showResultText(request.statusText + " (" + request.status + "): " + request.responseText);
            }
          }
        }
        request.open("POST", getBaseURL() + "/adm/admin/" + methodName, true);
        request.setRequestHeader("Content-type", "text/xml");
        request.setRequestHeader("Content-length", (requestXml != null) ? requestXml.length : 0);
        request.setRequestHeader("Connection", "close");
        request.send(requestXml);
        document.body.style.cursor = 'wait';
      }

    /*
    * REQUEST
    */
      function createRequest()
      {
        var result = null;

        if (window.XMLHttpRequest) {
          result = new XMLHttpRequest();
          if (result.overrideMimeType) {
            result.overrideMimeType("text/xml");
          }
        }
        else if (window.ActiveXObject) {
          try {
            result = new ActiveXObject("Msxml2.XMLHTTP");
          }
          catch (e) {
            try {
              result = new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e) {}
          }
        }
        return result;
      }

      /*
       * Get the base URL from the current URL.
       * 
       * Do not simply use <host>://<port> because the base URL could contain a prefix.
       */
      function getBaseURL()
      {
        var href = window.location.href;

        return href.substring(0, href.indexOf("/adm"));
      }

      /*
       *
       */
      function getPurgeStatus()
      {
        document.body.style.cursor = 'wait';

        var request = createRequest();

        request.onreadystatechange = function() {
          if (request.readyState == 4) {
            if (request.status && (request.status == 200)) {
              if (request.responseText.indexOf ("finished") >= 0) {
                document.body.style.cursor = 'default';
              }
              else {
                refreshPurgeStatus();
              }
              showResultXml(request);
            }
            else {
              document.body.style.cursor = 'default';
              showResultText(request.statusText + " (" + request.status + "): " + request.responseText);
            }
          }
        }
        request.open("GET", getBaseURL() + "/adm/admin/deleteobjects", true);
        request.setRequestHeader("Content-type", "text/xml");
        request.setRequestHeader("Content-length", 0);
        request.setRequestHeader("Connection", "close");
        request.send(null);
      }

      function getRadioButtonValue(radioButton)
      {
        for (var index = 0; index < radioButton.length; index++) {
          if (radioButton[index].checked) {
            return radioButton[index].value;
          }
        }
        return "";
      }

      /*
      *
      */
      function getRecacheStatus()
      {
        document.body.style.cursor = 'wait';

        var request = createRequest();

        request.onreadystatechange = function() {
          if (request.readyState == 4) {
            if (request.status && (request.status == 200)) {
              if (request.responseText.indexOf ("finished") >= 0) {
                document.body.style.cursor = 'default';
              }
              else {
                refreshRecacheStatus();
              }
              showResultXml(request);
            }
            else {
              document.body.style.cursor = 'default';
              showResultText(request.statusText + " (" + request.status + "): " + request.responseText);
            }
          }
        }
        request.open("GET", getBaseURL() + "/adm/admin/recache", true);
        request.setRequestHeader("Content-type", "text/xml");
        request.setRequestHeader("Content-length", 0);
        request.setRequestHeader("Connection", "close");
        request.send(null);
      }

      /*
      *
      */
      function getReindexStatus()
      {
        document.body.style.cursor = 'wait';

        var request = createRequest();

        request.onreadystatechange = function() {
          if (request.readyState == 4) {
            if (request.status && (request.status == 200)) {
              if (request.responseText.indexOf ("finished") >= 0) {
                document.body.style.cursor = 'default';
              }
              else {
                refreshReindexStatus();
              }
              showResultXml(request);
            }
            else {
              document.body.style.cursor = 'default';
              showResultText(request.statusText + " (" + request.status + "): " + request.responseText);
            }
          }
        }
        request.open("GET", getBaseURL() + "/adm/admin/reindex", true);
        request.setRequestHeader("Content-type", "text/xml");
        request.setRequestHeader("Content-length", 0);
        request.setRequestHeader("Connection", "close");
        request.send(null);
      }

      function refreshPurgeStatus()
      {
        setTimeout("getPurgeStatus()", 5000);
      }

      function refreshRecacheStatus()
      {
        setTimeout("getRecacheStatus()", 5000);
      }

      function refreshReindexStatus()
      {
        setTimeout("getReindexStatus()", 5000);
      }

      function retrieveObjectsMethod(parameters, type)
      {
        var requestXml = "<param>" + parameters + "<format>deleteParam</format></param>";

        document.forms['deleteobjects'].elements['filter'].value = "";
        showResultText("");

        var request = createRequest();

        for (var index = 0; index < type.length; index++) {
          if (type[index].checked) {
            request.onreadystatechange = function() {
              if (request.readyState == 4) {
                if (request.status && (request.status == 200)) {
                  document.forms['deleteobjects'].elements['filter'].value = request.responseText;
                }
                else {
                  showResultText(request.statusText + " (" + request.status + "): " + request.responseText);
                }
              }
              document.body.style.cursor = 'default';
            }
            request.open("POST", getBaseURL() + type[index].value, true);
            request.setRequestHeader("Content-type", "text/xml");
            request.setRequestHeader("Content-length", requestXml.length);
            request.setRequestHeader("Connection", "close");
            request.send(requestXml);
            break;
          }
        }
        document.body.style.cursor = 'wait';
      }

      /*
      *
      */
      function showResultText(text)
      {
        var doc = document.getElementById("result");

        doc.innerHTML = text;
      }

      /*
      *
      */
      function showResultXml(request)
      {
        var doc = document.getElementById("result");

        if (window.XSLTProcessor) {
          var xsl;

          try {
            xsl = document.implementation.createDocument("", "", null);
            xsl.async = false;
            xsl.load(getBaseURL() + "/xsl/Resource2Html.xsl");
          }
          catch (e) {
            //Google Chrome
            var xmlhttp = new window.XMLHttpRequest();

            xmlhttp.open("GET", getBaseURL() + "/xsl/Resource2Html.xsl",false);
            xmlhttp.send(null);
            xsl = xmlhttp.responseXML.documentElement;
          }

          var xsltProcessor = new XSLTProcessor();

          xsltProcessor.importStylesheet(xsl);
          doc.innerHTML = "";
          doc.appendChild(xsltProcessor.transformToFragment(request.responseXML, document));
        }
        else if (window.ActiveXObject) {
          var xslRequest = createRequest();

          xslRequest.open('GET', getBaseURL() + "/xsl/Resource2Html.xsl", true);
          xslRequest.onreadystatechange = function() {
            if ((xslRequest.readyState == 4) && (xslRequest.status == 200)) {
              stylesheetDoc = new ActiveXObject("MSXML2.DOMDocument.3.0");
              stylesheetDoc.async = false;
              stylesheetDoc.loadXML(xslRequest.responseText);
              doc.innerHTML = request.responseXML.transformNode(stylesheetDoc);
            }
          }
          xslRequest.send(null);
        }
      }
    </script>
</head>

<body bgcolor="#eeeeee">
<table width="100%" height="100%" class="sample">
	<tr height="80"
		style="background-image: url('/images/escidoc-banner.jpg'); background-repeat: no-repeat; background-color: #fcfcfc;">
		<td align="center" colspan="3">
		<h1>Admin Tool</h1>
		</td>
	</tr>
	<tr>
		<td align="right" colspan="3">
		<form name="logout" method="post" action="/aa/logout?target=/adm/">
		  <input type="submit" value="Logout" />
                </form>
		</td>
	</tr>
	<tr>
		<th>Task</th>
		<th>Parameters</th>
		<th>Action</th>
	</tr>
	<tr>
      <form name="retrieveObjects">
		<td valign="top">
		  <h4>Get a list of resources:</h4>
		</td>
		<td>
          <table width="100%">
            <tr>
              <td>
                <textarea name="filter" rows="5" cols="70">
<filter></filter>
                </textarea>
              </td>
              <td>
                <table>
                  <tr>
                    <td valign="top"><b>Resource type:</b></td>
                    <td>
                      <input type="radio" name="type" value="/ir/containers/filter"/>Container<br />
                      <input type="radio" name="type" value="/ir/contexts/filter"/>Context<br />
                      <input type="radio" name="type" value="/ir/items/filter" checked="checked"/>Item<br />
                      <input type="radio" name="type" value="/oum/organizational-units/filter"/>Organizational Unit<br />
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </td>
		<td>
		  <input type="button" value="Retrieve"
			onClick="javascript:retrieveObjectsMethod(document.forms['retrieveObjects'].elements['filter'].value,
                                                                                            document.forms['retrieveObjects'].elements['type'])" />
		</td>
      </form>
	</tr>
	<tr>
		<form name="deleteobjects">
		<td valign="top">
		<h4>Delete a list of resources:</h4>
		</td>
		<td>
                  <table width="100%">
                    <tr>
                      <td>
                        <textarea name="filter" rows="5" cols="70"></textarea>
                      </td>
                    </tr>
                  </table>
                </td>
		<td><input type="button" value="Delete"
			onClick="javascript:adminPostMethod('deleteobjects',
                                                                                document.forms['deleteobjects'].elements['filter'].value)" />
		</td>
		</form>
	</tr>
	<tr>
		<form name="load_examples">
		<td valign="top">
		<h4>Load examples:</h4>
		</td>
		<td>Load common set of examples into eSciDoc.</td>
		<td><input type="button" value="Load Examples"
			onClick="javascript:adminGetMethod('load-examples/common')" />
                </form>
         	</td>
	</tr>
	<tr>
		<form name="recache">
		<td>
		<h4>Rebuild the resource cache:</h4>
		</td>
                <td>
                  <table>
                    <tr>
		      <td>
                        <input type="checkbox" name="clearCache" checked="checked" title="Activate this to clear the cache and add all objects. Otherwise only those objects will be added which are not yet in the cache.">Clear cache
                      </td>
                    </tr>
                  </table>
                </td>
		<td><input type="button" value="Recache"
			onClick="javascript:adminPostMethod('recache/' + document.forms['recache'].elements['clearCache'].checked.toString(), null)" /></td>
		</form>
	</tr>
	<tr>
		<form name="reindex">
		<td>
		<h4>Rebuild the search index:</h4>
		</td>
		<td>
                  <table>
                    <tr>
                      <td colspan="3">
                        <input type="checkbox" name="clearIndex" checked="checked" title="Activate this to clear the index and add all objects. Otherwise only those objects will be added which are not yet in the index.">Clear index
                      </td>
                    </tr>
                    <tr>
                      <td valign="top"><b>Index name:</b></td>
                      <td width="40%">
                        <input type="radio" name="indexNamePrefix" value="all" checked="checked">all<br/>
                      </td>
                      <td>&nbsp;</td>
                    </tr>
		<%
            Map<String, HashMap<String, String>> indexConfigurations = getIndexConfigurations();
            Map<String, HashMap<String, HashMap<String, Object>>> objectTypeParameters = getObjectTypeParameters();
            Set<String> indexNamePrefixes = new HashSet<String>();

            for (String objectType : objectTypeParameters.keySet()) {
                HashMap<String, HashMap<String, Object>> resourceParameters = objectTypeParameters.get(objectType);

                for (String indexNamePrefix : resourceParameters.keySet()) {
                    indexNamePrefixes.add(indexNamePrefix);
                }
            }
            for (String indexNamePrefix : indexNamePrefixes) {
                String styleSheet = null;
                Map<String, String> indexConfiguration = indexConfigurations.get(indexNamePrefix);

                if (indexConfiguration != null) {
                    styleSheet = indexConfiguration.get("fgsindex.defaultUpdateIndexDocXslt");
                    try {
                        new URL(styleSheet);
                    }
                    catch (MalformedURLException e) {
                        styleSheet += ".xslt";
                    }
                }
                if ((styleSheet == null) || (styleSheet.length() == 0)) {
                    styleSheet = "default";
                }
                    %>
                    <tr>
                      <td>&nbsp;</td>
                      <td>
                        <input type="radio" name="indexNamePrefix" value="<%= indexNamePrefix %>"><%= indexNamePrefix %>
                      </td>
                      <td>
                        style sheet : <%= styleSheet %><br/>
                      </td>
                    </tr>
		    <%
            }
            %>
                  </table>
		</td>
		<td><input type="button" value="Reindex"
			onClick="javascript:adminPostMethod('reindex/' + document.forms['reindex'].elements['clearIndex'].checked.toString() + '/' + getRadioButtonValue(document.forms['reindex'].elements['indexNamePrefix']), null)" /></td>
		</form>
	</tr>
	<tr>
		<form name="get-repository-info">
		<td>
		<h4>Get some repository information:</h4>
		</td>
		<td>&nbsp;</td>
		<td><input type="button" value="Repository Info"
			onClick="javascript:adminGetMethod('get-repository-info')" /></td>
		</form>
	</tr>
	<tr height="50%">
		<td colspan="3">
		<div id="result" style="width: 100%; height: 100%;"></div>
		</td>
	</tr>
</table>
</body>
</html>
