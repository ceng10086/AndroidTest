# AndroidTest 文档索引

本目录包含该项目从 0 到 1 制作一个**完全离线运行**（不调用外部 API、默认不申请 `INTERNET` 权限）的 Android App 所需的完整规划与规格说明。

## 快速阅读顺序

1. `spec.md`：产品功能与 UX 规格（做什么、怎么用）
2. `architecture.md`：技术架构与代码组织（怎么做）
3. `data-model.md`：数据模型与持久化设计（存什么）
4. `plan.md`：总计划与里程碑（三次迭代怎么交付）
5. `iteration-1.md` / `iteration-2.md` / `iteration-3.md`：每次迭代的可交付物、任务拆分与验收标准
6. `ci.md`：GitHub Actions CI 设计（不在本地跑测试，依赖 CI）
7. `acceptance.md`：Definition of Done 与质量门禁
8. `risk.md`：风险清单与应对
9. `backlog.md`：超出三迭代范围的候选需求

## 关键约束（摘要）

- **离线运行**：不依赖任何外部服务；不实现登录/同步/网络请求。
- **不使用外部 API**：不集成第三方在线接口；不申请 `INTERNET` 权限（除非未来明确变更需求）。
- **测试策略**：不在本地执行测试；所有测试/静态检查通过 GitHub CI 完成。

> 注：依赖库（AndroidX、Room 等）来自 Maven 仓库属于构建依赖，不属于运行期外部 API 调用。
