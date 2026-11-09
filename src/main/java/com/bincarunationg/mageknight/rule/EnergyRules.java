package com.bincarunationg.mageknight.rule;

import com.bincarunationg.mageknight.profile.PlayerProfile;

import net.minecraft.entity.player.PlayerEntity;

/**
 * 能量相关的派生逻辑。
 */
public final class EnergyRules {
	private EnergyRules() {
	}

	/**
	 * 能量上限。
	 * 当前阶段 = 位阶基础值；
	 * TODO 装备 / 副职业 /状态效果的加成后续在这里叠加。
	 */
	public static int max(PlayerEntity player, PlayerProfile profile) {
		if (!profile.hasClass()) {
			return 0;
		}

		int cap = TierTable.energyCap(profile.classType(), profile.tier());

		// TODO 第二批之后：这里叠加装备加成，例如
		//   主手 / 铠甲 / 副手 的 attribute-like 加成
		//   副职业加成（仪式术 / 列阵 等）
		//   状态效果加成
		// 一旦加了，记得同时建立缓存与失效点

		return cap;
	}

	/**
	 * 当前可用槽位数量。
	 * <p>加成目前为 0</p>
	 * <p>上限 {@link PlayerProfile#SLOT_CAPACITY} </p>
	 */
	public static int slotCount(PlayerProfile profile) {
		int base = TierTable.slotCount(profile.classType(), profile.tier());

		int bonus = 0;
		// TODO 在这里叠加槽位加成
		//   凡尘上限突破
		//   法杖 / 法典铭刻、副职业加成

		return Math.min(base + bonus, PlayerProfile.SLOT_CAPACITY);
	}

	/** 把能量夹回合法区间；每次写入或读到旧值后都应该调用一次。 */
	public static int clampEnergy(PlayerEntity player, PlayerProfile profile, int value) {
		int cap = max(player, profile);
		return Math.max(0, Math.min(cap, value));
	}

	/** 判断当前选中槽位是否在当前位阶允许的范围内。 */
	public static boolean isSlotUsable(PlayerProfile profile, int index) {
		return index >= 0 && index < slotCount(profile);
	}
}
