package com.bincarunationg.mageknight;

import com.bincarunationg.mageknight.command.TierCommand;
import com.bincarunationg.mageknight.net.ProfileSync;
import com.bincarunationg.mageknight.profile.ProfileHolder;
import com.bincarunationg.mageknight.profile.ProfileLifecycle;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主入口。
 */
public class MageAndKnight implements ModInitializer {
	public static final String MOD_ID = "mage-and-knight";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// 指令
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
			TierCommand.register(dispatcher));

		// 玩家数据迁移
		ProfileLifecycle.register();

		// 玩家数据同步
		ProfileSync.register();

		selfCheck();

		LOGGER.info("Mage and Knight initialized.");
	}

	/**
	 * 启动日志,确认 mixin 是否把接口注入了 PlayerEntity。
	 */
	private static void selfCheck() {
		if (ProfileHolder.class.isAssignableFrom(PlayerEntity.class)) {
			LOGGER.info("PlayerProfile attached to PlayerEntity: OK");
		} else {
			LOGGER.error("PlayerProfile mixin NOT applied to PlayerEntity! profiles will not work.");
		}
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
