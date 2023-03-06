module com.example.projetmorpion {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.projetmorpion to javafx.fxml;
    exports com.example.projetmorpion;
}