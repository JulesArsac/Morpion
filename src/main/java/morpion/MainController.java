package morpion;

import ai.ConfigFileLoader;
import ai.Coup;
import ai.MultiLayerPerceptron;
import ai.SigmoidalTransferFunction;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static ai.Test.loadCoupsFromFile;

public class MainController {

    double error = 0.0;
    double epochs = 1000000;

    private Parent root;
    private Scene scene;
    private Stage stage;

    private String difficulty = "M";
    @FXML
    Label textError;

    @FXML
    ProgressBar progressBar;

    @FXML
    Button singlePlayerButton;

    @FXML
    Button backToMenu;

    @FXML
    RadioButton easyRadio;

    @FXML
    RadioButton mediumRadio;

    @FXML
    RadioButton hardRadio;

    @FXML
    void openDifficultySettings() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("difficultySettings.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Difficulty Settings");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void openModelSettings() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("deleteConfigs.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Manage models");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void backToMenu(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("titleScreen.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void onSinglePlayerButtonClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("startSoloGame.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    Button buttonValidate;

    @FXML
    void onClickButtonValidate(ActionEvent event) throws IOException {

        try {

            if (easyRadio.isSelected()) {
                difficulty = "F";
            } else if (mediumRadio.isSelected()) {
                difficulty = "M";
            } else {
                difficulty = "D";
                epochs = 100000;
            }
            ConfigFileLoader configuration = new ConfigFileLoader();
            configuration.loadConfigFile("resources/config.txt");

            int[] layers = new int[configuration.get(difficulty).numberOfhiddenLayers + 2];
            layers[0] = 9;
            for (int j = 1; j < configuration.get(difficulty).numberOfhiddenLayers; j++) {
                layers[j] = configuration.get(difficulty).hiddenLayerSize;
            }
            layers[configuration.get(difficulty).numberOfhiddenLayers + 1] = 9;

            String modelpath = "src/main/resources/models/model_" + configuration.get(difficulty).numberOfhiddenLayers + "_" + configuration.get(difficulty).learningRate + "_" + configuration.get(difficulty).hiddenLayerSize;

            File model = new File(modelpath);
            if (model.exists() && !model.isDirectory()) {

                MultiLayerPerceptron net = MultiLayerPerceptron.load(modelpath);

                root = FXMLLoader.load(getClass().getResource("game.fxml"));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
            }
            else {
                progressBar.setVisible(true);
                textError.setVisible(true);
                buttonValidate.setDisable(true);
                Task<Integer> trainingTask = new Task<Integer>() {
                    @Override
                    protected Integer call() throws Exception {
                        System.out.println();
                        System.out.println("START TRAINING ...");
                        System.out.println();

                        double error = 0.0;
                        MultiLayerPerceptron net = new MultiLayerPerceptron(layers, configuration.get(difficulty).learningRate, new SigmoidalTransferFunction());

                        System.out.println("---");
                        System.out.println("Load data ...");
                        HashMap<Integer, Coup> mapTrain = loadCoupsFromFile("./resources/train_dev_test/train.txt");
                        HashMap<Integer, Coup> mapDev = loadCoupsFromFile("./resources/train_dev_test/dev.txt");
                        HashMap<Integer, Coup> mapTest = loadCoupsFromFile("./resources/train_dev_test/test.txt");
                        System.out.println("---");
                        //TRAINING ...
                        for (int i = 0; i < epochs; i++) {

                            Coup c = null;
                            while (c == null)
                                c = mapTrain.get((int) (Math.round(Math.random() * mapTrain.size())));


                            error += net.backPropagate(c.in, c.out);


                            if (i % 10000 == 0 && difficulty != "D") {
                                System.out.println("Error at step " + i + " is " + (error / (double) i));
                                updateMessage("Error at step "+i+" is "+ (error/(double)i));
                                updateProgress(i, epochs);
                            }
                            else if (i % 100 == 0 && difficulty == "D") {
                                System.out.println("Error at step " + i + " is " + (error / (double) i));
                                updateMessage("Error at step "+i+" is "+ (error/(double)i));
                                updateProgress(i, epochs);
                            }
                        }

                        error /= epochs ;

                        textError.setTextFill(Color.valueOf("#17C42B"));

                        System.out.println("Learning completed! Error is "+error);
                        updateMessage("Learning completed! Error is "+error);
                        updateProgress(epochs, epochs);


                        net.save(modelpath);
                        buttonValidate.setDisable(false);
                        return 0;
                    }
                };
                Thread trainingThread = new Thread(trainingTask);
                progressBar.progressProperty().bind(trainingTask.progressProperty());
                textError.textProperty().bind(trainingTask.messageProperty());
                trainingThread.start();
            }
        }
        catch (Exception e) {
            System.out.println("Test.test()");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}