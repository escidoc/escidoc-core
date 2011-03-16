<?xml version="1.0" encoding="ASCII"?>
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

<!--This file was created automatically by html2xhtml-->
<!--from the HTML stylesheets.-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:doc="http://nwalsh.com/xsl/documentation/1.0" xmlns="http://www.w3.org/1999/xhtml" version="1.0" exclude-result-prefixes="doc">

<!-- ********************************************************************
     $Id: maketoc.xsl 6910 2007-06-28 23:23:30Z xmldoc $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://docbook.sf.net/release/xsl/current/ for
     copyright and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->

<xsl:import href="docbook.xsl"/>
<xsl:import href="chunk.xsl"/>

<xsl:output method="xml" indent="no" encoding="UTF-8" doctype-public="-//W3C//DTD XHTML 1.1//EN" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"/>

<xsl:param name="toc.list.type" select="'tocentry'"/>

<!-- refentry in autotoc.xsl does not use subtoc, so must
     handle it explicitly here. -->
<xsl:template match="refentry" mode="toc">
  <xsl:param name="toc-context" select="."/>

  <xsl:call-template name="subtoc">
    <xsl:with-param name="toc-context" select="$toc-context"/>
  </xsl:call-template>
</xsl:template>


<xsl:template name="subtoc">
  <xsl:param name="nodes" select="NOT-AN-ELEMENT"/>
  <xsl:variable name="filename">
    <xsl:apply-templates select="." mode="chunk-filename"/>
  </xsl:variable>

  <xsl:variable name="chunk">
    <xsl:call-template name="chunk"/>
  </xsl:variable>

  <xsl:if test="$chunk != 0">
    <xsl:call-template name="indent-spaces"/>
    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>
    <tocentry linkend="{$id}">
      <xsl:processing-instruction name="dbhtml">
        <xsl:text>filename="</xsl:text>
        <xsl:value-of select="$filename"/>
        <xsl:text>"</xsl:text>
      </xsl:processing-instruction>
      <xsl:text>
</xsl:text>
      <xsl:apply-templates mode="toc" select="$nodes"/>
      <xsl:call-template name="indent-spaces"/>
    </tocentry>
    <xsl:text>
</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template name="indent-spaces">
  <xsl:param name="node" select="."/>
  <xsl:text>  </xsl:text>
  <xsl:if test="$node/parent::*">
    <xsl:call-template name="indent-spaces">
      <xsl:with-param name="node" select="$node/parent::*"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template match="/" priority="-1">
  <xsl:text>
</xsl:text>
  <toc role="chunk-toc">
    <xsl:text>
</xsl:text>
    <xsl:apply-templates select="/" mode="toc"/>
  </toc>
  <xsl:text>
</xsl:text>
</xsl:template>

</xsl:stylesheet>
