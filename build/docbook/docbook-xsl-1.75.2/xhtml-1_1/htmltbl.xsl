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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">

<!-- ********************************************************************
     $Id: htmltbl.xsl 8477 2009-07-13 11:38:55Z nwalsh $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://docbook.sf.net/release/xsl/current/ for
     copyright and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->

<xsl:template match="colgroup" mode="htmlTable">
  <xsl:element name="{local-name()}" namespace="http://www.w3.org/1999/xhtml">
    <xsl:apply-templates select="@*" mode="htmlTableAtt"/>
    <xsl:apply-templates mode="htmlTable"/>
  </xsl:element>
</xsl:template>

<xsl:template match="col" mode="htmlTable">
  <xsl:element name="{local-name()}" namespace="http://www.w3.org/1999/xhtml">
    <xsl:apply-templates select="@*" mode="htmlTableAtt"/>
  </xsl:element>
</xsl:template>

<xsl:template match="caption" mode="htmlTable">
  <!-- do not use xsl:copy because of XHTML's needs -->
  <caption>  
    <xsl:apply-templates select="@*" mode="htmlTableAtt"/>

    <xsl:apply-templates select=".." mode="object.title.markup">
      <xsl:with-param name="allow-anchors" select="1"/>
    </xsl:apply-templates>

  </caption>
</xsl:template>

<xsl:template match="tbody|thead|tfoot|tr" mode="htmlTable">
  <xsl:element name="{local-name(.)}" namespace="http://www.w3.org/1999/xhtml">
    <xsl:apply-templates select="@*" mode="htmlTableAtt"/>
    <xsl:apply-templates mode="htmlTable"/>
  </xsl:element>
</xsl:template>

<xsl:template match="th|td" mode="htmlTable">
  <xsl:element name="{local-name(.)}" namespace="http://www.w3.org/1999/xhtml">
    <xsl:apply-templates select="@*" mode="htmlTableAtt"/>
    <xsl:apply-templates/> <!-- *not* mode=htmlTable -->
  </xsl:element>
</xsl:template>

<!-- don't copy through DocBook-specific attributes on HTML table markup -->
<!-- default behavior is to not copy through because there are more
     DocBook attributes than HTML attributes -->
<xsl:template mode="htmlTableAtt" match="@*"/>

<!-- copy these through -->
<xsl:template mode="htmlTableAtt" match="@abbr                    | @align                    | @axis                    | @bgcolor                    | @border                    | @cellpadding                    | @cellspacing                    | @char                    | @charoff                    | @class                    | @dir                    | @frame                    | @headers                    | @height                    | @lang                    | @nowrap                    | @onclick                    | @ondblclick                    | @onkeydown                    | @onkeypress                    | @onkeyup                    | @onmousedown                    | @onmousemove                    | @onmouseout                    | @onmouseover                    | @onmouseup                    | @rules                    | @style                    | @summary                    | @title                    | @valign                    | @valign                    | @width                    | @xml:lang">
  <xsl:copy-of select="."/>
</xsl:template>

<xsl:template match="@span|@rowspan|@colspan" mode="htmlTableAtt">
  <!-- No need to copy through the DTD's default value "1" of the attribute -->
  <xsl:if test="number(.) != 1">
    <xsl:attribute name="{local-name(.)}">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<!-- map floatstyle to HTML float values -->
<xsl:template match="@floatstyle" mode="htmlTableAtt">
  <xsl:attribute name="style">
    <xsl:text>float: </xsl:text>
    <xsl:choose>
      <xsl:when test="contains(., 'left')">left</xsl:when>
      <xsl:when test="contains(., 'right')">right</xsl:when>
      <xsl:when test="contains(., 'start')">
        <xsl:value-of select="$direction.align.start"/>
      </xsl:when>
      <xsl:when test="contains(., 'end')">
        <xsl:value-of select="$direction.align.end"/>
      </xsl:when>
      <xsl:when test="contains(., 'inside')">
        <xsl:value-of select="$direction.align.start"/>
      </xsl:when>
      <xsl:when test="contains(., 'outside')">
        <xsl:value-of select="$direction.align.end"/>
      </xsl:when>
      <xsl:when test="contains(., 'before')">none</xsl:when>
      <xsl:when test="contains(., 'none')">none</xsl:when>
    </xsl:choose>
    <xsl:text>;</xsl:text>
  </xsl:attribute>
</xsl:template>

</xsl:stylesheet>
