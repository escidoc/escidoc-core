<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xlink="http://www.w3.org/1999/xlink">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:template match="node()|@*">
  		<xsl:copy>
    		<xsl:apply-templates select="@*|node()" />
  		</xsl:copy>
	</xsl:template>

	<xsl:template match="*[local-name()='resources']" />

	<xsl:template match="@xlink:title" />
	<xsl:template match="@xlink:type" />
	<xsl:template match="@xlink:href">
		<xsl:attribute name="objid">
    		<xsl:value-of select="."/>
  		</xsl:attribute>
	</xsl:template>
	
	
	
	
	
	
	
	
	

</xsl:stylesheet>	
