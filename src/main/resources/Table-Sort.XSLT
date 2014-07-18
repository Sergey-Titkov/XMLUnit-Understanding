<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="ORDER_DOES_NOT_MATTER_LIST">
    <ul>
      <xsl:apply-templates select="ORDER_DOES_NOT_MATTER_ITEM">
        <xsl:sort select="NAME"/>
      </xsl:apply-templates>
    </ul>
  </xsl:template>

  <xsl:template match="ORDER_DOES_NOT_MATTER_ITEM">
      <li>
        <xsl:value-of select="NAME"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="CMNT"/>
      </li>
  </xsl:template>
</xsl:stylesheet>

