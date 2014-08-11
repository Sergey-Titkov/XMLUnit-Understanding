import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;

/**
 * Класc формирующий результаты сравнения двух XML в виде отчета JUnit.
 *
 * @author Sergey.Titkov
 * @version 001.00
 * @since 001.00
 */
public class CreateJUnitReport {
  /**
   * На основе коллекции различий выданных XMLUnit формирует очтет об различиях в виде JUnit.
   * Формат отчета JUnit следующий:
   * Успешное сравнение:
   *   <?xml version="1.0" encoding="UTF-8" standalone="no"?>
   *     <testsuite name="<type>.<name>" tests="1">
   *     <testcase classname="<type>.<name>" name=<name> + " structure""/>
   *   </testsuite>
   *
   * Примеры:
   *   <?xml version="1.0" encoding="UTF-8" standalone="no"?>
   *     <testsuite name="OBJECT.USER_TABLE_SAME" tests="1">
   *     <testcase classname="OBJECT.USER_TABLE_SAME" name="USER_TABLE_SAME structure"/>
   *   </testsuite>
   *
   * В том случае если есть различия:
   *   <?xml version="1.0" encoding="UTF-8" standalone="no"?>
   *     <testsuite name="<type>.<name>" tests="1">
   *       <testcase classname="<type>.<name>" name="<Краткое описание ошибки сравнения>"</>">
   *         <failure message="test failure" type="junit.framework.ComparisonFailure">
   *           <![CDATA["<Полное описание ошибки сравнения>"]]>
   *         </failure>
   *       </testcase>
   *     </testsuite>
   *
   * Пример:
   *   <?xml version="1.0" encoding="UTF-8" standalone="no"?>
   *     <testsuite name="OBJECT.USER_TABLE_ONE_DIFF" tests="1">
   *       <testcase classname="OBJECT.USER_TABLE_ONE_DIFF" name="Expected text value 'Это третий элемент' but was 'Это описание совершенно не верно' - comparing">
   *         <failure message="test failure" type="junit.framework.ComparisonFailure">
   *           <![CDATA[Expected text value 'Это третий элемент' but was 'Это описание совершенно не верно' - comparing <CMNT ...>Это третий элемент</CMNT> at /OBJECT[1]/ORDER_DOES_NOT_MATTER_LIST[1]/ORDER_DOES_NOT_MATTER_ITEM[3]/CMNT[1]/text()[1] to <CMNT ...>Это описание совершенно не верно</CMNT> at /OBJECT[1]/ORDER_DOES_NOT_MATTER_LIST[1]/ORDER_DOES_NOT_MATTER_ITEM[3]/CMNT[1]/text()[1]]]>
   *         </failure>
   *       </testcase>
   *     </testsuite>
   *
   * @param type Имя типа, используется в описаннии имени теста. Аналог имени пакета в JAVA.
   * @param name Имя сущности, используется в описаннии имени теста. Аналог имени класса в JAVA.
   * @param diff Список различий
   * @return XML в формате JUnit
   * @throws ParserConfigurationException
   * @throws TransformerException
   */
  public static String createReport(String type, String name, Diff diff) throws ParserConfigurationException, TransformerException {
    String result;
    // Готовим отчет
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // Обрабатываем ноду CDATA как единое целое, а не как набор нод
    factory.setCoalescing(true);
    DocumentBuilder build = factory.newDocumentBuilder();
    Document doc = build.newDocument();

    // Формат жосткий.
    Element testsuite = doc.createElement("testsuite");
    String className = type.toUpperCase() + "." + name.toUpperCase();
    testsuite.setAttribute("name", className);
    testsuite.setAttribute("tests", "1");
    doc.appendChild(testsuite);

    // Все очень хорошо :) Создаем один тест, и он успешен.
    if (diff.identical()) {
      Element testcase = doc.createElement("testcase");
      testcase.setAttribute("classname", className);
      testcase.setAttribute("name", name.toUpperCase() + " structure");
      testsuite.appendChild(testcase);
    } else {
      // Формируем весь пакет различий
      DetailedDiff detDiff = new DetailedDiff(diff);

      List differences = detDiff.getAllDifferences();
      for (Object item : differences) {

        String fullDiff = item.toString();
        String marker = "- comparing";
        String shortDiff = fullDiff.substring(0,fullDiff.indexOf(marker)+marker.length());

        Element testcase = doc.createElement("testcase");
        testcase.setAttribute("classname", className);
        testcase.setAttribute("name", shortDiff);

        Element failure = doc.createElement("failure");
        failure.setAttribute("message", "test failure");
        failure.setAttribute("type", "junit.framework.ComparisonFailure");
        CDATASection section = doc.createCDATASection(fullDiff);
        failure.appendChild(section);
        testcase.appendChild(failure);
        testsuite.appendChild(testcase);
      }
    }
    // Получаем свыше фабрику для трансформации
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    // Тут уже и трансформер подошол.
    Transformer transformer = transformerFactory.newTransformer();
    // Настрили параметры для трансформации
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

    // Это я не понимаю.
    Source domSource = new DOMSource(doc);
    // А это куда мы перельем результат, в нашем случае это файловый поток.
    StringWriter stringWriter = new StringWriter();
    Result stringResult = new StreamResult(stringWriter);
    // И получаем на выходе красиво оформленный XML
    transformer.transform(domSource, stringResult);
    result = stringWriter.toString();

    return result;
  }
}
