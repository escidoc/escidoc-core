<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:escidoc="http://www.escidoc.de/ontologies/properties/" xmlns:rel="http://www.nsdl.org/ontologies/relationships/">

	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	
	<xsl:template match="*">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="/">
		<atom:feed>
			<atom:title>Item List</atom:title>
			<atom:link rel="self" type="application/atom+xml" href="http://www.escidoc.de/ir/items"/>
			<atom:id>http://www.escidoc.de/ir/items</atom:id>
			<atom:updated>2007-11-08T12:28:05+01:00</atom:updated>
			<atom:generator>Frank Schwichtenberg (FRS)</atom:generator>
			<xsl:apply-templates/>
		</atom:feed>
	</xsl:template>
	
	<xsl:template match="/rdf:RDF/rdf:Description">
		<xsl:variable name="ID"><xsl:value-of select="substring(@rdf:about, 13)"/></xsl:variable>
		<atom:entry>
			<atom:author><atom:name><xsl:value-of select="dc:creator"/></atom:name></atom:author>
			<atom:id><xsl:value-of select="$ID"/></atom:id>
			<atom:link rel="self">
				<xsl:attribute name="href">http://www.escidoc.de/ir/item/<xsl:value-of select="$ID"/></xsl:attribute>
			</atom:link>
			<atom:title><xsl:value-of select="dc:title"/></atom:title>
			<atom:summary><xsl:value-of select="dc:description"/></atom:summary>
			<atom:updated><xsl:value-of select="*[local-name() = 'latest-version.date']"/></atom:updated>
			<xsl:copy-of select="*[namespace-uri() = 'http://purl.org/dc/elements/1.1/']" copy-namespaces="no"/>
			<xsl:if test="rel:hasComponent">
				<escidoc:component-cardinality><xsl:value-of select="count(rel:hasComponent)"/></escidoc:component-cardinality>
			</xsl:if>
		</atom:entry>
	</xsl:template>
	
</xsl:stylesheet>
