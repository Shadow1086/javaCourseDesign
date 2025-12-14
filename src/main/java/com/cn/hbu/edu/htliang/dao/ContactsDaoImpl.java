package com.cn.hbu.edu.htliang.dao;

import com.cn.hbu.edu.htliang.entityPojo.Contacts;
import com.cn.hbu.edu.htliang.util.DBUtil;
import java.sql.Connection;
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
public class ContactsDaoImpl implements ContactsDao {
    /**
     * 插入单个联系人方法
     */
    @Override
    public void insert(Contacts con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        insert into Contacts (name, tele1, tele2, home, email, notes) VALUES (?,?,?,?,?,?);
                    """);
            ps.setString(1, con.getName());
            ps.setString(2, con.getTele1());
            ps.setString(3, con.getTele2());
            ps.setString(4, con.getHome());
            ps.setString(5, con.getEmail());
            ps.setString(6, con.getNotes());
            ps.execute();
            rs = ps.getGeneratedKeys();
            if(rs.next()){
                int generatedId = rs.getInt(1);
                con.setId(generatedId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ContactsDaoImpl类下的insert方法出现问题");
        }finally{
            try{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
                if(conn!=null){
                    conn.close();
                }
            }catch (SQLException e){
                e.printStackTrace();
                System.err.println("添加联系人时关闭资源失败");
            }
        }
    }

    /**
     * 插入多个联系人方法,可以应用在从文件导入中
     */
    @Override
    public void batchInsert(List<Contacts> contacts) {
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("""
                        insert into Contacts (name, tele1, tele2, home, email, notes) values (?,?,?,?,?,?);
                    """);
            for (int i = 0; i < contacts.size(); i++) {
                Contacts con = contacts.get(i);
                ps.setString(1, con.getName());
                ps.setString(2, con.getTele1());
                ps.setString(3, con.getTele2());
                ps.setString(4, con.getHome());
                ps.setString(5, con.getEmail());
                ps.setString(6, con.getNotes());
                ps.execute();
            }
            ps.close();
            DBUtil.getConnection().close();
            System.err.println("batchInsert方法成功");
        } catch (SQLException e) {
            System.err.println("ContactsDaoImpl 类下的 batchInsert方法出错");
        }
    }

    /**
     * 查询：通过ID查询
     */
    @Override
    public Contacts findById(int id) {
        Contacts con = null;
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("""
                        select * from Contacts where id = ?;
                    """);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                con = new Contacts(rs.getInt("id"), rs.getString("name"), rs.getString("tele1"), rs.getString("tele2"), rs.getString("home"), rs.getString("email"), rs.getString("notes"));
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

    /**
     * 查询：查询所有数据
     */
    public List<Contacts> findAll() {
        List<Contacts> list = new ArrayList<Contacts>();
        try {
            PreparedStatement ps = DBUtil.getConnection().prepareStatement("""
                        select * from Contacts;
                    """);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Contacts con = new Contacts(rs.getInt("id"), rs.getString("name"), rs.getString("tele1"), rs.getString("tele2"), rs.getString("home"), rs.getString("email"), rs.getString("notes"));
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


    /**
     * 根据姓名查询,需要在serveice中去调用这个方法，使用返回的列表展示在GUI上
     *
     * @return List<Contacts> 以姓名查询的联系人列表
     */
    public List<Contacts> findByName(String name) {
        List<Contacts> list = new ArrayList<Contacts>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        select * from Contacts where name = ?;
                    """);
            ps.setString(1, name);
            rs = ps.executeQuery();
            while (rs.next()) {
                Contacts con = new Contacts(rs.getInt("id"), rs.getString("name"), rs.getString("tele1"), rs.getString("tele2"), rs.getString("home"), rs.getString("email"), rs.getString("notes"));
                list.add(con);
            }
            if(list == null){
                System.out.println("无姓名为："+name+"的联系人");
                return null;
            }
            rs.close();
            ps.close();
            DBUtil.getConnection().close();
        } catch (SQLException e) {
            System.err.println("findByName方法有误");
        }finally {
            try{
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(conn!=null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 根据电话号码查询
     */

    @Override
    public List<Contacts> findByTele(String tele) {
        List<Contacts> list = new ArrayList<Contacts>();
        Connection conn = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        select * from Contacts where tele1 = ? OR tele2 = ?;
                    """);
            ps.setString(1, tele);
            ps.setString(2, tele);
            rs = ps.executeQuery();
            while (rs.next()) {
                Contacts con = new Contacts(rs.getInt("id"), rs.getString("name"), rs.getString("tele1"), rs.getString("tele2"), rs.getString("home"), rs.getString("email"), rs.getString("notes"));
                list.add(con);
            }
        } catch (SQLException e) {
            System.err.println("findAll方法有误");
        }finally {
            try{
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(conn!=null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 更新数据
     */
    @Override
    public void update(Contacts con) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        update Contacts set name = ?,tele1 = ? ,tele2 = ?,home = ? ,email = ?,notes = ?;
                    """);
            ps.setString(1, con.getName());
            ps.setString(2, con.getTele1());
            ps.setString(3, con.getTele2());
            ps.setString(4, con.getHome());
            ps.setString(5, con.getEmail());
            ps.setString(6, con.getNotes());
            rs = ps.executeQuery();
            while (rs.next()) {
                Contacts contacts = new Contacts(rs.getInt("id"), rs.getString("name"), rs.getString("tele1"), rs.getString("tele2"), rs.getString("home"), rs.getString("email"), rs.getString("notes"));

            }
            rs.close();
            ps.close();
            DBUtil.getConnection().close();
        } catch (SQLException e) {
            System.err.println("findAll方法有误");
        }finally {
            try{
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(conn!=null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
    *根据电话号更新数据
    */

    @Override
    public void updateInfo(int id,String newName,String newTele1,String newTele2,String newHome,String newEmail,String newNotes){
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                update Contacts set name=?,tele1=?,tele2=?,home=?,email=?,notes=?;
            """);
            ps.setString(1,newName);
            ps.setString(2,newTele1);
            ps.setString(3,newTele2);
            ps.setString(4,newHome);
            ps.setString(5,newEmail);
            ps.setString(6,newNotes);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
    * 根据ID删除联系人
    */

    @Override
    public void deleteById(int id){
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = DBUtil.getConnection();
            ps= conn.prepareStatement("""
                delete from Contacts where id = ?;
            """);
            ps.setInt(1,id);
            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
            System.err.println("根据ID删除联系人失败");
        }finally {
            try{
                if(ps!=null){
                    ps.close();
                }
                if(conn!=null){
                    conn.close();
                }
            }catch (SQLException e){
                e.printStackTrace();
                System.err.println("根据ID删除联系人中资源关闭失败");
            }
        }
    }

    /**
    * 判断联系人是否存在
    */

    @Override
    public boolean exists(int id){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs= null;
        try{
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                select * from Contacts where id = ?;
            """);
            ps.setInt(1,id);
            rs = ps.executeQuery();
            if(rs.next())    return true;
            else    return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
                if(conn!=null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
