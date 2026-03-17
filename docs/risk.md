# 风险清单与应对

## 1. 技术风险

- Room 引入（Java + annotationProcessor）配置不当导致编译失败
  - 应对：Iteration 2 专门安排“依赖与 Room 最小可用”任务，先跑通编译再做功能。

- targetSdk 36 行为变化（存储/权限/后台限制）影响导入导出
  - 应对：使用 Storage Access Framework（不申请传统存储权限）。

- CI 仪器测试不稳定
  - 应对：先用 unit + lint 做主门禁；connected tests 放在可选 workflow。

## 2. 产品风险

- MVP 不持久化导致用户误以为数据会保存
  - 应对：Iteration 1 末尾在空态或提示中明确“后续版本会保存”；或在 Iteration 1 加入 onSaveInstanceState 简单保活（可选）。

- 导入策略（覆盖 vs 合并）争议
  - 应对：Iteration 3 之前在文档中明确策略，并在 UI 提示。

## 3. 范围风险

- 容易不断加功能（提醒、标签、多列表）导致延期
  - 应对：严格按 `spec.md` 与三迭代范围；额外需求进入 `backlog.md`。
