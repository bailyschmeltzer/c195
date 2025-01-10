package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactDB {

    /**
     * Creates an ObservableList of all contacts
     * @return contacts
     * @throws SQLException database error
     */
    public static ObservableList<Contact> getAll() throws SQLException {
        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        String query = "SELECT * from contacts";
        PreparedStatement statement = JDBC.getConnection().prepareStatement(query);
        ResultSet result = statement.executeQuery();
        //ensures all contacts are added
        while (result.next()) {
            int contID = result.getInt("Contact_ID");
            String contName = result.getString("Contact_Name");
            String contEmail = result.getString("Email");
            Contact contact = new Contact(contID, contName, contEmail);
            contacts.add(contact);
        }
        return contacts;
    }

    /**
     *
     * @param contID the contact ID
     * @return contID
     * @throws SQLException database error
     */
    public static String findID(String contID) throws SQLException {
        //finds the specified contact
        PreparedStatement statement = JDBC.getConnection().prepareStatement("SELECT * FROM contacts WHERE Contact_Name = ?");
        statement.setString(1, contID);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            contID = result.getString("Contact_ID");
        }
        return contID;
    }
}
