package mezz.jei.gui;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mezz.jei.IngredientRegistry;
import mezz.jei.Internal;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;

public class MasterFocus {
	public static MasterFocus create(IRecipeRegistry recipeRegistry, IFocus<?> focus) {
		return new MasterFocus(recipeRegistry, focus);
	}

	private final IRecipeRegistry recipeRegistry;
	private final IFocus<?> focus;

	public MasterFocus(IRecipeRegistry recipeRegistry) {
		this.recipeRegistry = recipeRegistry;
		this.focus = new Focus<Object>(null);
	}

	public MasterFocus(IRecipeRegistry recipeRegistry, IFocus<?> focus) {
		this.recipeRegistry = recipeRegistry;
		this.focus = focus;
	}

	public IFocus<?> getFocus() {
		return focus;
	}

	public IFocus.Mode getMode() {
		return focus.getMode();
	}

	public boolean equalsFocus(MasterFocus other) {
		if (this.getMode() == other.getMode()) {
			String uid1 = getUidForFocusValue(this.getFocus());
			String uid2 = getUidForFocusValue(other.getFocus());
			return uid1.equals(uid2);
		}
		return false;
	}

	private static <V> String getUidForFocusValue(IFocus<V> focus) {
		V value = focus.getValue();
		IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
		if (ingredientRegistry != null && value != null) {
			IIngredientHelper<V> ingredientHelper = ingredientRegistry.getIngredientHelper(value);
			return ingredientHelper.getUniqueId(value);
		}
		return "null";
	}

	public List<IRecipeCategory> getCategories() {
		return recipeRegistry.getRecipeCategories(focus);
	}

	public List<Object> getRecipes(IRecipeCategory recipeCategory) {
		return recipeRegistry.getRecipes(recipeCategory, focus);
	}

	public Collection<ItemStack> getRecipeCategoryCraftingItems(IRecipeCategory recipeCategory) {
		Collection<ItemStack> craftingItems = recipeRegistry.getCraftingItems(recipeCategory);
		Object ingredient = focus.getValue();
		if (ingredient instanceof ItemStack && focus.getMode() == IFocus.Mode.INPUT) {
			ItemStack itemStack = (ItemStack) ingredient;
			IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
			if (ingredientRegistry != null) {
				IIngredientHelper<ItemStack> ingredientHelper = ingredientRegistry.getIngredientHelper(itemStack);
				ItemStack matchingStack = ingredientHelper.getMatch(craftingItems, itemStack);
				if (matchingStack != null) {
					return Collections.singletonList(matchingStack);
				}
			}
		}
		return craftingItems;
	}
}
