package pl.sokolowski;

import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;

import java.io.File;

public class Const {

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;

    public static final int MAX_IMAGE_HEIGHT = HEIGHT / 3;
    public static final int MAX_IMAGE_WIDTH = WIDTH / 4;

    public static final int LEFT_MENU_SIZE = 250;
    public static final int RTG_WINDOW_SIZE = WIDTH - LEFT_MENU_SIZE;

//    public static final int POINT_SIZE = 5;

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String GREEN = "\u001B[32m";

    public static final Media ERROR_SOUND = new Media(new File("src/main/resources/pl/sokolowski/sounds/error.mp3").toURI().toString());

}
