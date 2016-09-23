package mezz.jei.api.ingredients;

/**
 * The ingredient render factory to allow JEI to render these ingredients in the ingredient list.
 */
public interface IIngredientRendererFactory<V> {
	/**
	 * Create a new {@link IIngredientRenderer} with the given ingredient.
	 */
	IIngredientRenderer<V> create(V ingredient);
}
