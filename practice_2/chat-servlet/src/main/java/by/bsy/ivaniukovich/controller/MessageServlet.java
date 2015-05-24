package by.bsy.ivaniukovich.controller;

import by.bsy.ivaniukovich.dao.MessageDBStorage;
import by.bsy.ivaniukovich.dao.MessageDao;
import by.bsy.ivaniukovich.parse.MessageDOMParser;
import org.apache.log4j.Logger;
import by.bsy.ivaniukovich.model.Message;
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
    private static MessageDao messageDao = new MessageDBStorage();

    @Override
    public void init() throws ServletException {
        addStubData();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //logger.debug("doGet");
        String token = request.getParameter("token");
        //logger.debug("Token " + token);

        if (token != null && !"".equals(token)) {
            int index = getIndex(token);
            //logger.debug("Index " + index);

            try {
                if(index < messageDao.getNumberOfMessages()){
                    String messages = null;
                    try {
                        messages = formResponse(index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    response.setContentType(ServletUtil.APPLICATION_JSON);
                    PrintWriter out = response.getWriter();
                    out.print(messages);
                    out.flush();

                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            //domParser.write(message);
            messageDao.addMessage(message);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doPut");
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            //domParser.changeMessage(message);
            messageDao.updateMessage(message);
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
        logger.info("doDelete");
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = stringToJson(data);
            Message message = jsonToMessage(json);
            //domParser.deleteMessage(message);
            messageDao.deleteMessage(message);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    private String formResponse(int index) throws Exception {
        /*while (index == domParser.size()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }*/

        JSONObject jsonObject = new JSONObject();
        //if (index < domParser.size()) {
            // new message added
            jsonObject.put("messages", messageDao.selectMessages(index));
            jsonObject.put("token", getToken(messageDao.getNumberOfMessages()));
        //} else {
            // message was removed, tell client to reload whole list
            //jsonObject.put("invalidateToken", true);
        //}
        return jsonObject.toJSONString();
    }

    private void addStubData(){
        try {
            //domParser.parse(0);
            List<Message> messages = messageDao.selectMessages(0);
            for(int i = 0; i < messages.size(); i++){
                Message message = messages.get(i);
                System.out.println(message.getDate() + " " + message.getAuthor() + " : " + message.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
