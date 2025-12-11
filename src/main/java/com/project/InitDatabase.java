package com.project;

/**
 * ClassName: InitDatabase
 * Description: 
 * 
 * 初始化数据库
 * 
 * {@code @Author} Liang-ht
 * {@code @Create} 2025-12-09 20:08:30
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InitDatabase {
    public static void initDatabase() {
        //创建联系人表
        try (Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement()) {
            // 创建联系人表
            String createContact = """
                    CREATE TABLE IF NOT EXISTS contacts(
                        id integer primary key autoincrement,
                        name varchar(10) NOT NULL,
                        tele1 varchar(11) NOT NULL ,
                        tele2 VARCHAR(11),
                        home VARCHAR(30),
                        email VARCHAR(30) ,
                        notes varchar(100) ,
                        update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                    """;
            stmt.executeUpdate(createContact);
            System.out.println("联系人表创建完成");
        } catch (SQLException e) {
            System.out.println("初始化联系人表失败"+e.getMessage());
        }
    }
    /**
    * 查看表的结构
    */
    public static void showTableInfo(){
        String pragmaSql = "PRAGMA table_info(contacts)";
        try(Connection conn = DBUtil.getConnection();
            Statement stmt = conn.createStatement();
            var rs = stmt.executeQuery(pragmaSql)){
            System.out.println("表结构：");
            while(rs.next()){
                System.out.printf("列：%s 类型：%s 非空：%s 默认：%s%n",
                    rs.getString("name"),
                rs.getString("type"),
                rs.getInt("notnull"),
                rs.getString("dflt_value"));
            }
        } catch (SQLException e) {
            System.out.println("出错："+ e.getMessage());
        }
    }
    
    /*
    * 读取表中数据
    */
    public static void showTableDetail(){
        String querySql = "select * from contacts;";
        try(Connection conn = DBUtil.getConnection();
            Statement stmt = conn.createStatement();
            var rs = stmt.executeQuery(querySql)){
            System.out.println("表数据：");
            while(rs.next()){
                System.out.printf("id=%d, name = %s, tele1 = %s, tele2 = %s, home = %s,email = %s,notes = %s,update_time = %s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("tele1"),
                        rs.getString("tele2"),
                        rs.getString("home"),
                        rs.getString("email"),
                        rs.getString("notes"),
                        rs.getString("update_time"));
            }
        }catch(SQLException e){
            System.out.println("错误："+e.getMessage());
        }
    }

    public static void insertContact(){

    }
}
