<%
	session.invalidate();

	Cookie cookie = new Cookie("escidocCookie", "");
	cookie.setPath("/");
	cookie.setMaxAge(0);

	Cookie sessionCookie = new Cookie("JSESSIONID", "");
    sessionCookie.setPath("/");
    sessionCookie.setMaxAge(0);

    response.addCookie(cookie);
    response.addCookie(sessionCookie);
	String target = request.getParameter("target");
	if (target != null)
	{
	    %>
	    <script type="text/javascript">location.href = '<%= target %>'</script>
	    <%
	}
%>