<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml"/>
	<xsl:include href="commonSoapRest.xsl"/>
	<xsl:template match="/">
		<chapter>
			<title>Elements and attributes of ReportDefinitionList</title>
            <xsl:for-each
                select="//xs:element[@name='report-definition-list']/xs:annotation/xs:documentation/para">
                <para>
                    <xsl:value-of select="."/> </para>
            </xsl:for-each>
            <xsl:apply-templates
                select="//xs:element[@name='report-definition-list']"/>
			<xsl:for-each select="//xs:import">
			    <xsl:variable name="pathToXsd" select="substring-after(@schemaLocation,$schemaLocationBase)"/>
				<xsl:variable name="reportdefinitionVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/reportdefinition/')"/>
				<xsl:if test="$reportdefinitionVersion">
          <xsl:apply-templates select="document(concat('../../../../../sm/src/main/xsd/',$pathToXsd))//xs:element[@name='report-definition']">
						<xsl:with-param name="SUB">/report-definition-list</xsl:with-param>
					</xsl:apply-templates>
				</xsl:if>
			</xsl:for-each>
           
		</chapter>
	</xsl:template>
</xsl:stylesheet>
