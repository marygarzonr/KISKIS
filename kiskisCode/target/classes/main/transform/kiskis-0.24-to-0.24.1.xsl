<?xml version="1.0" ?>

<!-- Transforms the document version 0.24 to 0.24.1. $Id: kiskis-0.15-to-0.21.xsl,v 
	1.1 2007/03/11 22:14:15 tbuchloh Exp $ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">

	<xsl:template match="//TPMDocument">
		<xsl:element name="TPMDocument">
			<xsl:apply-templates select="@*|node()" />
			<xsl:element name="Attachments">
				<xsl:call-template name="move-attachments" />
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="//Attachment">
		<xsl:element name="AttachmentRef">
			<xsl:attribute name="attachmentUuid">
                <xsl:value-of select="@uuid" />
            </xsl:attribute>
		</xsl:element>
	</xsl:template>

	<xsl:template name="move-attachments">
		<xsl:for-each select="//Attachment">
			<xsl:element name="Attachment">
				<xsl:apply-templates select="@*" />
			</xsl:element>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
