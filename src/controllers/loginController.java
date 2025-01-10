package controllers;

import helper.Appointment;
import helper.AppointmentDB;
import helper.UserDB;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


public class loginController implements Initializable {

    @FXML
    Label locationText;
    @FXML
    TextField loginLocation;
    @FXML
    Label loginPasswordLabel;
    @FXML
    Label loginUsernameLabel;
    @FXML
    Label loginLabel;
    @FXML
    PasswordField loginPassword;
    @FXML
    Button loginButton;
    @FXML
    TextField loginUsername;
    @FXML
    Button exitButton;

    /**
     * Attempts to sing in the user with the provided credentials
     */
    public void onActionLogin() {
        //attempts to connect to the database and login the user credentials
        try {
            ObservableList<Appointment> getAllAppointments = AppointmentDB.getAll();
            LocalDateTime minus15Min = LocalDateTime.now().minusMinutes(15);
            LocalDateTime plus15Min = LocalDateTime.now().plusMinutes(15);
            LocalDateTime start;
            int getID = 0;
            LocalDateTime display = null;
            boolean upcoming = false;

            ResourceBundle resource = ResourceBundle.getBundle("resources/login", Locale.getDefault());

            String username = loginUsername.getText();
            String password = loginPassword.getText();
            int ID = UserDB.validateUser(username, password);

            FileWriter fileWriter = new FileWriter("login_activity.txt", true);
            PrintWriter outputFile = new PrintWriter(fileWriter);

            //ensures login was successful
            if (ID > 0) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/MainScreen.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                outputFile.print("The user: " + username + " logged in successfully at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");
                //checks for any appointments within 15 minutes of login
                for (Appointment appt: getAllAppointments) {
                    start = appt.getStart();
                    if ((start.isAfter(minus15Min) || start.isEqual(minus15Min)) &&
                            (start.isBefore(plus15Min) || start.isEqual(plus15Min))) {
                        getID = appt.getApptID();
                        display = start;
                        upcoming = true;
                    }
                }
                //notifies of any appointments within 15 minutes, or notifies that there are none
                if (upcoming) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Upcoming appointment within 15 minutes: "
                        + getID + " with a start time of: " + display);
                    Optional<ButtonType> b = alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "There are no upcoming appointments");
                    Optional<ButtonType> b = alert.showAndWait();
                }
            //notifies of an incorrect login attempt
            } else if (ID < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(resource.getString("Error"));
                alert.setContentText(resource.getString("Incorrect"));
                alert.show();
                outputFile.print("The user: " + username + " failed  a login attempt at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");
            }
            outputFile.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancels the login attempt
     * @param actionEvent the cancel button being clicked
     */
    @FXML
    public void onActionCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the login screen
     * @param url url
     * @param resource resource
     */
    @Override
    public void initialize(URL url, ResourceBundle resource) {
        //attempts to connect to the database and load the language of the users computer
        try {
            Locale loc = Locale.getDefault();
            Locale.setDefault(loc);
            ZoneId id = ZoneId.systemDefault();
            loginLocation.setText(String.valueOf(id));
            resource = ResourceBundle.getBundle("resources/login", Locale.getDefault());
            loginLabel.setText(resource.getString("Login"));
            loginUsernameLabel.setText(resource.getString("Username"));
            loginPasswordLabel.setText(resource.getString("Password"));
            loginButton.setText(resource.getString("Login"));
            exitButton.setText(resource.getString("Exit"));
            locationText.setText(resource.getString("Location"));
        } catch(MissingResourceException e) {
            System.out.println("Resource file is missing: " + e);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
