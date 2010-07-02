<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output method="html" indent="yes" encoding="UTF-8"/>

	<xsl:param name="ERRORMESSAGE" select="''"/>

	<xsl:param name="TIMEUSEDMS" select="''"/>

	<xsl:template match="/resultPage">
        		<xsl:apply-templates select="error"/>
	</xsl:template>
	
	<xsl:template name="error">
		<p>
			<font color="red">
				<xsl:value-of select="/error/message"/>
			</font>
		</p>			
	</xsl:template>
	
</xsl:stylesheet>	





				




