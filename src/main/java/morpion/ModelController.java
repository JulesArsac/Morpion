package morpion;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import java.io.File;

public class ModelController {

    @FXML
    ScrollPane scrollPane;

    public void initialize(){
        File modelsFolder = new File("src/main/resources/models/");
        File[] listOfmodels = modelsFolder.listFiles();

        GridPane paneModels = new GridPane();

        paneModels.setPadding(new Insets(10));
        for (int i=0; i < listOfmodels.length; i++){
            Label label = new Label(listOfmodels[i].getName());
            Button button = new Button("Delete this model");

            final String fileName = label.getText();
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String buttonName = button.getText();
                    onClickButton(fileName);
                }
            });
            GridPane.setColumnIndex(label, 0);
            GridPane.setColumnIndex(button, 1);
            GridPane.setRowIndex(button, i);
            GridPane.setRowIndex(label, i);
            GridPane.setMargin(button, new Insets(15));
            GridPane.setMargin(label, new Insets(00,30 , 15, 0));
            paneModels.getChildren().add(button);
            paneModels.getChildren().add(label);
        }
        scrollPane.setContent(paneModels);
    }

    private void onClickButton(String buttonName) {
        File model = new File("src/main/resources/models/" + buttonName);
        model.delete();
        initialize();
    }

}
