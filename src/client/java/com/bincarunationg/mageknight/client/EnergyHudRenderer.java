package com.bincarunationg.mageknight.client;

import com.bincarunationg.mageknight.MageAndKnight;
import com.bincarunationg.mageknight.profile.ClassType;
import com.bincarunationg.mageknight.profile.Realm;
import com.bincarunationg.mageknight.rule.TierUtil;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * 能量条 HUD。
 *
 * <p>尺寸：宽 81 ,边框上下各 2px（外 1px 黑线 + 内 1px 白线），左右各 1px 竖边，四角各切 3px。</p>
 *
 * <p>贴图尺寸和这里的常量是绑定的：改 {@link #BAR_W}/{@link #BAR_H} 必须同步重导 PNG。</p>
 */
public final class EnergyHudRenderer {
	// ─ 贴图 ─
	private static final Identifier TEX_FRAME = MageAndKnight.id("textures/gui/energy_bar_frame.png");
	private static final Identifier TEX_TROUGH = MageAndKnight.id("textures/gui/energy_bar_trough.png");
	private static final Identifier TEX_LIQUID_MAGE = MageAndKnight.id("textures/gui/energy_bar_mage.png");
	private static final Identifier TEX_LIQUID_KNIGHT = MageAndKnight.id("textures/gui/energy_bar_knight.png");
	private static final Identifier TEX_LIQUID_MYTHIC = MageAndKnight.id("textures/gui/energy_bar_mythic.png");
	private static final Identifier TEX_SIGIL_MAGE = MageAndKnight.id("textures/gui/sigil_mage.png");
	private static final Identifier TEX_SIGIL_KNIGHT = MageAndKnight.id("textures/gui/sigil_knight.png");

	// ─ 尺寸 ─
	private static final int BAR_W = 81;
	private static final int BAR_H = 10;
	/** 左右竖边框宽度。 */
	private static final int SIDE = 1;
	/** 液体窗口上下各留 2px。 */
	private static final int LIQUID_TOP = 2;
	/** 液体窗口尺寸 = 液体贴图尺寸：79×6。 */
	private static final int LIQUID_W = BAR_W - SIDE * 2;
	private static final int LIQUID_H = BAR_H - LIQUID_TOP * 2;
	/** 徽记贴图尺寸。 */
	private static final int SIGIL_SIZE = 9;

	// ─ 位置 ─
	/** 血条行顶边距屏幕底部：scaledHeight − 39。 */
	private static final int HEALTH_ROW_FROM_BOTTOM = 39;
	/** 一排状态图标的高度：心形 9px + 1px 缝。 */
	private static final int HEART_ROW_STEP = 10;
	/** 数值基线在条顶上方的距离（字体 8px 高 + 1px 间隙）。 */
	private static final int TEXT_GAP = 9;
	/** false = 居中（与血条对齐）；true = 靠屏幕右侧。 */
	private static final boolean X_ALIGN_RIGHT = false;

	private static final int LABEL_EMPTY = 0xFF9AA1A9;
	private static final int LABEL_NORMAL = 0xFFFFFFFF;

	private EnergyHudRenderer() {
	}

	public static void register() {
		HudRenderCallback.EVENT.register(EnergyHudRenderer::onHudRender);
	}

	private static void onHudRender(DrawContext context, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.options.hudHidden || !ClientProfileCache.hasClass()) {
			return;
		}

		int max = Math.max(ClientProfileCache.maxEnergy(), 0);
		int current = Math.max(0, Math.min(ClientProfileCache.energy(), max));

		ClassType classType = ClientProfileCache.classType();
		boolean mythic = TierUtil.realmOf(classType, ClientProfileCache.tier()) == Realm.MYTH;
		Identifier liquid = mythic ? TEX_LIQUID_MYTHIC
				: (classType == ClassType.KNIGHT ? TEX_LIQUID_KNIGHT : TEX_LIQUID_MAGE);

		// 低能量（<25%）整条闪烁特效
		float alpha = 1.0F;
		if (max > 0 && current * 4 < max) {
			alpha = (client.player.age / 15) % 2 == 0 ? 1.0F : 0.62F;
		}

		int screenWidth = context.getScaledWindowWidth();
		int x = X_ALIGN_RIGHT ? screenWidth - 4 - BAR_W : screenWidth / 2 - 91;

		// 位置的原始推导
		float vital = client.player.getMaxHealth() + client.player.getAbsorptionAmount();
		int heartRows = Math.max(1, (int) Math.ceil(vital / 20.0F));
		int rowSpacing = Math.max(10 - (heartRows - 2), 3);
		int topRowFromBottom = HEALTH_ROW_FROM_BOTTOM + (heartRows - 1) * rowSpacing + HEART_ROW_STEP;

		int y = context.getScaledWindowHeight() - (topRowFromBottom + BAR_H);

		// 聊天框遮挡渲染调整
		context.getMatrices().push();
		context.getMatrices().translate(0.0F, 0.0F, 75.0F);

		// 液体宽度
		int filled = max <= 0 ? 0 : (int) ((long) LIQUID_W * current / max);
		int liquidX = x + SIDE;
		int liquidY = y + LIQUID_TOP;

		context.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

		// 空槽
		context.drawTexture(TEX_TROUGH, liquidX, liquidY, 0.0F, 0.0F,
			LIQUID_W, LIQUID_H, LIQUID_W, LIQUID_H);

		// ② 液体：从贴图左边裁出 filled 那么宽，1:1 不缩放
		if (filled > 0) {
			context.drawTexture(liquid, liquidX, liquidY, 0.0F, 0.0F,
				filled, LIQUID_H, LIQUID_W, LIQUID_H);
		}

		context.drawTexture(TEX_FRAME, x, y, 0.0F, 0.0F, BAR_W, BAR_H, BAR_W, BAR_H);

		// 职业徽记
		drawSigil(context, classType, mythic, x + BAR_W / 2 - SIGIL_SIZE / 2, y + (BAR_H - SIGIL_SIZE) / 2 + 1, alpha);

		context.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
		context.drawTexture(TEX_FRAME, x, y, 0.0F, 0.0F, BAR_W, 1, BAR_W, BAR_H);
		context.drawTexture(TEX_FRAME, x, y + BAR_H - 1, 0.0F, BAR_H - 1, BAR_W, 1, BAR_W, BAR_H);
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		// 数值
		TextRenderer textRenderer = client.textRenderer;
		String label = current + " / " + max;
		int labelX = x + BAR_W / 2 - textRenderer.getWidth(label) / 2;
		context.drawText(textRenderer, label, labelX, y - TEXT_GAP,
			current <= 0 ? LABEL_EMPTY : LABEL_NORMAL, true);

		context.getMatrices().pop();
	}

	private static void drawSigil(DrawContext context, ClassType classType, boolean mythic,
			int x, int y, float alpha) {
		Identifier texture = classType == ClassType.KNIGHT ? TEX_SIGIL_KNIGHT : TEX_SIGIL_MAGE;
		context.setShaderColor(1.0F, mythic ? 0.92F : 1.0F, mythic ? 0.62F : 1.0F, alpha);
		context.drawTexture(texture, x, y, 0.0F, 0.0F, SIGIL_SIZE, SIGIL_SIZE, SIGIL_SIZE, SIGIL_SIZE);
	}
}
