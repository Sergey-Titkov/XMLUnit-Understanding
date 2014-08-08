package foo.bar;

import org.custommonkey.xmlunit.Diff;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;

/**
 * Демонстрация работы классов.
 */
public class XMLUnitCompareToJUnitreport {
  /**
   * Читаем файл из ресурсов приложения. Не забыть добавить / по вскусу.
   *
   * @param resourceName Имя файла.
   * @return Содержимое в виде строки
   * @throws IOException
   */
  private static String readResourceToString(String resourceName) throws IOException {
    String result;
    // Прочитать ресурс в строку, везде кодировка UTF-8
    try (BufferedReader bufferedReader = new BufferedReader(
      new FileReader(
        XMLUnitCompareToJUnitreport.class.getResource(resourceName).getPath()
      )
    )) {
      StringBuilder stringBuilder = new StringBuilder();
      String currentLine;
      while ((currentLine = bufferedReader.readLine()) != null) {
        stringBuilder.append(currentLine).append("\n");
      }
      result = stringBuilder.toString();
    }
    return result;
  }

  /**
   * :)
   *
   * @param args Это и так понятно.
   */
  public static void main(String[] args) {

    try {
      // XML полностью совпадают.
      compareTwoXML("/SameStructure-Etalon.XML", "/SameStructure-Test.XML", "/Table-Sort.XSLT");

      // XML содержит одно различие.
      compareTwoXML("/OneDiff-Etalon.XML", "/OneDiff-Test.XML", "/Table-Sort.XSLT");

      // XML содержит множественные различия.
      compareTwoXML("/MultiDiff-Etalon.XML", "/MultiDiff-Test.XML", "/Table-Sort.XSLT");

      // XML содержит различия в структуре.

    } catch (SAXException|IOException|ParserConfigurationException|TransformerException|XPathExpressionException e) {
      e.printStackTrace();
    }
  }

  /**
   * Сравнивает два XML, предварительно применив к ним шаблон XSLT.
   *
   * @param nameEtalonXML Имя файла с эталонным XML
   * @param nameTestXML   Имя файла с тестовым XML
   * @param nameTemplate  Имя файла с шаблоном XSLT
   * @throws IOException
   * @throws TransformerException
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws XPathExpressionException
   */
  private static void compareTwoXML(
    String nameEtalonXML,
    String nameTestXML,
    String nameTemplate
  ) throws IOException, TransformerException, ParserConfigurationException, SAXException, XPathExpressionException {

    String etalonXML;
    String testXML;
    String template;

    etalonXML = readResourceToString(nameEtalonXML);
    testXML = readResourceToString(nameTestXML);
    template = readResourceToString(nameTemplate);

    Diff diff = CompareTwoObjectXML.compareXML(etalonXML, template, testXML, template);

    // А вот это нужно, что бы получить наименование корневой ноды в XML и имя объекта. Проще не придумал.
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
      new InputSource(
        new StringReader(
          etalonXML
        )
      )
    );

    XPathFactory xpathFact = XPathFactory.newInstance();
    XPath xpath = xpathFact.newXPath();
    // Имя корневого тега
    String xpathStr = "name(/*)";
    String typeObject = xpath.evaluate(xpathStr, doc);

    // Имя объекта
    xpathStr = "/" + typeObject + "/NAME/text()";
    String nameObject = xpath.evaluate(xpathStr, doc);

    String testSute = CreateJUnitReport.createReport(typeObject, nameObject, diff);

    // здесь пишем в файл
    try (FileWriter fileWriter = new FileWriter("./" + nameObject.toUpperCase() + "-TESTS.xml")) {
      fileWriter.write(testSute);
    }
  }

}

