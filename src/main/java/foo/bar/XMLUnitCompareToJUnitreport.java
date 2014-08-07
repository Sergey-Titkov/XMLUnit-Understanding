package foo.bar;

import org.custommonkey.xmlunit.Diff;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;

public class XMLUnitCompareToJUnitreport {

  private static String readResourceToString(String resourceName) throws IOException {
    String result = "";
    // Прочитать ресурс в строку, везде кодировка UTF-8
    try (BufferedReader bufferedReader = new BufferedReader(
      new FileReader(
        XMLUnitCompareToJUnitreport.class.getResource(resourceName).getPath()
      )
    )) {
      StringBuffer stringBuffer = new StringBuffer();
      String currentLine = "";
      while ((currentLine = bufferedReader.readLine()) != null) {
        stringBuffer.append(currentLine).append("\n");
      }
      result = stringBuffer.toString();
    }
    return result;
  }

  public static void main(String[] args) {
    // Что должно получиться? И проверить, что jenkins корретно все подцепил
    // Один объект с небольшими различиями
    // Один с большими различиями.

    try {
      // XML полностью совпадают.
      compareTwoXML("/SameStructure-Etalon.XML","/SameStructure-Test.XML", "/Table-Sort.XSLT" );

      // XML содержит одно различие.
      compareTwoXML("/OneDiff-Etalon.XML","/OneDiff-Test.XML", "/Table-Sort.XSLT" );

      // XML содержит множественные различия.
      compareTwoXML("/MultiDiff-Etalon.XML","/MultiDiff-Test.XML", "/Table-Sort.XSLT" );

      // XML содержит различия в структуре.

    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (TransformerException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (XPathExpressionException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

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

