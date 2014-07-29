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
  <xsl:template match="NAME">
    <xsl:value-of select="." />
  </xsl:template>


  <!--
  Этим шаблоном обрабатываем все, что не попало под выше описанные шаблоны.
   У этого шаблона самый низкий приоретет. Например: ORDER_AIS_MATTER_LIST
   -->


</xsl:stylesheet>
