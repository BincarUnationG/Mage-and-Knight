# 配置

## 当前没有配置文件

本模组**还没有实现配置文件**，`config/` 下不会生成任何东西。所有可调数值都写在源码里，改完需要重新构建。

这样做是刻意的：数值还没定下来（能量上限全是占位数），先做配置文件只会让人误以为它们是稳定接口。等第三批内容定型后再加 `config/mage-and-knight.json`。

下面列出"想调什么该改哪里"。

---

## 玩法数值

**文件**：`src/main/java/com/bincarunationg/mageknight/rule/TierTable.java`

### 能量上限

```java
private static final int[] ENERGY_CAP = {
    800, 600, 450, 350, 280, 220, 180, 150, 120, 100
};
//   0阶  1阶  2阶  3阶  4阶  5阶  6阶  7阶  8阶  9阶
```

⚠ **占位数值**。设定集只给了法术消耗，没给上限，平衡性未做。

### 槽位数量

```java
private static final int[] MAGE_SLOTS   = {6, 6, 5, 5, 4, 4, 3, 3, 2, 1};  // 法师法术位
private static final int[] KNIGHT_SLOTS = {5, 5, 4, 4, 3, 3, 2, 2, 1, 0};  // 骑士肌肉记忆位
```

两组数组都是**下标 = 位阶**（下标 0 是最高阶）。这组来自设定集原表，不是占位数。

> ⚠ 这里的数字是**位阶给的基础槽位数**，不是可用上限。可用槽位 = 基础值 + 加成
> （上限突破 +1、装备铭刻、副职业……），**可能超过 6**。
> 只有 `EnergyRules.slotCount` 能回答"现在能用几个"，`TierTable.slotCount` 只查表给基础值。

### 槽位容量

```java
public static final int BASE_SLOT_COUNT = 6;   // 基础值（0 阶满配），不是上限
public static final int SLOT_CAPACITY = 12;    // 预置槽位数组容量，防加成溢出
```

`SLOT_CAPACITY` 只是物理边界（能存几格、能选中第几格），不参与玩法判定。加成的上限设定里没写死，所以故意留宽。

### 位阶边界

**文件**：`src/main/java/com/bincarunationg/mageknight/profile/PlayerProfile.java`

```java
public static final int TIER_DEFAULT = 9;   // 缺键默认值，绝不能是 0（0 = 最高阶）
public static final int TIER_MIN = 0;
public static final int TIER_MAX = 9;
```

## HUD 外观

**文件**：`src/client/java/com/bincarunationg/mageknight/client/EnergyHudRenderer.java`

### 尺寸

```java
private static final int BAR_W = 81;   // 必须等于 energy_bar_frame.png 的像素宽
private static final int BAR_H = 10;   // 必须等于 energy_bar_frame.png 的像素高
private static final int SIDE = 1;         // 左右竖边框宽度
private static final int LIQUID_TOP = 2;   // 液体窗口上下留白
```

液体区尺寸由代码算：`LIQUID_W = BAR_W − 2`、`LIQUID_H = BAR_H − 4`，也就是液体贴图的尺寸。

> ⚠ 改 `BAR_W` / `BAR_H` **必须同步重导 5 张横条贴图**，否则会被拉伸、窗口对不上。详见 [features/能量条HUD.md](features/能量条HUD.md)。

### 位置

```java
private static final int HEALTH_ROW_FROM_BOTTOM = 39;  // 原版血条行，别改
private static final int HEART_ROW_STEP = 10;          // 原版一排状态图标高度，别改
private static final int TEXT_GAP = 9;                 // 数值文本距条顶的距离
private static final boolean X_ALIGN_RIGHT = false;    // true = 贴屏幕右侧
```

`X_ALIGN_RIGHT = true` 是不与血条对齐、但能彻底避开聊天区域的选项。

### 低能量闪烁

闪烁阈值和节奏目前是写在渲染方法里的字面量：

```java
if (max > 0 && current * 4 < max) {                                  // 阈值：低于 25%
    alpha = (client.player.age / 15) % 2 == 0 ? 1.0F : 0.62F;        // 每 15 tick 切换
}
```

想让阈值更宽松 / 更严，改 `current * 4 < max` 这个不等式（例如 `current * 5 < max` 是低于 20%）。

## 颜色与图案

**颜色全部烤进 PNG 里，代码里没有颜色常量。** 想改外观直接编辑贴图：

| 想改什么 | 改哪个文件 |
|---|---|
| 边框粗细 / 切角 / 白线颜色 | `energy_bar_frame.png`（81×10） |
| 空槽的深浅 | `energy_bar_trough.png`（79×6） |
| 液体配色 | `energy_bar_mage.png` / `energy_bar_knight.png` / `energy_bar_mythic.png`（各 79×6） |
| 职业徽记图案 | `sigil_mage.png` / `sigil_knight.png`（各 9×9） |

路径：`src/main/resources/assets/mage-and-knight/textures/gui/`

编辑要求：

- 保持像素尺寸不变，**必须保存为带 alpha 的 PNG**
- 不要加抗锯齿 / 半透明过渡像素，像素画在 1:1 绘制下半透明会发灰
- 徽记背景必须透明，且用白色 / 灰色单色图（神话阶会乘 `(1.0, 0.92, 0.62)` 染金）

## 文案

`assets/mage-and-knight/lang/zh_cn.json` 与 `en_us.json`，key 前缀：

- `mk.class.*`、`mk.realm.*`：职业名与境界名
- `mk.cmd.tier.*`：指令反馈

## 改完之后

**没有热重载**，改数值或贴图都要重新构建并重启游戏：

```bash
./gradlew build
```

## 未来计划

| 项 | 说明 |
|---|---|
| 配置文件 | `config/mage-and-knight.json`，至少覆盖能量上限表、低能量阈值、HUD 开关与位置 |
| 服务端权威 | 数值一律以服务端为准，配置只影响数值来源，不改变判定位置 |
| 平衡数据 | 能量上限的正式数值待第三批内容（法术消耗）确定后再调 |
