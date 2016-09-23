package mezz.jei.plugins.vanilla.ingredients;

import java.util.List;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemStackRenderer implements IIngredientRenderer<ItemStack> {
	private final ItemStack itemStack;

	public ItemStackRenderer(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public void render(Minecraft minecraft, int xPosition, int yPosition) {
		FontRenderer font = getFontRenderer(minecraft);
		minecraft.getRenderItem().renderItemAndEffectIntoGUI(null, itemStack, xPosition, yPosition);
		minecraft.getRenderItem().renderItemOverlayIntoGUI(font, itemStack, xPosition, yPosition, null);
		GlStateManager.disableBlend();
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft) {
		List<String> list = itemStack.getTooltip(minecraft.thePlayer, minecraft.gameSettings.advancedItemTooltips);
		for (int k = 0; k < list.size(); ++k) {
			if (k == 0) {
				list.set(k, itemStack.getRarity().rarityColor + list.get(k));
			} else {
				list.set(k, TextFormatting.GRAY + list.get(k));
			}
		}

		return list;
	}

	@Override
	public FontRenderer getFontRenderer(Minecraft minecraft) {
		FontRenderer fontRenderer = itemStack.getItem().getFontRenderer(itemStack);
		if (fontRenderer == null) {
			fontRenderer = minecraft.fontRendererObj;
		}
		return fontRenderer;
	}

	@Override
	public ItemStack getIngredient() {
		return itemStack;
	}
}
