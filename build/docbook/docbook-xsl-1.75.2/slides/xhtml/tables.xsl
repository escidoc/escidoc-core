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

<xsl:import href="slides-common.xsl"/>

<xsl:param name="blank.image" select="'blank.png'"/>
<xsl:param name="arrow.image" select="'pointer.png'"/>

<xsl:param name="toc.bg.color">#6A719C</xsl:param>
<xsl:param name="toc.width">220</xsl:param>

<xsl:param name="css.stylesheet" select="'slides-table.css'"/>

<!-- ============================================================ -->

<xsl:template name="foilgroup-body">
  <table border="0" width="100%" summary="Navigation and body table" cellpadding="0" cellspacing="0">
    <tr>
      <td width="{$toc.width}" valign="top" align="left">
	<xsl:if test="$toc.bg.color != ''">
	  <xsl:attribute name="bgcolor">
	    <xsl:value-of select="$toc.bg.color"/>
	  </xsl:attribute>
	</xsl:if>
        <div class="ttoc">
          <xsl:apply-templates select="." mode="t-toc"/>
        </div>
      </td>
      <td>&#160;</td>
      <td valign="top" align="left">
	<xsl:if test="$body.bg.color != ''">
	  <xsl:attribute name="bgcolor">
	    <xsl:value-of select="$body.bg.color"/>
	  </xsl:attribute>
	</xsl:if>
        <div class="{name(.)}">
          <xsl:apply-templates select="*[name(.) != 'foil'                                          and name(.) != 'foilgroup']"/>
        </div>

	<xsl:if test="$foilgroup.toc != 0">
	  <dl class="toc">
	    <xsl:apply-templates select="foil" mode="toc"/>
	  </dl>
	</xsl:if>
      </td>
    </tr>
  </table>
</xsl:template>

<xsl:template name="foil-body">
  <table border="0" width="100%" summary="Navigation and body table" cellpadding="0" cellspacing="0">
    <tr>
      <td width="{$toc.width}" valign="top" align="left">
	<xsl:if test="$toc.bg.color != ''">
	  <xsl:attribute name="bgcolor">
	    <xsl:value-of select="$toc.bg.color"/>
	  </xsl:attribute>
	</xsl:if>
        <div class="ttoc">
          <xsl:apply-templates select="." mode="t-toc"/>
        </div>
      </td>
      <td>&#160;</td>
      <td valign="top" align="left">
	<xsl:if test="$body.bg.color != ''">
	  <xsl:attribute name="bgcolor">
	    <xsl:value-of select="$body.bg.color"/>
	  </xsl:attribute>
	</xsl:if>
        <div class="{name(.)}">
          <xsl:apply-templates/>
        </div>
      </td>
    </tr>
  </table>
</xsl:template>

<!-- ============================================================ -->

<xsl:template match="foilgroup" mode="t-toc">
  <xsl:variable name="thisfoilgroup" select="."/>

  <xsl:for-each select="/slides/foil|/slides/foilgroup">
    <xsl:choose>
      <xsl:when test="name(.) = 'foilgroup'">
        <xsl:choose>
          <xsl:when test="$thisfoilgroup = .">
            <img alt="+">
              <xsl:attribute name="src">
                <xsl:call-template name="graphics-file">
                  <xsl:with-param name="image" select="$arrow.image"/>
                </xsl:call-template>
              </xsl:attribute>
            </img>
          </xsl:when>
          <xsl:otherwise>
            <img alt=" ">
              <xsl:attribute name="src">
                <xsl:call-template name="graphics-file">
                  <xsl:with-param name="image" select="$blank.image"/>
                </xsl:call-template>
              </xsl:attribute>
            </img>
          </xsl:otherwise>
        </xsl:choose>

        <span class="ttoc-foilgroup">
          <a>
            <xsl:attribute name="href">
              <xsl:apply-templates select="." mode="filename"/>
            </xsl:attribute>
            <xsl:apply-templates select="." mode="toc-title"/>
          </a>
        </span>
        <br/>

        <xsl:if test="$thisfoilgroup = .">
          <xsl:for-each select="foil">
            <img alt=" ">
              <xsl:attribute name="src">
                <xsl:call-template name="graphics-file">
                  <xsl:with-param name="image" select="$blank.image"/>
                </xsl:call-template>
              </xsl:attribute>
            </img>
            <img alt=" ">
              <xsl:attribute name="src">
                <xsl:call-template name="graphics-file">
                  <xsl:with-param name="image" select="$blank.image"/>
                </xsl:call-template>
              </xsl:attribute>
            </img>

            <span class="ttoc-foil">
              <a>
                <xsl:attribute name="href">
                  <xsl:apply-templates select="." mode="filename"/>
                </xsl:attribute>
                <xsl:apply-templates select="." mode="toc-title"/>
              </a>
            </span>
            <br/>
          </xsl:for-each>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <img alt=" ">
          <xsl:attribute name="src">
            <xsl:call-template name="graphics-file">
              <xsl:with-param name="image" select="$blank.image"/>
            </xsl:call-template>
          </xsl:attribute>
        </img>
        <span class="ttoc-foil">
          <a>
            <xsl:attribute name="href">
              <xsl:apply-templates select="." mode="filename"/>
            </xsl:attribute>
            <xsl:apply-templates select="." mode="toc-title"/>
          </a>
        </span>
        <br/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template match="foil" mode="t-toc">
  <xsl:variable name="thisfoil" select="."/>

  <xsl:for-each select="/slides/foil|/slides/foilgroup">
    <xsl:choose>
      <xsl:when test="name(.) = 'foilgroup'">
        <img alt=" ">
          <xsl:attribute name="src">
            <xsl:call-template name="graphics-file">
              <xsl:with-param name="image" select="$blank.image"/>
            </xsl:call-template>
          </xsl:attribute>
        </img>
        <span class="ttoc-foilgroup">
          <a>
            <xsl:attribute name="href">
              <xsl:apply-templates select="." mode="filename"/>
            </xsl:attribute>
            <xsl:apply-templates select="." mode="toc-title"/>
          </a>
        </span>
        <br/>

        <xsl:if test="$thisfoil/ancestor::foilgroup = .">
          <xsl:for-each select="foil">
            <img alt=" ">
              <xsl:attribute name="src">
                <xsl:call-template name="graphics-file">
                  <xsl:with-param name="image" select="$blank.image"/>
                </xsl:call-template>
              </xsl:attribute>
            </img>

            <xsl:choose>
              <xsl:when test="$thisfoil = .">
                <img alt="+">
                  <xsl:attribute name="src">
                    <xsl:call-template name="graphics-file">
                      <xsl:with-param name="image" select="$arrow.image"/>
                    </xsl:call-template>
                  </xsl:attribute>
                </img>
              </xsl:when>
              <xsl:otherwise>
                <img alt=" ">
                  <xsl:attribute name="src">
                    <xsl:call-template name="graphics-file">
                      <xsl:with-param name="image" select="$blank.image"/>
                    </xsl:call-template>
                  </xsl:attribute>
                </img>
              </xsl:otherwise>
            </xsl:choose>

            <span class="ttoc-foil">
              <a>
                <xsl:attribute name="href">
                  <xsl:apply-templates select="." mode="filename"/>
                </xsl:attribute>
                <xsl:apply-templates select="." mode="toc-title"/>
              </a>
            </span>
            <br/>
          </xsl:for-each>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <!-- foils only -->
        <xsl:for-each select="/slides/foil">
          <xsl:choose>
            <xsl:when test="$thisfoil = .">
              <img alt="+">
                <xsl:attribute name="src">
                  <xsl:call-template name="graphics-file">
                    <xsl:with-param name="image" select="$arrow.image"/>
                  </xsl:call-template>
                </xsl:attribute>
              </img>
            </xsl:when>
            <xsl:otherwise>
              <img alt=" ">
                <xsl:attribute name="src">
                  <xsl:call-template name="graphics-file">
                    <xsl:with-param name="image" select="$blank.image"/>
                  </xsl:call-template>
                </xsl:attribute>
              </img>
            </xsl:otherwise>
          </xsl:choose>
          <span class="ttoc-foil">
            <xsl:apply-templates select="." mode="toc-title"/>
          </span>
          <br/>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<!-- ============================================================ -->

<xsl:template match="slides" mode="toc-title">
  <xsl:call-template name="nobreak">
    <xsl:with-param name="string">
      <xsl:choose>
        <xsl:when test="slidesinfo/titleabbrev">
          <xsl:value-of select="slidesinfo/titleabbrev"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="slidesinfo/title"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<xsl:template match="foilgroup" mode="toc-title">
  <xsl:call-template name="nobreak">
    <xsl:with-param name="string">
      <xsl:choose>
        <xsl:when test="titleabbrev">
          <xsl:value-of select="titleabbrev"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="title"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<xsl:template match="foil" mode="toc-title">
  <xsl:call-template name="nobreak">
    <xsl:with-param name="string">
      <xsl:choose>
        <xsl:when test="titleabbrev">
          <xsl:value-of select="titleabbrev"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="title"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<xsl:template name="nobreak">
  <xsl:param name="string" select="''"/>
  <xsl:choose>
    <xsl:when test="contains($string, ' ')">
      <xsl:value-of select="substring-before($string, ' ')"/>
      <xsl:text>&#160;</xsl:text>
      <xsl:call-template name="nobreak">
        <xsl:with-param name="string" select="substring-after($string, ' ')"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$string"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ============================================================ -->

</xsl:stylesheet>
