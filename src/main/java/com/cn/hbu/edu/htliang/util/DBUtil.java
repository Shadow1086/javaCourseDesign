package com.cn.hbu.edu.htliang.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 类名: DBUtil
 * 创建时间: 2025/12/12 20:32
 * 项目描述:
 *  获取数据库连接
 * @author htLiang
 */
public class DBUtil {
    private static final String URL = "jdbc:sqlite:/Volumes/study/02-java/javaCurriculumDesign/contacts.sqlite";
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL);
    }
}
