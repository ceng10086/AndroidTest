# GitHub CI 方案（不在本地跑测试）

## 1. 目标

- 每个 PR 自动构建与检查，确保 `main` 始终可用。
- 不依赖任何外部 API（CI 仅拉依赖与构建，不运行网络功能）。

## 2. 推荐的 GitHub Actions 工作流

建议新增（或调整）以下工作流：

### 2.1 `android-ci.yml`（必选）

触发：
- `pull_request`
- `push` 到 `main`

步骤（建议顺序）：
1. Checkout
2. 设置 JDK（与工程一致，建议 17 或按 AGP 要求；代码 source/target 仍是 11）
3. Gradle 缓存
4. `./gradlew --no-daemon assembleDebug`
5. `./gradlew --no-daemon lintDebug`
6. `./gradlew --no-daemon testDebugUnitTest`

产物：
- 上传 lint 报告与单测报告（可选）

### 2.2 `android-instrumented.yml`（可选，迭代 3 再上）

触发：
- 手动（`workflow_dispatch`）或夜间定时

步骤：
- 启动 Android Emulator
- 运行 `./gradlew connectedDebugAndroidTest`

> 注意：仪器测试成本高，且可能引入不稳定因素；建议先把 unit + lint 作为主门禁。

## 3. “禁止网络权限”检查（可选增强）

目标：防止误加 `INTERNET` 权限。

思路（任选其一）：
- 方案 A：CI 中解析合并后的 Manifest（`processDebugMainManifest` 后的产物），grep `android.permission.INTERNET`。
- 方案 B：写一个简单 Gradle task，在 CI 中执行，发现则 fail。

## 4. 约定

- 开发时可以本地 `assembleDebug`/运行 App，但**不在本地执行测试**。
- 所有测试结果以 GitHub CI 为准。
