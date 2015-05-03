package by.bsy.ivaniukovich.model;

import java.util.Locale;

/**
 * Created by Hope on 4/24/2015.
 */
public class Message {
    private String id;
    private String author;
    private String text;
    private String date;

    public Message(){}

    public Message(String id, String author, String text, String date) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if(!(obj instanceof Message))
            return false;
        Message other = (Message) obj;
        if (!id.equals(other.getId()))
            return false;
        return true;
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }
    public String toString() {
        return "{\"id\":\"" + this.id + "\",\"author\":\"" + this.author + "\",\"text\":\"" + this.text + "\"}";
    }
}
