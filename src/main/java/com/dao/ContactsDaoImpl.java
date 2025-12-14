package com.dao;

import com.Util.DBUtil;
import com.entityPojo.Contacts;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名: ContactsDaoImpl
 * 创建时间: 2025/12/12 20:41
 * 项目描述:
 *
 * @author htLiang
 */
public class ContactsDaoImpl implements ContactsDao{
    /*
    * 插入单个联系人方法
    */
    @Override
    public void insert(Contacts con){
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("""
                insert into Contacts (name, tele1, tele2, home, email, notes) VALUES (?,?,?,?,?,?);
            """);
            ps.setString(1, con.getName());
            ps.setString(2,con.getTele1());
            ps.setString(3,con.getTele2());
            ps.setString(4,con.getHome());
            ps.setString(5,con.getEmail());
            ps.setString(6,con.getNotes());
            ps.execute();
            ps.close();
            DBUtil.getConnection().close();
        } catch (SQLException e) {
            System.err.println("ContactsDaoImpl类下的insert方法出现问题");
        }
    }

    /*
    * 插入多个联系人方法
    */
    @Override
    public void batchInsert(List<Contacts> contacts){
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("""
                insert into Contacts (name, tele1, tele2, home, email, notes) values (?,?,?,?,?,?);
            """);
            for(int i = 0;i<contacts.size();i++){
                Contacts con = contacts.get(i);
                ps.setString(1, con.getName());
                ps.setString(2,con.getTele1());
                ps.setString(3,con.getTele2());
                ps.setString(4,con.getHome());
                ps.setString(5,con.getEmail());
                ps.setString(6,con.getNotes());
                ps.execute();
            }
            ps.close();
            DBUtil.getConnection().close();
            System.err.println("batchInsert方法成功");
        } catch (SQLException e) {
            System.err.println("ContactsDaoImpl 类下的 batchInsert方法出错");
        }
    }

    /*
    * 查询：通过ID查询
    */
    @Override
    public Contacts findById(int id){
        Contacts con = null;
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("""
                select * from Contacts where id = ?;
            """);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                con = new Contacts(rs.getInt("id"),rs.getString("name"),rs.getString("tele1"),rs.getString("tele2"),rs.getString("home"),rs.getString("email"),rs.getString("notes"));
                break;
            }
            rs.close();
            ps.close();
            DBUtil.getConnection().close();
        } catch (SQLException e) {
            System.err.println("contactsDaoImpl类下的findById出错");
        }
        return con;
    }

    /*
    * 查询：查询所有数据
    */
    public List<Contacts> findAll(){
        List<Contacts> list = new ArrayList<Contacts>();
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("""
                select * from Contacts;
            """);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Contacts con = new Contacts(rs.getInt("id"),rs.getString("name"),rs.getString("tele1"),rs.getString("tele2"),rs.getString("home"),rs.getString("email"),rs.getString("notes"));
                list.add(con);
            }
            rs.close();
            ps.close();
            DBUtil.getConnection().close();
        } catch (SQLException e) {
            System.err.println("findAll方法有误");
        }
        return list;
    }


    /*
    * 根据姓名查询
    */
    public List<Contacts> findByName(String name){
        List<Contacts> list = new ArrayList<Contacts>();
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("""
                select * from Contacts where name = ?;
            """);
            ps.setString(1,name);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Contacts con = new Contacts(rs.getInt("id"),rs.getString("name"),rs.getString("tele1"),rs.getString("tele2"),rs.getString("home"),rs.getString("email"),rs.getString("notes"));
                list.add(con);
            }
            rs.close();
            ps.close();
            DBUtil.getConnection().close();
        } catch (SQLException e) {
            System.err.println("findByName方法有误");
        }
        return list;
    }

    /*
    * 根据电话号码查询
    */

    @Override
    public List<Contacts> findByTele(String tele) {
        List<Contacts> list = new ArrayList<Contacts>();
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("""
                select * from Contacts where tele1 = ? OR tele2 = ?;
            """);
            ps.setString(1,tele);
            ps.setString(2,tele);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Contacts con = new Contacts(rs.getInt("id"),rs.getString("name"),rs.getString("tele1"),rs.getString("tele2"),rs.getString("home"),rs.getString("email"),rs.getString("notes"));
                list.add(con);
            }
            rs.close();
            ps.close();
            DBUtil.getConnection().close();
        } catch (SQLException e) {
            System.err.println("findAll方法有误");
        }
        return list;
    }

    /*
    * 更新数据
    */
    public void Update(Contacts con){
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("""
                update Contacts set name = ?,tele1 = ? ,tele2 = ?,home = ? ,email = ?,notes = ?;
            """);
            ps.setString(1, con.getName());
            ps.setString(2,con.getTele1());
            ps.setString(3,con.getTele2());
            ps.setString(4,con.getHome());
            ps.setString(5,con.getEmail());
            ps.setString(6,con.getNotes());
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Contacts con = new Contacts(rs.getInt("id"),rs.getString("name"),rs.getString("tele1"),rs.getString("tele2"),rs.getString("home"),rs.getString("email"),rs.getString("notes"));
                list.add(con);
            }
            rs.close();
            ps.close();
            DBUtil.getConnection().close();
        } catch (SQLException e) {
            System.err.println("findAll方法有误");
        }
    }




}
