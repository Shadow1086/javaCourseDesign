# 联系人管理系统 - 项目计划

## 📋 项目概述

**项目名称：** 通讯录管理系统（数据库存储与分类查询）

**项目目标：** 实现个人通讯信息的系统化管理，支持多维度分类和快速检索

---

## 🎯 核心功能

### 1. 联系人信息管理
- **新增联系人**：姓名、电话1（必填），电话2、家庭住址、邮箱、备注（可选）
- **编辑联系人**：修改已有联系人的所有信息
- **删除联系人**：支持单个删除和批量删除
- **查看详情**：展示联系人完整信息及关联的分组和标签

### 2. 分类与标签管理
- **分组管理**：创建、编辑、删除分组（如：家人、朋友、同事、客户）
- **标签管理**：创建、编辑、删除标签（如：重要、VIP、生日提醒）
- **多对多关系**：一个联系人可以属于多个分组，拥有多个标签

### 3. 搜索与筛选
- **关键词搜索**：按姓名、电话号模糊查询
- **分组筛选**：查看指定分组下的所有联系人
- **标签筛选**：查看拥有指定标签的所有联系人
- **组合查询**：支持多条件组合筛选

### 4. 数据导入导出
- **导出格式**：vCard格式（.vcf），兼容主流通讯录应用
- **导入功能**：从vCard文件批量导入联系人
- **实现方式**：使用BufferedWriter/BufferedReader处理文件IO

### 5. 界面展示
- **首页列表**：按姓名首字母（A-Z）排序展示
- **分组视图**：按分组分类展示联系人
- **标签视图**：按标签分类展示联系人

---

## 🛠️ 技术栈

| 技术 | 用途 |
|------|------|
| Java 21 | 核心开发语言 |
| JDBC | 数据库连接与操作 |
| SQLite（第一阶段） | 本地化数据库存储 |
| MySQL（后续阶段） | 生产环境数据库 |
| Swing | 图形用户界面 |
| BufferedReader/Writer | 文件导入导出 |

---

## 🗄️ 数据库设计

### 核心表结构

#### 1. contacts（联系人表）
- **字段**：id、name、tele1、tele2、home、email、notes、update_time
- **约束**：name和tele1为NOT NULL
- **SQLite语法**：
```sql
CREATE TABLE IF NOT EXISTS contacts(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(20) NOT NULL,
    tele1 VARCHAR(11) NOT NULL,
    tele2 VARCHAR(11),
    home VARCHAR(100),
    email VARCHAR(30),
    notes VARCHAR(100),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 2. contact_groups（分组表）
- **字段**：id、group_name、update_time
- **SQLite语法**：
```sql
CREATE TABLE IF NOT EXISTS contact_groups(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    group_name VARCHAR(50) NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 3. contact_tags（标签表）
- **字段**：id、name、color、update_time
- **SQLite语法**：
```sql
CREATE TABLE IF NOT EXISTS contact_tags(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    color VARCHAR(20) DEFAULT '#000000',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 4. contact_group_relation（联系人-分组关联表）
- **字段**：contact_id、group_id
- **外键**：关联contacts和contact_groups
- **实现多对多关系**
- **SQLite语法**：
```sql
CREATE TABLE IF NOT EXISTS contact_group_relation(
    contact_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    PRIMARY KEY (contact_id, group_id),
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES contact_groups(id) ON DELETE CASCADE
);
```

#### 5. contact_tag_relation（联系人-标签关联表）
- **字段**：contact_id、tag_id
- **外键**：关联contacts和contact_tags
- **实现多对多关系**
- **SQLite语法**：
```sql
CREATE TABLE IF NOT EXISTS contact_tag_relation(
    contact_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    PRIMARY KEY (contact_id, tag_id),
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES contact_tags(id) ON DELETE CASCADE
);
```

---

## 🤔 技术问题与解决方案

### 问题1：是否需要创建联系人对象？

**答案：需要**

**理由：**
- 遵循面向对象设计原则，将数据库记录映射为Java对象（ORM思想）
- 便于在业务逻辑层传递和处理数据
- 提高代码可读性和可维护性

**建议设计：**
```java
public class Contact {
    private Integer id;
    private String name;
    private String tele1;
    private String tele2;
    private String home;
    private String email;
    private String notes;
    private List<Group> groups;      // 关联的分组列表
    private List<Tag> tags;          // 关联的标签列表
    private Timestamp updateTime;

    // getter/setter/constructor
}
```

---

### 问题2：数据库主键ID是否应该变动？

**答案：不应该变动**

**理由：**
- 主键ID是数据库记录的唯一标识，不应该因为排序或展示需求而修改
- 使用AUTOINCREMENT自增主键，保证唯一性和稳定性
- 排序逻辑应该在查询时通过ORDER BY实现，而不是修改ID

**实现方式：**
```sql
-- 按首字母排序查询
SELECT * FROM contacts ORDER BY name ASC;

-- SQLite中文排序（使用COLLATE）
SELECT * FROM contacts ORDER BY name COLLATE NOCASE ASC;
```

---

## 📅 开发计划

### 第一阶段：数据库设计与搭建 ✅
- [x] 设计5张表结构
- [x] 创建SQLite数据库
- [x] 编写数据库初始化脚本
- [ ] 编写测试数据
- [ ] 验证外键约束和关系

### 第二阶段：JDBC数据访问层
- [x] 封装数据库连接工具类（DBUtil）
- [ ] 实现ContactDAO（联系人数据访问对象）
- [ ] 实现GroupDAO（分组数据访问对象）
- [ ] 实现TagDAO（标签数据访问对象）
- [ ] 实现CRUD基本操作

### 第三阶段：业务逻辑层
- [ ] 实现联系人管理服务
- [ ] 实现搜索和筛选逻辑
- [ ] 实现多对多关系的增删改查
- [ ] 实现事务管理

### 第四阶段：Swing界面开发
- [ ] 设计主界面布局
- [ ] 实现联系人列表展示
- [ ] 实现添加/编辑/删除功能界面
- [ ] 实现搜索和筛选界面

### 第五阶段：导入导出功能
- [ ] 实现vCard格式解析
- [ ] 实现导出功能
- [ ] 实现导入功能

### 第六阶段：测试与优化
- [ ] 功能测试
- [ ] 性能优化
- [ ] 用户体验优化

---

## ⚠️ 注意事项

### 安全性
1. **SQL注入防护**：所有数据库操作必须使用PreparedStatement
2. **数据验证**：对用户输入进行严格验证

### 资源管理
3. **资源关闭**：使用try-with-resources确保Connection、Statement、ResultSet正确关闭
4. **连接池**：后续可考虑使用连接池提高性能

### 数据一致性
5. **事务处理**：涉及多表操作时使用事务保证数据一致性
6. **外键约束**：SQLite需要手动开启外键约束（`PRAGMA foreign_keys = ON`）

### 异常处理
7. **友好提示**：合理捕获和处理SQLException，提供友好的错误提示
8. **日志记录**：记录关键操作和异常信息

### SQLite特性
9. **数据类型**：SQLite动态类型系统，注意类型转换
10. **中文排序**：使用COLLATE NOCASE或自定义排序规则
11. **并发限制**：SQLite不适合高并发写入，适合单用户桌面应用

---

## 📝 开发日志

### 2025-12-09
- ✅ 创建项目结构
- ✅ 添加SQLite JDBC依赖
- ✅ 实现DBUtil数据库连接工具类
- ✅ 创建InitDatabase数据库初始化类
- 🔄 正在调试数据库连接和表创建

---

## 🔗 相关资源

- [SQLite官方文档](https://www.sqlite.org/docs.html)
- [JDBC教程](https://docs.oracle.com/javase/tutorial/jdbc/)
- [Swing教程](https://docs.oracle.com/javase/tutorial/uiswing/)
- [vCard格式规范](https://en.wikipedia.org/wiki/VCard)
