import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Задача класса сравнить два XML с использованием фреймворка XMLUnit.
 *
 * @author Sergey.Titkov
 * @version 001.00
 * @since 001.00
 */
public class CompareTwoObjectXML {
  /**
   * Применям указанный шаблон к XML. Если шаблон не задан, будет возвращен исходный XML.
   *
   * @param XML Исходный XML
   * @param template  XSLT шаблоно
   * @return Результирующий XML
   * @throws TransformerException
   */
  private static String applyXSLTTemplate(String XML, String template) throws TransformerException {
    String result;
    if (template == null || template.equals("")) {
      result = XML;
    } else {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      StringWriter stringWriter = new StringWriter();
      Transformer transformer = transformerFactory
        .newTransformer(new javax.xml.transform.stream.StreamSource(new StringReader(template)));
      transformer.transform(
        new javax.xml.transform.stream.StreamSource(new StringReader(XML)),
        new javax.xml.transform.stream.StreamResult(stringWriter)
      );
      result = stringWriter.toString();
    }
    return result;

  }

  /**
   * Метод выполняет сравнение двух XML объектов, предварительно применив к кажддому шаблон.
   * Шаблон необходимо применить в том случае, если необходима сортировка нод по некоторому признаку или если необходимо
   * сравнить только часть XML.
   * В результате возвращается объект содержищий коллекцию различий.
   * Сравнение выполняется наиболее "мягким способом", n.е игнорируем все что можно: комментарии, много пробелов, ведущие пробелы.
   *
   * @param etalonXML Эталонный XML
   * @param etalonTemplate Шаблон для обработки эталонного XML
   * @param testXML Кого будем сравнивать с эталоном
   * @param testTemplate  Шаблон для обработки тестового XML
   * @return Набор различий между двумя XML
   */
  public static Diff compareXML(String etalonXML, String etalonTemplate, String testXML, String testTemplate) throws TransformerException, ParserConfigurationException, IOException, SAXException {
    // Применям к эталонному XML шаблон преобразования.
    String _etalonXML = applyXSLTTemplate(etalonXML, etalonTemplate);

    // Применям к тестовому XML шаблон преобразования.
    String _testXML = applyXSLTTemplate(testXML, testTemplate);

    // Правильно все заигнорили.
    XMLUnit.setIgnoreComments(Boolean.TRUE);
    XMLUnit.setIgnoreWhitespace(Boolean.TRUE);
    XMLUnit.setNormalizeWhitespace(Boolean.TRUE);
    XMLUnit.setIgnoreDiffBetweenTextAndCDATA(Boolean.TRUE);
    XMLUnit.setIgnoreAttributeOrder(Boolean.TRUE);
    XMLUnit.setCompareUnmatched(Boolean.FALSE);

    return  new Diff(new StringReader(_etalonXML), new StringReader(_testXML));
  }
}
