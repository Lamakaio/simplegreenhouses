package com.koala.simplegreenhouses;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Checkbox.OnValueChange;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GreenhouseUnassembledScreen extends AbstractContainerScreen<GreenhouseUnassembledMenu> {

	private final static HashMap<String, Object> guistate = GreenhouseUnassembledMenu.guistate;

	Button button_assemble_greenhouse;

	public GreenhouseUnassembledScreen(GreenhouseUnassembledMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.imageWidth = 176;
		this.imageHeight = 182;
	}

	private static final ResourceLocation GUI_BACKGROUND = ResourceLocation
			.parse("simplegreenhouses:textures/screens/gh_controller_unassembled.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		this.renderTooltip(guiGraphics, mouseX, mouseY);

	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(GUI_BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight,
				this.imageWidth, this.imageHeight);

		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}

		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.simplegreenhouses.gh_controller_unassembled.label_empty"), 18,
				61, -65485, false);
	}

	@Override
	public void init() {
		super.init();

		button_assemble_greenhouse = Button
				.builder(Component.translatable("gui.simplegreenhouses.gh_controller_unassembled.button_assemble_greenhouse"), e -> {
					this.menu.setAssembled(1);
				}).bounds(this.leftPos + 25, this.topPos + 25, 124, 20).build();

		guistate.put("button:button_assemble_greenhouse", button_assemble_greenhouse);
		this.addRenderableWidget(button_assemble_greenhouse);

	}

}