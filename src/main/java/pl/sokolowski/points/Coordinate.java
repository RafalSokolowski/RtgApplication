package pl.sokolowski.points;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Coordinate {

    double x;
    double y;

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
