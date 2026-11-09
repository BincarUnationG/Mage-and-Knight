package com.bincarunationg.mageknight.rule;

import com.bincarunationg.mageknight.profile.ClassType;
import com.bincarunationg.mageknight.profile.PlayerProfile;
import com.bincarunationg.mageknight.profile.Realm;

/**
 * 位阶相关的逻辑。
 */
public final class TierUtil {
	/**
	 * 无职业者参与位阶比较时使用的值。
	 * <p>这个常量只允许出现在本类里。别处出现字面量 10 都算违规——更不许把它写进存档。</p>
	 */
	public static final int RANK_NONE = 10;

	private TierUtil() {
	}

	/** 境界推导。*/
	public static Realm realmOf(ClassType type, int tier) {
		if (type == null || type == ClassType.NONE || tier >= 5) {
			return Realm.MORTAL;
		}
		return tier >= 3 ? Realm.LEGEND : Realm.MYTH;
	}

	public static Realm realmOf(PlayerProfile profile) {
		return realmOf(profile.classType(), profile.tier());
	}


	/**
	 * 比较位阶用的强弱值。
	 * <p>不能直接读 {@code tier()}。</p>
	 */
	public static int rankOf(ClassType type, int tier) {
		return (type == null || type == ClassType.NONE) ? RANK_NONE : tier;
	}

	public static int rankOf(PlayerProfile profile) {
		return rankOf(profile.classType(), profile.tier());
	}
}
