package mezz.jei.plugins.vanilla.ingredients;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.plugins.vanilla.VanillaPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackRenderer implements IIngredientRenderer<FluidStack> {
	private final FluidStack fluidStack;

	public FluidStackRenderer(FluidStack fluidStack) {
		this.fluidStack = fluidStack;
	}

	@Override
	public void render(Minecraft minecraft, final int xPosition, final int yPosition) {
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		drawFluid(minecraft, xPosition, yPosition, fluidStack);

		GlStateManager.color(1, 1, 1, 1);

		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
	}

	private void drawFluid(Minecraft minecraft, final int xPosition, final int yPosition, @Nullable FluidStack fluidStack) {
		if (fluidStack == null) {
			return;
		}
		Fluid fluid = fluidStack.getFluid();
		if (fluid == null) {
			return;
		}

		TextureMap textureMapBlocks = minecraft.getTextureMapBlocks();
		ResourceLocation fluidStill = fluid.getStill();
		TextureAtlasSprite fluidStillSprite = null;
		if (fluidStill != null) {
			fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
		}
		if (fluidStillSprite == null) {
			fluidStillSprite = textureMapBlocks.getMissingSprite();
		}

		minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		final int fluidColor = fluid.getColor(fluidStack);
		setGLColorFromInt(fluidColor);

		drawFluidTexture(xPosition, yPosition, fluidStillSprite, 0, 0, 100);
	}

	private static void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;

		GlStateManager.color(red, green, blue, 1.0F);
	}

	private static void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
		double uMin = (double) textureSprite.getMinU();
		double uMax = (double) textureSprite.getMaxU();
		double vMin = (double) textureSprite.getMinV();
		double vMax = (double) textureSprite.getMaxV();
		uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
		vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexBuffer.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
		vertexBuffer.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
		vertexBuffer.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
		vertexBuffer.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
		tessellator.draw();
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft) {
		List<String> tooltip = new ArrayList<String>();
		Fluid fluidType = fluidStack.getFluid();
		if (fluidType == null) {
			return tooltip;
		}

		String fluidName = fluidType.getLocalizedName(fluidStack);
		tooltip.add(fluidName);

		tooltip.add(TextFormatting.DARK_GRAY.toString() + TextFormatting.ITALIC.toString() + "JEI Fluid");

		IIngredientHelper<FluidStack> ingredientHelper = VanillaPlugin.ingredientRegistry.getIngredientHelper(FluidStack.class);
		String modId = ingredientHelper.getModId(fluidStack);
		String modName = Internal.getHelpers().getModIdUtil().getModNameForModId(modId);
		tooltip.add(TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString() + modName);

		return tooltip;
	}

	@Override
	public FontRenderer getFontRenderer(Minecraft minecraft) {
		return minecraft.fontRendererObj;
	}

	@Override
	public FluidStack getIngredient() {
		return fluidStack;
	}
}
