package utils;

import de.tudresden.ws.container.SumoPosition2D;

public class Utils {

    public static double distance(SumoPosition2D point1, SumoPosition2D point2) {
        return Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }
}
