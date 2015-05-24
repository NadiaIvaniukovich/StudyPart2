package by.bsy.ivaniukovich.dao;

import by.bsy.ivaniukovich.model.Message;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Hope on 5/19/2015.
 */
public class MessageDBStorage implements MessageDao{

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat", "root", "root");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    private int findUserId(String username){
        int id = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("SELECT id FROM users WHERE name=?;");
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.isBeforeFirst()){
                addUser(username);
                preparedStatement = connection.prepareStatement("SELECT id FROM users WHERE name=?;");
                preparedStatement.setString(1,username);
                resultSet = preparedStatement.executeQuery();
            }
            while (resultSet.next()){
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return id;
    }

    private void addUser(String username){
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO users (name) VALUES(?)");
            preparedStatement.setString(1,username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getNumberOfMessages(){
        int number = 0;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        connection = getConnection();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) AS n FROM messages;");
            while (resultSet.next()){
                number = resultSet.getInt("n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return number;
    }

    @Override
    public List<Message> selectMessages(int index) {
        List<Message> messages = new ArrayList<Message>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int n = getNumberOfMessages()-index;

        connection = getConnection();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT messages.id,text,date,name FROM messages INNER JOIN users ON users.id=user_id ORDER BY date LIMIT "+index+","+n+";");
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String text = resultSet.getString("text");
                Timestamp date = resultSet.getTimestamp("date");
                String author = resultSet.getString("name");
                messages.add(new Message(id,author,text,date.toString()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return messages;
    }

    @Override
    public void addMessage(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO messages (id,text,user_id,date) VALUES(?,?,?,?)");

            preparedStatement.setString(1,message.getId());
            preparedStatement.setString(2,message.getText());
            preparedStatement.setInt(3,findUserId(message.getAuthor()));
            preparedStatement.setTimestamp(4, java.sql.Timestamp.valueOf(message.getDate()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void deleteMessage(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

            connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM messages WHERE id=?;");
            preparedStatement.setString(1,message.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void updateMessage(Message message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        connection = getConnection();
        try {
            preparedStatement = connection.prepareStatement("UPDATE messages SET text=? WHERE id=?;");
            preparedStatement.setString(1,message.getText());
            preparedStatement.setString(2,message.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
