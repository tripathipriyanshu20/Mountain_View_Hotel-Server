package bakery;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import util.CardUtils;
import util.StringUtils;

/**
 * Representation of Magic Bakery Game
 * The {@code MagicBakery} class represents a simple calculator with basic operations.
 * It provides methods to perform addition, subtraction, multiplication, and division.
 */
public class MagicBakery implements Serializable{

    /**
     * The serial version UID for serialization and deserialization.
     * Used to ensure that the serialized and deserialized objects
     * are compatible with the class definition.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Representing the collection of layers
     */
    private Collection<Layer> layers;

    /**
     *  Representing the group of players
     */
    private Collection<Player> players;

    /**
     * The ingredients present in the pantry
     */
    private Collection<Ingredient> pantry;

    /**
     * The ingredients present in the pantry deck
     */
    private Collection<Ingredient> pantryDeck;

    /**
     * The ingredients that have been discarded from pantry
     */
    private Collection<Ingredient> pantryDiscard;

    /**
     * random number generator
     */
    private Random random;

    /**
     * Enum representing the type of action in the game
     */
    public enum ActionType{
        DRAW_INGREDIENT,
        PASS_INGREDIENT,
        BAKE_LAYER,
        FULFIL_ORDER,
        REFRESH_PANTRY
    }

    /**
     * Collection of customer orders.
     */
    private Collection<CustomerOrder> customerOrders;

    /** Index of the current player*/
    private Player currentPlayer;

    /**
     * Representing the group of customers
     */
    private Customers customers;

    /**
     * Develops a magic bakery game using the seed, ingredients and deckfile name
     *
     * @param seed the seed for randomization
     * @param ingredientDeckFile file name for the ingredient deck
     * @param layerDeckFile file name for the layer deck
     */
    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile) {
        this.random = new Random(seed);
        this.layers = new ArrayList<>();
        this.players = new ArrayList<>();
        this.pantry = new ArrayList<>();
        this.pantryDeck = CardUtils.readIngredientFile(ingredientDeckFile);
        this.pantryDiscard = new ArrayList<>();
        this.layers.addAll(CardUtils.readLayerFile(layerDeckFile));


    }

    /**
     * Draws an ingredient from pantry
     *
     * @return ingredient from pantry
     */
    private Ingredient drawFromPantryDeck() {
        Iterator<Ingredient> it = pantryDeck.iterator();
        if (it.hasNext()) {
            Ingredient drawnIngredient = it.next();
            it.remove();
            return drawnIngredient;
        } else {
            return null;
        }
    }

    /**
     * Picks ingredient from pantry and adds it to player's hand
     *
     * @param ingredientName the ingredient to pick from pantry
     */
    public void drawFromPantry(String ingredientName) {
        Iterator<Ingredient> it = pantry.iterator();
        while (it.hasNext()) {
            Ingredient ingredient = it.next();
            if (ingredient.getName().equalsIgnoreCase(ingredientName)) {
                it.remove();
                // Replenish the pantry from the deck
                Ingredient newIngredient = drawFromPantryDeck();
                if (newIngredient != null) {
                    pantry.add(newIngredient);
                }
                return;
            }
        }
        // If the ingredient was not found and the player wants a random one from the deck
        Ingredient randomIngredient = drawFromPantryDeck();
        if (randomIngredient != null) {
            pantry.add(randomIngredient);
        }
    }

    /**
     * Picks an ingredient from pantry deck and adds it to player's hand
     *
     * @param ingredient the ingredient to pick from pantry deck
     */
    public void drawFromPantry(Ingredient ingredient) {
        if (pantry.remove(ingredient)) {
            // Replenish the pantry from the deck
            Ingredient newIngredient = drawFromPantryDeck();
            if (newIngredient != null) {
                pantry.add(newIngredient);
            }
        }
    }

    /**
     * The current player's turn is ended the next player plays
     *
     * @return true if turn was successfully completed, else false
     */
    public boolean endTurn() {

        return false;
    }

    /**
     * Returns number of actions permitted for every player
     *
     * @return the number of actions permitted for every player
     */
    public int getActionsPermitted() {
        return players.size() <= 3 ? 3 : 2;
    }

    /**
     * Returns number of actions remaining for current player
     *
     * @return number of actions remaining for current player
     */
    public int getActionsRemaining() {

        return 0;
    }

    /**
     * Returns a collection of layers that are bakeable based on the current player's hand
     *
     * @return the collection of layers that are bakeable
     */
    public Collection<Layer> getBakeableLayers() {
        List<Layer> bakeableLayers = new ArrayList<>();
        for (Layer layer : layers) {
            List<Ingredient> recipe = layer.getRecipe();
            boolean canBake = true;
            for (Ingredient ingredient : recipe) {
                if (!pantry.contains(ingredient)) {
                    canBake = false;
                    break;
                }
            }
            if (canBake) {
                bakeableLayers.add(layer);
            }
        }
        return bakeableLayers;
    }

    /**
     * Bakes the layer given if it can be baked with current ingredients present
     *
     * @param player the current player who is baking the layer
     * @param layerName the current layer to bake
     */
    public void bakelayer(Player player, String layerName) {
        // Find the layer by name from the available layers
        Layer layerToBake = null;
        for (Layer layer : layers) {
            if (layer.getName().equalsIgnoreCase(layerName)) {
                layerToBake = layer;
                break;
            }
        }

        if (layerToBake == null) {
            System.out.println("Layer not found: " + layerName);
            return;
        }

        List<Ingredient> recipe = layerToBake.getRecipe();
        Map<String, Integer> ingredientCountNeeded = new HashMap<>();

        // Prepare a map of needed ingredients by their count
        for (Ingredient ingredient : recipe) {
            ingredientCountNeeded.put(ingredient.getName(), ingredientCountNeeded.getOrDefault(ingredient.getName(), 0) + 1);
        }

        boolean canBake = true;
        System.out.println(player.getName() + " attempting to bake " + layerToBake.getName() + " with recipe:");

        // Check if player has all ingredients in the required quantities
        for (Map.Entry<String, Integer> entry : ingredientCountNeeded.entrySet()) {
            int countInHand = (int) player.getHand().stream()
                    .filter(i -> i.getName().equalsIgnoreCase(entry.getKey()))
                    .count();

            if (countInHand < entry.getValue()) {
                System.out.println("Missing: " + entry.getKey());
                canBake = false;
                break;
            }
        }

        if (canBake) {
            // Remove the correct amount of each ingredient from the player's hand
            for (Map.Entry<String, Integer> entry : ingredientCountNeeded.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    player.removeFromHand(new Ingredient(entry.getKey())); // Assumes Ingredient equals/hashCode is implemented based on name
                }
            }
            player.addToHand(new Ingredient(layerToBake.getName())); // Simulate adding the baked layer as an ingredient to player's hand
            System.out.println(player.getName() + " has successfully baked and added " + layerToBake.getName() + " to their hand.");
        } else {
            System.out.println("Cannot bake the layer due to missing ingredients.");
        }
    }


    /**
     * Returns the existing Layers
     *
     * @return the existing layers
     */
    public Collection<Layer> getLayers() {
        return layers;
    }

    /**
     * Returns the ingredients present in the pantry
     *
     * @return the ingredients present in the pantry
     */
    public Collection<Ingredient> getPantry() {
        return pantry;
    }

    /**
     * Returns the players in the game
     *
     * @return the players in the game
     */
    public Collection<Player> getPlayers() {
        return players;
    }

    /**
     * Gives an ingredient from the current player to a different player.
     *
     * @param ingredient the ingredient to pass to another player
     * @param recipient the other player
     */
    public void passCard(Ingredient ingredient, Player recipient) {
        if (ingredient == null || recipient == null) {
            System.out.println("Invalid operation: Ingredient or recipient is null.");
            return;
        }

        // Find the player who has the ingredient
        for (Player player : players) {
            if (player.hasIngredient(ingredient)) {
                // Remove the ingredient from this player's hand
                player.removeFromHand(ingredient);

                // Add the ingredient to the recipient's hand
                recipient.addToHand(ingredient);
                System.out.println(player.getName() + " has passed " + ingredient.getName() + " to " + recipient.getName());
                return;
            }
        }
        System.out.println("Operation failed: No player found with the specified ingredient.");
    }

    /**
     * Prints game state with the current layers and ingredients.
     */
    public void printGameState() {

        // Display the layers row
        StringBuilder layersDisplay = new StringBuilder("Layers Row: ");
        for (Layer layer : layers) {
            layersDisplay.append(layer.getName()).append(" - Recipe: [");
            List<Ingredient> recipe = layer.getRecipe();
            for (int i = 0; i < recipe.size(); i++) {
                layersDisplay.append(recipe.get(i).getName());
                if (i < recipe.size() - 1) {
                    layersDisplay.append(", ");
                }
            }
            layersDisplay.append("] | ");
        }
        if (layersDisplay.length() > 0) {
            layersDisplay.setLength(layersDisplay.length() - 3); // Remove the last " | "
        }
        System.out.println(layersDisplay.toString());

        // Display the pantry row
        StringBuilder pantryDisplay = new StringBuilder("Pantry Row: ");
        for (Ingredient ingredient : pantry) {
            pantryDisplay.append(ingredient.getName()).append(", ");
        }
        if (pantryDisplay.length() > 0) {
            pantryDisplay.setLength(pantryDisplay.length() - 2); // Remove the last comma and space
        }
        System.out.println(pantryDisplay.toString());

        // Display players and their hands
        System.out.println("Players:");
        for (Player player : players) {
            System.out.print(player.getName() + " - Hand: ");
            for (int i = 0; i < player.getHand().size(); i++) {
                System.out.print(player.getHand().get(i).getName());
                if (i < player.getHand().size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }

    }


    /**
     * The pantry will be refreshed by moving ingredients from the discarded pantry
     */
    public void refreshPantry() {
        // Discard all ingredients currently in the pantry
        pantryDiscard.addAll(pantry);
        pantry.clear();

        // Attempt to refill the pantry with 5 new cards from the deck
        List<Ingredient> tempPantryDeck = new ArrayList<>(pantryDeck);
        for (int i = 0; i < 5; i++) {
            if (!tempPantryDeck.isEmpty()) {
                pantry.add(tempPantryDeck.remove(0));
            } else {
                break; // Break if the pantry deck is empty
            }
        }

        // Update the main pantryDeck Collection with the modified temporary list
        pantryDeck.clear();
        pantryDeck.addAll(tempPantryDeck);

        // Optionally, handle the situation where pantryDeck is empty
        if (pantry.size() < 5) {
            System.out.println("Not enough ingredients in the pantry deck to refill the pantry row.");
            // You might choose to shuffle the discard into the deck if needed
            if (!pantryDiscard.isEmpty()) {
                tempPantryDeck.addAll(pantryDiscard);
                pantryDiscard.clear();
                Collections.shuffle(tempPantryDeck);
                pantryDeck.addAll(tempPantryDeck);

                // Try refilling the pantry again if still needed
                for (int i = pantry.size(); i < 5 && !tempPantryDeck.isEmpty(); i++) {
                    pantry.add(tempPantryDeck.remove(0));
                }
            }
        }

        System.out.println("Pantry has been refreshed. Current pantry items:");
        for (Ingredient ingredient : pantry) {
            System.out.println(ingredient.getName());
        }
    }

    /**
     * Starts the game taking the player names and the customer deck file as inputs
     *
     * @param playerNames the names of the players that will be playing the game
     * @param customerDeckFile the file name for the customer deck that will be served
     */
    public void startGame(List<String> playerNames, String customerDeckFile) {
        // Initialize game by adding players
        for (String playerName : playerNames) {
            players.add(new Player(playerName));
        }
        System.out.println("Added players: " + players);

        // Setup initial pantry deck with Helpful Duck and shuffle
        List<Ingredient> tempList = new ArrayList<>(pantryDeck);
        System.out.println("Initial deck size before adding Helpful Duck: " + tempList.size());

        tempList.add(Ingredient.HELPFUL_DUCK); // Only for the first game as per rules
        Collections.shuffle(tempList);

        // Ensure there are enough cards to setup pantry and distribute to players
        if (tempList.size() < 5 + playerNames.size() * 3) {
            System.out.println("Not enough cards in deck to start the game.");
            return; // Exit if not enough cards
        }


        // Setup the initial pantry row of 5 cards
        pantry.clear();
        for (int i = 0; i < 5; i++) {
            pantry.add(tempList.remove(0));
        }

        // Assign each player 3 random Ingredient cards to start the game
        for (Player player : players) {
            for (int i = 0; i < 3; i++) {
                player.addToHand(tempList.remove(0));
            }
        }

        // Check if all players received the cards
        for (Player player : players) {
            if (player.getHand().size() < 3) {
                System.out.println("Error: Player " + player.getName() + " did not receive enough cards.");
            }
        }

        System.out.println("Now we are ready to play\nStarting the game...");

        pantryDeck.clear();
        pantryDeck.addAll(tempList);

        System.out.println("Game setup complete. Starting gameplay...");
        printGameState(); // Print initial game state

    }

    /**
     * Returns a collection of Customer orders  with the current ingredients in hand
     *
     * @return the collection of customer orders that can be fulfilled
     */
    public Collection<CustomerOrder> getFulfillableCustomers() {
        List<Ingredient> currentPlayerIngredients = new ArrayList<>(currentPlayer.getHand());
        return customerOrders.stream()
                .filter(order -> order.canFulfill(currentPlayerIngredients))
                .collect(Collectors.toList());
    }

    /**
     * Fulfill a customer order and optionally garnish it if applicable.
     * @param customer The customer order to fulfill.
     * @param garnish Indicates whether to apply garnish to the order.
     * @return A list of ingredients used to fulfill the order.
     */
    public List<Ingredient> fulfillOrder(CustomerOrder customer, boolean garnish) {
        List<Ingredient> usedIngredients = new ArrayList<>();
        if (customer.canFulfill(currentPlayer.getHand())) {
            usedIngredients.addAll(customer.getRecipe()); // Simulate using ingredients

            // Remove ingredients from player's hand
            customer.getRecipe().forEach(ingredient -> currentPlayer.removeFromHand(ingredient));

            if (garnish && customer.getGarnish() != null) {
                usedIngredients.addAll(customer.getGarnish());
                customer.getGarnish().forEach(ingredient -> currentPlayer.removeFromHand(ingredient));
            }

            // Additional logic to handle the aftermath of fulfilling the order (e.g., rewards)
            // Example: Adding random ingredients if garnished
            if (garnish) {
                addRandomIngredientsToPlayer(currentPlayer, 2); // Assuming this method exists
            }
        }
        return usedIngredients;
    }

    /**
     * Adds random ingredients to the current player's hand
     *
     * @param player the current player
     * @param count the cards present
     */
    private void addRandomIngredientsToPlayer(Player player, int count) {
        List<Ingredient> tempList = new ArrayList<>(pantry); // Create a temporary list from pantry
        Collections.shuffle(tempList, random); // Shuffle the list for randomness

        // Add up to 'count' ingredients from the shuffled list to the player's hand
        for (int i = 0; i < count && !tempList.isEmpty(); i++) {
            Ingredient ingredient = tempList.remove(0); // Take the top ingredient
            player.addToHand(ingredient); // Add ingredient to the player's hand
            System.out.println("Added " + ingredient.getName() + " to " + player.getName() + "'s hand.");
        }
    }



    // Method to set up customers using a file path provided externally
    /**
     * Sets up customers using a file path provided externally.
     *
     * @param customerDeckFile The file path to the customer deck file.
     * @param numPlayers The number of players in the game.
     */
    public void setupCustomers(String customerDeckFile, int numPlayers) {
        this.customers = new Customers(customerDeckFile, new Random(), this.layers, numPlayers);
    }


    /**
     * Returns the Customers that can be garnished
     *
     * @return the collection of customers that can be garnished
     */
    public Collection<CustomerOrder> getGarnishableCustomers() {
        if (currentPlayer == null) {
            System.out.println("No current player set.");
            return Collections.emptyList();
        }

        return customers.getGarnishable(currentPlayer.getHand());
    }

    /**
     * Returns the customers present
     *
     * @return the customers present
     */
    public Customers getCustomers() {
        return this.customers;
    }

    /**
     * Returns current player
     *
     * @return current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the current player.
     *
     * @param player The player to set as the current player.
     */
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    /**
     * Bakes the layer that has been specified if it can be baked with the current ingredients
     *
     * @param layer the specified layer to bake
     */
    public void bakeLayer(Layer layer)
    {
        if (getBakeableLayers().contains(layer))
        {
            System.out.println("bakeable");
        }
        else {
            System.out.println("Can't Bake this Layer with current ingredient");
        }
    }

    /**
     * Prints the customer Service record for the player
     */
    public void printCustomerServiceRecord() {
    }

    /**
     * Saves the current game state to the file specified
     *
     * @param file the file whose state will be saved
     */
    public void saveState(File file)
    {

    }

    /**
     * Loads the current game state for the specified file
     *
     * @param file the file from where the state will be loaded
     * @return the magic bakery instance loaded from the file
     */
    public static MagicBakery loadState(File file)
    {
        return null;
    }
    
    /**
     * Generates and displays a string representation of all layers along with their ingredients.
     * Each layer is listed with its name followed by its ingredients in a bracketed list.
     * The method formats the output in a way that each layer's description is separated by a pipe symbol,
     * with the final trailing separator being removed for cleanliness.
     *
     * @return A formatted string displaying each layer's name and ingredients.
     */
	public String setUIlayer() {
		// Display the layers row
	    StringBuilder layersDisplay = new StringBuilder("Layers Row: ");
	    for (Layer layer : layers) {
	        layersDisplay.append(layer.getName()).append(" - Recipe: [");
	        List<Ingredient> recipe = layer.getRecipe();
	        for (int i = 0; i < recipe.size(); i++) {
	            layersDisplay.append(recipe.get(i).getName());
	            if (i < recipe.size() - 1) {
	                layersDisplay.append(", ");
	            }
	        }
	        layersDisplay.append("] | ");
	    }
	    if (layersDisplay.length() > 0) {
	        layersDisplay.setLength(layersDisplay.length() - 3); // Remove the last " | "
	    }
	    System.out.println(layersDisplay.toString());
	    	return layersDisplay.toString();
	}
	/**
	 * Generates and displays a string representation of all ingredients currently available in the pantry.
	 * Each ingredient's name is separated by a comma and a space. The final comma and space are removed
	 * from the string to maintain formatting before it's displayed and returned.
	 *
	 * @return A formatted string listing all ingredients in the pantry, suitable for display.
	 */

	public String setUIIngredients() {
		// Display the pantry row
	    StringBuilder pantryDisplay = new StringBuilder("Pantry Row: ");
	    for (Ingredient ingredient : pantry) {
	        pantryDisplay.append(ingredient.getName()).append(", ");
	    }
	    if (pantryDisplay.length() > 0) {
	        pantryDisplay.setLength(pantryDisplay.length() - 2); // Remove the last comma and space
	    }
	    System.out.println(pantryDisplay.toString());
	    return pantryDisplay.toString();
	  
	}
	/**
	 * Constructs and returns a formatted string that represents each player along with the cards in their hand.
	 * The output lists each player's name followed by the contents of their hand, which are detailed with each
	 * card's name separated by commas. The output for each player is displayed on the console and then appended
	 * with a newline character in the final string for clear separation when viewed.
	 *
	 * @return A multi-line string where each line details a player's name and the cards in their hand.
	 */
	public String setUIPlayers() {
	    StringBuilder displayBuilder = new StringBuilder();
	    
	    // Display players and their hands
	    System.out.println("Players:");
	    for (Player player : players) {
	        StringBuilder handBuilder = new StringBuilder();
	        for (int i = 0; i < player.getHand().size(); i++) {
	            handBuilder.append(player.getHand().get(i).getName());
	            if (i < player.getHand().size() - 1) {
	                handBuilder.append(", ");
	            }
	        }
	        String playerInfo = player.getName() + " - Hand: " + handBuilder.toString();
	        System.out.println(playerInfo);
	        displayBuilder.append(playerInfo).append("\n");
	    }
	    
	    return displayBuilder.toString();
	}


}


























