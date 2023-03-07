package morpion;

import ai.ConfigFileLoader;
import ai.MultiLayerPerceptron;
import ai.SigmoidalTransferFunction;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    double error = 0.0;
    double samples = 100000000;

    @FXML
    Label textError;

    @FXML
    Button buttonTrain;

    @FXML
    ProgressBar progressBar;

    @FXML
    Button singlePlayerButton;

    @FXML
    void train() {
        try {
            buttonTrain.setDisable(true);
            System.out.println();
            System.out.println("START TRAINING ...");
            System.out.println();
            int[] layers = new int[]{ 2, 5, 1 };


            MultiLayerPerceptron net = new MultiLayerPerceptron(layers, 0.1, new SigmoidalTransferFunction());

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

                    buttonTrain.setDisable(false);
                    return 0;
                }
            };

            Thread trainingThread = new Thread(trainingTask);
            progressBar.progressProperty().bind(trainingTask.progressProperty());
            textError.textProperty().bind(trainingTask.messageProperty());
            trainingThread.start();

        }
        catch (Exception e) {
            System.out.println("Test.test()");
            e.printStackTrace();
            System.exit(-1);
        }
    }

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
    void onSinglePlayerButtonClick() {

    }
}