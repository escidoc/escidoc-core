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
                xmlns:xi="http://www.w3.org/2001/XInclude"
                version='1.0'>

<!-- ********************************************************************
     $Id: insertfile.xsl 5262 2005-10-12 14:58:42Z xmldoc $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://docbook.sf.net/release/xsl/current/ for
     copyright and other information.

     ******************************************************************** -->

<xsl:param name="textdata.default.encoding"></xsl:param>

<!-- * This stylesheet makes a copy of a source tree, replacing all -->
<!-- * instances of the following with corresponding Xinclude instances -->
<!-- * in the result tree. -->
<!-- * -->
<!-- *   <textobject><textdata fileref="foo.txt"> -->
<!-- *   <imagedata format="linespecific" fileref="foo.txt"> -->
<!-- *   <inlinegraphic format="linespecific" fileref="foo.txt"> -->
<!-- * -->
<!-- * Those become: -->
<!-- * -->
<!-- *   <xi:include href="foo.txt" parse="text"/> -->
<!-- * -->
<!-- * It also works as expected with entityref in place of fileref, -->
<!-- * and copies over the value of the <textdata>“encoding” atrribute (if -->
<!-- * found). It is basically intended as an alternative to using the -->
<!-- * DocBook XSLT Java insertfile() extension. -->

<!-- ==================================================================== -->

<xsl:template name="get.external.filename">
  <xsl:choose>
    <xsl:when test="@entityref">
      <xsl:value-of select="unparsed-entity-uri(@entityref)"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="@fileref"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template match="textobject[child::textdata[@entityref|@fileref]]">
  <xsl:apply-templates select="textdata"/>
</xsl:template>

<xsl:template match="textdata[@entityref|@fileref]">
  <xsl:variable name="filename">
    <xsl:call-template name="get.external.filename"/>
  </xsl:variable>
  <xsl:variable name="encoding">
    <xsl:choose>
      <xsl:when test="@encoding">
        <xsl:value-of select="@encoding"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$textdata.default.encoding"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xi:include href="{$filename}" parse="text" encoding="{$encoding}"/>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template
    match="inlinemediaobject
           [child::imageobject
           [child::imagedata
           [@format = 'linespecific' and
           (@entityref|@fileref)]]]">
  <xsl:apply-templates select="imageobject/imagedata"/>
</xsl:template>

<xsl:template match="imagedata
                     [@format = 'linespecific' and
                     (@entityref|@fileref)]">
  <xsl:variable name="filename">
    <xsl:call-template name="get.external.filename"/>
  </xsl:variable>
  <xi:include href="{$filename}" parse="text" encoding="{$textdata.default.encoding}"/>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template match="inlinegraphic
                     [@format = 'linespecific' and
                     (@entityref|@fileref)]">
  <xsl:variable name="filename">
    <xsl:call-template name="get.external.filename"/>
  </xsl:variable>
  <xi:include href="{$filename}" parse="text" encoding="{$textdata.default.encoding}"/>
</xsl:template>

<!-- ==================================================================== -->

<!-- * copy everything else into result tree as-is -->
<xsl:template match="node() | @*">
  <xsl:copy>
    <xsl:apply-templates select="@* | node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
