package com.bincarunationg.mageknight.client;

import com.bincarunationg.mageknight.profile.ClassType;

/**
 * 客户端侧的数值缓存。
 *
 * <p>它<b>只是服务端数据的镜像</b>，不是权威。客户端任何判定都不许读它做决定
 */
public final class ClientProfileCache {
	private static boolean hasClass;
	private static ClassType classType = ClassType.NONE;
	private static int tier = 9;
	private static int energy;
	private static int maxEnergy;
	private static int selectedSlot;
	private static int slotCount;
	private static int inspiration;

	private ClientProfileCache() {
	}

	/** 由网络接收器{@link ClientProfileReceiver}在客户端线程调用。 */
	public static void update(boolean hasClass, int classOrdinal, int tier,
			int energy, int maxEnergy, int selectedSlot, int slotCount, int inspiration) {
		ClientProfileCache.hasClass = hasClass;
		ClientProfileCache.classType = ordinalToClass(classOrdinal);
		ClientProfileCache.tier = tier;
		ClientProfileCache.energy = energy;
		ClientProfileCache.maxEnergy = maxEnergy;
		ClientProfileCache.selectedSlot = selectedSlot;
		ClientProfileCache.slotCount = slotCount;
		ClientProfileCache.inspiration = inspiration;
	}

	/** 断开连接时清空，避免把上一个存档的数据带入到下一个世界。 */
	public static void clear() {
		update(false, ClassType.NONE.ordinal(), 9, 0, 0, 0, 0, 0);
	}

	private static ClassType ordinalToClass(int ordinal) {
		ClassType[] values = ClassType.values();
		if (ordinal < 0 || ordinal >= values.length) {
			return ClassType.NONE;
		}
		return values[ordinal];
	}

	public static boolean hasClass() {
		return hasClass;
	}

	public static ClassType classType() {
		return classType;
	}

	public static int tier() {
		return tier;
	}

	public static int energy() {
		return energy;
	}

	public static int maxEnergy() {
		return maxEnergy;
	}

	public static int selectedSlot() {
		return selectedSlot;
	}

	public static int slotCount() {
		return slotCount;
	}

	public static int inspiration() {
		return inspiration;
	}
}
