package com.koala.simplegreenhouses;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientProxy
{
	public static void addClientListeners(IEventBus modBus)
	{
		modBus.addListener(ClientProxy::onRegisterMenuScreens);
	}
	
	private static void onRegisterMenuScreens(RegisterMenuScreensEvent event)
	{
		event.register(SimpleGreenhouses.GH_MENU.get(), GreenhouseScreen::new);
		event.register(SimpleGreenhouses.GH_UNASSEMBLED_MENU.get(), GreenhouseUnassembledScreen::new);
	}
}
