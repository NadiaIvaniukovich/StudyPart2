package by.bsy.ivaniukovich.util;

import by.bsy.ivaniukovich.model.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hope on 4/24/2015.
 */
public class MessageUtil {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private MessageUtil() {
    }

    public static String getToken(int index) {
        Integer number = index * 8 + 11;
        return "TN" + number + "EN";
    }

    public static int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    public static JSONObject stringToJson(String data) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(data.trim());
    }

    public static Message jsonToMessage(JSONObject json) {
        Object id = json.get("id");
        Object author = json.get("author");
        Object text = json.get("text");
        String date = simpleDateFormat.format(new Date());

        if (id != null && author != null && text != null) {
            return new Message((String) id, (String) author, (String) text, date);
        }
        return null;
    }
}
