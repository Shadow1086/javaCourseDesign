## 方案A: 最小知识点清单(快速上手,边学边做)

### 核心知识点(只学必须的)


创建表
### 【核心知识点清单】

1. __主键约束（PRIMARY KEY）__
2. __自增长（AUTO_INCREMENT）__
3. __外键约束（FOREIGN KEY）__
4. __索引（INDEX）__
5. __唯一约束（UNIQUE KEY）__
6. __非空约束（NOT NULL）__
7. __默认值（DEFAULT）__
8. __时间戳自动更新（ON UPDATE CURRENT_TIMESTAMP）__
9. __存储引擎（ENGINE）__
10. __字符集（CHARSET）__
11. __数据类型选择__
12. __级联操作（ON DELETE/ON UPDATE）__
13. __表注释（COMMENT）__


#### 1. JDBC 连接三件套(30分钟)

- __加载驱动__: `Class.forName("com.mysql.cj.jdbc.Driver")`
- __获取连接__: `DriverManager.getConnection(url, user, password)`
- __关闭资源__: try-with-resources 自动关闭

#### 2. SQL 增删改查(1小时)

只需掌握 5 个语句:

- `INSERT INTO 表名 (字段) VALUES (值)` - 添加
- `DELETE FROM 表名 WHERE 条件` - 删除
- `UPDATE 表名 SET 字段=值 WHERE 条件` - 修改
- `SELECT * FROM 表名 WHERE 条件` - 查询
- `SELECT * FROM 表名 WHERE 字段 LIKE '%关键词%'` - 模糊搜索

#### 3. JDBC 执行 SQL(1小时)

- __增删改__: `PreparedStatement` + `executeUpdate()`
- __查询__: `PreparedStatement` + `executeQuery()` + `ResultSet`

#### 5. 实践步骤(边做边查)

1. __Day 1-2__: 写一个测试类,连接数据库 + 插入一条数据
2. __Day 3__: 封装 DAO 类(ContactDAO),实现增删改查方法
3. __Day 4-5__: 用 Swing 做界面,调用 DAO 方法
4. __Day 6__: 实现分组查询和模糊搜索
5. __Day 7__: vCard 导出(查文档边做边学)

## 方案B: 分天学习计划(系统学习,每天一个模块)

### Day 1: JDBC 基础(理论+实践 3小时)

__上午学习__:

- JDBC 是什么: Java 访问数据库的标准接口
- JDBC 核心接口: DriverManager、Connection、Statement、ResultSet
- 连接字符串格式: `jdbc:mysql://host:port/database?参数`

__下午实践__:

1. 在 pom.xml 添加 MySQL 驱动依赖
2. 创建测试类,测试连接远程 MySQL
3. 练习: 连接成功后,执行 `SELECT 1` 测试查询

__关键点__:

- 理解 try-with-resources 自动关闭资源
- 记住连接字符串参数: `useSSL=false&serverTimezone=UTC`

---

### Day 2: SQL 增删改查(理论+实践 4小时)

__上午学习__:

- SQL 基础语法: INSERT、DELETE、UPDATE、SELECT
- WHERE 条件: =、LIKE、IN、BETWEEN
- 排序和分页: ORDER BY、LIMIT

__下午实践__:

1. 创建通讯录三张表(contacts、groups、contact_tags)
2. 用 SQL 语句手动插入测试数据
3. 练习各种查询: 按分组查、模糊搜索、分页

__关键点__:

- 掌握 LIKE 模糊查询: `WHERE name LIKE '%张%'`
- 理解外键关联: `WHERE group_id = ?`

---

### Day 3: PreparedStatement 与 DAO 封装(实践 4小时)

__上午学习__:

- PreparedStatement vs Statement: 防 SQL 注入
- 参数占位符: `?` 的使用
- ResultSet 遍历结果集

__下午实践__:

1. 创建实体类: Contact、Group
2. 创建 DAO 类: ContactDAO、GroupDAO
3. 实现方法: add()、delete()、update()、findAll()、findByName()

__关键点__:

- DAO 模式: 数据访问对象,封装数据库操作
- 异常处理: try-catch 捕获 SQLException

---


### Day 5: 功能整合(实践 5小时)

__全天实践__:

1. 实现添加联系人: 弹出对话框输入信息
2. 实现删除联系人: 选中行后删除
3. 实现修改联系人: 双击行弹出编辑对话框
4. 实现搜索功能: 输入框实时搜索

__关键点__:

- 界面与数据库交互: 按钮点击 → 调用 DAO → 刷新界面
- 数据刷新: 操作后重新查询并更新 JTable

---

### Day 6: 分组与标签(实践 4小时)

__上午实践__:

1. 实现分组管理: 添加、删除、重命名分组
2. 实现按分组筛选: 点击分组显示对应联系人
3. 联系人分配分组: 下拉框选择分组

__下午实践__:

1. 实现标签功能: 为联系人添加多个标签
2. 按标签筛选: 显示包含指定标签的联系人

__关键点__:

- 多对多关系: contact_tags 表存储联系人与标签关系
- 联表查询: `JOIN` 语句关联多张表

---

### Day 7: 数据导入导出(实践 4小时)

__上午学习__:

- vCard 格式: 通讯录标准格式(.vcf 文件)
- vCard 基本结构:

```
```
```java
public static void showDatabase() {
    showTableInfo();
    showContacts();
}

private static void showTableInfo() {
    String pragmaSql = "PRAGMA table_info(contacts);";
    try (Connection conn = DBUtil.getConnection();
         Statement stmt = conn.createStatement();
         var rs = stmt.executeQuery(pragmaSql)) {
        System.out.println("表结构：");
        while (rs.next()) {
            System.out.printf("列:%s 类型:%s 非空:%d 默认:%s%n",
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getInt("notnull"),
                    rs.getString("dflt_value"));
        }
    } catch (SQLException e) {
        System.out.println("读取表结构出错:" + e.getMessage());
    }
}

private static void showContacts() {
    String querySql = "SELECT * FROM contacts;";
    try (Connection conn = DBUtil.getConnection();
         Statement stmt = conn.createStatement();
         var rs = stmt.executeQuery(querySql)) {
        System.out.println("表数据：");
        while (rs.next()) {
            System.out.printf("id=%d, name=%s, tele1=%s, tele2=%s, home=%s, email=%s, notes=%s, update_time=%s%n",
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("tele1"),
                    rs.getString("tele2"),
                    rs.getString("home"),
                    rs.getString("email"),
                    rs.getString("notes"),
                    rs.getString("update_time"));
        }
    } catch (SQLException e) {
        System.out.println("读取表数据出错:" + e.getMessage());
    }
}

```