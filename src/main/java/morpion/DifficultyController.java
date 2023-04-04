package morpion;

import ai.ConfigFileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
    @FXML
    Button quitButton;
    @FXML
    Button resetButton;
    @FXML
    Label errorMessage;
    @FXML
    Label successMessage;

    public void initialize(){
        ConfigFileLoader configuration = new ConfigFileLoader();
        configuration.loadConfigFile("resources/config.txt");

        // Configure all regex
        setNumericTextFormater(ndEasy);
        setNumericTextFormater(ndMedium);
        setNumericTextFormater(ndHard);
        setFloatTextFormater(learnEasy);
        setFloatTextFormater(learnMedium);
        setFloatTextFormater(learnHard);
        setNumericTextFormater(layEasy);
        setNumericTextFormater(layMedium);
        setNumericTextFormater(layHard);

        // Set the fields
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

    public void setNumericTextFormater(TextField textfield) {
        TextFormatter<Integer> formatter = new TextFormatter<>(new IntegerStringConverter(), 0,
                change -> {
                    String newText = change.getControlNewText();
                    if (newText.matches("\\d*")) {
                        return change;
                    }
                    return null;
                });
        textfield.setTextFormatter(formatter);
    }

    public void setFloatTextFormater(TextField textfield) {
        TextFormatter<Double> formatter = new TextFormatter<>(new DoubleStringConverter(), 0.0,
                change -> {
                    String newText = change.getControlNewText();
                    if (newText.matches("\\d*\\.?\\d*")) {
                        return change;
                    }
                    return null;
                });
        textfield.setTextFormatter(formatter);
    }

    @FXML
    void validateChoises() throws IOException {
        Boolean validate = true;
        String ndEasyValue = ndEasy.getText();
        String layEasyValue = layEasy.getText();
        String learnEasyValue = learnEasy.getText();
        String parameters = "F:" + ndEasyValue + ":" + learnEasyValue + ":" + layEasyValue + "\n";
        String ndMediumValue = ndMedium.getText();
        String layMediumValue = layMedium.getText();
        String learnMediumValue = learnMedium.getText();
        parameters += "M:" + ndMediumValue + ":" + learnMediumValue + ":" + layMediumValue + "\n";
        String ndHardValue = ndHard.getText();
        String layHardValue = layHard.getText();
        String learnHardValue = learnHard.getText();
        parameters += "D:" + ndHardValue + ":" + learnHardValue + ":" + layHardValue;

        if ((ndEasyValue.isBlank()) || (layEasyValue.isBlank()) || (learnEasyValue.isBlank()) || (ndMediumValue.isBlank()) || (layMediumValue.isBlank())
                || (learnMediumValue.isBlank()) || (ndHardValue.isBlank()) || (layHardValue.isBlank()) || (learnHardValue.isBlank())) {
            successMessage.setVisible(false);
            errorMessage.setVisible(true);
            return;
        }
        errorMessage.setVisible(false);
        successMessage.setVisible(true);
        File config = new File("resources/config.txt");
        FileWriter writer = new FileWriter(config, false);
        writer.write(parameters);
        writer.close();
    }

    @FXML
    void resetValues() throws IOException {
        String parameters = "F:256:0.1:2\nM:512:0.01:2\nD:1024:0.001:2";
        File config = new File("resources/config.txt");
        FileWriter writer = new FileWriter(config, false);
        writer.write(parameters);
        writer.close();
        initialize();
    }

    @FXML
    void quitDifficulty() {
        Stage stage = (Stage) quitButton.getScene().getWindow();
        stage.close();
    }
}