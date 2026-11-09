package com.bincarunationg.mageknight.client;

import com.bincarunationg.mageknight.net.ProfileSync;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * 客户端网络接收器：把服务端发来的数值写进 {@link ClientProfileCache}。
 *
 * <p>注意事项：
 * <ol>
 *     <li>在<b>网络线程</b>上把所有字段读完，再切回客户端线程写缓存</li>
 *     <li>读取顺序必须与 {@link ProfileSync#send} 的写入顺序严格一致。</li>
 * </ol>
 */
public final class ClientProfileReceiver {
	private ClientProfileReceiver() {
	}

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(ProfileSync.CHANNEL,
				(client,
				 handler,
				 buf,
				 responseSender) -> {
			// ─ 读取顺序 ─
			boolean hasClass = buf.readBoolean();
			int classOrdinal = buf.readVarInt();
			int tier = buf.readVarInt();
			int energy = buf.readVarInt();
			int maxEnergy = buf.readVarInt();
			int selectedSlot = buf.readVarInt();
			int slotCount = buf.readVarInt();
			int inspiration = buf.readVarInt();

			client.execute(() -> ClientProfileCache.update(
					hasClass,
					classOrdinal,
					tier,
					energy,
					maxEnergy,
					selectedSlot,
					slotCount,
					inspiration)
			);
		});
	}
}
