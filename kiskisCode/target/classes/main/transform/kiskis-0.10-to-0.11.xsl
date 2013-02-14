<?xml version="1.0" ?>

<!--
	Transforms the document versions <= 0.10 to 0.11.
	
	$Id: kiskis-0.10-to-0.11.xsl,v 1.1 2004/10/03 08:59:40 tbuchloh Exp $
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >

	<xsl:template match="de.tbuchloh.kiskis.model.TPMDocument" >
		<TPMDocument>
			<xsl:attribute name="version" >0.11</xsl:attribute>
			<xsl:apply-templates />
		</TPMDocument>
	</xsl:template>
	
	<xsl:template match="de.tbuchloh.kiskis.model.Group" >
		<Group>
		  	<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</Group>
	</xsl:template>
	
	<xsl:template match="de.tbuchloh.kiskis.model.BankAccount" >
		<BankAccount>
		  	<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</BankAccount>
	</xsl:template>
	
	<xsl:template match="de.tbuchloh.kiskis.model.NetAccount" >
		<NetAccount>
		  	<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</NetAccount>
	</xsl:template>
	
	<xsl:template match="de.tbuchloh.kiskis.model.CreditCard" >
		<CreditCard>
		  	<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</CreditCard>
	</xsl:template>
	
	<xsl:template match="de.tbuchloh.kiskis.model.SecuredFile" >
		<SecuredFile>
		  	<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</SecuredFile>
	</xsl:template>
	
	<xsl:template match="de.tbuchloh.kiskis.model.TAN" >
		<TAN>
		  	<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</TAN>
	</xsl:template>
	
	<xsl:template match="de.tbuchloh.kiskis.model.TANList" >
		<TANList>
		  	<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</TANList>
	</xsl:template>
	
	<xsl:template match="@*|node()">
	  	<xsl:copy-of select="." />
	</xsl:template>
	
</xsl:stylesheet>