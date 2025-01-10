package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDB extends User {

    //may be able to be deleted
    private static User user;

    //may be able to be deleted
    public UserDB(int userID, String userName, String userPassword) {
        super();
    }

    /**
     *
     * @return user
     */
    //may be able to be deleted
    public static User getCurrentUser() {
        return user;
    }

    /**
     * Creates an ObservableList of all users
     * @return allUsers
     * @throws SQLException database error
     */
    public static ObservableList<UserDB> getAllUsers() throws SQLException {
        ObservableList<UserDB> allUsers = FXCollections.observableArrayList();
        String sql = "SELECT * from users";
        PreparedStatement statement = JDBC.getConnection().prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        //searches and adds all users to the ObservableList
        while (result.next()) {
            int userID = result.getInt("User_ID");
            String username = result.getString("User_Name");
            String password = result.getString("Password");
            UserDB user = new UserDB(userID, username, password);
            allUsers.add(user);
        }
        return allUsers;
    }

    /**
     * Validates the login attempt
     * @param username the username
     * @param password the password
     * @return the result of the login attempt
     */
    public static int validateUser(String username, String password) {
        //attempts to connect to the database and verify login credentials
        try {
            String query = "SELECT * FROM users WHERE user_name = '" + username + "' AND password = '" + password + "'";
            PreparedStatement statement = JDBC.getConnection().prepareStatement(query);
            ResultSet result = statement.executeQuery();
            result.next();
            if (result.getString("User_Name").equals(username)) {
                if (result.getString("Password").equals(password)) {
                    return result.getInt("User_ID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
