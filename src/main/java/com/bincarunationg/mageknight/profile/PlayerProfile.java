package com.bincarunationg.mageknight.profile;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

/**
 * 玩家数据容器
 *
 * <p><b>这是一个纯数据对象</b>：不持有 {@code PlayerEntity} 引用，也不写任何玩法规则。
 * 派生值（境界、能量上限、槽位数量……）全部放在 {@code rule} 包里用静态方法算，
 * 需要读装备时再传入 player 参数。
 *
 * <p>加字段的标准动作只有三步：
 * <ol>
 *     <li>在这里加一个字段（并想清楚它是持久 / 运行时 / 派生）；</li>
 *     <li>{@link #toNbt()} 与 {@link #fromNbt} 各加一行；</li>
 *     <li>需要给客户端看的话，再动同步包（{@code net.ProfileSync}）。</li>
 * </ol>
 */
public final class PlayerProfile {
	/** 数据版本。*/
	public static final int DATA_VERSION = 2;

	/** 位阶的默认值。*/
	public static final int TIER_DEFAULT = 9;
	public static final int TIER_MIN = 0;
	public static final int TIER_MAX = 9;

	/**
	 * 基础槽位数
	 * <p>实际数量见 {@code rule.EnergyRules#slotCount}。</p>
	 */
	public static final int BASE_SLOT_COUNT = 6;

	/** 预置槽位数组的容量。 */
	public static final int SLOT_CAPACITY = 16;

	// ─ NBT 键名 ─
	private static final String KEY_VERSION = "mk_ver";
	private static final String KEY_CLASS = "mk_class";
	private static final String KEY_TIER = "mk_tier";
	private static final String KEY_EXP = "mk_exp";
	private static final String KEY_ENERGY = "mk_energy";
	private static final String KEY_INSPIRATION = "mk_inspiration";
	private static final String KEY_SELECTED_SLOT = "mk_selected_slot";

	private ClassType classType = ClassType.NONE;
	private int tier = TIER_DEFAULT;
	private long exp;
	private int energy;
	private int inspiration;
	private int selectedSlot;

	// ─ 读取操作 ─

	public ClassType classType() {
		return classType;
	}

	/** 是否已获得职业。无职业时 {@link #tier()} 没有意义。 */
	public boolean hasClass() {
		return classType != ClassType.NONE;
	}

	public int tier() {
		return tier;
	}

	public long exp() {
		return exp;
	}

	/** 当前能量值。上限见 {@code EnergyRules.max}。 */
	public int energy() {
		return energy;
	}

	public int inspiration() {
		return inspiration;
	}

	public int selectedSlot() {
		return selectedSlot;
	}

	// ─ 写入操作（仅服务端） ─

	public void setClass(ClassType type) {
		this.classType = (type == null) ? ClassType.NONE : type;
	}

	/** 位阶会被夹在 0~9，避免任何来源写出越界值。 */
	public void setTier(int value) {
		this.tier = clampTier(value);
	}

	public void addExp(long amount) {
		if (amount <= 0) {
			return;
		}
		this.exp += amount;
	}

	/** 能量设置；下限固定为 0。上限见 {@code EnergyRules.max}  */
	public void setEnergy(int value) {
		this.energy = Math.max(0, value);
	}

	/** 消耗能量。不足时不扣、返回 false。*/
	public boolean spendEnergy(int amount) {
		if (amount <= 0) {
			return true;
		}
		if (this.energy < amount) {
			return false;
		}
		this.energy -= amount;
		return true;
	}

	public void addEnergy(int amount) {
		this.energy = Math.max(0, this.energy + amount);
	}

	public void setInspiration(int value) {
		this.inspiration = Math.max(0, value);
	}

	public void addInspiration(int amount) {
		this.inspiration = Math.max(0, this.inspiration + amount);
	}

	/** 选中槽位：夹在数组容量内；「这个位阶能不能选这一格」由上层按位阶 + 加成校验。 */
	public void selectSlot(int index) {
		this.selectedSlot = Math.max(0, Math.min(SLOT_CAPACITY - 1, index));
	}

	// ─ 序列化 ─

	/** 全部字段的唯一写入入口。 */
	public NbtCompound toNbt() {
		NbtCompound nbt = new NbtCompound();
		nbt.putInt(KEY_VERSION, DATA_VERSION);
		nbt.putString(KEY_CLASS, classType.name());
		nbt.putInt(KEY_TIER, tier);
		nbt.putLong(KEY_EXP, exp);
		nbt.putInt(KEY_ENERGY, energy);
		nbt.putInt(KEY_INSPIRATION, inspiration);
		nbt.putInt(KEY_SELECTED_SLOT, selectedSlot);
		return nbt;
	}

	/** 全部字段的唯一读取入口。 */
	public void fromNbt(NbtCompound nbt) {
		if (nbt.contains(KEY_CLASS, NbtElement.STRING_TYPE)) {
			String raw = nbt.getString(KEY_CLASS);
			try {
				classType = ClassType.valueOf(raw);
			} catch (IllegalArgumentException e) {
				classType = ClassType.NONE;
			}
		}

		tier = nbt.contains(KEY_TIER) ? clampTier(nbt.getInt(KEY_TIER)) : TIER_DEFAULT;
		exp = nbt.contains(KEY_EXP) ? nbt.getLong(KEY_EXP) : 0L;
		energy = nbt.contains(KEY_ENERGY) ? Math.max(0, nbt.getInt(KEY_ENERGY)) : 0;
		inspiration = nbt.contains(KEY_INSPIRATION) ? Math.max(0, nbt.getInt(KEY_INSPIRATION)) : 0;
		selectedSlot = nbt.contains(KEY_SELECTED_SLOT)
			? Math.max(0, Math.min(SLOT_CAPACITY - 1, nbt.getInt(KEY_SELECTED_SLOT)))
			: 0;
	}

	/** 重生 / 换维度时，把旧对象的数据搬到新对象上。 */
	public void copyFrom(PlayerProfile other) {
		if (other == null) {
			return;
		}
		this.classType = other.classType;
		this.tier = other.tier;
		this.exp = other.exp;
		this.energy = other.energy;
		this.inspiration = other.inspiration;
		this.selectedSlot = other.selectedSlot;
	}

	/** 把位阶夹回合法区间；每次写入或读到旧值后都应该调用一次。 */
	private static int clampTier(int value) {
		return Math.max(TIER_MIN, Math.min(TIER_MAX, value));
	}
}
