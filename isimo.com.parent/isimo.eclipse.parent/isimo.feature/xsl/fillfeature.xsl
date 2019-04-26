<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"  xmlns="http://maven.apache.org/POM/4.0.0"  xmlns:pom="http://maven.apache.org/POM/4.0.0">
   <xsl:param name="browser"/>
   
   <xsl:template match="node()|@*">
   	   <xsl:copy><xsl:apply-templates select="node()|@*"/></xsl:copy>
   </xsl:template>
   
   <xsl:template match="feature">
   	   <xsl:copy><xsl:apply-templates select="node()[name()!='plugin']|@*"/>
   	   <xsl:apply-templates select="//requires/import" mode="pluginsfromimports"/>
   	   </xsl:copy>
   </xsl:template>
   
   <xsl:template match="import" mode="pluginsfromimports">
       <plugin
         id="{@plugin}"
         download-size="0"
         install-size="0"
         version="0.0.0"
         unpack="false"/>
   </xsl:template>
   
</xsl:stylesheet>