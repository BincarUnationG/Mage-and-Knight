# Mage-and-Knight

## Setup

For setup instructions, please see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up) related to the IDE that you are using.

## 本地构建环境（国内网络）

构建前请确认：

- **JDK 21**（本机 `C:\Program Files\Java\jdk-21`）。Loom 1.18+ 要求构建 JVM 为 **Java 25**，
  否则报 `Dependency requires at least JVM runtime version 25`，因此本项目的 `loom_version`
  锁定在 **1.17.21**（1.17 系列最后一个支持 Java 21 的版本）。升级到 JDK 25 后可以改回 `1.19.3`。
- Gradle 分发包走**腾讯云镜像**（见 `gradle/wrapper/gradle-wrapper.properties`），官方
  `services.gradle.org` 在国内直连会 connect timeout。
- Maven 依赖走**阿里云镜像**（见 `settings.gradle`、`build.gradle`），官方源保留做兜底；
  Fabric 官方仓库 `maven.fabricmc.net` 没有可用镜像，仍需直连。

只需 `./gradlew build`，产物在 `build/libs/`。

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
