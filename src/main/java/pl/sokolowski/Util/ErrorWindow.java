package pl.sokolowski.Util;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AllArgsConstructor;

import static pl.sokolowski.Const.ERROR_SOUND;

@AllArgsConstructor
public class ErrorWindow extends Application {

    private int width;
    private int height;
    private String message;

    @Override
    public void start(Stage stage) {

        stage.initStyle(StageStyle.UNDECORATED);                   // style of the window
        stage.initModality(Modality.APPLICATION_MODAL);            // block all other Windows/Stages opened by the app (cannot access any other Windows/Stages until this Stage window has been closed)

        VBox information = new VBox();
        information.setStyle("-fx-background-color: #111111;");
        information.setAlignment(Pos.CENTER);

        Label label = new Label (message);
        label.setStyle("-fx-font: 20px Calibri; -fx-font-weight: bold; -fx-text-fill: red;");

        Button button = new Button("Understand");
//        button.setStyle("-fx-background-insets: 20px;");
        button.setOnAction(event -> stage.close());

        information.getChildren().add(label);
        information.getChildren().add(button);

        Scene dialogScene = new Scene(information, width, height);
        dialogScene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) stage.close();
        });
        stage.setScene(dialogScene);
        new MediaPlayer(ERROR_SOUND).play();
        stage.show();

    }
}
