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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;
import java.io.File;
import java.io.IOException;

import static ai.Test.loadCoupsFromFile;

public class MainController {
    private final double epochs = 1000000;
    private Parent root;
    private Scene scene;
    private Stage stage;
    private String difficulty = "M";
    private static Player playerToPlay;
    private static final Player player1 = new Player();
    private static final Player player2 = new Player();
    private Timeline delayBackground;
    private final double[] gameArray = new double[9];
    private ImageView backgroundTop;
    private static MultiLayerPerceptron network;
    private static MediaPlayer mediaPlayer;
    private static MediaPlayer newMediaPlayer;
    private static boolean isMuted = false;
    private static double volume = 1.0;

    @FXML
    Slider volumeSlider;
    @FXML
    MenuItem soundMenu;
    @FXML
    Label textError;
    @FXML
    Label textNameError;
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
    Label errorName1;
    @FXML
    Label errorName2;
    @FXML
    Label infoLabel;
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
    Label labelScore1;
    @FXML
    Label labelScore2;
    @FXML
    GridPane playGrid;
    @FXML
    Pane mainPane;
    @FXML
    Button buttonValidate;
    @FXML
    Button buttonBackToLobby;

    // Initialize menu
    public void initialize() {
        setupAudio();
        if (singlePlayerButton != null){ // If we are on the title screen
            Image backgroundTopimg = new Image("file:resources/images/backgroundTop.png");
            Image backgroundBottomimg = new Image("file:resources/images/backgroundBottom.png");
            backgroundTop = new ImageView(backgroundTopimg);
            ImageView backgroundBottom = new ImageView(backgroundBottomimg);
            mainPane.getChildren().add(backgroundTop);
            mainPane.getChildren().add(backgroundBottom);
            backgroundTop.toBack();
            backgroundBottom.toBack();
            delayBackground = new Timeline();
            delayBackground.setCycleCount(Timeline.INDEFINITE);
            delayBackground.getKeyFrames().add(new KeyFrame(Duration.millis(700), event -> animateTitleScreenBackground()));
            delayBackground.play();
        }
        else if (playGrid != null) { // If we are in the game screen
            changeMusicTrack("./src/main/resources/sounds/MorpionOST.mp3");
            labelScore1.setText(player1.getName() + "\n" + "Score: 0");
            labelScore2.setText(player2.getName() + "\n" + "Score: 0");
            for (int i=0; i<9; i++){
                gameArray[i]=0;
            }
            setRandomPlayer();
            if (!player2.isIa()){ // If we're in multiplayer
                infoLabel.setText(playerToPlay.getName() + "'s turn");
            }
            mainGameLogic("none");
        }
    }

    // Window-opening functions
    @FXML
    public void openDifficultySettings() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("difficultySettings.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Difficulty Settings");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void openModelSettings() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("deleteConfigs.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 400);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Manage models");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void openRules() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("rules.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 700);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("TicTacToe Rules");
        stage.setScene(scene);
        stage.show();
    }


    // Screen-changing functions
    @FXML
    public void onSinglePlayerButtonClick(ActionEvent event) throws IOException {
        changeMusicTrack("./src/main/resources/sounds/MorpionOST2.mp3");
        delayBackground.stop();
        root = FXMLLoader.load(getClass().getResource("startSoloGame.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void onMultiPlayerButtonClick(ActionEvent event) throws IOException {
        changeMusicTrack("./src/main/resources/sounds/MorpionOST2.mp3");
        delayBackground.stop();
        root = FXMLLoader.load(getClass().getResource("startMultiGame.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void backToMenu(ActionEvent event) throws IOException {
        changeMusicTrack("./src/main/resources/sounds/MorpionOST3.mp3");
        root = FXMLLoader.load(getClass().getResource("titleScreen.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void getPlaySolo(ActionEvent event) {
        textNameError.setVisible(false);
        if (soloname.getText().isEmpty()) {
            textNameError.setVisible(true);
            return;
        }
        player1.setName(soloname.getText());
        player2.setName("IA");
        player2.setIa(true);
        backToMenu.setDisable(true);
        try {
            if (easyRadio.isSelected()) {
                difficulty = "F";
            } else if (mediumRadio.isSelected()) {
                difficulty = "M";
            } else {
                difficulty = "D";
            }
            ConfigFileLoader configuration = new ConfigFileLoader();
            configuration.loadConfigFile("resources/config.txt");

            int[] layers = new int[configuration.get(difficulty).numberOfhiddenLayers + 2];
            layers[0] = 9;

            for (int j = 1; j < configuration.get(difficulty).numberOfhiddenLayers; j++) {
                layers[j] = configuration.get(difficulty).hiddenLayerSize;
            }

            layers[configuration.get(difficulty).numberOfhiddenLayers + 1] = 9;
            String modelpath = "src/main/resources/models/model_" + configuration.get(difficulty).numberOfhiddenLayers + "_" + configuration.get(difficulty).learningRate + "_" + configuration.get(difficulty).hiddenLayerSize + ".srl";

            File model = new File(modelpath);
            if (model.exists() && !model.isDirectory()) { // If the model is already trained
                network = MultiLayerPerceptron.load(modelpath);
                root = FXMLLoader.load(getClass().getResource("game.fxml"));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
            }
            else { // We need to train the model
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
                        network = new MultiLayerPerceptron(layers, configuration.get(difficulty).learningRate, new SigmoidalTransferFunction());

                        System.out.println("---");
                        System.out.println("Load data ...");
                        HashMap<Integer, Coup> mapTrain = loadCoupsFromFile("./resources/train_dev_test/train.txt");
                        System.out.println("---");
                        //TRAINING ...
                        for (int i = 0; i < epochs; i++) {

                            Coup c = null;
                            while (c == null)
                                c = mapTrain.get((int) (Math.round(Math.random() * mapTrain.size())));


                            error += network.backPropagate(c.in, c.out);


                            if (i % 10000 == 0) {
                                System.out.println("Error at step " + i + " is " + (error / (double) i));
                                updateMessage("Error at step "+i+" is "+ (error/(double)i));
                                updateProgress(i, epochs);
                            }
                        }

                        error /= epochs ;

                        textError.setTextFill(Color.valueOf("#004f09"));

                        System.out.println("Learning completed! Error is "+error);
                        updateMessage("Learning completed! Error is "+error);
                        updateProgress(epochs, epochs);


                        network.save(modelpath);
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

    @FXML
    public void getPlayMulti(ActionEvent event) throws IOException{
        errorName1.setVisible(false);
        errorName2.setVisible(false);
        if (textName1.getText().isBlank() && textName2.getText().isBlank()){
            errorName1.setVisible(true);
            errorName2.setVisible(true);
        }
        else if (textName1.getText().isBlank()){
            errorName1.setVisible(true);
        }
        else if (textName2.getText().isBlank()){
            errorName2.setVisible(true);
        }
        else {
            player1.setName(textName1.getText());
            player2.setName(textName2.getText());
            player2.setIa(false);
            root = FXMLLoader.load(getClass().getResource("game.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    public void backToLobby(ActionEvent event) throws IOException {
        changeMusicTrack("./src/main/resources/sounds/MorpionOST2.mp3");
        player1.setScore(0);
        player2.setScore(0);
        if (!player2.isIa()) {
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


    // Game-logic functions
    public void getClickedButton(ActionEvent actionEvent) {
        Button clickedButton = (Button) actionEvent.getSource();
        clickedButton.setDisable(true);
        String bId = clickedButton.getId();
        mainGameLogic(bId);
    }

    public void mainGameLogic(String buttonId) {
        Image X = new Image("file:resources/images/X.png");
        Image O = new Image("file:resources/images/O.png");
        ImageView pieceImage;
        if (playerToPlay.getPiece() == -1.0){
            pieceImage = new ImageView(X);
        }
        else {
            pieceImage = new ImageView(O);
        }
        GridPane.setMargin(pieceImage, new Insets(25, 25, 25, 25));
        if (playerToPlay.isIa()){ // If the current player is the IA
            int index = getIAMove();
            gameArray[index] = playerToPlay.getPiece();
            switch (index) {
                case 0 -> b1.setDisable(true);
                case 1 -> {
                    b2.setDisable(true);
                    GridPane.setColumnIndex(pieceImage, 1);
                }
                case 2 -> {
                    b3.setDisable(true);
                    GridPane.setColumnIndex(pieceImage, 2);
                }
                case 3 -> {
                    b4.setDisable(true);
                    GridPane.setRowIndex(pieceImage, 1);
                }
                case 4 -> {
                    b5.setDisable(true);
                    GridPane.setRowIndex(pieceImage, 1);
                    GridPane.setColumnIndex(pieceImage, 1);
                }
                case 5 -> {
                    b6.setDisable(true);
                    GridPane.setRowIndex(pieceImage, 1);
                    GridPane.setColumnIndex(pieceImage, 2);
                }
                case 6 -> {
                    b7.setDisable(true);
                    GridPane.setRowIndex(pieceImage, 2);
                }
                case 7 -> {
                    b8.setDisable(true);
                    GridPane.setRowIndex(pieceImage, 2);
                    GridPane.setColumnIndex(pieceImage, 1);
                }
                case 8 -> {
                    b9.setDisable(true);
                    GridPane.setRowIndex(pieceImage, 2);
                    GridPane.setColumnIndex(pieceImage, 2);
                }
            }
        }
        else { // If the current player is a real player
            switch (buttonId) {
                case "b1" -> gameArray[0] = playerToPlay.getPiece();
                case "b2" -> {
                    gameArray[1] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(pieceImage, 1);
                }
                case "b3" -> {
                    gameArray[2] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(pieceImage, 2);
                }
                case "b4" -> {
                    gameArray[3] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(pieceImage, 0);
                    GridPane.setRowIndex(pieceImage, 1);
                }
                case "b5" -> {
                    gameArray[4] = playerToPlay.getPiece();
                    GridPane.setRowIndex(pieceImage, 1);
                    GridPane.setColumnIndex(pieceImage, 1);
                }
                case "b6" -> {
                    gameArray[5] = playerToPlay.getPiece();
                    GridPane.setRowIndex(pieceImage, 1);
                    GridPane.setColumnIndex(pieceImage, 2);
                }
                case "b7" -> {
                    gameArray[6] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(pieceImage, 0);
                    GridPane.setRowIndex(pieceImage, 2);
                }
                case "b8" -> {
                    gameArray[7] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(pieceImage, 1);
                    GridPane.setRowIndex(pieceImage, 2);
                }
                case "b9" -> {
                    gameArray[8] = playerToPlay.getPiece();
                    GridPane.setColumnIndex(pieceImage, 2);
                    GridPane.setRowIndex(pieceImage, 2);
                }
                case "none" -> {
                    return;
                }
            }
        }
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(500));
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setNode(pieceImage);
        fade.play();
        playGrid.getChildren().add(pieceImage);
        if (checkWinner(gameArray, playGrid)) {
            playerToPlay.setScore(playerToPlay.getScore() + 1);
            if (playerToPlay == player1){
                labelScore1.setText(player1.getName() + "\n" + "Score: " + player1.getScore());
            }
            else {
                labelScore2.setText(player2.getName() + "\n" + "Score: " + player2.getScore());
            }
            infoLabel.setText(playerToPlay.getName() + " won !");
            infoLabel.setVisible(true);
            replayButton.setVisible(true);
            playGrid.setDisable(true);
            return;
        }
        boolean isFinished = true;
        for (Double value : gameArray) {
            if (value == 0.0) {
                isFinished = false;
                break;
            }
        }
        if (isFinished) {
            infoLabel.setText("Égalité !");
            infoLabel.setVisible(true);
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
        if (!player2.isIa()){ // If we're in multiplayer
            infoLabel.setText(playerToPlay.getName() + "'s turn");
        }
        if (player2.isIa()){
            mainGameLogic("none");
        }
    }

    public void resetGrid() {
        replayButton.setVisible(false);
        ObservableList<Node> children = playGrid.getChildren();
        List<Node> nodesToRemove = new ArrayList<>();
        for (Node child : children) {
            if (child instanceof ImageView) {
                nodesToRemove.add(child);
            }
            else if (child instanceof Button) {
                child.setDisable(false);
            }
        }
        playGrid.setDisable(false);
        infoLabel.setText("");
        playGrid.getChildren().removeAll(nodesToRemove);
        for (int i=0; i<9; i++) {
            gameArray[i] = 0;
        }
        setRandomPlayer();
        if (!player2.isIa()){ // If we're in multiplayer
            infoLabel.setText(playerToPlay.getName() + "'s turn");
        }
        mainGameLogic("none");
    }

    public int getIAMove(){
        double min=100;
        int index = 0;
        double[] probaCoups = network.forwardPropagation(gameArray);
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

    public static boolean checkWinner(double[] board, GridPane playGrid) {
        Image X = new Image("file:resources/images/redX.png");
        Image O = new Image("file:resources/images/redO.png");
        ImageView imageView;
        for (int i = 0; i < 9; i += 3) {
            if (board[i] == board[i+1] && board[i+1] == board[i+2] && board[i] != 0.0) { // Lines
                for (int j = 0; j < 3; j++){
                    if (board[i] == -1.0){
                        imageView = new ImageView(X);
                    }
                    else{
                        imageView = new ImageView(O);
                    }
                    GridPane.setMargin(imageView, new Insets(25, 25, 25, 25));
                    GridPane.setColumnIndex(imageView, j);
                    GridPane.setRowIndex(imageView, i/3);
                    FadeTransition fade = new FadeTransition();
                    fade.setDuration(Duration.millis(500));
                    fade.setFromValue(0);
                    fade.setToValue(10);
                    fade.setNode(imageView);
                    fade.play();
                    playGrid.getChildren().add(imageView);
                }
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[i] == board[i+3] && board[i+3] == board[i+6] && board[i] != 0.0) { // Columns
                for (int j = 0; j < 3; j++){
                    if (board[i] == -1.0){
                        imageView = new ImageView(X);
                    }
                    else{
                        imageView = new ImageView(O);
                    }
                    GridPane.setMargin(imageView, new Insets(25, 25, 25, 25));
                    GridPane.setColumnIndex(imageView, i);
                    GridPane.setRowIndex(imageView, j);
                    FadeTransition fade = new FadeTransition();
                    fade.setDuration(Duration.millis(500));
                    fade.setFromValue(0);
                    fade.setToValue(10);
                    fade.setNode(imageView);
                    fade.play();
                    playGrid.getChildren().add(imageView);
                }
                return true;
            }
        }
        if (board[0] == board[4] && board[4] == board[8] && board[0] != 0.0) { // First diagonal
            for (int j = 0; j < 3; j++) {
                if (board[0] == -1.0) {
                    imageView = new ImageView(X);
                } else {
                    imageView = new ImageView(O);
                }
                GridPane.setMargin(imageView, new Insets(25, 25, 25, 25));
                GridPane.setColumnIndex(imageView, j);
                GridPane.setRowIndex(imageView, j);
                FadeTransition fade = new FadeTransition();
                fade.setDuration(Duration.millis(500));
                fade.setFromValue(0);
                fade.setToValue(10);
                fade.setNode(imageView);
                fade.play();
                playGrid.getChildren().add(imageView);
            }
            return true;
        }
        if (board[2] == board[4] && board[4] == board[6] && board[2] != 0.0) { // Second diagonal
            for (int j = 0; j < 3; j++) {
                if (board[2] == -1.0) {
                    imageView = new ImageView(X);
                } else {
                    imageView = new ImageView(O);
                }
                GridPane.setMargin(imageView, new Insets(25, 25, 25, 25));
                GridPane.setColumnIndex(imageView, j);
                GridPane.setRowIndex(imageView, 2-j);
                FadeTransition fade = new FadeTransition();
                fade.setDuration(Duration.millis(500));
                fade.setFromValue(0);
                fade.setToValue(10);
                fade.setNode(imageView);
                fade.play();
                playGrid.getChildren().add(imageView);
            }
            return true;
        }
        return false;
    }

    public static void setRandomPlayer() {
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
    }

    // Animation and music functions
    public void animateTitleScreenBackground(){
        Image animImage;
        int randomImg = (int) (Math.random() * (3 - 1)) + 1;
        if (randomImg == 1){
            animImage = new Image("file:resources/images/X.png");
        }
        else {
            animImage = new Image("file:resources/images/O.png");
        }
        ImageView imageTitleScreen = new ImageView(animImage);

        double randOpacity = 0.1 + Math.random() * (0.9 - 0.1);
        double randSpeedMove = (int) (Math.random() * 4000) + 10000;
        double randSize =  0.7 + Math.random();
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
        // To make the shapes go behind the mountains and above the background
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
            Platform.runLater(MainController.this::animateTitleScreenBackground);
        }
    }

    public void setupAudio(){
        volumeSlider.setValue(volume);

        // Change the volume value according to the slider
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            volume = newValue.doubleValue();
            if (mediaPlayer != null){
                mediaPlayer.setVolume(volume);
            }
            if (newMediaPlayer != null){
                newMediaPlayer.setVolume(volume);
            }
        });
        if (mediaPlayer == null || newMediaPlayer == null){
            Media media = new Media(new File("./src/main/resources/sounds/MorpionOST4.mp3").toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        }
    }

    public void muteAudio() {
        if (!isMuted) {
            volume = 0.0;
            if (mediaPlayer != null){
                mediaPlayer.setVolume(volume);
            }
            if (newMediaPlayer != null){
                newMediaPlayer.setVolume(volume);
            }
            soundMenu.setText("Enable music");
            isMuted = true;
        }
        else {
            volume = 1.0;
            if (mediaPlayer != null){
                mediaPlayer.setVolume(volume);
            }
            if (newMediaPlayer != null){
                newMediaPlayer.setVolume(volume);
            }
            soundMenu.setText("Disable music");
            isMuted = false;
        }
    }

    public void changeMusicTrack(String fileName){
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
            Media media = new Media(new File(fileName).toURI().toString());
            Duration currentTime = mediaPlayer.getCurrentTime();
            newMediaPlayer = new MediaPlayer(media);
            newMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            newMediaPlayer.setStartTime(currentTime);
            newMediaPlayer.setOnPlaying(() -> {
                System.out.println("New Media player is now playing!");
                newMediaPlayer.setStartTime(Duration.ZERO);
                mediaPlayer.stop();
            });
            newMediaPlayer.setVolume(volume);
            newMediaPlayer.play();
        }
        else {
            Media media = new Media(new File(fileName).toURI().toString());
            Duration currentTime = newMediaPlayer.getCurrentTime();
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setStartTime(currentTime);
            mediaPlayer.setOnPlaying(() -> {
                System.out.println("Media player is now playing!");
                mediaPlayer.setStartTime(Duration.ZERO);
                newMediaPlayer.stop();
            });
            mediaPlayer.setVolume(volume);
            mediaPlayer.play();
        }
    }
}