package bakery;
import java.io.Serializable;

/**
 * Custom exception to signal that the allowable limit of actions has been surpassed.
 * This is applicable in scenarios where the number of actions attempted by a player exceeds the set limit.
 */
public class TooManyActionsException extends Exception implements Serializable {
    /**
     * Stores the upper limit of permissible actions.
     */
    private int maxActions;

    /**
     * Serial version UID for ensuring consistency during the serialization and deserialization process.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor that initializes the exception with a generic message.
     */
    public TooManyActionsException() {
        super("Exceeded maximum number of actions.");
    }

    /**
     * Constructor that allows specification of a maximum number of allowable actions, enhancing the detail of the error message.
     *
     * @param maxActions The maximum number of permissible actions.
     */
    public TooManyActionsException(int maxActions) {
        super("Exceeded maximum number of actions: " + maxActions);
        this.maxActions = maxActions;
    }

    /**
     * Provides the maximum number of actions that can be taken.
     *
     * @return The maximum number of permissible actions.
     */
    public int getMaxActions() {
        return maxActions;
    }

    /**
     * Constructor to initialize the exception with a specific message detailing the issue.
     *
     * @param message Detailed message explaining the cause of the exception.
     */
    public TooManyActionsException(String message) {
        super(message);
    }
}
