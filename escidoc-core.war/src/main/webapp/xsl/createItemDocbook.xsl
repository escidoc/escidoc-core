<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml"/>
	<xsl:include href="commonSoapRest.xsl"/>
	<xsl:template match="/">
		<chapter>
			<title>Elements and attributes of Item</title>
			<xsl:for-each select="//xs:element[@name='item']/xs:annotation/xs:documentation/para">
				<para>
					<xsl:value-of select="."/>
				</para>
			</xsl:for-each>
			<xsl:apply-templates select="//xs:element[@name='item']"/>
			<xsl:for-each select="//xs:import">
			    <xsl:variable name="pathToXsd" select="substring-after(@schemaLocation,$schemaLocationBase)"/>
<!--  				<xsl:variable name="md-RecordsVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/metadatarecords/')"/> -->
				<xsl:variable name="relationsVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/relations/')"/> 
				<xsl:variable name="componentsVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/components/')"/>

				<xsl:if test="$componentsVersion">
					<xsl:apply-templates select="document(concat('../xsd/',$pathToXsd))//xs:element[@name='components']">
						<xsl:with-param name="SUB">/item</xsl:with-param>
					</xsl:apply-templates>
<!-- 
					<xsl:for-each select="document(concat('../xsd/',$pathToXsd))//xs:import">
						<xsl:variable name="component-md-RecordsVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/metadatarecords/')"/>

						<xsl:if test="$component-md-RecordsVersion">
							<xsl:apply-templates select="document(concat('../xsd/',substring-after(@schemaLocation,$schemaLocationBase)))//xs:element[@name='md-records']">
								<xsl:with-param name="SUB">/item/components</xsl:with-param>
							</xsl:apply-templates>
						</xsl:if>
					</xsl:for-each>
 -->
				</xsl:if>
				<xsl:if test="$relationsVersion">
					<xsl:apply-templates select="document(concat('../xsd/',$pathToXsd))//xs:element[@name='relations']">
						<xsl:with-param name="SUB">/item</xsl:with-param>
					</xsl:apply-templates>
				</xsl:if>
			</xsl:for-each>
		</chapter>
	</xsl:template>
</xsl:stylesheet>
