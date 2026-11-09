package com.bincarunationg.mageknight.profile;

import net.minecraft.entity.player.PlayerEntity;

/**
 * 访问层：全项目唯一的玩家数据容器入口。
 */
public final class Profiles {
	private Profiles() {
	}

	/**
	 * 取玩家的数据容器。首次调用时创建
	 *
	 * @param player 目标玩家（服务端与客户端各有一个对象，各自持有各自的容器）
	 * @return 该玩家的数据容器
	 */
	public static PlayerProfile of(PlayerEntity player) {
		return ((ProfileHolder) player).mk$profile();
	}
}
