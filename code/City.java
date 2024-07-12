/**
 * A city's properties are kept in city class
 * @author Kerem Oğuz, Student ID: 2022400270
 * @since Date: 24.03.2024
 */
import java.util.ArrayList;
public class City {
    ArrayList<City> previousCities = new ArrayList<City>(); // it represents the previous cities of the current city
    double cost = Integer.MAX_VALUE; // initially cost is infinity
    String cityName; // a city name
    int x,y; // x and y coordinates of a city
    City(String cityName, int x, int y){
        this.cityName = cityName; // cityName, x and y are constructed
        this.x = x;
        this.y = y;
    }
    @Override
    public String toString() {
        return cityName + " " + x + " " + y;
    }
}
