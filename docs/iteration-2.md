# Iteration 2（持久化与分层：Room + MVVM）

## 1. 目标

把 MVP 变成“可长期使用”的离线待办：
- 引入 Room，数据重启不丢
- 支持编辑任务
- 引入 ViewModel + Repository 分层

## 2. 范围

### 2.1 必做
- Room 数据库：`AppDatabase`、`TaskEntity`、`TaskDao`
- Repository：统一 CRUD
- ViewModel：列表页与编辑页
- 编辑能力：新增/编辑复用同一套界面

### 2.2 可选
- 删除撤销（Snackbar Undo）
- 列表按更新时间排序

## 3. 任务拆分（工程级）

### 3.1 Gradle 依赖
- 在 `gradle/libs.versions.toml` 增加：
  - `recyclerview`
  - `lifecycle-viewmodel`、`lifecycle-livedata`
  - `room-runtime`、`room-compiler`
- 在 `app/build.gradle.kts` 增加依赖：
  - `implementation(...)`
  - `annotationProcessor(...)`（Room compiler）

### 3.2 数据层
- 新建 `data/db/TaskEntity.java`（字段见 `data-model.md`）
- 新建 `data/db/TaskDao.java`
- 新建 `data/db/AppDatabase.java`
- 新建 `data/repo/TaskRepository.java`
- 线程：写操作走单线程 Executor

### 3.3 UI/交互
- 列表数据源改为观察 Room（LiveData）：
  - `TaskListViewModel.observeTasks()`
- 新增 `EditTaskActivity`（或 Fragment）：
  - 保存时调用 repository insert/update
- 列表项点击进入编辑

### 3.4 质量与可维护性
- 增加基础单元测试（只在 CI 跑）：
  - Repository 的纯逻辑测试（不依赖 Android）
  - 字段校验（标题非空、长度限制）
- 静态检查：lint 必须通过

## 4. 交付物（验收清单）

- 任务可新增/编辑/删除/完成切换。
- 杀进程/重启 App 后数据仍存在。
- UI 不直接访问 Room（至少通过 Repository）。
- GitHub CI 通过：`assembleDebug`、`lintDebug`、`testDebugUnitTest`。

## 5. 变更文件清单（预期）

- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `app/src/main/java/.../data/**`
- `app/src/main/java/.../ui/viewmodel/**`
- `app/src/main/java/.../EditTaskActivity.java`
- `app/src/main/AndroidManifest.xml`（注册新 Activity）
- 布局与字符串资源
