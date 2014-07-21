<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output omit-xml-declaration="no" indent="yes"/>

<!--
Данный шаблон демонстирует возможность сортировки с использованием XST, для с наиболее удобного сравнения с помощью XMLUnit.
-->
  <xsl:template match="/">
    <xsl:for-each select="/OBJECT/ORDER_DOES_NOT_MATTER_LIST/ORDER_DOES_NOT_MATTER_ITEM">
      <xsl:sort select="NAME" data-type="text"/>

      <xsl:variable name="name_node" select="name()"/>
      <xsl:element name="{$name_node}">
        <!--
        Сортировка по именам тегов.
        -->
        <xsl:for-each select="*">
          <xsl:sort select="name()" data-type="text"/>
          <xsl:copy-of select="."/>
        </xsl:for-each>
      </xsl:element>



    </xsl:for-each>
  </xsl:template>
  <!--

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
    -->
</xsl:stylesheet>


  <!-- Вывод и сортировка подготовленных данных -->
  <!--

<xsl:apply-templates select="xalan:nodeset($prepared-data)/*" mode="sort">
<xsl:sort order="{$order}" select="@sort-value" data-type="text"/>
<xsl:with-param name="sort-order" select="$order"/>
</xsl:apply-templates>


-->