package by.bsy.ivaniukovich.controller;

import static by.bsy.ivaniukovich.util.MessageUtil.MESSAGES;
import static by.bsy.ivaniukovich.util.MessageUtil.TOKEN;
import static by.bsy.ivaniukovich.util.MessageUtil.getIndex;
import static by.bsy.ivaniukovich.util.MessageUtil.getToken;
import static by.bsy.ivaniukovich.util.MessageUtil.jsonToMessage;
import static by.bsy.ivaniukovich.util.MessageUtil.stringToJson;

import by.bsy.ivaniukovich.parse.MessageDOMParser;
import org.apache.log4j.Logger;
import by.bsy.ivaniukovich.model.Message;
import by.bsy.ivaniukovich.model.MessageStorage;
import by.bsy.ivaniukovich.util.ServletUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Hope on 4/24/2015.
 */

@WebServlet("/chat")
public class MessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(MessageServlet.class.getName());

    @Override
    public void init() throws ServletException {
        addStubData();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doGet");
        String token = request.getParameter(TOKEN);
        logger.info("Token " + token);

        if (token != null && !"".equals(token)) {
            int index = getIndex(token);
            logger.info("Index " + index);
            String messages = formResponse(index);
            response.setContentType(ServletUtil.APPLICATION_JSON);
            PrintWriter out = response.getWriter();
            out.print(messages);
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doPost");
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            System.out.println(message.getDate() + " " + message.getAuthor() + " : " + message.getText());
            MessageStorage.addMessage(message);
            new MessageDOMParser().write(message);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    private String formResponse(int index) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGES, MessageStorage.getSubMessagesByIndex(index));
        jsonObject.put(TOKEN, getToken(MessageStorage.getSize()));
        return jsonObject.toJSONString();
    }

    private void addStubData(){
        MessageDOMParser parser = new MessageDOMParser();
        try {
            parser.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
