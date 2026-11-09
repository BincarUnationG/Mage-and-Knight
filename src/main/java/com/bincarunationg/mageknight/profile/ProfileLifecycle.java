package com.bincarunationg.mageknight.profile;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

/**
 * 玩家数据的生命周期事件。
 * <p>功能:重生/切换维度时迁移字段，保证不丢失数据</p>
 */
public final class ProfileLifecycle {
	private ProfileLifecycle() {
	}

	public static void register() {
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
			Profiles.of(newPlayer).copyFrom(Profiles.of(oldPlayer)));
	}
}
