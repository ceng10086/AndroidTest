# Iteration 1（MVP 可用：离线待办基础）

## 1. 目标

交付一个“能用”的离线待办：
- 任务列表展示
- 新增任务
- 标记完成/未完成
- 删除任务

> Iteration 1 允许 **不做持久化**（先用内存列表），以最短路径跑通 UX。

## 2. 范围

### 2.1 必做
- 主页面：RecyclerView 列表 + 空态 + FAB 新建
- 新建：对话框输入标题（必填）、备注（可选）
- 完成状态切换：CheckBox
- 删除：列表项菜单/滑动删除（二选一）

### 2.2 不做
- Room 持久化（放到 Iteration 2）
- 导入导出（Iteration 3）
- 多页面复杂导航

## 3. 任务拆分（工程级）

### 3.1 UI 与资源
- 新建布局：
  - `activity_main.xml`：加入 RecyclerView / FAB / 空态
  - 列表项布局 `item_task.xml`
- 文案与无障碍：
  - 所有文本放 `strings.xml`
  - FAB/菜单加 `contentDescription`

### 3.2 代码结构
- 增加 `Task` UI 模型（内存数据结构）
- 增加 `TaskAdapter`（RecyclerView Adapter）
- 在 `MainActivity` 中维护一个 `List<Task>`：
  - 新建：插入列表顶部
  - 切换完成：更新对应项
  - 删除：移除对应项

### 3.3 质量
- 单元测试：可选（不强制，Iteration 2 起再补）
- 基本 lint：确保无明显警告

## 4. 交付物（验收清单）

- 可以新增、展示、完成、删除待办。
- 旋转屏幕不崩溃（是否保留列表：可先不要求，Iteration 2 用持久化兜底）。
- 不新增 `INTERNET` 权限。
- GitHub CI 通过（至少 `assembleDebug` + `lintDebug`）。

## 5. 变更文件清单（预期）

- `app/src/main/java/.../MainActivity.java`
- `app/src/main/java/.../adapter/TaskAdapter.java`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/layout/item_task.xml`
- `app/src/main/res/values/strings.xml`
- （可选）`docs/*` 更新
