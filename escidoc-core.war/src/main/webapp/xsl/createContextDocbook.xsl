<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml"/>
	<xsl:include href="commonSoapRest.xsl"/>
	<xsl:template match="/">
		<chapter>
			<title>List of elements and attributes</title>
			<xsl:for-each select="//xs:element[@name='context']/xs:annotation/xs:documentation/para">
				<para>
					<xsl:value-of select="."/>
				</para>
			</xsl:for-each>
			<xsl:apply-templates select="//xs:element[@name='context']"/>
			
			<xsl:for-each select="//xs:import">
			<xsl:variable name="pathToXsd" select="substring-after(@schemaLocation,$schemaLocationBase)"/>
				<xsl:variable name="adminDescriptorVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/admindescriptor/')"/>
				<xsl:if test="$adminDescriptorVersion">
					<xsl:apply-templates select="document(concat('../xsd/',$pathToXsd))//xs:element[@name='admin-descriptor']">
						<xsl:with-param name="SUB">/context</xsl:with-param>
					</xsl:apply-templates>
				</xsl:if>
			</xsl:for-each>						
		</chapter>
	</xsl:template>
</xsl:stylesheet>
