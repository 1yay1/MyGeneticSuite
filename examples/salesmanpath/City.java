
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by yay on 13.10.2016.
 */
public class City {
    public final static int MIN_X = 0;
    public final static int MIN_Y = 0;
    public final static int MAX_X = 100000;
    public final static int MAX_Y = 100000;
    private final String CoordinatesOutOfBoundsString = "Coordinates are out of bounds.";
    private final int x;
    private final int y;



    /**
     * Get the location of the city as a new Point object.
     * b
     *
     * @return new Point object.
     */
    public int[] getLocation() {
        return new int[]{getX(), getY()};
    }

    /**
     * Simple get method for the y coordinate.
     *
     * @return value of x.
     */
    public int getX() {
        return x;
    }

    /**
     * Simple get method for the y coordinate.
     *
     * @return value of y
     */
    public int getY() {
        return y;
    }

    /**
     * Constructor for a new City Object that takes no arguments and returns a City with randomly generated coordinates.
     */
    public City() {
        this(ThreadLocalRandom.current().nextInt(MAX_X), ThreadLocalRandom.current().nextInt(MAX_Y));
    }

    /**
     * Constructor for a new City Object, takes the Coordinates as byte value.
     *
     * @param x x coordinate of the City.
     * @param y y coordinate of the City.
     * @throws IllegalArgumentException when x or y is bigger than their respectively allowed max value.
     */
    public City(int x, int y) throws IllegalArgumentException {
        if (x < MIN_X || x > MAX_X || y < MIN_Y || y > MAX_Y) {
            throw new IllegalArgumentException(CoordinatesOutOfBoundsString);
        }
        this.x = x;
        this.y = y;
    }

    /**
     * Calculates the distance between two City Objects
     *
     * @param c1 First City Object
     * @param c2 Second City Object
     * @return the distance as a long value.
     */
    public static double distanceFromTo(City c1, City c2) {

        Double x1 = new Double(c1.getX());
        Double y1 = new Double(c1.getY());
        Double x2 = new Double(c2.getX());
        Double y2 = new Double(c2.getY());
        Double line1 = x1 > x2 ? x1 - x2 : x2 - x1;
        Double line2 = y1 > y2 ? y1 - y2 : y2 - y1;
        return Math.sqrt(Math.pow(line1, 2) + Math.pow(line2, 2));
    }

    /**
     * Simple toString() method that returns a string represenantion of the City coordinates.
     *
     * @return String representation of the City
     */
    @Override
    public String toString() {
        return getX() + ":" + getY();
    }

    /**
     * Two City objects are the same, when they have the same coordinates.
     *
     * @param o Object to be checked for equality.
     * @return boolean true when the Objects are the same, false if they are not.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof City)) {
            return false;
        }
        City city = (City) o;
        if (city.getX() == getX() && city.getY() == getY()) {
            return true;
        }
        return false;
    }

    /**
     * We just use the hashCode method of the String method.
     * This way two different City objects with the same coordinates will return the same value.
     *
     * @return
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Builds a list of randomly generated City Objects.
     * Because we don't want to have a city with the same coordinates twice,
     * we create new City Objects until we have the amount we want and try to add it to a HashSet.
     * We don't care about the order of the citties.
     *
     * @param cityAmount the amount of City objects we generate.
     * @return new ArrayList with cityAmount amount of City objects.
     */
    public static List<City> getRandomListOfCities(int cityAmount) {
        Set<City> citySet = new HashSet<>();
        while (citySet.size() < cityAmount) {
            citySet.add(new City());
        }
        return new ArrayList<>(citySet);
    }


    /**
     * Creates a String representation of a List of City Objects.
     *
     * @param cityList
     * @return String in format c1.toString > c2.toString > ...
     */
    public static String ListOfCitiesToString(List<City> cityList) {
        StringBuilder sb = new StringBuilder();
        for (City c : cityList) {
            sb.append(c);
            sb.append(" > ");
        }
        sb.replace(sb.lastIndexOf(" > "), sb.length(), "");
        return sb.toString();
    }
}
