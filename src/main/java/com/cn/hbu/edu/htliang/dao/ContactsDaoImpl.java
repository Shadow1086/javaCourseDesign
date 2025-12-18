package com.cn.hbu.edu.htliang.dao;

import com.cn.hbu.edu.htliang.entityPojo.Contacts;
import com.cn.hbu.edu.htliang.entityPojo.Groups;
import com.cn.hbu.edu.htliang.entityPojo.Tags;
import com.cn.hbu.edu.htliang.util.DBUtil;

import java.io.BufferedWriter;
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
                        insert into contacts (name, tele1, tele2, home, email, notes) VALUES (?,?,?,?,?,?);
                    """);
            ps.setString(1, con.getName());
            ps.setString(2, con.getTele1());
            ps.setString(3, con.getTele2());
            ps.setString(4, con.getHome());
            ps.setString(5, con.getEmail());
            ps.setString(6, con.getNotes());
            ps.execute();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                con.setId(generatedId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ContactsDaoImpl类下的insert方法出现问题");
        } finally {
            try{
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 插入多个联系人方法,可以应用在从文件导入中
     */
    @Override
    public void batchInsert(List<Contacts> contacts) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        insert into contacts (name, tele1, tele2, home, email, notes) values (?,?,?,?,?,?);
                    """);
            for (Contacts con : contacts) {
                ps.setString(1, con.getName());
                ps.setString(2, con.getTele1());
                ps.setString(3, con.getTele2());
                ps.setString(4, con.getHome());
                ps.setString(5, con.getEmail());
                ps.setString(6, con.getNotes());
                ps.addBatch();
            }
            ps.executeBatch();//使用批处理，一次性执行所有
            System.err.println("batchInsert方法成功");
        } catch (SQLException e) {
            System.err.println("ContactsDaoImpl 类下的 batchInsert方法出错");
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询：通过ID查询
     */
    @Override
    public Contacts findById(int id) {
        Contacts con = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        select * from contacts where id = ?;
                    """);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                con = getContactsFromRsNext(rs);
                break;
            }
        } catch (SQLException e) {
            System.err.println("contactsDaoImpl类下的findById出错");
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return con;
    }

    /**
     * 查询：查询所有数据
     */
    public List<Contacts> findAll() {
        List<Contacts> list = new ArrayList<Contacts>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        select * from contacts;
                    """);
            rs = ps.executeQuery();
            while (rs.next()) {
                Contacts con = getContactsFromRsNext(rs);
                list.add(con);
            }
        } catch (SQLException e) {
            System.err.println("findAll方法有误");
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
                        select * from contacts where name like ?;
                    """);
            ps.setString(1, "%" + name + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                Contacts con = getContactsFromRsNext(rs);
                list.add(con);
            }
        } catch (SQLException e) {
            System.err.println("findByName方法有误");
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
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
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        select * from contacts where tele1 = ? OR tele2 = ?;
                    """);
            ps.setString(1, tele);
            ps.setString(2, tele);
            rs = ps.executeQuery();
            while (rs.next()) {
                Contacts con = getContactsFromRsNext(rs);
                list.add(con);
            }
        } catch (SQLException e) {
            System.err.println("findByTele方法有误");
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
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
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        update contacts set name = ?,tele1 = ? ,tele2 = ?,home = ? ,email = ?,notes = ?
                        where id = ?;
                    """);
            ps.setString(1, con.getName());
            ps.setString(2, con.getTele1());
            ps.setString(3, con.getTele2());
            ps.setString(4, con.getHome());
            ps.setString(5, con.getEmail());
            ps.setString(6, con.getNotes());
            ps.setInt(7, con.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("update方法有误");
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据电话号更新数据
     */

    @Override
    public void updateInfo(int id, String newName, String newTele1, String newTele2, String newHome, String newEmail, String newNotes) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        update contacts set name=?,tele1=?,tele2=?,home=?,email=?,notes=?
                        where id  = ?;
                    """);
            ps.setString(1, newName);
            ps.setString(2, newTele1);
            ps.setString(3, newTele2);
            ps.setString(4, newHome);
            ps.setString(5, newEmail);
            ps.setString(6, newNotes);
            ps.setInt(7, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据ID删除联系人
     */

    @Override
    public void deleteById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        delete from contacts where id = ?;
                    """);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("根据ID删除联系人失败");
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断联系人是否存在
     */

    @Override
    public boolean exists(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        select * from contacts where id = ?;
                    """);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 新建分组,并向表中添加联系人,这里只添加一个，会另写方法单独添加联系人
     */
    @Override
    public boolean addGroup(String name, String notes) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        -- 先创建一个分组行
                        insert into groups (group_name, group_notes) VALUES (?,?); 
                    """);
            ps.setString(1, name);
            ps.setString(2, notes);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 添加联系人
     */
    @Override
    public boolean addContactInGroup(Contacts con, Groups group) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        insert into contacts_group(contacts_id,group_id) values (?,?);
                    """);
            ps.setInt(1, con.getId());
            ps.setInt(2, group.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 删除选择的分组
     */
    @Override
    public boolean deleteGroup(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        delete from groups where group_name = ?;
                    """);
            ps.setString(1, name);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 查看分组中的全部联系人
     */
    @Override
    public List<Contacts> findByGroup(String name) {
        List<Contacts> list = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        select c.id,c.name,c.tele1,c.tele2,g.group_name,c.notes,c.home,c.email
                            from contacts c join contacts_group cg 
                        on c.id = cg.contacts_id
                        join groups g 
                        on g.id = cg.group_id
                        where g.group_name = ?;
                    """);
            ps.setString(1, name);
            rs = ps.executeQuery();
            list = new ArrayList<>();
            while (rs.next()) {
                Contacts con = getContactsFromRsNext(rs);
                list.add(con);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 显示所有分组
     */
    public List<Groups> findAllGroup() {
        List<Groups> list = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                    select  * from groups;   
                    """);
            rs = ps.executeQuery();
            list = new ArrayList<>();
            while (rs.next()) {
                Groups group = new Groups(rs.getInt("id"), rs.getString("group_name"), rs.getString("group_notes"));
                list.add(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

//标签操作

    /**
     * 新建标签
     */
    @Override
    public boolean addTag(String color, String name, String notes) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        insert into tags(tag_color, tag_name, tag_notes) VALUES (?,?,?);
                    """);
            ps.setString(1, color);
            ps.setString(2, name);
            ps.setString(3, notes);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 根据标签颜色删除标签
     */
    @Override
    public boolean deleteTag(String color) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        delete from tags where tag_color = ?;
                    """);
            ps.setString(1, color);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 向标签中添加联系人
     */
    @Override
    public boolean addContactToTag(Contacts con, Tags tag) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        insert into tag_contacts(contacts_id, tag_id) values (?,?);
                    """);
            ps.setInt(1, con.getId());
            ps.setInt(2, tag.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 查找标签中所有联系人
     */
    public List<Contacts> findByTag(String color) {
        List<Contacts> list = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        select c.id,c.name,c.tele1,c.tele2,t.tag_color,c.notes,c.home,c.email
                            from contacts c join tag_contacts tg
                        on c.id = tg.contacts_id
                        join tags t
                        on t.id = tg.tag_id
                        where t.tag_color = ?;
                    """);
            ps.setString(1, color);
            rs = ps.executeQuery();
            list = new ArrayList<Contacts>();
            while (rs.next()) {
                list.add(getContactsFromRsNext(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 显示所有标签
     */
    @Override
    public List<Tags> findAllTags() {
        List<Tags> list = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        select * from tags;
                    """);
            rs = ps.executeQuery();
            list = new ArrayList<>();
            while (rs.next()) {
                Tags tags = new Tags(rs.getInt("id"), rs.getString("tag_name"), rs.getString("tag_color"), rs.getString("tag_notes"));
                list.add(tags);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 根据联系人来查找分组和标签列表
     */
    @Override
    public List<Groups> findGroupsByContact(int id) {
        List<Groups> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                    select g.group_name,g.id,g.group_notes
                        from groups g join contacts_group cg
                    on g.id = cg.group_id
                    join contacts c 
                    on c.id = cg.contacts_id
                    where c.id  = ?;
                    """);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                Groups groups = new Groups(rs.getInt("id"), rs.getString("group_name"), rs.getString("group_notes"));
                list.add(groups);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 根据联系人ID查找标签组
     */
    public List<Tags> findTagsByContact(int id) {
        List<Tags> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                    select t.id,t.tag_name,tag_color,t.tag_notes
                        from tags t join tag_contacts tc
                    on t.id = tc.tag_id
                    join contacts c 
                    on c.id = tc.contacts_id
                    where c.id  = ?;
                    """);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                Tags tags = new Tags(rs.getInt("id"), rs.getString("tag_name"), rs.getString("tag_color"), rs.getString("tag_notes"));
                list.add(tags);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 从rs中获取Contact对象，省略重复代码
     *
     */
    private Contacts getContactsFromRsNext(ResultSet rs) {
        Contacts con = null;
        try {
            con = new Contacts(rs.getInt("id"), rs.getString("name"), rs.getString("tele1"), rs.getString("tele2"), rs.getString("home"), rs.getString("email"), rs.getString("notes"));
            return con;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    /**
     * 将数据库中的联系人导出到文件中
     *
     * @param bw : 从FileVcf中被调用
     */
    @Override
    public List<String> writeVcfFileInService(BufferedWriter bw) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> resultList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("""
                        select c.name,c.tele1,c.tele2,c.home,c.email,c.notes,g.group_name,t.tag_name
                            from contacts c 
                                join contacts_group cg  on c.id = cg.contacts_id 
                                join groups g           on g.id = cg.group_id
                                join tag_contacts tc    on c.id = tc.contacts_id
                                join tags t             on t.id = tc.tag_id;
                    """);
            rs = ps.executeQuery();

            while (rs.next()) {
                String line = rs.getString(1) + "," +
                        rs.getString(2) + "," +
                        rs.getString(3) + "," +
                        rs.getString(4) + "," +
                        rs.getString(5) + "," +
                        rs.getString(6) + "," +
                        rs.getString(7) + "," +
                        rs.getString(8);
                resultList.add(line);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }
}
