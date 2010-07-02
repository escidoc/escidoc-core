<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
		
<!-- This xslt stylesheet puts the repository specific info into a table,
     each element text being put into one table cell.
-->
	
	<xsl:output method="html" indent="yes" encoding="UTF-8"/>

	<xsl:param name="ERRORMESSAGE" select="''"/>

	<xsl:param name="TIMEUSEDMS" select="''"/>

	<xsl:template match="/resultPage">
		<html>
			<head>
				<title>REST Client Demo of Fedora Generic Search Service</title>
				<link rel="stylesheet" type="text/css" href="css/demo.css"/>
				<style type="text/css">
					.highlight {
						background: yellow;
					}
				</style>
				<script language="javascript">
				</script>
			</head>
			<body>
				<div id="header">
					<a href="" id="logo"></a>
					<div id="title">
						<h1>REST Client Demo of Fedora Generic Search Service</h1>
					</div>
				</div>
				<table cellspacing="10" cellpadding="10">
					<tr>
					<th><a href="?operation=updateIndex">updateIndex</a></th>
					<th><a href="?operation=gfindObjects">gfindObjects</a></th>
					<th><a href="?operation=browseIndex">browseIndex</a></th>
					<th><a href="?operation=getRepositoryInfo">getRepositoryInfo</a></th>
					<th><a href="?operation=getIndexInfo">getIndexInfo</a></th>
					<td>(<xsl:value-of select="$TIMEUSEDMS"/> milliseconds)</td>
					</tr>
				</table>
				<p/>
				<!-- 
				<xsl:if test="$ERRORMESSAGE">
					<xsl:call-template name="error"/>
	 			</xsl:if>
	 			 -->
        		<xsl:apply-templates select="error"/>
				<xsl:call-template name="opSpecifics"/>
				<div id="footer">
   					<div id="copyright">
						Copyright &#xA9; 2006 Technical University of Denmark, Fedora Project
					</div>
					<div id="lastModified">
						Last Modified
						<script type="text/javascript">
							//<![CDATA[
							var cvsDate = "$Date: 2006/10/13 14:17:06 $";
							var parts = cvsDate.split(" ");
							var modifiedDate = parts[1];
							document.write(modifiedDate);
							//]]>
						</script>
						by Gert Schmeltz Pedersen 
					</div>
				</div>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template name="error">
		<p>
			<font color="red">
				<xsl:value-of select="$ERRORMESSAGE"/>
			</font>
		</p>			
	</xsl:template>
	
	<xsl:template match="message">
		<p>
			<font color="red">
				<xsl:value-of select="./text()"/>
			</font>
		</p>			
	</xsl:template>
	
	<xsl:template name="opSpecifics">
		<xsl:variable name="REPOSITORYNAME" select="@repositoryName"/>
		<h2>getRepositoryInfo</h2>
			<form method="get" action="rest">
				<table border="3" cellpadding="5" cellspacing="0">
					<tr>
						<td>
							<xsl:text> </xsl:text>Repository name: 
								<select name="repositoryName">
									<xsl:choose>
										<xsl:when test="$REPOSITORYNAME='DemoAtDtu'">
											<option value="DemoAtDtu" selected="true">DemoAtDtu</option>
											<option value="SindapAtDtu">SindapAtDtu</option>
										</xsl:when>
										<xsl:when test="$REPOSITORYNAME='SindapAtDtu'">
											<option value="DemoAtDtu">DemoAtDtu</option>
											<option value="SindapAtDtu" selected="true">SindapAtDtu</option>
										</xsl:when>
										<xsl:otherwise>
											<option value="DemoAtDtu">DemoAtDtu</option>
											<option value="SindapAtDtu">SindapAtDtu</option>
										</xsl:otherwise>
									</xsl:choose>
								</select>
						</td>
						<td>
							<input type="hidden" name="operation" value="getRepositoryInfo"/>
							<xsl:text> </xsl:text><input type="submit" value="Show it"/>
						</td>
					</tr>
				</table>
			</form>
			<p/>
		<table border="3" cellpadding="5" cellspacing="0" width="784">
			<xsl:for-each select="repositoryInfo/*">
			<tr>
				<td>
					<xsl:value-of select="local-name()"/>
				</td>			
				<td>
					<xsl:copy-of select="node()"/>
				</td>			
			</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	
</xsl:stylesheet>	





				




