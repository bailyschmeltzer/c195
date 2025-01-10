package group;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Creates an application for scheduling appointments
 */
public class Main extends Application {

    /**
     * loads the database connection and starts the program
     * @param args args
     */
    public static void main(String[] args) {
        JDBC.openConnection();
        ResourceBundle rb = ResourceBundle.getBundle("resources/login", Locale.getDefault());
        if (Locale.getDefault() == Locale.FRANCE) {
            System.out.println(rb.getString("hello") + " " + rb.getString("world"));
        } else {
            System.out.println("hello world");
        }
        launch(args);
        JDBC.closeConnection();
    }

    /**
     * initializes the login screen
     * @param stage stage
     * @throws Exception error message
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/login.fxml")));
        Scene scene = new Scene(parent, 200, 350);
        stage.setTitle("Scheduling Application Login");
        stage.setScene(scene);
        stage.show();
    }
}
