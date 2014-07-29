package foo.bar;

import org.custommonkey.xmlunit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

public class ElementQualifierDiffTest {

  private static String readResourceToString(String resourceName) throws IOException {
      String result = "";
      // Прочитать ресурс в строку, везде кодировка UTF-8
      try (BufferedReader bufferedReader = new BufferedReader(new FileReader(ElementQualifierDiffTest.class.getResource(resourceName).getPath()))) {
        StringBuffer stringBuffer = new StringBuffer();
        String currentLine = "";
        while ((currentLine = bufferedReader.readLine()) != null) {
          stringBuffer.append(currentLine).append("\n");
        }
        result = stringBuffer.toString();
      }
    return result;
  }

  private static String printNode(Node node) {
    if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
      StringWriter sw = new StringWriter();
      try {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.transform(new DOMSource(node), new StreamResult(sw));
      } catch (TransformerException te) {
        System.out.println("nodeToString Transformer Exception");
      }
      return sw.toString();

    }
    return null;
  }

  public static void main(String[] args) {
    String template = "";
    String etalonXML = "";
    String testXML = "";

    try {
      etalonXML = readResourceToString("/Structurally-Etalon.XML");
      testXML = readResourceToString("/Structurally-Test.XML");
      template = readResourceToString("/Table-Sort.XSLT");
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    // Производим сортировку для эталонного XML.
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      StringWriter stringWriter = new StringWriter();
      Transformer transformer = transformerFactory
        .newTransformer(new javax.xml.transform.stream.StreamSource(new StringReader(template)));
      transformer.transform(
        new javax.xml.transform.stream.StreamSource(new StringReader(readResourceToString("/Structurally-Etalon.XML"))),
        new javax.xml.transform.stream.StreamResult(stringWriter)
      );
      etalonXML = stringWriter.toString();
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

    // Производим сортировку для тестового XML.
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      StringWriter stringWriter = new StringWriter();
      Transformer transformer = transformerFactory
        .newTransformer(new javax.xml.transform.stream.StreamSource(new StringReader(template)));
      transformer.transform(
        new javax.xml.transform.stream.StreamSource(new StringReader(testXML)),
        new javax.xml.transform.stream.StreamResult(stringWriter)
      );
      testXML = stringWriter.toString();
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    }


    XMLUnit.setIgnoreComments(Boolean.TRUE);
    XMLUnit.setIgnoreWhitespace(Boolean.TRUE);
    XMLUnit.setNormalizeWhitespace(Boolean.TRUE);
    XMLUnit.setIgnoreDiffBetweenTextAndCDATA(Boolean.TRUE);
    XMLUnit.setIgnoreAttributeOrder(Boolean.TRUE);
    XMLUnit.setCompareUnmatched(Boolean.FALSE);
/*

// здесь создаем документ
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
DocumentBuilder build = factory.newDocumentBuilder();
Document doc = build.newDocument();

Element rootElement = doc.createElement("root");
rootElement.setAttribute("atr_name", "bla-bla-bla");
Element body = doc.createElement("body");
Text textNode = doc.createTextNode(value);
body.appendChild(textNode);
rootElement.appendChild(body);
doc.appendChild(rootElement);

// здесь пишем в файл
Transformer t = TransformerFactory.newInstance().newTransformer();
t.setOutputProperty(OutputKeys.INDENT, "yes");
t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(PATH + "tempXML.xml")));

 */

    try {
      // Готовим отчет
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder build = factory.newDocumentBuilder();
      Document doc = build.newDocument();

      Element testsuites = doc.createElement("testsuites");
      testsuites.setAttribute("name", "OBJECT");
      doc.appendChild(testsuites);

      Element testsuite = doc.createElement("testsuite");
      testsuite.setAttribute("tests", "0");
      testsuite.setAttribute("name", "NAME");
      testsuites.appendChild(testsuite);
/*

Необходимо получить XML принимаемый Jenkinsom за XML от JUnit.
1. Сформировть XML если все совпало:
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<testsuites name="Имя тега корня: OBJECT, TABLE, etc">
  <testsuite name="Имя вехнего уровня /NAME" tests="1">
    <testcase name="Имя тега корня.Имя вехнего уровня"/>
  </testsuite>
</testsuites>

2. Проверить как это переварил Jenkins

3. Сформировть XML если не совпало :
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<testsuites name="Имя тега корня: OBJECT, TABLE, etc">
  <testsuite name="Имя вехнего уровня /NAME" tests="1">
    <testcase name="Difference+Index">
       <failure message="test failure">
         difference вывести как есть.

       </failure>
   </testcase>

  </testsuite>
</testsuites>

4. Проверить как это переварил Jenkins

 */
      Diff diff = new Diff(new StringReader(etalonXML), new StringReader(testXML));
      System.out.println("Similar? " + diff.similar());
      System.out.println("Identical? " + diff.identical());
      if (diff.identical()){
        Element testcase = doc.createElement("testcase");
        testcase.setAttribute("name", "OBJECT.NAME.Имя");
        testsuite.appendChild(testcase);
        testsuite.setAttribute("tests", "1");
      }

      DetailedDiff detDiff = new DetailedDiff(diff);
      List differences = detDiff.getAllDifferences();
      for (Object object : differences) {
        Difference difference = (Difference) object;
        System.out.println("####################");
        System.out.println(difference);
        System.out.println("####################");
      }
      // здесь пишем в файл
      Transformer t = TransformerFactory.newInstance().newTransformer();
      t.setOutputProperty(OutputKeys.INDENT, "yes");
      t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("./" + "OBJECT-TEST.xml")));

/*
Вот xsd схема того как Jenkins обрабатывает XML от JUnit:
https://svn.jenkins-ci.org/trunk/hudson/dtkit/dtkit-format/dtkit-junit-model/src/main/resources/com/thalesgroup/dtkit/junit/model/xsd/junit-4.xsd

Исходя из этого получается:

<?xml version="1.0" encoding="UTF-8"?>
<testsuite
           tests="Сколько тестов"
           package="OBJECT" <- TABLE, SEQ и прочее
           name="Из тега NAME"
>

   <testcase name="Имя теста, приедся как то гененрить, посмотреть что есть в difference"> </testcase>
   <testcase name="Это провальный тест, у нас походу всегда провальные?">
     <failure message="test failure">Тут длинное описание то, что не совпало.
</failure>

   </testcase>
</testsuite>

Необходимо реализовать

 */
              /*
        if (difference != null) {
          NodeDetail controlNode = difference.getControlNodeDetail();
          NodeDetail testNode = difference.getTestNodeDetail();
          String controlNodeValue = printNode(controlNode.getNode());
          String testNodeValue = printNode(testNode.getNode());
          if (controlNodeValue != null) {
            System.out.println("####################");
            System.out.println("Control Node: " + controlNodeValue);
          }
          if (testNodeValue != null) {
            System.out.println("Test Node: " + testNodeValue);
            System.out.println("####################");
          }

         */



      /*
      DetailedDiff detDiff = new DetailedDiff(diff);
      detDiff.overrideMatchTracker(new MatchTrackerImpl());
      detDiff.overrideElementQualifier(new ElementNameQualifier());
      detDiff.getAllDifferences();
*/

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
    }
  }

}

class MatchTrackerImpl implements MatchTracker {

  public void matchFound(Difference difference) {
    if (difference != null) {
      NodeDetail controlNode = difference.getControlNodeDetail();
      NodeDetail testNode = difference.getTestNodeDetail();

      String controlNodeValue = printNode(controlNode.getNode());
      String testNodeValue = printNode(testNode.getNode());

      if (controlNodeValue != null) {
        System.out.println("####################");
        System.out.println("Control Node: " + controlNodeValue);
      }
      if (testNodeValue != null) {
        System.out.println("Test Node: " + testNodeValue);
        System.out.println("####################");
      }
    }
  }

  private static String printNode(Node node) {
    if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
      StringWriter sw = new StringWriter();
      try {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.transform(new DOMSource(node), new StreamResult(sw));
      } catch (TransformerException te) {
        System.out.println("nodeToString Transformer Exception");
      }
      return sw.toString();

    }
    return null;
  }
}
