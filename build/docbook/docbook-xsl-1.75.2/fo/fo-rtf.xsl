<?xml version="1.0"?>
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
                xmlns:exsl="http://exslt.org/common"
                xmlns:set="http://exslt.org/sets"
                exclude-result-prefixes="exsl set"
                version="1.0">

<!-- ********************************************************************
     $Id: fo-rtf.xsl 6910 2007-06-28 23:23:30Z xmldoc $
     ********************************************************************

     This file is part of the DocBook XSL Stylesheet distribution.
     See ../README or http://docbook.sf.net/ for copyright
     copyright and other information.

     ******************************************************************** -->

<!-- This module contains templates that match against FO nodes. It is used
     to post-process result tree fragments for some sorts of cleanup.
     These templates can only ever be fired by a processor that supports
     exslt:node-set(). -->

<!-- ==================================================================== -->

<!-- insert.fo.fnum mode templates insert a particular RTF at the beginning
     of the first paragraph in the primary RTF. In fact, they are inserting
     a footnote-number, so we tinker a few other things too, like spacing and
     font-sizes. -->

<xsl:template match="/" mode="insert.fo.fnum">
  <xsl:param name="mark" select="'?'"/>
  <xsl:apply-templates mode="insert.fo.fnum">
    <xsl:with-param name="mark" select="$mark"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="*" mode="insert.fo.fnum">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="insert.fo.fnum">
      <xsl:with-param name="mark" select="$mark"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

<xsl:template match="fo:block" mode="insert.fo.fnum">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy>
    <xsl:for-each select="@*">
      <xsl:choose>
        <xsl:when test="starts-with(name(.), 'space-before')"/>
        <xsl:when test="starts-with(name(.), 'space-after')"/>
        <xsl:when test="starts-with(name(.), 'font-size')"/>
        <xsl:otherwise>
          <xsl:copy-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <xsl:if test="not(preceding::fo:block)">
      <xsl:copy-of select="$mark"/>
    </xsl:if>
    <xsl:apply-templates mode="insert.fo.fnum">
      <xsl:with-param name="mark" select="$mark"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

<xsl:template match="text()|processing-instruction()|comment()" mode="insert.fo.fnum">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy/>
</xsl:template>

<!-- ==================================================================== -->

<!-- insert.fo.block mode templates insert a particular RTF at the beginning
     of the first paragraph in the primary RTF. -->

<xsl:template match="/" mode="insert.fo.block">
  <xsl:param name="mark" select="'?'"/>
  <xsl:apply-templates mode="insert.fo.block">
    <xsl:with-param name="mark" select="$mark"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="*" mode="insert.fo.block">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="insert.fo.block">
      <xsl:with-param name="mark" select="$mark"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

<xsl:template match="fo:block" mode="insert.fo.block">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:if test="not(preceding::fo:block)">
      <xsl:copy-of select="$mark"/>
    </xsl:if>
    <xsl:apply-templates mode="insert.fo.block">
      <xsl:with-param name="mark" select="$mark"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

<xsl:template match="text()|processing-instruction()|comment()" mode="insert.fo.block">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy/>
</xsl:template>

<!-- ==================================================================== -->

<!-- insert.fo.text mode templates insert a particular RTF at the beginning
     of the first text-node in the primary RTF. -->

<xsl:template match="/" mode="insert.fo.text">
  <xsl:param name="mark" select="'?'"/>
  <xsl:apply-templates mode="insert.fo.text">
    <xsl:with-param name="mark" select="$mark"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="*" mode="insert.fo.text">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="insert.fo.text">
      <xsl:with-param name="mark" select="$mark"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

<xsl:template match="text()|processing-instruction()|comment()" mode="insert.fo.text">
  <xsl:param name="mark" select="'?'"/>

  <xsl:if test="not(preceding::text())">
    <xsl:copy-of select="$mark"/>
  </xsl:if>

  <xsl:copy/>
</xsl:template>

<xsl:template match="processing-instruction()|comment()" mode="insert.fo.text">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy/>
</xsl:template>

<!-- ==================================================================== -->

</xsl:stylesheet>
