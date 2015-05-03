package by.bsy.ivaniukovich.controller;

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
import java.util.List;

import static by.bsy.ivaniukovich.util.MessageUtil.*;

/**
 * Created by Hope on 4/24/2015.
 */

@WebServlet("/chat")
public class MessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(MessageServlet.class.getName());
    private static MessageDOMParser domParser = new MessageDOMParser();

    @Override
    public void init() throws ServletException {
        addStubData();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //logger.info("doGet");
        String token = request.getParameter(TOKEN);
        //logger.info("Token " + token);

        if (token != null && !"".equals(token)) {
            int index = getIndex(token);
            //logger.info("Index " + index);
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
        //logger.info("doPost");
        String data = ServletUtil.getMessageBody(request);
        //logger.info(data);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            System.out.println(message.getDate() + " " + message.getAuthor() + " : " + message.getText());
            domParser.write(message);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = ServletUtil.getMessageBody(request);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            domParser.deleteMessage(message);
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
        while (index == MessageStorage.getSize()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }

        JSONObject jsonObject = new JSONObject();
        if (index < MessageStorage.getSize()) {
            // new message added
            jsonObject.put(MESSAGES, MessageStorage.getSubMessagesByIndex(index));
            jsonObject.put(TOKEN, getToken(MessageStorage.getSize()));
        } else {
            // message was removed, tell client to reload whole list
            jsonObject.put(INVALIDATE_TOKEN, true);
        }
        return jsonObject.toJSONString();
    }

    private void addStubData(){
        try {
            domParser.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
