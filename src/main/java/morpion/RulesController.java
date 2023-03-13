package morpion;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class RulesController {

    @FXML
    ImageView rules1;
    @FXML
    ImageView rules2;

    @FXML
    Pane rulesPane;

    @FXML
    public void initialize() {
        Image rules1 = new Image("file:resources/images/rules1.jpg");
        Image rules2 = new Image("file:resources/images/rules2.jpg");
        ImageView rules1View = new ImageView(rules1);
        ImageView rules2View = new ImageView(rules2);
        rules2View.setLayoutY(400);
        rulesPane.getChildren().addAll(rules1View, rules2View);
    }
}
