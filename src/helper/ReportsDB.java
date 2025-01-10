package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReportsDB extends Appointment {

    public ReportsDB(int apptID, String apptTitle, String apptDesc, String apptLocation, String apptType,
                     LocalDateTime start, LocalDateTime end, int custID, int userID, int contactID) {
        super(apptID, apptTitle, apptDesc, apptLocation, apptType, start, end, custID, userID, contactID);
    }

    /**
     * Creates an ObservableList of all countries
     * @return countries
     * @throws SQLException database error
     */
    public static ObservableList<Reports> getCountries() throws SQLException {
        ObservableList<Reports> countries = FXCollections.observableArrayList();
        //MySQL statement to retrieve all countries and order them in alphabetical order
        String query = "select countries.Country, count(*) as countryCount from customers inner " +
                "join first_level_divisions on customers.Division_ID = first_level_divisions.Division_ID inner join " +
                "countries on countries.Country_ID = first_level_divisions.Country_ID where  customers.Division_ID = " +
                "first_level_divisions.Division_ID group by first_level_divisions.Country_ID order by count(*) desc";
        PreparedStatement statement = JDBC.getConnection().prepareStatement(query);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            String name = result.getString("Country");
            int count = result.getInt("countryCount");
            Reports report = new Reports(name, count);
            countries.add(report);
        }

        return countries;
    }
}
