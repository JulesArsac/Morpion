package morpion;

import ai.ConfigFileLoader;
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
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainController {

    double error = 0.0;
    double samples = 100000000;
    private String difficulty = "M";

    private Parent root;
    private Scene scene;
    private Stage stage;


    @FXML
    Label textError;

    @FXML
    ProgressBar progressBar;

    @FXML
    Button singlePlayerButton;

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
            System.out.println();
            System.out.println("START TRAINING ...");
            System.out.println();

            if (easyRadio.isSelected()) {
                difficulty = "F";
            }
            else if (mediumRadio.isSelected()) {
                difficulty = "M";
            }
            else {
                difficulty = "D";
            }
            ConfigFileLoader configuration = new ConfigFileLoader();
            configuration.loadConfigFile("resources/config.txt");

            int[] layers = new int[configuration.get(difficulty).numberOfhiddenLayers + 2];
            layers[0] = 9;
            for (int i=1; i<=configuration.get(difficulty).numberOfhiddenLayers; i++){
                layers[i]=configuration.get(difficulty).hiddenLayerSize;
            }

            String modelpath = "model_" + configuration.get(difficulty).numberOfhiddenLayers +"_" + configuration.get(difficulty).learningRate + "_" + configuration.get(difficulty).hiddenLayerSize;

            File model = new File(modelpath);
            if(model.exists() && !model.isDirectory()) {
                MultiLayerPerceptron net = MultiLayerPerceptron.load(modelpath);

                root = FXMLLoader.load(getClass().getResource("game.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
            }
            else{

                root = FXMLLoader.load(getClass().getResource("trainingView.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();

                MultiLayerPerceptron net = new MultiLayerPerceptron(layers, configuration.get(difficulty).learningRate, new SigmoidalTransferFunction());
                //TRAINING ...
                Task<Integer> trainingTask = new Task<Integer>() {
                    @Override
                    protected Integer call() throws Exception {
                        for(int i = 0; i < samples; i++){
                            double[] inputs = new double[]{Math.round(Math.random()), Math.round(Math.random())};
                            double[] output = new double[1];

                            if((inputs[0] == 1.0) || (inputs[1] == 1.0))
                                output[0] = 1.0;
                            else
                                output[0] = 0.0;



                            error += net.backPropagate(inputs, output);

                            if ( i % 100000 == 0 ) {
                                System.out.println("Error at step "+i+" is "+ (error/(double)i));
                                updateMessage("Error at step "+i+" is "+ (error/(double)i));
                                updateProgress(i, samples);
                            }
                        }
                        error /= samples ;
                        System.out.println("Error is "+error);
                        updateMessage("Error is "+error);
                        //
                        System.out.println("Learning completed!");
                        updateMessage("Learning completed!");

                        net.save(modelpath);
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
            System.out.println("Train()");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}