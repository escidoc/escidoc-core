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
     $Id: param.xweb 7076 2007-07-18 16:20:33Z xmldoc $
     ********************************************************************

     This file is part of the DocBook XSL Stylesheets distribution.
     See ../README or http://docbook.sf.net/release/xsl/current/ for
     copyright and other information.

     ******************************************************************** -->

<xsl:param name="autolayout-file">autolayout.xml</xsl:param>
<xsl:param name="header.hr" select="1"/>
<xsl:param name="footer.hr" select="1"/>
<xsl:param name="feedback.href"/>
<xsl:param name="feedback.with.ids" select="0"/>
<xsl:param name="feedback.link.text">Feedback</xsl:param>
<xsl:param name="filename-prefix"/>
<xsl:param name="output-root">.</xsl:param>
    <xsl:param name="dry-run" select="0"/>
  
<xsl:param name="rebuild-all" select="0"/>
<xsl:param name="nav.table.summary">Navigation</xsl:param>
<xsl:param name="navtocwidth">220</xsl:param>
<xsl:param name="navbodywidth"/>
<xsl:param name="textbgcolor">white</xsl:param>
<xsl:param name="navbgcolor">#4080FF</xsl:param>
<xsl:param name="toc.spacer.graphic" select="1"/>
<xsl:param name="toc.spacer.text">&#160;&#160;&#160;</xsl:param>
<xsl:param name="toc.spacer.image">graphics/blank.gif</xsl:param>
<xsl:param name="toc.pointer.graphic" select="1"/>
<xsl:param name="toc.pointer.text">&#160;&gt;&#160;</xsl:param>
<xsl:param name="toc.pointer.image">graphics/arrow.gif</xsl:param>
<xsl:param name="toc.blank.graphic" select="1"/>
<xsl:param name="toc.blank.text">&#160;&#160;&#160;</xsl:param>
<xsl:param name="toc.blank.image">graphics/blank.gif</xsl:param>
<xsl:param name="suppress.homepage.title" select="1"/>
<xsl:attribute-set name="body.attributes">
  <xsl:attribute name="bgcolor">white</xsl:attribute>
  <xsl:attribute name="text">black</xsl:attribute>
  <xsl:attribute name="link">#0000FF</xsl:attribute>
  <xsl:attribute name="vlink">#840084</xsl:attribute>
  <xsl:attribute name="alink">#0000FF</xsl:attribute>
</xsl:attribute-set>
<xsl:param name="sequential.links" select="0"/>
<xsl:param name="currentpage.marker">@</xsl:param>
<xsl:param name="banner.before.navigation" select="1"/>
<xsl:param name="table.spacer.image">graphics/spacer.gif</xsl:param>

</xsl:stylesheet>
