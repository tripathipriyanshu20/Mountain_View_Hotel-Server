package bakery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.Serializable;

/**
 * Represents an order placed by a customer in the bakery game. Each order includes details such as the customer's name, the desired recipe, any garnish preferences, and the current status of the order.
 * Customer orders play a crucial role in the gameplay as players strive to fulfill them to earn points and progress in the game.
 * The CustomerOrder class encapsulates the functionality related to managing customer orders within the bakery game. It provides methods for handling various aspects of customer orders, including tracking the recipe, garnish preferences, and status updates.
 *

 */
public class CustomerOrder implements Serializable{
	/**
     * Unique identifier for serialization and deserialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * List of ingredients required for the recipe.
     */
    private List<Ingredient> recipe;
    
    /**
     * List of ingredients used for garnishing the order.
     */
    private List<Ingredient> garnish;
    
    /**
     * Order's complexity or importance level.
     */
    private int level;
    /**
     * Name of the customer order.
     */
    private String name;
    /**
     * Order's current state, represented by the CustomerOrderStatus enum.
     * Possible values include:
     * - PENDING: Order is waiting to be fulfilled.
     * - FULFILLED: Order has been fulfilled and is ready for delivery.
     * - CANCELLED: Order has been cancelled.
     * - GIVEN_UP: Customer has abandoned the order.
     */
    private CustomerOrderStatus status;

    /**
     * Creates a new CustomerOrder with the specified name, recipe, garnish, and level.
     *
     * @param name the name of the customer order
     * @param recipe the list of ingredients in the order's recipe
     * @param garnish the list of ingredients for garnishing the order
     * @param level the complexity or importance level of the order
     */
    public CustomerOrder(String name, List<Ingredient> recipe, List<Ingredient> garnish, int level) {
        this.name = name;
        this.recipe = recipe;
        this.garnish = garnish;
        this.level = level;
        this.status = CustomerOrderStatus.WAITING; // Default status
    }
    /**
     * Marks the CustomerOrder as canceled.
     */
    public void abandon() {
        this.status = CustomerOrderStatus.GIVEN_UP;
    }
    /**
     * Checks if the CustomerOrder can be prepared with the provided ingredients.
     *
     * @param ingredients the list of ingredients to check
     * @return true if the order can be fulfilled with the provided ingredients, otherwise false
     */
    public boolean canFulfill(List<Ingredient> ingredients) {
        return recipe.stream().allMatch(ingredients::contains);
    }
    /**
     * Checks if the order can be garnished with available ingredients.
     *
     * @param ingredients the ingredients to check for
     * @return true if at least one garnish ingredient is available, else false
     */
    public boolean canGarnish(List<Ingredient> ingredients) {
        return garnish.stream().allMatch(ingredients::contains);
    }
    /**
     * Checks if the order can be fulfilled based on available ingredients and fulfills the order if possible.
     *
     * @param ingredients List of available ingredients.
     * @param garnish Whether the customer wants the order garnished.
     * @return List of ingredients required to fulfill the order, including garnish if requested.
     *         Returns an empty list if the order cannot be fulfilled.
     */
    public List<Ingredient> fulfill(List<Ingredient> ingredients, boolean garnish) {
        if (canFulfill(ingredients) && (!garnish || canGarnish(ingredients))) {
            setStatus(CustomerOrderStatus.FULFILLED);
            if (garnish) {
                return Stream.concat(recipe.stream(), this.garnish.stream()).collect(Collectors.toList());
            }
            return new ArrayList<>(recipe);
        }
        return Collections.emptyList();
    }
    /**
     * Returns the recipe ingredients.
     *
     * @return List of ingredients required for the recipe.
     */
    public List<Ingredient> getRecipe() {
        return recipe;
    }
    /**
     * Generates a formatted description of the recipe.
     *
     * @return String containing the recipe description.
     */
    public String getRecipeDescription() {
        StringBuilder description = new StringBuilder();
        description.append("Recipe for ").append(name).append(":\n");
        for (Ingredient ingredient : recipe) {
            description.append("- ").append(ingredient).append("\n");
        }
        return description.toString();
    }
    /**
     * Returns the garnish ingredients.
     *
     * @return List of ingredients required for the garnish.
     */
    public List<Ingredient> getGarnish() {
        return garnish;
    }
    /**
     * Generates a formatted description of the garnish.
     *
     * @return String containing the garnish description.
     */
    public String getGarnishDescription() {
        StringBuilder description = new StringBuilder();
        description.append("Garnish for ").append(name).append(":\n");
        for (Ingredient ingredient : garnish) {
            description.append("- ").append(ingredient).append("\n");
        }
        return description.toString();
    }
    /**
     * Returns the difficulty level of the order.
     *
     * @return Integer representing the order's difficulty level.
     */
    public int getLevel() {
        return level;
    }
    
    public String getName() {
        return name;
    }

    /**
     * Gets the current status of the customer order.
     *
     * @return status representing the current order status.
     */
    //check once here
    public CustomerOrderStatus getStatus() {
        return status;
    }
    /**
     * Enumeration representing the possible status of a customer order.
     */
    public enum CustomerOrderStatus{
        WAITING,
        FULFILLED,
        GARNISHED,
        IMPATIENT,
        GIVEN_UP
    }
    
    /**
     * Sets the order status.
     *
     * @param status CustomerOrderStatus enum representing the new order status.
     */
    public void setStatus(CustomerOrderStatus status) {
        this.status = status;
    }
    /**
     * Gets a string of the Customer Order Card name for matching
     *
     * @return a string of the customer order card name.
     */

    @Override
    public String toString() {
        return String.format("CustomerOrder{name='%s', level=%d, status=%s}", name, level, status);
    }
}
