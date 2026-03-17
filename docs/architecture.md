# 技术架构设计（Java + XML）

## 1. 总体原则

- **离线优先**：所有数据来源于本地数据库（Room）。
- **分层清晰**：UI / Domain / Data 层职责明确，便于迭代与测试。
- **最小依赖**：仅引入必要 Jetpack 组件。

## 2. 技术栈

- 语言：Java 11（工程已配置）
- UI：XML + Material Components
- 列表：RecyclerView
- 架构：MVVM（Activity/Fragment + ViewModel + LiveData）
- 持久化：Room
- JSON：Gson（用于导入/导出）

## 3. 包结构建议（`com.example.androidtest`）

- `ui/`
  - `MainActivity`（列表页容器）
  - `EditTaskActivity`（新增/编辑）
  - `adapter/TaskAdapter`
- `ui/viewmodel/`
  - `TaskListViewModel`
  - `EditTaskViewModel`
- `data/`
  - `db/AppDatabase`
  - `db/TaskEntity`
  - `db/TaskDao`
  - `repo/TaskRepository`
- `domain/`
  - `model/Task`
  - `usecase/`（可选，迭代 2/3 再引入）

> 说明：若想更轻量，也可以不引入 `domain/`，但建议至少把 Room 实体与 UI 模型隔离。

## 4. 组件职责

### 4.1 UI 层
- 只处理渲染与用户交互。
- 不直接操作 Room；通过 ViewModel 调用 Repository。

### 4.2 ViewModel 层
- 暴露 `LiveData<List<Task>>`、`LiveData<UiState>`。
- 使用后台线程（Executor）或 Room 自带支持执行写操作。

### 4.3 Repository 层
- 统一提供 CRUD：新增、更新、删除、查询。
- （迭代 3）提供导入/导出：
  - `exportToJson(OutputStream)`
  - `importFromJson(InputStream)`

## 5. 线程与响应

- Room 查询使用 `LiveData`/`Flow`（Java 侧建议 LiveData）。
- 写操作用单线程 Executor，避免阻塞主线程。

## 6. 版本与兼容

- `minSdk = 24`，因此可使用现代 Jetpack。
- `targetSdk = 36`：注意前台行为/权限策略遵循新版本规范。

## 7. 禁用网络（工程性约束）

- 不增加 `INTERNET` 权限。
- 不引入需要网络权限才能工作的 SDK。
- （可选）CI 增加一个检查：若合并后 Manifest 出现 `INTERNET` 则失败。
