package util;

import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.Layer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

	/** Utility class for handling various operations related to bakery orders.
	 * 
	 * The {@code CardUtils} class represents a simple calculator with basic operations.
	 * It provides methods to perform addition, subtraction, multiplication, and division.
	 *
	 * @author Shiv
	 * @version 1.0
	 * @since 1.0
	 */
public class CardUtils {
	/**
     * Reads a customer order file and constructs a list of customer orders from it.
     * Each valid line in the file is converted into a CustomerOrder object.
     *
     * @param path the file path to read customer orders from
     * @param layers a collection of available layers to verify against the order data
     * @return a list of CustomerOrder objects
     */
	public static List<CustomerOrder> readCustomerFile(String path, Collection<Layer> layers) {
	    List<CustomerOrder> customerOrders = new ArrayList<>();
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            
	            CustomerOrder order = stringToCustomerOrder(line, layers);
	            if (order != null) { // Add only valid orders
	                customerOrders.add(order);
	            } else {
	                System.out.println("Invalid or skipped line: " + line); // Debug print for invalid lines
	            }
	        }
	    } catch (IOException e) {
	        System.err.println("Error reading customer file: " + e.getMessage());
	    }
	    return customerOrders;
	}
	/**
     * Reads an ingredient file and returns a list of ingredients
     * 
     * @param path the path to the ingredient file
     * @return the list of ingredients read from the file
     */
	public static List<Ingredient> readIngredientFile(String path) {
	    List<Ingredient> ingredients = new ArrayList<>();
	    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] parts = line.split(","); // Assumes the file is formatted as "name, count"
	            if (parts.length == 2) {
	                String name = parts[0].trim();
	                int count = Integer.parseInt(parts[1].trim());
	                for (int i = 0; i < count; i++) {
	                    ingredients.add(new Ingredient(name));
	                }
	            }
	        }
	    } catch (IOException e) {
	        System.err.println("Error reading ingredient file: " + e.getMessage());
	    } catch (NumberFormatException e) {
	        System.err.println("Error parsing the count of ingredients.");
	    }
	    return ingredients;
	}
	 /**
     * Reads a layer file and returns a list of layers.
     * 
     * @param path the path to the layer file
     * @return the list of layers read from the file
     */

    public static List<Layer> readLayerFile(String path) {
        List<Layer> layers = new ArrayList<>();
        File file = new File(path);
        if (!file.exists() || !file.canRead()) {
            System.err.println("File does not exist or cannot be read: " + path);
            return layers; // Return empty list or consider throwing an exception
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    layers.add(stringToLayer(line));
                } catch (Exception e) { // Catching all exceptions might not be best practice but used here for debugging
                    System.err.println("Error parsing line: " + line);
                    e.printStackTrace(); // For debugging purposes
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading layer file: " + path);
            e.printStackTrace(); // Print stack trace for deeper debugging
        }
        return layers;
    }


    private static CustomerOrder stringToCustomerOrder(String str, Collection<Layer> layers) {
        // Splitting the input string into parts
        String[] parts = str.split(",", -1); // -1 to include trailing empty strings
        if (parts.length < 4) { // Expect at least 4 parts: LEVEL, NAME, RECIPE, GARNISH
            System.err.println("Invalid customer order string: " + str);
            return null;
        }

        // Parsing the level and ensuring it's an integer
        int level;
        try {
            level = Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid level format in customer order string: " + str);
            return null;
        }

        String name = parts[1].trim();
        List<Ingredient> recipe = stringToIngredients(parts[2].trim());
        List<Ingredient> garnish = stringToIngredients(parts[3].trim());

        // No need to handle the fifth part as it doesn't exist in the provided data

        return new CustomerOrder(name, recipe, garnish, level);
    }


    private static Ingredient stringToIngredient(String str) {
        String[] parts = str.split(",");
        if (parts.length != 2) {
            System.err.println("Invalid ingredient string: " + str);
            return null;
        }
        String name = parts[0].trim();
        int count = Integer.parseInt(parts[1].trim());

        // Create a single Ingredient object
        return new Ingredient(name);
    }

    private static List<Ingredient> stringToIngredients(String str) {
        List<Ingredient> ingredients = new ArrayList<>();
        String[] ingredientNames = str.split(";"); // Assuming ingredients are separated by ;
        for (String ingredientName : ingredientNames) {
            ingredientName = ingredientName.trim();
            ingredients.add(new Ingredient(ingredientName));
        }
        return ingredients;
    }

    private static Layer stringToLayer(String line) throws Exception {
        String[] parts = line.split(",", 2); // Split only into two parts: name and the rest
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid layer format: " + line);
        }
        String name = parts[0].trim();
        List<Ingredient> ingredients = new ArrayList<>();
        if (parts.length > 1 && parts[1] != null) {
            String[] ingredientNames = parts[1].split(";");
            for (String ingredientName : ingredientNames) {
                if (!ingredientName.trim().isEmpty()) {
                    ingredients.add(new Ingredient(ingredientName.trim()));
                }
            }
        }
        return new Layer(name, ingredients);
    }

    private static List<Layer> stringToLayers(String line) throws Exception {
        String[] parts = line.split(",", 2); // Split only into two parts: name and the rest
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid layer format: " + line);
        }
        String name = parts[0].trim();
        List<Layer> ingredients = new ArrayList<>();
        
        return ingredients;
    }
    


    // Private constructor to prevent instantiation of the utility class
    private CardUtils() {}
}
