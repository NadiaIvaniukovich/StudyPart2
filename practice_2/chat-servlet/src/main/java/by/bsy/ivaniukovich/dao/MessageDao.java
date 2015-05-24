package by.bsy.ivaniukovich.dao;

import by.bsy.ivaniukovich.model.Message;

import java.util.List;

/**
 * Created by Hope on 5/19/2015.
 */
public interface MessageDao {
    List<Message> selectMessages(int index);
    void addMessage(Message message);
    void deleteMessage(Message message);
    void updateMessage(Message message);
    int getNumberOfMessages();
}
