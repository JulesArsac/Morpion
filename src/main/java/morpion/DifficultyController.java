package morpion;

import ai.ConfigFileLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DifficultyController {
    @FXML
    TextField ndEasy;
    @FXML
    TextField ndMedium;
    @FXML
    TextField ndHard;
    @FXML
    TextField learnEasy;
    @FXML
    TextField learnMedium;
    @FXML
    TextField learnHard;
    @FXML
    TextField layEasy;
    @FXML
    TextField layMedium;
    @FXML
    TextField layHard;


    public void initialize(){
        ConfigFileLoader configuration = new ConfigFileLoader();
        configuration.loadConfigFile("resources/config.txt");

        ndEasy.setText(Integer.toString(configuration.get("F").hiddenLayerSize));
        ndMedium.setText(Integer.toString(configuration.get("M").hiddenLayerSize));
        ndHard.setText(Integer.toString(configuration.get("D").hiddenLayerSize));

        learnEasy.setText(Double.toString(configuration.get("F").learningRate));
        learnMedium.setText(Double.toString(configuration.get("M").learningRate));
        learnHard.setText(Double.toString(configuration.get("D").learningRate));

        layEasy.setText(Integer.toString(configuration.get("F").numberOfhiddenLayers));
        layMedium.setText(Integer.toString(configuration.get("M").numberOfhiddenLayers));
        layHard.setText(Integer.toString(configuration.get("D").numberOfhiddenLayers));
    }



    @FXML
    RadioButton easy;

    @FXML
    RadioButton medium;

    @FXML
    RadioButton hard;

    @FXML
    void validateDifficulty() {
        if (easy.isSelected()) System.out.println("Easy");
        if (medium.isSelected()) System.out.println("Medium");
        if (hard.isSelected()) System.out.println("Hard");
    }

}
