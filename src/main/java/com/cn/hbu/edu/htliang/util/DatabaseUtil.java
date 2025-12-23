package com.cn.hbu.edu.htliang.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 类名: HikariCP
 * 创建时间: 2025/12/22 17:00
 * 项目描述:
 *
 * @author htLiang
 */
public class DatabaseUtil {
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        // Sqlite配置
        config.setJdbcUrl("jdbc:sqlite:contacts.sqlite");
        // MySQL配置
        // config.setJdbcUrl("");
        // config.setUsername("");
        // config.setPassword("");
        // config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // 连接池配置
        config.setMaximumPoolSize(3); // 最大连接数
        config.setMinimumIdle(1); // 最小空闲连接
        config.setIdleTimeout(300000); // 空闲超时 5分钟
        config.setConnectionTimeout(30000); // 连接超时 30秒
        config.setMaxLifetime(1800000); // 连接最大存活时间：30分钟
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
