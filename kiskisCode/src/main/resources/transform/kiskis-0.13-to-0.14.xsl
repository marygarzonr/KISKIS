<?xml version="1.0" ?>

<!--
    Transforms the document version 0.13 to 0.14.
    
    $Id: kiskis-0.13-to-0.14.xsl,v 1.1 2005/02/04 13:46:30 tbuchloh Exp $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >
    
    <xsl:template match="//Group" >
        <Group>
            <xsl:apply-templates select="@*|node()" />        
            <Comment><xsl:value-of select="@comment" /></Comment>
        </Group>
    </xsl:template>
    
    <xsl:template match="//SecuredElement" >
        <SecuredElement>
            <xsl:apply-templates select="@*|node()" />        
            <Comment><xsl:value-of select="@comment" /></Comment>
        </SecuredElement>
    </xsl:template>
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    
</xsl:stylesheet>