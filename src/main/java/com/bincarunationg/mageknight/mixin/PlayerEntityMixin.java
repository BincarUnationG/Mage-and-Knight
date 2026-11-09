package com.bincarunationg.mageknight.mixin;

import com.bincarunationg.mageknight.profile.PlayerProfile;
import com.bincarunationg.mageknight.profile.ProfileHolder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 把玩家数据容器作为字段挂到原版 {@code PlayerEntity} 上。
 *
 * <p>原版方法：
 * <ul>
 *     <li>{@code writeCustomDataToNbt} —— 原版存玩家时调用</li>
 *     <li>{@code readCustomDataFromNbt} —— 原版读玩家时调用</li>
 * </ul>
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ProfileHolder {
	/** 唯一根字段。 */
	@Unique
	private PlayerProfile mk$profile;

	@Override
	public PlayerProfile mk$profile() {
		if (mk$profile == null) {
			mk$profile = new PlayerProfile();
		}
		return mk$profile;
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void mk$writeProfile(NbtCompound nbt, CallbackInfo ci) {
		// 避免给所有人塞一个空容器
		if (mk$profile != null) {
			nbt.put("mk_profile", mk$profile.toNbt());
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void mk$readProfile(NbtCompound nbt, CallbackInfo ci) {
		if (nbt.contains("mk_profile")) {
			mk$profile().fromNbt(nbt.getCompound("mk_profile"));
		}
	}
}
