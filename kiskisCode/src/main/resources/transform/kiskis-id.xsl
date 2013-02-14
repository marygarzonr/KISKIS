<?xml version="1.0" ?>

<!--
	Transforms any document to itself, that means, that this stylesheet
	does nothing actually.
	
	$Id: kiskis-id.xsl,v 1.1 2004/10/03 08:59:40 tbuchloh Exp $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >

	
	<xsl:template match="/">
	  	<xsl:copy-of select="@*|node()" />
	</xsl:template>
	
</xsl:stylesheet>