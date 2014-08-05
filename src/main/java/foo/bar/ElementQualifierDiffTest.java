package foo.bar;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.MatchTracker;
import org.custommonkey.xmlunit.NodeDetail;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class ElementQualifierDiffTest {

  private static String readResourceToString(String resourceName) throws IOException {
    String result = "";
    // Прочитать ресурс в строку, везде кодировка UTF-8
    try (BufferedReader bufferedReader = new BufferedReader(
      new FileReader(
        ElementQualifierDiffTest.class.getResource(resourceName).getPath()
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

    try {
      Diff diff = CompareTwoObjectXML.compareXML(etalonXML, template, testXML, template);
      // Надо получать type и name
      String testSute = CreateJUnitReport.createReport("type", "name", diff);

      // здесь пишем в файл
     try(FileWriter fileWriter = new FileWriter("./" + "OBJECT-TEST.xml")){
       fileWriter.write(testSute);
     }

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
