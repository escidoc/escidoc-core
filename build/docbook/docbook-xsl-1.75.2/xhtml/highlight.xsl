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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xslthl="http://xslthl.sf.net" xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xslthl" version="1.0">

<!-- ********************************************************************
     $Id: highlight.xsl 8419 2009-04-29 20:37:52Z kosek $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://docbook.sf.net/release/xsl/current/ for
     and other information.

     ******************************************************************** -->

<xsl:import href="../highlighting/common.xsl"/>

<xsl:template match="xslthl:keyword" mode="xslthl">
  <b class="hl-keyword"><xsl:apply-templates mode="xslthl"/></b>
</xsl:template>

<xsl:template match="xslthl:string" mode="xslthl">
  <b class="hl-string"><i style="color:red"><xsl:apply-templates mode="xslthl"/></i></b>
</xsl:template>

<xsl:template match="xslthl:comment" mode="xslthl">
  <i class="hl-comment" style="color: silver"><xsl:apply-templates mode="xslthl"/></i>
</xsl:template>

<xsl:template match="xslthl:directive" mode="xslthl">
  <span class="hl-directive" style="color: maroon"><xsl:apply-templates mode="xslthl"/></span>
</xsl:template>

<xsl:template match="xslthl:tag" mode="xslthl">
  <b class="hl-tag" style="color: #000096"><xsl:apply-templates mode="xslthl"/></b>
</xsl:template>

<xsl:template match="xslthl:attribute" mode="xslthl">
  <span class="hl-attribute" style="color: #F5844C"><xsl:apply-templates mode="xslthl"/></span>
</xsl:template>

<xsl:template match="xslthl:value" mode="xslthl">
  <span class="hl-value" style="color: #993300"><xsl:apply-templates mode="xslthl"/></span>
</xsl:template>

<xsl:template match="xslthl:html" mode="xslthl">
  <b><i style="color: red"><xsl:apply-templates mode="xslthl"/></i></b>
</xsl:template>

<xsl:template match="xslthl:xslt" mode="xslthl">
  <b style="color: #0066FF"><xsl:apply-templates mode="xslthl"/></b>
</xsl:template>

<!-- Not emitted since XSLTHL 2.0 -->
<xsl:template match="xslthl:section" mode="xslthl">
  <b><xsl:apply-templates mode="xslthl"/></b>
</xsl:template>

<xsl:template match="xslthl:number" mode="xslthl">
  <span class="hl-number"><xsl:apply-templates mode="xslthl"/></span>
</xsl:template>

<xsl:template match="xslthl:annotation" mode="xslthl">
  <i><span class="hl-annotation" style="color: gray"><xsl:apply-templates mode="xslthl"/></span></i>
</xsl:template>

<!-- Not sure which element will be in final XSLTHL 2.0 -->
<xsl:template match="xslthl:doccomment|xslthl:doctype" mode="xslthl">
  <b class="hl-tag" style="color: blue"><xsl:apply-templates mode="xslthl"/></b>
</xsl:template>

</xsl:stylesheet>
