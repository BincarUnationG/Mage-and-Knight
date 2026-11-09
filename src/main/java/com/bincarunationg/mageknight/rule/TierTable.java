package com.bincarunationg.mageknight.rule;

import com.bincarunationg.mageknight.profile.ClassType;
import com.bincarunationg.mageknight.profile.PlayerProfile;

/**
 * 位阶数值表
 * <p>索引约定：数组下标 = 位阶编号。</p>
 */
public final class TierTable {
	/** 能量上限表。*/
	private static final int[] ENERGY_CAP = {
		800, // 0 阶
		600, // 1 阶
		450, // 2 阶
		350, // 3 阶
		280, // 4 阶
		220, // 5 阶
		180, // 6 阶
		150, // 7 阶
		120, // 8 阶
		100  // 9 阶
	};

	/** 法术位基础数量。*/
	private static final int[] MAGE_SLOTS = {6, 6, 5, 5, 4, 4, 3, 3, 2, 1};

	/** 肌肉记忆槽位基础数量。*/
	private static final int[] KNIGHT_SLOTS = {5, 5, 4, 4, 3, 3, 2, 2, 1, 0};

	private TierTable() {
	}

	/** 能量上限。无职业返回 0。 */
	public static int energyCap(ClassType type, int tier) {
		if (type == null || type == ClassType.NONE) {
			return 0;
		}
		return ENERGY_CAP[clampTier(tier)];
	}

	/**
	 * 基础槽位数量
	 * <p>实际槽位数量见 {@code rule.EnergyRules#slotCount}</p>
	 */
	public static int slotCount(ClassType type, int tier) {
		if (type == null || type == ClassType.NONE) {
			return 0;
		}
		int[] table = (type == ClassType.MAGE) ? MAGE_SLOTS : KNIGHT_SLOTS;
		return table[clampTier(tier)];
	}

	/** 把位阶夹回合法区间；每次写入或读到旧值后都应该调用一次。 */
	private static int clampTier(int tier) {
		return Math.max(PlayerProfile.TIER_MIN, Math.min(PlayerProfile.TIER_MAX, tier));
	}
}
