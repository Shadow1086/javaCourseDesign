package com.pojo;

import com.entityPojo.Contacts;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名: ContactsList
 * 创建时间: 2025/12/12 18:48
 * 项目描述:
 * <p>
 * 使用JDBC思想完成ORM框架
 *
 * @author htLiang
 */
public class ContactsList {
    public final List<Contacts> contactsList = new ArrayList<Contacts>();

    /*
     * 初始化数据库中的表
     */
    public void InitDatabase() {
        try {
            PreparedStatement ps = getConnection().prepareStatement("""
                        create table if not exists Contacts(
                            id int primary Key autoincrement,       -- 联系人ID，自动生成，从1开始递增
                            name text not null ,                    -- 姓名，必填
                            tele1 varchar(11) not NULL,             -- 电话，必填
                            tele2 varchar(11),                      -- 备用电话
                            home text,                              -- 家庭住址
                            email varchar(20),                      -- 电子邮件
                            notes text                              -- 备注
                        );
                    """);
            if (!ps.execute()) {
                System.out.println("初始化联系人表成功");
            } else {
                System.out.println("初始化联系人表失败");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * 将数据库中一行数据转化为一个对象,加载到不定列表contactsList中
     */
    public void tranformDatabaseClass() {
        try {
            PreparedStatement ps = getConnection().prepareStatement("select * from Contacts");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String tele1 = rs.getString("tele1");
                String tele2 = rs.getString("tele2");
                String home = rs.getString("home");
                String email = rs.getString("email");
                String notes = rs.getString("notes");
                contactsList.add(new Contacts(name, tele1, tele2, home, email, notes));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * 向数据库中添加行数据
     * @param Contacts con : 要传入需要写入数据库的联系人对象
     */
    public void updateTable(Contacts con) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("""
                        update Contacts set name = ?,tele1 = ?,tele2 = ?,home = ?,email = ?,notes = ?;
                    """);
            ps.setString(1, con.getName());
            ps.setString(2, con.getTele1());
            ps.setString(3, con.getTele2());
            ps.setString(4, con.getHome());
            ps.setString(5, con.getEmail());
            ps.setString(6, con.getNotes());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * 打印表中全部的数据
     */
    public void PrintTable() {
        try {
            PreparedStatement ps = getConnection().prepareStatement("""
                        select * from Contacts;
                    """);
            System.out.println("id" + "\t" + "name" + "\t" + "tele1" + "\t" + "tele2" + "\t" + "home" + "\t" + "email" + "\t" + "notes");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String tele1 = rs.getString("tele1");
                String tele2 = rs.getString("tele2");
                String home = rs.getString("home");
                String email = rs.getString("email");
                String notes = rs.getString("notes");
                System.out.println(id + "\t" + name + "\t" + tele1 + "\t" + tele2 + "\t" + home + "\t" + email + "\t" + notes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * 打印关键信息
     * 在GUI界面中显示
     */
    public void printInfo() {
        try {
            PreparedStatement ps = getConnection().prepareStatement("""
                        select * from Contacts;
                    """);
            System.out.println("name" + "\t" + "tele1" + "\t" + "notes");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String tele1 = rs.getString("tele1");
                String notes = rs.getString("notes");
                System.out.println(name + "\t" + tele1 + "\t" + notes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * 获取连接的方法
     * @return 数据库的连接
     */
    public Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/Volumes/study/02-java/javaCurriculumDesign/contacts.sqlite");
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
