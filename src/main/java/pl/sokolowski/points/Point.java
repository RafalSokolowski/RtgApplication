package pl.sokolowski.points;

import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
//@AllArgsConstructor
@EqualsAndHashCode
public class Point {

    private TextField x;
    private TextField y;

    private Color color;

    public Point(TextField x, TextField y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;

        setPrefWidthXY();
    }

    private void setPrefWidthXY () {
        x.setPrefWidth(50);
        y.setPrefWidth(50);
    }

    @Override
    public String toString() {
        return "(" + x.getText() + ", " + y.getText() + ", " + color + ")";
    }
}
