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
                xmlns:exsl="http://exslt.org/common"
                xmlns:set="http://exslt.org/sets"
                exclude-result-prefixes="exsl set"
                version="1.0">

<!-- ********************************************************************
     $Id: html-rtf.xsl 8345 2009-03-16 06:44:07Z bobstayton $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://docbook.sf.net/release/xsl/current/ for
     copyright and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->

<!-- This module contains templates that match against HTML nodes. It is used
     to post-process result tree fragments for some sorts of cleanup.
     These templates can only ever be fired by a processor that supports
     exslt:node-set(). -->

<!-- ==================================================================== -->

<!-- insert.html.p mode templates insert a particular RTF at the beginning
     of the first paragraph in the primary RTF. -->

<xsl:template match="/" mode="insert.html.p">
  <xsl:param name="mark" select="'?'"/>
  <xsl:apply-templates mode="insert.html.p">
    <xsl:with-param name="mark" select="$mark"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="*" mode="insert.html.p">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="insert.html.p">
      <xsl:with-param name="mark" select="$mark"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

<xsl:template xmlns:html="http://www.w3.org/1999/xhtml"
              match="html:p|p" mode="insert.html.p">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:if test="not(preceding::p|preceding::html:p)">
      <xsl:copy-of select="$mark"/>
    </xsl:if>
    <xsl:apply-templates mode="insert.html.p">
      <xsl:with-param name="mark" select="$mark"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

<xsl:template match="text()|processing-instruction()|comment()" mode="insert.html.p">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy/>
</xsl:template>

<!-- ==================================================================== -->

<!-- insert.html.text mode templates insert a particular RTF at the beginning
     of the first text-node in the primary RTF. -->

<xsl:template match="/" mode="insert.html.text">
  <xsl:param name="mark" select="'?'"/>
  <xsl:apply-templates mode="insert.html.text">
    <xsl:with-param name="mark" select="$mark"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="*" mode="insert.html.text">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="insert.html.text">
      <xsl:with-param name="mark" select="$mark"/>
    </xsl:apply-templates>
  </xsl:copy>
</xsl:template>

<xsl:template match="text()|processing-instruction()|comment()" mode="insert.html.text">
  <xsl:param name="mark" select="'?'"/>

  <xsl:if test="not(preceding::text())">
    <xsl:copy-of select="$mark"/>
  </xsl:if>

  <xsl:copy/>
</xsl:template>

<xsl:template match="processing-instruction()|comment()" mode="insert.html.text">
  <xsl:param name="mark" select="'?'"/>
  <xsl:copy/>
</xsl:template>

<!-- ==================================================================== -->

<!-- unwrap.p mode templates remove blocks from HTML p elements (and
     other places where blocks aren't allowed) -->

<xsl:template name="unwrap.p">
  <xsl:param name="p"/>
  <xsl:choose>
    <xsl:when test="$exsl.node.set.available != 0
                    and function-available('set:leading')
                    and function-available('set:trailing')">
      <xsl:apply-templates select="exsl:node-set($p)" mode="unwrap.p"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy-of select="$p"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template xmlns:html="http://www.w3.org/1999/xhtml"
              match="html:p|p" mode="unwrap.p">
  <!-- xmlns:html is necessary for the xhtml stylesheet case -->
  <xsl:variable name="blocks" xmlns:html="http://www.w3.org/1999/xhtml"
                select="address|blockquote|div|hr|h1|h2|h3|h4|h5|h6
                        |layer|p|pre|table|dl|menu|ol|ul|form
                        |html:address|html:blockquote|html:div|html:hr
                        |html:h1|html:h2|html:h3|html:h4|html:h5|html:h6
                        |html:layer|html:p|html:pre|html:table|html:dl
                        |html:menu|html:ol|html:ul|html:form"/>
  <xsl:choose>
    <xsl:when test="$blocks">
      <xsl:call-template name="unwrap.p.nodes">
        <xsl:with-param name="wrap" select="."/>
        <xsl:with-param name="first" select="1"/>
        <xsl:with-param name="nodes" select="node()"/>
        <xsl:with-param name="blocks" select="$blocks"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates mode="unwrap.p"/>
      </xsl:copy>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="*" mode="unwrap.p">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="unwrap.p"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="text()|processing-instruction()|comment()" mode="unwrap.p">
  <xsl:copy/>
</xsl:template>

<xsl:template name="unwrap.p.nodes">
  <xsl:param name="wrap" select="."/>
  <xsl:param name="first" select="0"/>
  <xsl:param name="nodes"/>
  <xsl:param name="blocks"/>
  <xsl:variable name="block" select="$blocks[1]"/>

  <!-- This template should never get called if these functions aren't available -->
  <!-- but this test is still necessary so that processors don't choke on the -->
  <!-- function calls if they don't support the set: functions -->
  <xsl:if test="function-available('set:leading')
                and function-available('set:trailing')">
    <xsl:choose>
      <xsl:when test="$blocks">
        <xsl:variable name="leading" select="set:leading($nodes,$block)"/>
        <xsl:variable name="trailing" select="set:trailing($nodes,$block)"/>

        <xsl:if test="(($wrap/@id or $wrap/@xml:id) 
                        and $first = 1) or $leading">
          <xsl:element name="{local-name($wrap)}" namespace="{namespace-uri($wrap)}">
            <xsl:for-each select="$wrap/@*">
              <xsl:if test="$first != 0 or local-name(.) != 'id'">
                <xsl:copy/>
              </xsl:if>
            </xsl:for-each>
            <xsl:apply-templates select="$leading" mode="unwrap.p"/>
          </xsl:element>
        </xsl:if>

        <xsl:apply-templates select="$block" mode="unwrap.p"/>

        <xsl:if test="$trailing">
          <xsl:call-template name="unwrap.p.nodes">
            <xsl:with-param name="wrap" select="$wrap"/>
            <xsl:with-param name="nodes" select="$trailing"/>
            <xsl:with-param name="blocks" select="$blocks[position() &gt; 1]"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:when>

      <xsl:otherwise>
        <xsl:if test="(($wrap/@id or $wrap/@xml:id) and $first = 1) or $nodes">
          <xsl:element name="{local-name($wrap)}" namespace="{namespace-uri($wrap)}">
            <xsl:for-each select="$wrap/@*">
              <xsl:if test="$first != 0 or local-name(.) != 'id'">
                <xsl:copy/>
              </xsl:if>
            </xsl:for-each>
            <xsl:apply-templates select="$nodes" mode="unwrap.p"/>
          </xsl:element>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
</xsl:template>

<!-- ==================================================================== -->
<!-- make.verbatim.mode replaces spaces and newlines -->

<xsl:template match="/" mode="make.verbatim.mode">
  <xsl:apply-templates mode="make.verbatim.mode"/>
</xsl:template>

<xsl:template match="*" mode="make.verbatim.mode">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="make.verbatim.mode"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="processing-instruction()|comment()" mode="make.verbatim.mode">
  <xsl:copy/>
</xsl:template>

<xsl:template match="text()" mode="make.verbatim.mode">
  <xsl:variable name="text" select="translate(., ' ', '&#160;')"/>

  <xsl:choose>
    <xsl:when test="not(contains($text, '&#xA;'))">
      <xsl:value-of select="$text"/>
    </xsl:when>

    <xsl:otherwise>
      <xsl:variable name="len" select="string-length($text)"/>

      <xsl:choose>
        <xsl:when test="$len = 1">
          <br/><xsl:text>&#xA;</xsl:text>
        </xsl:when>

        <xsl:otherwise>
          <xsl:variable name="half" select="$len div 2"/>
          <xsl:call-template name="make-verbatim-recursive">
            <xsl:with-param name="text" select="substring($text, 1, $half)"/>
          </xsl:call-template>
          <xsl:call-template name="make-verbatim-recursive">
            <xsl:with-param name="text"
                            select="substring($text, ($half + 1), $len)"/>
          </xsl:call-template>
    	</xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="make-verbatim-recursive">
  <xsl:param name="text" select="''"/>

  <xsl:choose>
    <xsl:when test="not(contains($text, '&#xA;'))">
      <xsl:value-of select="$text"/>
    </xsl:when>

    <xsl:otherwise>
      <xsl:variable name="len" select="string-length($text)"/>

      <xsl:choose>
        <xsl:when test="$len = 1">
          <br/><xsl:text>&#xA;</xsl:text>
        </xsl:when>

        <xsl:otherwise>
    	  <xsl:variable name="half" select="$len div 2"/>
          <xsl:call-template name="make-verbatim-recursive">
    	    <xsl:with-param name="text" select="substring($text, 1, $half)"/>
    	  </xsl:call-template>
    	  <xsl:call-template name="make-verbatim-recursive">
    	    <xsl:with-param name="text"
    			    select="substring($text, ($half + 1), $len)"/>
    	  </xsl:call-template>
    	</xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ==================================================================== -->

<!-- remove.empty.div mode templates remove empty blocks -->

<xsl:template name="remove.empty.div">
  <xsl:param name="div"/>
  <xsl:choose>
    <xsl:when test="$exsl.node.set.available != 0">
      <xsl:apply-templates select="exsl:node-set($div)" mode="remove.empty.div"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy-of select="$div"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template xmlns:html="http://www.w3.org/1999/xhtml"
              match="html:p|p|html:div|div" mode="remove.empty.div">
  <xsl:if test="node()">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates mode="remove.empty.div"/>
    </xsl:copy>
  </xsl:if>
</xsl:template>

<xsl:template match="*" mode="remove.empty.div">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="remove.empty.div"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="text()|processing-instruction()|comment()" mode="remove.empty.div">
  <xsl:copy/>
</xsl:template>

<!-- ==================================================================== -->

</xsl:stylesheet>
