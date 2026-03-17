# 总计划（3 次迭代交付）

## 0. 约束与边界

- 该 App **完全离线运行**，不调用任何外部 API。
- 不在本地执行测试（包括 `./gradlew test`）；所有测试与静态检查由 GitHub CI 执行。
- 代码基于现有模板：Java + XML + AndroidX（当前 `minSdk 24`，`targetSdk 36`）。

## 1. 项目目标（可交付）

在 3 次迭代内交付一个可发布的离线待办清单 App：
- 迭代 1：可用的 UI + 基本新增/完成/删除（MVP）
- 迭代 2：引入 Room 持久化 + 编辑能力 + 基本架构分层
- 迭代 3：导入/导出 + 完整验收 + CI 质量门禁完善

## 2. 里程碑与节奏（建议）

- Iteration 1（MVP 可用）：1 周
- Iteration 2（持久化与结构化）：1~2 周
- Iteration 3（可迁移数据与收尾）：1 周

> 实际周期取决于投入时间；本计划按“每次迭代可独立交付”组织。

## 3. 工作流（与 GitHub 远程仓库协作）

- 分支：
  - `main`：稳定可发布
  - `feature/*`：每个需求一个分支
- PR 规则：
  - 必须通过 GitHub Actions CI
  - 至少 1 次自检（可用 Checklist 模板）
- 版本：
  - Iteration 1：`0.1.0`
  - Iteration 2：`0.2.0`
  - Iteration 3：`1.0.0`

## 4. 依赖与工程改造（跨迭代）

- 新增依赖（建议在 Iteration 2 统一引入）：
  - RecyclerView
  - Lifecycle ViewModel / LiveData
  - Room runtime + compiler（Java: `annotationProcessor`）
  - Gson（迭代 3）
- 资源与规范：
  - 字符串放 `strings.xml`
  - 颜色/主题沿用模板，不新增自定义颜色体系
- 离线约束：
  - Manifest 不引入 `INTERNET`
  - （可选）CI 检查 merged manifest

## 5. 验收标准（总）

- 核心功能：新增/编辑/完成/删除/持久化/导入导出。
- 稳定性：无明显崩溃；旋转屏幕/前后台切换不丢当前编辑内容。
- 质量门禁：CI 必须绿（lint + unit tests + assemble）。

## 6. 文档与交付物

- 规格：`spec.md`
- 架构：`architecture.md`
- 数据：`data-model.md`
- CI：`ci.md`
- DoD：`acceptance.md`
- 迭代拆分：`iteration-1.md`/`iteration-2.md`/`iteration-3.md`
