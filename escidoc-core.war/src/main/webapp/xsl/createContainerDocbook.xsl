<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml"/>
	<xsl:include href="commonSoapRest.xsl"/>
	<xsl:template match="/">
		<chapter>
			<title>Elements and attributes of Container</title>
			<xsl:for-each select="//xs:element[@name='container']/xs:annotation/xs:documentation/para">
				<para>
					<xsl:value-of select="."/>
				</para>
			</xsl:for-each>
			
			<xsl:apply-templates select="//xs:element[@name='container']"/>
			
			<xsl:for-each select="//xs:import">
			    <xsl:variable name="pathToXsd" select="substring-after(@schemaLocation,$schemaLocationBase)"/>
<!--  19.09.2007 hka
				<xsl:variable name="resourceVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/resources/')"/>				
				<xsl:variable name="md-RecordsVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/metadatarecords/')"/>
				<xsl:variable name="relationsVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/relations/')"/>
-->
				<xsl:variable name="structMapVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/structmap/')"/>
				<xsl:variable name="adminDescriptorVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/admindescriptor/')"/>
<!--   19.09.2007 hka
				<xsl:if test="$resourceVersion">
					<xsl:apply-templates select="document(concat('../xsd/',$pathToXsd))//xs:element[@name='resources']">
						<xsl:with-param name="SUB">/container</xsl:with-param>
					</xsl:apply-templates>			
				</xsl:if>
				
				<xsl:if test="$md-RecordsVersion">
					<xsl:apply-templates select="document(concat('../xsd/',$pathToXsd))//xs:element[@name='md-records']">
						<xsl:with-param name="SUB">/container</xsl:with-param>
					</xsl:apply-templates>		
				</xsl:if>

				<xsl:if test="$relationsVersion">
					<xsl:apply-templates select="document(concat('../xsd/',$pathToXsd))//xs:element[@name='relations']">
						<xsl:with-param name="SUB">/container</xsl:with-param>
					</xsl:apply-templates>
				</xsl:if>
-->				
				<xsl:if test="$structMapVersion">
					<xsl:apply-templates select="document(concat('../xsd/',$pathToXsd))//xs:element[@name='struct-map']">
						<xsl:with-param name="SUB">/container</xsl:with-param>
					</xsl:apply-templates>
				</xsl:if>	
		
				<xsl:if test="$adminDescriptorVersion">
					<xsl:apply-templates select="document(concat('../xsd/',$pathToXsd))//xs:element[@name='admin-descriptor']">
						<xsl:with-param name="SUB">/container</xsl:with-param>
					</xsl:apply-templates>
				</xsl:if>	
			
			</xsl:for-each>	
			
		</chapter>
	</xsl:template>
</xsl:stylesheet>
