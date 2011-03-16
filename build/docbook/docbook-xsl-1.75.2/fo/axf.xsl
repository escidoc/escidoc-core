<?xml version='1.0'?>
<!--
  ~ CDDL HEADER START
  ~
  ~ The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
  ~ only (the "License"). You may not use this file except in compliance with the License.
  ~
  ~ You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
  ~ for the specific language governing permissions and limitations under the License.
  ~
  ~ When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
  ~ license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
  ~ brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
  ~
  ~ CDDL HEADER END
  ~
  ~ Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
  ~ and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
  ~ terms.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:axf="http://www.antennahouse.com/names/XSL/Extensions"
                version='1.0'>

<!-- ********************************************************************
     $Id: axf.xsl 6483 2007-01-08 18:00:22Z bobstayton $
     ******************************************************************** -->

<xsl:template name="axf-document-information">

    <xsl:variable name="authors" select="(//author|//editor|
                                          //corpauthor|//authorgroup)[1]"/>
    <xsl:if test="$authors">
      <xsl:variable name="author">
        <xsl:choose>
          <xsl:when test="$authors[self::authorgroup]">
            <xsl:call-template name="person.name.list">
              <xsl:with-param name="person.list" 
                 select="$authors/*[self::author|self::corpauthor|
                               self::othercredit|self::editor]"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="$authors[self::corpauthor]">
            <xsl:value-of select="$authors"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="person.name">
              <xsl:with-param name="node" select="$authors"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:element name="axf:document-info">
        <xsl:attribute name="name">author</xsl:attribute>
        <xsl:attribute name="value">
          <xsl:value-of select="normalize-space($author)"/>
        </xsl:attribute>
      </xsl:element>
    </xsl:if>

    <xsl:variable name="title">
      <xsl:apply-templates select="/*[1]" mode="label.markup"/>
      <xsl:apply-templates select="/*[1]" mode="title.markup"/>
    </xsl:variable>

    <!-- * see bug report #1465301 - mzjn -->
    <axf:document-info name="title">
      <xsl:attribute name="value">
        <xsl:value-of select="normalize-space($title)"/>
      </xsl:attribute>
    </axf:document-info>

    <xsl:if test="//keyword">
      <xsl:element name="axf:document-info">
        <xsl:attribute name="name">keywords</xsl:attribute>
        <xsl:attribute name="value">
          <xsl:for-each select="//keyword">
            <xsl:value-of select="normalize-space(.)"/>
            <xsl:if test="position() != last()">
              <xsl:text>, </xsl:text>
            </xsl:if>
          </xsl:for-each>
        </xsl:attribute>
      </xsl:element>
    </xsl:if>

    <xsl:if test="//subjectterm">
      <xsl:element name="axf:document-info">
        <xsl:attribute name="name">subject</xsl:attribute>
        <xsl:attribute name="value">
          <xsl:for-each select="//subjectterm">
            <xsl:value-of select="normalize-space(.)"/>
            <xsl:if test="position() != last()">
              <xsl:text>, </xsl:text>
            </xsl:if>
          </xsl:for-each>
        </xsl:attribute>
      </xsl:element>
    </xsl:if>

</xsl:template>

<!-- These properties are added to fo:simple-page-master -->
<xsl:template name="axf-page-master-properties">
  <xsl:param name="page.master" select="''"/>

  <xsl:if test="$crop.marks != 0">
    <xsl:attribute name="axf:printer-marks">crop</xsl:attribute>
    <xsl:attribute name="axf:bleed"><xsl:value-of
                          select="$crop.mark.bleed"/></xsl:attribute>
    <xsl:attribute name="axf:printer-marks-line-width"><xsl:value-of
                          select="$crop.mark.width"/></xsl:attribute>
    <xsl:attribute name="axf:crop-offset"><xsl:value-of
                          select="$crop.mark.offset"/></xsl:attribute>
  </xsl:if>

  <xsl:call-template name="user-axf-page-master-properties">
    <xsl:with-param name="page.master" select="$page.master"/>
  </xsl:call-template>

</xsl:template>

<xsl:template name="user-axf-page-master-properties">
  <xsl:param name="page.master" select="''"/>
</xsl:template>

</xsl:stylesheet>
