<?xml version="1.0" ?>

<!--
    Transforms the document version 0.11 to 0.12.
    
    $Id: kiskis-0.11-to-0.12.xsl,v 1.3 2005/02/04 13:46:30 tbuchloh Exp $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >
    
    <xsl:template match="SecuredElement" >
        <SecuredElement>
            <xsl:copy-of select="@*" />
            <xsl:attribute name="lastViewedDate">
                <xsl:value-of select="@lastChangeDate" />
            </xsl:attribute>
            <xsl:attribute name="viewCounter" >1</xsl:attribute>
            <xsl:apply-templates />
        </SecuredElement>
    </xsl:template>
    
    <xsl:template match="CreditCard" >
        <CreditCard>
            <xsl:copy-of select="@*" />
            <xsl:attribute name="pin" ></xsl:attribute>
            <xsl:apply-templates />
        </CreditCard>
    </xsl:template>
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>
