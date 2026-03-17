# Iteration 3（可迁移数据与收尾：导入/导出 + 质量门禁）

## 1. 目标

把离线待办做到“可备份、可维护、可发布”：
- 导出/导入 JSON（本地文件）
- 完善错误处理与空态
- 完善 CI（质量门禁、可选的仪器测试）

## 2. 范围

### 2.1 必做
- 导出：把 DB 中任务写入 JSON 文件
- 导入：从 JSON 读取并写入 DB（支持覆盖或合并，二选一并文档化）
- 设置入口：主页面菜单（导入/导出）
- 数据版本：导出文件含 `version`

### 2.2 可选
- 优先级字段与排序
- “关于”页（展示版本号）
- CI 中增加一个“禁止网络权限”检查

## 3. 任务拆分（工程级）

### 3.1 依赖
- 引入 Gson（或 Moshi，但 Java 下 Gson 更直接）。
- 视需要引入 `androidx.documentfile:documentfile`（SAF 辅助）。

### 3.2 文件读写（SAF）
- 导出：`ACTION_CREATE_DOCUMENT` 获取 `Uri`，写入 OutputStream。
- 导入：`ACTION_OPEN_DOCUMENT` 获取 `Uri`，读取 InputStream。
- 错误处理：
  - JSON 解析失败提示
  - 版本不兼容提示

### 3.3 质量门禁（GitHub CI）
- 必跑：
  - `./gradlew assembleDebug`
  - `./gradlew lintDebug`
  - `./gradlew testDebugUnitTest`
- 可选（资源允许时）：
  - `./gradlew connectedDebugAndroidTest`（需要 Emulator）
- 增加检查（可选）：
  - 若 merged manifest 出现 `android.permission.INTERNET` 则 CI fail

## 4. 交付物（验收清单）

- 可导出 JSON 并在同机导入恢复。
- 导入/导出过程中出现错误时有明确提示，且不会写坏 DB。
- 代码结构稳定：主要职责在 data/repo、viewmodel、ui 分层。
- CI 全绿，达到 `acceptance.md` 的 DoD。

## 5. 变更文件清单（预期）

- `gradle/libs.versions.toml`（Gson 等）
- `app/build.gradle.kts`
- `app/src/main/java/.../data/repo/`（导入导出实现）
- `app/src/main/java/.../ui/`（菜单、交互）
- `docs/*` 更新（导入/导出策略、版本字段）
