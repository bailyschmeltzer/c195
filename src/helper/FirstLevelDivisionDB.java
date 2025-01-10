package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FirstLevelDivisionDB extends FirstLevelDivision {

    public FirstLevelDivisionDB(int divID, String divName, int countryID) {
        super(divID, divName, countryID);
    }

    /**
     * Creates an ObservableList of all first level divisions
     * @return list
     * @throws SQLException database error
     */
    public static ObservableList<FirstLevelDivisionDB> getAll() throws SQLException {
        ObservableList<FirstLevelDivisionDB> list = FXCollections.observableArrayList();
        String query = "SELECT * from first_level_divisions";
        PreparedStatement statement = JDBC.getConnection().prepareStatement(query);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            int divID = result.getInt("Division_ID");
            String divName = result.getString("Division");
            int countryID = result.getInt("COUNTRY_ID");
            FirstLevelDivisionDB first = new FirstLevelDivisionDB(divID, divName, countryID);
            list.add(first);
        }
        return list;
    }
}
