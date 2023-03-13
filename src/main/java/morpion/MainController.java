package morpion;

import ai.ConfigFileLoader;
import ai.Coup;
import ai.MultiLayerPerceptron;
import ai.SigmoidalTransferFunction;
import javafx.animation.*;
import javafx.application.Platform;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Arrays;
import java.util.TimerTask;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static ai.Test.loadCoupsFromFile;
import static ai.Test.play;

public class MainController {

    double epochs = 1000000;
    private Parent root;
    private Scene scene;
    private Stage stage;
    private String difficulty = "M";
    boolean isXturn = false;
    static boolean isMulti = false;
    String username1 = "Quoi";
    String username2 = "Feur";
    private Timeline delayBackground;
    private double[] gameArray = new double[9];
    ImageView backgroundTop;
    ImageView backgroundBottom;

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
    Pane mainPane;

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
    void openRules() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("rules.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 700);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("TicTacToe Rules");
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
        delayBackground.stop();
        root = FXMLLoader.load(getClass().getResource("startSoloGame.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void onMultiPlayerButtonClick(ActionEvent event) throws IOException {
        delayBackground.stop();
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
    void onClickButtonValidate(ActionEvent event) {
        backToMenu.setDisable(true);
        isMulti=false;
        try {
            if (easyRadio.isSelected()) {
                difficulty = "F";
                epochs = 10000000;
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
                Task<Integer> trainingTask = new Task<>() {
                    @Override
                    protected Integer call() {
                        System.out.println();
                        System.out.println("START TRAINING ...");
                        System.out.println();

                        double error = 0.0;
                        MultiLayerPerceptron net = new MultiLayerPerceptron(layers, configuration.get(difficulty).learningRate, new SigmoidalTransferFunction());

                        System.out.println("---");
                        System.out.println("Load data ...");
                        HashMap<Integer, Coup> mapTrain = loadCoupsFromFile("./resources/train_dev_test/train.txt");
                        // HashMap<Integer, Coup> mapDev = loadCoupsFromFile("./resources/train_dev_test/dev.txt");
                        // HashMap<Integer, Coup> mapTest = loadCoupsFromFile("./resources/train_dev_test/test.txt");
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
        //clickedButton.setDisable(true);
        String bId = clickedButton.getId();
        Image X = new Image("file:resources/images/X.png");
        Image O = new Image("file:resources/images/O.png");
        ImageView imageViewX = new ImageView(X);
        ImageView imageViewO = new ImageView(O);
        ImageView imageView = null;
        GridPane.setMargin(imageViewX, new Insets(25, 25, 25, 25));
        GridPane.setMargin(imageViewO, new Insets(25, 25, 25, 25));
        isXturn = !isXturn;
        if (!isXturn){
            MultiLayerPerceptron net = MultiLayerPerceptron.load("src/main/resources/models/model_2_0.01_64");
            HashMap<Integer, Coup> mapTest = loadCoupsFromFile("./resources/train_dev_test/test.txt");
            Coup coup = mapTest.get((int)(Math.round(Math.random() * mapTest.size())));
            coup.addInBoard(gameArray);
            double min=100;
            int index = 0;
            double[] probaCoups = play(net, coup);
            for (int i=0; i < probaCoups.length; i++){
                if (probaCoups[i] < min){
                    min = probaCoups[i];
                    index = i;
                }
            }
            gameArray[index]=1;
            if (index>3 && index <=6){
                imageView = imageViewO;
                GridPane.setRowIndex(imageView, 1);
                GridPane.setColumnIndex(imageView, index%3);
            }
            else if (index > 6) {
                imageView = imageViewO;
                GridPane.setRowIndex(imageView, 2);
                GridPane.setColumnIndex(imageView, index%3);
            }
            else {
                imageView = imageViewO;
                GridPane.setRowIndex(imageView, 0);
                GridPane.setColumnIndex(imageView, index%3);
            }
        }
        switch (bId) {
            case "b1":
                if (isXturn){
                    imageView = imageViewX;
                    gameArray[0]=-1;
                }
                else {
                    imageView = imageViewO;
                    gameArray[0]=1;
                }

                break;
            case "b2":
                if (isXturn){
                    imageView = imageViewX;
                    gameArray[1]=-1;
                }
                else {
                    imageView = imageViewO;
                    gameArray[1]=1;
                }
                GridPane.setColumnIndex(imageView, 1);
                break;
            case "b3":
                if (isXturn){
                    imageView = imageViewX;
                    gameArray[2]=-1;
                }
                else {
                    imageView = imageViewO;
                    gameArray[2]=1;
                }
                GridPane.setColumnIndex(imageView, 2);
                break;
            case "b4":
                if (isXturn){
                    imageView = imageViewX;
                    gameArray[3]=-1;
                }
                else {
                    imageView = imageViewO;
                    gameArray[3]=1;
                }
                GridPane.setRowIndex(imageView, 1);
                break;
            case "b5":
                if (isXturn){
                    imageView = imageViewX;
                    gameArray[4]=-1;
                }
                else {
                    imageView = imageViewO;
                    gameArray[4]=1;
                }
                GridPane.setRowIndex(imageView, 1);
                GridPane.setColumnIndex(imageView, 1);
                break;
            case "b6":
                if (isXturn){
                    imageView = imageViewX;
                    gameArray[5]=-1;
                }
                else {
                    imageView = imageViewO;
                    gameArray[5]=1;
                }
                GridPane.setRowIndex(imageView, 1);
                GridPane.setColumnIndex(imageView, 2);
                break;
            case "b7":
                if (isXturn){
                    imageView = imageViewX;
                    gameArray[6]=-1;
                }
                else {
                    imageView = imageViewO;
                    gameArray[6]=1;
                }
                GridPane.setRowIndex(imageView, 2);
                break;
            case "b8":
                if (isXturn){
                    imageView = imageViewX;
                    gameArray[7]=-1;
                }
                else {
                    imageView = imageViewO;
                    gameArray[7]=1;
                }
                GridPane.setRowIndex(imageView, 2);
                GridPane.setColumnIndex(imageView, 1);
                break;
            case "b9":
                if (isXturn){
                    imageView = imageViewX;
                    gameArray[8]=-1;
                }
                else {
                    imageView = imageViewO;
                    gameArray[8]=1;
                }
                GridPane.setRowIndex(imageView, 2);
                GridPane.setColumnIndex(imageView, 2);
                break;
        }
        System.out.println(Arrays.toString(gameArray));
        double winner = checkWinner(gameArray);
        if (winner == 1.0) {
            System.out.println("O has won!");
        } else if (winner == -1.0) {
            System.out.println("X has won!");
        } else {
            System.out.println("Game in progress!");
        }
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(500));
        fade.setFromValue(0);
        fade.setToValue(10);
        fade.setNode(imageView);
        fade.play();
        playGrid.getChildren().add(imageView);
    }

    public static double checkWinner(double[] board) {
        for (int i = 0; i < 9; i += 3) {
            if (board[i] == board[i+1] && board[i+1] == board[i+2]) {
                if (board[i] == 1.0) {
                    return 1.0;
                } else if (board[i] == -1.0) {
                    return -1.0;
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[i] == board[i+3] && board[i+3] == board[i+6]) {
                if (board[i] == 1.0) {
                    return 1.0;
                } else if (board[i] == -1.0) {
                    return -1.0;
                }
            }
        }
        if (board[0] == board[4] && board[4] == board[8]) {
            if (board[0] == 1.0) {
                return 1.0;
            } else if (board[0] == -1.0) {
                return -1.0;
            }
        }
        if (board[2] == board[4] && board[4] == board[6]) {
            if (board[2] == 1.0) {
                return 1.0;
            } else if (board[2] == -1.0) {
                return -1.0;
            }
        }

        return 0.0;
    }

    @FXML
    void test(){
        MultiLayerPerceptron net = MultiLayerPerceptron.load("src/main/resources/models/model_2_256_0.1.srl");
        double[] board = {0, 0, 0, -1, 1, 1, -1, -1, 0};
        double[] output = net.forwardPropagation(board);
        System.out.println(Arrays.toString(output));
        double[] board2 = {0, 0, 0, -1, 1, 1, -1, 0, 0};
        output = net.forwardPropagation(board2);
        System.out.println(Arrays.toString(output));
    }


    public void goJamy(){

        Image animImage;
        int img = (int) (Math.random() * (3 - 1)) + 1;
        if (img == 1){
            animImage = new Image("file:resources/images/X.png");
        }
        else {
            animImage = new Image("file:resources/images/O.png");
        }
        ImageView imageTitleScreen = new ImageView(animImage);

        double randOpacity = 0.1 + Math.random() * (0.9 - 0.1);
        double randSpeedMove = (int) (Math.random() * (8000 - 4000)) + 10000;
        double randSize =  0.7 + Math.random() * (1.7 - 0.7);
        double randPos = (int) (Math.random() * 635);
        double randRot = Math.random() * 360;

        double imageWidth = imageTitleScreen.getBoundsInLocal().getWidth();
        double imageHeight = imageTitleScreen.getBoundsInLocal().getHeight();

        imageTitleScreen.setTranslateX(-imageWidth / 2 + randPos);
        imageTitleScreen.setTranslateY(-imageHeight / 2 - 100);
        imageTitleScreen.setRotate(randRot);
        imageTitleScreen.setOpacity(randOpacity);
        imageTitleScreen.setScaleX(randSize);
        imageTitleScreen.setScaleY(randSize);
        mainPane.getChildren().add(imageTitleScreen);
        imageTitleScreen.toBack();
        backgroundTop.toBack();

        TranslateTransition translate = new TranslateTransition();
        translate.setByY(900);
        translate.setDuration(Duration.millis(randSpeedMove));
        translate.setCycleCount(1);
        translate.setNode(imageTitleScreen);
        translate.setOnFinished(event -> mainPane.getChildren().remove(imageTitleScreen));
        translate.play();

        RotateTransition rotate = new RotateTransition();
        rotate.setNode(imageTitleScreen);
        rotate.setDuration(Duration.millis(10000));
        rotate.setCycleCount(TranslateTransition.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setByAngle(360);
        rotate.setAxis(Rotate.Z_AXIS);
        rotate.play();
    }

    public class renderBackground extends TimerTask {
        public void run() {
            Platform.runLater(MainController.this::goJamy);
        }
    }

    public void initialize() {
        if (singlePlayerButton != null){ //Si on est dans l'écran titre
            Image backgroundTopimg = new Image("file:resources/images/backgroundTop.png");
            Image backgroundBottomimg = new Image("file:resources/images/backgroundBottom.png");
            backgroundTop = new ImageView(backgroundTopimg);
            backgroundBottom = new ImageView(backgroundBottomimg);
            mainPane.getChildren().add(backgroundTop);
            mainPane.getChildren().add(backgroundBottom);
            backgroundTop.toBack();
            backgroundBottom.toBack();
            delayBackground = new Timeline();
            delayBackground.setCycleCount(Timeline.INDEFINITE);
            delayBackground.getKeyFrames().add(new KeyFrame(Duration.millis(700), event -> goJamy()));
            delayBackground.play();
        }
        else if (b1 != null) { //Si on est dans le jeu
            for (int i=0; i<9; i++){
                gameArray[i]=0;
            }
        }
    }
}