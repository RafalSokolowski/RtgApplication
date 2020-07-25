package pl.sokolowski;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

import static pl.sokolowski.Const.HEIGHT;
import static pl.sokolowski.Const.WIDTH;


@Log4j2
public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("panel.fxml"));
        stage.setTitle("RTG Application");
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);

        double ratio = (double) HEIGHT / (double) WIDTH;
        stage.minHeightProperty().bind(stage.widthProperty().multiply(ratio));
        stage.maxHeightProperty().bind(stage.widthProperty().multiply(ratio));

        stage.show();

        stage.setMinWidth(stage.getWidth());        // do not shrink too much... beyond the beginning value...

    }

    public static void main(String[] args) {
        launch(args);
    }

}