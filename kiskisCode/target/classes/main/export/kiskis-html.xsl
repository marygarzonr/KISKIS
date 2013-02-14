<?xml version="1.0" ?>

<!-- Transforms any document to a XHTML-representation of itself. -->
<!-- $Id: kiskis-html.xsl,v 1.2 2006/04/16 17:37:52 tbuchloh Exp $ -->
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:k="http://kiskis.sourceforge.net/model/v1.0/">

  <!-- Der Einstiegspunkt -->
  <xsl:template
    match="/">
    <html
      xml:lang="en"
      lang="en">
      <head>
        <title>KisKis</title>
        <style>
          .small { font-size: small; }
          table { width: 100%; }
          th { color:
          white; background-color: #000099; text-align: center }
          tbody {
          background-color: lightgray }
          tfooter { border: 1px line; }
                </style>
      </head>
      <body>
        <h1>Keep It Secret! Keep It Safe!</h1>
        <xsl:apply-templates />
        <p
          class="small">
          Generated with KisKis - Keep It Secret! Keep It Safe!
          <br />
          <a
            href="http://kiskis.sourceforge.net">http://kiskis.sourceforge.net</a>
          <br />
          Copyright by Tobias Buchloh 2004 - 2013
        </p>
      </body>
    </html>
  </xsl:template>

  <xsl:template
    match="k:Group"
    mode="GroupPath">
    <xsl:if
      test="parent::k:Group">
      <xsl:apply-templates
        mode="GroupPath"
        select=".." />
    </xsl:if>
    <xsl:element
      name="a">
      <xsl:attribute
        name="href">#<xsl:value-of
        select="@name" /></xsl:attribute>
      <xsl:value-of
        select="@name" />
    </xsl:element>
    /
  </xsl:template>

  <xsl:template
    match="k:Group">
    <h2>
      <xsl:apply-templates
        mode="GroupPath"
        select="." />
    </h2>

    <xsl:if
      test="./*/k:SecuredElement">
      <table>
        <thead>
          <tr>
            <th>Label</th>
            <th>Password</th>
            <th>Created</th>
            <th>Last Changed</th>
            <th>Last Viewed</th>
            <th>View Count</th>
            <th>Expires On</th>
            <th>Archived On</th>
            <th>Comment</th>
            <th>Username</th>
            <th>URL</th>
            <th>E-Mail</th>
          </tr>
        </thead>
        <tbody>
          <xsl:for-each
            select="./*/k:SecuredElement">
            <tr>
              <td>
                <xsl:value-of
                  select="@name" />
              </td>

              <td>
                <xsl:value-of
                  select="k:Password/@pwd" />
              </td>
              <td>
                <xsl:value-of
                  select="@creationDate" />
              </td>
              <td>
                <xsl:value-of
                  select="@lastChangeDate" />
              </td>
              <td>
                <xsl:value-of
                  select="@lastViewedDate" />
              </td>
              <td>
                <xsl:value-of
                  select="@viewCounter" />
              </td>
              <td>
                <xsl:value-of
                  select="k:Password[../@expiresNever='false']/@expires" />
              </td>
              <td>
                <xsl:value-of
                  select="@archivedOnDate" />
              </td>
              <td>
                <xsl:value-of
                  select="k:Comment/text()" />
              </td>
              <td>
                <xsl:value-of
                  select="../@username" />
              </td>
              <td>
                <xsl:value-of
                  select="../@url" />
              </td>
              <td>
                <xsl:value-of
                  select="../@email" />
              </td>
            </tr>
          </xsl:for-each>
        </tbody>
        <tfooter>
          <xsl:value-of
            select="k:Comment/text()" />
        </tfooter>
      </table>
    </xsl:if>

    <xsl:apply-templates />

  </xsl:template>

  <!-- Ignoriert den Rest an text()-Knoten -->
  <xsl:template
    match="text()" />

</xsl:stylesheet>