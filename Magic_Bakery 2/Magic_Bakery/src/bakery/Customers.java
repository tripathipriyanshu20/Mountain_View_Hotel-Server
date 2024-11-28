package bakery;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import util.CardUtils;

/**
 * Represents a collection of customers and their orders.
 * The Customers class manages customer orders, including adding new orders,
 * fulfilling orders, and simulating the passing of time to move customers
 * through the queue.
 */
public class Customers implements Serializable {

    /**
     * Collection representing the deck of customer orders.
     */
    private Collection<CustomerOrder> customerDeck = new LinkedList<>();

    /**
     * Collection of active customer orders.
     */
    private Collection<CustomerOrder> activeCustomer = new ArrayList<>();

    /**
     * List of inactive customer orders.
     */
    private List<CustomerOrder> inactiveCustomers;

    /**
     * List of fulfilled customer orders.
     */
    private List<CustomerOrder> fulfilledOrders = new ArrayList<>();

    /**
     * List of customer orders waiting to be served.
     */
    private List<CustomerOrder> waitingCustomerOrders = new ArrayList<>();

    /**
     * Random number generator used for various operations.
     */
    private Random random;

    /**
     * Unique identifier for serialization and deserialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor to initialize the Customers object.
     *
     * @param deckFile   The file containing customer orders.
     * @param random     The random number generator.
     * @param layers     The layers for the bakery items.
     * @param numPlayers The number of players in the game.
     */
    public Customers(String deckFile, Random random, Collection<Layer> layers, int numPlayers) {
        this.random = random;
        this.customerDeck = new LinkedList<>();
        this.activeCustomer = new ArrayList<>();
        this.inactiveCustomers = new ArrayList<>();

        instantiateCustomerDeck(deckFile, layers, numPlayers);

    }

    /**
     * Retrieves the collection of fulfilled customer orders.
     * @return A collection of fulfilled customer orders.
     */
    public Collection<CustomerOrder> getFulfilledOrders() {
        return new ArrayList<>(fulfilledOrders);
    }

    /**
     * Initializes the customer deck with orders from the provided file and shuffles them.
     *
     * @param deckFile   The file containing customer orders.
     * @param layers     The layers for the bakery items.
     * @param numPlayers The number of players in the game.
     */
    private void instantiateCustomerDeck(String deckFile, Collection<Layer> layers, int numPlayers) {
        List<CustomerOrder> orders = CardUtils.readCustomerFile(deckFile, layers);
        Collections.shuffle(orders, random);
        customerDeck.addAll(orders);
        initializeActiveOrders(numPlayers);
    }

    /**
     * Returns a collection of customer orders that can be fulfilled with the given list of ingredients.
     *
     * @param hand The list of ingredients available to fulfill orders.
     * @return A collection of customer orders that can be fulfilled with the provided ingredients.
     */
    public Collection<CustomerOrder> getFulfilable(List<Ingredient> hand)
    {
        return null;
    }

    /**
     * Initializes the active orders based on the number of players in the game.
     *
     * @param numPlayers The number of players in the game.
     */
    private void initializeActiveOrders(int numPlayers) {
        // Selection based on levels and number of players
        Map<Integer, Integer> levelCounts = new HashMap<>();
        switch (numPlayers) {
            case 2: levelCounts.put(1, 4); levelCounts.put(2, 2); levelCounts.put(3, 1); break;
            case 3:
            case 4: levelCounts.put(1, 1); levelCounts.put(2, 2); levelCounts.put(3, 4); break;
            case 5: levelCounts.put(2, 1); levelCounts.put(3, 6); break;
        }

        Map<Integer, List<CustomerOrder>> ordersByLevel = new HashMap<>();
        for (CustomerOrder order : customerDeck) {
            ordersByLevel.computeIfAbsent(order.getLevel(), k -> new ArrayList<>()).add(order);
        }

        List<CustomerOrder> selectedOrders = new ArrayList<>();
        levelCounts.forEach((level, count) -> {
            List<CustomerOrder> levelOrders = ordersByLevel.getOrDefault(level, new ArrayList<>());
            Collections.shuffle(levelOrders, random);
            selectedOrders.addAll(levelOrders.subList(0, Math.min(count, levelOrders.size())));
        });

        Collections.shuffle(selectedOrders, random);
        if (!selectedOrders.isEmpty()) {
            activeCustomer.add(selectedOrders.remove(0)); // Initially add one order
        }
    }

    /**
     * Adds a new customer order to the active orders if the customer deck is not empty
     * and the number of active orders is less than 3.
     *
     * @return The newly added customer order if added successfully, otherwise null.
     */
    public CustomerOrder addCustomerOrder() {
        Queue<CustomerOrder> queue = (Queue<CustomerOrder>) customerDeck;
        if (!queue.isEmpty() && activeCustomer.size() < 3) {
            CustomerOrder newOrder = queue.poll();
            activeCustomer.add(newOrder);
            System.out.println("New order added: " + newOrder.getName() + " - " + newOrder.getStatus());
            return newOrder;
        }
        return null;
    }

    /**
     * Processes end-of-round actions, such as removing the oldest active order if there are exactly three active orders,
     * adding a new order from the customer deck if it's not empty, and providing debug output to track active orders
     * and orders in the waiting row.
     */
    public void processEndOfRound() {
        System.out.println("Processing end of game actions.");
        System.out.println("Active orders before processing: " + activeCustomer.size());

        // Ensure activeCustomer is treated as a List
        List<CustomerOrder> listActiveCustomer = (List<CustomerOrder>) activeCustomer;

        // Check if there are exactly three active orders
        if (listActiveCustomer.size() == 3) {
            // Remove the oldest customer (top left)
            CustomerOrder removedOrder = listActiveCustomer.remove(0);
            waitingCustomerOrders.add(removedOrder);  // Place the removed customer in the waiting row
            System.out.println("Removed oldest order: " + removedOrder.getName());
        }

        // Ensure customerDeck is treated as a Queue
        Queue<CustomerOrder> queueCustomerDeck = (Queue<CustomerOrder>) customerDeck;
        
        // Add a new customer at the end of the list (top right)
        if (!queueCustomerDeck.isEmpty()) {
            CustomerOrder newOrder = queueCustomerDeck.poll();
            listActiveCustomer.add(newOrder);
            System.out.println("New order added at end of game: " + newOrder.getName() + " - " + newOrder.getStatus());
        }

        // Debug output to track which orders are in the active list after processing
        System.out.println("Active orders after processing:");
        for (CustomerOrder order : listActiveCustomer) {
            System.out.println(order.getName() + " - " + order.getStatus());
        }

        // Debug output to track orders in the waiting row
        System.out.println("Orders in the waiting row:");
        for (CustomerOrder order : waitingCustomerOrders) {
            System.out.println(order.getName() + " - " + order.getStatus());
        }
    }
   
    /**
     * Fulfills the specified customer order by removing it from the list of active orders
     * and adding it to the list of fulfilled orders.
     *
     * @param order The customer order to be fulfilled.
     */
    public void fulfillOrder(CustomerOrder order) {
        if (activeCustomer.remove(order)) {
            fulfilledOrders.add(order);
            System.out.println("Order fulfilled and moved to fulfilled list: " + order.getName());
        }
    }

    /**
     * Determines if a customer will soon leave the queue based on the queue size.
     *
     * @return true if there are more than 3 active customers, false otherwise.
     */
    public boolean customerWillLeaveSoon() {
        return !activeCustomer.isEmpty() && activeCustomer.size() > 3;
    }

    /**
     * Draws a customer order from the customer deck.
     *
     * @return The drawn customer order, or null if the deck is empty.
     */
    public CustomerOrder drawCustomer() {
        // Cast customerDeck to Queue to use the poll() method
        Queue<CustomerOrder> queue = (Queue<CustomerOrder>) customerDeck;
        return queue.isEmpty() ? null : queue.poll();
    }
    /**
     * Retrieves the list of active customers.
     *
     * @return A collection containing the active customer orders.
     */
    public Collection<CustomerOrder> getActiveCustomers() {
        return new ArrayList<>(activeCustomer);
    }

    /**
     * Retrieves the full customer deck.
     *
     * @return A collection containing all customer orders in the deck.
     */
    public Collection<CustomerOrder> getCustomerDeck() {
        return new ArrayList<>(customerDeck);
    }

    /**
     * Returns customer orders that can be fulfilled given a list of ingredients in hand.
     *
     * @param hand The list of ingredients available to fulfill orders.
     * @return Collection of customer orders that can be fulfilled with the provided ingredients.
     */
    public Collection<CustomerOrder> getFulfillable(List<Ingredient> hand) {


        return activeCustomer.stream()
                .filter(order -> order.canFulfill(hand))
                .collect(Collectors.toList());
    }

    /**
     * Initializes the customer deck
     *
     * @param deckFile the file that consists the card deck
     * @param layers the layers to add
     * @param numPlayers
     */
    private void initialiseCustomerDeck(String deckFile, Collection<Layer> layers, int numPlayers)
    {
    	System.out.println("initialised");
    }

    /**
     * Adds the specified number of customer orders from the given deck
     * to the customer deck
     *
     * @param deck the deck of customer orders to add from
     * @param numOfCard the number of customer orders to add
     */
    private void addToDeck(List<CustomerOrder> deck, int numOfCard)
    {

    }

    /**
     * Gets a collection of inactive customers
     *
     * @param status the status of inactive customers
     * @return a collection of inactive customers with the status specified
     */
    public Collection<CustomerOrder> getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus status)
    {
        return null;
    }

    /**
     * Retrieves garnishable customer orders based on the provided list of ingredients.
     *
     * @param ingredients The list of ingredients available for garnishing.
     * @return A collection of customer orders that can be garnished with the provided ingredients.
     */
    public Collection<CustomerOrder> getGarnishable(List<Ingredient> ingredients) {
        return activeCustomer.stream()
                .filter(order -> order.canGarnish(ingredients))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the list of ingredients that are missing from the player's hand to fulfill the specified customer order.
     *
     * @param order      The customer order to check for missing ingredients.
     * @param playerHand The list of ingredients available in the player's hand.
     * @return A list of ingredients that are required by the order but not present in the player's hand.
     */
    public List<Ingredient> getMissingIngredients(CustomerOrder order, List<Ingredient> playerHand) {
        List<Ingredient> recipe = order.getRecipe();
        List<Ingredient> missingIngredients = new ArrayList<>();

        for (Ingredient ingredient : recipe) {
            if (!playerHand.contains(ingredient)) {
                missingIngredients.add(ingredient);
            }
        }

        return missingIngredients;
    }


    /**
     * Retrieves inactive customer orders with the specified status.
     *
     * @param status The status of customer orders to filter by.
     * @return A collection of inactive customer orders with the specified status.
     */
    public Collection<CustomerOrder> getInactiveCustomerWithStatus(CustomerOrder.CustomerOrderStatus status) {
        return null;
    }

    /**
     * Checks if the customer deck is empty.
     *
     * @return true if the customer deck is empty, false otherwise.
     */
    public boolean isEmpty() {
        return customerDeck.isEmpty();
    }

    /**
     * Peeks at the next customer in the deck.
     *
     * @return The next customer in the deck, or null if the deck is empty.
     */
    public CustomerOrder peek() {
        // Cast customerDeck to Queue to use the peek() method
        Queue<CustomerOrder> queue = (Queue<CustomerOrder>) customerDeck;
        return queue.peek();
    }

    /**
     * Removes a customer from the active list and moves it to the inactive list.
     *
     * @param customer The customer to be removed.
     */
    public void remove(CustomerOrder customer) {
        activeCustomer.remove(customer);
        inactiveCustomers.add(customer);
    }

    /**
     * Returns the size of the customer deck.
     *
     * @return The number of customer orders in the deck.
     */
    public int size() {
        return customerDeck.size();
    }

    /**
     * Simulates the passing of time by moving customers in the queue.
     *
     * @return The customer order that has been moved, or null if there are no active customers.
     */
    public CustomerOrder timePasses() {
    	List<CustomerOrder> listActiveCustomer = (List<CustomerOrder>) activeCustomer;
        
        if (!listActiveCustomer.isEmpty()) {
            CustomerOrder movedOrder = listActiveCustomer.remove(0); // Remove the first element
            if (listActiveCustomer.size() >= 3) {
                inactiveCustomers.add(movedOrder); // Move to inactive if there is an overflow
            } else {
                listActiveCustomer.add(movedOrder); // Cycle back to the end if space available
            }
            return movedOrder;
        }
        return null;
    }


}
