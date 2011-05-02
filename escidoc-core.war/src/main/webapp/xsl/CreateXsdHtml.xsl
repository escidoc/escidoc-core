<?xml version="1.0" encoding="UTF-8"?>
<!-- 
 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License").  You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END

 Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 fuer wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Foerderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
 -->
<!-- 
 XSLT to transform XML Schema defining eSciDoc resources into 
 documentation of elements and attributes of the resource in form 
 of a HTML page.
 XML Schema documents of imported namespaces are loaded from the 
 given ''schemaLocation''. Because prefixes are used to find 
 referenced elements/attributes the use of one prefix for different
 namespaces inside the schema graph will probably cause errors.

 author Frank Schwichtenberg
 -->
 <xsl:stylesheet 
 		version="2.0" 
 		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
 		xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html"/>
    <xsl:param name="schemaLocationBase"/>
    <xsl:param name="schemaLocationBaseReplacement"/>
	<xsl:template match="/">
		<xsl:variable name="bindings">
			<!-- test namespaces and include -->
			<bindings>
				<xsl:for-each select="xs:schema/namespace::*">
					<xsl:call-template name="createBindings"/>
				</xsl:for-each>
			</bindings>
		</xsl:variable>
		<!-- xsl:copy-of select="$bindings//binding[prefix = 'md-records']//xs:element[@name = 'md-record']"/ -->
		<html>
			<head>
				<script language="JavaScript" type="text/javascript" src="/js/shownhide.js">
				</script>
			</head>		
			<body>
				<dl>
					<!-- xsl:copy-of select="$bindings"/ -->
					<!-- start with first globaly defined element, the rest recursive -->
					<xsl:apply-templates select="xs:schema/xs:element[1]">
						<xsl:with-param name="bindings" select="$bindings"/>
					</xsl:apply-templates>
				</dl>
			</body>
		</html>
	</xsl:template>
	<xsl:template name="createBindings">
		<xsl:variable name="prefix">
			<xsl:value-of select="local-name()"/>
		</xsl:variable>
		<xsl:variable name="NS">
			<xsl:value-of select="."/>
		</xsl:variable>
		<xsl:variable name="theSchemaLocation">
			<xsl:value-of select="replace(//xs:import[@namespace = $NS]/@schemaLocation,$schemaLocationBase,$schemaLocationBaseReplacement)"/>
		</xsl:variable>
		<xsl:if test="$prefix != '' and $theSchemaLocation != ''">
			<xsl:message>Including <xsl:value-of select="$theSchemaLocation"/>
			</xsl:message>
			<xsl:variable name="theSchemaDocument" select="document($theSchemaLocation)"/>
			<binding>
				<prefix>
					<xsl:value-of select="$prefix"/>
				</prefix>
				<namespace>
					<xsl:value-of select="$NS"/>
				</namespace>
				<location>
					<xsl:value-of select="$theSchemaLocation"/>
				</location>
				<content>
					<xsl:copy-of select="$theSchemaDocument"/>
				</content>
			</binding>
			<xsl:for-each select="$theSchemaDocument/xs:schema/namespace::*">
				<xsl:call-template name="createBindings"/>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
	<xsl:template match="xs:element[@name]">
		<xsl:param name="path">/</xsl:param>
		<xsl:param name="bindings"/>
		<xsl:choose>
			<xsl:when test="xs:annotation/xs:documentation/*">
				<dt>
					<xsl:attribute name="id"><xsl:value-of select="$path"/><xsl:value-of select="@name"/></xsl:attribute>
					<xsl:attribute name="onClick">toggleVisibility('<xsl:value-of select="$path"/><xsl:value-of select="@name"/>');</xsl:attribute>
					<xsl:value-of select="$path"/>
					<xsl:value-of select="@name"/>
				</dt>
				<dd>
					<xsl:apply-templates select="xs:annotation/xs:documentation"/>
				</dd>
				<br/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>Element declaration without documentation. [<xsl:value-of select="@name"/>][<xsl:value-of select="$path"/>]</xsl:comment>
			</xsl:otherwise>
		</xsl:choose>
		<dl>
			<xsl:if test="xs:annotation/xs:documentation/*">
				<xsl:attribute name="style">display: none; visibility: hidden;</xsl:attribute>
				<xsl:attribute name="id"><xsl:value-of select="$path"/><xsl:value-of select="@name"/>-body</xsl:attribute>
			</xsl:if>
			<!-- either local or global type for this element -->
			<xsl:choose>
				<xsl:when test="not(xs:complexType)">
					<xsl:variable name="typePrefix">
						<xsl:value-of select="substring-before(@type, ':')"/>
					</xsl:variable>
					<xsl:variable name="typeLocalName">
						<xsl:value-of select="substring-after(@type, ':')"/>
					</xsl:variable>
					<xsl:apply-templates select="$bindings//binding[prefix = $typePrefix][1]//xs:complexType[@name = $typeLocalName]">
						<xsl:with-param name="path">
							<xsl:value-of select="$path"/>
							<xsl:value-of select="@name"/>/</xsl:with-param>
						<xsl:with-param name="bindings" select="$bindings"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="xs:complexType">
						<xsl:with-param name="path">
							<xsl:value-of select="$path"/>
							<xsl:value-of select="@name"/>/</xsl:with-param>
						<xsl:with-param name="bindings" select="$bindings"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</dl>
	</xsl:template>
	<xsl:template match="xs:element[@ref]">
		<xsl:param name="path">/</xsl:param>
		<xsl:param name="bindings"/>
		<xsl:variable name="refPrefix">
			<xsl:value-of select="substring-before(@ref, ':')"/>
		</xsl:variable>
		<xsl:variable name="refLocalName">
			<xsl:value-of select="substring-after(@ref, ':')"/>
		</xsl:variable>
		<xsl:variable name="elementDecl" 
			select="$bindings//binding[prefix = $refPrefix][1]//xs:element[@name = $refLocalName]|//xs:element[@name = $refLocalName]"/>
		<xsl:choose>
			<xsl:when test="xs:annotation/xs:documentation/* or not($elementDecl/xs:annotation/xs:documentation/*)">
				<dt>
					<xsl:attribute name="id"><xsl:value-of select="$path"/><xsl:value-of select="$refLocalName"/></xsl:attribute>
					<xsl:attribute name="onClick">toggleVisibility('<xsl:value-of select="$path"/><xsl:value-of select="$refLocalName"/>');</xsl:attribute>
					<xsl:value-of select="$path"/>
					<xsl:value-of select="$refLocalName"/>
				</dt>
				<dd>
					<xsl:apply-templates select="xs:annotation/xs:documentation"/>
				</dd>
				<br/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>Element reference without documentation. [<xsl:value-of select="$refPrefix"/>:<xsl:value-of select="$refLocalName"/>][<xsl:value-of select="$path"/>]</xsl:comment>
			</xsl:otherwise>
		</xsl:choose>
		<dl>
			<xsl:if test="xs:annotation/xs:documentation/* or not($elementDecl/xs:annotation/xs:documentation/*)">
				<xsl:attribute name="style">display: none; visibility: hidden;</xsl:attribute>
				<xsl:attribute name="id"><xsl:value-of select="$path"/><xsl:value-of select="$refLocalName"/>-body</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="$elementDecl">
				<xsl:with-param name="path" select="$path"/>
				<xsl:with-param name="bindings" select="$bindings"/>
			</xsl:apply-templates>
		</dl>
	</xsl:template>
	<xsl:template match="xs:attributeGroup">
		<xsl:param name="path"/>
		<xsl:param name="bindings"/>
		<xsl:variable name="refPrefix">
			<xsl:value-of select="substring-before(@ref, ':')"/>
		</xsl:variable>
		<xsl:variable name="refLocalName">
			<xsl:value-of select="substring-after(@ref, ':')"/>
		</xsl:variable>
		<xsl:apply-templates select="$bindings//binding[prefix = $refPrefix][1]//xs:attributeGroup[@name = $refLocalName]/xs:attribute">
			<xsl:with-param name="path" select="$path"/>
			<xsl:with-param name="bindings" select="$bindings"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="xs:attribute[@name]">
		<xsl:param name="path">/</xsl:param>
		<xsl:param name="bindings"/>
		<xsl:param name="prefix"/>
		<xsl:choose>
			<xsl:when test="xs:annotation/xs:documentation/*">
				<dt>
					<xsl:value-of select="$path"/>@<xsl:if test="@prefix">
						<xsl:value-of select="@prefix"/>:</xsl:if>
					<xsl:value-of select="@name"/>
				</dt>
				<dd>
					<xsl:apply-templates select="xs:annotation/xs:documentation"/>
				</dd>
				<br/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>Attribute declaration without documentation. [<xsl:value-of select="@name"/>][<xsl:value-of select="$path"/>]</xsl:comment>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="xs:attribute[@ref]">
		<xsl:param name="path">/</xsl:param>
		<xsl:param name="bindings"/>
		<xsl:variable name="refPrefix">
			<xsl:value-of select="substring-before(@ref, ':')"/>
		</xsl:variable>
		<xsl:variable name="refLocalName">
			<xsl:value-of select="substring-after(@ref, ':')"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="xs:annotation/xs:documentation/*">
				<dt>
					<xsl:value-of select="$path"/>@<xsl:value-of select="@ref"/>
				</dt>
				<dd>
					<xsl:apply-templates select="xs:annotation/xs:documentation"/>
				</dd>
				<br/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>Attribute reference without documentation. [<xsl:value-of select="$refPrefix"/>:<xsl:value-of select="$refLocalName"/>][<xsl:value-of select="$path"/>]</xsl:comment>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="$bindings//binding[prefix = $refPrefix][1]//xs:attribute[@name = $refLocalName]">
			<xsl:with-param name="path" select="$path"/>
			<xsl:with-param name="bindings" select="$bindings"/>
			<xsl:with-param name="prefix" select="$refPrefix"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="xs:complexType">
		<xsl:param name="path"/>
		<xsl:param name="bindings"/>
		<!-- attributes declared in local type -->
		<xsl:apply-templates select="./xs:attribute">
			<xsl:with-param name="path">
				<xsl:value-of select="$path"/>
			</xsl:with-param>
			<xsl:with-param name="bindings" select="$bindings"/>
		</xsl:apply-templates>
		<!-- attributes declared in a referenced attribute group -->
		<xsl:apply-templates select="./xs:attributeGroup">
			<xsl:with-param name="path">
				<xsl:value-of select="$path"/>
			</xsl:with-param>
			<xsl:with-param name="bindings" select="$bindings"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="./xs:sequence/xs:element">
			<xsl:with-param name="path">
				<xsl:value-of select="$path"/>
			</xsl:with-param>
			<xsl:with-param name="bindings" select="$bindings"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="xs:documentation">
		<xsl:if test=".//create">
			<span class="create">
				<xsl:value-of select=".//create"/> on create</span>
		</xsl:if>
		<xsl:if test=".//create and .//update">, </xsl:if>
		<xsl:if test=".//create and not(.//update)">. </xsl:if>
		<xsl:if test=".//update">
			<span class="update">
				<xsl:value-of select=".//update"/> on update. </span>
		</xsl:if>
		<xsl:if test=".//comment">
			<span class="comment">Note: <xsl:value-of select=".//comment"/>
			</span>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*">
		<!-- 
		<found>
			<xsl:attribute name="element"><xsl:value-of select="local-name()"/></xsl:attribute>
			<xsl:for-each select="@*">
				<xsl:copy/>
			</xsl:for-each>
			<xsl:value-of select="."/>
		</found>
		<xsl:apply-templates/>
		-->
	</xsl:template>
</xsl:stylesheet>
