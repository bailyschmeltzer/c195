package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class CustomerDB {

    /**
     * Creates an ObservableList of all customers
     * @return customers
     * @throws SQLException database error
     */
    public static ObservableList<Customer> getAll() throws SQLException {

        //MySQL statement to select all customers and their associated data
        String query = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, " +
                "customers.Postal_Code, customers.Phone, customers.Division_ID, " +
                "first_level_divisions.Division from customers INNER JOIN  first_level_divisions ON customers.Division_ID = " +
                "first_level_divisions.Division_ID";

        PreparedStatement statement = JDBC.getConnection().prepareStatement(query);
        ResultSet result = statement.executeQuery();

        ObservableList<Customer> customers = FXCollections.observableArrayList();

        //populates with the data of all customers in the database
        while (result.next()) {
            int custID = result.getInt("Customer_ID");
            String custName = result.getString("Customer_Name");
            String custAddress = result.getString("Address");
            String custZip = result.getString("Postal_Code");
            String custPhone = result.getString("Phone");
            int divID = result.getInt("Division_ID");
            String divName = result.getString("Division");
            Customer cust = new Customer(custID, custName, custAddress, custZip, custPhone, divID, divName);
            customers.add(cust);
        }
        return customers;
    }

}
