package foo.bar;

import org.custommonkey.xmlunit.*;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

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

    try {
      Diff diff = new Diff(new StringReader(etalonXML), new StringReader(testXML));
      System.out.println("Similar? " + diff.similar());
      System.out.println("Identical? " + diff.identical());


      DetailedDiff detDiff = new DetailedDiff(diff);
      List differences = detDiff.getAllDifferences();
      for (Object object : differences) {
        Difference difference = (Difference) object;
        System.out.println("***********************");
        System.out.println(difference);
        System.out.println("***********************");
      }

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
