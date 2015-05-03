package by.bsy.ivaniukovich.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Hope on 4/24/2015.
 */
public final class ServletUtil {
    public static final String APPLICATION_JSON = "application/json; charset=UTF-8";

    private ServletUtil() {
    }

    public static String getMessageBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
