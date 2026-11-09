# Mage and Knight

> 一个 Minecraft 模组：把玩家分成**法师**与**骑士**两条成长路线，用位阶（0~9 阶）衡量强弱，用能量条承载施法 / 武艺的消耗。

| 项 | 值 |
|---|---|
| Minecraft | 1.20.1 |
| 加载器 | Fabric Loader 0.20.1+ / Fabric API |
| 模组版本 | 1.0.0（**开发中，尚未发布**） |
| 模组 ID | `mage-and-knight` |
| 许可证 | MIT（见 [LICENSE](LICENSE)） |

---

## 这是什么

玩家开局是**普通人**：没有职业、没有位阶、屏幕上没有力量条。获得职业后才有位阶，位阶决定能量上限与可用槽位数量；位阶跨过阈值时境界从「凡」升到「传说」再到「神话」。

**已经能跑通的**：数据存档、位阶 / 职业指令、服务端→客户端同步、能量条 HUD。
**还没有的**：任何一个真正能施放的法术或战技——第三批开发中。

---

## 当前进度

| 批次 | 内容 | 状态 |
|---|---|---|
| 第一批 | 玩家数据容器、NBT 存档、重生拷贝、`/tier` 指令 | ✅ 完成 |
| 第二批 | 规则层（数值表 / 派生）、网络同步、能量条 HUD（贴图版） | ✅ 完成 |
| 第三批 | 槽位内容、法术与武艺、第一个可施放技能 | ⏳ 未开始 |

---

## 安装

1. 装 **Fabric Loader 0.20.1+**（Minecraft 1.20.1）
2. 把 [Fabric API](https://modrinth.com/mod/fabric-api) 和本模组的 jar 一起放进 `mods/`
3. 单人游戏需在创建世界时勾选「允许作弊」，否则位阶指令用不了

## 构建

```bash
cd Mage-and-Knight
./gradlew build          # 产物 build/libs/mage-and-knight-1.0.0.jar
```

**构建需要 JDK 21**（Fabric Loom 要求，版本固定在 `gradle/gradle-daemon-jvm.properties`）。

```bash
./gradlew runClient      # 起开发客户端
./gradlew runServer      # 起开发服务端
./gradlew genSources     # 需要读原版源码时用
```

### 本地构建环境（国内网络）

- Gradle 分发包走**腾讯云镜像**（`gradle/wrapper/gradle-wrapper.properties`）：官方 `services.gradle.org` 国内直连会 connect timeout。
- Maven 依赖走**阿里云镜像**（`settings.gradle`、`build.gradle`），官方源保留兜底；Fabric 官方仓库 `maven.fabricmc.net` 没有可用镜像，仍需直连。
- `org.gradle.configuration-cache=false` **不能改**：Fabric devlaunch 在初始化脚本里用 `Gradle.addListener`，开配置缓存会让 `runClient` / `runServer` 直接失败。

版本约束、GUI 缩放、与原版 HUD 的关系详见 [docs/compatibility.md](docs/compatibility.md)。

---

## 文档

```
docs/
├── features/
│   ├── 位阶与职业.md      职业、位阶、境界、数值表
│   ├── 能量条HUD.md       屏幕上的能量条：位置、尺寸、贴图、渲染顺序
│   └── 指令.md            /tier 及全部子命令
├── configuration.md      可调整的数值在哪里（当前无配置文件）
└── compatibility.md      版本要求、GUI 缩放、与原版 HUD 的关系
```

- [CHANGELOG.md](CHANGELOG.md)：版本变更记录
- `../vision/`：设计设定集与教学文档（不在模组包内，供开发时查阅）
  - `../vision/设定集/`：世界设定、施法机制、成长曲线
  - `../vision/方案/`：架构方案、源码逐行讲解、HUD 设计稿
  - `../vision/开发进度.md`：阶段性开发记录

---

## 快速验证

进游戏（需开作弊）按顺序敲：

```
/tier class mage        → HUD 出现一条空蓝条
/tier set 4             → 能量上限变成 280
/tier energy set 200    → 条填到约 71%
/tier set 0             → 神话阶：液体变金色、徽记染金
/tier class none        → 条消失（普通人没有力量条）
```

启动日志里应出现：

```
PlayerProfile attached to PlayerEntity: OK
Client profile receiver registered: OK
Energy HUD registered: OK
```

---

## 已知限制

- 能量上限目前是**占位数值**，平衡性未做（见 [docs/configuration.md](docs/configuration.md)）
- 能量条位置必然与聊天框的纵向范围重叠（已处理遮挡，但无法完全避开）
- `fabric.mod.json` 的 description / homepage 仍是模板内容，发布前需替换

## License

MIT，全文见 [LICENSE](LICENSE)。代码与贴图（`assets/mage-and-knight/textures/gui/` 下的全部 PNG）适用同一个许可证。

简单说：可以自由使用、修改、再分发甚至商用，但**必须保留版权声明和许可证副本**。
