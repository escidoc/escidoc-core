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

<xsl:output method="html"/>

<xsl:template name="script-dir">
  <xsl:variable name="source.script.dir">
    <xsl:call-template name="dbhtml-attribute">
      <xsl:with-param name="pis" select="/processing-instruction('dbhtml')"/>
      <xsl:with-param name="attribute" select="'script-dir'"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="$source.script.dir != ''">
      <xsl:value-of select="$source.script.dir"/>
      <xsl:text>/</xsl:text>
    </xsl:when>
    <xsl:when test="$script.dir != ''">
      <xsl:value-of select="$script.dir"/>
      <xsl:text>/</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>http://docbook.sourceforge.net/release/slides/browser/</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="script-file">
  <xsl:param name="js" select="'slides.js'"/>
  <xsl:call-template name="script-dir"/>
  <xsl:value-of select="$js"/>
</xsl:template>

<xsl:template name="ua.js">
  <!-- Danger Will Robinson: template shadows parameter -->
  <xsl:param name="language" select="'javascript'"/>
  <script type="text/javascript" language="{$language}">
    <xsl:attribute name="src">
      <xsl:call-template name="script-file">
        <xsl:with-param name="js" select="$ua.js"/>
      </xsl:call-template>
    </xsl:attribute>
    <xsl:text> </xsl:text>
  </script>
</xsl:template>

<xsl:template name="xbDOM.js">
  <!-- Danger Will Robinson: template shadows parameter -->
  <xsl:param name="language" select="'javascript'"/>
  <script type="text/javascript" language="{$language}">
    <xsl:attribute name="src">
      <xsl:call-template name="script-file">
        <xsl:with-param name="js" select="$xbDOM.js"/>
      </xsl:call-template>
    </xsl:attribute>
    <xsl:text> </xsl:text>
  </script>
</xsl:template>

<xsl:template name="xbStyle.js">
  <!-- Danger Will Robinson: template shadows parameter -->
  <xsl:param name="language" select="'javascript'"/>
  <script type="text/javascript" language="{$language}">
    <xsl:attribute name="src">
      <xsl:call-template name="script-file">
        <xsl:with-param name="js" select="$xbStyle.js"/>
      </xsl:call-template>
    </xsl:attribute>
    <xsl:text> </xsl:text>
  </script>
</xsl:template>

<xsl:template name="xbLibrary.js">
  <!-- Danger Will Robinson: template shadows parameter -->
  <xsl:param name="language" select="'javascript'"/>
  <script type="text/javascript" language="{$language}">
    <xsl:attribute name="src">
      <xsl:call-template name="script-file">
        <xsl:with-param name="js" select="$xbLibrary.js"/>
      </xsl:call-template>
    </xsl:attribute>
    <xsl:text> </xsl:text>
  </script>
</xsl:template>

<xsl:template name="xbCollapsibleLists.js">
  <!-- Danger Will Robinson: template shadows parameter -->
  <xsl:param name="language" select="'javascript'"/>
  <script type="text/javascript" language="{$language}">
    <xsl:attribute name="src">
      <xsl:call-template name="script-file">
        <xsl:with-param name="js" select="$xbCollapsibleLists.js"/>
      </xsl:call-template>
    </xsl:attribute>
    <xsl:text> </xsl:text>
  </script>
</xsl:template>

<xsl:template name="overlay.js">
  <!-- Danger Will Robinson: template shadows parameter -->
  <xsl:param name="language" select="'javascript'"/>
  <script type="text/javascript" language="{$language}">
    <xsl:attribute name="src">
      <xsl:call-template name="script-file">
        <xsl:with-param name="js" select="$overlay.js"/>
      </xsl:call-template>
    </xsl:attribute>
    <xsl:text> </xsl:text>
  </script>
</xsl:template>

<xsl:template name="slides.js">
  <!-- Danger Will Robinson: template shadows parameter -->
  <xsl:param name="language" select="'javascript'"/>
  <script type="text/javascript" language="{$language}">
    <xsl:attribute name="src">
      <xsl:call-template name="script-file">
        <xsl:with-param name="js" select="$slides.js"/>
      </xsl:call-template>
    </xsl:attribute>
    <xsl:text> </xsl:text>
  </script>
</xsl:template>

</xsl:stylesheet>
