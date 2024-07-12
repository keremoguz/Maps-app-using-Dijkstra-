/**
 * Given two cities, the shortest distance and path are found using Dijkstra's algorithm and displayed with StdDraw
 * @author Kerem Oğuz, Student ID: 2022400270
 * @since Date: 24.03.2024
 */
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class KeremOguz {
    /**
     * shortest path between two cities is displayed through StdDraw library
     * @param args Main input arguments are not used
     */
    public static void main(String[] args) throws FileNotFoundException {
        // an array list of cities is created
        ArrayList<City> cities = new ArrayList<City>();

        // city coordinates text file is assigned to cityCoordsFile of type File to be used in Scanner
        String cityCoordinates = "city_coordinates.txt";
        File cityCoordsFile = new File(cityCoordinates);

        // if file does not exist inform the user and exit the program
        if (!cityCoordsFile.exists()) {
            System.out.printf("%s cannot be found.", cityCoordinates);
            System.exit(1);
        }
        // it scans the file for parsing later
        Scanner cityCoordsInputFile = new Scanner(cityCoordsFile);

        // reads the file line by line
        while (cityCoordsInputFile.hasNextLine()) {
            String line = cityCoordsInputFile.nextLine(); // current line is kept
            String[] info = line.split(","); // since line is seperated with commas, we parse it
            String cityName = info[0]; // the first element is the city name
            int xCord = Integer.parseInt(info[1].trim()); // the second element is the x coordinate of the city
            int yCord = Integer.parseInt(info[2].trim()); // the third element is the y coordinate of the city
            // created an instance of city with  cityName,xCord,yCord and added to cities ArrayList
            cities.add(new City(cityName, xCord, yCord));
        }
        cityCoordsInputFile.close(); // for safety, file is closed
        // city connections text file is assigned to cityConnectionFile of type File to be used in Scanner
        String cityConnections = "city_connections.txt";
        File cityConnectionsFile = new File(cityConnections);
        // if file does not exist inform the user and exit the program
        if (!cityConnectionsFile.exists()) {
            System.out.printf("%s cannot be found.", cityCoordinates);
            System.exit(1);
        }
        // it scans the file for parsing later
        Scanner cityConnectionsInputFile = new Scanner(cityConnectionsFile);
        // created an ArrayList each index represents key and the ArrayLists represent its values
        ArrayList<ArrayList<City>> connections = new ArrayList<>();
        //  initialized the connections ArrayList
        for (int i = 0; i < cities.size(); i++) {
            connections.add(new ArrayList<>());
        }
        while (cityConnectionsInputFile.hasNextLine()) {
            String line = cityConnectionsInputFile.nextLine(); // current line is kept
            String[] info = line.split(","); // since line is seperated with commas, we parse it
            String city1 = info[0]; // the first element is the name of the first city
            String city2 = info[1]; // the second element is the name of the second city
            City tempCity1 = stringToClass(city1, cities); // used stringToClass method to convert city name to city class
            City tempCity2 = stringToClass(city2, cities);
            // filled the ArrayList with sources and neighbours
            connections.get(cities.indexOf(tempCity1)).add(tempCity2);
            connections.get(cities.indexOf(tempCity2)).add(tempCity1);
        }
        cityConnectionsInputFile.close(); // closed the file for safety
        Scanner input = new Scanner(System.in); // created input scanner to read input
        String initialCity; // starting city is declared in String and City type
        City sourceCity;
        while (true) {
            System.out.print("Enter starting city: ");
            initialCity = input.nextLine(); // it assigns the taken input to the initialCity string variable
            // if the input string is one of the cities name,
            // it is converted to a class otherwise it will be null
            sourceCity = stringToClass(initialCity, cities);
            if (sourceCity == null)
                System.out.printf("City named '%s' not found. Please enter a valid city name.\n", initialCity);
            else break; // breaks if a valid input is gotten
        }
        String lastCity; // ending city is declared in String and City type
        City destCity;
        while (true) {
            System.out.print("Enter destination city: ");
            lastCity = input.nextLine(); // it assigns the taken input to the lastCity string variable
            // if the input string is one of the cities name,
            // it is converted to a class otherwise it will be null
            destCity = stringToClass(lastCity, cities);
            if (destCity == null)
                System.out.printf("City named '%s' not found. Please enter a valid city name.\n", lastCity);
            else break; // breaks if a valid input is gotten
        }
        ArrayList<City> result = shortestPath(cities, connections, sourceCity, destCity); // contains the shortest path
        if(result == null || !result.contains(sourceCity)) System.out.println("No path could be found."); // if result is null meaning that there is no path
        else {
            double totalDistance = result.getLast().cost; // the total distance is accumulated in the last city's cost
            System.out.printf("Total Distance: %.2f.", totalDistance);
            System.out.print("Path: ");
            for (City city : result) {
                if (city == destCity) System.out.println(city.cityName); // if it is the last city don't print "->"
                else System.out.print(city.cityName + "->");
            }

            StdDraw.setCanvasSize(2377/2, 1055/2); // sets canvas size that is suitable for image
            StdDraw.setXscale(0,2377); // x and y axis are scaled
            StdDraw.setYscale(0,1055);

            // draws the given image on the canvas
            StdDraw.picture(2377/2.0, 1055/2.0, "map.png",2377,1055);
            StdDraw.enableDoubleBuffering(); // for smooth display
            StdDraw.setPenColor(StdDraw.GRAY); // sets pen color to gray
            StdDraw.setPenRadius(0.002); // sets pen thickness to 0.002 unit
            StdDraw.setFont( new Font("Serif", Font.PLAIN, 12) ); // serif font is used and the font size :12
            for(int i = 0 ; i< connections.size() ; ++i){
                StdDraw.text(cities.get(i).x, cities.get(i).y + 13, cities.get(i).cityName); // city name is written just above the drawn point
                StdDraw.filledCircle(cities.get(i).x,cities.get(i).y,5);
                for (City neighbour :connections.get(i)){
                    StdDraw.text(neighbour.x, neighbour.y + 13, neighbour.cityName);
                    StdDraw.filledCircle(neighbour.x,neighbour.y,5);
                    StdDraw.line(cities.get(i).x,cities.get(i).y, neighbour.x,neighbour.y); // source and neighbour city is connected
                }
            }
            StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE); // to specify destination road, pen color is set to blue
            StdDraw.setPenRadius(0.0086); // thickness is increased to specify the road

            if (result.size() == 1){
                // meaning that the city itself is entered as both starting and destination cities
                City first = result.getFirst();
                StdDraw.text(first.x, first.y + 13, first.cityName);
                StdDraw.filledCircle(first.x,first.y,5);
            }
            else {
                for (int i = 0; i < result.size() - 1; i++) {
                    // city name is written just above the drawn point but in blue
                    City first = result.get(i);
                    StdDraw.text(first.x, first.y + 13, first.cityName);
                    StdDraw.filledCircle(first.x, first.y, 5);

                    City next = result.get(i + 1);
                    StdDraw.text(next.x, next.y + 13, next.cityName);
                    StdDraw.filledCircle(next.x, next.y, 5);
                    // draws line in blue between two consecutive cities
                    StdDraw.line(first.x, first.y, next.x, next.y);
                }
            }
            StdDraw.show(); // shows on the canvas
        }

    }

    public static ArrayList<City> shortestPath(ArrayList<City> cities, ArrayList<ArrayList<City>> connections, City source, City destination) {
        source.cost = 0;// Cost from source to source is 0

        ArrayList<City> visited = new ArrayList<City>(); // created a visited Arraylist to prevent revisiting cities
        City current = source; // starting with source
        while (!visited.contains(destination)) {
            visited.add(current);
            for (City neighbor : connections.get(cities.indexOf(current))) {
                if (!visited.contains(neighbor)) {
                    // cost is computed as the total distance thus far + distance between current and neighbour
                    double cost = current.cost + distance(current, neighbor);
                    if (cost < neighbor.cost) {
                        // if cost is less then, update the cost attribute
                        neighbor.cost = cost;
                        neighbor.previousCities = new ArrayList<>(current.previousCities); // keeps track of the path
                        neighbor.previousCities.add(current);
                    }
                }
            }
            // Select the unvisited city with the smallest known distance
            double minDistance = Double.MAX_VALUE;
            for (City city : cities) {
                if (!visited.contains(city) && city.cost < minDistance) {
                    // the next starting is chosen as the min cost city
                    minDistance = city.cost;
                    current = city;
                }
            }
            if (minDistance == Double.MAX_VALUE) {
                // Destination is unreachable
                return null;
            }
        }
        ArrayList<City> result = new ArrayList<>();
        result.addAll(destination.previousCities); // add all the cities to get to destination
        result.add(destination); // since destination city is also a part of the path, it is added to result
        return result;
    }

    /**
     * calculates the Euclidean distance between two cities
     * @param city1 first city
     * @param city2 second city
     * @return Euclidean distance
     */
    public static double distance(City city1, City city2) {
        return Math.sqrt(Math.pow((city1.x - city2.x), 2) + Math.pow((city1.y - city2.y), 2));
    }
    /**
     *
     * @param cName a string which is a possible city name
     * @param cities containing cities of City type
     * @return the city whose name is 'cName' if there is no, returns null
     */
    public static City stringToClass(String cName, ArrayList<City> cities) {
        for (City city : cities) {
            if (city.cityName.equals(cName)) return city;
        }
        return null;
    }
}

