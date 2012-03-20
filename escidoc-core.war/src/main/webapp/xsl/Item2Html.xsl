<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:prop="http://escidoc.de/core/01/properties/"
    xmlns:srel="http://escidoc.de/core/01/structural-relations/"
    xmlns:md="http://www.escidoc.de/schemas/metadatarecords/0.5"
    xmlns:item="http://www.escidoc.de/schemas/item/0.10"
    xmlns:co="http://www.escidoc.de/schemas/components/0.9"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" version="1.0"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    <xsl:output encoding="iso-8859-1" indent="yes" method="html"/>

    <xsl:template match="/co:component">
        <h3>Component standalone (to be revised)</h3>
        <xsl:apply-templates/>
    </xsl:template>
</xsl:stylesheet>
