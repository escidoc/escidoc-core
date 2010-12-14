<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xlink="http://www.w3.org/1999/xlink">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:template match="node()|@*">
  		<xsl:copy>
    		<xsl:apply-templates select="@*|node()"/>
  		</xsl:copy>
	</xsl:template>

	<xsl:template match="/*[local-name()='search-result-record']/*[local-name()='item']/*[local-name()='resources']" />
	<xsl:template match="/*[local-name()='search-result-record']/*[local-name()='container']/*[local-name()='resources']" />

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
	
	
</xsl:stylesheet>	
