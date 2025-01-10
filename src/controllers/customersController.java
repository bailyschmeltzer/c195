package controllers;

import helper.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
public class customersController implements Initializable {

    @FXML
    public TableView<Customer> custTable;
    @FXML
    public TextField editPhone;
    @FXML
    public TableColumn<Object, Object> custID;
    @FXML
    public TableColumn<Object, Object> custName;
    @FXML
    public TableColumn<Object, Object> custAddress;
    @FXML
    public TableColumn<Object, Object> custZip;
    @FXML
    public TableColumn<Object, Object> custPhone;
    @FXML
    public TableColumn<Object, Object> custState;
    @FXML
    public TextField editID;
    @FXML
    public TextField editName;
    @FXML
    public TextField editAddress;
    @FXML
    public TextField editZip;
    @FXML
    public ComboBox<String> editCountry;
    @FXML
    public ComboBox<String> editState;
    @FXML
    public Button edit;
    @FXML
    public Button cancel;
    @FXML
    public Button delete;
    @FXML
    public Button add;

    /**
     * Changes the country when chosen
     * Lambda expression is contained here to loop and select the first level division more efficiently
     */
    public void onActionCountry() {
        //attempts to connect to the database and show customer based on the selected country
        try {
            JDBC.openConnection();
            String country = editCountry.getSelectionModel().getSelectedItem();
            ObservableList<FirstLevelDivisionDB> allDivs = FirstLevelDivisionDB.getAll();
            ObservableList<String> us = FXCollections.observableArrayList();
            ObservableList<String> uk = FXCollections.observableArrayList();
            ObservableList<String> canada = FXCollections.observableArrayList();
            //LAMBDA EXPRESSION
            allDivs.forEach(FirstLevelDivision -> {
                if (FirstLevelDivision.getCountryID() == 1) {
                    us.add(FirstLevelDivision.getDivName());
                } else if (FirstLevelDivision.getCountryID() == 2) {
                    uk.add(FirstLevelDivision.getDivName());
                } else if (FirstLevelDivision.getCountryID() == 3) {
                    canada.add(FirstLevelDivision.getDivName());
                }
            });

            switch (country) {
                case "U.S":
                    editState.setItems(us);
                    break;
                case "UK":
                    editState.setItems(uk);
                    break;
                case "Canada":
                    editState.setItems(canada);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * saves edits made to selected customer
     */
    public void onActionSave() {
        //attempts to open the database connection and save edits made to a customer
        try {
            //ensures all data to be edited is present and valid
            if (!editName.getText().isEmpty() && !editAddress.getText().isEmpty() && !editZip.getText().isEmpty() &&
                    !editPhone.getText().isEmpty() && !editCountry.getValue().isEmpty() && !editState.getValue().isEmpty()) {
                int divName = 0;
                for (FirstLevelDivisionDB first : FirstLevelDivisionDB.getAll()) {
                    if (editState.getSelectionModel().getSelectedItem().equals(first.getDivName())) {
                        divName = first.getDivID();
                    }
                }

                //MySQL statement to update an existing customer in the database
                String insert = "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, " +
                        "Postal_Code = ?, Phone = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, " +
                        "Division_ID = ? WHERE Customer_ID = ?";

                PreparedStatement statement = JDBC.connection.prepareStatement(insert);
                statement.setInt(1, Integer.parseInt(editID.getText()));
                statement.setString(2, editName.getText());
                statement.setString(3, editAddress.getText());
                statement.setString(4, editZip.getText());
                statement.setString(5, editPhone.getText());
                statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(7, "admin");
                statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(9, "admin");
                statement.setInt(10, divName);
                statement.setInt(11, Integer.parseInt(editID.getText()));
                statement.execute();
                editID.clear();
                editName.clear();
                editAddress.clear();
                editZip.clear();
                editPhone.clear();
                ObservableList<Customer> refresh = CustomerDB.getAll();
                custTable.setItems(refresh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the selected customers data to be able to edit
     */
    public void onActionEdit() {
        //connects to the database and attempts to fill in the edit boxes with an existing selected customers data
        try {
            JDBC.openConnection();
            Customer cust = custTable.getSelectionModel().getSelectedItem();
            String divName = "";
            String country = "";
            //ensures a customer is selected
            if (cust != null) {
                ObservableList<CountryDB> countries = CountryDB.getAll();
                ObservableList<FirstLevelDivisionDB> allDivNames = FirstLevelDivisionDB.getAll();
                ObservableList<String> allDivs = FXCollections.observableArrayList();
                editState.setItems(allDivs);
                editID.setText(String.valueOf(cust.getCustID()));
                editName.setText(cust.getCustName());
                editAddress.setText(cust.getCustAddress());
                editZip.setText(cust.getCustZip());
                editPhone.setText(cust.getCustPhone());
                for (FirstLevelDivision first : allDivNames) {
                    allDivs.add(first.getDivName());
                    int id = first.getCountryID();
                    if (first.getDivID() == cust.getDivID()) {
                        divName = first.getDivName();
                        for (Country var : countries) {
                            if (var.getCountryID() == id) {
                                country = var.getCountryName();
                            }
                        }
                    }
                }
                editState.setValue(divName);
                editCountry.setValue(country);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns to the main screen
     * @param actionEvent the cancel button being clicked
     * @throws IOException error message
     */
    public void onActionCancel(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/MainScreen.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Deletes the selected customer
     * @throws SQLException database error
     */
    public void onActionDelete() throws SQLException {
        ObservableList<Appointment> allList = AppointmentDB.getAll();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to delete the selected customer and all associated appointments?");
        Optional<ButtonType> b = alert.showAndWait();
        //ensures that the user agrees to delete the selected customer
        if (b.isPresent() && b.get() == ButtonType.OK) {
            int id = custTable.getSelectionModel().getSelectedItem().getCustID();

            AppointmentDB.delete(id);
            String delete = "DELETE FROM customers WHERE Customer_ID = ?";
            PreparedStatement statement = JDBC.connection.prepareStatement(delete);
            for (Appointment appt : allList) {
                int temp = appt.getCustID();
                if (temp == id) {
                    String deleteStatement = "DELETE FROM appointments WHERE Customer_ID = ?";
                    PreparedStatement var = JDBC.connection.prepareStatement(deleteStatement);
                    var.setInt(1, temp);
                    var.execute();
                }
            }
            statement.setInt(1, id);
            statement.execute();
            ObservableList<Customer> refresh = CustomerDB.getAll();
            custTable.setItems(refresh);
        }
    }

    /**
     * Adds new customer data
     */
    public void onActionAdd() {
        //attempts to connect to the database and save edits made to a customer
        try {
            //ensures all customer data is present and valid
            if (!editName.getText().isEmpty() && !editAddress.getText().isEmpty() && !editZip.getText().isEmpty() &&
                    !editPhone.getText().isEmpty() && !editCountry.getValue().isEmpty() && !editState.getValue().isEmpty()) {
                int id = 1;
                ObservableList<Customer> all = CustomerDB.getAll();
                for (Customer cust : all) {
                    if (id == cust.getCustID()) {
                        id++;
                    }
                }
                int divName = 0;
                for (FirstLevelDivisionDB first : FirstLevelDivisionDB.getAll()) {
                    if (editState.getSelectionModel().getSelectedItem().equals(first.getDivName())) {
                        divName = first.getDivID();
                    }
                }

                //MySQL statement to add new customer data
                String insert = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, " +
                        "Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?,?)";

                PreparedStatement statement = JDBC.connection.prepareStatement(insert);
                statement.setInt(1, id);
                statement.setString(2, editName.getText());
                statement.setString(3, editAddress.getText());
                statement.setString(4, editZip.getText());
                statement.setString(5, editPhone.getText());
                statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(7, "admin");
                statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(9, "admin");
                statement.setInt(10, divName);
                statement.execute();
                editID.clear();
                editName.clear();
                editAddress.clear();
                editZip.clear();
                editPhone.clear();
                ObservableList<Customer> refresh = CustomerDB.getAll();
                custTable.setItems(refresh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the customer screen
     * Lambda expression is contained to condense a for each loop to obtain all first level division names
     * @param url url
     * @param resource resource
     */
    @Override
    public void initialize(URL url, ResourceBundle resource) {
        //attempts to connect to the database and populate customer table
        try {
            ObservableList<CountryDB> allCountries = CountryDB.getAll();
            ObservableList<String> countryNames = FXCollections.observableArrayList();
            ObservableList<FirstLevelDivisionDB> allDivisions = FirstLevelDivisionDB.getAll();
            ObservableList<String> allDivNames = FXCollections.observableArrayList();
            ObservableList<Customer> allList = CustomerDB.getAll();
            custID.setCellValueFactory(new PropertyValueFactory<>("CustID"));
            custName.setCellValueFactory(new PropertyValueFactory<>("CustName"));
            custAddress.setCellValueFactory(new PropertyValueFactory<>("CustAddress"));
            custZip.setCellValueFactory(new PropertyValueFactory<>("CustZip"));
            custPhone.setCellValueFactory(new PropertyValueFactory<>("CustPhone"));
            custState.setCellValueFactory(new PropertyValueFactory<>("DivName"));
            allCountries.stream().map(Country::getCountryName).forEach(countryNames::add);
            editCountry.setItems(countryNames);
            //LAMBDA EXPRESSION
            allDivisions.forEach(FirstLevelDivision -> allDivNames.add(FirstLevelDivision.getDivName()));

            editState.setItems(allDivNames);
            custTable.setItems(allList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
