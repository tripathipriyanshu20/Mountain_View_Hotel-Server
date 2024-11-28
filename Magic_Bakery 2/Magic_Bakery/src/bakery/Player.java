package bakery;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * Represents a player in the bakery game.
 * The {@code Player} class shows a simple calculator with basic operations.
 * It provides methods to calculator functions of addition, subtraction, multiplication, and division.
 */
public class Player implements Serializable {

    /**
     * list of ingredients present in the players hand
     */
    private List<Ingredient> hand;

    /**
     * The name of current player
     */
    private String name;

    /**
     * For serialisation and deserialisation,
     * use the serial version UID.
     * This is done to make sure that the class definition and the serialised and deserialised objects are compatible.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new player with the name given.
     *
     * @param name the name of player
     */
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    /**
     * Adds the given ingredients to current player's hand.
     *
     * @param ingredients consists of list of ingredients to add to player's hand
     */
    public void addToHand(List<Ingredient> ingredients) {
        hand.addAll(ingredients);
    }

    /**
     * Adds one ingredient to the current player's hand.
     *
     * @param ingredient consists of the ingredient to add to player's hand
     */
    public void addToHand(Ingredient ingredient) {
        hand.add(ingredient);
    }

    /**
     * Verifies if the current player has a specific ingredient present in their hand.
     *
     * @param ingredient the ingredient to check the availability of
     * @return true if the player has the ingredient, else false
     */
    public boolean hasIngredient(Ingredient ingredient) {
        return hand.stream().anyMatch(i -> i.getName().equalsIgnoreCase(ingredient.getName()));

    }

    /**
     * Removes one ingredient from the current player's hand.
     *
     * @param ingredient the ingredient to remove from the player's hand
     */
    public void removeFromHand(Ingredient ingredient) {
        hand.removeIf(i -> i.getName().equalsIgnoreCase(ingredient.getName()));
    }

    /**
     * Fetches the list of ingredients present in the current player's hand.
     *
     * @return the list of ingredients present in the current player's hand
     */
    public List<Ingredient> getHand() {
        return hand;
    }

    /**
     * Fetches the name of player.
     *
     * @return name of player
     */
    public String getName() {
        return name;
    }

    /**
     * Attempts to use a Helpful Duck card from the player's hand.
     * If a Helpful Duck card is present in the hand, it removes one from the hand and returns true.
     * If no Helpful Duck card is present, it returns false.
     *
     * @return true if a Helpful Duck card was successfully used, false otherwise.
     */
    public boolean useHelpfulDuck() {
        Ingredient helpfulDuck = new Ingredient("Helpful Duck");
        if (this.hand.contains(helpfulDuck)) {
            this.hand.remove(helpfulDuck);  // Remove one Helpful Duck card from the hand
            return true;  // Return true if the Helpful Duck was used
        }
        return false;  // Return false if the Helpful Duck was not available to use
    }

    /**
     * Fetches a string representing the ingredients in the player's hand.
     *
     * @return a string representing the ingredients present in the player's hand
     */
    public String getHandStr() {
        StringBuilder handStr = new StringBuilder();
        for (Ingredient ingredient : hand) {
            handStr.append(ingredient).append(", ");
        }
        return handStr.toString();
    }

    /**
     * Returns a string representing the player.
     *
     * @return a string representing the player
     */
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                '}';
    }
}
