package Main;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.animation.PauseTransition;
import bakery.CustomerOrder;
import bakery.Customers;
import bakery.EmptyPantryException;
import bakery.Ingredient;
import bakery.Layer;
import bakery.MagicBakery;
import bakery.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import util.ConsoleUtils;
import javafx.util.Duration;

public class ViewController {
	MagicBakery magicBakery=null;
	Customers customer=null;
	int round=1;
	Iterator<Player> playerIterator =null;
	Player currentPlayer = null;      
	 //private int actionCount = 0;
	  private int actionCountForCurrentPlayer = 0; // Track actions taken by the current player
	    private int totalActionCount = 0; // Track total actions taken in the current round
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label RoundNo;

    @FXML
    private Label CurrentPlayer;

    @FXML
    private Label CurrentPlayerTurn;

    @FXML
    private Label ActiveCustomerRow;

    @FXML
    private Label LayersRow;

    @FXML
    private Label PantryRow;

    @FXML
    private TextField DrawIngredientName;

    @FXML
    private TextField PassCardIngredientName;

    @FXML
    private TextField PassCardPlayerName;

    @FXML
    private TextField BakeLayerName;

    @FXML
    private TextField FulfilCustomerOrerName;

    @FXML
    private TextField Garnishable;

    @FXML
    private ComboBox<String> combobox;

    @FXML
    private Label playerHandDetails;

    @FXML
    private Label FulfilledOrderRow;

    @FXML
    private Label logs;

    @FXML
    void close(ActionEvent event) {
    	System.exit(0);
    }

    @FXML
    void loadGame(ActionEvent event) {
    	
    }
    
    public void UIManager() {
    	//layer Row
    	String layerRow=magicBakery.setUIlayer();  
    	//Ingredient Row
    	String IngredientRow=magicBakery.setUIIngredients();
    	//Player Row
    	String playerDetails = magicBakery.setUIPlayers();
        //Active Customer Row
    	List<CustomerOrder> activeOrders = new ArrayList<>(magicBakery.getCustomers().getActiveCustomers());
        StringBuilder ordersText = new StringBuilder();
        activeOrders.forEach(order -> ordersText.append(order.getName()).append(" - ").append(order.getStatus()).append("\n"));
        
    	////Setting Module on GUI
    	
    	LayersRow.setText(layerRow);
    	PantryRow.setText(IngredientRow);
    	playerHandDetails.setText(playerDetails);
    	ActiveCustomerRow.setText(ordersText.toString());
    	//FulfilCustomerOrerName.setText(waitingOrder.toString());
    }
     
    @FXML
    void performAction(ActionEvent event) throws EmptyPantryException {
        System.out.println("Action event triggered. Round: " + round + ", End Turn: " + magicBakery.endTurn());
        RoundNo.setText(round+"");
        // Initialize or check current player
        if (currentPlayer == null || actionCountForCurrentPlayer >= magicBakery.getActionsPermitted()) {
            if (!playerIterator.hasNext()) {
                playerIterator = magicBakery.getPlayers().iterator();  // Reinitialize the iterator for the next round
                System.out.println("Round: " + round + " ends.");
                magicBakery.getCustomers().processEndOfRound();
                round++;
                if (round > 20) {
                    System.out.println("Game over! Calculate scores or determine winner.");
                    showMsg("Game over! Calculate scores or determine winner.");
                    return;
                }
                System.out.println("Round: " + round + " begins.");
                RoundNo.setText(round+"");
            }
            currentPlayer = playerIterator.next();  // Fetch the next player
            actionCountForCurrentPlayer = 0; // Reset action count for the new player
            System.out.println("Current Player: " + currentPlayer.getName() + ", Starting new set of actions.");
            showMsg("Current Player: " + currentPlayer.getName() + ", Starting new set of actions.");
        }

        // Execute action based on user input or other game logic
        String actionCommand = combobox.getEditor().getText(); // Sample way to get the action command
        processPlayerAction(actionCommand, currentPlayer);

        // Increment the action count and display current action number
        actionCountForCurrentPlayer++;
        System.out.println("Action #" + actionCountForCurrentPlayer + " taken by " + currentPlayer.getName());
        CurrentPlayerTurn.setText(actionCountForCurrentPlayer +"");
        CurrentPlayer.setText(currentPlayer.getName());
        // Update UI or console with the current game state
        UIManager();
    }

  
    void processPlayerAction(String actionCommand,Player currentPlayer) throws EmptyPantryException {
        switch (actionCommand) {
            case "DRAW_INGREDIENT":
                drawIngredient(magicBakery, currentPlayer);               
                break;
            case "PASS_INGREDIENT":
                passIngredient(magicBakery, currentPlayer);
                break;
            case "BAKE_LAYER":
                bakeLayer(magicBakery, currentPlayer);
                break;
            case "FULFIL_ORDER":
                handleFulfilOrder(magicBakery, currentPlayer);
                break;
            case "REFRESH_PANTRY":
                refreshPantry(magicBakery, currentPlayer);
                break;
            default:
                logs.setText("Invalid action choice.");
                magicBakery.printGameState(); // Print game state after each action
                break;
        }
        logs.setText(currentPlayer.getName() + " performed: " + actionCommand);
    }

    @FXML
    void saveGame(ActionEvent event) {
    	
    }
       
    @FXML
    void startGame(ActionEvent event) {
    	showMsg("Navigate to console for setup game");
    	long seed = 12345; // Default seed value
        String ingredientDeckFile = "C:\\Users\\Lenovo\\Downloads\\Magic_Bakery 2\\Magic_Bakery\\src\\Main\\ingredients.csv"; // Default ingredient deck file name
        String layerDeckFile = "C:\\Users\\Lenovo\\Downloads\\Magic_Bakery 2\\Magic_Bakery\\src\\Main\\layers.csv"; // Default layer deck file name
        //String customerDeckFile= System.out.println("enter customer deck file path"); //uncomment it as you need 1 file as input
        String customerDeckFile = "C:\\Users\\Lenovo\\Downloads\\Magic_Bakery 2\\Magic_Bakery\\src\\Main\\customers.csv";
        
         magicBakery = new MagicBakery(seed, ingredientDeckFile, layerDeckFile);
        
          
        ////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("###############################################################################################\n");
         //printGameonGUI();
    	
    	Scanner scc =new Scanner(System.in);
        System.out.println("Enter number of players!");
        int num =scc.nextInt();
        List<String> playerNames = ConsoleUtils.promptForNewPlayers("Confirm number of players: ");
        magicBakery.setupCustomers(customerDeckFile, num);
        magicBakery.startGame(playerNames, customerDeckFile);       
        magicBakery.getCustomers().addCustomerOrder();  
        playerIterator = magicBakery.getPlayers().iterator();      
        //showMsg("Started Game");
        RoundNo.setText(1+"");
        if (!playerNames.isEmpty()) {
            System.out.println("First player's name: " + playerNames.get(0));
            CurrentPlayer.setText((playerNames.get(0)));
        } else {
            System.out.println("No players were added.");
            CurrentPlayer.setText("Loding...");
        }
        CurrentPlayerTurn.setText(1+"");
    	UIManager();
    	
    }
    
    private void handleFulfilOrder(MagicBakery magicBakery, Player currentPlayer) {
        Scanner scanner = new Scanner(System.in);
        logs.setText("Available orders for fulfillment:");
        List<CustomerOrder> activeOrders = new ArrayList<>(magicBakery.getCustomers().getActiveCustomers());
        activeOrders.forEach(order ->System.out.print(order.getName() + " - " + order.getStatus()));

        System.out.print("Enter the name of the customer order you wish to fulfill: ");
       
        String orderName = FulfilCustomerOrerName.getText().trim();
        System.out.println(orderName);
        CustomerOrder chosenOrder = activeOrders.stream()
                                                 .filter(order -> order.getName().equalsIgnoreCase(orderName))
                                                 .findFirst()
                                                 .orElse(null);

        if (chosenOrder == null) {
        	showMsg("No such order found.");
            return;
        }

        // Ask if the order is garnishable
        System.out.print("Is this order garnishable? (1 for Yes, 2 for No): ");
        int garnishOption = Integer.parseInt(Garnishable.getText());
        System.out.println(garnishOption);
        boolean isGarnishable = (garnishOption == 1);

        // Check if the order is garnishable and if the player has garnishable ingredients
        if (isGarnishable && chosenOrder.getGarnish() != null) {
            if (chosenOrder.canGarnish(currentPlayer.getHand())) {
                // Assign any two random ingredients in the player's hand
                assignRandomIngredients(currentPlayer, 2);
            } else {
                showMsg("Cannot fulfill the order due to missing garnishable ingredients.");
                return;
            }
        }

        // Check if player can fulfill the order with one missing ingredient and has a Helpful Duck card
        List<Ingredient> missingIngredients = magicBakery.getCustomers().getMissingIngredients(chosenOrder, currentPlayer.getHand());
        if (missingIngredients.size() == 1 && currentPlayer.hasIngredient(new Ingredient("Helpful Duck"))) {
            currentPlayer.removeFromHand(new Ingredient("Helpful Duck"));
            showMsg("Helpful Duck card used to cover missing ingredient.");
        } else if (missingIngredients.size() > 1) {
        	showMsg("Cannot fulfill the order due to missing ingredients.");
            return;
        }

        // Fulfill the order
        magicBakery.getCustomers().fulfillOrder(chosenOrder);
        System.out.println("Order fulfilled: " + chosenOrder.getName());
        magicBakery.printGameState(); // Print game state after each action
        UIManager();
    }

    private void assignRandomIngredients(Player currentPlayer, int count) {
        List<Ingredient> playerHand = currentPlayer.getHand();

        // Check if the player has enough ingredients
        if (playerHand.size() < count) {
        	showMsg("Player does not have enough ingredients to fulfill garnish requirement.");
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

        showMsg("Random ingredients assigned from player's hand: " + randomIngredients);
        UIManager();
    }

   public void drawIngredient(MagicBakery magicBakery,Player currentPlayer) throws EmptyPantryException {
	// Prompt the user to choose a specific ingredient or a random one from the pantry
       System.out.println("Enter the name of the ingredient you wish to draw (leave blank for random): ");
       Scanner scanner = new Scanner(System.in);
       String ingredientChoice = DrawIngredientName.getText().trim();

       if (ingredientChoice.isEmpty()) {
           // Implement logic to draw a random ingredient from pantry
           boolean drawn = false;
           for (Ingredient ingredient : magicBakery.getPantry()) {
               if (ingredient != null) { // Ensure there's something to draw
                   magicBakery.drawFromPantry(ingredient);
                   currentPlayer.addToHand(ingredient);
                   showMsg(currentPlayer.getName() + " drew " + ingredient.getName() + " from the pantry.");
                   drawn = true;
                   break; // Break after successfully drawing one random ingredient
               }
           }
           if (!drawn) {
        	   showMsg("No ingredients available to draw.");
        	   //throw  emptypantryexceptin here
        	   throw new EmptyPantryException("No ingredients available to draw.");
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
        	   showMsg("The specified ingredient is not available in the pantry.");
           }
       }
       magicBakery.printGameState(); // Print game state after each action
       UIManager();
   }
   
 public void passIngredient(MagicBakery magicBakery,Player currentPlayer) {
	 // Check if current player has cards to pass
     if (!currentPlayer.getHand().isEmpty()) {
    	 showMsg("Available ingredients to pass: " + currentPlayer.getHand());

         // Ask for the ingredient name to pass
         System.out.println("Enter the name of the ingredient to pass:");
         Scanner ingredientScanner = new Scanner(System.in);
         String ingredientName = PassCardIngredientName.getText().trim();

         // Find the ingredient in the current player's hand
         Ingredient toPass = currentPlayer.getHand().stream()
                                 .filter(ing -> ing.getName().equalsIgnoreCase(ingredientName))
                                 .findFirst()
                                 .orElse(null);

         if (toPass == null) {
        	 showMsg("Ingredient not found in hand or invalid name.");
             return;
         }

         // Ask for the name of the player to whom to pass the ingredient
         System.out.println("Enter the name of the player to pass the ingredient to:");
         String recipientName = PassCardPlayerName.getText().trim();
         
         // Find the recipient player
         Player recipient = magicBakery.getPlayers().stream()
                                 .filter(p -> p.getName().equalsIgnoreCase(recipientName))
                                 .findFirst()
                                 .orElse(null);

         if (recipient == null || recipient == currentPlayer) {
        	 showMsg("Invalid recipient or cannot pass to oneself.");
             return;
         }
         //ingredientScanner.close();  //advisible not to close scanner, it may produce errors

         // Pass the card
         magicBakery.passCard(toPass, recipient);
         showMsg(currentPlayer.getName() + " has passed " + toPass.getName() + " to " + recipient.getName());
     } else {
    	 showMsg("No ingredients available to pass.");
    	 logs.setText("No ingredients available to pass.");
     }
     magicBakery.printGameState(); // Print game state after each action
     UIManager();
 }
 
 public void bakeLayer(MagicBakery magicBakery,Player currentPlayer) {
	 System.out.print("Enter the name of the layer you wish to bake: ");
 	 Scanner sc = new Scanner(System.in);
     String layerName = BakeLayerName.getText();
     magicBakery.bakelayer(currentPlayer, layerName);    
     magicBakery.printGameState(); // Print game state after each action
     showMsg("Baked Layer Succesfully!");
     UIManager();
 }
 
 public  void refreshPantry(MagicBakery magicBakery,Player currentPlayer) {
	 // Refresh the pantry
     magicBakery.refreshPantry();
     showMsg("The pantry has been refreshed.");
     magicBakery.printGameState(); // Print game state after each action
     UIManager();
 }

void showMsg(String msg) {
 	Alert alert=new Alert(AlertType.INFORMATION);
 	alert.setTitle("MagicBakery Notificaiton");
 	alert.setHeaderText("Kim Joy's Magic Bakery!" );
 	alert.setContentText(msg);
 	alert.showAndWait();
 }

    @FXML
    void initialize() {
        assert RoundNo != null : "fx:id=\"RoundNo\" was not injected: check your FXML file 'View.fxml'.";
        assert CurrentPlayer != null : "fx:id=\"CurrentPlayer\" was not injected: check your FXML file 'View.fxml'.";
        assert CurrentPlayerTurn != null : "fx:id=\"CurrentPlayerTurn\" was not injected: check your FXML file 'View.fxml'.";
        assert ActiveCustomerRow != null : "fx:id=\"ActiveCustomerRow\" was not injected: check your FXML file 'View.fxml'.";
        assert LayersRow != null : "fx:id=\"LayersRow\" was not injected: check your FXML file 'View.fxml'.";
        assert PantryRow != null : "fx:id=\"PantryRow\" was not injected: check your FXML file 'View.fxml'.";
        assert DrawIngredientName != null : "fx:id=\"DrawIngredientName\" was not injected: check your FXML file 'View.fxml'.";
        assert PassCardIngredientName != null : "fx:id=\"PassCardIngredientName\" was not injected: check your FXML file 'View.fxml'.";
        assert PassCardPlayerName != null : "fx:id=\"PassCardPlayerName\" was not injected: check your FXML file 'View.fxml'.";
        assert BakeLayerName != null : "fx:id=\"BakeLayerName\" was not injected: check your FXML file 'View.fxml'.";
        assert FulfilCustomerOrerName != null : "fx:id=\"FulfilCustomerOrerName\" was not injected: check your FXML file 'View.fxml'.";
        assert Garnishable != null : "fx:id=\"Garnishable\" was not injected: check your FXML file 'View.fxml'.";
        assert combobox != null : "fx:id=\"combobox\" was not injected: check your FXML file 'View.fxml'.";
        assert playerHandDetails != null : "fx:id=\"playerHandDetails\" was not injected: check your FXML file 'View.fxml'.";
        assert FulfilledOrderRow != null : "fx:id=\"FulfilledOrderRow\" was not injected: check your FXML file 'View.fxml'.";
        assert logs != null : "fx:id=\"logs\" was not injected: check your FXML file 'View.fxml'.";
        ArrayList<String> ary=new ArrayList<String>(Arrays.asList("DRAW_INGREDIENT","PASS_INGREDIENT","BAKE_LAYER","FULFIL_ORDER","REFRESH_PANTRY"));
    	combobox.getItems().addAll(ary);
       
    }
}
