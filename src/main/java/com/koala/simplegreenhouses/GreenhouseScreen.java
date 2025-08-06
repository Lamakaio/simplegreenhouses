package com.koala.simplegreenhouses;

import java.util.HashMap;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Checkbox.OnValueChange;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GreenhouseScreen extends AbstractContainerScreen<GreenhouseMenu> {
	public static final ResourceLocation GUI_TEXTURE = SimpleGreenhouses.id("textures/screens/ghcontroller.png");
	public static final ResourceLocation BONEMEAL_ICON = SimpleGreenhouses.id("textures/screens/bone_meal.png");
	public static final ResourceLocation TANK = SimpleGreenhouses.id("textures/screens/tank.png");
	private static final ResourceLocation PROGRESS_SPRITE = ResourceLocation
			.withDefaultNamespace("container/furnace/burn_progress");
	private static final ResourceLocation PROGRESS_SPRITE_EMPTY = SimpleGreenhouses.id("arrow");

	private final static HashMap<String, Object> guistate = GreenhouseMenu.guistate;

	ImageButton imagebutton_cross;

	// progress bar stuff
	public static final int BURN_METER_FROM_X = 176;
	public static final int BURN_METER_FROM_Y = 0;
	public static final int BURN_METER_WIDTH = 13;
	public static final int BURN_METER_HEIGHT = 13;
	public static final int BURN_METER_TO_X = 27;
	public static final int BURN_METER_TO_Y = 73;

	public static final int METER_WIDTH = 24;
	public static final int METER_HEIGHT = 16;
	public static final int METER_TO_X = 52;
	public static final int METER_TO_Y = 44;

	public GreenhouseScreen(GreenhouseMenu screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
		this.imageWidth = 176;
		this.imageHeight = 182;
		this.titleLabelX = 8;
		this.titleLabelY = 6;
		this.inventoryLabelX = 8;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	public void render(GuiGraphics graphics, int x, int y, float partialTicks) {
		this.renderBackground(graphics, x, y, partialTicks);
		super.render(graphics, x, y, partialTicks);
		this.renderTooltip(graphics, x, y);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		int xStart = (this.width - this.imageWidth) / 2;
		int yStart = (this.height - this.imageHeight) / 2;

		// draw the background
		graphics.blit(GUI_TEXTURE, xStart, yStart, this.imageWidth, this.imageHeight, 0, 0, this.imageWidth * 2,
				this.imageHeight * 2, this.imageWidth * 2, this.imageHeight * 2);

		// bone meal icon
		graphics.blit(BONEMEAL_ICON, xStart + 52, yStart + 15, 0, 0, 16, 16, 16, 16);

		// empty tank
		graphics.blit(TANK, xStart + 6, yStart + 20, 0, 0, 16, 64, 16, 64);
		// render meter
		int cookMeterPixels = this.getCookMeterPixels(partialTicks);
		if (cookMeterPixels < METER_WIDTH) {
			graphics.blitSprite(PROGRESS_SPRITE_EMPTY, METER_WIDTH, METER_HEIGHT, cookMeterPixels, 0,
					xStart + METER_TO_X + cookMeterPixels,
					yStart + METER_TO_Y, METER_WIDTH - cookMeterPixels, METER_HEIGHT);
		}
		if (cookMeterPixels > 0) {
			graphics.blitSprite(PROGRESS_SPRITE, METER_WIDTH, METER_HEIGHT, 0, 0, xStart + METER_TO_X,
					yStart + METER_TO_Y, cookMeterPixels, METER_HEIGHT);
		}

		// int cookProgress = (this.menu).getCookProgressionScaled() + 1;
		// graphics.blit(GUI_TEXTURE, xStart + COOK_METER_TO_X, yStart +
		// COOK_METER_TO_Y, COOK_METER_FROM_X, COOK_METER_FROM_Y, cookProgress,
		// COOK_METER_HEIGHT);
		// debug recipes to make sure we're doing it right
		// graphics.drawString(Minecraft.getInstance().font, Component.literal("beep boop"), xStart + METER_TO_X + 10,
		// 		yStart + METER_TO_Y + 20, 0x373737, false);
	}

	private int getCookMeterPixels(float partialTicks) {
		if (this.menu.getMaxProgressValue() <= 0) {
			return 0;
		}
		return (METER_WIDTH * this.menu.getProgress()) / this.menu.getMaxProgressValue();
	}

	@Override
	public void init() {
		super.init();

		// imagebutton_cross = new ImageButton(this.leftPos + 47, this.topPos + 67, 16, 16, new WidgetSprites(ResourceLocation.parse("idk:textures/screens/cross.png"), ResourceLocation.parse("idk:textures/screens/cross_select.png")), e -> {
		// 	this.menu.setAssembled(0);
		// }) {
		// 	@Override
		// 	public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
		// 		guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
		// 	}
		// };

		// guistate.put("button:imagebutton_cross", imagebutton_cross);
		// this.addRenderableWidget(imagebutton_cross);
	}
}
