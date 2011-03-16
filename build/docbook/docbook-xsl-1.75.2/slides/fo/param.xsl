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

<!-- This file is generated from param.xweb -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- ********************************************************************
     $Id: param.xweb 6633 2007-02-21 18:33:33Z xmldoc $
     ********************************************************************

     This file is part of the DocBook Slides Stylesheet distribution.
     See ../README or http://docbook.sf.net/release/xsl/current/ for
     copyright and other information.

     ******************************************************************** -->

<xsl:param name="slide.font.family">Helvetica</xsl:param>
<xsl:param name="slide.title.font.family">Helvetica</xsl:param>
<xsl:param name="foil.title.master">36</xsl:param>
<!-- Inconsistant use of point size? -->
    <xsl:param name="foil.title.size">
      <xsl:value-of select="$foil.title.master"/><xsl:text>pt</xsl:text>
    </xsl:param>
  
    <xsl:attribute-set name="foilgroup.properties">
      <xsl:attribute name="font-family">
        <xsl:value-of select="$slide.font.family"/>
      </xsl:attribute>
    </xsl:attribute-set>
  
    <xsl:attribute-set name="foil.properties">
      <xsl:attribute name="font-family">
        <xsl:value-of select="$slide.font.family"/>
      </xsl:attribute>
      <xsl:attribute name="margin-{$direction.align.start}">1in</xsl:attribute>
      <xsl:attribute name="margin-{$direction.align.end}">1in</xsl:attribute>
      <xsl:attribute name="font-size">
        <xsl:value-of select="$body.font.size"/>
      </xsl:attribute>
      <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>
  
    <xsl:attribute-set name="foil.subtitle.properties">
      <xsl:attribute name="font-family">
        <xsl:value-of select="$slide.title.font.family"/>
      </xsl:attribute>
      <xsl:attribute name="text-align">center</xsl:attribute>
      <xsl:attribute name="font-size">
        <xsl:value-of select="$foil.title.master * 0.8"/><xsl:text>pt</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="space-after">12pt</xsl:attribute>
    </xsl:attribute-set>
  
    <xsl:attribute-set name="running.foot.properties">
      <xsl:attribute name="font-family">
        <xsl:value-of select="$slide.font.family"/>
      </xsl:attribute>
      <xsl:attribute name="font-size">14pt</xsl:attribute>
      <xsl:attribute name="color">#9F9F9F</xsl:attribute>
    </xsl:attribute-set>
  
    <xsl:attribute-set name="speakernote.properties">
      <xsl:attribute name="font-family">Times Roman</xsl:attribute>
      <xsl:attribute name="font-style">italic</xsl:attribute>
      <xsl:attribute name="font-size">12pt</xsl:attribute>
      <xsl:attribute name="font-weight">normal</xsl:attribute>
    </xsl:attribute-set>
  
    <xsl:attribute-set name="slides.properties">
      <xsl:attribute name="font-family">
        <xsl:value-of select="$slide.font.family"/>
      </xsl:attribute>
    </xsl:attribute-set>
  

</xsl:stylesheet>
