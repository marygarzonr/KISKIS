<?xml version="1.0" ?>

<!--
	Transforms the document version 0.14 to 0.15.
	
	$Id: kiskis-0.14-to-0.15.xsl,v 1.1 2006/04/09 20:24:16 tbuchloh Exp $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	
	<xsl:template match="//AccountPropertyValue">
		<xsl:variable name="accountName" select="../@type" />
		<xsl:variable name="pos" select="position()" />
		<xsl:element name="AccountPropertyValue">
			<xsl:variable name="propName" select="@name" />
			<xsl:attribute name="name">
				<xsl:value-of select="@name" />
			</xsl:attribute>
			<!-- 
				here happens the magic. use the type from the first
				match in the template definition.
			-->
			<xsl:attribute name="type">
				<xsl:value-of
					select="//AccountType[@name=$accountName]//AccountProperty[@name=$propName]/@type" />
			</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:value-of select="@value" />
			</xsl:attribute>
		</xsl:element>
	</xsl:template>


	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>


</xsl:stylesheet>
