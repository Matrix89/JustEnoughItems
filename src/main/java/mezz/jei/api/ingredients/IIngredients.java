package mezz.jei.api.ingredients;

import java.util.List;

public interface IIngredients {
	/**
	 * Gets the recipe's inputs for one class of ingredients.
	 *
	 * @param ingredientClass The class of ingredient: ItemStack.class, FluidStack.class, etc.
	 * @return The outer list represents the slot, the inner list is a rotating list of ingredients in that slot.
	 */
	<T> List<List<T>> getInputs(Class<T> ingredientClass);

	/**
	 * Gets the recipe's outputs for one class of ingredients.
	 *
	 * @param ingredientClass The class of ingredient: ItemStack.class, FluidStack.class, etc.
	 * @return The list of ingredients representing each output slot.
	 */
	<T> List<T> getOutputs(Class<T> ingredientClass);
}
