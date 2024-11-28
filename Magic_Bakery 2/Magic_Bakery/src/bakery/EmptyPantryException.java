package bakery;
import java.io.Serializable;
/**
 * A custom exception indicating that the pantry is empty and cannot fulfill the requested operation.
 * This exception is typically thrown when attempting to perform an action that requires ingredients
 * from the pantry, but the pantry is found to be empty.
 * 
 * The {@code EmptyPantryException} class represents a simple calculator with basic operations.
 * It provides methods to perform addition, subtraction, multiplication, and division.
 */
public class EmptyPantryException extends Exception implements Serializable {
    /**
     * The serial version UID for serialization and deserialization.
     * This is used to ensure that the serialized and deserialized objects
     * are compatible with the class definition.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Constructs an EmptyPantryException with no specified detail message.
     */
    public EmptyPantryException() {
        super();
    }
    /**
     * Constructs an EmptyPantryException with the specified detail message.
     * 
     * @param message The detail message explaining the exception.
     */
    public EmptyPantryException(String message) {
        super(message);
    }
    /**
     * Constructs an EmptyPantryException with the specified detail message and cause.
     * 
     * @param message The detail message explaining the exception.
     * @param cause   The cause of the exception.
     */
    public EmptyPantryException(String message, Throwable cause) {
        super(message, cause);
    }
}




