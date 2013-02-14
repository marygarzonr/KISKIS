<?xml version="1.0" ?>

<!-- Transforms the document version 0.21 to 0.24. $Id: kiskis-0.15-to-0.21.xsl,v 
	1.1 2007/03/11 22:14:15 tbuchloh Exp $ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">

	<xsl:template match="//TPMDocument">
		<xsl:element name="TPMDocument">
			<xsl:attribute name="uuid">
                <xsl:value-of select="generate-id(.)" />
            </xsl:attribute>
			<xsl:apply-templates select="@*|node()" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="//Group">
		<xsl:element name="Group">
			<xsl:attribute name="uuid">
                <xsl:value-of select="generate-id(.)" />
            </xsl:attribute>
			<xsl:apply-templates select="@*|node()" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="//SecuredElement">
		<xsl:element name="SecuredElement">
			<xsl:attribute name="uuid">
                <xsl:value-of select="generate-id(.)" />
            </xsl:attribute>
			<xsl:apply-templates select="@*|node()" />
		</xsl:element>
	</xsl:template>
	
   <xsl:template match="//CreditCard">
        <xsl:element name="CreditCard">
            <xsl:attribute name="cardValidationCode">
            </xsl:attribute>
            <xsl:apply-templates select="@*|node()" />
        </xsl:element>
    </xsl:template>

	<xsl:template match="//Attachment">
		<xsl:element name="Attachment">
			<xsl:attribute name="uuid">
                <xsl:value-of select="generate-id(.)" />
            </xsl:attribute>
			<xsl:apply-templates select="@*|node()" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>


</xsl:stylesheet>
