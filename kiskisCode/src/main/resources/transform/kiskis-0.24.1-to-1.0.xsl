<?xml version="1.0" ?>

<!-- Transforms the document version 0.24.1 to 1.0. $Id: kiskis-0.15-to-0.21.xsl,v 
	1.1 2007/03/11 22:14:15 tbuchloh Exp $ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	xmlns:xml="http://www.w3.org/XML/1998/namespace"
	xmlns="http://kiskis.sourceforge.net/model/v1.0/">

    <xsl:template match="//Comment">
        <xsl:element name="Comment" namespace="http://kiskis.sourceforge.net/model/v1.0/">
            <xsl:attribute name="xml:space"
                namespace="http://www.w3.org/XML/1998/namespace">preserve</xsl:attribute>
            <xsl:apply-templates select="@*|node()" />
        </xsl:element>
    </xsl:template>
    
	<xsl:template match="//TPMDocument">
		<xsl:element name="TPMDocument"
			namespace="http://kiskis.sourceforge.net/model/v1.0/">
			<xsl:apply-templates select="@*|node()" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
