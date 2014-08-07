package foo.bar;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
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
 * Описание
 *
 * @author Sergey.Titkov
 * @version 001.00
 * @since 001.00
 */
public class CreateJUnitReport {
  public static String createReport(String type, String name, Diff diff) throws ParserConfigurationException, TransformerException {
    String result = "";
    // Готовим отчет
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // Обрабатываем ноду CDATA как единое целое, а не как набор нод
    factory.setCoalescing(true);
    DocumentBuilder build = factory.newDocumentBuilder();
    Document doc = build.newDocument();

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
        Difference difference = (Difference) item;

        String fullDiff = ((Difference) item).toString();
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


    /*
try {
/*

Необходимо получить XML принимаемый Jenkins за XML от JUnit.
1. Сформировть XML если все совпало:
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
  <testsuite name="<Тип>.<Имя таблицы>" tests="1">
    <testcase classname="<Тип>.<Имя таблицы>" name="Имя теста"/>
  </testsuite>
Примеры:
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
  <testsuite name="TABLE.FFF" tests="1">
    <testcase classname="TABLE.FFF" name="JJJ"/>
  </testsuite>

<?xml version="1.0" encoding="UTF-8" standalone="no"?>
  <testsuite name="SEQUENCES.SC_SEQ" tests="1">
    <testcase classname="SEQUENCES.SC_SEQ" name="ZZZ"/>
  </testsuite>


<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<testsuites name="Имя тега корня: OBJECT, TABLE, etc">
  <testsuite name="Имя вехнего уровня /NAME" tests="1">
    <testcase name="Имя тега корня.Имя вехнего уровня"/>
  </testsuite>
</testsuites>

3. Сформировть XML если не совпало :
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
   <testsuite name="<Тип>.<Имя таблицы>" tests="<количество тестов>">
    <testcase classname="<Тип>.<Имя таблицы>" name="<Начальный текст>"/>
      <failure message="test failure" type="junit.framework.ComparisonFailure">
        <![CDATA[
        Полный текст сравнения, только в этих тегах!
        ]]>
       </failure>
    </testcase>


Примеры:
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
 <testsuite name="TABLE.SC_B2B_USERS" tests="3">
    <testcase classname="TABLE.SC_B2B_USERS" name="Expected text value '0003' but was '0002' - comparing">
       <failure message="test failure" type="junit.framework.ComparisonFailure">
       <![CDATA[
         Expected text value '0003' but was '00031' - comparing <NAME ...>0003</NAME> at /OBJECT[1]/ORDER_IS_MATTER_LIST[1]/ORDER_IS_MATTER_ITEM[3]/COL_LIST[1]/COL_LIST_ITEM[1]/NAME[1]/text()[1] to <NAME ...>00031</NAME> at /OBJECT[1]/ORDER_IS_MATTER_LIST[1]/ORDER_IS_MATTER_ITEM[3]/COL_LIST[1]/COL_LIST_ITEM[1]/NAME[1]/text()[1]
   ]]>
       </failure>
   </testcase>
    <testcase classname="TABLE.SC_B2B_USERS" name="Expected text value '0003' but was '0002' - comparing">
       <failure message="test failure" type="junit.framework.ComparisonFailure">
       <![CDATA[
          Expected text value '0003' but was '0002' - comparing <NAME ...>0003</NAME> at /OBJECT[1]/ORDER_IS_MATTER_LIST[1]/ORDER_IS_MATTER_ITEM[3]/COL_LIST[1]/COL_LIST_ITEM[1]/NAME[1]/text()[1] to <NAME ...>0002</NAME> at /OBJECT[1]/ORDER_IS_MATTER_LIST[1]/ORDER_IS_MATTER_ITEM[3]/COL_LIST[1]/COL_LIST_ITEM[1]/NAME[1]/text()[1]
   ]]>
       </failure>
   </testcase>
    <testcase classname="TABLE.SC_B2B_USERS" name="Expected text value '0002' but was '0003' - comparing">
       <failure message="test failure" type="junit.framework.ComparisonFailure">
       <![CDATA[
          Expected text value '0002' but was '0003' - comparing <NAME ...>0002</NAME> at /OBJECT[1]/ORDER_IS_MATTER_LIST[1]/ORDER_IS_MATTER_ITEM[3]/COL_LIST[1]/COL_LIST_ITEM[1]/NAME[2]/text()[1] to <NAME ...>0003</NAME> at /OBJECT[1]/ORDER_IS_MATTER_LIST[1]/ORDER_IS_MATTER_ITEM[3]/COL_LIST[1]/COL_LIST_ITEM[1]/NAME[2]/text()[1]
   ]]>
       </failure>
   </testcase>
  </testsuite>

 */
    /*
    Diff diff = CompareTwoObjectXML.compareXML(etalonXML, template,testXML,template );

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

    */
