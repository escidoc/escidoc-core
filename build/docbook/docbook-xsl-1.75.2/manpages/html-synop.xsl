<?xml version="1.0" encoding="UTF-8"?>
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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- ********************************************************************
     $Id: synop.xsl 8421 2009-05-04 07:49:49Z bobstayton $
     ********************************************************************

     This file is part of the XSL DocBook Stylesheet distribution.
     See ../README or http://docbook.sf.net/release/xsl/current/ for
     copyright and other information.

     ******************************************************************** -->

<!-- ==================================================================== -->

<!-- synopsis is in verbatim -->

<!-- ==================================================================== -->

<xsl:template match="cmdsynopsis">
  <div>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <p>
      <xsl:if test="..//processing-instruction('dbcmdlist')">
        <!-- * Placing a dbcmdlist PI as a child of a particular element -->
        <!-- * creates a hyperlinked list of all cmdsynopsis instances -->
        <!-- * that are descendants of that element; so for any -->
        <!-- * cmdsynopsis that is a descendant of an element containing -->
        <!-- * a dbcmdlist PI, we need to output an a@id instance so that -->
        <!-- * we will have something to link to -->
        <xsl:call-template name="anchor">
          <xsl:with-param name="conditional" select="0"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:apply-templates/>
    </p>
  </div>
</xsl:template>

<xsl:template match="cmdsynopsis/command">
  <xsl:text>
.
</xsl:text>
  <xsl:call-template name="inline.monoseq"/>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="cmdsynopsis/command[1]" priority="2">
  <xsl:call-template name="inline.monoseq"/>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="group|arg" name="group-or-arg">
  <xsl:variable name="choice" select="@choice"/>
  <xsl:variable name="rep" select="@rep"/>
  <xsl:variable name="sepchar">
    <xsl:choose>
      <xsl:when test="ancestor-or-self::*/@sepchar">
        <xsl:value-of select="ancestor-or-self::*/@sepchar"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:if test="preceding-sibling::*">
    <xsl:value-of select="$sepchar"/>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="$choice='plain'">
      <xsl:value-of select="$arg.choice.plain.open.str"/>
    </xsl:when>
    <xsl:when test="$choice='req'">
      <xsl:value-of select="$arg.choice.req.open.str"/>
    </xsl:when>
    <xsl:when test="$choice='opt'">
      <xsl:value-of select="$arg.choice.opt.open.str"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$arg.choice.def.open.str"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates/>
  <xsl:choose>
    <xsl:when test="$rep='repeat'">
      <xsl:value-of select="$arg.rep.repeat.str"/>
    </xsl:when>
    <xsl:when test="$rep='norepeat'">
      <xsl:value-of select="$arg.rep.norepeat.str"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$arg.rep.def.str"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:choose>
    <xsl:when test="$choice='plain'">
      <xsl:value-of select="$arg.choice.plain.close.str"/>
    </xsl:when>
    <xsl:when test="$choice='req'">
      <xsl:value-of select="$arg.choice.req.close.str"/>
    </xsl:when>
    <xsl:when test="$choice='opt'">
      <xsl:value-of select="$arg.choice.opt.close.str"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$arg.choice.def.close.str"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="group/arg">
  <xsl:variable name="choice" select="@choice"/>
  <xsl:variable name="rep" select="@rep"/>
  <xsl:if test="preceding-sibling::*">
    <xsl:value-of select="$arg.or.sep"/>
  </xsl:if>
  <xsl:call-template name="group-or-arg"/>
</xsl:template>

<xsl:template match="sbr">
  <xsl:text>
.
</xsl:text>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template match="synopfragmentref">
  <xsl:variable name="target" select="key('id',@linkend)"/>
  <xsl:variable name="snum">
    <xsl:apply-templates select="$target" mode="synopfragment.number"/>
  </xsl:variable>
  <i>
    <a href="#{@linkend}">
      <xsl:text>(</xsl:text>
      <xsl:value-of select="$snum"/>
      <xsl:text>)</xsl:text>
    </a>
    <xsl:text> </xsl:text>
    <xsl:apply-templates/>
  </i>
</xsl:template>

<xsl:template match="synopfragment" mode="synopfragment.number">
  <xsl:number format="1"/>
</xsl:template>

<xsl:template match="synopfragment">
  <xsl:variable name="snum">
    <xsl:apply-templates select="." mode="synopfragment.number"/>
  </xsl:variable>
  <!-- You can't introduce another <p> here, because you're 
       already in a <p> from cmdsynopsis-->
  <span>
    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>
    <a name="{$id}">
      <xsl:text>(</xsl:text>
      <xsl:value-of select="$snum"/>
      <xsl:text>)</xsl:text>
    </a>
    <xsl:text> </xsl:text>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="funcsynopsis">
  <xsl:if test="..//processing-instruction('dbfunclist')">
    <!-- * Placing a dbfunclist PI as a child of a particular element -->
    <!-- * creates a hyperlinked list of all funcsynopsis instances that -->
    <!-- * are descendants of that element; so for any funcsynopsis that is -->
    <!-- * a descendant of an element containing a dbfunclist PI, we need -->
    <!-- * to output an a@id instance so that we will have something to -->
    <!-- * link to -->
    <xsl:call-template name="anchor">
      <xsl:with-param name="conditional" select="0"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:call-template name="informal.object"/>
</xsl:template>

<xsl:template match="funcsynopsisinfo">
  <xsl:text>.sp
</xsl:text><xsl:text>.nf
</xsl:text><pre>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates/>
  </pre><xsl:text/><xsl:text>.fi
</xsl:text>
</xsl:template>

<!-- ====================================================================== -->
<!-- funcprototype -->
<!--

funcprototype ::= (funcdef,
                   (void|varargs|paramdef+))

funcdef       ::= (#PCDATA|type|replaceable|function)*

paramdef      ::= (#PCDATA|type|replaceable|parameter|funcparams)*
-->

<xsl:template match="funcprototype">
  <xsl:variable name="html-style">
    <xsl:call-template name="pi.dbhtml_funcsynopsis-style">
      <xsl:with-param name="node" select="ancestor::funcsynopsis/descendant-or-self::*"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="style">
    <xsl:choose>
      <xsl:when test="$html-style != ''">
        <xsl:value-of select="$html-style"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$funcsynopsis.style"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

<!-- * 2008-02-17. the code no longer relies on the funcsynopsis.tabular.threshold -->
<!-- * param at all (the stuff below has been commented out since mid -->
<!-- * 2006), so I completely removed the funcsynopsis.tabular.threshold param -->
<!-- * .. MikeSmith -->
<!--
  <xsl:variable name="tabular-p"
                select="$funcsynopsis.tabular.threshold &gt; 0
                        and string-length(.) &gt; $funcsynopsis.tabular.threshold"/>
-->

  <xsl:variable name="tabular-p" select="true()"/>

  <xsl:choose>
    <xsl:when test="$style = 'kr' and $tabular-p">
      <xsl:apply-templates select="." mode="kr-tabular"/>
    </xsl:when>
    <xsl:when test="$style = 'kr'">
      <xsl:apply-templates select="." mode="kr-nontabular"/>
    </xsl:when>
    <xsl:when test="$style = 'ansi' and $tabular-p">
      <xsl:apply-templates select="." mode="ansi-tabular"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="." mode="ansi-nontabular"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ====================================================================== -->
<!-- funcprototype: kr, non-tabular -->

<xsl:template match="funcprototype" mode="kr-nontabular">
  <p>
    <xsl:apply-templates mode="kr-nontabular"/>
    <xsl:if test="paramdef">
      <xsl:text>
.
</xsl:text>
      <xsl:apply-templates select="paramdef" mode="kr-funcsynopsis-mode"/>
    </xsl:if>
  </p>
</xsl:template>

<xsl:template match="funcdef" mode="kr-nontabular">
  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="kr-nontabular"/>
    <xsl:text>(</xsl:text>
  </code>
</xsl:template>

<xsl:template match="funcdef/function" mode="kr-nontabular">
  <xsl:choose>
    <xsl:when test="$funcsynopsis.decoration != 0">
      <b class="fsfunc"><xsl:apply-templates mode="kr-nontabular"/></b>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates mode="kr-nontabular"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="void" mode="kr-nontabular">
  <code>)</code>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="varargs" mode="kr-nontabular">
  <xsl:text>...</xsl:text>
  <code>)</code>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="paramdef" mode="kr-nontabular">
  <xsl:apply-templates select="parameter" mode="kr-nontabular"/>
  <xsl:choose>
    <xsl:when test="following-sibling::*">
      <xsl:text>, </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <code>)</code>
      <xsl:text>;</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="paramdef/parameter" mode="kr-nontabular">
  <xsl:choose>
    <xsl:when test="$funcsynopsis.decoration != 0">
      <var class="pdparam">
        <xsl:apply-templates mode="kr-nontabular"/>
      </var>
    </xsl:when>
    <xsl:otherwise>
      <code>
	<xsl:apply-templates mode="kr-nontabular"/>
      </code>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="paramdef" mode="kr-funcsynopsis-mode">
  <xsl:if test="preceding-sibling::paramdef"><xsl:text>
.
</xsl:text></xsl:if>
  <code>
    <xsl:apply-templates mode="kr-funcsynopsis-mode"/>
  </code>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="paramdef/parameter" mode="kr-funcsynopsis-mode">
  <xsl:choose>
    <xsl:when test="$funcsynopsis.decoration != 0">
      <var class="pdparam">
        <xsl:apply-templates mode="kr-funcsynopsis-mode"/>
      </var>
    </xsl:when>
    <xsl:otherwise>
      <code>
	<xsl:apply-templates mode="kr-funcsynopsis-mode"/>
      </code>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="funcparams" mode="kr-funcsynopsis-mode">
  <code>(</code>
  <xsl:apply-templates mode="kr-funcsynopsis-mode"/>
  <code>)</code>
</xsl:template>

<!-- ====================================================================== -->
<!-- funcprototype: kr, tabular -->

<xsl:template match="funcprototype" mode="kr-tabular">
  <table border="0" summary="Function synopsis" cellspacing="0" cellpadding="0" class="funcprototype-table">
    <tr>
      <td>
        <xsl:apply-templates select="funcdef" mode="kr-tabular"/>
      </td>
      <xsl:apply-templates select="(void|varargs|paramdef)[1]" mode="kr-tabular"/>
    </tr>
    <xsl:for-each select="(void|varargs|paramdef)[preceding-sibling::*[not(self::funcdef)]]">
      <tr>
        <td> </td>
        <xsl:apply-templates select="." mode="kr-tabular"/>
      </tr>
    </xsl:for-each>
  </table>
  <xsl:if test="paramdef">
    <div class="paramdef-list">
      <xsl:apply-templates select="paramdef" mode="kr-funcsynopsis-mode"/>
    </div>
  </xsl:if>
  <div class="funcprototype-spacer"> </div> <!-- hACk: blank div for vertical spacing -->
</xsl:template>

<xsl:template match="funcdef" mode="kr-tabular">
  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="kr-tabular"/>
    <xsl:text>(</xsl:text>
  </code>
</xsl:template>

<xsl:template match="funcdef/function" mode="kr-tabular">
  <xsl:choose>
    <xsl:when test="$funcsynopsis.decoration != 0">
      <b class="fsfunc"><xsl:apply-templates mode="kr-nontabular"/></b>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates mode="kr-tabular"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="void" mode="kr-tabular">
  <td>
    <code>)</code>
    <xsl:text>;</xsl:text>
  </td>
  <td> </td>
</xsl:template>

<xsl:template match="varargs" mode="kr-tabular">
  <td>
    <xsl:text>...</xsl:text>
    <code>)</code>
    <xsl:text>;</xsl:text>
  </td>
  <td> </td>
</xsl:template>

<xsl:template match="paramdef" mode="kr-tabular">
  <td>
    <xsl:apply-templates select="parameter" mode="kr-tabular"/>
    <xsl:choose>
      <xsl:when test="following-sibling::*">
	<xsl:text>, </xsl:text>
      </xsl:when>
      <xsl:otherwise>
	<code>)</code>
	<xsl:text>;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </td>
  <td> </td>
</xsl:template>

<xsl:template match="paramdef/parameter" mode="kr-tabular">
  <xsl:choose>
    <xsl:when test="$funcsynopsis.decoration != 0">
      <var class="pdparam">
        <xsl:apply-templates mode="kr-tabular"/>
      </var>
    </xsl:when>
    <xsl:otherwise>
      <code>
	<xsl:apply-templates mode="kr-tabular"/>
      </code>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="paramdef" mode="kr-tabular-funcsynopsis-mode">
  <xsl:variable name="type">
    <xsl:choose>
      <xsl:when test="type">
	<xsl:apply-templates select="type" mode="kr-tabular-funcsynopsis-mode"/>
      </xsl:when>
      <xsl:when test="normalize-space(parameter/preceding-sibling::node()[not(self::parameter)]) != ''">
	<xsl:copy-of select="parameter/preceding-sibling::node()[not(self::parameter)]"/>
      </xsl:when>
    </xsl:choose>
  </xsl:variable>

  <tr>
    <xsl:choose>
      <xsl:when test="$type != '' and funcparams">
        <td>
	  <code>
	    <xsl:copy-of select="$type"/>
	  </code>
          <xsl:text> </xsl:text>
        </td>
        <td>
	  <code>
	    <xsl:choose>
	      <xsl:when test="type">
		<xsl:apply-templates select="type/following-sibling::*" mode="kr-tabular-funcsynopsis-mode"/>
	      </xsl:when>
	      <xsl:otherwise>
		<xsl:apply-templates select="*" mode="kr-tabular-funcsynopsis-mode"/>
	      </xsl:otherwise>
	    </xsl:choose>
	  </code>
        </td>
      </xsl:when>

      <xsl:when test="funcparams">
        <td colspan="2">
	  <code>
	    <xsl:apply-templates mode="kr-tabular-funcsynopsis-mode"/>
	  </code>
        </td>
      </xsl:when>

      <xsl:otherwise>
        <td>
	  <code>
	    <xsl:apply-templates select="parameter/preceding-sibling::node()[not(self::parameter)]" mode="kr-tabular-funcsynopsis-mode"/>
	  </code>
          <xsl:text> </xsl:text>
        </td>
        <td>
	  <code>
	    <xsl:apply-templates select="parameter" mode="kr-tabular"/>
	    <xsl:apply-templates select="parameter/following-sibling::*[not(self::parameter)]" mode="kr-tabular-funcsynopsis-mode"/>
	    <xsl:text>;</xsl:text>
	  </code>
        </td>
      </xsl:otherwise>
    </xsl:choose>
  </tr>
</xsl:template>

<xsl:template match="paramdef/parameter" mode="kr-tabular-funcsynopsis-mode">
  <xsl:choose>
    <xsl:when test="$funcsynopsis.decoration != 0">
      <var class="pdparam">
        <xsl:apply-templates mode="kr-tabular-funcsynopsis-mode"/>
      </var>
    </xsl:when>
    <xsl:otherwise>
      <code>
	<xsl:apply-templates mode="kr-tabular-funcsynopsis-mode"/>
      </code>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="funcparams" mode="kr-tabular-funcsynopsis-mode">
  <code>(</code>
  <xsl:apply-templates mode="kr-tabular-funcsynopsis-mode"/>
  <code>)</code>
  <xsl:text>;</xsl:text>
</xsl:template>

<!-- ====================================================================== -->
<!-- funcprototype: ansi, non-tabular -->

<xsl:template match="funcprototype" mode="ansi-nontabular">
  <p>
    <xsl:apply-templates mode="ansi-nontabular"/>
  </p>
</xsl:template>

<xsl:template match="funcdef" mode="ansi-nontabular">
  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="ansi-nontabular"/>
    <xsl:text>(</xsl:text>
  </code>
</xsl:template>

<xsl:template match="funcdef/function" mode="ansi-nontabular">
  <xsl:choose>
    <xsl:when test="$funcsynopsis.decoration != 0">
      <b class="fsfunc"><xsl:apply-templates mode="ansi-nontabular"/></b>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates mode="ansi-nontabular"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="void" mode="ansi-nontabular">
  <code>void)</code>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="varargs" mode="ansi-nontabular">
  <xsl:text>...</xsl:text>
  <code>)</code>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template match="paramdef" mode="ansi-nontabular">
  <xsl:apply-templates mode="ansi-nontabular"/>
  <xsl:choose>
    <xsl:when test="following-sibling::*">
      <xsl:text>, </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <code>)</code>
      <xsl:text>;</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="paramdef/parameter" mode="ansi-nontabular">
  <xsl:choose>
    <xsl:when test="$funcsynopsis.decoration != 0">
      <var class="pdparam">
        <xsl:apply-templates mode="ansi-nontabular"/>
      </var>
    </xsl:when>
    <xsl:otherwise>
      <code>
	<xsl:apply-templates mode="ansi-nontabular"/>
      </code>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="funcparams" mode="ansi-nontabular">
  <code>(</code>
  <xsl:apply-templates mode="ansi-nontabular"/>
  <code>)</code>
</xsl:template>

<!-- ====================================================================== -->
<!-- funcprototype: ansi, tabular -->

<xsl:template match="funcprototype" mode="ansi-tabular">
  <table border="0" summary="Function synopsis" cellspacing="0" cellpadding="0" class="funcprototype-table">
    <tr>
      <td>
        <xsl:apply-templates select="funcdef" mode="ansi-tabular"/>
      </td>
      <xsl:apply-templates select="(void|varargs|paramdef)[1]" mode="ansi-tabular"/>
    </tr>
    <xsl:for-each select="(void|varargs|paramdef)[preceding-sibling::*[not(self::funcdef)]]">
      <tr>
        <td> </td>
        <xsl:apply-templates select="." mode="ansi-tabular"/>
      </tr>
    </xsl:for-each>
  </table>
  <div class="funcprototype-spacer"> </div> <!-- hACk: blank div for vertical spacing -->
</xsl:template>

<xsl:template match="funcdef" mode="ansi-tabular">
  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="ansi-tabular"/>
    <xsl:text>(</xsl:text>
  </code>
</xsl:template>

<xsl:template match="funcdef/function" mode="ansi-tabular">
  <xsl:choose>
    <xsl:when test="$funcsynopsis.decoration != 0">
      <b class="fsfunc"><xsl:apply-templates mode="ansi-nontabular"/></b>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates mode="kr-tabular"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="void" mode="ansi-tabular">
  <td>
    <code>void)</code>
    <xsl:text>;</xsl:text>
  </td>
  <td> </td>
</xsl:template>

<xsl:template match="varargs" mode="ansi-tabular">
  <td>
    <xsl:text>...</xsl:text>
    <code>)</code>
    <xsl:text>;</xsl:text>
  </td>
  <td> </td>
</xsl:template>

<xsl:template match="paramdef" mode="ansi-tabular">
      <td>
        <xsl:apply-templates mode="ansi-tabular"/>
        <xsl:choose>
          <xsl:when test="following-sibling::*">
            <xsl:text>, </xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <code>)</code>
            <xsl:text>;</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </td>
</xsl:template>

<xsl:template match="paramdef/parameter" mode="ansi-tabular">
  <xsl:choose>
    <xsl:when test="$funcsynopsis.decoration != 0">
      <var class="pdparam">
        <xsl:apply-templates mode="ansi-tabular"/>
      </var>
    </xsl:when>
    <xsl:otherwise>
      <code>
	<xsl:apply-templates mode="ansi-tabular"/>
      </code>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="funcparams" mode="ansi-tabular">
  <code>(</code>
  <xsl:apply-templates/>
  <code>)</code>
</xsl:template>

<!-- ====================================================================== -->

<xsl:variable name="default-classsynopsis-language">java</xsl:variable>

<xsl:template match="classsynopsis                      |fieldsynopsis                      |methodsynopsis                      |constructorsynopsis                      |destructorsynopsis">
  <xsl:param name="language">
    <xsl:choose>
      <xsl:when test="@language">
	<xsl:value-of select="@language"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="$default-classsynopsis-language"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:choose>
    <xsl:when test="$language='java' or $language='Java'">
      <xsl:apply-templates select="." mode="java"/>
    </xsl:when>
    <xsl:when test="$language='perl' or $language='Perl'">
      <xsl:apply-templates select="." mode="perl"/>
    </xsl:when>
    <xsl:when test="$language='idl' or $language='IDL'">
      <xsl:apply-templates select="." mode="idl"/>
    </xsl:when>
    <xsl:when test="$language='cpp' or $language='c++' or $language='C++'">
      <xsl:apply-templates select="." mode="cpp"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message>
	<xsl:text>Unrecognized language on </xsl:text>
        <xsl:value-of select="local-name(.)"/>
        <xsl:text>: </xsl:text>
	<xsl:value-of select="$language"/>
      </xsl:message>
      <xsl:apply-templates select=".">
	<xsl:with-param name="language" select="$default-classsynopsis-language"/>
      </xsl:apply-templates>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="synop-break">
  <xsl:if test="parent::classsynopsis                 or (following-sibling::fieldsynopsis                     |following-sibling::methodsynopsis                     |following-sibling::constructorsynopsis                     |following-sibling::destructorsynopsis)">
    <xsl:text>
.
</xsl:text>
  </xsl:if>
</xsl:template>


<!-- ===== Java ======================================================== -->

<xsl:template match="classsynopsis" mode="java">
  <xsl:text>.sp
</xsl:text><xsl:text>.nf
</xsl:text><pre>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates select="ooclass[1]" mode="java"/>
    <xsl:if test="ooclass[preceding-sibling::*]">
      <xsl:text> extends</xsl:text>
      <xsl:apply-templates select="ooclass[preceding-sibling::*]" mode="java"/>
      <xsl:if test="oointerface|ooexception">
        <xsl:text>
.
</xsl:text>
	<xsl:text>    </xsl:text>
      </xsl:if>
    </xsl:if>
    <xsl:if test="oointerface">
      <xsl:text>implements</xsl:text>
      <xsl:apply-templates select="oointerface" mode="java"/>
      <xsl:if test="ooexception">
        <xsl:text>
.
</xsl:text>
	<xsl:text>    </xsl:text>
      </xsl:if>
    </xsl:if>
    <xsl:if test="ooexception">
      <xsl:text>throws</xsl:text>
      <xsl:apply-templates select="ooexception" mode="java"/>
    </xsl:if>
    <xsl:text> {</xsl:text>
    <xsl:text>
.
</xsl:text>
    <xsl:apply-templates select="constructorsynopsis                                  |destructorsynopsis                                  |fieldsynopsis                                  |methodsynopsis                                  |classsynopsisinfo" mode="java"/>
    <xsl:text>}</xsl:text>
  </pre><xsl:text/><xsl:text>.fi
</xsl:text>
</xsl:template>

<xsl:template match="classsynopsisinfo" mode="java">
  <xsl:apply-templates mode="java"/>
</xsl:template>

<xsl:template match="ooclass|oointerface|ooexception" mode="java">
  <xsl:choose>
    <xsl:when test="preceding-sibling::*">
      <xsl:text>, </xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text> </xsl:text>
    </xsl:otherwise>
  </xsl:choose>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="java"/>
  </span>
</xsl:template>

<xsl:template match="modifier|package" mode="java">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="java"/>
    <xsl:if test="following-sibling::*">
      <xsl:text> </xsl:text>
    </xsl:if>
  </span>
</xsl:template>

<xsl:template match="classname" mode="java">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'classname'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="java"/>
  </span>
</xsl:template>

<xsl:template match="interfacename" mode="java">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'interfacename'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="java"/>
  </span>
</xsl:template>

<xsl:template match="exceptionname" mode="java">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'exceptionname'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="java"/>
  </span>
</xsl:template>

<xsl:template match="fieldsynopsis" mode="java">
  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:if test="parent::classsynopsis">
      <xsl:text>  </xsl:text>
    </xsl:if>
    <xsl:apply-templates mode="java"/>
    <xsl:text>;</xsl:text>
  </code>
  <xsl:call-template name="synop-break"/>
</xsl:template>

<xsl:template match="type" mode="java">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="java"/>
    <xsl:text> </xsl:text>
  </span>
</xsl:template>

<xsl:template match="varname" mode="java">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="java"/>
    <xsl:text> </xsl:text>
  </span>
</xsl:template>

<xsl:template match="initializer" mode="java">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:text>= </xsl:text>
    <xsl:apply-templates mode="java"/>
  </span>
</xsl:template>

<xsl:template match="void" mode="java">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:text>void </xsl:text>
  </span>
</xsl:template>

<xsl:template match="methodname" mode="java">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="java"/>
  </span>
</xsl:template>

<xsl:template match="methodparam" mode="java">
  <xsl:param name="indent">0</xsl:param>
  <xsl:if test="preceding-sibling::methodparam">
    <xsl:text>,</xsl:text>
    <xsl:text>
.
</xsl:text>
    <xsl:if test="$indent &gt; 0">
      <xsl:call-template name="copy-string">
	<xsl:with-param name="string"> </xsl:with-param>
	<xsl:with-param name="count" select="$indent + 1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="java"/>
  </span>
</xsl:template>

<xsl:template match="parameter" mode="java">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="java"/>
  </span>
</xsl:template>

<xsl:template mode="java" match="constructorsynopsis|destructorsynopsis|methodsynopsis">
  <xsl:variable name="start-modifiers" select="modifier[following-sibling::*[local-name(.) != 'modifier']]"/>
  <xsl:variable name="notmod" select="*[local-name(.) != 'modifier']"/>
  <xsl:variable name="end-modifiers" select="modifier[preceding-sibling::*[local-name(.) != 'modifier']]"/>
  <xsl:variable name="decl">
    <xsl:if test="parent::classsynopsis">
      <xsl:text>  </xsl:text>
    </xsl:if>
    <xsl:apply-templates select="$start-modifiers" mode="java"/>

    <!-- type -->
    <xsl:if test="local-name($notmod[1]) != 'methodname'">
      <xsl:apply-templates select="$notmod[1]" mode="java"/>
    </xsl:if>

    <xsl:apply-templates select="methodname" mode="java"/>
  </xsl:variable>

  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:copy-of select="$decl"/>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="methodparam" mode="java">
      <xsl:with-param name="indent" select="string-length($decl)"/>
    </xsl:apply-templates>
    <xsl:text>)</xsl:text>
    <xsl:if test="exceptionname">
      <xsl:text>
.
</xsl:text>
      <xsl:text>    throws </xsl:text>
      <xsl:apply-templates select="exceptionname" mode="java"/>
    </xsl:if>
    <xsl:if test="modifier[preceding-sibling::*[local-name(.) != 'modifier']]">
      <xsl:text> </xsl:text>
      <xsl:apply-templates select="$end-modifiers" mode="java"/>
    </xsl:if>
    <xsl:text>;</xsl:text>
  </code>
  <xsl:call-template name="synop-break"/>
</xsl:template>

<!-- ===== C++ ========================================================= -->

<xsl:template match="classsynopsis" mode="cpp">
  <xsl:text>.sp
</xsl:text><xsl:text>.nf
</xsl:text><pre>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates select="ooclass[1]" mode="cpp"/>
    <xsl:if test="ooclass[preceding-sibling::*]">
      <xsl:text>: </xsl:text>
      <xsl:apply-templates select="ooclass[preceding-sibling::*]" mode="cpp"/>
      <xsl:if test="oointerface|ooexception">
        <xsl:text>
.
</xsl:text>
	<xsl:text>    </xsl:text>
      </xsl:if>
    </xsl:if>
    <xsl:if test="oointerface">
      <xsl:text> implements</xsl:text>
      <xsl:apply-templates select="oointerface" mode="cpp"/>
      <xsl:if test="ooexception">
        <xsl:text>
.
</xsl:text>
	<xsl:text>    </xsl:text>
      </xsl:if>
    </xsl:if>
    <xsl:if test="ooexception">
      <xsl:text> throws</xsl:text>
      <xsl:apply-templates select="ooexception" mode="cpp"/>
    </xsl:if>
    <xsl:text> {</xsl:text>
    <xsl:text>
.
</xsl:text>
    <xsl:apply-templates select="constructorsynopsis                                  |destructorsynopsis                                  |fieldsynopsis                                  |methodsynopsis                                  |classsynopsisinfo" mode="cpp"/>
    <xsl:text>}</xsl:text>
  </pre><xsl:text/><xsl:text>.fi
</xsl:text>
</xsl:template>

<xsl:template match="classsynopsisinfo" mode="cpp">
  <xsl:apply-templates mode="cpp"/>
</xsl:template>

<xsl:template match="ooclass|oointerface|ooexception" mode="cpp">
  <xsl:if test="preceding-sibling::*">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="cpp"/>
  </span>
</xsl:template>

<xsl:template match="modifier|package" mode="cpp">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="cpp"/>
    <xsl:if test="following-sibling::*">
      <xsl:text> </xsl:text>
    </xsl:if>
  </span>
</xsl:template>

<xsl:template match="classname" mode="cpp">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'classname'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="cpp"/>
  </span>
</xsl:template>

<xsl:template match="interfacename" mode="cpp">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'interfacename'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="cpp"/>
  </span>
</xsl:template>

<xsl:template match="exceptionname" mode="cpp">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'exceptionname'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="cpp"/>
  </span>
</xsl:template>

<xsl:template match="fieldsynopsis" mode="cpp">
  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:if test="parent::classsynopsis">
      <xsl:text>  </xsl:text>
    </xsl:if>
    <xsl:apply-templates mode="cpp"/>
    <xsl:text>;</xsl:text>
  </code>
  <xsl:call-template name="synop-break"/>
</xsl:template>

<xsl:template match="type" mode="cpp">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="cpp"/>
    <xsl:text> </xsl:text>
  </span>
</xsl:template>

<xsl:template match="varname" mode="cpp">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="cpp"/>
    <xsl:text> </xsl:text>
  </span>
</xsl:template>

<xsl:template match="initializer" mode="cpp">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:text>= </xsl:text>
    <xsl:apply-templates mode="cpp"/>
  </span>
</xsl:template>

<xsl:template match="void" mode="cpp">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:text>void </xsl:text>
  </span>
</xsl:template>

<xsl:template match="methodname" mode="cpp">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="cpp"/>
  </span>
</xsl:template>

<xsl:template match="methodparam" mode="cpp">
  <xsl:if test="preceding-sibling::methodparam">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="cpp"/>
  </span>
</xsl:template>

<xsl:template match="parameter" mode="cpp">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="cpp"/>
  </span>
</xsl:template>

<xsl:template mode="cpp" match="constructorsynopsis|destructorsynopsis|methodsynopsis">
  <xsl:variable name="start-modifiers" select="modifier[following-sibling::*[local-name(.) != 'modifier']]"/>
  <xsl:variable name="notmod" select="*[local-name(.) != 'modifier']"/>
  <xsl:variable name="end-modifiers" select="modifier[preceding-sibling::*[local-name(.) != 'modifier']]"/>

  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:if test="parent::classsynopsis">
      <xsl:text>  </xsl:text>
    </xsl:if>
    <xsl:apply-templates select="$start-modifiers" mode="cpp"/>

    <!-- type -->
    <xsl:if test="local-name($notmod[1]) != 'methodname'">
      <xsl:apply-templates select="$notmod[1]" mode="cpp"/>
    </xsl:if>

    <xsl:apply-templates select="methodname" mode="cpp"/>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="methodparam" mode="cpp"/>
    <xsl:text>)</xsl:text>
    <xsl:if test="exceptionname">
      <xsl:text>
.
</xsl:text>
      <xsl:text>    throws </xsl:text>
      <xsl:apply-templates select="exceptionname" mode="cpp"/>
    </xsl:if>
    <xsl:if test="modifier[preceding-sibling::*[local-name(.) != 'modifier']]">
      <xsl:text> </xsl:text>
      <xsl:apply-templates select="$end-modifiers" mode="cpp"/>
    </xsl:if>
    <xsl:text>;</xsl:text>
  </code>
  <xsl:call-template name="synop-break"/>
</xsl:template>

<!-- ===== IDL ========================================================= -->

<xsl:template match="classsynopsis" mode="idl">
  <xsl:text>.sp
</xsl:text><xsl:text>.nf
</xsl:text><pre>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:text>interface </xsl:text>
    <xsl:apply-templates select="ooclass[1]" mode="idl"/>
    <xsl:if test="ooclass[preceding-sibling::*]">
      <xsl:text>: </xsl:text>
      <xsl:apply-templates select="ooclass[preceding-sibling::*]" mode="idl"/>
      <xsl:if test="oointerface|ooexception">
        <xsl:text>
.
</xsl:text>
	<xsl:text>    </xsl:text>
      </xsl:if>
    </xsl:if>
    <xsl:if test="oointerface">
      <xsl:text> implements</xsl:text>
      <xsl:apply-templates select="oointerface" mode="idl"/>
      <xsl:if test="ooexception">
        <xsl:text>
.
</xsl:text>
	<xsl:text>    </xsl:text>
      </xsl:if>
    </xsl:if>
    <xsl:if test="ooexception">
      <xsl:text> throws</xsl:text>
      <xsl:apply-templates select="ooexception" mode="idl"/>
    </xsl:if>
    <xsl:text> {</xsl:text>
    <xsl:text>
.
</xsl:text>
    <xsl:apply-templates select="constructorsynopsis                                  |destructorsynopsis                                  |fieldsynopsis                                  |methodsynopsis                                  |classsynopsisinfo" mode="idl"/>
    <xsl:text>}</xsl:text>
  </pre><xsl:text/><xsl:text>.fi
</xsl:text>
</xsl:template>

<xsl:template match="classsynopsisinfo" mode="idl">
  <xsl:apply-templates mode="idl"/>
</xsl:template>

<xsl:template match="ooclass|oointerface|ooexception" mode="idl">
  <xsl:if test="preceding-sibling::*">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="idl"/>
  </span>
</xsl:template>

<xsl:template match="modifier|package" mode="idl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="idl"/>
    <xsl:if test="following-sibling::*">
      <xsl:text> </xsl:text>
    </xsl:if>
  </span>
</xsl:template>

<xsl:template match="classname" mode="idl">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'classname'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="idl"/>
  </span>
</xsl:template>

<xsl:template match="interfacename" mode="idl">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'interfacename'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="idl"/>
  </span>
</xsl:template>

<xsl:template match="exceptionname" mode="idl">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'exceptionname'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="idl"/>
  </span>
</xsl:template>

<xsl:template match="fieldsynopsis" mode="idl">
  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:if test="parent::classsynopsis">
      <xsl:text>  </xsl:text>
    </xsl:if>
    <xsl:apply-templates mode="idl"/>
    <xsl:text>;</xsl:text>
  </code>
  <xsl:call-template name="synop-break"/>
</xsl:template>

<xsl:template match="type" mode="idl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="idl"/>
    <xsl:text> </xsl:text>
  </span>
</xsl:template>

<xsl:template match="varname" mode="idl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="idl"/>
    <xsl:text> </xsl:text>
  </span>
</xsl:template>

<xsl:template match="initializer" mode="idl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:text>= </xsl:text>
    <xsl:apply-templates mode="idl"/>
  </span>
</xsl:template>

<xsl:template match="void" mode="idl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:text>void </xsl:text>
  </span>
</xsl:template>

<xsl:template match="methodname" mode="idl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="idl"/>
  </span>
</xsl:template>

<xsl:template match="methodparam" mode="idl">
  <xsl:if test="preceding-sibling::methodparam">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="idl"/>
  </span>
</xsl:template>

<xsl:template match="parameter" mode="idl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="idl"/>
  </span>
</xsl:template>

<xsl:template mode="idl" match="constructorsynopsis|destructorsynopsis|methodsynopsis">
  <xsl:variable name="start-modifiers" select="modifier[following-sibling::*[local-name(.) != 'modifier']]"/>
  <xsl:variable name="notmod" select="*[local-name(.) != 'modifier']"/>
  <xsl:variable name="end-modifiers" select="modifier[preceding-sibling::*[local-name(.) != 'modifier']]"/>
  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:if test="parent::classsynopsis">
      <xsl:text>  </xsl:text>
    </xsl:if>
    <xsl:apply-templates select="$start-modifiers" mode="idl"/>

    <!-- type -->
    <xsl:if test="local-name($notmod[1]) != 'methodname'">
      <xsl:apply-templates select="$notmod[1]" mode="idl"/>
    </xsl:if>

    <xsl:apply-templates select="methodname" mode="idl"/>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="methodparam" mode="idl"/>
    <xsl:text>)</xsl:text>
    <xsl:if test="exceptionname">
      <xsl:text>
.
</xsl:text>
      <xsl:text>    raises(</xsl:text>
      <xsl:apply-templates select="exceptionname" mode="idl"/>
      <xsl:text>)</xsl:text>
    </xsl:if>
    <xsl:if test="modifier[preceding-sibling::*[local-name(.) != 'modifier']]">
      <xsl:text> </xsl:text>
      <xsl:apply-templates select="$end-modifiers" mode="idl"/>
    </xsl:if>
    <xsl:text>;</xsl:text>
  </code>
  <xsl:call-template name="synop-break"/>
</xsl:template>

<!-- ===== Perl ======================================================== -->

<xsl:template match="classsynopsis" mode="perl">
  <xsl:text>.sp
</xsl:text><xsl:text>.nf
</xsl:text><pre>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:text>package </xsl:text>
    <xsl:apply-templates select="ooclass[1]" mode="perl"/>
    <xsl:text>;</xsl:text>
    <xsl:text>
.
</xsl:text>

    <xsl:if test="ooclass[preceding-sibling::*]">
      <xsl:text>@ISA = (</xsl:text>
      <xsl:apply-templates select="ooclass[preceding-sibling::*]" mode="perl"/>
      <xsl:text>);</xsl:text>
      <xsl:text>
.
</xsl:text>
    </xsl:if>

    <xsl:apply-templates select="constructorsynopsis                                  |destructorsynopsis                                  |fieldsynopsis                                  |methodsynopsis                                  |classsynopsisinfo" mode="perl"/>
  </pre><xsl:text/><xsl:text>.fi
</xsl:text>
</xsl:template>

<xsl:template match="classsynopsisinfo" mode="perl">
  <xsl:apply-templates mode="perl"/>
</xsl:template>

<xsl:template match="ooclass|oointerface|ooexception" mode="perl">
  <xsl:if test="preceding-sibling::*">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="perl"/>
  </span>
</xsl:template>

<xsl:template match="modifier|package" mode="perl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="perl"/>
    <xsl:if test="following-sibling::*">
      <xsl:text> </xsl:text>
    </xsl:if>
  </span>
</xsl:template>

<xsl:template match="classname" mode="perl">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'classname'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="perl"/>
  </span>
</xsl:template>

<xsl:template match="interfacename" mode="perl">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'interfacename'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="perl"/>
  </span>
</xsl:template>

<xsl:template match="exceptionname" mode="perl">
  <xsl:if test="local-name(preceding-sibling::*[1]) = 'exceptionname'">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="perl"/>
  </span>
</xsl:template>

<xsl:template match="fieldsynopsis" mode="perl">
  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:if test="parent::classsynopsis">
      <xsl:text>  </xsl:text>
    </xsl:if>
    <xsl:apply-templates mode="perl"/>
    <xsl:text>;</xsl:text>
  </code>
  <xsl:call-template name="synop-break"/>
</xsl:template>

<xsl:template match="type" mode="perl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="perl"/>
    <xsl:text> </xsl:text>
  </span>
</xsl:template>

<xsl:template match="varname" mode="perl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="perl"/>
    <xsl:text> </xsl:text>
  </span>
</xsl:template>

<xsl:template match="initializer" mode="perl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:text>= </xsl:text>
    <xsl:apply-templates mode="perl"/>
  </span>
</xsl:template>

<xsl:template match="void" mode="perl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:text>void </xsl:text>
  </span>
</xsl:template>

<xsl:template match="methodname" mode="perl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="perl"/>
  </span>
</xsl:template>

<xsl:template match="methodparam" mode="perl">
  <xsl:if test="preceding-sibling::methodparam">
    <xsl:text>, </xsl:text>
  </xsl:if>
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="perl"/>
  </span>
</xsl:template>

<xsl:template match="parameter" mode="perl">
  <span>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:apply-templates mode="perl"/>
  </span>
</xsl:template>

<xsl:template mode="perl" match="constructorsynopsis|destructorsynopsis|methodsynopsis">
  <xsl:variable name="start-modifiers" select="modifier[following-sibling::*[local-name(.) != 'modifier']]"/>
  <xsl:variable name="notmod" select="*[local-name(.) != 'modifier']"/>
  <xsl:variable name="end-modifiers" select="modifier[preceding-sibling::*[local-name(.) != 'modifier']]"/>

  <code>
    <xsl:apply-templates select="." mode="common.html.attributes"/>
    <xsl:text>sub </xsl:text>

    <xsl:apply-templates select="methodname" mode="perl"/>
    <xsl:text> { ... };</xsl:text>
  </code>
  <xsl:call-template name="synop-break"/>
</xsl:template>

<!-- Used when not occurring as a child of classsynopsis -->
<xsl:template match="ooclass|oointerface|ooexception">
  <xsl:apply-templates/>
</xsl:template>

<!-- ==================================================================== -->

<!-- * DocBook 5 allows linking elements (link, olink, and xref) -->
<!-- * within the OO *synopsis elements (classsynopsis, fieldsynopsis, -->
<!-- * methodsynopsis, constructorsynopsis, destructorsynopsis) and -->
<!-- * their children. So we need to have mode="java|cpp|idl|perl" -->
<!-- * per-mode matches for those linking elements in order for them -->
<!-- * to be processed as expected. -->

<xsl:template match="link|olink|xref" mode="java">
  <xsl:apply-templates select="."/>
</xsl:template>

<xsl:template match="link|olink|xref" mode="cpp">
  <xsl:apply-templates select="."/>
</xsl:template>

<xsl:template match="link|olink|xref" mode="idl">
  <xsl:apply-templates select="."/>
</xsl:template>

<xsl:template match="link|olink|xref" mode="perl">
  <xsl:apply-templates select="."/>
</xsl:template>

</xsl:stylesheet>

