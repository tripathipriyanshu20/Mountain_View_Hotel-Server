package Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javafx.event.ActionEvent;
import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.Layer;
import bakery.MagicBakery;
import bakery.Player;
import javafx.event.ActionEvent;
import util.CardUtils;
import util.ConsoleUtils;
public class BakeryDriver {
	
    public static void main(String[] args) {
        long seed = 12345; // Default seed value
        String ingredientDeckFile = "C:\\Users\\Lenovo\\Downloads\\Magic_Bakery 2\\Magic_Bakery\\src\\Main\\ingredients.csv"; // Default ingredient deck file name
        String layerDeckFile = "C:\\Users\\Lenovo\\Downloads\\Magic_Bakery 2\\Magic_Bakery\\src\\Main\\layers.csv"; // Default layer deck file name
        //String customerDeckFile= System.out.println("enter customer deck file path"); //uncomment it as you need 1 file as input
        String customerDeckFile = "C:\\Users\\Lenovo\\Downloads\\Magic_Bakery 2\\Magic_Bakery\\src\\Main\\customers.csv";
        // Check if command-line arguments are provided
        if (args.length >= 3) {
            try {
                seed = Long.parseLong(args[0]);
                ingredientDeckFile = args[1];
                layerDeckFile = args[2];
            } catch (NumberFormatException e) {
                System.err.println("Invalid seed value. Using default seed.");
            }
        }
        MagicBakery magicBakery = new MagicBakery(seed, ingredientDeckFile, layerDeckFile);
        Scanner scc =new Scanner(System.in);
        System.out.println("Enter number of players!");
        int num =scc.nextInt();
        List<String> playerNames = ConsoleUtils.promptForNewPlayers("Confirm number of players: ");
        
        // Ensure that customers are setup immediately after the game instance is created
        magicBakery.setupCustomers(customerDeckFile, num);
        magicBakery.startGame(playerNames, customerDeckFile);
        // Add the first order before the game starts
        magicBakery.getCustomers().addCustomerOrder(); 
        
        Iterator<Player> playerIterator = magicBakery.getPlayers().iterator();
       // nextPlayer(playerIterator, magicBakery); // Set the first player as current player.
        Player currentPlayer = null;
        
	    int round = 1;
	
	    
	    while (!magicBakery.endTurn() && round < 20) {
	        if (!playerIterator.hasNext()) {
	            playerIterator = magicBakery.getPlayers().iterator();
	            System.out.println("Round: " + round + " ends...");

	            // Process end of round in customer management
	            magicBakery.getCustomers().processEndOfRound();

	            // Move to the next round
	            round++;
	            System.out.println("Round: " + round + " begins...");
	        }

	        if (playerIterator.hasNext()) {
	            currentPlayer = playerIterator.next();
	            magicBakery.setCurrentPlayer(currentPlayer);
	            System.out.println("Current Player: " + currentPlayer.getName());

	            int actionsPerTurn = magicBakery.getActionsPermitted();
	            for (int actionCount = 1; actionCount <= actionsPerTurn; actionCount++) {
	                System.out.println(currentPlayer.getName() + " is taking action " + actionCount);
	                MagicBakery.ActionType actionChoice = ConsoleUtils.promptForAction("Choose an action: ", magicBakery);

	                
                    switch (actionChoice) {
                    case DRAW_INGREDIENT:
                    	drawIngredient(magicBakery,currentPlayer);
                        break;


                    case PASS_INGREDIENT:
                    	passIngredient(magicBakery,currentPlayer);
                        break;

                  case BAKE_LAYER:
                    	bakeLayer(magicBakery,currentPlayer);
                       
                        break;
                           
                  case FULFIL_ORDER:
                	  handleFulfilOrder(magicBakery, currentPlayer);
                	                   	    
                	    break;

                            
                 case REFRESH_PANTRY:
                           refreshPantry(magicBakery, currentPlayer);
                            break;
                 default:
                            System.out.println("Invalid action choice.");
                            magicBakery.printGameState(); // Print game state after each action
                            break;
                    }
                }
                
                magicBakery.printGameState(); // Print game state after each action
                if (magicBakery.endTurn()) {
                    System.out.println("End of turn for " + currentPlayer.getName());
                }
            }
        }

        System.out.println("Game over! Calculate scores or determine winner.");
    }
    
    private static void handleFulfilOrder(MagicBakery magicBakery, Player currentPlayer) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Available orders for fulfillment:");
        List<CustomerOrder> activeOrders = new ArrayList<>(magicBakery.getCustomers().getActiveCustomers());
        activeOrders.forEach(order -> System.out.println(order.getName() + " - " + order.getStatus()));

        System.out.print("Enter the name of the customer order you wish to fulfill: ");
        String orderName = scanner.nextLine().trim();

        CustomerOrder chosenOrder = activeOrders.stream()
                                                 .filter(order -> order.getName().equalsIgnoreCase(orderName))
                                                 .findFirst()
                                                 .orElse(null);

        if (chosenOrder == null) {
            System.out.println("No such order found.");
            return;
        }
        

        // Ask if the order is garnishable
        System.out.print("Is this order garnishable? (1 for Yes, 2 for No): ");
        int garnishOption = scanner.nextInt();
        boolean isGarnishable = (garnishOption == 1);

        // Check if the order is garnishable and if the player has garnishable ingredients
        if (isGarnishable && chosenOrder.getGarnish() != null) {
            if (chosenOrder.canGarnish(currentPlayer.getHand())) {
                // Assign any two random ingredients in the player's hand
                assignRandomIngredients(currentPlayer, 2);
            } else {
                System.out.println("Cannot fulfill the order due to missing garnishable ingredients.");
                return;
            }
        }

        // Check if player can fulfill the order with one missing ingredient and has a Helpful Duck card
        List<Ingredient> missingIngredients = magicBakery.getCustomers().getMissingIngredients(chosenOrder, currentPlayer.getHand());
        if (missingIngredients.size() == 1 && currentPlayer.hasIngredient(new Ingredient("Helpful Duck"))) {
            currentPlayer.removeFromHand(new Ingredient("Helpful Duck"));
            System.out.println("Helpful Duck card used to cover missing ingredient.");
        } else if (missingIngredients.size() > 1) {
            System.out.println("Cannot fulfill the order due to missing ingredients.");
            return;
        }

        // Fulfill the order
        magicBakery.getCustomers().fulfillOrder(chosenOrder);
        System.out.println("Order fulfilled: " + chosenOrder.getName());
        magicBakery.printGameState(); // Print game state after each action
    }


    private static void assignRandomIngredients(Player currentPlayer, int count) {
        List<Ingredient> playerHand = currentPlayer.getHand();

        // Check if the player has enough ingredients
        if (playerHand.size() < count) {
            System.out.println("Player does not have enough ingredients to fulfill garnish requirement.");
            return;
        }

        // Randomly select 'count' number of ingredients from the player's hand
        Random random = new Random();
        List<Ingredient> randomIngredients = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(playerHand.size());
            Ingredient randomIngredient = playerHand.remove(randomIndex);
            randomIngredients.add(randomIngredient);
        }

        System.out.println("Random ingredients assigned from player's hand: " + randomIngredients);
    }

   public static void drawIngredient(MagicBakery magicBakery,Player currentPlayer) {
	// Prompt the user to choose a specific ingredient or a random one from the pantry
       System.out.println("Enter the name of the ingredient you wish to draw (leave blank for random): ");
       Scanner scanner = new Scanner(System.in);
       String ingredientChoice = scanner.nextLine().trim();

       if (ingredientChoice.isEmpty()) {
           // Implement logic to draw a random ingredient from pantry
           boolean drawn = false;
           for (Ingredient ingredient : magicBakery.getPantry()) {
               if (ingredient != null) { // Ensure there's something to draw
                   magicBakery.drawFromPantry(ingredient);
                   currentPlayer.addToHand(ingredient);
                   System.out.println(currentPlayer.getName() + " drew " + ingredient.getName() + " from the pantry.");
                   drawn = true;
                   break; // Break after successfully drawing one random ingredient
               }
           }
           if (!drawn) {
               System.out.println("No ingredients available to draw.");
           }
       } else {
           // Attempt to draw the specified ingredient from the pantry
           Ingredient ingredientToDraw = null;
           for (Ingredient ingredient : magicBakery.getPantry()) {
               if (ingredient.getName().equalsIgnoreCase(ingredientChoice)) {
                   ingredientToDraw = ingredient;
                   break;
               }
           }
           if (ingredientToDraw != null) {
               magicBakery.drawFromPantry(ingredientToDraw);
               currentPlayer.addToHand(ingredientToDraw);
               System.out.println(currentPlayer.getName() + " specifically drew " + ingredientToDraw.getName() + " from the pantry.");
           } else {
               System.out.println("The specified ingredient is not available in the pantry.");
           }
       }
       magicBakery.printGameState(); // Print game state after each action
   }
   
 public static void passIngredient(MagicBakery magicBakery,Player currentPlayer) {
	 // Check if current player has cards to pass
     if (!currentPlayer.getHand().isEmpty()) {
         System.out.println("Available ingredients to pass: " + currentPlayer.getHand());

         // Ask for the ingredient name to pass
         System.out.println("Enter the name of the ingredient to pass:");
         Scanner ingredientScanner = new Scanner(System.in);
         String ingredientName = ingredientScanner.nextLine().trim();

         // Find the ingredient in the current player's hand
         Ingredient toPass = currentPlayer.getHand().stream()
                                 .filter(ing -> ing.getName().equalsIgnoreCase(ingredientName))
                                 .findFirst()
                                 .orElse(null);

         if (toPass == null) {
             System.out.println("Ingredient not found in hand or invalid name.");
             return;
         }

         // Ask for the name of the player to whom to pass the ingredient
         System.out.println("Enter the name of the player to pass the ingredient to:");
         String recipientName = ingredientScanner.nextLine().trim();
         
         // Find the recipient player
         Player recipient = magicBakery.getPlayers().stream()
                                 .filter(p -> p.getName().equalsIgnoreCase(recipientName))
                                 .findFirst()
                                 .orElse(null);

         if (recipient == null || recipient == currentPlayer) {
             System.out.println("Invalid recipient or cannot pass to oneself.");
             return;
         }
         //ingredientScanner.close();  //advisible not to close scanner, it may produce errors

         // Pass the card
         magicBakery.passCard(toPass, recipient);
         System.out.println(currentPlayer.getName() + " has passed " + toPass.getName() + " to " + recipient.getName());
     } else {
         System.out.println("No ingredients available to pass.");
     }
     magicBakery.printGameState(); // Print game state after each action
 }
 
 public static void bakeLayer(MagicBakery magicBakery,Player currentPlayer) {
	 System.out.print("Enter the name of the layer you wish to bake: ");
 	 Scanner sc = new Scanner(System.in);
     String layerName = sc.nextLine();
     magicBakery.bakelayer(currentPlayer, layerName);    
     magicBakery.printGameState(); // Print game state after each action
 }
 
 
 public static void refreshPantry(MagicBakery magicBakery,Player currentPlayer) {
	 // Refresh the pantry
     magicBakery.refreshPantry();
     System.out.println("The pantry has been refreshed.");
     magicBakery.printGameState(); // Print game state after each action
 }
}