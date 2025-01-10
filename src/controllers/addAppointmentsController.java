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
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;


public class addAppointmentsController {
    @FXML
    public TextField apptID;
    @FXML
    public TextField apptTitle;
    @FXML
    public TextField apptDesc;
    @FXML
    public ComboBox<String> apptContact;
    @FXML
    public DatePicker startDate;
    @FXML
    public ComboBox<String> endTime;
    @FXML
    public ComboBox<String> startTime;
    @FXML
    public DatePicker endDate;
    @FXML
    public Button save;
    @FXML
    public Button cancel;
    @FXML
    public TextField type;
    @FXML
    public TextField loc;
    @FXML
    public TextField custID;
    @FXML
    public TextField userID;



    /**
     * Saves the entered data
     * @param actionEvent the save button being clicked
     */
    public void onActionSave(ActionEvent actionEvent) {
        try {
            //ensures all data to be saved is populated
            if (!apptTitle.getText().isEmpty() && !apptDesc.getText().isEmpty() &&
                    !loc.getText().isEmpty() && !type.getText().isEmpty() &&
                    startDate.getValue() != null && endDate.getValue() != null &&
                    !startTime.getValue().isEmpty() && !endTime.getValue().isEmpty() &&
                    !custID.getText().isEmpty()) {

                ObservableList<Customer> customers = CustomerDB.getAll();
                ObservableList<Integer> custIDs = FXCollections.observableArrayList();
                ObservableList<UserDB> users = UserDB.getAllUsers();
                ObservableList<Integer> userIDs = FXCollections.observableArrayList();
                ObservableList<Appointment> appointments = AppointmentDB.getAll();

                customers.stream().map(Customer::getCustID).forEach(custIDs::add);
                users.stream().map(User::getUserID).forEach(userIDs::add);

                LocalDate localDateStart = startDate.getValue();
                LocalDate localDateEnd = endDate.getValue();
                DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
                String startDateS = startDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String startDateString = startDateS.substring(0, 10);
                String startTimeS = startTime.getValue();
                String startTimeString = startTimeS.substring(11, 16);
                String endDateS = endDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String endDateString = endDateS.substring(0, 10);
                String endTimeS = endTime.getValue();
                String endTimeString = endTimeS.substring(11, 16);
                String startUTC = startDateString + " " + startTimeString + ":00";
                String endUTC = endDateString + " " + endTimeString + ":00";
                LocalTime startTimeLocal = LocalTime.parse(startTimeString, format);
                LocalTime endTimeLocal = LocalTime.parse(endTimeString, format);
                LocalDateTime startTimeDate = LocalDateTime.of(localDateStart, startTimeLocal);
                LocalDateTime endTimeDate = LocalDateTime.of(localDateEnd, endTimeLocal);
                ZonedDateTime startZoned = ZonedDateTime.of(startTimeDate, ZoneId.systemDefault());
                ZonedDateTime endZoned = ZonedDateTime.of(endTimeDate, ZoneId.systemDefault());
                ZonedDateTime startEST = startZoned.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime endEST = endZoned.withZoneSameInstant(ZoneId.of("America/New_York"));
                LocalTime startTimeCheck = startEST.toLocalTime();
                LocalTime endTimeCheck = endEST.toLocalTime();
                DayOfWeek startDayCheck = startEST.toLocalDate().getDayOfWeek();
                DayOfWeek endDayCheck = endEST.toLocalDate().getDayOfWeek();
                int startInt = startDayCheck.getValue();
                int endInt = endDayCheck.getValue();
                int weekStart = DayOfWeek.MONDAY.getValue();
                int weekEnd = DayOfWeek.FRIDAY.getValue();
                LocalTime busStart = LocalTime.of(8, 0, 0);
                LocalTime busEnd = LocalTime.of(22, 0, 0);

                //ensures that appointment starts on a week day
                if (startInt < weekStart || startInt > weekEnd ||
                        endInt < weekStart || endInt > weekEnd) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Business only operates Monday-Friday");
                    Optional<ButtonType> b = alert.showAndWait();
                    return;
                }

                //ensures appointment times are within business hours
                if (startTimeCheck.isBefore(busStart) || startTimeCheck.isAfter(busEnd)
                        || endTimeCheck.isBefore(busStart) || endTimeCheck.isAfter(busEnd)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Business hours are 8am-10pm EST: ");
                    Optional<ButtonType> b = alert.showAndWait();
                    return;
                }

                //ensures the start date is not after the end date
                if (startTimeDate.isAfter(endTimeDate)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment has a start time that is after the end time");
                    Optional<ButtonType> b = alert.showAndWait();
                    return;
                }

                //ensures the start time is not the same as the end time
                if (startTimeDate.isEqual(endTimeDate)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment has same start and end time");
                    Optional<ButtonType> b = alert.showAndWait();
                    return;
                }


                int newApptID = 1;
                for (Appointment appt : appointments) {
                    if (newApptID == appt.getApptID()) {
                        newApptID++;
                    }
                }
                int newCustID = Integer.parseInt(custID.getText());

                for (Appointment appointment: appointments)
                {
                    LocalDateTime startCheck = appointment.getStart();
                    LocalDateTime checkEnd = appointment.getEnd();

                    //ensures the new appointment is for a valid customer and does not overlap with an existing appointment
                    if ((newCustID == appointment.getCustID()) && (newApptID != appointment.getApptID()) &&
                            ((startTimeDate.isBefore(startCheck)) && (endTimeDate.isAfter(checkEnd))) ||
                            startTimeDate.isEqual(startCheck) || endTimeDate.isEqual(checkEnd) ||
                            (endTimeDate.isBefore(checkEnd) && endTimeDate.isAfter(startCheck)) ||
                            (startTimeDate.isAfter(startCheck) && startTimeDate.isBefore(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment overlaps with existing appointment.");
                        Optional<ButtonType> b = alert.showAndWait();
                        return;
                    }
                }

                //MySQL statement to add the new appointment into the database
                String insert = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, " +
                        "Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES " +
                        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                PreparedStatement statement = JDBC.connection.prepareStatement(insert);
                statement.setInt(1, newApptID);
                statement.setString(2, apptTitle.getText());
                statement.setString(3, apptDesc.getText());
                statement.setString(4, loc.getText());
                statement.setString(5, type.getText());
                statement.setTimestamp(6, Timestamp.valueOf(startUTC));
                statement.setTimestamp(7, Timestamp.valueOf(endUTC));
                statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(9, "admin");
                statement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                statement.setInt(11, 1);
                statement.setInt(12, Integer.parseInt(custID.getText()));
                statement.setInt(13, Integer.parseInt(ContactDB.findID(userID.getText())));
                statement.setInt(14, Integer.parseInt(ContactDB.findID(apptContact.getValue())));
                statement.execute();
            }
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/appointments.fxml")));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * cancels the addition of new data
     * @param actionEvent the cancel button being clicked
     * @throws IOException error message
     */
    public void onActionCancel(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/appointments.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initializes the Add Appointments screen
     * Lambda expression is contained to condense a for each loop to obtain all contact names
     * @throws SQLException database error
     */
    @FXML
    public void initialize() throws SQLException {
        ObservableList<Contact> contacts = ContactDB.getAll();
        ObservableList<String> allNames = FXCollections.observableArrayList();
        //LAMBDA EXPRESSION
        contacts.forEach(contact -> allNames.add(contact.getName()));
        apptContact.setItems(allNames);
    }

    /**
     * Populates the start and end time selections based on the date picked from the start date picker
     */
    @FXML
    public void onActionPopulate() {
        if (startDate != null) {
            ObservableList<String> times = FXCollections.observableArrayList();
            ObservableList<String> times2 = FXCollections.observableArrayList();
            TimeZone local = TimeZone.getDefault();
            TimeZone utc = TimeZone.getTimeZone("America/New_York");
            int i = local.getRawOffset() - utc.getRawOffset();
            i = i / 3600000;
            int j = 8 + i;
            int k = 2 + -i;
            LocalTime first = LocalTime.MIN.plusHours(j);
            LocalDateTime startOfDay = LocalDateTime.of(startDate.getValue(), first);
            LocalDateTime endOfDay = startOfDay.plusHours(13).plusMinutes(30);

            while (startOfDay.isBefore(endOfDay)) {
                times.add(String.valueOf(startOfDay));
                times2.add(String.valueOf(startOfDay));
                startOfDay = startOfDay.plusMinutes(15);
            }
            startTime.setItems(times);
            int a = 0;
            while (a < 3) {
                times2.add(String.valueOf(startOfDay));
                startOfDay = startOfDay.plusMinutes(15);
                a++;
            }
            endTime.setItems(times2);
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
        String dateTime = endTime.getValue();
        String date = dateTime.substring(0,10);
        LocalDate endOfDay = LocalDate.parse(date, formatter);
        endDate.setValue(endOfDay);
    }
}
