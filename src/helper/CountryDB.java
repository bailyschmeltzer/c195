package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountryDB extends Country{

    public CountryDB(int countryID, String countryName) {
        super(countryID, countryName);
    }

    /**
     * Creates an ObservableList of all countries
     * @return countries
     * @throws SQLException database error
     */
    public static ObservableList<CountryDB> getAll() throws SQLException {
        ObservableList<CountryDB> countries = FXCollections.observableArrayList();
        String query = "SELECT Country_ID, Country from countries";
        PreparedStatement statement = JDBC.getConnection().prepareStatement(query);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            int countryID = result.getInt("Country_ID");
            String countryName = result.getString("Country");
            CountryDB country = new CountryDB(countryID, countryName);
            countries.add(country);
        }
        return countries;
    }

}
