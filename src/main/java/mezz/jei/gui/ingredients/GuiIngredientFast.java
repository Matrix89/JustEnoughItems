package mezz.jei.gui.ingredients;

import javax.annotation.Nullable;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import mezz.jei.IngredientRegistry;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.config.Config;
import mezz.jei.config.Constants;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.util.Translator;
import mezz.jei.util.color.ColorNamer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ForgeHooksClient;

public class GuiIngredientFast {
	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	private static final int blacklistItemColor = Color.yellow.getRGB();
	private static final int blacklistWildColor = Color.red.getRGB();
	private static final int blacklistModColor = Color.blue.getRGB();

	private final Rectangle area;
	private final int padding;
	private final ItemModelMesher itemModelMesher;

	@Nullable
	private IIngredientRenderer<?> ingredientRenderer;

	public GuiIngredientFast(int xPosition, int yPosition, int padding) {
		this.padding = padding;
		final int size = 16 + (2 * padding);
		this.area = new Rectangle(xPosition, yPosition, size, size);
		this.itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
	}

	public Rectangle getArea() {
		return area;
	}

	public void setIngredientRenderer(IIngredientRenderer ingredientRenderer) {
		this.ingredientRenderer = ingredientRenderer;
	}

	@Nullable
	public IIngredientRenderer getIngredientRenderer() {
		return ingredientRenderer;
	}

	public void clear() {
		this.ingredientRenderer = null;
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return (ingredientRenderer != null) && area.contains(mouseX, mouseY);
	}

	public void renderItemAndEffectIntoGUI() {
		if (ingredientRenderer == null) {
			return;
		}

		Object ingredient = ingredientRenderer.getIngredient();
		if (!(ingredient instanceof ItemStack)) {
			return;
		}

		ItemStack itemStack = (ItemStack) ingredient;

		IBakedModel bakedModel = itemModelMesher.getItemModel(itemStack);
		bakedModel = bakedModel.getOverrides().handleItemState(bakedModel, itemStack, null, null);

		if (Config.isEditModeEnabled()) {
			renderEditMode();
			GlStateManager.enableBlend();
		}

		GlStateManager.pushMatrix();
		{
			GlStateManager.translate(area.x + padding + 8.0f, area.y + padding + 8.0f, 150.0F);
			GlStateManager.scale(16F, -16F, 16F);

			bakedModel = ForgeHooksClient.handleCameraTransforms(bakedModel, ItemCameraTransforms.TransformType.GUI, false);

			GlStateManager.translate(-0.5F, -0.5F, -0.5F);

			Minecraft minecraft = Minecraft.getMinecraft();
			RenderItem renderItem = minecraft.getRenderItem();
			renderItem.renderModel(bakedModel, itemStack);

			if (itemStack.hasEffect()) {
				renderEffect(bakedModel);
			}
		}
		GlStateManager.popMatrix();
	}

	private void renderEffect(IBakedModel model) {
		Minecraft minecraft = Minecraft.getMinecraft();
		TextureManager textureManager = minecraft.getTextureManager();
		RenderItem renderItem = minecraft.getRenderItem();

		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.blendFunc(768, 1);
		textureManager.bindTexture(RES_ITEM_GLINT);
		GlStateManager.matrixMode(5890);

		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
		renderItem.renderModel(model, -8372020);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f1 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
		GlStateManager.translate(-f1, 0.0F, 0.0F);
		GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
		renderItem.renderModel(model, -8372020);
		GlStateManager.popMatrix();

		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(770, 771);
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}

	public void renderSlow() {
		if (Config.isEditModeEnabled()) {
			renderEditMode();
		}

		if (ingredientRenderer != null) {
			ingredientRenderer.render(Minecraft.getMinecraft(), area.x + padding, area.y + padding);
		}
	}

	public void renderOverlay(Minecraft minecraft) {
		if (ingredientRenderer == null) {
			return;
		}

		Object ingredient = ingredientRenderer.getIngredient();
		if (!(ingredient instanceof ItemStack)) {
			return;
		}

		ItemStack itemStack = (ItemStack) ingredient;

		FontRenderer font = getFontRenderer(minecraft, itemStack);
		RenderItem renderItem = minecraft.getRenderItem();
		renderItem.renderItemOverlayIntoGUI(font, itemStack, area.x + padding, area.y + padding, null);
	}

	private void renderEditMode() {
		if (ingredientRenderer == null) {
			return;
		}

		Object ingredient = ingredientRenderer.getIngredient();

		if (Config.isIngredientOnConfigBlacklist(ingredient, Config.IngredientBlacklistType.ITEM)) {
			GuiScreen.drawRect(area.x + padding, area.y + padding, area.x + 8 + padding, area.y + 16 + padding, blacklistItemColor);
			GlStateManager.color(1f, 1f, 1f, 1f);
		}
		if (Config.isIngredientOnConfigBlacklist(ingredient, Config.IngredientBlacklistType.WILDCARD)) {
			GuiScreen.drawRect(area.x + 8 + padding, area.y + padding, area.x + 16 + padding, area.y + 16 + padding, blacklistWildColor);
			GlStateManager.color(1f, 1f, 1f, 1f);
		}
		if (Config.isIngredientOnConfigBlacklist(ingredient, Config.IngredientBlacklistType.MOD_ID)) {
			GuiScreen.drawRect(area.x + padding, area.y + 8 + padding, area.x + 16 + padding, area.y + 16 + padding, blacklistModColor);
			GlStateManager.color(1f, 1f, 1f, 1f);
		}
	}

	public static FontRenderer getFontRenderer(Minecraft minecraft, ItemStack itemStack) {
		Item item = itemStack.getItem();
		FontRenderer fontRenderer = item.getFontRenderer(itemStack);
		if (fontRenderer == null) {
			fontRenderer = minecraft.fontRendererObj;
		}
		return fontRenderer;
	}

	public void drawHovered(Minecraft minecraft) {
		if (ingredientRenderer == null) {
			return;
		}

		renderSlow();
		renderOverlay(minecraft);
		drawHighlight();
	}

	public void drawHighlight() {
		if (ingredientRenderer == null) {
			return;
		}

		GlStateManager.disableDepth();
		Gui.drawRect(area.x, area.y, area.x + area.width, area.y + area.height, 0x7FFFFFFF);
		GlStateManager.color(1f, 1f, 1f, 1f);
		GlStateManager.enableDepth();
	}

	public void drawTooltip(Minecraft minecraft, int mouseX, int mouseY) {
		if (ingredientRenderer == null) {
			return;
		}

		List<String> tooltip = getTooltip(minecraft, ingredientRenderer);
		FontRenderer fontRenderer = ingredientRenderer.getFontRenderer(minecraft);
		Object ingredient = ingredientRenderer.getIngredient();

		if (ingredient instanceof ItemStack) {
			ItemStack itemStack = (ItemStack) ingredient;
			TooltipRenderer.drawHoveringText(itemStack, minecraft, tooltip, mouseX, mouseY, fontRenderer);
		} else {
			TooltipRenderer.drawHoveringText(minecraft, tooltip, mouseX, mouseY, fontRenderer);
		}
	}

	private static <V> List<String> getTooltip(Minecraft minecraft, IIngredientRenderer<V> ingredientRenderer) {
		V ingredient = ingredientRenderer.getIngredient();
		List<String> list = ingredientRenderer.getTooltip(minecraft);

		int maxWidth = Constants.MAX_TOOLTIP_WIDTH;
		for (String tooltipLine : list) {
			int width = minecraft.fontRendererObj.getStringWidth(tooltipLine);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}

		if (Config.isColorSearchEnabled()) {
			ColorNamer colorNamer = Internal.getColorNamer();
			IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
			if (colorNamer != null && ingredientRegistry != null) {
				IIngredientHelper<V> ingredientHelper = ingredientRegistry.getIngredientHelper(ingredient);
				Iterable<Color> colors = ingredientHelper.getColors(ingredient);
				Collection<String> colorNames = colorNamer.getColorNames(colors);
				if (!colorNames.isEmpty()) {
					String colorNamesString = Joiner.on(", ").join(colorNames);
					String colorNamesLocalizedString = TextFormatting.GRAY + Translator.translateToLocalFormatted("jei.tooltip.item.colors", colorNamesString);
					list.addAll(minecraft.fontRendererObj.listFormattedStringToWidth(colorNamesLocalizedString, maxWidth));
				}
			}
		}

		if (Config.isEditModeEnabled()) {
			list.add("");
			list.add(TextFormatting.ITALIC + Translator.translateToLocal("gui.jei.editMode.description"));
			if (Config.isIngredientOnConfigBlacklist(ingredient, Config.IngredientBlacklistType.ITEM)) {
				String description = TextFormatting.YELLOW + Translator.translateToLocal("gui.jei.editMode.description.show");
				list.addAll(minecraft.fontRendererObj.listFormattedStringToWidth(description, maxWidth));
			} else {
				String description = TextFormatting.YELLOW + Translator.translateToLocal("gui.jei.editMode.description.hide");
				list.addAll(minecraft.fontRendererObj.listFormattedStringToWidth(description, maxWidth));
			}

			if (Config.isIngredientOnConfigBlacklist(ingredient, Config.IngredientBlacklistType.WILDCARD)) {
				String description = TextFormatting.RED + Translator.translateToLocal("gui.jei.editMode.description.show.wild");
				list.addAll(minecraft.fontRendererObj.listFormattedStringToWidth(description, maxWidth));
			} else {
				String description = TextFormatting.RED + Translator.translateToLocal("gui.jei.editMode.description.hide.wild");
				list.addAll(minecraft.fontRendererObj.listFormattedStringToWidth(description, maxWidth));
			}

			if (Config.isIngredientOnConfigBlacklist(ingredient, Config.IngredientBlacklistType.MOD_ID)) {
				String description = TextFormatting.BLUE + Translator.translateToLocal("gui.jei.editMode.description.show.mod.id");
				list.addAll(minecraft.fontRendererObj.listFormattedStringToWidth(description, maxWidth));
			} else {
				String description = TextFormatting.BLUE + Translator.translateToLocal("gui.jei.editMode.description.hide.mod.id");
				list.addAll(minecraft.fontRendererObj.listFormattedStringToWidth(description, maxWidth));
			}
		}

		return list;
	}
}
