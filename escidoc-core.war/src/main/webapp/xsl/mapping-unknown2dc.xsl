<?xml version="1.0" encoding="UTF-8"?>
<!--
  $Id$
  XSLT transformation from eSciDoc XSLT Schema to Dublin Core
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:md-records="http://www.escidoc.de/schemas/metadatarecords/0.3" xmlns:md-profile-escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:types="http://escidoc.mpg.de/metadataprofile/schema/0.1/types" exclude-result-prefixes="xsl md-records md-profile-escidoc types">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<xsl:param name="ID"></xsl:param>
	<xsl:template match="*">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="/*/*/md-records:md-record[@name='escidoc'] | /md-records:md-record[@name='escidoc']">
		<xsl:if test=".//*[namespace-uri() = 'http://purl.org/dc/elements/1.1/']">
			<oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:prefix-dc="http://purl.org/dc/elements/1.1/">
				<xsl:choose>
					<xsl:when test=".//dc:title">
						<xsl:copy-of select=".//dc:title" copy-namespaces="no"/>
					</xsl:when>
					<xsl:when test="./*/*[local-name() = 'name']">
						<dc:title>
							<xsl:value-of select="./*/*[local-name() = 'name']"/>
						</dc:title>
					</xsl:when>
					<xsl:when test="./*/*[local-name() = 'title']">
						<dc:title>
							<xsl:value-of select="./*/*[local-name() = 'title']"/>
						</dc:title>
					</xsl:when>
					<xsl:otherwise>
						<dc:title>Item</dc:title>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="$ID"><dc:identifier><xsl:value-of select="$ID"/></dc:identifier></xsl:if>
				<xsl:copy-of select=".//*[namespace-uri() = 'http://purl.org/dc/elements/1.1/' and local-name() != 'title']" copy-namespaces="no"/>
			</oai_dc:dc>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
