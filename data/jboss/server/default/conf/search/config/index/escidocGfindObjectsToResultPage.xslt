<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:param name="ERRORMESSAGE" select="''"/>
    
    <xsl:template match="/">
        <hits>
        <xsl:for-each select="//*[local-name()='hit']">
            <hit>
                <xsl:value-of select="./*[local-name()='field'][@name='PID']"/>
            </hit>
        </xsl:for-each>
        </hits>
    </xsl:template>

</xsl:stylesheet>   
