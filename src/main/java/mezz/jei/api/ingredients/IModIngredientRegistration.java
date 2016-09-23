package mezz.jei.api.ingredients;

import java.util.Collection;

/**
 * Allows registration of new types of ingredients, beyond the basic ItemStack and FluidStack.
 * After every mod has registered its ingredients, the {@link IIngredientRegistry} is created from this information.
 */
public interface IModIngredientRegistration {
	/**
	 * Register a new type of ingredient.
	 *
	 * @param ingredientClass           The class of the ingredient.
	 * @param allIngredients            A collection of every to be displayed in the ingredient list.
	 * @param ingredientHelper          The ingredient helper to allows JEI to get information about ingredients for searching and other purposes.
	 * @param ingredientRendererFactory The ingredient render factory to allow JEI to render these ingredients in the ingredient list.
	 */
	<V> void register(
			Class<V> ingredientClass,
			Collection<V> allIngredients,
			IIngredientHelper<V> ingredientHelper,
			IIngredientRendererFactory<V> ingredientRendererFactory
	);
}
