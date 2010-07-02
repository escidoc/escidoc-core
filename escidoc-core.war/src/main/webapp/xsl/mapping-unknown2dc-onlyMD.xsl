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
	<xsl:template match="/">
		<!-- if there is an element in the dc-elements namespace, do something -->
		<xsl:if test=".//*[namespace-uri() = 'http://purl.org/dc/elements/1.1/']">
			<oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
				xmlns:dc="http://purl.org/dc/elements/1.1/"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				 xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
				<!-- select a value for dc:title, fallback is "eSciDoc Object [id]" -->
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
						<dc:title>eSciDoc Object <xsl:value-of select="$ID"/></dc:title>
					</xsl:otherwise>
				</xsl:choose>
					
				<xsl:choose>
					<xsl:when test=".//dc:description">
						<xsl:copy-of select=".//dc:description" copy-namespaces="no"/>
					</xsl:when>
					<xsl:when test="./*/*[local-name() = 'description']">
						<dc:description>
							<xsl:value-of select="./*/*[local-name() = 'description']"/>
						</dc:description>
					</xsl:when>
				</xsl:choose>
				<!-- 	
				<xsl:if test="$ID"><dc:identifier><xsl:value-of select="$ID"/></dc:identifier></xsl:if>
				 -->
				 <!-- TODO: and local-name() != 'identifier' raulöschen-->
				<xsl:copy-of select="./*/*[namespace-uri() = 'http://purl.org/dc/elements/1.1/' and local-name() != 'identifier' and local-name() != 'title' and local-name() != 'description']" copy-namespaces="no"/>
			</oai_dc:dc>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
