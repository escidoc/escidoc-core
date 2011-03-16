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
                version="1.0">

<xsl:import href="slides-common.xsl"/>

<xsl:template match="/">
  <html>
    <head>
      <title><xsl:value-of select="/slides/slidesinfo/title"/></title>
    </head>
    <body>
      <xsl:apply-templates/>
    </body>
  </html>
</xsl:template>

<xsl:template match="slidesinfo">
  <xsl:variable name="id">
    <xsl:call-template name="object.id"/>
  </xsl:variable>

  <div id="{$id}" class="titlepage">
    <div class="titlepage-body">
      <xsl:call-template name="titlepage-body"/>
    </div>
  </div>
</xsl:template>

<xsl:template match="slides" mode="toc">
  <!-- nop -->
</xsl:template>

<xsl:template match="foil">
  <xsl:variable name="id">
    <xsl:call-template name="object.id"/>
  </xsl:variable>

  <div class="{name(.)}" id="{$id}">
    <div class="foil-body">
      <xsl:call-template name="foil-body"/>
    </div>
    <xsl:call-template name="process.footnotes"/>
  </div>
</xsl:template>

<xsl:template match="foilgroup">
  <xsl:variable name="id">
    <xsl:call-template name="object.id"/>
  </xsl:variable>

  <div class="{name(.)}" id="{$id}">
    <div class="foilgroup-body">
      <xsl:call-template name="foilgroup-body"/>
    </div>
    <xsl:call-template name="process.footnotes"/>
  </div>

  <xsl:apply-templates select="foil"/>
</xsl:template>

<xsl:template match="author" mode="titlepage.mode">
  <div class="{name(.)}">
    <h2 class="{name(.)}"><xsl:call-template name="person.name"/></h2>
    <xsl:apply-templates mode="titlepage.mode" select="./contrib"/>
    <xsl:apply-templates mode="titlepage.mode" select="./affiliation"/>
  </div>
</xsl:template>

</xsl:stylesheet>
