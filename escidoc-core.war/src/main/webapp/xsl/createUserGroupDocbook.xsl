<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml"/>
	<xsl:include href="commonSoapRest.xsl"/>
	<xsl:template match="/">
		<!-- <chapter>
			<title>Elements and attributes of UserGroup and Grants</title> -->
            <section>
            <title>Elements and attributes of UserGroup</title>
                <xsl:for-each
                    select="//xs:element[@name='user-group']/xs:annotation/xs:documentation/para">
                    <para>
                        <xsl:value-of select="."/> </para>
                </xsl:for-each>
                <xsl:apply-templates
                    select="//xs:element[@name='user-group']"/>
           
           
            </section>
			
			<!--<section>
			<title>Elements and attributes of Grants</title>
				<xsl:apply-templates select="document('../xsd/user-account/0.5/grants.xsd')//xs:element[@name='grant']">
                    <xsl:with-param name="SUB">
                        /user-account</xsl:with-param>
				</xsl:apply-templates>
			</section>
		 </chapter> -->
	</xsl:template>
</xsl:stylesheet>
