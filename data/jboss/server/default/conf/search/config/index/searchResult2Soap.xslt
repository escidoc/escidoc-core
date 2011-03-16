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
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xlink="http://www.w3.org/1999/xlink">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:template match="node()|@*">
  		<xsl:copy>
    		<xsl:apply-templates select="@*|node()"/>
  		</xsl:copy>
	</xsl:template>

	<xsl:template match="/*[local-name()='search-result-record']/*/*[local-name()='resources']" />
	<xsl:template match="/*/*[local-name()='resources']" />

	<xsl:template match="@xml:base" />
	<xsl:template match="@xlink:title" />
	<xsl:template match="@xlink:type" />
	<xsl:template match="@xlink:href">
		<xsl:variable name="href">
        	<xsl:call-template name="substring-after-last">
            	<xsl:with-param name="string" select="."/>
                <xsl:with-param name="delimiter">/</xsl:with-param>
            </xsl:call-template>
		</xsl:variable>
		<xsl:if test="contains($href, ':')">
			<xsl:attribute name="objid"><xsl:value-of select="$href"/></xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="substring-after-last">
	  <xsl:param name="string" />
	  <xsl:param name="delimiter" />
	  <xsl:choose>
	    <xsl:when test="contains($string, $delimiter)">
	      <xsl:call-template name="substring-after-last">
	        <xsl:with-param name="string"
	          select="substring-after($string, $delimiter)" />
	        <xsl:with-param name="delimiter" select="$delimiter" />
	      </xsl:call-template>
	    </xsl:when>
	    <xsl:otherwise><xsl:value-of select="$string"/></xsl:otherwise>
	  </xsl:choose>
	</xsl:template>

	<xsl:template name="convertXml">
  		<xsl:copy>
    		<xsl:apply-templates select="@*|node()"/>
  		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>	
