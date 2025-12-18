# 通讯录管理系统

## 项目简介

这是一个基于 Java Swing 和 SQLite 的桌面通讯录管理系统，实现了联系人的增删改查、分组管理、标签管理以及数据导入导出等功能。

## 功能特性

### 核心功能

- ✅ **联系人管理**：添加、编辑、删除、查看联系人信息
- ✅ **分组管理**：创建分组，为联系人分配多个分组
- ✅ **标签管理**：创建标签，为联系人添加多个标签
- ✅ **搜索功能**：按姓名、电话号码快速查找联系人
- ✅ **筛选功能**：按分组或标签筛选联系人
- ✅ **数据导入导出**：支持 vCard 格式的导入和导出

### 技术特点

- 采用 MVC 分层架构设计
- SQLite 本地数据库存储
- 支持多对多关系（联系人-分组、联系人-标签）
- Swing 图形用户界面
- Maven 项目管理

## 技术栈

| 技术     | 版本       | 说明     |
|--------|----------|--------|
| Java   | 21       | 开发语言   |
| SQLite | 3.45.0.0 | 本地数据库  |
| Swing  | JDK 内置   | GUI 框架 |
| JDBC   | JDK 内置   | 数据库连接  |
| Maven  | 3.x      | 项目构建工具 |
| JUnit  | 4.13.2   | 单元测试框架 |

## 项目结构

```
javaCurriculumDesign/
├── src/
│   ├── main/
│   │   ├── java/com/cn/hbu/edu/htliang/
│   │   │   ├── util/              # 工具类
│   │   │   │   └── DBUtil.java    # 数据库连接工具
│   │   │   ├── entityPojo/        # 实体类
│   │   │   │   ├── Contacts.java  # 联系人实体
│   │   │   │   ├── Groups.java    # 分组实体
│   │   │   │   └── Tags.java      # 标签实体
│   │   │   ├── dao/               # 数据访问层
│   │   │   │   ├── ContactsDao.java
│   │   │   │   └── ContactsDaoImpl.java
│   │   │   ├── service/           # 业务逻辑层
│   │   │   │   ├── ContactService.java
│   │   │   │   └── ContactServiceImpl.java
│   │   │   ├── view/              # 视图层
│   │   │   │   └── MainGUI.java   # 主界面
│   │   │   └── sQL/               # SQL 脚本
│   │   │       └── InitTables.sql # 数据库初始化脚本
│   │   └── resources/             # 资源文件
│   └── test/                      # 测试代码
│       └── java/com/cn/hbu/edu/htliang/
│           └── service/
│               └── ContactServiceTest.java
├── doc/                           # 项目文档
│   ├── planned.md                 # 项目计划
│   ├── study.md                   # 学习计划
│   ├── api-documentation.md       # API 文档
│   ├── database-design.md         # 数据库设计文档
│   ├── user-manual.md             # 用户手册
│   ├── developer-guide.md         # 开发者指南
│   ├── test-report.md             # 测试报告
│   └── project-summary.md         # 项目总结
├── contacts.sqlite                # SQLite 数据库文件
├── pom.xml                        # Maven 配置文件
└── README.md                      # 项目说明文档

```

## 快速开始

### 环境要求

- JDK 21 或更高版本
- Maven 3.6 或更高版本
- 操作系统：Windows / macOS / Linux

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd javaCurriculumDesign
   ```

2. **编译项目**
   ```bash
   mvn clean compile
   ```

3. **运行程序**
   ```bash
   mvn exec:java -Dexec.mainClass="com.cn.hbu.edu.htliang.view.MainGUI"
   ```

   或者使用 IDE（如 IntelliJ IDEA 或 Eclipse）直接运行 `MainGUI.java`

### 数据库初始化

首次运行时，程序会自动创建 `contacts.sqlite` 数据库文件并初始化表结构。如需手动初始化，可执行：

```bash
sqlite3 contacts.sqlite < src/main/java/com/cn/hbu/edu/htliang/sQL/InitTables.sql
```

## 使用说明

### 主界面功能

- **添加联系人**：点击工具栏"添加"按钮，填写联系人信息
- **搜索联系人**：在搜索框输入姓名或电话号码，点击"搜索"
- **刷新列表**：点击"刷新"按钮重新加载联系人列表
- **分组管理**：点击"分组"按钮管理联系人分组
- **导入联系人**：点击"导入"按钮，选择 vCard 文件导入
- **导出联系人**：点击"导出"按钮，将联系人导出为 vCard 格式

### 联系人操作

- **查看详情**：双击联系人条目查看详细信息
- **编辑联系人**：右键点击联系人，选择"编辑"
- **删除联系人**：右键点击联系人，选择"删除"
- **批量删除**：选中多个联系人，右键选择"批量删除"

## 数据库设计

### 表结构

| 表名               | 说明        | 主要字段                                       |
|------------------|-----------|--------------------------------------------|
| `contacts`       | 联系人表      | id, name, tele1, tele2, home, email, notes |
| `groups`         | 分组表       | id, group_name, group_notes                |
| `tags`           | 标签表       | id, tag_name, tag_color, tag_notes         |
| `contacts_group` | 联系人-分组关联表 | contacts_id, group_id                      |
| `tag_contacts`   | 联系人-标签关联表 | contacts_id, tag_id                        |

详细的数据库设计请参考 [数据库设计文档](doc/database-design.md)

## 开发文档

- [API 文档](doc/api-documentation.md) - 接口说明和使用示例
- [数据库设计文档](doc/database-design.md) - 数据库表结构和关系设计
- [用户使用手册](doc/user-manual.md) - 详细的功能使用说明
- [开发者指南](doc/developer-guide.md) - 代码结构和开发规范
- [测试报告](doc/test-report.md) - 测试用例和测试结果
- [项目总结](doc/project-summary.md) - 项目开发总结和心得

## 测试

### 运行测试

```bash
mvn test
```

### 手动测试

运行测试类：

```bash
mvn exec:java -Dexec.mainClass="com.cn.hbu.edu.htliang.service.ContactServiceTest"
```

## 开发计划

项目采用分阶段开发模式：

- [x] **阶段 1**：数据库设计与实体类创建
- [x] **阶段 2**：DAO 层基础实现
- [x] **阶段 3**：Service 层基础实现
- [x] **阶段 4**：GUI 界面框架搭建
- [ ] **阶段 5**：功能完善与优化
- [ ] **阶段 6**：测试与文档完善

详细的开发计划请参考 [项目计划文档](doc/planned.md)

## 常见问题

### 1. 数据库文件在哪里？

数据库文件 `contacts.sqlite` 位于项目根目录下，首次运行时自动创建。

### 2. 如何修改数据库路径？

在 `DBUtil.java` 中修改 `DB_URL` 常量。

### 3. 程序无法启动怎么办？

- 检查 JDK 版本是否为 21 或更高
- 确认 Maven 依赖已正确下载
- 查看控制台错误信息

### 4. 如何添加新功能？

请参考 [开发者指南](doc/developer-guide.md) 了解代码结构和开发规范。

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 许可证

本项目仅用于课程设计学习目的。

## 联系方式

- 项目作者：htliang
- 学校：河北大学（HBU）

## 致谢

感谢所有为本项目提供帮助和建议的老师和同学！

---

**最后更新时间**：2025-12-17
