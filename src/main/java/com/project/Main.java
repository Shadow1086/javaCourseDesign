package com.project;

/**
 * ClassName: Main
 * Description: 
 * 
 * 调用JDBC连接数据库
 * 
 * {@code @Author} Liang-ht
 * {@code @Create} 2025-12-09 20:17:05
 */ 
public class Main {
    public static void main(String[] args) {
        InitDatabase.initDatabase();
        InitDatabase.showTableInfo();
        InitDatabase.showTableDetail();
    }
}
