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

<xsl:stylesheet version='1.0'
  xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
  xmlns:rnd='http://docbook.org/ns/docbook/roundtrip'
  xmlns:db='http://docbook.org/ns/docbook'
  exclude-result-prefixes='db'>

  <!-- ********************************************************************
       $Id$
       ********************************************************************

       This file is part of the XSL DocBook Stylesheet distribution.
       See ../README or http://nwalsh.com/docbook/xsl/ for copyright
       and other information.

       ******************************************************************** -->

  <!-- rnd:map-paragraph-style and rd:map-character-style
       allows the application to customise
       the style names used by overriding this template.
       The idea is to map custom names back to standard names. -->
  <xsl:template name='rnd:map-paragraph-style'>
    <xsl:param name='style'/>
    <xsl:choose>
      <xsl:when test='starts-with($style, "Normal")'/>

      <!-- Probably should fold all style names to lower-case -->
      <xsl:when test='$style = "Caption"'>caption</xsl:when>

      <xsl:otherwise>
        <xsl:value-of select='$style'/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name='rnd:map-character-style'>
    <xsl:param name='style'/>
    <xsl:value-of select='$style'/>
  </xsl:template>

</xsl:stylesheet>
