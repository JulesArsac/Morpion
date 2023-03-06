package morpion;

import ai.ConfigFileLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("trainingView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("Morpion");
        stage.getIcons().add(new Image("file:resources/images/Enorme.jpg"));
        stage.setScene(scene);
        stage.show();


        ConfigFileLoader c = new ConfigFileLoader();
        c.loadConfigFile("resources/config.txt");
        System.out.println(c.get("F").hiddenLayerSize);
    }

    public static void main(String[] args) {
        launch();
    }
}