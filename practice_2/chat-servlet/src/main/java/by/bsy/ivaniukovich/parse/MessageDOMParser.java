package by.bsy.ivaniukovich.parse;

import by.bsy.ivaniukovich.model.Message;
import org.w3c.dom.*;

import by.bsy.ivaniukovich.model.MessageStorage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Created by Hope on 4/24/2015.
 */
public class MessageDOMParser {
    private String filePath = "d:\\Work\\Study\\part2\\practice_2\\chat-servlet\\history.xml";

    public void write(Message message) throws  Exception {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(filePath);

        Node messages = doc.getFirstChild();
        Node messageNode = doc.createElement("message");

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
        DOMSource source = new DOMSource(doc);
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

                message.setAuthor(eElement.getElementsByTagName("author").item(0).getTextContent());
                message.setText(eElement.getElementsByTagName("text").item(0).getTextContent());
                message.setDate(eElement.getElementsByTagName("date").item(0).getTextContent());
                System.out.println(message.getDate() + " " + message.getAuthor() + " : " + message.getText());
                MessageStorage.addMessage(message);
            }
        }

    }
}
