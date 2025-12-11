package com.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ClassName: Main
 * Description:
 * 
 * 使用JDBC来连接数据库,工具类
 * 
 * {@code @Author} Liang-ht
 * {@code @Create} 2025-12-09 19:56:11
 */
public class DBUtil {
    private static final String DB_PATH = "src/main/java/com/project/contacts.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    /**
     * 获取数据库连接
     * 
     * @return Connection 对象
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC驱动器未找到");
        }
    }

}
