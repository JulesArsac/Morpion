module com.example.projetmorpion {
    requires javafx.controls;
    requires javafx.fxml;
    //requires javafx.media;

    opens morpion to javafx.fxml;
    exports morpion;
}