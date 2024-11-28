package bakery;

import java.util.List;
import java.io.Serializable;

/**
 * Represents a player in the bakery game.
 * The {@code Layer} class represents a simple calculator with basic operations.
 * It provides methods to perform addition, subtraction, multiplication, and division.
 */
public class Layer extends Ingredient implements Serializable {

    private List<Ingredient> recipe;
    private static final long serialVersionUID = 1L;

    /**
     * Makes a new Layer with given name and recipe.
     *
     * @param name name of Layer
     * @param recipe list of ingredients layer's recipe
     */
    public Layer(String name, List<Ingredient> recipe) {
        super(name); // Call the constructor of the superclass (Ingredient)
        this.recipe = recipe;
    }

    /**
     * Fetches recipe of layer.
     *
     * @return list of the ingredients present in layer's recipe
     */
    public List<Ingredient> getRecipe() {
        return recipe;
    }

    /**
     * Makes sure that the layer can be baked with ingredients present.
     *
     * @param ingredients consist of list of ingredients present for baking
     * @return true if layer can be baked, else false
     */
    public boolean canBake(List<Ingredient> ingredients) {
        // Check if all ingredients in the layer's recipe are present in the provided ingredients
        for (Ingredient ingredient : recipe) {
            if (!ingredients.contains(ingredient)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the hash code value for the present layer.
     *
     * @return the hash code value for the present layer
     */
    public int hashCode() {
        int var2 = 1;
        var2 = 31 * var2 + (this.recipe == null ? 0 : this.recipe.hashCode());
        return var2;
    }

    /**
     * Fetches the description of the layer's recipe
     *
     * @return string description of layer's recipe
     */
    public String getRecipeDescription() {
        StringBuilder description = new StringBuilder();
        description.append("Recipe for ").append(getName()).append(":\n");
        for (Ingredient ingredient : recipe) {
            description.append("- ").append(ingredient.getName()).append("\n");
        }
        return description.toString();
    }
}
