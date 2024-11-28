package util;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.MagicBakery;
import bakery.Player;
/**
 * Utility class for handling console input/output operations.
 * 
 * The {@code ConsoleUtils} class represents a operations for managing console related operations.
 * It provides methods to perform utility operations.
 *
 * @author Shiv
 * @version 1.0
 * @since 1.0
 */
public class ConsoleUtils {
	
	/**
     * Console Object
     */
     private Console console;

    /**
     * Constructs a new ConsoleUtils object
     *
     * Initializes the console attribute with the system console object obtained from system.console().
     */
    public ConsoleUtils() {
    	console = System.console();
    }
    
    

    private static final Scanner scanner = new Scanner(System.in);
    /**
     * Reads a line from the console without prompt.
     *
     * @return the line as a String
     */
    public static String readLine() {
        return scanner.nextLine();
    }
    /**
     * Prints a formatted prompt and reads a line from the console.
     *
     * @param fmt  the format string for the prompt
     * @param args arguments referenced by the format specifiers in the format string
     * @return the line as a String
     */

    public static String readLine(String fmt, Object... args) {
        System.out.printf(fmt, args);
        return scanner.nextLine();
    }
    /**
     * Displays a list of actions to the user and prompts them to choose one.
     *
     * @param prompt  the message to display before the choices
     * @param bakery  the bakery game instance where actions are defined
     * @return the chosen ActionType from MagicBakery
     */
    public static MagicBakery.ActionType promptForAction(String prompt, MagicBakery bakery) {
        System.out.println(prompt);
        MagicBakery.ActionType[] actionTypes = MagicBakery.ActionType.values();
        for (int i = 0; i < actionTypes.length; i++) {
            System.out.printf("%d. %s%n", i + 1, actionTypes[i]);
        }

        while (true) {
            System.out.print("Enter the number of the action: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= actionTypes.length) {
                    return actionTypes[choice - 1];
                } else {
                    System.out.println("Invalid choice. Please select a valid action.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    /**
     * Prompts the user to select a customer from a collection.
     *
     * @param prompt    the message to display before the choices
     * @param customers the collection of CustomerOrder from which to choose
     * @return the selected CustomerOrder
     */
    private static CustomerOrder promptForCustomer(String prompt, Collection<CustomerOrder> customers) {
        List<Object> customerObjects = new ArrayList<>(customers);
        return (CustomerOrder) promptEnumerateCollection(prompt, customerObjects);
    }
    /**
     * Prompts the user to select an existing player from the bakery.
     *
     * @param prompt  the message to display before the choices
     * @param bakery  the bakery game instance containing the list of players
     * @return the selected Player
     */
    public static Player promptForExistingPlayer(String prompt, MagicBakery bakery) {
        List<Player> players = new ArrayList<>(bakery.getPlayers());

        // Prompt user to select a player
        System.out.println(prompt);
        System.out.println("Choose a player:");

        List<Object> playerObjects = new ArrayList<>(players);
        Player selectedPlayer = (Player) promptEnumerateCollection("Enter the name of the player: ", playerObjects);
        while (selectedPlayer == null) {
            System.out.println("Invalid choice. Please select a valid player by name.");
            selectedPlayer = (Player) promptEnumerateCollection("Enter the name of the player: ", playerObjects);
        }
        return selectedPlayer;
    }
    /**
     * Prompts the user to choose an ingredient from the list of ingredients.
     * 
     * @param prompt      the prompt message
     * @param ingredients the list of ingredients
     * @return the chosen ingredient
     */
    public static Ingredient promptForIngredient(String prompt, Collection<Ingredient> ingredients) {
        List<Object> ingredientObjects = new ArrayList<>(ingredients);
        return (Ingredient) promptEnumerateCollection(prompt, ingredientObjects);
    }
    /**
     * Prompts the user and reads a file path from the console.
     *
     * @param prompt the prompt to display to the user
     * @return a File object corresponding to the entered file path
     */
    public static File promptForFilePath(String prompt) {
        System.out.print(prompt);
        String filePath = scanner.nextLine();
        return new File(filePath);
    }
    /**
     * Prompts the user to enter names for a specified number of players.
     *
     * @param prompt the prompt to display to the user
     * @return a list containing the names of the players
     */
    public static List<String> promptForNewPlayers(String prompt) {
        int numPlayers = promptForNumberOfPlayers(prompt);
        List<String> playerNames = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            System.out.printf("Enter name for player %d: ", i + 1);
            String playerName = scanner.nextLine();
            playerNames.add(playerName);
        }
        return playerNames;
    }
    /**
     * Prompts the user to enter the number of players, ensuring the number is within an acceptable range.
     *
     * @param prompt the prompt to display before receiving input
     * @return the number of players as an integer
     */
    public static int promptForNumberOfPlayers(String prompt) {
        int numPlayers;
        while (true) {
            System.out.print(prompt);
            try {
                numPlayers = Integer.parseInt(scanner.nextLine());
                if (numPlayers >= 2 && numPlayers <= 5) {
                    break;
                } else {
                    System.out.println("Please enter a number of players between 2 and 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return numPlayers;
    }
    /**
     * Utilizes the yes/no prompt for situations requiring start or load confirmation.
     *
     * @param prompt the prompt to display to the user
     * @return true if the user agrees (yes), false otherwise
     */
    public static boolean promptForStartLoad(String prompt) {
        return promptForYesNo(prompt);
    }
    /**
     * Prompts the user with a yes or no question and interprets the response.
     *
     * @param prompt the question to prompt the user with
     * @return true if the user responds with 'yes' or 'y', false otherwise
     */
    public static boolean promptForYesNo(String prompt) {
        System.out.print(prompt + " (Y/N): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }
    /**
     * Utilizes the yes/no prompt for situations requiring continuation confirmation.
     *
     * @param prompt the prompt to display to the user
     * @return true if the user agrees (yes), false otherwise
     */
    public static boolean promptForContinue(String prompt) {
        return promptForYesNo(prompt);
    }
    /**
     * Utilizes the yes/no prompt for situations requiring overwrite confirmation.
     *
     * @param prompt the prompt to display to the user
     * @return true if the user agrees (yes), false otherwise
     */
    public static boolean promptForOverwrite(String prompt) {
        return promptForYesNo(prompt);
    }
    /**
     * Displays a list of options from a collection and prompts the user to select one.
     *
     * @param prompt     the prompt to display to the user
     * @param collection the collection of objects to choose from
     * @return the selected object from the collection
     */
    public static Object promptEnumerateCollection(String prompt, Collection<Object> collection) {
        System.out.println(prompt);

        // Display the items in the collection using an iterator
        int index = 1;
        Iterator<Object> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Object item = iterator.next();
            System.out.printf("%d. %s%n", index++, item.toString());
        }

        // Prompt user to select an item
        int choice;
        while (true) {
            System.out.print("Enter the number of the item: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= collection.size()) {
                    // Re-obtain the iterator to access the chosen item
                    iterator = collection.iterator();
                    for (int i = 0; i < choice - 1; i++) {
                        iterator.next();
                    }
                    return iterator.next();
                } else {
                    System.out.println("Invalid choice. Please select a valid item.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
