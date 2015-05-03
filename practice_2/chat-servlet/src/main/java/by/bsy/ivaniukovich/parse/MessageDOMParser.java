package by.bsy.ivaniukovich.parse;

import by.bsy.ivaniukovich.model.Message;
import org.w3c.dom.*;

import by.bsy.ivaniukovich.model.MessageStorage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Hope on 4/24/2015.
 */
public class MessageDOMParser {
    private String filePath = "d:\\Work\\Study\\part2\\practice_2\\chat-servlet\\history.xml";

    private static Boolean lock = Boolean.TRUE;

    public void write(Message message) throws  Exception {
        synchronized (lock) {
            MessageStorage.addMessage(message);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filePath);

            Node messages = doc.getFirstChild();
            Element messageNode = doc.createElement("message");

            messageNode.setAttribute("id", message.getId());
            Element author = doc.createElement("author");
            author.appendChild(doc.createTextNode(message.getAuthor()));
            messageNode.appendChild(author);
            Element text = doc.createElement("text");
            text.appendChild(doc.createTextNode(message.getText()));
            messageNode.appendChild(text);
            Element date = doc.createElement("date");
            date.appendChild(doc.createTextNode(message.getDate()));
            messageNode.appendChild(date);

            messages.appendChild(messageNode);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
        }
    }

    public void deleteMessage(Message message) throws  Exception {
        MessageStorage.deleteMessage(message);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new FileInputStream(filePath));

        XPath xPath =  XPathFactory.newInstance().newXPath();
        String expression="//message[@id='"+message.getId()+"']";
        Node node = (Node) xPath.compile(expression).evaluate(document, XPathConstants.NODE);
        if(node != null){
            Node messages = document.getFirstChild();
            messages.removeChild(node);
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);

    }
    public void parse() throws  Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(filePath));
        NodeList nodeList = document.getElementsByTagName("message");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Message message = new Message();
                Element eElement = (Element) node;

                message.setId(eElement.getAttribute("id"));
                message.setAuthor(eElement.getElementsByTagName("author").item(0).getTextContent());
                message.setText(eElement.getElementsByTagName("text").item(0).getTextContent());
                message.setDate(eElement.getElementsByTagName("date").item(0).getTextContent());
                System.out.println(message.getDate() + " " + message.getAuthor() + " : " + message.getText());
                MessageStorage.addMessage(message);
            }
        }

    }
}
