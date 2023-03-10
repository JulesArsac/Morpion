package morpion;

import ai.ConfigFileLoader;
import ai.Coup;
import ai.MultiLayerPerceptron;
import ai.SigmoidalTransferFunction;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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
    boolean isXturn = false;
    static boolean isMulti = false;
    String username1 = "Théodule";
    String username2 = "Feur";

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
    TextField soloname;
    @FXML
    TextField textName1;
    @FXML
    TextField textName2;
    @FXML
    Label labelName1;
    @FXML
    Label labelName2;

    @FXML
    Button b1;
    @FXML
    Button b2;
    @FXML
    Button b3;
    @FXML
    Button b4;
    @FXML
    Button b5;
    @FXML
    Button b6;
    @FXML
    Button b7;
    @FXML
    Button b8;
    @FXML
    Button b9;
    @FXML
    GridPane playGrid;

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
        Scene scene = new Scene(fxmlLoader.load(), 300, 400);
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
    void onMultiPlayerButtonClick(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("startMultiGame.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void getPlayMulti(ActionEvent event) throws IOException{
        labelName1.setVisible(false);
        labelName2.setVisible(false);
        if (textName1.getText().isBlank() && textName2.getText().isBlank()){
            labelName1.setVisible(true);
            labelName2.setVisible(true);
        }
        else if (textName1.getText().isBlank()){
            labelName1.setVisible(true);
        }
        else if (textName2.getText().isBlank()){
            labelName2.setVisible(true);
        }
        else {
            isMulti=true;
            root = FXMLLoader.load(getClass().getResource("game.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }

    }

    @FXML
    Button buttonValidate;

    @FXML
    Button buttonBackToLobby;

    @FXML
    void backToLobby(ActionEvent event) throws IOException {
        if (isMulti) {
            root = FXMLLoader.load(getClass().getResource("startMultiGame.fxml"));
        }
        else {
            root = FXMLLoader.load(getClass().getResource("startSoloGame.fxml"));
        }

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void onClickButtonValidate(ActionEvent event) throws IOException {
        backToMenu.setDisable(true);
        isMulti=false;
        try {
            if (easyRadio.isSelected()) {
                difficulty = "F";
                epochs = 1000;
            } else if (mediumRadio.isSelected()) {
                difficulty = "M";
                epochs = 10000;
            } else {
                difficulty = "D";
                epochs = 1000000;
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
                        backToMenu.setDisable(false);
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

    public void getPlay(ActionEvent actionEvent) {
        Button clickedButton = (Button) actionEvent.getSource();
        clickedButton.setDisable(true);
        String bId = clickedButton.getId();
        Image X = new Image("file:resources/images/X.png");
        Image O = new Image("file:resources/images/O.png");
        ImageView imageViewX = new ImageView(X);
        ImageView imageViewO = new ImageView(O);
        ImageView imageView;
        GridPane.setMargin(imageViewX, new Insets(25, 25, 25, 25));
        GridPane.setMargin(imageViewO, new Insets(25, 25, 25, 25));
        if (isXturn) isXturn = false;
        else isXturn = true;
        switch (bId) {
            case "b1":
                if (isXturn) imageView = imageViewX;
                else imageView = imageViewO;
                playGrid.getChildren().add(imageView);
                break;
            case "b2":
                if (isXturn) imageView = imageViewX;
                else imageView = imageViewO;
                GridPane.setColumnIndex(imageView, 1);
                playGrid.getChildren().add(imageView);
                break;
            case "b3":
                if (isXturn) imageView = imageViewX;
                else imageView = imageViewO;
                GridPane.setColumnIndex(imageView, 2);
                playGrid.getChildren().add(imageView);
                break;
            case "b4":
                if (isXturn) imageView = imageViewX;
                else imageView = imageViewO;
                GridPane.setRowIndex(imageView, 1);
                playGrid.getChildren().add(imageView);
                break;
            case "b5":
                if (isXturn) imageView = imageViewX;
                else imageView = imageViewO;
                GridPane.setRowIndex(imageView, 1);
                GridPane.setColumnIndex(imageView, 1);
                playGrid.getChildren().add(imageView);
                break;
            case "b6":
                if (isXturn) imageView = imageViewX;
                else imageView = imageViewO;
                GridPane.setRowIndex(imageView, 1);
                GridPane.setColumnIndex(imageView, 2);
                playGrid.getChildren().add(imageView);
                break;
            case "b7":
                if (isXturn) imageView = imageViewX;
                else imageView = imageViewO;
                GridPane.setRowIndex(imageView, 2);
                playGrid.getChildren().add(imageView);
                break;
            case "b8":
                if (isXturn) imageView = imageViewX;
                else imageView = imageViewO;
                GridPane.setRowIndex(imageView, 2);
                GridPane.setColumnIndex(imageView, 1);
                playGrid.getChildren().add(imageView);
                break;
            case "b9":
                if (isXturn) imageView = imageViewX;
                else imageView = imageViewO;
                GridPane.setRowIndex(imageView, 2);
                GridPane.setColumnIndex(imageView, 2);
                playGrid.getChildren().add(imageView);
                break;
        }
    }
}