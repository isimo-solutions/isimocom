<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="node()|@*">
	<xsl:copy><xsl:apply-templates select="node()|@*"/></xsl:copy>
</xsl:template>

<xsl:template match="*[@critical='yes' and text()!='0']/@class">
<xsl:attribute name="class"><xsl:value-of select="."/> critical</xsl:attribute>
</xsl:template>
</xsl:stylesheet>