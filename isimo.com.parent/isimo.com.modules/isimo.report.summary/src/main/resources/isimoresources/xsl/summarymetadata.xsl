<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output omit-xml-declaration="yes"/>
<xsl:variable name="firsttestsuite" select="/summary/testcase[1]/testsuite"/>
<xsl:template match="@*|node()"><xsl:copy><xsl:apply-templates select="@*|node()"/></xsl:copy></xsl:template>

<xsl:template match="summary">
<xsl:copy>
<suiteproperties>
	<browser><xsl:value-of select="$firsttestsuite/properties/property[@name='isimo.browser']/@value"/></browser>
	<environment><xsl:value-of select="$firsttestsuite/properties/property[@name='env']/@value"/></environment>
	<module><xsl:value-of select="$firsttestsuite/properties/property[@name='module']/@value"/></module>
	<hostname><xsl:value-of select="$firsttestsuite/@hostname"/></hostname>
</suiteproperties>
<failures>
	<xsl:apply-templates select=".//system-out//failure"/>
</failures>
<xsl:apply-templates select="@*|node()"/></xsl:copy>
</xsl:template>
</xsl:stylesheet>
