package pl.sokolowski.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import pl.sokolowski.Util.ErrorWindow;
import pl.sokolowski.points.Coordinate;
import pl.sokolowski.points.Point;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static pl.sokolowski.Const.*;

@Log4j2
public class Controller {

    @FXML
    private GridPane centerGridPane;
    @FXML
    private ImageView rtgImageView00;
    @FXML
    private ImageView rtgImageView10;
    @FXML
    private ImageView rtgImageView01;
    @FXML
    private ImageView rtgImageView11;
    @FXML
    private Image rtgImage;

    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ChoiceBox<String> sizePicker;

    @FXML
    private VBox leftMenu;

    @FXML
    TextField currentX;
    @FXML
    TextField currentY;

    private Map<Point, Circle[]> points;    // for additional development in v 2.0

    private double rtgImageWidth;
    private double rtgImageHeight;

    private double offsetX;
    private double offsetY;

    private double offsetImage00_X;
    private double offsetImage00_Y;

    private Coordinate rtgImagePosition00;
    private Coordinate rtgImagePosition10;
    private Coordinate rtgImagePosition01;
    private Coordinate rtgImagePosition11;


    public Controller() {
        this.points = new LinkedHashMap<>();
        this.rtgImageWidth = 0;
        this.rtgImageHeight = 0;
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetImage00_X = 0;
        this.offsetImage00_Y = 0;
        this.rtgImagePosition00 = new Coordinate(0, 0);
        this.rtgImagePosition10 = new Coordinate(0, 0);
        this.rtgImagePosition01 = new Coordinate(0, 0);
        this.rtgImagePosition11 = new Coordinate(0, 0);
    }

    @FXML
    public void initialize() {

        centerGridPane.setOnMouseMoved(event -> {
            currentX.setText(String.valueOf(Math.round(event.getX())));
            currentY.setText(String.valueOf(Math.round(event.getY())));
        });

// check / preparation for resizing in v 2.0 :)
//        rootBorderPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
//            System.out.println("New width: " + newSceneWidth);
//        });
//        rootBorderPane.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
//            System.out.println("New height: " + newSceneHeight);
//        });

    }


    @FXML
    public void getCoordinateAndDrawPoint(MouseEvent event) {

        // printInternalData(event);

        // 0. calculate / check RTG image size, offsets and position on the GridPane
        calculateUpdateRtgImageSize();
        calculateUpdateImageOffset();
        calculateUpdateOffsets();
        calculateUpdateImagePositions();


        // 1. Create circles representation
        Point point = new Point(
                new TextField(String.valueOf(Math.round(event.getX()))),    // X value relative to the image | event.getSceneX()) - relative to the scene
                new TextField(String.valueOf(Math.round(event.getY()))),    // Y value relative to the image | event.getSceneY()) - relative to the scene
                colorPicker.getValue()
        );

        generateAllCircles(event).ifPresent(circles -> {
            // 2. Add point-circle to database
            points.put(point, circles);

            // 3. Adding point description (with coordinate) to the left-hand-side menu (with delete button)
            Button delete = createPointRepresentation(point, circles[0]);
            delete.setOnMouseClicked(e -> {
                leftMenu.getChildren().remove(delete.getParent().getParent());
                centerGridPane.getChildren().removeAll(circles);
                points.remove(point);
            });

            // 4. Listen for the changes in the point's coordinates (on the lef-hand-size menu)
            pointCoordinatesListener(point, circles);

            // 5. Allow for and track drag and drop procedure
            circleDragAndDrop(point, circles);
        });
    }


    private Circle[] calculateCircles(MouseEvent event, ImageView imageView) {      // extremely dirty solution, needs to be verified, change, polish ...
        Circle spot00, spot10, spot01, spot11;

        if (imageView.equals(rtgImageView00)) {
            spot00 = createCircleNoSizeColor(
                    event.getX(),
                    event.getY()
            );
            spot10 = createCircleNoSizeColor(
                    event.getX() + offsetX,
                    event.getY()
            );
            spot01 = createCircleNoSizeColor(
                    event.getX(),
                    event.getY() + offsetY
            );
            spot11 = createCircleNoSizeColor(
                    event.getX() + offsetX,
                    event.getY() + offsetY
            );
            return new Circle[]{spot00, spot10, spot01, spot11};
        }
        if (imageView.equals(rtgImageView10)) {
            spot00 = createCircleNoSizeColor(
                    event.getX() - offsetX,
                    event.getY()
            );
            spot10 = createCircleNoSizeColor(
                    event.getX(),
                    event.getY()
            );
            spot01 = createCircleNoSizeColor(
                    event.getX() - offsetX,
                    event.getY() + offsetY
            );
            spot11 = createCircleNoSizeColor(
                    event.getX(),
                    event.getY() + offsetY
            );
            return new Circle[]{spot00, spot10, spot01, spot11};
        }
        if (imageView.equals(rtgImageView01)) {
            spot00 = createCircleNoSizeColor(
                    event.getX(),
                    event.getY() - offsetY
            );
            spot10 = createCircleNoSizeColor(
                    event.getX() + offsetX,
                    event.getY() - offsetY
            );
            spot01 = createCircleNoSizeColor(
                    event.getX(),
                    event.getY()
            );
            spot11 = createCircleNoSizeColor(
                    event.getX() + offsetX,
                    event.getY()
            );
            return new Circle[]{spot00, spot10, spot01, spot11};
        }
        if (imageView.equals(rtgImageView11)) {
            spot00 = createCircleNoSizeColor(
                    event.getX() - offsetX,
                    event.getY() - offsetY
            );
            spot10 = createCircleNoSizeColor(
                    event.getX(),
                    event.getY() - offsetY
            );
            spot01 = createCircleNoSizeColor(
                    event.getX() - offsetX,
                    event.getY()
            );
            spot11 = createCircleNoSizeColor(
                    event.getX(),
                    event.getY()
            );
            return new Circle[]{spot00, spot10, spot01, spot11};
        }

        log.error(RED + "there needs to be exactly one option fulfilled (out of 4)... check XML settings" + RESET);
        throw new IllegalArgumentException("circles' fuckup :/");
    }

    private Optional<Circle[]> generateAllCircles (MouseEvent event) {
        Circle[] circles;
        if (event.getPickResult().getIntersectedNode().equals(rtgImageView00)) {
            circles = calculateCircles(event, rtgImageView00);
            addCirclesToTheGrid(circles);
        } else if (event.getPickResult().getIntersectedNode().equals(rtgImageView10)) {
            circles = calculateCircles(event, rtgImageView10);
            addCirclesToTheGrid(circles);
        } else if (event.getPickResult().getIntersectedNode().equals(rtgImageView01)) {
            circles = calculateCircles(event, rtgImageView01);
            addCirclesToTheGrid(circles);
        } else if (event.getPickResult().getIntersectedNode().equals(rtgImageView11)) {
            circles = calculateCircles(event, rtgImageView11);
            addCirclesToTheGrid(circles);
        } else {
            return Optional.empty();
        }
        return Optional.of(circles);
    }

    private void addCirclesToTheGrid(Circle[] circles) {
        if (circles.length != 4) {
            throw new IllegalArgumentException("There needs to be exactly four point-representation, not " + circles.length);
        }
        centerGridPane.add(circles[0], 0, 0);   // 00
        centerGridPane.add(circles[1], 1, 0);   // 10
        centerGridPane.add(circles[2], 0, 1);   // 01
        centerGridPane.add(circles[3], 1, 1);   // 11
    }

    private Circle createCircleNoSizeColor(double x, double y) {
        Circle circle = new Circle(
                x,
                y,
                selectCircleSize(),
                colorPicker.getValue()
        );
        circle.setManaged(false);
        return circle;
    }

    private Button createPointRepresentation(Point point, Circle circle) {
        VBox selectedPoint = new VBox();

        selectedPoint.setStyle("-fx-background-color: #333333; -fx-background-radius: 3px;");
        selectedPoint.setBorder(new Border(new BorderStroke(
                circle.getFill(),
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(2)))
        );

        Text name = new Text("Point");
        name.setFill(circle.getFill());

        HBox coordinate = new HBox();

        Label xLabel = new Label("x=");
        xLabel.setPrefWidth(30);

        Label yLabel = new Label("y=");
        yLabel.setPrefWidth(35);
        yLabel.setStyle("-fx-padding: 5px 5px 5px 10px");

        coordinate.getChildren().add(xLabel);
        point.getX().setText(String.valueOf(Math.round(circle.getCenterX() - offsetImage00_X)));
        coordinate.getChildren().add(point.getX());

        coordinate.getChildren().add(yLabel);
        point.getY().setText(String.valueOf(Math.round(circle.getCenterY() - offsetImage00_Y)));
        coordinate.getChildren().add(point.getY());

        Button button = new Button("X");
        button.setStyle("-fx-background-insets: 5px; -fx-spacing: 0 0 0 10px");
        coordinate.getChildren().add(button);

        selectedPoint.getChildren().add(name);
        selectedPoint.getChildren().add(coordinate);

        leftMenu.getChildren().add(selectedPoint);

        return button;
    }

    private void pointCoordinatesListener(Point point, Circle[] circles) {

        // 1. Listen for mouse event on the point's TextFields
        EventHandler<MouseEvent> mouseEvent = event -> {
            updateCoordinates(point, circles);
            event.consume();
        };

        // 2. Listen for ENTER pressed on the point's TextFields
        EventHandler<KeyEvent> keyEvent = (event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                updateCoordinates(point, circles);
                event.consume();
            }
        });

        point.getX().addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent);  // update X point's TextField while mouse is moved away
        point.getX().addEventHandler(KeyEvent.KEY_PRESSED, keyEvent);       // update X point's TextField while ENTER is pressed
        point.getY().addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent);  // update Y point's TextField while mouse is moved away
        point.getY().addEventHandler(KeyEvent.KEY_PRESSED, keyEvent);       // update Y point's TextField while ENTER is pressed
    }

    private void updateCoordinates(Point point, Circle[] circles) {
        String errorMessage;
        String newValueX = point.getX().getText();
        String newValueY = point.getY().getText();

        if (!newValueX.matches("\\d+") || !newValueY.matches("\\d+")) {
            updatePointBasedOnCircle(point, circles[0]);
            errorMessage = "Unacceptable coordinate\nOnly positive digits are accepted\nData in point's description was restored to previous value";
            new ErrorWindow(350, 200, errorMessage).start(new Stage());
            return;
        }

        double newX = Double.parseDouble(newValueX);
        double newY = Double.parseDouble(newValueY);

        if (newX >= 0 && newX <= rtgImageWidth && newY >= 0 && newY <= rtgImageHeight) {
            updateCirclesBasedOnPoint(circles, point);
        } else {
            updatePointBasedOnCircle(point, circles[0]);
            String message = "Unacceptable coordinate\nPoint is out of the image\nData in point's description was restored to previous value";
            new ErrorWindow(350, 200, message).start(new Stage());
        }

    }

    private void circleDragAndDrop(Point point, Circle[] circles) {
        // 1. Mouse is moved over the circle -> change cursor
        for (Circle c : circles) {
            c.setOnMouseEntered(event -> c.setCursor(Cursor.HAND));
        }

        // 2. Circle was clicked (drag) -> change cursor
        for (Circle c : circles) {
            c.setOnMousePressed(event -> c.setCursor(Cursor.DISAPPEAR));
        }

        // 3. Circle is moving (dragging) -> change cursor and visually update coordinates (to see where the circle is moving - in version v 2.0)
        for (Circle c : circles) {
            c.setOnMouseDragged(event -> {
                c.setCursor(Cursor.DISAPPEAR);
            });
        }

        // 4. Mouse left-button is released (dropped)
        for (Circle c : circles) {
            c.setOnMouseReleased(event -> {
                updateCircleBasedOnCircles(event, circles);

                double newX = circles[0].getCenterX() - offsetImage00_X;
                double newY = circles[0].getCenterY() - offsetImage00_Y;

                if (newX >= 0 && newX <= rtgImageWidth && newY >= 0 && newY <= rtgImageHeight) {
                    updatePointBasedOnCircle(point, circles[0]);
                } else {
                    updateCirclesBasedOnPoint(circles, point);
                    String message = "Unacceptable coordinate\nCircle is out of the image\nCircle location on the image was restored";
                    new ErrorWindow(400, 200, message).start(new Stage());
                }
            });
        }
    }


    private void updatePointBasedOnCircle(Point point, Circle circle) {
        String newTextX = String.valueOf(Math.round(circle.getCenterX() - offsetImage00_X));
        String newTextY = String.valueOf(Math.round(circle.getCenterY() - offsetImage00_Y));
        point.getX().setText(newTextX);
        point.getY().setText(newTextY);
    }

    private void updateCirclesBasedOnPoint(Circle[] circles, Point point) {
        double deltaX = Double.parseDouble(point.getX().getText());
        double deltaY = Double.parseDouble(point.getY().getText());

        circles[0].setCenterX(rtgImagePosition00.getX() + deltaX);
        circles[0].setCenterY(rtgImagePosition00.getY() + deltaY);
        circles[1].setCenterX(rtgImagePosition10.getX() + deltaX);
        circles[1].setCenterY(rtgImagePosition10.getY() + deltaY);
        circles[2].setCenterX(rtgImagePosition01.getX() + deltaX);
        circles[2].setCenterY(rtgImagePosition01.getY() + deltaY);
        circles[3].setCenterX(rtgImagePosition11.getX() + deltaX);
        circles[3].setCenterY(rtgImagePosition11.getY() + deltaY);
    }

    private void updateCircleBasedOnCircles(MouseEvent event, Circle[] circles) {
        double positionX = event.getPickResult().getIntersectedNode().getLocalToParentTransform().getTx();
        double positionY = event.getPickResult().getIntersectedNode().getLocalToParentTransform().getTy();

        circles[0].setCenterX(rtgImagePosition00.getX() + event.getX() - positionX);
        circles[0].setCenterY(rtgImagePosition00.getY() + event.getY() - positionY);
        circles[1].setCenterX(rtgImagePosition10.getX() + event.getX() - positionX);
        circles[1].setCenterY(rtgImagePosition10.getY() + event.getY() - positionY);
        circles[2].setCenterX(rtgImagePosition01.getX() + event.getX() - positionX);
        circles[2].setCenterY(rtgImagePosition01.getY() + event.getY() - positionY);
        circles[3].setCenterX(rtgImagePosition11.getX() + event.getX() - positionX);
        circles[3].setCenterY(rtgImagePosition11.getY() + event.getY() - positionY);
    }


    private double selectCircleSize() {

        switch (sizePicker.getValue()) {
            case "XS":
                return 1;
            case "S":
                return 2;
            case "M":
                return 3;
            case "L":
                return 4;
            case "XL":
                return 5;
        }

        log.error("something went wrong, there needs to be one out of 5 (XS, S, M, L or XL), " +
                "check XML settings... default value was selected, ie. M");
        return 3;
    }

    private void calculateUpdateRtgImageSize() {
        rtgImageWidth = rtgImageView00.getBoundsInLocal().getWidth();       // rescale ready (when the image is resized, the image's boundaries need to be recalculated / updated.
        rtgImageHeight = rtgImageView00.getBoundsInLocal().getHeight();     // rescale ready (when the image is resized, the image's boundaries need to be recalculated / updated.
//        log.info("Calculated / checked size of the image: " + BLUE + "(width " + Math.round(rtgImageWidth) + ", height " + Math.round(rtgImageHeight) + ")" + RESET);
    }

    private void calculateUpdateOffsets() {
        offsetX = rtgImageView10.getLocalToParentTransform().getTx() - offsetImage00_X;
        offsetY = rtgImageView01.getLocalToParentTransform().getTy() - offsetImage00_Y;
    }

    private void calculateUpdateImageOffset() {
        offsetImage00_X = rtgImageView00.getLocalToParentTransform().getTx();
        offsetImage00_Y = rtgImageView00.getLocalToParentTransform().getTy();
    }

    private void calculateUpdateImagePositions() {
        rtgImagePosition00 = new Coordinate(offsetImage00_X, offsetImage00_Y);
        rtgImagePosition10 = new Coordinate(offsetImage00_X + offsetX, offsetImage00_Y);
        rtgImagePosition01 = new Coordinate(offsetImage00_X, offsetImage00_Y + offsetY);
        rtgImagePosition11 = new Coordinate(offsetImage00_X + offsetX, offsetImage00_Y + offsetY);
    }

//    public void selectNewFile() {
//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        directoryChooser.setInitialDirectory(new File("src/main/resources/pl/sokolowski/images"));
//        directoryChooser.showDialog(new Stage());
//    }

    private void printInternalData(MouseEvent event) {
        log.info("Coordinates relative to the image - event.getXY() : " + BLUE + event.getX() + ", " + event.getY() + RESET);
        log.info("Coordinates relative to the scene - event.getSceneXY(): " + BLUE + event.getSceneX() + ", " + event.getSceneY() + RESET);
        log.info("Screen: " + BLUE + event.getScreenX() + ", " + event.getScreenY() + RESET);                             // Screen - change while the App's window is moved on the screen...

        log.info("rtgImagePosition00: " + YELLOW + rtgImagePosition00 + RESET);
        log.info("rtgImagePosition10: " + YELLOW + rtgImagePosition10 + RESET);
        log.info("rtgImagePosition01: " + YELLOW + rtgImagePosition01 + RESET);
        log.info("rtgImagePosition11: " + YELLOW + rtgImagePosition11 + RESET);

        System.out.println(rtgImageView00.getX());
        System.out.println(rtgImageView00.getY());
        System.err.println(rtgImageView00.getTranslateX());
        System.err.println(rtgImageView00.getTranslateY());
        System.out.println(rtgImageView00.getFitHeight());
        System.out.println(rtgImageView00.getFitWidth());
        System.out.println("Width: " + rtgImage.getHeight());
        System.out.println("Height: " + rtgImage.getWidth());

        System.out.println("getLocalToSceneTransform: " + rtgImageView00.getLocalToSceneTransform());
        System.out.println("getLocalToParentTransform: " + rtgImageView00.getLocalToParentTransform());
        System.out.println("getLocalToSceneTransform 2: " + rtgImageView10.getLocalToSceneTransform());
        System.out.println("getLocalToParentTransform 2: " + rtgImageView10.getLocalToParentTransform());

        System.err.println("getX 2: " + rtgImageView10.getX());
        System.err.println("getBoundsInLocal 2: " + rtgImageView10.getBoundsInLocal());
        System.err.println("getTranslateX 2: " + rtgImageView10.getTranslateX());
        System.err.println("getBaselineOffset 2: " + rtgImageView10.getBaselineOffset());
        System.err.println("getBoundsInParent 2: " + rtgImageView10.getBoundsInParent());
        System.err.println("getProperties 2: " + rtgImageView10.getProperties());
        System.err.println("getViewport 2: " + rtgImageView10.getViewport());

        System.err.println(rtgImageView00.getBoundsInLocal().getWidth());
        System.err.println(rtgImageView00.getBoundsInLocal().getHeight());
        System.out.println(rtgImageView00.getTransforms());


    }

}



