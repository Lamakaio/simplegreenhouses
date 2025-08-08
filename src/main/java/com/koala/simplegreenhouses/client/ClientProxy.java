package com.koala.simplegreenhouses.client;

import com.koala.simplegreenhouses.SimpleGreenhouses;
import com.koala.simplegreenhouses.block_entities.GhGlassBER;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientProxy
{
	public static void addClientListeners(IEventBus modBus)
	{
		modBus.addListener(ClientProxy::onRegisterMenuScreens);
		modBus.addListener(ClientProxy::registerEntityRenderers);
	}
	
	private static void onRegisterMenuScreens(RegisterMenuScreensEvent event)
	{
		event.register(SimpleGreenhouses.GH_MENU.get(), GreenhouseScreen::new);
	}

	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(
				SimpleGreenhouses.GH_GLASS_BLOCK_ENTITY.get(),
				GhGlassBER::new
		);
	}
}
