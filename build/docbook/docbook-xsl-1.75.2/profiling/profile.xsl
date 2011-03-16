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

<!-- Include common profiling stylesheet -->
<xsl:import href="profile-mode.xsl"/>

<!-- This file must be included, because profile-mode is using templates from it -->
<xsl:import href="../common/stripns.xsl"/>

<!-- In the two pass processing there is no need for base URI fixup -->
<xsl:param name="profile.baseuri.fixup" select="false()"/>

<!-- If you need to validate profiled content against DTD, 
     create customization that will import this stylesheet and
     will use xsl:output (see example bellow) to output reference 
     to the desired DTD version. --> 
<!-- Generate DocBook instance with correct DOCTYPE -->
<!--
<xsl:output method="xml" 
            doctype-public="-//OASIS//DTD DocBook XML V4.5//EN"
            doctype-system="http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd"/>
-->

<!-- Profiling parameters -->
<xsl:param name="profile.arch" select="''"/>
<xsl:param name="profile.audience" select="''"/>
<xsl:param name="profile.condition" select="''"/>
<xsl:param name="profile.conformance" select="''"/>
<xsl:param name="profile.lang" select="''"/>
<xsl:param name="profile.os" select="''"/>
<xsl:param name="profile.revision" select="''"/>
<xsl:param name="profile.revisionflag" select="''"/>
<xsl:param name="profile.role" select="''"/>
<xsl:param name="profile.security" select="''"/>
<xsl:param name="profile.status" select="''"/>
<xsl:param name="profile.userlevel" select="''"/>
<xsl:param name="profile.vendor" select="''"/>
<xsl:param name="profile.wordsize" select="''"/>
<xsl:param name="profile.attribute" select="''"/>
<xsl:param name="profile.value" select="''"/>
<xsl:param name="profile.separator" select="';'"/>

<xsl:param name="exsl.node.set.available"> 
  <xsl:choose>
    <xsl:when xmlns:exsl="http://exslt.org/common" exsl:foo="" test="function-available('exsl:node-set') or contains(system-property('xsl:vendor'), 'Apache Software Foundation')">1</xsl:when>
    <xsl:otherwise>0</xsl:otherwise>
  </xsl:choose>
</xsl:param>

<!-- Call common profiling mode -->
<xsl:template match="/">
  <xsl:apply-templates select="." mode="profile"/>
</xsl:template>

</xsl:stylesheet>

