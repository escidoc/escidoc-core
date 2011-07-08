<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>

<html>
<head>
<title>eSciDoc - Open ID Login page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
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
    	<c:if test="${not empty param.login_error}">
      		<font face="Arial, Helvetica, sans-serif" color="red">
        	Your login attempt was not successful, try again.<br/><br/>
        	Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
      		</font>
    	</c:if>
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
