<?xml version="1.0" encoding="UTF-8"?> 
<!--
  ~ CDDL HEADER START
  ~
  ~ The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
  ~ only (the "License"). You may not use this file except in compliance with the License.
  ~
  ~ You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
  ~ for the specific language governing permissions and limitations under the License.
  ~
  ~ When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
  ~ license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
  ~ brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
  ~
  ~ CDDL HEADER END
  ~
  ~ Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
  ~ and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
  ~ terms.
  -->

<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
		
<!-- This xslt stylesheet presents a browseIndex page.
-->
	<xsl:output method="html" indent="yes" encoding="UTF-8"/>

	<xsl:param name="ERRORMESSAGE" select="''"/>
	
	<xsl:variable name="FIELDNAME" select="/resultPage/browseIndex/@fieldName"/>
	<xsl:variable name="INDEXNAME" select="/resultPage/@indexName"/>
	<xsl:variable name="STARTTERM" select="/resultPage/browseIndex/@startTerm"/>
	<xsl:variable name="TERMPAGESIZE" select="/resultPage/browseIndex/@termPageSize"/>
	<xsl:variable name="TERMTOTAL" select="/resultPage/browseIndex/@termTotal"/>
	<xsl:variable name="PAGELASTNO" select="/resultPage/browseIndex/terms/term[position()=last()]/@no"/>
	<xsl:variable name="PAGELASTTERM" select="/resultPage/browseIndex/terms/term[position()=last()]/text()"/>

	<xsl:variable name="EQCHAR">
		<xsl:choose>
			<xsl:when test="$INDEXNAME = 'DemoOnZebra'">=</xsl:when>
			<xsl:otherwise>:</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
							
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
		
		<h2>browseIndex</h2>
			<form method="get" action="rest">
				<table border="3" cellpadding="5" cellspacing="0">
					<tr>
						<td>
							<input type="hidden" name="operation" value="browseIndex"/>
							Start term: <input type="text" name="startTerm" size="30" value="{$STARTTERM}"/> 
							Field name: <select name="fieldName">
					            <xsl:apply-templates select="browseIndex/fields"/>
							</select>
							<xsl:text> </xsl:text>Term page size: <input type="text" name="termPageSize" size="4" value="{$TERMPAGESIZE}"/> 
							<xsl:text> </xsl:text><input type="submit" value="Browse"/>
						</td>
					</tr>
					<tr>
						<td>
							<xsl:text> </xsl:text>Index name: 
								<select name="indexName">
									<xsl:choose>
										<xsl:when test="$INDEXNAME='DemoOnZebra'">
											<option value="DemoOnLucene">DemoOnLucene</option>
											<option value="SmileyDemoOnLucene">SmileyDemoOnLucene</option>
											<option value="SindapDemoOnLucene">SindapDemoOnLucene</option>
											<option value="DemoOnZebra" selected="true">DemoOnZebra</option>
										</xsl:when>
										<xsl:when test="$INDEXNAME='DemoOnLucene'">
											<option value="DemoOnLucene" selected="true">DemoOnLucene</option>
											<option value="SmileyDemoOnLucene">SmileyDemoOnLucene</option>
											<option value="SindapDemoOnLucene">SindapDemoOnLucene</option>
											<option value="DemoOnZebra">DemoOnZebra</option>
										</xsl:when>
										<xsl:when test="$INDEXNAME='SmileyDemoOnLucene'">
											<option value="DemoOnLucene">DemoOnLucene</option>
											<option value="SmileyDemoOnLucene" selected="true">SmileyDemoOnLucene</option>
											<option value="SindapDemoOnLucene">SindapDemoOnLucene</option>
											<option value="DemoOnZebra">DemoOnZebra</option>
										</xsl:when>
										<xsl:when test="$INDEXNAME='SindapDemoOnLucene'">
											<option value="DemoOnLucene">DemoOnLucene</option>
											<option value="SmileyDemoOnLucene">SmileyDemoOnLucene</option>
											<option value="SindapDemoOnLucene" selected="true">SindapDemoOnLucene</option>
											<option value="DemoOnZebra">DemoOnZebra</option>
										</xsl:when>
										<xsl:otherwise>
											<option value="DemoOnLucene">DemoOnLucene</option>
											<option value="SmileyDemoOnLucene">SmileyDemoOnLucene</option>
											<option value="SindapDemoOnLucene">SindapDemoOnLucene</option>
											<option value="DemoOnZebra">DemoOnZebra</option>
										</xsl:otherwise>
									</xsl:choose>
								</select>
							<xsl:text> </xsl:text>restXslt: 
								<select name="restXslt">
									<option value="demoBrowseIndexToHtml">demoBrowseIndexToHtml</option>
									<option value="copyXml">no transformation</option>
								</select>
							<xsl:text> </xsl:text>resultPageXslt: 
								<select name="resultPageXslt">
									<option value="browseIndexToResultPage">browseIndexToResultPage</option>
									<option value="copyXml">no transformation</option>
								</select>
							<xsl:text> </xsl:text>
						</td>
					</tr>
				</table>
			</form>
			<p/>
			<xsl:if test="$TERMTOTAL = 0 and $STARTTERM and $STARTTERM != '' ">
				<p>No terms!</p>
	 		</xsl:if>
			<xsl:if test="$TERMTOTAL > 0">
	 			<table border="0" cellpadding="5" cellspacing="0">
					<tr>
						<xsl:if test="99999999 > $TERMTOTAL">
							<td><xsl:value-of select="$TERMTOTAL"/> terms found.
							</td>
	 					</xsl:if>
					  <xsl:if test="$PAGELASTNO='' or $PAGELASTNO=' ' or $TERMTOTAL > $PAGELASTNO">
	 					<td>
						<form method="get" action="rest">
							<input type="hidden" name="operation" value="browseIndex"/>
							<input type="hidden" name="fieldName" value="{$FIELDNAME}"/>
							<input type="hidden" name="indexName" value="{$INDEXNAME}"/>
							<input type="hidden" name="startTerm" value="{$PAGELASTTERM}"/>
							<input type="hidden" name="termPageSize" value="{$TERMPAGESIZE}"/>
							<input type="submit" value="Next term page"/>
						</form>
	 					</td>
	 				  </xsl:if>
					</tr>
				</table>
				<table border="3" cellpadding="5" cellspacing="0" bgcolor="silver">
					<xsl:apply-templates select="browseIndex/terms"/>
				</table>
	 		</xsl:if>
	</xsl:template>

	<xsl:template match="field">
		<xsl:variable name="THISFIELDNAME" select="text()"/>
		<xsl:choose>
			<xsl:when test="$FIELDNAME=$THISFIELDNAME">
				<option selected="true">
				    <xsl:attribute name="value">
				        <xsl:value-of select="$THISFIELDNAME"/>
					</xsl:attribute>
				    <xsl:value-of select="$THISFIELDNAME"/>
				</option>
			</xsl:when>
			<xsl:otherwise>
				<option>
				    <xsl:attribute name="value">
				        <xsl:value-of select="$THISFIELDNAME"/>
					</xsl:attribute>
				    <xsl:value-of select="$THISFIELDNAME"/>
				</option>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="term">
		<tr>
			<td>
				<xsl:value-of select="@no"/>.
				<a>
					<xsl:variable name="TERM" select="text()"/>
					<xsl:variable name="QUERYSTRING" select="concat('?operation=gfindObjects', '&amp;', 'indexName=', $INDEXNAME, '&amp;', 'query=', $FIELDNAME, $EQCHAR, '&#034;', $TERM, '&#034;')"/>
					<xsl:attribute name="href"><xsl:value-of select="$QUERYSTRING"/>
					</xsl:attribute>
					<xsl:value-of select="$TERM"/>
					[<xsl:value-of select="@fieldtermhittotal"/>]
				</a>
			</td>
		</tr>
	</xsl:template>
	
</xsl:stylesheet>	





				




