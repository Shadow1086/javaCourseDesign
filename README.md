# 通讯录管理系统

## 项目概览

本项目为基于 Java Swing 与 SQLite 的桌面通讯录管理系统，面向课程设计与桌面应用实践。系统提供联系人信息的增删改查、分组与标签管理，并支持 vCard 数据导入与导出。

## 功能说明

### 联系人与组织管理

- 联系人管理：新增、编辑、删除与查看联系人详情
- 分组管理：创建分组并为联系人分配多个分组
- 标签管理：创建标签并为联系人分配多个标签
- 搜索与筛选：按姓名或电话搜索，按分组或标签筛选

### 数据能力

- vCard 导入与导出
- SQLite 本地持久化存储
- 联系人-分组、联系人-标签多对多关系支持

## 技术栈

| 技术 | 版本 | 说明 |
| --- | --- | --- |
| Java | 21 | 开发语言 |
| SQLite | 3.45.0.0 | 本地数据库 |
| Swing | JDK 内置 | 图形界面 |
| JDBC | JDK 内置 | 数据访问 |
| Maven | 3.x | 构建与依赖管理 |
| JUnit | 4.13.2 | 单元测试 |

## 目录结构

```
javaCurriculumDesign/
├── src/
│   ├── main/
│   │   ├── java/com/cn/hbu/edu/htliang/
│   │   │   ├── util/              # 工具类
│   │   │   │   └── DBUtil.java    # 数据库连接工具
│   │   │   ├── entityPojo/        # 实体类
│   │   │   │   ├── Contacts.java
│   │   │   │   ├── Groups.java
│   │   │   │   └── Tags.java
│   │   │   ├── dao/               # 数据访问层
│   │   │   │   ├── ContactsDao.java
│   │   │   │   └── ContactsDaoImpl.java
│   │   │   ├── service/           # 业务逻辑层
│   │   │   │   ├── ContactService.java
│   │   │   │   └── ContactServiceImpl.java
│   │   │   ├── view/              # 视图层
│   │   │   │   └── MainGUI.java
│   │   │   └── sQL/               # SQL 脚本
│   │   │       └── InitTables.sql
│   │   └── resources/
│   └── test/
│       └── java/com/cn/hbu/edu/htliang/
│           └── service/
│               └── ContactServiceTest.java
├── doc/                           # 项目文档
│   ├── planned.md
│   ├── study.md
│   ├── api-documentation.md
│   ├── database-design.md
│   ├── user-manual.md
│   ├── developer-guide.md
│   ├── test-report.md
│   └── project-summary.md
├── contacts.sqlite                # SQLite 数据库文件
├── pom.xml
└── README.md
```

## 快速开始

### 环境要求

- JDK 21 或更高版本
- Maven 3.6 或更高版本
- Windows / macOS / Linux

### 获取与运行

1. 克隆项目
   ```bash
   git clone <repository-url>
   cd javaCurriculumDesign
   ```

2. 编译项目
   ```bash
   mvn clean compile
   ```

3. 运行程序
   ```bash
   mvn exec:java -Dexec.mainClass="com.cn.hbu.edu.htliang.view.MainGUI"
   ```

亦可在 IDE 中直接运行 `MainGUI.java`。

### 数据库初始化

首次启动将自动创建 `contacts.sqlite` 并初始化表结构。如需手动初始化，请执行：

```bash
sqlite3 contacts.sqlite < src/main/java/com/cn/hbu/edu/htliang/sQL/InitTables.sql
```

## 使用说明

### 主界面功能

- 添加联系人：工具栏“添加”
- 搜索联系人：搜索框输入姓名或电话，点击“搜索”
- 刷新列表：点击“刷新”
- 分组管理：点击“分组”
- 导入联系人：点击“导入”并选择 vCard 文件
- 导出联系人：点击“导出”生成 vCard 文件

### 联系人操作

- 查看详情：双击联系人条目
- 编辑联系人：右键 -> “编辑”
- 删除联系人：右键 -> “删除”
- 批量删除：多选后右键 -> “批量删除”

## 数据库设计

| 表名 | 说明 | 主要字段 |
| --- | --- | --- |
| contacts | 联系人表 | id, name, tele1, tele2, home, email, notes |
| groups | 分组表 | id, group_name, group_notes |
| tags | 标签表 | id, tag_name, tag_color, tag_notes |
| contacts_group | 联系人-分组关联表 | contacts_id, group_id |
| tag_contacts | 联系人-标签关联表 | contacts_id, tag_id |

详情参见 `doc/database-design.md`。

## 项目文档

- `doc/api-documentation.md`：接口说明
- `doc/database-design.md`：数据库设计
- `doc/user-manual.md`：用户手册
- `doc/developer-guide.md`：开发者指南
- `doc/test-report.md`：测试报告
- `doc/project-summary.md`：项目总结
- `doc/planned.md`：项目计划

## 测试

执行测试：

```bash
mvn test
```

手动运行测试类：

```bash
mvn exec:java -Dexec.mainClass="com.cn.hbu.edu.htliang.service.ContactServiceTest"
```

## 开发计划

- [x] 阶段 1：数据库设计与实体类创建
- [x] 阶段 2：DAO 层基础实现
- [x] 阶段 3：Service 层基础实现
- [x] 阶段 4：GUI 界面框架搭建
- [ ] 阶段 5：功能完善与优化
- [ ] 阶段 6：测试与文档完善

详情参见 `doc/planned.md`。

## 常见问题

1. 数据库文件位置？
   - `contacts.sqlite` 位于项目根目录，首次运行自动生成。

2. 如何修改数据库路径？
   - 在 `DBUtil.java` 中调整 `DB_URL` 常量。

3. 程序无法启动？
   - 确认 JDK 版本不低于 21
   - 检查 Maven 依赖是否完整
   - 查看控制台错误信息

4. 如何扩展功能？
   - 参考 `doc/developer-guide.md`。

## 贡献

欢迎提交 Issue 或 Pull Request：

1. Fork 本项目
2. 新建分支：`git checkout -b feature/YourFeature`
3. 提交变更：`git commit -m "Add YourFeature"`
4. 推送分支：`git push origin feature/YourFeature`
5. 发起 Pull Request

## 许可

本项目仅用于课程设计与学习目的。

## 联系方式

- 作者：htliang
- 学校：河北大学（HBU）

## 致谢

感谢所有提供帮助与建议的老师和同学。

---

最后更新时间：2025-12-17
