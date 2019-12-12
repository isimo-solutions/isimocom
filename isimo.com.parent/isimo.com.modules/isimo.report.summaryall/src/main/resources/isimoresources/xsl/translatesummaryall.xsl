<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml"/>


 	<xsl:template match="span[@id]">
 	<xsl:variable name="id"><xsl:value-of select="./@id"/></xsl:variable>
 	
 		<xsl:choose>
	 		<xsl:when test="document('language/summaryallLanguage.xml')//span[@id=$id]">
	 			<xsl:copy><xsl:apply-templates select="document('language/summaryallLanguage.xml')//span[@id=$id]/node()|@*"/></xsl:copy>
	 		</xsl:when>
			<xsl:otherwise>
				<xsl:copy><xsl:apply-templates select="node()|@*"/></xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

 	<xsl:template match="node()|@*">
		<xsl:copy><xsl:apply-templates select="node()|@*"/></xsl:copy>
	</xsl:template>

</xsl:stylesheet>