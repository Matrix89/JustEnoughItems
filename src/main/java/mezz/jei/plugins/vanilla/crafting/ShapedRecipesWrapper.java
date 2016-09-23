package mezz.jei.plugins.vanilla.crafting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.plugins.vanilla.VanillaPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

public class ShapedRecipesWrapper extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper {

	private final ShapedRecipes recipe;

	public ShapedRecipesWrapper(ShapedRecipes recipe) {
		this.recipe = recipe;
		for (ItemStack itemStack : this.recipe.recipeItems) {
			if (itemStack != null && itemStack.stackSize != 1) {
				itemStack.stackSize = 1;
			}
		}
	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		IStackHelper stackHelper = VanillaPlugin.jeiHelpers.getStackHelper();

		List<ItemStack> recipeItems = Arrays.asList(recipe.recipeItems);
		List<List<ItemStack>> inputs = stackHelper.expandRecipeInputs(recipeItems);
		ingredients.getInputs(ItemStack.class).addAll(inputs);

		List<ItemStack> outputs = Collections.singletonList(recipe.getRecipeOutput());
		ingredients.getOutputs(ItemStack.class).addAll(outputs);
	}

	@Override
	public List getInputs() {
		return Arrays.asList(recipe.recipeItems);
	}

	@Override
	public List<ItemStack> getOutputs() {
		return Collections.singletonList(recipe.getRecipeOutput());
	}

	@Override
	public int getWidth() {
		return recipe.recipeWidth;
	}

	@Override
	public int getHeight() {
		return recipe.recipeHeight;
	}
}
