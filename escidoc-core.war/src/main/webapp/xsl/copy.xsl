<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output encoding="utf-8" method="xml"/>
	
	<!-- falls false soll alles ausgegeben werden, default = true -->
	<xsl:variable name="checkVisible" select="''"/>

  <xsl:variable name="lower" select="'abcdefghijklmnopqrstuvwxyz'"/>
  <xsl:variable name="upper" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
  
  <xsl:variable name="true" select="'true'"/>
  <xsl:variable name="false" select="'false'"/>
  
	<xsl:template match="/">
	<xsl:copy-of select="/"/>
	</xsl:template>
</xsl:stylesheet>
