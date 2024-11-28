package bakery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an ingredient used for baking the layer.
 *
 * The {@code Ingredient} class represents a simple calculator with basic operations.
 * It provides methods to perform addition, subtraction, multiplication, and division.
 */
public class Ingredient implements Serializable, Comparable<Ingredient> {

    /**
     * Ingredient's name
     */
    private String name;

    /**
     * A helpful duck ingredient instance.
     */
    public static final Ingredient HELPFUL_DUCK = new Ingredient("Helpful Duck");

    /**
     * The serial version UID for serialization and deserialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for Ingredients
     *
     * @param name the name of ingredient.
     */
    public Ingredient(String name) {
        this.name = name;
    }

    /**
     * Returns a string representing the name
     *
     * @return A string representing the name
     */
    public String getName() {
        return name;
    }

    // Overridden equals method from Object class
    @Override
    /**
     * Indicates whether two objects are equal or not
     *
     * @param o The reference object to compare with.
     * @return True if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(name, that.name);
    }

    // Overridden hashCode method from Object class
    @Override
    /**
     * Returns the hash code value for the soecific ingredient.
     *
     * @return The hash code value for the soecific ingredient.
     */
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Compares this Ingredient object with the specified Layer object for order.
     *
     * @param o The Layer object to be compared.
     * @return A negative integer, zero, or a positive integer
     */
    public int compareTo(Ingredient o) {
        return name.compareTo(o.toString());
    }

    @Override
    /**
     * Returns a string representating ingredient.
     *
     * @return A string representating ingredient.
     */
    public String toString() {
        return name;
    }
}
