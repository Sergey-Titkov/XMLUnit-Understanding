<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output omit-xml-declaration="no" indent="yes"/>
  <!--
  xsl:strip-space имеет силу только над символами: пробел (#x20), возврат каретки (#xD), перевод строки (#xA), знак табуляции (#x9).
  -->
  <xsl:strip-space elements="*"/>

  <!--
  Данный шаблон демонстирует возможность сортировки с использованием XST, для того что бы была возможность выполнит сравнение с помощью XMLUnit.
  И что бы было удобно получать список различий
  -->
  <xsl:template match="OBJECT">
    <xsl:element name="OBJECT">
      <!-- Сортируем по имени узла  -->
      <xsl:apply-templates>
        <xsl:sort select="name()" data-type="text"/>
      </xsl:apply-templates>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ORDER_DOES_NOT_MATTER_LIST">
    <xsl:element name="ORDER_DOES_NOT_MATTER_LIST">
      <!-- Сортируем по содержимому узла с именем NAME  -->
      <xsl:apply-templates select="ORDER_DOES_NOT_MATTER_ITEM">
        <xsl:sort select="NAME" data-type="text"/>
      </xsl:apply-templates>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ORDER_DOES_NOT_MATTER_ITEM">
    <xsl:element name="ORDER_DOES_NOT_MATTER_ITEM">
      <!-- Не предумал как сделать с помощью xsl:apply-templates  -->
      <xsl:for-each select="*">
        <xsl:sort select="name()" data-type="text"/>
        <xsl:copy-of select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!--
  Этим шаблоном обрабатываем все, что не попало под выше описанные шаблоны.
   У этого шаблона самый низкий приоретет. Например: ORDER_AIS_MATTER_LIST
   -->

  <xsl:template match="*">
    <xsl:copy-of select="."/>
  </xsl:template>

</xsl:stylesheet>
