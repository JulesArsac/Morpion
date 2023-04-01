package morpion;

import ai.ConfigFileLoader;
import ai.Coup;
import ai.MultiLayerPerceptron;
import ai.SigmoidalTransferFunction;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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

import java.util.*;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static ai.Test.loadCoupsFromFile;

public class MainController {
    private double epochs = 1000000;
    private Parent root;
    private Scene scene;
    private Stage stage;
    private String difficulty = "M";
    private Player playerToPlay;
    private static boolean isMulti = false;
    private static Player player1 = new Player();
    private static Player player2 = new Player();
    private Timeline delayBackground;
    private double[] gameArray = new double[9];
    private ImageView backgroundTop;
    private ImageView backgroundBottom;
    private static MultiLayerPerceptron net;
    private int score1;
    private int score2;

    @FXML
    Label textError;

    @FXML
    ProgressBar progressBar;

    @FXML
    Button singlePlayerButton;

    @FXML
    Button backToMenu;

    @FXML
    Button replayButton;

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
    Label winLabel;
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
            player1.setName(textName1.getText());
            player2.setName(textName2.getText());
            player2.setIa(false);
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
        player1.setName(soloname.getText());
        player2.setName("IA");
        player2.setIa(true);
        backToMenu.setDisable(true);
        isMulti=false;
        try {
            if (easyRadio.isSelected()) {
                difficulty = "F";
                epochs = 10000000;
            } else if (mediumRadio.isSelected()) {
                difficulty = "M";
                epochs = 1000000;
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
                net = MultiLayerPerceptron.load(modelpath);
                root = FXMLLoader.load(getClass().getResource("game.fxml"));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
                initialize();
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
                        net = new MultiLayerPerceptron(layers, configuration.get(difficulty).learningRate, new SigmoidalTransferFunction());

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
        clickedButton.setDisable(true);
        String bId = clickedButton.getId();
        game(bId);
    }

    public void game(String buttonId) {
        Image X = new Image("file:resources/images/X.png");
        Image O = new Image("file:resources/images/O.png");
        ImageView imageView;
        if (playerToPlay.getPiece() == -1.0){
            imageView = new ImageView(X);
        }
        else {
            imageView = new ImageView(O);
        }
        GridPane.setMargin(imageView, new Insets(25, 25, 25, 25));
        if (playerToPlay.isIa()){
            int index = iaPlay();
            gameArray[index]=playerToPlay.getPiece();
            switch (index) {
                case 0 -> b1.setDisable(true);
                case 1 -> {
                    b2.setDisable(true);
                    GridPane.setColumnIndex(imageView, 1);
                }
                case 2 -> {
                    b3.setDisable(true);
                    GridPane.setColumnIndex(imageView, 2);
                }
                case 3 -> {
                    b4.setDisable(true);
                    GridPane.setRowIndex(imageView, 1);
                }
                case 4 -> {
                    b5.setDisable(true);
                    GridPane.setRowIndex(imageView, 1);
                    GridPane.setColumnIndex(imageView, 1);
                }
                case 5 -> {
                    b6.setDisable(true);
                    GridPane.setRowIndex(imageView, 1);
                    GridPane.setColumnIndex(imageView, 2);
                }
                case 6 -> {
                    b7.setDisable(true);
                    GridPane.setRowIndex(imageView, 2);
                }
                case 7 -> {
                    b8.setDisable(true);
                    GridPane.setRowIndex(imageView, 2);
                    GridPane.setColumnIndex(imageView, 1);
                }
                case 8 -> {
                    b9.setDisable(true);
                    GridPane.setRowIndex(imageView, 2);
                    GridPane.setColumnIndex(imageView, 2);
                }
            }
        }
        else {
            switch (buttonId) {
                case "b1" -> gameArray[0] = playerToPlay.getPiece();
                case "b2" -> {
                    gameArray[1] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(imageView, 1);
                }
                case "b3" -> {
                    gameArray[2] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(imageView, 2);
                }
                case "b4" -> {
                    gameArray[3] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(imageView, 0);
                    GridPane.setRowIndex(imageView, 1);
                }
                case "b5" -> {
                    gameArray[4] = playerToPlay.getPiece();
                    GridPane.setRowIndex(imageView, 1);
                    GridPane.setColumnIndex(imageView, 1);
                }
                case "b6" -> {
                    gameArray[5] = playerToPlay.getPiece();
                    GridPane.setRowIndex(imageView, 1);
                    GridPane.setColumnIndex(imageView, 2);
                }
                case "b7" -> {
                    gameArray[6] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(imageView, 0);
                    GridPane.setRowIndex(imageView, 2);
                }
                case "b8" -> {
                    gameArray[7] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(imageView, 1);
                    GridPane.setRowIndex(imageView, 2);
                }
                case "b9" -> {
                    gameArray[8] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(imageView, 2);
                    GridPane.setRowIndex(imageView, 2);
                }
                case "none" -> {
                    return;
                }
            }
        }
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(500));
        fade.setFromValue(0);
        fade.setToValue(10);
        fade.setNode(imageView);
        fade.play();
        playGrid.getChildren().add(imageView);
        if (checkWinner(gameArray)) {
            System.out.println(checkWinner(gameArray));
            winLabel.setText(playerToPlay.getName() + " a gagné !");
            winLabel.setVisible(true);
            replayButton.setVisible(true);
            playGrid.setDisable(true);
            return;
        }
        if (playerToPlay == player1){
            playerToPlay = player2;
        }
        else {
            playerToPlay = player1;
        }
        if (player2.isIa()){
            game("none");
        }
        return;

    }

    public void resetGrid() {
        ObservableList<Node> children = playGrid.getChildren();
        List<Node> nodesToRemove = new ArrayList<>(); // Nouvelle liste pour stocker les noeuds à supprimer
        for (Node child : children) {
            if (child instanceof ImageView) {
                nodesToRemove.add(child);
            }
            else if (child instanceof Button) {
                child.setDisable(false);
            }
        }
        playGrid.setDisable(false);
        winLabel.setText("");
        playGrid.getChildren().removeAll(nodesToRemove); // Supprimer les noeuds de la liste principale en une seule fois
        for (int i=0; i<9; i++) {
            gameArray[i] = 0;
        }
        game("none");
    }

    public int iaPlay(){
        double min=100;
        int index = 0;
        double[] probaCoups = net.forwardPropagation(gameArray);
        for (int i=0; i < probaCoups.length; i++){
            if (probaCoups[i] < min && gameArray[i] == 0){
                System.out.println(gameArray[i]);
                System.out.println(i);
                min = probaCoups[i];
                index = i;
            }
        }
        return index;
    }

    public static boolean checkWinner(double[] board) {
        for (int i = 0; i < 9; i += 3) {
            if (board[i] == board[i+1] && board[i+1] == board[i+2] && board[i] != 0.0) {
                System.out.println("1");
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[i] == board[i+3] && board[i+3] == board[i+6] && board[i] != 0.0) {
                System.out.println("2");
                return true;
            }
        }
        if (board[0] == board[4] && board[4] == board[8] && board[0] != 0.0) {
            System.out.println("3");
            return true;
        }
        if (board[2] == board[4] && board[4] == board[6] && board[2] != 0.0) {
            System.out.println("4");
            return true;
        }
        return false;
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

    // Initialize menu
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
        else if (playGrid != null) { //Si on est dans le jeu
            for (int i=0; i<9; i++){
                gameArray[i]=0;
            }
            final int randPlayer = (int) (Math.random() * (3 - 1)) + 1;
            if (randPlayer == 1){
                player1.setPiece(-1.0);
                player2.setPiece(1.0);
                playerToPlay = player1;
            }
            else {
                player1.setPiece(1.0);
                player2.setPiece(-1.0);
                playerToPlay = player2;
            }
            game("none");
        }
    }
}