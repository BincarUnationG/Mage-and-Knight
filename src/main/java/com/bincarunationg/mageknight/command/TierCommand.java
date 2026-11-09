package com.bincarunationg.mageknight.command;

import java.util.Locale;

import com.bincarunationg.mageknight.net.ProfileSync;
import com.bincarunationg.mageknight.profile.ClassType;
import com.bincarunationg.mageknight.profile.PlayerProfile;
import com.bincarunationg.mageknight.profile.Profiles;
import com.bincarunationg.mageknight.profile.Realm;
import com.bincarunationg.mageknight.rule.EnergyRules;
import com.bincarunationg.mageknight.rule.TierUtil;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * 调试 / 验证用指令：{@code /tier}。
 *
 * <pre>
 * /tier                          查看职业 / 位阶 / 境界 / 经验 / 能量 / 灵感 / 槽位
 * /tier set &lt;0-9&gt;                设位阶　　　（需要 2 级权限）
 * /tier class &lt;mage|knight|none&gt;  设职业　　　（需要 2 级权限）
 * /tier exp add &lt;n&gt;              加经验　　　（需要 2 级权限）
 * /tier energy set &lt;n&gt;           设当前能量　（需要 2 级权限）
 * /tier sync                     手动重发同步包（需要 2 级权限）
 * </pre>
 */
public final class TierCommand {
	/** 需要的最低权限等级。 */
	private static final int REQUIRED_PERMISSION = 2;

	private TierCommand() {
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("tier")
			.executes(ctx -> show(ctx.getSource()))

			// /tier set <0-9>
			.then(CommandManager.literal("set")
				.then(CommandManager.argument("value",
						IntegerArgumentType.integer(PlayerProfile.TIER_MIN, PlayerProfile.TIER_MAX))
					.executes(ctx -> setTier(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "value")))))

			// /tier class <mage|knight|none>
			.then(CommandManager.literal("class")
				.then(CommandManager.literal("mage")
					.executes(ctx -> setClass(ctx.getSource(), ClassType.MAGE)))
				.then(CommandManager.literal("knight")
					.executes(ctx -> setClass(ctx.getSource(), ClassType.KNIGHT)))
				.then(CommandManager.literal("none")
					.executes(ctx -> setClass(ctx.getSource(), ClassType.NONE))))

			// /tier exp add <n>
			.then(CommandManager.literal("exp")
				.then(CommandManager.literal("add")
					.then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
						.executes(ctx -> addExp(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount"))))))

			// /tier energy set <n>
			.then(CommandManager.literal("energy")
				.then(CommandManager.literal("set")
					.then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100000))
						.executes(ctx -> setEnergy(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "value"))))))

			// /tier sync
			.then(CommandManager.literal("sync")
				.executes(ctx -> sync(ctx.getSource()))));
	}

	// ─ 子命令 ─

	private static int show(ServerCommandSource source) {
		ServerPlayerEntity player = requirePlayer(source);
		if (player == null) {
			return 0;
		}

		PlayerProfile profile = Profiles.of(player);

		// 无职业
		if (!profile.hasClass()) {
			source.sendFeedback(() -> Text.translatable("mk.cmd.tier.noclass"), false);
			return 1;
		}

		Text head = Text.translatable(
			"mk.cmd.tier.line",
			Text.translatable(classNameKey(profile.classType())),
			profile.tier(),
			Text.translatable(realmNameKey(TierUtil.realmOf(profile))),
			profile.exp());
		source.sendFeedback(() -> head, false);

		Text energyLine = Text.translatable(
			"mk.cmd.tier.energy",
			profile.energy(),
			EnergyRules.max(player, profile),
			profile.inspiration(),
			profile.selectedSlot() + 1,
			EnergyRules.slotCount(profile));
		source.sendFeedback(() -> energyLine, false);
		return 1;
	}

	private static int setTier(ServerCommandSource source, int value) {
		if (!requirePermission(source)) {
			return 0;
		}
		ServerPlayerEntity player = requirePlayer(source);
		if (player == null) {
			return 0;
		}

		PlayerProfile profile = Profiles.of(player);
		profile.setTier(value);

		profile.setEnergy(EnergyRules.clampEnergy(player, profile, profile.energy()));
		ProfileSync.send(player);

		source.sendFeedback(() -> Text.translatable("mk.cmd.tier.set", profile.tier()), true);
		return 1;
	}

	private static int setClass(ServerCommandSource source, ClassType type) {
		if (!requirePermission(source)) {
			return 0;
		}
		ServerPlayerEntity player = requirePlayer(source);
		if (player == null) {
			return 0;
		}

		PlayerProfile profile = Profiles.of(player);
		profile.setClass(type);
		profile.setEnergy(EnergyRules.clampEnergy(player, profile, profile.energy()));
		ProfileSync.send(player);

		source.sendFeedback(() -> Text.translatable(
			"mk.cmd.tier.class", Text.translatable(classNameKey(type))), true);
		return 1;
	}

	private static int addExp(ServerCommandSource source, int amount) {
		if (!requirePermission(source)) {
			return 0;
		}
		ServerPlayerEntity player = requirePlayer(source);
		if (player == null) {
			return 0;
		}

		PlayerProfile profile = Profiles.of(player);
		profile.addExp(amount);
		ProfileSync.send(player);

		source.sendFeedback(() -> Text.translatable("mk.cmd.tier.exp", amount, profile.exp()), true);
		return 1;
	}

	private static int setEnergy(ServerCommandSource source, int value) {
		if (!requirePermission(source)) {
			return 0;
		}
		ServerPlayerEntity player = requirePlayer(source);
		if (player == null) {
			return 0;
		}

		PlayerProfile profile = Profiles.of(player);
		profile.setEnergy(EnergyRules.clampEnergy(player, profile, value));
		ProfileSync.send(player);

		source.sendFeedback(() -> Text.translatable("mk.cmd.tier.energy_set", profile.energy()), true);
		return 1;
	}

	private static int sync(ServerCommandSource source) {
		if (!requirePermission(source)) {
			return 0;
		}
		ServerPlayerEntity player = requirePlayer(source);
		if (player == null) {
			return 0;
		}
		ProfileSync.send(player);
		source.sendFeedback(() -> Text.translatable("mk.cmd.tier.sync"), false);
		return 1;
	}

	// ─ 工具 ─

	private static boolean requirePermission(ServerCommandSource source) {
		if (source.hasPermissionLevel(REQUIRED_PERMISSION)) {
			return true;
		}
		source.sendError(Text.translatable("mk.cmd.tier.no_permission", REQUIRED_PERMISSION));
		return false;
	}

	private static ServerPlayerEntity requirePlayer(ServerCommandSource source) {
		ServerPlayerEntity player = source.getPlayer();
		if (player == null) {
			source.sendError(Text.translatable("mk.cmd.tier.only_player"));
		}
		return player;
	}

	private static String classNameKey(ClassType type) {
		return "mk.class." + type.name().toLowerCase(Locale.ROOT);
	}

	private static String realmNameKey(Realm realm) {
		return "mk.realm." + realm.name().toLowerCase(Locale.ROOT);
	}
}
