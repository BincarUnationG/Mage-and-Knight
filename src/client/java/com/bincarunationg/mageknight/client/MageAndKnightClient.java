package com.bincarunationg.mageknight.client;

import com.bincarunationg.mageknight.MageAndKnight;

import net.fabricmc.api.ClientModInitializer;

public class MageAndKnightClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// 接收服务端同步过来的玩家数值
		ClientProfileReceiver.register();
		// 能量条 HUD
		EnergyHudRenderer.register();

		// 客户端日志
		MageAndKnight.LOGGER.info("Client profile receiver registered: OK");
		MageAndKnight.LOGGER.info("Energy HUD registered: OK");
	}
}
