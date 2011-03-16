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

<!-- ====================================================================== -->

<xsl:template name="css-file">
  <xsl:param name="css" select="'slides.css'"/>

  <xsl:variable name="source.css.dir">
    <xsl:call-template name="dbhtml-attribute">
      <xsl:with-param name="pis" select="/processing-instruction('dbhtml')"/>
      <xsl:with-param name="attribute" select="'css-stylesheet-dir'"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="$source.css.dir != ''">
      <xsl:value-of select="$source.css.dir"/>
      <xsl:text>/</xsl:text>
    </xsl:when>
    <xsl:when test="$css.stylesheet.dir != ''">
      <xsl:value-of select="$css.stylesheet.dir"/>
      <xsl:text>/</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>http://docbook.sourceforge.net/release/slides/browser/</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:value-of select="$css"/>
</xsl:template>

<!-- ====================================================================== -->
<!-- active navigation images -->

<xsl:template name="css.stylesheet">
  <xsl:param name="css" select="$css.stylesheet"/>
  <!-- Danger Will Robinson: template shadows parameter -->
  <xsl:call-template name="css-file">
    <xsl:with-param name="css" select="$css"/>
  </xsl:call-template>
</xsl:template>

<!-- ====================================================================== -->

</xsl:stylesheet>
