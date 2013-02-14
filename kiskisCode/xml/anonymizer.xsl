<?xml version="1.0" ?>
<!--
    Transforms the document version 0.11 to 0.12.
    
    $Id: anonymizer.xsl,v 1.2 2005/01/06 20:11:39 tbuchloh Exp $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	  <xsl:output indent="yes" doctype-system="kiskis-0.12.dtd" />
	  <xsl:variable name="source" >0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789</xsl:variable>
	  <xsl:template match="node()">
	  		<xsl:if test="string-length(local-name(.)) &gt; 0" >
		    <xsl:element name="{local-name(.)}">
			      <xsl:for-each select="@*">
				        <xsl:attribute name="{local-name(.)}">
				            <xsl:value-of select="substring($source, 1, string-length(string(.)))" />
				        </xsl:attribute>
			      </xsl:for-each>
			      <xsl:apply-templates />
		    </xsl:element>
		    </xsl:if>
	  </xsl:template>
	  
</xsl:stylesheet>

