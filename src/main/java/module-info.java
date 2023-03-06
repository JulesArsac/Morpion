module com.example.projetmorpion {
    requires javafx.controls;
    requires javafx.fxml;


    opens morpion to javafx.fxml;
    exports morpion;
}