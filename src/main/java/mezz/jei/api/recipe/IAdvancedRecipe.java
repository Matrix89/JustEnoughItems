package mezz.jei.api.recipe;

import javax.annotation.Nullable;

/**
 * Offers more control than a regular recipe, at the cost of some performance.
 */
public interface IAdvancedRecipe {

	<V> boolean handlesFocus(IFocus<V> focus);

	@Nullable
	<T extends IRecipeWrapper, V> IRecipeCategory<T> getRecipeCategory(IFocus<V> focus);


}
