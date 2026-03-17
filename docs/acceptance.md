# 验收标准与 Definition of Done（DoD）

## 1. 迭代级 DoD（每次迭代）

- 功能按对应迭代文档完成，且自测通过（手动操作）。
- 不引入外部 API 调用；Manifest 无 `INTERNET` 权限。
- 关键文案进入 `strings.xml`。
- GitHub CI 全绿（至少 build + lint；从迭代 2 开始加入 unit test）。
- PR 描述包含：变更点、风险点、回滚方式。

## 2. 发布级 DoD（Iteration 3 / 1.0.0）

- 核心闭环：新增/编辑/完成/删除/持久化/导入导出。
- 异常处理：导入导出失败有明确提示，不破坏已有数据。
- 兼容性：`minSdk 24` 真机/模拟器可正常运行。
- 可访问性：主要按钮有 contentDescription；大字体下页面可用。
- CI 质量门禁：
  - `assembleDebug` ✅
  - `lintDebug` ✅
  - `testDebugUnitTest` ✅
  - （可选）`connectedDebugAndroidTest` ✅

## 3. 验收用例（手动）

- 新增一个标题为“测试任务”的待办，返回列表可见。
- 切换完成状态后，状态持久化（重启 App 仍保持）。
- 编辑标题/备注后列表刷新正确。
- 删除后任务消失（若支持撤销，撤销后恢复）。
- 导出后生成 JSON 文件；清空数据（或卸载重装）后导入可恢复。
