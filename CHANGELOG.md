# 更新日志

本文件记录本模组的版本变更。格式参考 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，版本号遵循语义化版本。

> ⚠ 当前处于开发阶段，`1.0.0` 是首个能跑通的版本号，**不代表功能完整**。

---

## [Unreleased]

### 计划中

- 槽位内容：`learnedAbilities` / `incantations` / `gestures` 的存储与同步
- `Ability` 抽象：能量消耗、冷却、位阶门槛、施法渠道（法师咏唱 / 骑士肌肉记忆）
- 第一个可施放的法术，打通「按键 → 发包 → 服务端校验 → 扣能量 → 生效 → 回同步」整条链路
- 灵感值 HUD
- 装备 / 副职业对能量上限的加成（`EnergyRules.max` 里已留 TODO）
- 配置文件（当前所有可调数值都在源码里）

### 待清理

- `fabric.mod.json` 的 `description`、`homepage` 仍是模板内容
- 模板残留文件：`mixin/ExampleMixin`、`client/mixin/ExampleClientMixin`、`datagen/` 下 4 个 provider

---

## [1.0.0] - 2026-11-09

首个可运行版本。包含第一、二批开发内容。

### 新增

**玩家数据（第一批）**

- `PlayerProfile` 玩家数据容器，通过 Mixin 挂到 `PlayerEntity` 上（`@Unique mk$profile`）
- NBT 持久化：嵌套复合标签 `mk_profile`，7 个字段（职业 / 位阶 / 经验 / 能量 / 灵感 / 选中槽位 / 数据版本）
- 数据版本号 `DATA_VERSION = 2`，为后续结构变更预留
- 重生、换维度时自动拷贝数据（`ServerPlayerEvents.COPY_FROM`）
- 启动自检：Mixin 未生效时日志直接报错，避免后续 `ClassCastException` 难查

**规则层（第二批）**

- `TierTable`：能量上限表、法师法术位表、骑士肌肉记忆位表
- `TierUtil`：境界换算（凡 / 传说 / 神话）、比较用排名（无职业 = `RANK_NONE`）
- `EnergyRules`：能量上限、槽位数量、能量夹紧、槽位可用性校验
- 派生值一律现算，不入存档

**网络同步（第二批）**

- 通道 `mage-and-knight:profile_sync`，服务端 → 客户端全量同步 8 个字段
- 玩家登录时自动发送；任何改动数值的操作结束后重发
- 客户端缓存 `ClientProfileCache` 只作镜像，不参与判定

**能量条 HUD（第二批）**

- 位置紧贴血条正上方，宽度与血条（10 颗心 = 81px）等长
- 贴图渲染：空槽 / 三套液体 / 白框 / 职业徽记，共 7 张 PNG，全部 1:1 像素绘制
- 液体按比例裁剪贴图，不缩放，任意数值精确到 1px
- 职业徽记：法师菱形水晶、骑士白盾十字；神话阶只染金不换形
- 无职业时不显示（普通人不该有力量条）
- 血量 / 吸收超过一排时自动让位，不遮挡原版盔甲、气泡、饱食度
- 低能量（< 25%）整条闪烁，每 15 tick 切换，与帧率无关
- 绘制层抬到 z = +75，压住聊天框底色但保留聊天文字可读

**指令**

- `/tier`：查看职业 / 位阶 / 境界 / 经验 / 能量 / 灵感 / 槽位
- `/tier set <0~9>`、`/tier class <mage|knight|none>`、`/tier exp add <n>`、`/tier energy set <n>`、`/tier sync`（均需 OP 2）
- 权限判断写在 handler 内，权限不足时给出可读提示，而不是让子命令从补全里消失

### 变更

- 职业不再在开局选择：默认 `NONE`（普通人），无位阶、无 HUD 面板

### 修复

- 构建失败：`services.gradle.org` 连不上 → wrapper 改腾讯云镜像
- 构建失败：Loom 1.18+ 要求 Java 25 → 锁 Loom 1.17.21 + 强制 JVM 21
- `runClient` / `runServer` 失败：配置缓存与 Fabric devlaunch 不兼容 → 关闭配置缓存
- 只有 `/tier` 可用、子命令不可见：`.requires(权限)` 在关作弊时隐藏整条分支 → 改为 handler 内判断
- 能量条被聊天框底色压暗：聊天框底色画在 z = +50 而 HUD 在 z = 0 → 整条抬到 z = +75
- 能量条左右没有白框闭合：逐行边框只画了液体行两端 → 改为整行铺框色后挖中间
- 徽记黑描边融进空槽：`#12151A` 与空槽 `#0E1114` 亮度几乎相同 → 描边改为 `#79838F`

### 已知问题

- 能量上限是占位数值，未做平衡
- 9px 徽记在 10px 条上会盖住内侧白线，白线中间有 7px 缺口
- 能量条无法完全避开聊天框区域（这是底部居中位置的固有问题）
