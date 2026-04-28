import bagel.util.Point;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A utility class that provides methods to read and write files.
 */
public class IOUtils {
    /***
     * Read a properties file and return a Properties object
     * @param configFile: the path to the properties file
     * @return: Properties object
     */
    public static Properties readPropertiesFile(String configFile) {
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(configFile));
        } catch(IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        return appProps;
    }

    /**
     * Parses a comma seperate coordinate string
     * @param coords string in the form of "x,y"
     * @return Point with given coordinates
     */
    public static Point parseCoords(String coords) {
        String[] coordinates = coords.split(",");
        return new Point(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
    }

    /**
     * Parses a semi-colon seperated list of coordinate pairs
     * @param coords list string or 0 when empty
     * @return list of Points
     */
    public static ArrayList<Point> parseCoordsList(String coords) {
        List<Point> points = new ArrayList<>();
        if (coords.equals("0")) { // if no elements
            return (ArrayList<Point>) points;
        }

        String[] pairs = coords.split(";");
        for (int i = 0; i < pairs.length; i++) {
            String pair = pairs[i];
            if (!pair.isEmpty()) {
                points.add(parseCoords(pair));
            }
        }
        return (ArrayList<Point>) points;

    }

    /**
     * Parses a semicolon seperated list of tripes "x,y,n;"
     * @param coords triple list string
     * @return list of parsed triples
     */
    public static ArrayList<String[]> parseTreasure(String coords) {
        ArrayList<String[]> treasures = new ArrayList<>();

        coords = coords.trim(); // remove trailing space

        if (coords.equals("0")) { // if no elements
            return treasures;
        }

        String[] triples = coords.split(";"); // split list

        for (int i = 0; i < triples.length; i++) {
            String[] parts = triples[i].split(",");
            if (parts.length == 3) {
                treasures.add(parts);
            }
        }
        return treasures;

    }

}
