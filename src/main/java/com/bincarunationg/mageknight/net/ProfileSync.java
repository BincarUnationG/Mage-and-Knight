package com.bincarunationg.mageknight.net;

import com.bincarunationg.mageknight.MageAndKnight;
import com.bincarunationg.mageknight.profile.PlayerProfile;
import com.bincarunationg.mageknight.profile.Profiles;
import com.bincarunationg.mageknight.rule.EnergyRules;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * 玩家数据同步。
 * <p>写入顺序必须与 {@code ClientProfileReceiver} 的读取顺序完全一致
 */
public final class ProfileSync {
	/** 通道标识：{@code mage-and-knight:profile_sync}。 */
	public static final Identifier CHANNEL = new Identifier(MageAndKnight.MOD_ID, "profile_sync");

	private ProfileSync() {
	}

	/** 同步的注册 */
	public static void register() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> send(handler.player));
	}

	/** 把当前玩家的数据全量发给他的客户端。 */
	public static void send(ServerPlayerEntity player) {
		PlayerProfile profile = Profiles.of(player);

		PacketByteBuf buf = PacketByteBufs.create();
		// ─ 写入顺序 ─
		buf.writeBoolean(profile.hasClass());
		buf.writeVarInt(profile.classType().ordinal());
		buf.writeVarInt(profile.tier());
		buf.writeVarInt(profile.energy());
		buf.writeVarInt(EnergyRules.max(player, profile));
		buf.writeVarInt(profile.selectedSlot());
		buf.writeVarInt(EnergyRules.slotCount(profile));
		buf.writeVarInt(profile.inspiration());

		ServerPlayNetworking.send(player, CHANNEL, buf);
	}
}
