<?xml version="1.0" ?>

<!--
    Transforms the document version 0.15 to 0.21.

    $Id: kiskis-0.15-to-0.21.xsl,v 1.1 2007/03/11 22:14:15 tbuchloh Exp $
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:template match="//TANList">
        <xsl:element name="TANList">
            <xsl:apply-templates select="@*" />
            <xsl:for-each select="TAN">
                <xsl:variable
                    name="pos"
                    select="position()" />

                <xsl:element name="TAN">
                    <xsl:attribute name="id">
                        <xsl:value-of select="$pos" />
                    </xsl:attribute>
                    <xsl:apply-templates select="@*|node()" />
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template match="//Group[not(@comment)]">
        <xsl:element name="Group">
            <xsl:attribute name="comment">
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
