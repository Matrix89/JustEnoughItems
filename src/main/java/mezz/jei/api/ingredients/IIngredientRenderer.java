package mezz.jei.api.ingredients;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

/**
 * Renders a type of ingredient in JEI's item list.
 */
public interface IIngredientRenderer<V> {
	V getIngredient();

	void render(Minecraft minecraft, int xPosition, int yPosition);

	List<String> getTooltip(Minecraft minecraft);

	FontRenderer getFontRenderer(Minecraft minecraft);
}
