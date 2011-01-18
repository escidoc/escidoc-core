<?xml version="1.0" encoding="UTF-8"?>
<!--
  $Id$
  XSLT transformation from eSciDoc XML Schema to Dublin Core
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:md-profile-escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:types="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
	exclude-result-prefixes="xsl md-profile-escidoc types">

	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<xsl:param name="ID"/>

	<!-- disable default template -->
	<xsl:template match="*"/>

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
						<dc:title>
							<xsl:value-of select=".//dc:title"/>
						</dc:title>
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
						<dc:description>
							<xsl:value-of select=".//dc:description"/>
						</dc:description>
					</xsl:when>
					<xsl:when test="./*/*[local-name() = 'description']">
						<dc:description>
							<xsl:value-of select="./*/*[local-name() = 'description']"/>
						</dc:description>
					</xsl:when>
				</xsl:choose>

				<!-- xsl:copy-of select="./*/*[namespace-uri() = 'http://purl.org/dc/elements/1.1/' and local-name() != 'identifier' and local-name() != 'title' and local-name() != 'description']" copy-namespaces="no"/ -->
				<xsl:apply-templates select="./*/*"/>

			</oai_dc:dc>
		</xsl:if>
	</xsl:template>

	<!-- DC ELEMENTS -->

	<xsl:template match="dc:title">
		<!-- already handled matching document root -->
	</xsl:template>

	<xsl:template match="dc:description">
		<!-- already handled matching document root -->
	</xsl:template>

	<xsl:template match="dc:creator">
		<dc:creator>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:creator>
	</xsl:template>

	<xsl:template match="dc:subject">
		<dc:subject>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:subject>
	</xsl:template>

	<xsl:template match="dc:publisher">
		<dc:publisher>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:publisher>
	</xsl:template>

	<xsl:template match="dc:contributor">
		<dc:contributor>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:contributor>
	</xsl:template>

	<xsl:template match="dc:date">
		<dc:date>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:date>
	</xsl:template>

	<xsl:template match="dc:type">
		<dc:type>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:type>
	</xsl:template>

	<xsl:template match="dc:format">
		<dc:format>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:format>
	</xsl:template>

	<xsl:template match="dc:identifier">
		<dc:identifier>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:identifier>
	</xsl:template>

	<xsl:template match="dc:source">
		<dc:source>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:source>
	</xsl:template>

	<xsl:template match="dc:language">
		<dc:language>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:language>
	</xsl:template>

	<xsl:template match="dc:relation">
		<dc:relation>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:relation>
	</xsl:template>

	<xsl:template match="dc:coverage">
		<dc:coverage>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:coverage>
	</xsl:template>

	<xsl:template match="dc:rights">
		<dc:rights>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:rights>
	</xsl:template>

	<xsl:template name="dcSimpleElementContent">
		<xsl:param name="dcElement"/>
		<xsl:copy-of select="@xml:lang"/>
		<xsl:value-of select="."/>
	</xsl:template>

	<!-- DC TERMS -->

</xsl:stylesheet>
