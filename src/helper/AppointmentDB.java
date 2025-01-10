package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.*;

public class AppointmentDB {

    /**
     * Creates an ObservableList of all appointments
     * @return appointments
     * @throws SQLException database error
     */
    public static ObservableList<Appointment> getAll() throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String query = "SELECT * from appointments";
        PreparedStatement statement = JDBC.getConnection().prepareStatement(query);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            int apptID = result.getInt("Appointment_ID");
            String apptTitle = result.getString("Title");
            String apptDesc = result.getString("Description");
            String apptLocation = result.getString("Location");
            String apptType = result.getString("Type");
            LocalDateTime start = result.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = result.getTimestamp("End").toLocalDateTime();
            int custID = result.getInt("Customer_ID");
            int userID = result.getInt("User_ID");
            int contactID = result.getInt("Contact_ID");
            Appointment appt = new Appointment(apptID, apptTitle, apptDesc, apptLocation, apptType, start, end, custID, userID, contactID);
            appointments.add(appt);
        }
        return appointments;
    }

    /**
     *
     * @param cust the customer
     * @throws SQLException database error
     */
    public static void delete(int cust) throws SQLException {
        String query = "DELETE FROM appointments WHERE Appointment_ID=?";
        PreparedStatement statement = JDBC.connection.prepareStatement(query);
        statement.setInt(1, cust);
        statement.executeUpdate();
        statement.close();
    }
}
