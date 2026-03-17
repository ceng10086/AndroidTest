# 数据模型与持久化（Room）

## 1. 表结构

### 1.1 `tasks`

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| `id` | Long | PK, auto | 主键 |
| `title` | String | NOT NULL | 标题 |
| `note` | String | NULL | 备注 |
| `is_done` | Boolean | NOT NULL | 完成状态 |
| `created_at` | Long | NOT NULL | 创建时间（epoch millis） |
| `updated_at` | Long | NOT NULL | 更新时间（epoch millis） |
| `priority` | Int | NOT NULL(默认0) | 迭代 3：0低/1中/2高 |

> 迭代 1 可先不落库；迭代 2 引入 Room 后按上述字段落地（priority 可先留默认值）。

## 2. DAO 设计

建议接口：
- `LiveData<List<TaskEntity>> observeAll()`（按更新时间倒序）
- `LiveData<TaskEntity> observeById(long id)`
- `long insert(TaskEntity e)`
- `int update(TaskEntity e)`
- `int delete(TaskEntity e)`
- `int deleteById(long id)`

## 3. 索引与排序

- 为 `updated_at` 加索引（列表默认按更新时间倒序）。

## 4. 导入/导出格式（迭代 3）

### 4.1 JSON 结构

```json
{
  "version": 1,
  "exportedAt": 1710000000000,
  "tasks": [
    {
      "id": 1,
      "title": "买牛奶",
      "note": "低脂",
      "isDone": false,
      "createdAt": 1710000000000,
      "updatedAt": 1710000000000,
      "priority": 0
    }
  ]
}
```

### 4.2 导入策略

- 默认：按 `title + createdAt` 去重（或直接全量覆盖，二选一，迭代 3 定稿）。
- 若发现 `version` 不兼容：提示失败，不写入数据库。

## 5. 数据迁移

- Room `version` 从 1 开始。
- 迭代 3 若新增字段/索引：提供 Migration（保持升级不丢数据）。
