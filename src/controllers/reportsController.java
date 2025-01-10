package controllers;

import helper.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Month;
import java.util.Collections;
import java.util.Objects;

public class reportsController {

    @FXML
    public Button exit;
    @FXML
    public TableView<Appointment> allTable;
    @FXML
    public TableColumn<Object, Object> apptID;
    @FXML
    public TableColumn<Object, Object> apptTitle;
    @FXML
    public TableColumn<Object, Object> apptDesc;
    @FXML
    public TableColumn<Object, Object> apptLocation;
    @FXML
    public TableColumn<Object, Object> apptContact;
    @FXML
    public TableColumn<Object, Object> apptType;
    @FXML
    public TableColumn<Object, Object> start;
    @FXML
    public TableColumn<Object, Object> contactID;
    @FXML
    public TableColumn<Object, Object> custID;
    @FXML
    public TableColumn<Object, Object> end;
    @FXML
    public Tab apptTotals;
    @FXML
    public TableView<Type> apptTotalsType;
    @FXML
    public TableColumn<Object, Object> apptTotalsTypeColumn;
    @FXML
    public TableColumn<Object, Object> apptTypeTotalColumn;
    @FXML
    public TableView<Monthly> monthly;
    @FXML
    public TableColumn<Object, Object> apptByMonth;
    @FXML
    public TableColumn<Object, Object> apptsMonthlyTotal;
    @FXML
    public Tab custCountry;
    @FXML
    public TableView<Reports> custByCountry;
    @FXML
    public TableColumn<Object, Object> name;
    @FXML
    public TableColumn<Object, Object> counter;
    @FXML
    public ComboBox<String> contact;

    /**
     * exits the reports screen
     * @param actionEvent exit button being clicked
     * @throws IOException error message
     */
    public void onActionExit(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/MainScreen.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * fills data on the selected contact
     * @throws SQLException database error
     */
    public void onActionContact() throws SQLException {
        int id = 0;
        ObservableList<Appointment> allAppts = AppointmentDB.getAll();
        ObservableList<Appointment> apptData = FXCollections.observableArrayList();
        ObservableList<Contact> allContacts = ContactDB.getAll();
        Appointment contactApptData;
        String contactName = contact.getSelectionModel().getSelectedItem();

        for (Contact contact : allContacts) {
            if (contactName.equals(contact.getName())) {
                id = contact.getId();
            }
        }

        for (Appointment appointment : allAppts) {
            if (appointment.getContactID() == id) {
                contactApptData = appointment;
                apptData.add(contactApptData);
            }
        }
        allTable.setItems(apptData);
    }

    /**
     * Totals the amount of appointments based on type, monthly, and weekly
     * Contains multiple Lambda expressions to condense for each loops to get various appointment data pieces
     */
    public void apptTotals() {
        //attempts to connect to the database and fill in totals for appointment
        try {
            ObservableList<Appointment> allAppts = AppointmentDB.getAll();
            ObservableList<Month> apptMonths = FXCollections.observableArrayList();
            ObservableList<Month> monthOfApps = FXCollections.observableArrayList();
            ObservableList<String> apptType = FXCollections.observableArrayList();
            ObservableList<String> isUnique = FXCollections.observableArrayList();
            ObservableList<Type> type = FXCollections.observableArrayList();
            ObservableList<Monthly> reportMonths = FXCollections.observableArrayList();

            //LAMBDA EXPRESSION
            allAppts.forEach(appointment -> apptType.add(appointment.getApptType()));

            //LAMBDA EXPRESSION
            allAppts.stream().map(appointment -> appointment.getStart().getMonth()).forEach(apptMonths::add);

            //LAMBDA EXPRESSION
            apptMonths.stream().filter(month -> !monthOfApps.contains(month)).forEach(monthOfApps::add);

            for (Appointment appts : allAppts) {
                String holder = appts.getApptType();
                if (!isUnique.contains(holder)) {
                    isUnique.add(holder);
                }
            }
            for (Month month : monthOfApps) {
                int total = Collections.frequency(apptMonths, month);
                String monthName = month.name();
                Monthly holder = new Monthly(monthName, total);
                reportMonths.add(holder);
            }
            monthly.setItems(reportMonths);
            for (String types : isUnique) {
                int total = Collections.frequency(apptType, types);
                Type toAdd = new Type(types, total);
                type.add(toAdd);
            }
            apptTotalsType.setItems(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * fills data on the countries
     */
    public void custCountry() {
        //attempts to connect to the database and fill data on countries
        try {
            ObservableList<Reports> countries = ReportsDB.getCountries();
            ObservableList<Reports> toAdd = FXCollections.observableArrayList();
            toAdd.addAll(countries);
            custByCountry.setItems(toAdd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * initializes the reports tables
     * Lambda expression to condense a for each loop to add all contacts
     * @throws SQLException database error
     */
    public void initialize() throws SQLException {
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        counter.setCellValueFactory(new PropertyValueFactory<>("Count"));
        apptID.setCellValueFactory(new PropertyValueFactory<>("ApptID"));
        apptTitle.setCellValueFactory(new PropertyValueFactory<>("AppTitle"));
        apptDesc.setCellValueFactory(new PropertyValueFactory<>("ApptDesc"));
        apptLocation.setCellValueFactory(new PropertyValueFactory<>("ApptLocation"));
        apptType.setCellValueFactory(new PropertyValueFactory<>("ApptType"));
        start.setCellValueFactory(new PropertyValueFactory<>("start"));
        end.setCellValueFactory(new PropertyValueFactory<>("end"));
        custID.setCellValueFactory(new PropertyValueFactory<>("CustID"));
        contactID.setCellValueFactory(new PropertyValueFactory<>("ContactID"));
        apptTotalsTypeColumn.setCellValueFactory(new PropertyValueFactory<>("Type"));
        apptTypeTotalColumn.setCellValueFactory(new PropertyValueFactory<>("Total"));
        apptByMonth.setCellValueFactory(new PropertyValueFactory<>("Month"));
        apptsMonthlyTotal.setCellValueFactory(new PropertyValueFactory<>("Total"));
        ObservableList<Contact> contacts = ContactDB.getAll();
        ObservableList<String> allNames = FXCollections.observableArrayList();
        //LAMBDA EXPRESSION
        contacts.forEach(conts -> allNames.add(conts.getName()));
        contact.setItems(allNames);
    }
}
