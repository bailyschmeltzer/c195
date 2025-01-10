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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;


public class appointmentsController {
    public TableView<Appointment> allAppointments;
    public TableColumn<Object, Object> apptID;
    public TableColumn<Object, Object> apptTitle;
    public TableColumn<Object, Object> apptDesc;
    public TableColumn<Object, Object> apptLocation;
    public TableColumn<Object, Object> apptType;
    public TableColumn<Object, Object> apptStart;
    public TableColumn<Object, Object> apptEnd;
    public TableColumn<Object, Object> apptCustID;
    public TableColumn<Object, Object> tableContactID;
    public TableColumn<Object, Object> tableUserID;
    public RadioButton apptWeekly;
    public ToggleGroup appt;
    public RadioButton apptMonthly;
    public RadioButton allAppt;
    public Button addAppt;
    public Button deleteAppt;
    public Button exit;
    public TextField addDesc;
    public ComboBox<String> addContact;
    public DatePicker addStartDate;
    public ComboBox<String> addEndTime;
    public ComboBox<String> addStartTime;
    public DatePicker addEndDate;
    public TextField addType;
    public TextField addLocation;
    public TextField addCustID;
    public Button save;
    public TextField updateID;
    public TextField addUserID;
    public TextField addTitle;

    /**
     * Loads the appointments screen
     * Lambda expression is contained to condense a for each loop to obtain all contact names
     */
    @FXML
    public void load() {
        //attempts to create database connection and load appointments table
        try {
            JDBC.openConnection();
            Appointment selected = allAppointments.getSelectionModel().getSelectedItem();
            //ensures a appointment is selected to update
            if (selected != null) {
                ObservableList<Contact> contactsList = ContactDB.getAll();
                ObservableList<String> names = FXCollections.observableArrayList();
                String display = "";
                //LAMBDA EXPRESSION
                contactsList.forEach(contacts -> names.add(contacts.getName()));
                addContact.setItems(names);

                for (Contact contacts : contactsList) {
                    if (selected.getContactID() == contacts.getId()) {
                        display = contacts.getName();
                        break;
                    }
                }

                updateID.setText(String.valueOf(selected.getApptID()));
                addTitle.setText(selected.getAppTitle());
                addDesc.setText(selected.getApptDesc());
                addLocation.setText(selected.getApptLocation());
                addType.setText(selected.getApptType());
                addCustID.setText(String.valueOf(selected.getCustID()));
                addStartDate.setValue(selected.getStart().toLocalDate());
                addEndDate.setValue(selected.getEnd().toLocalDate());
                addStartTime.setValue(String.valueOf(selected.getStart()));
                addEndTime.setValue(String.valueOf(selected.getEnd()));
                addUserID.setText(String.valueOf(selected.getUserID()));
                addContact.setValue(display);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows all appointments within a week of the current day
     */
    @FXML
    public void apptWeeklySelected() {
        //attempts to connect to the database and load all appointments within a week of the current date
        try {
            ObservableList<Appointment> allList = AppointmentDB.getAll();
            ObservableList<Appointment> weekly = FXCollections.observableArrayList();
            LocalDateTime weeklyStart = LocalDateTime.now().minusWeeks(1);
            LocalDateTime weeklyEnd = LocalDateTime.now().plusWeeks(1);
            for (Appointment appointment : allList) {
                if (appointment.getEnd().isAfter(weeklyStart) && appointment.getEnd().isBefore(weeklyEnd)) {
                    weekly.add(appointment);
                }
            }
            allAppointments.setItems(weekly);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * shows all appointments within a month of the current day
     */
    @FXML
    public void apptMonthlySelected() {
        //attempts to connect to the database and load all appointments within a month of the current date
        try {
            ObservableList<Appointment> allList = AppointmentDB.getAll();
            ObservableList<Appointment> monthly = FXCollections.observableArrayList();
            LocalDateTime monthlyStart = LocalDateTime.now().minusMonths(1);
            LocalDateTime monthlyEnd = LocalDateTime.now().plusMonths(1);
            for (Appointment appointment : allList) {
                if (appointment.getEnd().isAfter(monthlyStart) && appointment.getEnd().isBefore(monthlyEnd)) {
                    monthly.add(appointment);
                }
            }
            allAppointments.setItems(monthly);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows all scheduled appointments
     */
    @FXML
    public void apptAllSelected() {
        //attempts to connect to the database and load all appointments
        try {
            ObservableList<Appointment> allList = AppointmentDB.getAll();
            for (Appointment ignored : allList) {
                allAppointments.setItems(allList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the add appointment screen
     * @param actionEvent the add button being clicked
     * @throws IOException error message
     */
    @FXML
    public void addAppt(ActionEvent actionEvent) throws IOException {
        //THIS TAKES TO A NEW SCREEN TO ADD APPOINTMENT
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/add.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Deletes the selected appointment
     */
    @FXML
    public void deleteAppt() {
        //attempts to open the database connection and delete the selected appointment
        try {
            Connection conn = JDBC.openConnection();
            int deleteID = allAppointments.getSelectionModel().getSelectedItem().getApptID();
            String deleteType = allAppointments.getSelectionModel().getSelectedItem().getApptType();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected appointment with appointment id: " +
                    deleteID + " and appointment type " + deleteType);
            Optional<ButtonType> b = alert.showAndWait();
            if (b.isPresent() && b.get() == ButtonType.OK) {
                assert conn != null;
                AppointmentDB.delete(deleteID);
                ObservableList<Appointment> allAppointmentsList = AppointmentDB.getAll();
                allAppointments.setItems(allAppointmentsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns to the main screen
     * @param actionEvent the exit button being clicked
     * @throws IOException error message
     */
    @FXML
    public void onActionExit(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/MainScreen.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Saves edits made to a selected appointment
     */
    @FXML
    public void onActionSave() {
        //attempts to connect to the database and save edits made to an existing appointment
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
            String starter = addStartTime.getValue();
            String startForm = starter.substring(11, 16);
            String ender = addEndTime.getValue();
            String endForm = ender.substring(11, 16);

            String date1 = addStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String date2 = addEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));


            LocalTime startTime = LocalTime.parse(startForm, format);
            LocalTime endTime = LocalTime.parse(endForm, format);

            LocalDateTime dateStart = LocalDateTime.of(addStartDate.getValue(), startTime);
            LocalDateTime dateEnd = LocalDateTime.of(addEndDate.getValue(), endTime);

            ZonedDateTime zonedStart = ZonedDateTime.of(dateStart, ZoneId.systemDefault());
            ZonedDateTime zonedEnd = ZonedDateTime.of(dateEnd, ZoneId.systemDefault());
            ZonedDateTime startEST = zonedStart.withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime endEST = zonedEnd.withZoneSameInstant(ZoneId.of("America/New_York"));


            int newCustID = Integer.parseInt(addCustID.getText());
            int appointmentID = Integer.parseInt(updateID.getText());

            //ensures all appointment details are filled in
            if (!addTitle.getText().isEmpty() && !addDesc.getText().isEmpty() &&
                    !addLocation.getText().isEmpty() && !addType.getText().isEmpty() &&
                    addStartDate.getValue() != null && addEndDate.getValue() != null &&
                    addStartTime.getValue() != null && addEndTime.getValue() != null&&
                    !addCustID.getText().isEmpty()) {
                ObservableList<Customer> allCust = CustomerDB.getAll();
                ObservableList<Integer> custIDs = FXCollections.observableArrayList();
                ObservableList<UserDB> allUsers = UserDB.getAllUsers();
                ObservableList<Integer> userIDs = FXCollections.observableArrayList();
                ObservableList<Appointment> allAppointments = AppointmentDB.getAll();

                allCust.stream().map(Customer::getCustID).forEach(custIDs::add);
                allUsers.stream().map(User::getUserID).forEach(userIDs::add);

                //ensures appointment is changed to a valid business day
                if (startEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) ||
                        startEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue()) ||
                        endEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) ||
                        endEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue())) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Please select a week day");
                    Optional<ButtonType> b = alert.showAndWait();
                    return;
                }

                //ensures the appointment is changed to be within valid business hours
                if (startEST.toLocalTime().isBefore(LocalTime.of(8, 0, 0)) ||
                        startEST.toLocalTime().isAfter(LocalTime.of(22, 0, 0)) ||
                        endEST.toLocalTime().isBefore(LocalTime.of(8, 0, 0)) ||
                        endEST.toLocalTime().isAfter(LocalTime.of(22, 0, 0))) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Selected time is outside of business hours (8am-10pm EST): " +
                            startEST.toLocalTime() + " - " + endEST.toLocalTime() + " EST");
                    Optional<ButtonType> b = alert.showAndWait();
                    return;
                }

                //ensures start date is before the end date
                //originally two separate checks, combined to simplify and condense
                if (dateStart.isAfter(dateEnd) || dateStart.isEqual(dateEnd)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment cannot start after the end time");
                    Optional<ButtonType> b = alert.showAndWait();
                    return;
                }

                for (Appointment appointment : allAppointments) {
                    LocalDateTime checkStart = appointment.getStart();
                    LocalDateTime checkEnd = appointment.getEnd();

                    //checks appointment is valid to be scheduled and not overlapping an existing appointment
                    if ((newCustID == appointment.getCustID()) && (appointmentID != appointment.getApptID()) &&
                            (dateStart.isBefore(checkStart)) && (dateEnd.isAfter(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment overlaps with an existing appointment.");
                        Optional<ButtonType> b = alert.showAndWait();
                        return;
                    }

                    //ensures appointment does not overlap with and existing appointment
                    if ((newCustID == appointment.getCustID()) && (appointmentID != appointment.getApptID()) &&
                            (dateStart.isAfter(checkStart)) && (dateStart.isBefore(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The start time overlaps with existing appointment.");
                        Optional<ButtonType> b = alert.showAndWait();
                        return;
                    }

                    //ensures appointment does not overlap with and existing appointment
                    if ((newCustID == appointment.getCustID()) && (appointmentID != appointment.getApptID()) &&
                            (dateEnd.isAfter(checkStart)) && (dateEnd.isBefore(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The end time overlaps with existing appointment.");
                        Optional<ButtonType> b = alert.showAndWait();
                        return;
                    }
                }
                String startFinal = date1 + " " + startForm;
                String endFinal = date2 + " " + endForm;
                String startUTC = startFinal + ":00";
                String endUTC = endFinal + ":00";

                //MySQL statement to add a new appointment into the database
                PreparedStatement statement = JDBC.connection.prepareStatement("UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, " +
                        "Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, " +
                        "User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?");

                statement.setInt(1, Integer.parseInt(updateID.getText()));
                statement.setString(2, addTitle.getText());
                statement.setString(3, addDesc.getText());
                statement.setString(4, addLocation.getText());
                statement.setString(5, addType.getText());
                statement.setTimestamp(6, Timestamp.valueOf(startUTC));
                statement.setTimestamp(7, Timestamp.valueOf(endUTC));
                statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(9, "admin");
                statement.setInt(10, Integer.parseInt(addCustID.getText()));
                statement.setInt(11, Integer.parseInt(addUserID.getText()));
                statement.setInt(12, Integer.parseInt(ContactDB.findID(addContact.getValue())));
                statement.setInt(13, Integer.parseInt(updateID.getText()));
                statement.execute();
                ObservableList<Appointment> allList = AppointmentDB.getAll();
                allAppointments.setAll(allList);
                initialize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the appointments table
     * @throws SQLException database error
     */
    public void initialize() throws SQLException {
        ObservableList<Appointment> allList = AppointmentDB.getAll();
        apptID.setCellValueFactory(new PropertyValueFactory<>("ApptID"));
        apptTitle.setCellValueFactory(new PropertyValueFactory<>("AppTitle"));
        apptDesc.setCellValueFactory(new PropertyValueFactory<>("ApptDesc"));
        apptLocation.setCellValueFactory(new PropertyValueFactory<>("ApptLocation"));
        apptType.setCellValueFactory(new PropertyValueFactory<>("ApptType"));
        apptStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        apptEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        apptCustID.setCellValueFactory(new PropertyValueFactory<>("CustID"));
        tableContactID.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        tableUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        allAppointments.setItems(allList);
    }

    /**
     * Populates the start and end time selections based on the date picked from the start date picker
     */
    @FXML
    public void onActionPopulate() {
        if (addStartDate != null) {
            ObservableList<String> times = FXCollections.observableArrayList();
            ObservableList<String> times2 = FXCollections.observableArrayList();
            TimeZone local = TimeZone.getDefault();
            TimeZone utc = TimeZone.getTimeZone("America/New_York");
            int i = local.getRawOffset() - utc.getRawOffset();
            i = i / 3600000;
            int j = 8 + i;

            LocalTime first = LocalTime.MIN.plusHours(j);
            LocalDateTime startOfDay = LocalDateTime.of(addStartDate.getValue(), first);
            LocalDateTime endOfDay = startOfDay.plusHours(13).plusMinutes(30);

            while (startOfDay.isBefore(endOfDay)) {
                times.add(String.valueOf(startOfDay));
                times2.add(String.valueOf(startOfDay));
                startOfDay = startOfDay.plusMinutes(15);
            }
            addStartTime.setItems(times);
            int a = 0;
            while (a < 3) {
                times2.add(String.valueOf(startOfDay));
                startOfDay = startOfDay.plusMinutes(15);
                a++;
            }
            addEndTime.setItems(times2);
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Select a start date first");
            Optional<ButtonType> b = alert.showAndWait();
            return;
        }
    }

    /**
     * populates the end date box based on the selected end time
     */
    @FXML
    public void endAppt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateTime = addEndTime.getValue();
        String date = dateTime.substring(0,10);
        LocalDate endOfDay = LocalDate.parse(date, formatter);
        addEndDate.setValue(endOfDay);
    }
}
