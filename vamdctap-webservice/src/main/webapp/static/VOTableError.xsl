<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:vot="http://www.ivoa.net/xml/VOTable/v1.2">
  
  <xsl:template match="/vot:VOTABLE">
    <html>
      <head>
        <title>Request errors</title>
      </head>
      <body>
        <h1>Request errors encountered</h1>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="vot:INFO[@value='ERROR']">
  		<p style="color:#ff0000">
          	<xsl:value-of select="."/>
        </p>
  </xsl:template>
  
  
  
</xsl:stylesheet>